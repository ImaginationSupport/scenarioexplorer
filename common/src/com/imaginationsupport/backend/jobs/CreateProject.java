package com.imaginationsupport.backend.jobs;

import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.data.tree.Tree;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.data.TimeStepGenerator;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.views.MasterView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

public class CreateProject extends Job {

	private ObjectId projectId=null;
	
	public CreateProject(ObjectId projectId){
		this.projectId=projectId;
	}
	
	@Override
	public void execute() throws GeneralScenarioExplorerException
	{
		if(projectId==null){
			LOGGER.error("Null ProjectId sent to CreateProject job.");
			throw new RuntimeException("Null ProjectId sent to CreateProject job.");
		}
		
		Project project=Database.get(Project.class, projectId);
		if (project==null) {
			LOGGER.error("No such ProjectId in Database.");
			throw new RuntimeException("No such ProjectId in Database.");
		}
		
		// Create Project View
		LOGGER.info("Creating Master View for Project: "+project.getName());
		MasterView mv=new MasterView( project.getId(), ProjectManager.MASTER_VIEW_NAME, ProjectManager.MASTER_VIEW_DESC);
		mv.save();
		
		// Create the Now state
		LOGGER.info("Building Now State for Project: "+project.getName());
		State root=new State();
		root.setLabel("Now");
		root.setDescription("This is the root or Now state for this project.");
		root.setRange(false); // indicates this state can be built from with new states
		root.setProject(project);
		root.setStart(project.getStart().minusSeconds(10)); //.minusDays(10));
		root.setEnd(project.getStart().plusSeconds(10)); // .plusDays(10));
		root.setProbability(1.0);
		for (FeatureMap map: project.getFeatureMaps()){
			try{
				root.updateFeature(map.getDefaultFeature());
/*
 *TODO: Need to project after the historic data
				Projector projector=map.getProjector();
				if (projector!=null){ 
					updateFeature(projector.project(map, previous.getFeature(map.getUid())));	
				} else {
					updateFeature(previous.getFeature(map.getUid()));
				}
*/
			} catch (DatastoreException | InvalidDataException e) {
				LOGGER.error("ERROR: Failed to create Feature("+map.getUid()+":"+map.getLabel()+" on Now state for Project View: ",e);
			}
		}
		
		root.save();

		LOGGER.info("Building Now State for Project: "+project.getName());


		TimeStepGenerator tsg=new TimeStepGenerator();
		tsg.addFullTimeline(project.getId());
		tsg.addIncrements(project.getStart(),project.getEnd(),project.getDaysIncrement());
		List<LocalDateTime> times= tsg.getTimeSteps();
		
		Set<ConditioningEvent> temp=null;
		try {
			temp = ProjectManager.getInstance().getConditioningEvents(project);
		} catch ( InvalidDataException e) {
			LOGGER.error("Unable to get Conditioning Events for Project ("+project.getId()+"): "+e);
		}
		if (temp==null) temp=new HashSet<ConditioningEvent>();
		List<ConditioningEvent> tempList=new ArrayList<ConditioningEvent>(temp.size());
		tempList.addAll(temp);
		
		Tree tree=new Tree(root);
		ProjectFromStateJob pj=new ProjectFromStateJob(project, root,  tempList,  times);

		try
		{
			pj.executeWithTree(tree.getRoot());
		}
		catch( final InvalidDataException e )
		{
			throw new GeneralScenarioExplorerException( "Error updating tree", e );
		}

		mv.setTree(tree);
		mv.save();
		
		LOGGER.info("Saving Project: "+project.getName());
		project.setMasterView(mv.getId());
		project.setNow(root);
		project.save();
	}

}
