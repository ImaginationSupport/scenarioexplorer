package com.imaginationsupport.data;

import com.imaginationsupport.Database;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.UserManager;
import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.FeatureType;
import com.imaginationsupport.plugins.Projector;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Indexed;

import java.time.LocalDateTime;
import java.util.*;

@RestApiObjectInfo( definitionName = "Project", tagName = RestApiHandlerInfo.CategoryNames.Project, description = "Scenario Explorer Project" )
public class Project extends Persistent implements ApiObject, Comparable< Project >
{
	public static class JsonKeys extends Persistent.JsonKeys
	{
		public static final String Name = "name";
		public static final String Description = "description";
		public static final String CreatedOn = "createdOn";
		public static final String LastEditOn = "lastEditOn";
		public static final String Start = "start";
		public static final String End = "end";
		public static final String DaysIncrement = "increment";
		public static final String Owner = "owner";
		public static final String Notifications = "notifications";
	}

	/**
	 * Holds the available days increment values
	 */
	@SuppressWarnings( "unused" )
	public abstract static class DaysIncrementValues
	{
		public static final int Day = 1;
		public static final int Week = 7;
		public static final int Month = 30;
		public static final int Year = 365;
	}

	/**
	 * Holds the available notification keys
	 */
	public abstract static class Notifications
	{
		public static final String ADD_FEATURES = "add-features";
		public static final String ADD_TIMELINE_EVENTS = "add-timeline-events";
		public static final String ADD_USERS = "add-users";
		public static final String ADD_VIEWS = "add-views";
	}

	/**
	 * Holds the project name
	 */
	@Indexed
	@RestApiFieldInfo( description = "The name of the project" )
	private String name;

	/**
	 * Holds the project description
	 */
	@RestApiFieldInfo( description = "The description of the project" )
	private String description;

	/**
	 * Holds the date the project was created
	 */
	@RestApiFieldInfo( description = "The date and time the project was created", isRequired = false )
	private LocalDateTime createdOn;

	/**
	 * Holds the date the project was last edited
	 */
	@RestApiFieldInfo( description = "The date and time of the last edit", isRequired = false )
	private LocalDateTime lastEditOn;

	/**
	 * Holds the project start date
	 */
	@RestApiFieldInfo( description = "The starting date and time of the project" )
	private LocalDateTime start;

	/**
	 * Holds the project end date
	 */
	@RestApiFieldInfo( description = "The ending date and time of the project" )
	private LocalDateTime end;

	/**
	 * Holds the number of days to increment when updating the tree
	 */
	@RestApiFieldInfo( description = "The number of days to increment when looking to add new states and conditioning events" )
	private int daysIncrement = DaysIncrementValues.Month;

//	@RestApiFieldInfo
//	private String historicDataFile = null;

	@Indexed
	@RestApiFieldInfo( description = "The id of the owner of the project" )
	private ObjectId owner;

	@Embedded
	@RestApiFieldInfo( description = "The set of features" )
	private Hashtable< String, FeatureMap > map = new Hashtable<>();

	/**
	 * Holds the project notifications
	 */
	@Embedded
	@RestApiFieldInfo( description = "The set of notifications" )
	private Set< Notification > notifications = new HashSet<>();

	@Embedded
	@RestApiFieldInfo( description = "The date and time of the now state", isRequired = false )
	private ObjectId now;

	// Link to MASTER view for this mission - which contains all conditioning events
	@Embedded
	private ObjectId master;

	public Project()
	{
		super();
	}

	/**
	 * Constructor
	 *
	 * @param name          the project name
	 * @param description   the project description
	 * @param ownerUserId   the owner id
	 * @param start         the start date
	 * @param end           the end date
	 * @param daysIncrement the days increment
	 */
	public Project( String name, String description, ObjectId ownerUserId, LocalDateTime start, LocalDateTime end, int daysIncrement ) throws InvalidDataException
	{
		super();

		if( name == null || name.trim().isEmpty() )
		{
			throw new InvalidDataException( "Project name cannot be null or empty!" );
		}
		if( description == null )
		{
			throw new InvalidDataException( "Project description cannot be null!" );
		}
		if( ownerUserId == null )
		{
			throw new InvalidDataException( "Project owner id cannot be null!" );
		}
		if( start == null )
		{
			throw new InvalidDataException( "Project start date cannot be null!" );
		}
		if( end == null )
		{
			throw new InvalidDataException( "Project end date cannot be null!" );
		}
		if( end.isBefore( start ) || end.equals( start ) )
		{
			throw new InvalidDataException( "Project end date must be AFTER the start date!" );
		}
		if( daysIncrement <= 0 )
		{
			throw new InvalidDataException( "Invalid Days Increment!" );
		}

		this.name = name;
		this.description = description;
		this.owner = ownerUserId;
		this.start = start;
		this.end = end;
		this.daysIncrement = daysIncrement;
		this.createdOn = LocalDateTime.now();
		this.lastEditOn = LocalDateTime.now();
		this.notifications = new TreeSet<>();
		markModified();

		return;
	}

