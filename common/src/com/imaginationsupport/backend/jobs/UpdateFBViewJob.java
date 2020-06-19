package com.imaginationsupport.backend.jobs;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import com.imaginationsupport.Database;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.tree.CNode;
import com.imaginationsupport.data.tree.Trajectory;
import com.imaginationsupport.data.tree.Tree;
import com.imaginationsupport.data.tree.TreeNode;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.views.View;

public class UpdateFBViewJob extends Job {
	
	private ObjectId projectId=null;
	private ObjectId viewId=null;
	
	public UpdateFBViewJob(ObjectId projectId, ObjectId viewId) {
		this.projectId=projectId;
		this.viewId=viewId;
	}
	
	private Project project=null;
	private View view=null;
	private MasterView mv=null;

	protected UpdateFBViewJob(Project project, View view, MasterView mv){
		this.project=project;
		this.projectId=project.getId();
		this.view=view;
		this.viewId=view.getId();
		this.mv=mv;
	}
	
	@Override
	public void execute() {
		
		if(project==null || view==null || mv==null) {
			project=Database.get(Project.class, projectId);
			view=Database.get(View.class, viewId);
			mv= (MasterView) Database.get(View.class, project.getMasterView());
		}
		 
		Tree updated=new Tree(project.getNow());
		List<Trajectory> mtree=mv.getTree().getTrajectories();
		Set<ObjectId> assigned= view.getAssignedConditioningEventIds();

		LOGGER.info("Updating trajectories for View ("+viewId+")");
		for (Trajectory t: mtree){
			boolean include=true;
			for(TreeNode n: t.getNodes()){
				if(n instanceof CNode){
					if(!assigned.contains(n.getDBId())){
						include=false;
						continue;
					}
				}
			}
			if(include){ // && hasCe){
				updated.addTrajectory(t);
			}
		}
		view.setTree(updated);
		view.save();
	}

}
