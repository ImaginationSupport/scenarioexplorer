package com.imaginationsupport.backend.jobs;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.views.View;
import org.bson.types.ObjectId;

import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.TimelineEvent;
import com.imaginationsupport.plugins.Precondition;
import com.imaginationsupport.plugins.preconditions.OnHold;
import com.imaginationsupport.plugins.preconditions.TimelineEventPrecondition;
import org.json.JSONObject;

public class RemoveTimelineEvent extends Job {

	private ProjectManager pm=ProjectManager.getInstance();
	private ObjectId projectId=null;
	private ObjectId timelineEventId=null;
	private ObjectId replacementTimelineEventId=null;
	
	public RemoveTimelineEvent(ObjectId projectId, ObjectId timelineEventId) {
		this.projectId=projectId;
		this.timelineEventId=timelineEventId;
	}

	public RemoveTimelineEvent(ObjectId projectId, ObjectId timelineEventId, ObjectId replacementTimelineEventId) {
		this.projectId=projectId;
		this.timelineEventId=timelineEventId;
		this.replacementTimelineEventId=replacementTimelineEventId;
	}
	
	private Project project=null;
	private TimelineEvent timelineEvent=null;
	
	public RemoveTimelineEvent(Project project, TimelineEvent timelineEvent) {
		this.project=project;
		this.projectId=project.getId();
		this.timelineEvent=timelineEvent;
		this.timelineEventId=timelineEvent.getId();
	}
	
	@Override
	public void execute() {
		if (project==null || timelineEvent==null) {
			project=Database.get(Project.class, projectId);
			timelineEvent=Database.get(TimelineEvent.class,timelineEventId);
		}
		ProjectManager projectManager=ProjectManager.getInstance();
		
		// Removing a timeline event could break a conditioning event, check for this and put CE on hold, and remove from trees
		try {
			for (ConditioningEvent ce: pm.getConditioningEventsForProjectId(projectId)) {
				for(Precondition p:ce.getPreconditions()) {
					if(p instanceof TimelineEventPrecondition) {
						TimelineEventPrecondition tep=(TimelineEventPrecondition)p;
						if(tep.getTimelineEvent().getId()==timelineEventId) {
							if(replacementTimelineEventId!=null) {
								LOGGER.info("Replacing of Conditioning Event (" + ce.getId() + ") precondition that depends on updated TimelineEvent (" + timelineEventId + ").");
								ObjectId originalViewId=ce.getOriginViewId();
								ObjectId originalCeId=ce.getId();

								JSONObject configJSON=tep.getConfig();
								JsonHelper.put( configJSON, TimelineEventPrecondition.JSON_KEY_TIMELINE_EVENT_ID, replacementTimelineEventId );
								tep.setConfig( configJSON );
								ce.setId(null);
								ce.save();

								ConditioningEvent updated=projectManager.addConditioningEvent(projectId, originalViewId, ce);

								// Assign new CE to all the existing Views that the previous one was assigned
								for(View v:projectManager.getViews(projectId)) {
									if(v.isAssigned(originalCeId)) {
										v.assign(updated);
										v.save();
									}
								}
								projectManager.removeConditioningEvent(projectId, originalViewId, originalCeId);
							} else {
								LOGGER.info("Detected removal of Conditioning Event (" + ce.getId() + ") precondition that depends on removed TimelineEvent (" + timelineEventId + ").");
								ce.removePrecondition(tep);
								ce.addPrecondition(new OnHold("Previously set Timeline Event (" + tep.getLabel() + ") was removed."));
								ce.save();
								Job job = new RemoveConditioningEventTreesOnly(projectId, ce.getId());
								job.execute();
							}
						}
					}
				}
			}
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			LOGGER.error("Unable to get Conditioning Events for Project ("+projectId+").");
			e.printStackTrace();
		}
		
		Database.delete(TimelineEvent.class, timelineEventId);
	}

}
