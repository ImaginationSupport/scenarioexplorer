package com.imaginationsupport.backend.jobs;


import java.util.ArrayList;
import java.util.List;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;

import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.data.*;
import com.imaginationsupport.data.tree.CNode;
import com.imaginationsupport.data.tree.SNode;
import com.imaginationsupport.data.tree.Tree;
import com.imaginationsupport.data.tree.TreeNode;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.views.View;

public class RemoveConditioningEvent extends Job {
	
	protected ObjectId projectId=null;
	protected ObjectId viewId=null;
	protected ObjectId eventId=null;
		
	public RemoveConditioningEvent (ObjectId projectId, ObjectId originId, ObjectId eventId){
		this.projectId=projectId;
		this.viewId=originId;
		this.eventId=eventId;
	}
	
	protected Project project=null;
	protected View view=null;
	protected ConditioningEvent event=null;

	protected MasterView mv=null;
	
	@Override
	public void execute() throws GeneralScenarioExplorerException
	{
		if(project==null || view==null || event==null){
			project=Database.get(Project.class, projectId);
			view=Database.get(View.class, viewId);
			event=Database.get(ConditioningEvent.class, eventId);
		}
		
		mv=Database.get(MasterView.class, project.getMasterView());
		
		// check if this should just unassign a ce from an assigned view
		if (!event.getOriginViewId().equals(viewId)) {
			LOGGER.info("Unassigning Removed ConditioningEvent ("+eventId+") from Assigned View ("+viewId+") ");
			view.unassign(event);
			view.save();
			Job job=new UpdateFBViewJob(project, view, mv);
			job.execute();
			return;
		}
		
		// view==origin, so ce must be removed from all views
		LOGGER.info("Removing ConditioningEvent ("+eventId+") from Origin View ("+viewId+") and any Assigned Views.");
		
		// Remove assignment from views and update them
		try {
			for(View v: ProjectManager.getInstance().getViews(projectId)){
				if (v.isAssigned(event)) {
					LOGGER.info("Unassigning Removed ConditioningEvent ("+eventId+") from Assigned View ("+viewId+") ");
					v.unassign(event);
					v.save();
					UpdateFBViewJob job=new UpdateFBViewJob(project,v, mv);
					job.execute();
				}
			}
		} catch ( InvalidDataException e) {
			LOGGER.error("Unable to get Views for Project ("+projectId+"): "+e);
			e.printStackTrace();
		}
		
		// Remove-right all instances of conditioning event in master view
		LOGGER.info("Removing ConditioningEvent ("+eventId+") from Master View for Project ("+projectId+").");
		Tree tree=mv.getTree();
		updateDFS(tree.getRoot(), false);
		tree.forceDehydrate();
		mv.setTree(tree);
		mv.save();
		
		// delete the conditioning event itself
		Database.delete(ConditioningEvent.class,event.getId());
	}
	
	protected void updateDFS(TreeNode current, boolean prune){
		if(current instanceof CNode) {
			if(current.getDBId().equals(event.getId())){
				prune=true;	// reached a prune point in the tree, clip children from here
			}
			List<TreeNode> branch = copy(current.getChildren());
			if(branch!=null) {
				for (TreeNode next:branch) {
					updateDFS(next,prune); // start pruning from here out
				}
			}
			// after handling children move to 
			if (prune) { // remove this CE node from its parent
				List<TreeNode> siblings=current.getParent().getChildren();
				siblings.remove(current);
				current.getParent().setChildren(siblings);
			}
		} else if (current instanceof SNode) {
			if(current.getChildren()!=null) {
				for (TreeNode next:copy(current.getChildren())) {
					updateDFS(next,prune);
				}
			}
			// either no children or all have been processed
			if (prune) { // remove this CE node from its parent
				List<TreeNode> siblings=current.getParent().getChildren();
				siblings.remove(current);
				current.getParent().setChildren(siblings);
				Database.delete(State.class,current.getDBId());
			}
		}
	}
	
	// avoid editing same list we are iterating in
	protected List<TreeNode> copy(List<TreeNode> in){
		List<TreeNode> out= new ArrayList<>(in.size());
		out.addAll(in);
		return out;
	}
	
}
