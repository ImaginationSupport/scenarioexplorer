package com.imaginationsupport.backend.jobs;

import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.tree.Tree;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.views.View;
import org.bson.types.ObjectId;

public class RemoveConditioningEventTreesOnly extends RemoveConditioningEvent {
		
	public RemoveConditioningEventTreesOnly (ObjectId projectId, ObjectId eventId){
		super(projectId,null,eventId);
	}
	
	@Override
	public void execute() {
		if(project==null || view==null || event==null){
			project=Database.get(Project.class, projectId);
			event=Database.get(ConditioningEvent.class, eventId);
		}
		
		mv=Database.get(MasterView.class, project.getMasterView());	
		
		LOGGER.info("Removing ConditioningEvent ("+event.getId()+") from Master View Only.");
		
		// Remove-right all instances of conditioning event in master view
		Tree tree=mv.getTree();
		updateDFS(tree.getRoot(), false);
		tree.forceDehydrate();
		mv.setTree(tree);
		mv.save();
		
		// update changed trees
		try {
			for(View v: ProjectManager.getInstance().getViews(projectId)){
				if (v.isAssigned(event)) {
					LOGGER.info("Updating View ("+event.getId()+") due to tree-removal of assigned Conditioning Event.");
					UpdateFBViewJob job=new UpdateFBViewJob(project,v, mv);
					job.execute();
				}
			}
		} catch ( InvalidDataException e) {
			LOGGER.error("Unable to get Views for Project ("+projectId+").");
			e.printStackTrace();
		}
	}

}
