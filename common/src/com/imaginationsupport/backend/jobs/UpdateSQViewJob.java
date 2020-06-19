package com.imaginationsupport.backend.jobs;

import com.imaginationsupport.Database;
import com.imaginationsupport.StateManager;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.backend.JobStatus;
import com.imaginationsupport.data.Indicator;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.StateGroup;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.data.tree.CNode;
import com.imaginationsupport.data.tree.Trajectory;
import com.imaginationsupport.data.tree.TreeNode;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.views.SQView;
import com.imaginationsupport.views.View;
import org.apache.commons.math3.ml.clustering.*;
import org.bson.types.ObjectId;

import java.text.DecimalFormat;
import java.util.*;

public class UpdateSQViewJob extends Job {

	private ObjectId projectId = null;
	private ObjectId viewId = null;

	public UpdateSQViewJob(ObjectId projectId, ObjectId viewId) {
		this.projectId = projectId;
		this.viewId = viewId;
	}

	private Project project = null;
	private SQView view = null;
	private MasterView mv = null;
	private DecimalFormat df = new DecimalFormat("0.###");
	private static int CLUSTERING_NUM_TRIALS=10;
	private static int CLUSTERING_MIN_K=4;

	private class TrajectoryPoint {
		public ObjectId stateId = null;
		public StateGroup sg = null;
		public String value = "";
		public List<String> eventSeq = new ArrayList<>(5);
	}

	private class IndicatorWrapper {
		public String key="";
		public Indicator indicator=null;
		public List<TrajectoryPoint> points=new ArrayList<>();
	}

	private class ContinuousFeature implements Clusterable {
		public double value = 0.0;

		public ContinuousFeature(String value) {
			this.value = Double.parseDouble(value);
		}

		public double[] getPoint() {
			double[] out = new double[1];
			out[0] = value;
			return out;
		}
	}

	private List<TrajectoryPoint> points = null;
	private HashSet<String> uniqueValues = null;
	//private HashSet<String> uniqueCEOs = null;
	private Hashtable<String,IndicatorWrapper> uniqueIndicators=null;
	private List<StateGroup> sgList=null;

	@Override
	public void execute() {

		if (project == null || view == null || mv == null) {
			project = Database.get(Project.class, projectId);
			View temp = Database.get(View.class, viewId);
			if (temp instanceof SQView) {
				view = (SQView) temp;
			} else {
				LOGGER.error("Incorrect non-SQView (" + view.getId() + ") sent to UpdateSQViewJob.");
				setStatus(JobStatus.ERROR);
				return;
			}
			mv = (MasterView) Database.get(View.class, project.getMasterView());
		}

		List<Trajectory> mtree = mv.getTree().getTrajectories();

		FeatureMap feature = null;
		try {
			feature = view.getFeatureMap();
		} catch ( InvalidDataException e) {
			LOGGER.error("No FeatureMap set in SQView before UpdateSQViewJob runs.");
			setStatus(JobStatus.ERROR);
			return;
		}

		// pull out state/feature information
		points = loadTrajectories(mtree, feature);

		// separate into clusters
		sgList = new ArrayList<StateGroup>();

		if (feature.getType().isContinuousVariable()) {
			int min=CLUSTERING_MIN_K;
			if(uniqueValues.size()<CLUSTERING_MIN_K) min=uniqueValues.size(); // This avoids the issue of no CEs in project.
			Clusterer<ContinuousFeature> clusterer = new MultiKMeansPlusPlusClusterer(new KMeansPlusPlusClusterer(min), CLUSTERING_NUM_TRIALS);
			List<ContinuousFeature> values = new ArrayList<>(uniqueValues.size());
			for (String c : uniqueValues) {
				values.add(new ContinuousFeature(c));
			}
			List<? extends Cluster<ContinuousFeature>> clusters = clusterer.cluster(values);
			for (Cluster<ContinuousFeature> cluster : clusters) {
				String name = "";
				String description = "";
				if (cluster instanceof CentroidCluster) {
					name = "~" + df.format(((CentroidCluster) cluster).getCenter().getPoint()[0]);
					description = feature.getLabel() + " values centered around " + name;
				}
				StateGroup sg = new StateGroup(name, description);
				int count=0;
				double DEFAULT_DOUBLE = 0.123456789;
				double minValue=DEFAULT_DOUBLE;
				double maxValue=DEFAULT_DOUBLE;
				for (TrajectoryPoint p : points) {
					double pDouble = Double.parseDouble(p.value);
					for (ContinuousFeature cf : cluster.getPoints()) {
						if (pDouble == cf.value) {
							sg.addMember(p.stateId);
							if (minValue==DEFAULT_DOUBLE || minValue>pDouble) minValue=pDouble;
							if (maxValue==DEFAULT_DOUBLE || maxValue<pDouble) maxValue=pDouble;
							count++;
							p.sg = sg;
							break;
						}
					}
				}
				if(minValue!=maxValue){
					sg.setDescription("Cluster of "+count+" terminal states with "+feature.getLabel() + " values centered around " + name+" (min="+df.format(minValue)+" max="+df.format(maxValue)+").");
				} else{
					sg.setDescription("Cluster of "+count+" terminal states with "+feature.getLabel() + " value " + name+".");
				}
				sgList.add(sg);
			}
		} else {
			for (String cluster : uniqueValues) {
				String name = cluster;
				String description = "";
				StateGroup sg = new StateGroup(name, description);
				int count=0;
				for (TrajectoryPoint p : points) {
					if (p.value.equalsIgnoreCase(cluster)) {
						sg.addMember(p.stateId);
						count++;
						p.sg = sg;
					}
				}
				sg.setDescription("Cluster of "+count+" terminal states with "+feature.getLabel()+" set to \"" + cluster+"\".");
				sgList.add(sg);
			}
		}

		processIndicators();
		view.setStateGroupings(sgList);
		view.save();
	}

