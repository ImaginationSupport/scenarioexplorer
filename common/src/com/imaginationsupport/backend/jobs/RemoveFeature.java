package com.imaginationsupport.backend.jobs;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;

import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.data.tree.CNode;
import com.imaginationsupport.data.tree.SNode;
import com.imaginationsupport.data.tree.TreeNode;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.views.View;
import com.imaginationsupport.plugins.Precondition;
import com.imaginationsupport.plugins.preconditions.FeaturePrecondition;
import com.imaginationsupport.plugins.preconditions.OnHold;

public class RemoveFeature extends Job {
	
	private ProjectManager pm=ProjectManager.getInstance();
	private ObjectId projectId=null;
	private String featureMapId=null;

	public RemoveFeature(ObjectId projectId, String featureMapId) {
		this.projectId=projectId;
		this.featureMapId=featureMapId;
	}
	
	public RemoveFeature(Project project, FeatureMap featureMap) {
		this.project=project;
		this.projectId=project.getId();
		this.fm=featureMap;
		this.featureMapId=featureMap.getUid();
	}
	
	private Project project=null;
	private FeatureMap fm=null;
	
	@Override
	public void execute() {
		if (project==null || fm==null) {
			project=Database.get(Project.class, projectId);
			try {
				fm=project.getFeatureMap(featureMapId);
				project.removeFeatureMap(fm);
				project.save();
			} catch ( InvalidDataException e) {
				LOGGER.error("Error getting Feature Map ("+featureMapId+") for Project ("+projectId.toHexString()+"): "+e);
			} 
	
			// Removing a feature could break a conditioning event, check for this and put CE on hold, and remove from trees
			try {
				for (ConditioningEvent ce: pm.getConditioningEventsForProjectId(projectId)) {
					for(Precondition p:ce.getPreconditions()) {
						if(p instanceof FeaturePrecondition) {
							FeaturePrecondition fp=(FeaturePrecondition)p;
							if(fp.getFeatureUid().equals(featureMapId)) {
								LOGGER.info("Detected removal of Conditioning Event ("+ce.getId()+") precondition that depends on removed Feature ("+featureMapId+").");
								ce.removePrecondition(fp);
								ce.addPrecondition(new OnHold("Previously set Feature ("+fp.getLabel()+") was removed."));
								ce.save();
								Job job=new RemoveConditioningEventTreesOnly(projectId, ce.getId());
								job.execute();
							}
						}
					}
				}
			} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
				LOGGER.error("Unable to get ConditioningEvents for Project ("+projectId+").");
				e.printStackTrace();
			}
			
			LOGGER.info("Updating States to remove Feature ("+featureMapId+").");
			MasterView mv=(MasterView) Database.get(View.class, project.getMasterView());
			//TODO: this could be improved with simple DB query for states in project, but this could be a lot of data...
			updateDFS(mv.getTree().getRoot(), fm);
		}
	}
	
	private void updateDFS(TreeNode currentNode, FeatureMap map){
		if (currentNode instanceof CNode) { // Skip over CNodes
			for(TreeNode following: currentNode.getChildren()) {
				updateDFS(following, map);
			}
			return;
		}
		
		if (currentNode instanceof SNode) {
			State current=Database.get(State.class, currentNode.getDBId());
			current.removeFeature(map);
			current.save();
			if (currentNode.getChildren()==null)
				return;
			for (TreeNode next: currentNode.getChildren()) {
				updateDFS(next, map);					
			}
		}
	}
	
}