	public Project( final JSONObject source ) throws InvalidDataException
	{
		super( source );

		this.name = JsonHelper.getRequiredParameterString( source, JsonKeys.Name );
		this.description = JsonHelper.getRequiredParameterString( source, JsonKeys.Description );
		this.createdOn = JsonHelper.getRequiredParameterDateTime( source, JsonKeys.CreatedOn );
		this.lastEditOn = JsonHelper.getRequiredParameterDateTime( source, JsonKeys.LastEditOn );
		this.start = JsonHelper.getRequiredParameterDateTime( source, JsonKeys.Start );
		this.end = JsonHelper.getRequiredParameterDateTime( source, JsonKeys.End );
		this.daysIncrement = JsonHelper.getRequiredParameterInt( source, JsonKeys.DaysIncrement );
		this.owner = JsonHelper.getRequiredParameterObjectId( source,JsonKeys. Owner, true );

		// NOTE: Do not deserialize the features or the notifications

		this.notifications = new TreeSet<>();

		if( this.name == null || this.name.trim().isEmpty() )
		{
			throw new InvalidDataException( "Project name cannot be null or empty!" );
		}
		if( this.description == null )
		{
			throw new InvalidDataException( "Project description cannot be null!" );
		}
//		if( owner == null )
//		{
//			throw new InvalidDataException( "Project owner id cannot be null!" );
//		}
		if( this.start == null )
		{
			throw new InvalidDataException( "Project start date cannot be null!" );
		}
		if( this.end == null )
		{
			throw new InvalidDataException( "Project end date cannot be null!" );
		}
		if( this.end.isBefore( this.start ) || this.end.equals( this.start ) )
		{
			throw new InvalidDataException( "Project end date must be AFTER the start date!" );
		}
		if( this.daysIncrement <= 0 )
		{
			throw new InvalidDataException( "Invalid Days Increment!" );
		}

		return;
	}

