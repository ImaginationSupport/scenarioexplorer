package com.imaginationsupport.data;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.UserManager;
import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Indexed;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RestApiObjectInfo( definitionName = "ProjectTemplate", tagName = RestApiHandlerInfo.CategoryNames.ProjectTemplate, description = "Scenario Explorer Project Template" )
public class ProjectTemplate extends Persistent implements ApiObject, Comparable< ProjectTemplate >
{
	public static final String JSON_KEY_NAME = "name";
	public static final String JSON_KEY_DESCRIPTION = "description";
	public static final String JSON_KEY_START = "start";
	public static final String JSON_KEY_END = "end";
	public static final String JSON_KEY_DAYS_INCREMENT = "increment";
	public static final String JSON_KEY_CREATED_ON = "createdOn";
	public static final String JSON_KEY_CREATOR_ID = "creatorId";
	public static final String JSON_KEY_SOURCE_PROJECT_ID = "sourceProjectId";
	public static final String JSON_KEY_FEATURES = "features";
	public static final String JSON_KEY_TIMELINE_EVENTS = "timelineEvents";
//	public static final String JSON_KEY_VIEWS = "views";
//	public static final String JSON_KEY_CONDITIONING_EVENTS = "conditioningEvents";

	/**
	 * Holds the project name
	 */
	@Indexed
	@RestApiFieldInfo( description = "The name of the project template template" )
	private String name;

	/**
	 * Holds the project description
	 */
	@RestApiFieldInfo( description = "The description of the project template template" )
	private String description;

	/**
	 * Holds the project start date
	 */
	@RestApiFieldInfo( description = "The starting date and time of the project template" )
	private LocalDateTime start;

	/**
	 * Holds the project end date
	 */
	@RestApiFieldInfo( description = "The ending date and time of the project template" )
	private LocalDateTime end;

	/**
	 * Holds the number of days to increment when updating the tree
	 */
	@RestApiFieldInfo( description = "The number of days to increment when looking to add new states and conditioning events" )
	private int daysIncrement;

	/**
	 * Holds the date the project was created
	 */
	@RestApiFieldInfo( description = "The date and time the project template was created", isRequired = false )
	private LocalDateTime createdOn;

	@Indexed
	@RestApiFieldInfo( description = "The id of the creator of the project template" )
	private ObjectId creatorId;

	@RestApiFieldInfo( description = "The set of features" )
	private Set< FeatureMap > features;

	@RestApiFieldInfo( description = "The set of timeline events" )
	private Set< TimelineEvent > timelineEvents;

//	@Embedded
//	@RestApiFieldInfo( description = "The set of views" )
//	private final Set< View > views;

//	@Embedded
//	@RestApiFieldInfo( description = "The set of conditioning events" )
//	private final Set< ConditioningEvent > conditioningEvents;

	/**
	 * Holds the id of the source project
	 */
	@Indexed
	@RestApiFieldInfo( description = "The id of the project this template was created from" )
	private ObjectId sourceProjectId;

	/**
	 * Default constructor should only be used by Morphia
	 */
	public ProjectTemplate()
	{
		return;
	}

	public ProjectTemplate(
		final String name,
		final String description,
		final LocalDateTime start,
		final LocalDateTime end,
		final int daysIncrement,
		final ObjectId creatorId,
		final ObjectId sourceProjectId,
		final Collection< FeatureMap > features,
		final Collection< TimelineEvent > timelineEvents ) throws InvalidDataException
	{
		if( timelineEvents == null )
		{
			throw new InvalidDataException( "Source timeline events cannot be null!" );
		}

		this.name = name;
		this.description = description;
		this.start = start;
		this.end = end;
		this.daysIncrement = daysIncrement;
		this.createdOn = LocalDateTime.now();
		this.creatorId = creatorId;
		this.sourceProjectId = sourceProjectId;
		this.features = new HashSet<>( features );
		this.timelineEvents = new HashSet<>( timelineEvents );
//		this.views =
//		this.conditioningEvents =

		markModified();

		return;
	}

