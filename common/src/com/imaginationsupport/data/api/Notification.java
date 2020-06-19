package com.imaginationsupport.data.api;

import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;
import org.json.JSONObject;

@RestApiObjectInfo( definitionName = "Notification", tagName = RestApiHandlerInfo.CategoryNames.Project, description = "Scenario Explorer Project Notification" )
public class Notification implements ApiObject, Comparable< Notification >
{
	private static class JsonKeys
	{
		private static final String Scope = "scope";
		private static final String Key = "key";
		private static final String Description = "description";
		private static final String State = "state";
		private static final String ViewId = "viewId";
		private static final String TimelineEventId = "timelineEventId";
		private static final String ConditioningEventId = "conditioningEventId";
		private static final String FeatureId = "featureId";
	}

	private static final String JSON_VALUE_SCOPE_PROJECT = "project";
	private static final String JSON_VALUE_SCOPE_VIEW = "view";
	private static final String JSON_VALUE_SCOPE_FEATURE = "feature";
	private static final String JSON_VALUE_SCOPE_TIMELINE_EVENT = "timeline-event";
	private static final String JSON_VALUE_SCOPE_CONDITIONING_EVENT = "conditioning-event";

	private static final String JSON_VALUE_STATE_NEW = "new";
	private static final String JSON_VALUE_STATE_ACKNOWLEDGED = "acknowledged";

	public enum Scope
	{
		Project,
		View,
		Feature,
		ConditioningEvent,
		TimelineEvent
	}

	public enum State
	{
		New,
		Acknowledged,
	}

	@RestApiFieldInfo( jsonField = JsonKeys.Scope, description = "The notification scope" )
	private Scope mScope;

	@RestApiFieldInfo( jsonField = JsonKeys.Key, description = "The notification key" )
	private String mKey;

	@RestApiFieldInfo( jsonField = JsonKeys.Description, description = "A description of the notification" )
	private String mDescription;

	@RestApiFieldInfo( jsonField = JsonKeys.State, description = "The current state" )
	private State mState;

	@RestApiFieldInfo( jsonField = JsonKeys.ViewId, description = "The id of the view the notification applies to, if applicable" )
	private ObjectId mViewId;

	@RestApiFieldInfo( jsonField = JsonKeys.TimelineEventId, description = "The id of the timeline event the notification applies to, if applicable" )
	private ObjectId mTimelineEventId;

	@RestApiFieldInfo( jsonField = JsonKeys.ConditioningEventId, description = "The id of the conditioning event the notification applies to, if applicable" )
	private ObjectId mConditioningEventId;

	@RestApiFieldInfo( jsonField = JsonKeys.FeatureId, description = "The id of the feature the notification applies to, if applicable" )
	private String mFeatureId;

	/**
	 * Constructor used by morphia
	 */
	@SuppressWarnings( "unused" )
	private Notification()
	{
		return;
	}

	public Notification( final Scope scope, final String key, final String description ) throws InvalidDataException
	{
		this( scope, key, description, null, null, null, null, State.New );

		return;
	}

	public Notification(
		final Scope scope,
		final String key,
		final String description,
		final ObjectId viewId,
		final ObjectId timelineEventId,
		final ObjectId conditioningEventId,
		final String featureId ) throws InvalidDataException
	{
		this( scope, key, description, viewId, timelineEventId, conditioningEventId, featureId, State.New );

		return;
	}

	public Notification(
		final Scope scope,
		final String key,
		final String description,
		final ObjectId viewId,
		final ObjectId timelineEventId,
		final ObjectId conditioningEventId,
		final String featureId,
		final State state ) throws InvalidDataException
	{
		if( scope == null )
		{
			throw new InvalidDataException( "Notification scope cannot be null!" );
		}

		if( key == null || key.isEmpty() )
		{
			throw new InvalidDataException( "Notification key cannot be null or empty!" );
		}

		if( description == null || description.isEmpty() )
		{
			throw new InvalidDataException( "Notification description cannot be null or empty!" );
		}

		if( state == null )
		{
			throw new InvalidDataException( "Notification state cannot be null!" );
		}

		mScope = scope;
		mKey = key;
		mDescription = description;
		mState = state;

		mViewId = viewId;
		mTimelineEventId = timelineEventId;
		mConditioningEventId = conditioningEventId;
		mFeatureId = featureId;

		return;
	}

