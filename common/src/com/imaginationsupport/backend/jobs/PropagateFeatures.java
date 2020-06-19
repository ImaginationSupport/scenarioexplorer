package com.imaginationsupport.backend.jobs;

import java.util.ArrayList;
import java.util.List;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;

import com.imaginationsupport.Database;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.Feature;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.data.tree.CNode;
import com.imaginationsupport.data.tree.SNode;
import com.imaginationsupport.data.tree.TreeNode;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.views.View;
import com.imaginationsupport.plugins.Projector;

public class PropagateFeatures extends Job {
	
	private ObjectId projectId=null;
	private List<String> featureMapIds=new ArrayList<String>();

	public PropagateFeatures(ObjectId projectId, String featureMapId) {
		this.projectId=projectId;
		this.featureMapIds.add(featureMapId);
	}
	
	public PropagateFeatures(ObjectId projectId, List<String> featureMapIds)
	{
		this.featureMapIds=featureMapIds;
	}
	
	public PropagateFeatures(Project project, FeatureMap featureMap) {
		this.project=project;
		this.projectId=project.getId();
		this.featureMapIds.add(featureMap.getUid());
		this.featureMaps= new ArrayList<>();
		this.featureMaps.add(featureMap);
	}
	
	private Project project=null;
	private List<FeatureMap> featureMaps=null;

	
	@Override
	public void execute() throws GeneralScenarioExplorerException
	{
		if (project==null || featureMaps==null) {
			project=Database.get(Project.class, projectId);
			featureMaps= new ArrayList<>();
			for(String fid:featureMapIds) {
				try {
					featureMaps.add(project.getFeatureMap(fid));
				} catch ( InvalidDataException e) {
					LOGGER.error("Error getting Feature Map ("+fid+") for Project ("+projectId.toHexString()+"): "+e);
				}
			}
		}
		if (featureMaps.isEmpty()) return; // no features to propagate
		
		MasterView mv=(MasterView) Database.get(View.class, project.getMasterView());
		updateDFS(mv.getTree().getRoot(), featureMaps, null);
	}
	
	private void updateDFS(TreeNode currentNode, List<FeatureMap> fms, State previous) throws GeneralScenarioExplorerException
	{
		
		if (currentNode instanceof CNode) { // Skip over CNodes
			for(TreeNode following: ((CNode)currentNode).getChildren()) {
				updateDFS(following, fms, previous);
			}
			return;
		}
		
		if (currentNode instanceof SNode) {
			State current=Database.get(State.class, currentNode.getDBId());
			if (previous==null) {
				for(FeatureMap map: fms) {
					try {
						Feature f=new Feature(map);
						f.setValue(map.getType().getDefaultValue());
						current.updateFeature(f);
					} catch(DatastoreException | InvalidDataException e) {
						LOGGER.error("ERROR: Failed to project feature in new state ("+map.getUid()+":"+map.getLabel()+": "+e);
						e.printStackTrace();
					}
				}
			} else {
				for(FeatureMap map: fms) {
					try{
						// NOTE: this block is duplicated in State.populate()...
						Projector projector=map.getProjector();
						Feature f=new Feature(map);
						if (projector!=null){ // if we have a projector then project it
							f.setValue(projector.project(map, previous.getFeature(map.getUid()).getValue(),previous, current));
						} else { // if no projector we just copy the previous value
							f.setValue(previous.getFeature(map.getUid()).getValue());
						}
						current.updateFeature(f);
					} catch (DatastoreException | InvalidDataException e) {
						LOGGER.error("ERROR: Failed to project feature in new state ("+map.getUid()+":"+map.getLabel()+": "+e);
						e.printStackTrace();
					}
				}
			}
			current.save();
			if(currentNode.getChildren()!=null) {
				for (TreeNode next: currentNode.getChildren()) {
					updateDFS(next, fms, current);					
				}
			}
		}	
	}
}
