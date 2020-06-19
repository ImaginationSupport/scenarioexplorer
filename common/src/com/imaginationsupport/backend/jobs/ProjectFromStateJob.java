package com.imaginationsupport.backend.jobs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.imaginationsupport.Database;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Outcome;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.tree.CNode;
import com.imaginationsupport.data.tree.SNode;
import com.imaginationsupport.data.tree.Tree;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.plugins.Effect;

public class ProjectFromStateJob extends Job {
	
	public static final String BORING_LABEL="no intervening events";
	public static final String NOTHING_HAPPENS_LABEL="...";
	public static final String NOTHING_HAPPENS_DESC="...";
	public static final int DAYS_FOR_NONE_RANGE=1;
	
	private State starting=null;
	private Project project=null;
	private List<ConditioningEvent> context=null;
	private List<LocalDateTime> times=null;

	public ProjectFromStateJob(Project project, State starting, List<ConditioningEvent> context, List<LocalDateTime> times){
		this.project=project;
		this.starting=starting;	
		this.context=context;
		this.times=times;
	}
	
	@Override
	public void execute() throws GeneralScenarioExplorerException
	{
		MasterView mv=Database.get(MasterView.class, project.getMasterView());
		Tree tree= mv.getTree();
		SNode startSnode=tree.getStateNode(starting);

		try
		{
			updateDFS(startSnode,context,times);
		}
		catch( final InvalidDataException e )
		{
			throw new GeneralScenarioExplorerException( "Error updating tree", e );
		}

		mv.setTree(tree);
		mv.save();
	}
	
	// This is used when being called from other jobs where we have a tree instantiated already in memory
	public void executeWithTree(SNode state) throws InvalidDataException, GeneralScenarioExplorerException
	{
		LOGGER.info(this.getClass().getSimpleName()+" called as sub-job.");
		updateDFS(state,context,times);
	}
	
	private void updateDFS(
		SNode startingNode,
		List<ConditioningEvent> remainingCEs,
		List<LocalDateTime> remainingTimes ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		State previous=Database.get(State.class, startingNode.getDBId());
		remainingTimes.add(previous.end); // want to check immediate application
		
		// Add state to bring us to end of mission
		if (previous.getEnd().isAfter(project.getEnd())) return; // nothing to do here
		State boring =new State(project,BORING_LABEL,NOTHING_HAPPENS_DESC,previous.getEnd(),project.getEnd(),previous);
		boring.setRange(true);
		boring.save();
		SNode boringNode=new SNode(boring);
		startingNode.addChild(boringNode);
		
		for (LocalDateTime future: remainingTimes){
			State stateRangeToNextTime=new State(project,NOTHING_HAPPENS_LABEL,NOTHING_HAPPENS_DESC,previous.getEnd(),future,previous);
			stateRangeToNextTime.setRange(true);
			SNode stateRangeToNextTimeNode=null;
			for(ConditioningEvent futureEvent:remainingCEs){
				if(futureEvent.preconditionsSatisfied(stateRangeToNextTime)){
					if(stateRangeToNextTimeNode==null){ // save state if it will be used
						stateRangeToNextTime.save();
						stateRangeToNextTimeNode=new SNode(stateRangeToNextTime);
						startingNode.addChild(stateRangeToNextTimeNode);
					}
					int osize=futureEvent.getOutcomes().size();
					for(int outcomeIndex=0; outcomeIndex<osize; outcomeIndex++){
						CNode cnode=new CNode(futureEvent,outcomeIndex);
						stateRangeToNextTimeNode.addChild(cnode);
						Outcome outcome=futureEvent.outcome(outcomeIndex);
						LocalDateTime postCeTime=stateRangeToNextTime.getEnd().plusDays(DAYS_FOR_NONE_RANGE); //mission.getDaysIncrement()); // how large to make state until next
						State postCeState=new State(project,outcome.getLabel(),outcome.getDescription(),stateRangeToNextTime.getEnd(),postCeTime,stateRangeToNextTime);
						postCeState.setProbability(previous.getProbability()*futureEvent.getPEventGivenPreconditions()*outcome.getLikelihood());
						postCeState.setRange(false);
						for (Effect e: outcome.getEffects()){
							try {
								e.apply(postCeState);
							} catch (DatastoreException e1) {
								System.err.println(e1);
								e1.printStackTrace();
							}
						}
						postCeState.save();
						SNode postCeSnode=new SNode(postCeState);
						cnode.addChild(postCeSnode);
						
						// recursive update on this node
						List<ConditioningEvent> leftOverCe= new ArrayList<ConditioningEvent>(remainingCEs.size());
						leftOverCe.addAll(remainingCEs);
						//TODO: if event can be repeated the check for that here...
						leftOverCe.remove(futureEvent);
						List<LocalDateTime> leftOverTimes= new ArrayList<LocalDateTime>(remainingTimes.size());
						for (LocalDateTime t: remainingTimes){
							if(t.isAfter(postCeTime))
								leftOverTimes.add(t); // should also remove the current time
						}
						updateDFS(postCeSnode, leftOverCe, leftOverTimes);
					}
				}
			}
		}
		
	}

}