	/**
	 * Copy constructor
	 *
	 * @param projectToCopy the project to copy
	 */
	public Project( final Project projectToCopy )
	{
		super();

		this.name = projectToCopy.name;
		this.description = projectToCopy.description;
		this.start = projectToCopy.start;
		this.end = projectToCopy.end;
		this.daysIncrement = projectToCopy.daysIncrement;
		this.owner = projectToCopy.owner;

		this.now = projectToCopy.now;
		this.map = new Hashtable<>( projectToCopy.map );
		this.master = projectToCopy.master;
		this.createdOn = LocalDateTime.now();
		this.lastEditOn = LocalDateTime.now();

		this.notifications = new TreeSet<>( projectToCopy.getNotifications() );

		markModified();

		return;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String newName ) throws InvalidDataException
	{
		if( newName == null || newName.trim().isEmpty() )
		{
			throw new InvalidDataException( "Project name cannot be null or empty!" );
		}

		this.name = newName;
		markModified();
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription( String newDescription ) throws InvalidDataException
	{
		if( newDescription == null )
		{
			throw new InvalidDataException( "Project description cannot be null!" );
		}

		this.description = newDescription;
		markModified();
	}

	public LocalDateTime getCreatedOn()
	{
		return createdOn;
	}

	public void setCreatedOn( LocalDateTime newCreatedOn ) throws InvalidDataException
	{
		if( newCreatedOn == null )
		{
			throw new InvalidDataException( "Created on date cannot be null!" );
		}

		this.createdOn = newCreatedOn;
		markModified();
	}

	public LocalDateTime getLastEditOn()
	{
		return lastEditOn;
	}

	public void setLastEditOn( LocalDateTime newLastEditOn ) throws InvalidDataException
	{
		if( newLastEditOn == null )
		{
			throw new InvalidDataException( "Last edit date cannot be null!" );
		}

		this.lastEditOn = newLastEditOn;
		markModified();
	}

	public ObjectId getOwnerId()
	{
		return owner;
	}

	public User getOwner() throws GeneralScenarioExplorerException
	{
		return UserManager.getInstance().getUser( owner );
	}

	public void setOwner( User newOwner ) throws InvalidDataException
	{
		if( newOwner == null )
		{
			throw new InvalidDataException( "Project owner id cannot be null!" );
		}

		this.owner = newOwner.getId();
		markModified();
	}

	public LocalDateTime getStart()
	{
		return start;
	}

	public void setStart( LocalDateTime newStart ) throws InvalidDataException
	{
		if( newStart == null )
		{
			throw new InvalidDataException( "Project start date cannot be null!" );
		}

		this.start = newStart;
		markModified();
	}

	public LocalDateTime getEnd()
	{
		return end;
	}

	public void setEnd( LocalDateTime newEnd ) throws InvalidDataException
	{
		if( newEnd == null )
		{
			throw new InvalidDataException( "Project end date cannot be null!" );
		}

		this.end = newEnd;
		markModified();
	}

	public void setDaysIncrement( int newDaysIncrement )
	{
		this.daysIncrement = newDaysIncrement;
		markModified();
	}

	public int getDaysIncrement()
	{
		return daysIncrement;
	}

	public FeatureMap mapFeature(
		FeatureType type,
		String featureTypeConfig,
		String featureName,
		String featureDescription,
		Class< ? extends Projector > projector,
		String projectorConfig ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvalidDataException, GeneralScenarioExplorerException
	{
		FeatureMap mapping = new FeatureMap( type, featureName, featureDescription, projector );
		if( featureTypeConfig != null && !featureTypeConfig.isEmpty() )
		{
			mapping.setFeatureTypeConfig( featureTypeConfig );
		}
		if( projectorConfig != null && !projectorConfig.isEmpty() )
		{
			mapping.setProjectorConfig( projectorConfig );
		}
		addFeatureMap( mapping );
		markModified();
		return mapping;
	}

	public void addFeatureMap( FeatureMap mapping ) throws InvalidDataException
	{
		if( map.containsKey( mapping.getUid() ) )
		{
			throw new InvalidDataException( "The feature '" + mapping.getUid() + ":" + mapping.getLabel() + "' is already used in this scope, please make labels unique." );
		}
		map.put( mapping.getUid(), mapping );
		markModified();
	}

	public void updateFeatureMap( FeatureMap mapping ) throws InvalidDataException
	{
		map.remove( mapping.getUid() );
		addFeatureMap( mapping );
		markModified();
	}

	public void removeFeatureMap( FeatureMap mapping )
	{
		map.remove( mapping.getUid() );
		markModified();
	}

	public Set< String > getFeatures()
	{
		return map.keySet();
	}

	public Collection< FeatureMap > getFeatureMaps()
	{
		return new TreeSet<>( map.values() );
	}

	public void setNow( State s )
	{
		now = s.getId();
		markModified();
	}

	public State getNow()
	{
		if( now == null )
		{
			return null;
		}

		return Database.get( State.class, now );
	}

	public void setMasterView( ObjectId newMaster )
	{
		this.master = newMaster;
		markModified();
	}

	public ObjectId getMasterView()
	{
		return master;
	}

	public FeatureMap getFeatureMap( final String featureMapId ) throws InvalidDataException
	{
		if( this.map.containsKey( featureMapId ) )
		{
			return this.map.get( featureMapId );
		}
		else
		{
			throw new InvalidDataException( String.format( "Unknown feature map id: %s", featureMapId ) );
		}
	}

//	public String getHistoricDataFile()
//	{
//		return historicDataFile;
//	}

//	public void setHistoricDataFile( String historicDataFile )
//	{
//		this.historicDataFile = historicDataFile;
//		markModified();
//	}

	public Set< Notification > getNotifications()
	{
		return this.notifications;
	}

	public void setNotifications( final SortedSet< Notification > newNotifications ) throws InvalidDataException
	{
		if( newNotifications == null )
		{
			throw new InvalidDataException( "Notifications cannot be null!" );
		}

		this.notifications = newNotifications;
		markModified();

		return;
	}

	@Override
	public JSONObject toJSON() throws InvalidDataException, GeneralScenarioExplorerException
	{
		final JSONObject json = super.getBaseJson();

		JsonHelper.put( json, JsonKeys.Name, this.name );
		JsonHelper.put( json, JsonKeys.Description, this.description );
		JsonHelper.put( json, JsonKeys.CreatedOn, ImaginationSupportUtil.formatDateTime( this.createdOn ) );
		JsonHelper.put( json, JsonKeys.LastEditOn, ImaginationSupportUtil.formatDateTime( this.lastEditOn ) );
		JsonHelper.put( json, JsonKeys.Start, ImaginationSupportUtil.formatDateTime( this.start ) );
		JsonHelper.put( json, JsonKeys.End, ImaginationSupportUtil.formatDateTime( this.end ) );
		JsonHelper.put( json, JsonKeys.DaysIncrement, this.daysIncrement );
		JsonHelper.put( json, JsonKeys.Owner, this.owner.toHexString() );
		JsonHelper.put( json, JsonKeys.Notifications, this.notifications );

		// NOTE: do not serialize the features here, the API exposes them as a separate call

		return json;
	}

	@Override
	public int compareTo( final Project other )
	{
		return this.name.equals( other.name )
			? this.getId().compareTo( other.getId() )
			: this.name.compareTo( other.name );
	}
}