	public Scope getScope()
	{
		return mScope;
	}

	public String getKey()
	{
		return mKey;
	}

	public String getDescription()
	{
		return mDescription;
	}

	public State getState()
	{
		return mState;
	}

	@Override
	public JSONObject toJSON() throws InvalidDataException
	{
		final JSONObject json = new JSONObject();

		JsonHelper.put( json, JsonKeys.Key, mKey );
		JsonHelper.put( json, JsonKeys.Description, mDescription );
		JsonHelper.put( json, JsonKeys.ViewId, mViewId );
		JsonHelper.put( json, JsonKeys.TimelineEventId, mTimelineEventId );
		JsonHelper.put( json, JsonKeys.ConditioningEventId, mConditioningEventId );
		JsonHelper.put( json, JsonKeys.FeatureId, mFeatureId );

		switch( mScope )
		{
			case Project:
				JsonHelper.put( json, JsonKeys.Scope, JSON_VALUE_SCOPE_PROJECT );
				break;

			case View:
				JsonHelper.put( json, JsonKeys.Scope, JSON_VALUE_SCOPE_VIEW );
				break;

			case Feature:
				JsonHelper.put( json, JsonKeys.Scope, JSON_VALUE_SCOPE_FEATURE );
				break;

			case TimelineEvent:
				JsonHelper.put( json, JsonKeys.Scope, JSON_VALUE_SCOPE_TIMELINE_EVENT );
				break;

			case ConditioningEvent:
				JsonHelper.put( json, JsonKeys.Scope, JSON_VALUE_SCOPE_CONDITIONING_EVENT );
				break;

			default:
				throw new InvalidDataException( String.format( "Unknown notification scope: %s", mScope ) );
		}

		switch( mState )
		{
			case New:
				JsonHelper.put( json, JsonKeys.State, JSON_VALUE_STATE_NEW );
				break;

			case Acknowledged:
				JsonHelper.put( json, JsonKeys.State, JSON_VALUE_STATE_ACKNOWLEDGED );
				break;

			default:
				throw new InvalidDataException( String.format( "Unknown notification state: %s", mState ) );
		}

		return json;
	}

	private int getScopeIntValue( final Scope scope )
	{
		switch( scope )
		{
			case Project:
				return 0;

			case View:
				return 1;

			case Feature:
				return 2;

			case TimelineEvent:
				return 3;

			case ConditioningEvent:
				return 4;

			default:
				return 99;
		}
	}

	@Override
	public int compareTo( final Notification other )
	{
		if( mScope != other.mScope )
		{
			return Integer.compare( getScopeIntValue( mScope ), getScopeIntValue( other.mScope ) );
		}
		else
		{
			return mKey.compareTo( other.mKey );
		}
	}

	@Override
	public boolean equals( final Object other )
	{
		if( !( other instanceof Notification ) )
		{
			return false;
		}
		if( other == this )
		{
			return true;
		}

		final Notification otherNotification = (Notification)other;
		return new EqualsBuilder()
			.append( mKey, otherNotification.mKey )
			.append( mViewId, otherNotification.mViewId )
			.append( mTimelineEventId, otherNotification.mTimelineEventId )
			.append( mConditioningEventId, otherNotification.mConditioningEventId )
			.append( mFeatureId, otherNotification.mFeatureId )
			.isEquals();
	}

	@Override
	public int hashCode()
	{
		return new HashCodeBuilder( 17, 31 )
			.append( mKey )
			.append( mViewId )
			.append( mTimelineEventId )
			.append( mConditioningEventId )
			.append( mFeatureId )
			.toHashCode();
	}
}
