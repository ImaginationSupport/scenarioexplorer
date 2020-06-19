package com.imaginationsupport.plugins.preconditions;

import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.TimelineEvent;
import com.imaginationsupport.data.TemporalRelationship;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.Precondition;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class TimelineEventPrecondition extends Precondition {

	public static final String JSON_KEY_RELATION = "relation";
	public static final String JSON_KEY_TIMELINE_EVENT_ID = "timelineEventId";
	// Note: timeline event ids can also be changed when timeline events change in job.

//	private TemporalRelationship relation=TemporalRelationship.DURING;
//	private ObjectId timelineEventId=null;

//	@NotSaved
//	private TimelineEvent timelineEvent;

	public TimelineEventPrecondition()
	{
		final JSONObject configJSON = new JSONObject();

		JsonHelper.put( configJSON, JSON_KEY_RELATION, TemporalRelationship.DURING.getLabel() );
		JsonHelper.put( configJSON, JSON_KEY_TIMELINE_EVENT_ID, (ObjectId)null );

		setConfig( configJSON );

		return;
	}

	public TimelineEventPrecondition( final TemporalRelationship relation, final ObjectId timelineEventId )
	{
		final JSONObject configJSON = new JSONObject();

		JsonHelper.put( configJSON, JSON_KEY_RELATION, relation.getLabel() );
		JsonHelper.put( configJSON, JSON_KEY_TIMELINE_EVENT_ID, timelineEventId );

		setConfig( configJSON );

		return;
	}

//	public TimelineEventPrecondition(TemporalRelationship relation, ObjectId timelineEventId){
//		this.relation=relation;
//		this.timelineEventId=timelineEventId;
//	}

	public TimelineEvent getTimelineEvent() throws InvalidDataException, GeneralScenarioExplorerException
	{
		return ProjectManager.getInstance().getTimelineEvent( JsonHelper.getRequiredParameterObjectId( getConfig(), JSON_KEY_TIMELINE_EVENT_ID, false ) );
	}

	public TemporalRelationship getRelation() throws InvalidDataException, GeneralScenarioExplorerException
	{
		return TemporalRelationship.fromLabel( JsonHelper.getRequiredParameterString( getConfig(), JSON_KEY_RELATION ) );
	}

	public boolean satisfied(State state) throws InvalidDataException, GeneralScenarioExplorerException
	{
		LocalDateTime point=state.getEnd();
		TimelineEvent timelineEvent = getTimelineEvent();

		LocalDateTime intervalStart=timelineEvent.getStart();
		LocalDateTime intervalEnd=timelineEvent.getEnd();

		switch(getRelation()){
			case PRECEEDES:
				if(point.isBefore(intervalStart) && !point.isEqual(intervalStart)) return true;
				break;
			case STARTS:
				if(point.isEqual(intervalStart)) return true;
				break;
			case DURING:
				if(point.isAfter(intervalStart) && point.isBefore(intervalEnd)) return true;
				break;
			case FINISHES:
				if(point.isEqual(intervalEnd)) return true;
				break;
			case PRECEDEDBY:
				if(point.isAfter(intervalEnd) && !point.isEqual(intervalEnd)) return true;
				break;
			default:
				throw new GeneralScenarioExplorerException( "Error in Temporal Relationship for Conditioning Event Precondition to Timeline.");
		}
		return false;
	}

	public String getLabel() {
		return "Timeline Event";
	}

	public String getDescription() {
		return "This precondition type constrains the conditioning event based on its relation to a timeline event.";
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/preconditions/timelineEventPrecondition.js";
	}
}
