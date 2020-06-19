package com.imaginationsupport.data;

import java.time.LocalDateTime;

import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.*;

@RestApiObjectInfo( definitionName = "TimelineEvent", tagName = RestApiHandlerInfo.CategoryNames.TimelineEvent, description = "Scenario Explorer Timeline Event" )
public class TimelineEvent extends Persistent implements ApiObject, Comparable< TimelineEvent >
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys extends Persistent.JsonKeys
	{
		public static final String Label = "name";
		public static final String Description = "description";
		public static final String Start = "start";
		public static final String End = "end";
		public static final String Url = "url";
	}

	/**
	 * Holds the available notification keys
	 */
	public abstract static class Notifications
	{
		public static final String NEVER_USED = "timeline-event-never-used";
	}

	@Indexed @Embedded
	private ObjectId project;

	@RestApiFieldInfo( description = "The name of the timeline event" )
	private String label = "NO LABEL SET";

	@RestApiFieldInfo( description = "The description of the timeline event" )
	private String description = "NO DESCRIPTION SET";

	@Indexed
	@RestApiFieldInfo( description = "The date and time of the start of the timeline event" )
	private LocalDateTime start;

	@Indexed
	@RestApiFieldInfo( description = "The date and time of the end of the timeline event" )
	private LocalDateTime end;

	@RestApiFieldInfo( description = "The URL of the timeline event", isRequired = false )
	private String url = null;

	public TimelineEvent(){
	}

	public TimelineEvent(ObjectId projectId, String label, String description, LocalDateTime start, LocalDateTime end, String url){
		this.project=projectId;
		this.label=label;
		this.description=description;
		this.start=start;
		this.end=end;
		this.url=url;
		markModified();
	}

	public TimelineEvent( final JSONObject source ) throws InvalidDataException
	{
		super( source );

		this.label = JsonHelper.getRequiredParameterString( source, JsonKeys.Label );
		this.description = JsonHelper.getRequiredParameterString( source, JsonKeys.Description );
		this.start = JsonHelper.getRequiredParameterDateTime( source, JsonKeys.Start );
		this.end = JsonHelper.getRequiredParameterDateTime( source, JsonKeys.End );
		this.url = JsonHelper.getOptionalParameterString( source, JsonKeys.Url );

		return;
	}

	public ObjectId getProject() {
		return project;
	}

	public void setProject(ObjectId projectId) {
		this.project=projectId;
		markModified();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String newLabel) {
		this.label = newLabel;
		markModified();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String newDescription) {
		this.description = newDescription;
		markModified();
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime newStart) {
		this.start = newStart;
		markModified();
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime newEnd) {
		this.end = newEnd;
		markModified();
	}

	public String getURL() {
		return url;
	}

	public void setURL(String newURL) {
		this.url = newURL;
		markModified();
	}

	@Override
	public JSONObject toJSON() throws GeneralScenarioExplorerException
	{
		final JSONObject json = super.getBaseJson();

		JsonHelper.put( json, JsonKeys.Label, this.label );
		JsonHelper.put( json, JsonKeys.Description, this.description );
		JsonHelper.put( json, JsonKeys.Start, this.start );
		JsonHelper.put( json, JsonKeys.End, this.end );
		JsonHelper.put( json, JsonKeys.Url, this.url );

		return json;
	}

	@Override
	public int compareTo(TimelineEvent other) {
		return this.start.equals(other.start)
				? this.getId().compareTo(other.getId())
				: this.start.compareTo( other.start );
	}

	@Override
	public boolean equals( final Object other )
	{
		if( other == this )
		{
			return true;
		}

		return other instanceof TimelineEvent
			&& this.getId().equals( ( (TimelineEvent)other ).getId() );
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder( 17, 31 )
			.append( getId() )
			.toHashCode();
	}
}