	public ProjectTemplate( final JSONObject source ) throws InvalidDataException
	{
		super( source );

		this.name = JsonHelper.getRequiredParameterString( source, JSON_KEY_NAME );
		this.description = JsonHelper.getRequiredParameterString( source, JSON_KEY_DESCRIPTION );
		this.start = JsonHelper.getRequiredParameterDateTime( source, JSON_KEY_START );
		this.end = JsonHelper.getRequiredParameterDateTime( source, JSON_KEY_END );
		this.daysIncrement = JsonHelper.getRequiredParameterInt( source, JSON_KEY_DAYS_INCREMENT );
		this.createdOn = JsonHelper.getRequiredParameterDateTime( source, JSON_KEY_CREATED_ON );
		this.creatorId = JsonHelper.getRequiredParameterObjectId( source, JSON_KEY_CREATOR_ID, true );
		this.sourceProjectId = JsonHelper.getRequiredParameterObjectId( source, JSON_KEY_SOURCE_PROJECT_ID, true );

		if( this.name == null || this.name.trim().isEmpty() )
		{
			throw new InvalidDataException( "Project template name cannot be null or empty!" );
		}
		if( this.description == null )
		{
			throw new InvalidDataException( "Project template description cannot be null!" );
		}
		if( this.start == null )
		{
			throw new InvalidDataException( "Project template start date cannot be null!" );
		}
		if( this.end == null )
		{
			throw new InvalidDataException( "Project template end date cannot be null!" );
		}
		if( this.end.isBefore( this.start ) || this.end.equals( this.start ) )
		{
			throw new InvalidDataException( "Project template end date must be AFTER the start date!" );
		}
		if( this.daysIncrement <= 0 )
		{
			throw new InvalidDataException( "Invalid Days Increment!" );
		}
		if( this.createdOn == null )
		{
			throw new InvalidDataException( "Project template created in cannot be null!" );
		}

		// Note: creatorId and sourceProjectId are allowed to be null

		this.features = new HashSet<>();
		final JSONArray rawFeatures = JsonHelper.getRequiredParameterJSONArray( source, JSON_KEY_FEATURES );
		for( int i = 0; i < rawFeatures.length(); ++i )
		{
			this.features.add( new FeatureMap( rawFeatures.getJSONObject( i ) ) );
		}

		this.timelineEvents = new HashSet<>();
		final JSONArray rawTimelineEvents = JsonHelper.getRequiredParameterJSONArray( source, JSON_KEY_TIMELINE_EVENTS );
		for( int i = 0; i < rawTimelineEvents.length(); ++i )
		{
			this.timelineEvents.add( new TimelineEvent( rawTimelineEvents.getJSONObject( i ) ) );
		}

		// this.views =
		// this.conditioningEvents =

		markModified();

		return;
	}

	public String getName()
	{
		return this.name;
	}

	public String getDescription()
	{
		return this.description;
	}

	public LocalDateTime getStart()
	{
		return this.start;
	}

	public LocalDateTime getEnd()
	{
		return this.end;
	}

	public int getDaysIncrement()
	{
		return this.daysIncrement;
	}

	public Set< FeatureMap > getFeatures()
	{
		return this.features;
	}

	public Set< TimelineEvent > getTimelineEvents()
	{
		return this.timelineEvents;
	}

//	public Set< View > getViews()
//	{
//		return this.views;
//	}

//	public Set< ConditioningEvent > getConditioningEvents()
//	{
//		return this.conditioningEvents;
//	}

	public LocalDateTime getCreatedOn()
	{
		return this.createdOn;
	}

	public User getCreator() throws GeneralScenarioExplorerException
	{
		return UserManager.getInstance().getUser( this.creatorId );
	}

	public void setCreator( final User newCreator )
	{
		this.creatorId = newCreator.getId();
		markModified();

		return;
	}

	public ObjectId getSourceProjectId()
	{
		return this.sourceProjectId;
	}

	@Override
	public JSONObject toJSON() throws InvalidDataException, GeneralScenarioExplorerException
	{
		final JSONObject json = super.getBaseJson();

		JsonHelper.put( json, JSON_KEY_NAME, this.name );
		JsonHelper.put( json, JSON_KEY_DESCRIPTION, this.description );
		JsonHelper.put( json, JSON_KEY_START, ImaginationSupportUtil.formatDateTime( this.start ) );
		JsonHelper.put( json, JSON_KEY_END, ImaginationSupportUtil.formatDateTime( this.end ) );
		JsonHelper.put( json, JSON_KEY_DAYS_INCREMENT, this.daysIncrement );
		JsonHelper.put( json, JSON_KEY_CREATED_ON, ImaginationSupportUtil.formatDateTime( this.createdOn ) );
		JsonHelper.put( json, JSON_KEY_CREATOR_ID, this.creatorId == null ? null : this.creatorId.toHexString() );
		JsonHelper.put( json, JSON_KEY_SOURCE_PROJECT_ID, this.sourceProjectId == null ? null : this.sourceProjectId.toHexString() );
		JsonHelper.put( json, JSON_KEY_FEATURES, this.features );
		JsonHelper.put( json, JSON_KEY_TIMELINE_EVENTS, this.timelineEvents );

		return json;
	}

	@Override
	public int compareTo( final ProjectTemplate other )
	{
		return this.name.equals( other.name )
			? this.getId().compareTo( other.getId() )
			: this.name.compareTo( other.name );
	}
}