	private List<TrajectoryPoint> loadTrajectories(List<Trajectory> mtree, FeatureMap featureMap) {
		List<TrajectoryPoint> points = new ArrayList<>(mtree.size());
		boolean isContinuous = featureMap.getType().isContinuousVariable();
		uniqueValues = new HashSet<>();
		uniqueIndicators = new Hashtable<>();
		for (Trajectory t : mtree) {
			try {
				TrajectoryPoint p = new TrajectoryPoint();
				p.stateId = t.getLeaf();
				State s = StateManager.getInstance().getState(projectId, t.getLeaf());
				p.value = s.getFeature(featureMap.getUid()).getValue();
				//if(p.value.isEmpty()) continue; // skip states that have empty values
				uniqueValues.add(p.value);
				// TODO: ADD ALL COMBINATIONS HERE...
				extractIndicators(t,p);

//				for (TreeNode n : t.getNodes()) {
//					if (n instanceof CNode) {
//						String ceo = n.dehydrate();
//						IndicatorWrapper iw=null;
//						if(uniqueIndicators.containsKey(ceo)){
//							iw=uniqueIndicators.get(ceo);
//						} else {
//							iw=new IndicatorWrapper();
//							iw.key=ceo;
//							Indicator indicator=new Indicator();
//							indicator.addToPath((CNode)n);
//							iw.indicator=indicator;
//							uniqueIndicators.put(iw.key,iw);
//						}
//						iw.points.add(p);
//						//uniqueCEOs.add(ceo);
//						//p.eventSeq.add(ceo);
//					}
//				}
				points.add(p);
			} catch ( InvalidDataException e) {
				LOGGER.warn("Unable to process trajectory (" + t.getLeaf().toHexString() + ") from trajectory.");
			} catch (DatastoreException e) {
				LOGGER.warn("Unable to get value (" + featureMap.getUid() + ") for state (" + t.getLeaf().toHexString() + ") from trajectory.");
			}
		}
		return points;
	}

	private void extractIndicators(Trajectory t, TrajectoryPoint p) {
		List<CNode> ceos = new ArrayList<>(t.getNodes().size());
		for (TreeNode n : t.getNodes()) {
			if (n instanceof CNode) {
				ceos.add((CNode) n);
			}
		}
		divide(ceos,p);
	}

	private void divide(List<CNode> incoming, TrajectoryPoint p) {
		Indicator indicator=new Indicator();
		String key="";
		for (CNode n : incoming) {
			key += n.dehydrate()+">";
			indicator.addToPath(n);
		}
		IndicatorWrapper iw = null;
		if (uniqueIndicators.containsKey(key)) {
			iw = uniqueIndicators.get(key);
		} else {
			iw = new IndicatorWrapper();
			iw.key = key;
			iw.indicator = indicator;
			uniqueIndicators.put(iw.key, iw);
		}
		iw.points.add(p);
		if(incoming.size()==1) return;

		// recurse using each combination of sublist
		List<CNode> sublist=null;
		for (int i=0; i<incoming.size(); i++) {
			sublist = new ArrayList<>(incoming.size() - 1);
			for (int j = 0; j < incoming.size(); j++) {
				if (i != j) {
					sublist.add(incoming.get(i));
				}
			}
			divide(sublist,p);
		}
	}

	private void processIndicators() {
		for(StateGroup sg:sgList){
			for(String iwKey:uniqueIndicators.keySet()){
				Indicator indicator=new Indicator();
				IndicatorWrapper iw=uniqueIndicators.get(iwKey);
				indicator.setPath(iw.indicator.getPath());
				int tp=0, tn=0, fp=0, fn=0;
				for(TrajectoryPoint point:points){
					if(positiveIndication(iw,point)){ //is a positive
						if(point.sg==sg) tp++;
						else fp++;
					} else {
						if(point.sg==sg) fn++;
						else tn++;
					}
				}
				if (tp==0) continue;
				indicator.setSensitivity(((double)tp)/((double)tp+fn));
				indicator.setSpecificity(((double)tn)/((double)tn+fp));
				System.out.println("SG: "+sg.getName()+" ("+sg.getDescription()+") with indicator=\""+iw.key+"\": tp="+tp+" tn="+tn+" fn="+fn+" fp="+fp+" = "+indicator.getSensitivity()+" / "+indicator.getSpecificity()+".");
				sg.addIndicator(indicator);
			}
		}
	}

	private boolean positiveIndication(IndicatorWrapper iw, TrajectoryPoint p){
		for(TrajectoryPoint p2:iw.points){
			if(p==p2) return true;
		}
		return false;
	}


}
