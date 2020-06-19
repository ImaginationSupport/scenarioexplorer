package com.imaginationsupport.backend.jobs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.data.*;
import com.imaginationsupport.data.tree.CNode;
import com.imaginationsupport.data.tree.SNode;
import com.imaginationsupport.data.tree.Tree;
import com.imaginationsupport.data.tree.TreeNode;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.data.TimeStepGenerator;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.views.View;
import com.imaginationsupport.plugins.Effect;
import com.imaginationsupport.plugins.Precondition;
import com.imaginationsupport.plugins.preconditions.TimelineEventPrecondition;

import org.bson.types.ObjectId;

public class AddConditioningEvent extends Job {
	
	private ObjectId projectId=null;
	private ObjectId viewId=null;
	private ObjectId eventId=null;
		
	public AddConditioningEvent (ObjectId projectId, ObjectId viewId, ObjectId eventId){
		this.projectId=projectId;
		this.viewId=viewId;
		this.eventId=eventId;
	}
	
	private ProjectManager pm= ProjectManager.getInstance();
	
	private Project project=null;
	private View view=null;
	private ConditioningEvent event=null;
	private MasterView mv=null;
	
	@Override
	public void execute() throws GeneralScenarioExplorerException
	{
		if(projectId==null || viewId==null || eventId==null){
			LOGGER.error("Null Project, View, or Event sent to AddConditioningEventJob, failing job.");
			throw new RuntimeException("Null Project, View, or Event sent to AddConditioningEventJob");
		}
		
		project=Database.get(Project.class,projectId);
		view=Database.get(View.class, viewId);
		event=Database.get(ConditioningEvent.class, eventId);
		mv=Database.get(MasterView.class, project.getMasterView());
		
		event.setProject(projectId);
		event.setOriginView(viewId);
		event.save();
		
		Set<ConditioningEvent> conditioningEvents=null;
		try {
			conditioningEvents = pm.getConditioningEventsForProjectId(projectId);
		} catch ( InvalidDataException e) {
			LOGGER.error("Unable to get Conditioning Events for Project ("+projectId+"): "+e);
		}
		if(conditioningEvents==null) conditioningEvents = new HashSet<>();
		if(conditioningEvents.contains(event)) {
			conditioningEvents.remove(event);  // we do not want the new conditioning event in the list
		}	
		
		view.assignConditioningEvent(eventId);
		view.save();
		
		TimeStepGenerator tsg=new TimeStepGenerator();
		tsg.addFullTimeline(projectId);
		tsg.addIncrements(project.getStart(),project.getEnd(),project.getDaysIncrement());
		List<LocalDateTime> times= tsg.getTimeSteps();
		LOGGER.info("Looking at "+times.size()+" temporal points for Conditioning Event "+event.getLabel()+".");
		
		List<ConditioningEvent> remainingCEs= new ArrayList<>();
		remainingCEs.addAll(conditioningEvents);
		
		Tree tree=mv.getTree();
		try
		{
			updateDFS( tree.getRoot(), event, remainingCEs );
		}
		catch( final InvalidDataException e )
		{
			throw new GeneralScenarioExplorerException( "Error updating tree", e );
		}

		tree.forceDehydrate();
		mv.setTree(tree);
		mv.save();
		
		UpdateFBViewJob uvj= new UpdateFBViewJob(project, view, mv);
		uvj.execute();
		
	}
	
	private void updateDFS(TreeNode current, ConditioningEvent event, List<ConditioningEvent> available) throws InvalidDataException, GeneralScenarioExplorerException
	{

		if(current instanceof CNode) {
			if(current.getDBId().equals(event.getId())){
				return;  // this tree already contains this conditioning event.
			}
			List<ConditioningEvent> remaining= new ArrayList<ConditioningEvent>();
			for(ConditioningEvent ce: available) {
				if(!current.getDBId().equals(ce.getId())) {
					remaining.add(ce); // remove the current conditioning event from the future context
				}
			}
			if(current.getChildren()!=null) {
				for (TreeNode next:current.getChildren()) {
					updateDFS(next,event,remaining); // recurse removing this ce from list to use in the future
				}
			}
		} else if (current instanceof SNode) {
			if(current.getChildren()!=null) {
				for (TreeNode next:current.getChildren()) {
					updateDFS(next,event,available);
				}
			}
			// either no children or all have been processed
			SNode checkStateNode=(SNode) current;
			State checkState=Database.get(State.class,checkStateNode.getDBId());
	
			if(!checkState.isRange()) { // This is a point from which we can extends
				
				// Find appropriate time ranges for this conditioning event
				TimeStepGenerator tsg=new TimeStepGenerator();
				boolean constrained=false;
				if(event.getPreconditions()!=null) {
					for(Precondition p: event.getPreconditions()) {
						if(p instanceof TimelineEventPrecondition) {
							tsg.addTimelineEvent(((TimelineEventPrecondition)p).getTimelineEvent());
							constrained=true;
						}
					}
				}
				if (!constrained) {
					tsg.addIncrements(checkState.getEnd(), project.getEnd(), project.getDaysIncrement());
				}
				List<LocalDateTime> times= new ArrayList<LocalDateTime>();
				for (LocalDateTime t: tsg.getTimeSteps()){
					if(t.isAfter(checkState.getEnd()))
						times.add(t); // should also remove the current time
				}
				
				//Attempt to attached CE to each time
				List<ConditioningEvent> leftOverCe= new ArrayList<ConditioningEvent>(available.size());
				leftOverCe.addAll(available);
				//TODO: if event can be repeated the check for that here...
				leftOverCe.remove(event);
				attachCE(checkState,checkStateNode,event,times, available);
			}
		}
	}
		
	private void attachCE(
		State attachmentState,
		SNode attachmentNode,
		ConditioningEvent event,
		List<LocalDateTime> remainingTimes,
		List<ConditioningEvent> remainingCEs) throws InvalidDataException, GeneralScenarioExplorerException
	{
		for (LocalDateTime future: remainingTimes){
			State stateRangeToNextTime=new State(project,ProjectFromStateJob.NOTHING_HAPPENS_LABEL,ProjectFromStateJob.NOTHING_HAPPENS_DESC,
				attachmentState.getEnd(),future,attachmentState);
			stateRangeToNextTime.setRange(true);
			SNode stateRangeToNextTimeNode=null;
			
			// Attempt to attach CE to previous state with stateRange
			if(event.preconditionsSatisfied(stateRangeToNextTime)){
				// this is a keeper state range
				stateRangeToNextTime.save();
				stateRangeToNextTimeNode=new SNode(stateRangeToNextTime);
				attachmentNode.addChild(stateRangeToNextTimeNode);
				
				int osize=event.getOutcomes().size();
				for(int outcomeIndex=0; outcomeIndex<osize; outcomeIndex++){
					CNode cnode=new CNode(event,outcomeIndex);
					stateRangeToNextTimeNode.addChild(cnode);
					Outcome outcome=event.outcome(outcomeIndex);
					LocalDateTime postCeTime=stateRangeToNextTime.getEnd().plusDays(ProjectFromStateJob.DAYS_FOR_NONE_RANGE); //mission.getDaysIncrement()); // how large to make state until next
					State postCeState=new State(project,outcome.getLabel(),outcome.getDescription(),stateRangeToNextTime.getEnd(),postCeTime,stateRangeToNextTime);
					postCeState.setRange(false);
					postCeState.setProbability(attachmentState.getProbability()*event.getPEventGivenPreconditions()*outcome.getLikelihood());
					for (Effect e: outcome.getEffects()){
						try {
							e.apply(postCeState);
						} catch (DatastoreException e1) {
							LOGGER.error(e1);
//							e1.printStackTrace();
						}
					}
					postCeState.save();
					SNode postCeSnode=new SNode(postCeState);
					cnode.addChild(postCeSnode);
					
					// recursive update on this node
					List<ConditioningEvent> leftOverCe= new ArrayList<>(remainingCEs.size());
					leftOverCe.addAll(remainingCEs);
					//TODO: if event can be repeated the check for that here...
					leftOverCe.remove(event);
					
					List<LocalDateTime> leftOverTimes= new ArrayList<LocalDateTime>();
					TimeStepGenerator tsg=new TimeStepGenerator();
					tsg.addFullTimeline(projectId);
					tsg.addIncrements(postCeState.getEnd(), project.getEnd(), project.getDaysIncrement());
					for (LocalDateTime t: tsg.getTimeSteps()){
						if(t.isAfter(postCeTime))
							leftOverTimes.add(t); // should also remove the current time
					}
					
					ProjectFromStateJob pj=new ProjectFromStateJob(project, postCeState, leftOverCe, leftOverTimes);
					pj.executeWithTree(postCeSnode);
				}
				return;
			}
		}		
	}
	
	
}
