package com.imaginationsupport;

import com.imaginationsupport.backend.Job;
import com.imaginationsupport.backend.JobId;
import com.imaginationsupport.backend.JobManager;
import com.imaginationsupport.backend.jobs.*;
import com.imaginationsupport.data.*;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.exceptions.JobException;
import com.imaginationsupport.plugins.Projector;
import com.imaginationsupport.views.FBView;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.views.SQView;
import com.imaginationsupport.views.View;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class ProjectManager
{
	/*
	 * Singleton
	 * Project Manager is a singleton class
	 */
	private static ProjectManager instance = null;

	protected ProjectManager()
	{
	}

	public static ProjectManager getInstance()
	{
		if( instance == null )
		{
			instance = new ProjectManager();
		}
		return instance;
	}

	private Database db = Database.getInstance();
	private JobManager jm = JobManager.getInstance();
	private PlugInManager pluginManager = PlugInManager.getInstance();

	protected static final Logger LOGGER = ImaginationSupportUtil.getBackendLogger();

	public static final String MASTER_VIEW_NAME = "_MASTER";
	public static final String MASTER_VIEW_DESC = "Not for User Viewing";

	//
	// Projects
	//

	/**
	 * Create a new Project.
	 *
	 * @param name        Unique name by which this Project will be identified.
	 * @param description Text description of the purpose behind the Project.
	 * @param start       The date when the Project windows starts.
	 * @param end         The data when the Project window ends.
	 * @param owner       The User who created and owns the Project.
	 *
	 * @return Project The resulting Project object that is created.
	 */
	public Project createProject(
		String name,
		String description,
		LocalDateTime start,
		LocalDateTime end,
		int daysIncrement,
		User owner ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		if( name == null || name.isEmpty() )
		{
			throw new InvalidDataException( "Project name cannot be null or empty!" );
		}
		if( description == null )
		{
			throw new InvalidDataException( "Project description cannot be null!" );
		}
		if( start == null )
		{
			throw new InvalidDataException( "Project start date cannot be null!" );
		}
		if( end == null )
		{
			throw new InvalidDataException( "Project end date cannot be null!" );
		}
		if( start.isAfter( end ) || start.equals( end ) )
		{
			throw new InvalidDataException( "Project start date must be before the end date!" );
		}
		if( owner == null )
		{
			throw new InvalidDataException( "Project owner cannot be null!" );
		}
		if( exists( name ) )
		{
			throw new GeneralScenarioExplorerException( "Another project with this name already exists" );
		}

		Project project = new Project( name, description, owner.getId(), start, end, daysIncrement );
		project.save();

		owner.addAccess( project.getId() );
		owner.save();

		return project;
	}

	public void initProject( ObjectId projectId ) throws InvalidDataException
	{
		if( projectId == null )
		{
			throw new InvalidDataException( "ProjectId cannot be null!" );
		}

		final Job job = new CreateProject( projectId );

		try
		{
			final JobId id = jm.submit( job );

			jm.waitFor( id );
		}
		catch( final JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR Creating Project (" + projectId + "): " + e );
		}

		return;
	}

	/**
	 * Updates the entire Master tree and all subordinate views.
	 * THIS WILL TAKE A LONG TIME.
	 *
	 * @param projectId
	 */
	public void rebuildAll( ObjectId projectId ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( projectId == null )
		{
			throw new InvalidDataException( "Null not premitted" );
		}

		// Remove all current states for project
		Query< State > q = db.datastore.createQuery( State.class );
		q.criteria( State.PROJECT_ID_FIELD ).equal( projectId );
		Database.getInstance().datastore.delete( q );

		// Recreate States given existing CEs
		initProject( projectId );

		// Update all views in Project
		for( View v : getViews( projectId ) )
		{
			Job job = new UpdateFBViewJob( projectId, v.getId() );
			try
			{
				JobId id = jm.submit( job );
				jm.waitFor( id );
			}
			catch( JobException | InterruptedException e )
			{
				LOGGER.error( "ERROR Updating View (" + v.getId() + "): " + e );
				e.printStackTrace();
			}
		}

		NotificationsManager.updateProjectNotifications( projectId );

		return;
	}

	/**
	 * Remove Project
	 *
	 * @param projectId
	 */
	public void removeProject( ObjectId projectId ) throws InvalidDataException
	{
		if( projectId == null )
		{
			throw new InvalidDataException( "ERROR: cannot remove a null Project Id." );
		}

		for( User u : UserManager.getInstance().getUsers() )
		{
			u.removeAccess( projectId );
			u.save();
		}

		Project p = Database.get( Project.class, projectId );
		Database.delete( Project.class, projectId );

		// Deleting the Master View will cascade to all Views in Project
		removeView( projectId, p.getMasterView() );

		// Removing a Timeline event can have repercussions in Conditioning Events
		for( TimelineEvent t : getTimelineEvents( projectId ) )
		{
			removeTimelineEvent( projectId, t );
		}

		// Delete all states belonging to this project
		Query< State > q = db.datastore.createQuery( State.class );
		q.criteria( State.PROJECT_ID_FIELD ).equal( projectId );
		Database.getInstance().datastore.delete( q );
	}

	/**
	 * Checks if the Project exists or the name is already used
	 *
	 * @param name
	 *
	 * @return true if Project label already exists
	 */
	public boolean exists( String name ) throws GeneralScenarioExplorerException
	{
		if( name == null )
		{
			throw new GeneralScenarioExplorerException( "ERROR: exists passed a null Project name." );
		}
		Query< Project > q = db.datastore.createQuery( Project.class );
		q.criteria( Project.JsonKeys.Name ).equal( name );
		Project m = q.get(); // TODO: should be able to check this without loading it up or cache it..
		if( m == null )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Checks if the Project exists by its database id
	 *
	 * @param projectId
	 *
	 * @return true if Project label already exists
	 */
	public boolean exists( ObjectId projectId ) throws GeneralScenarioExplorerException
	{
		if( projectId == null )
		{
			throw new GeneralScenarioExplorerException( "ERROR: exists passed a null Project id." );
		}
		Project p = null;
		p = Database.get( Project.class, projectId );
		if( p == null )
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Get Project by name
	 *
	 * @param name
	 *
	 * @return Project or null if does not exist
	 */
	public Project getProject( String name ) throws InvalidDataException
	{
		if( name == null )
		{
			throw new InvalidDataException( "ERROR: exists passed a null Project name." );
		}

		return db.datastore.find( Project.class ).field( Project.JsonKeys.Name ).equal( name ).get();
	}

	/**
	 * Get a Project by the DB id
	 *
	 * @param id
	 *
	 * @return Project or null if does not exist
	 */
	public Project getProject( ObjectId id ) throws InvalidDataException
	{
		if( id == null )
		{
			throw new InvalidDataException( "ERROR: get Project by id passed a null id." );
		}

		return db.datastore.get( Project.class, id );
	}

	/**
	 * Get all Projects
	 *
	 * @return Set of all Projects
	 */
	public SortedSet< Project > getProjects()
	{
		return new TreeSet<>( db.datastore.createQuery( Project.class ).asList() );
	}

	/**
	 * Get all Projects for User
	 *
	 * @return Set of all Projects the User has access to
	 */
	public Set< Project > getProjects( User user ) throws InvalidDataException
	{
		if( user == null )
		{
			throw new InvalidDataException( "ERROR: getProjects passed a null user." );
		}

		Set< Project > out = new TreeSet<>();
		for( ObjectId id : user.getAccess() )
		{
			Project m = getProject( id );
			if( m != null )
			{
				out.add( m );
			}
		}
		return out;
	}

	//
	// FEATURES
	//

	public List< FeatureMap > getFeatures( ObjectId projectId ) throws GeneralScenarioExplorerException
	{
		if( projectId == null )
		{
			throw new GeneralScenarioExplorerException( "null parameter not permitted." );
		}
		Project p = Database.get( Project.class, projectId );
		List< FeatureMap > out = new ArrayList< FeatureMap >();
		out.addAll( p.getFeatureMaps() );
		return out;
	}

	public FeatureMap getFeature( ObjectId projectId, String featureUid ) throws InvalidDataException
	{
		if( projectId == null || featureUid == null || featureUid.isEmpty() )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}
		Project p = Database.get( Project.class, projectId );
		return p.getFeatureMap( featureUid );
	}

	public FeatureMap mapFeature(
		ObjectId projectId,
		String featureTypeId,
		String featureTypeConfig,
		String label,
		String description,
		String projectorId,
		String projectorConfig ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( projectId == null || featureTypeId == null || featureTypeId.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "Null project or featuretype not allowed." );
		}
		if( label == null || label.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "Features must have a name." );
		}

		Project p = getProject( projectId );
		FeatureMap fm = null;

		if( p.getFeatures().contains( label ) )
		{
			throw new GeneralScenarioExplorerException( "All features must have a unique name within the project." );
		}

		Class< ? extends Projector > projector = null;
		if( projectorId != null && !projectorId.isEmpty() )
		{
			projector = pluginManager.getProjector( projectorId ).getClass();
		}
		try
		{
			fm = p.mapFeature( pluginManager.getFeatureType( featureTypeId ), featureTypeConfig, label, description, projector, projectorConfig );
		}
		catch( final ClassNotFoundException | InstantiationException | IllegalAccessException e )
		{
			throw new GeneralScenarioExplorerException( "Failed to map projector (" + projectorId + "): " + e );
		}
		if( fm == null )
		{
			return null;
		}
		p.save();

		Job job = new PropagateFeatures( projectId, fm.getUid() );
		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id );
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR Mapping Feature (" + label + "): " + e );
			e.printStackTrace();
		}
		return fm;
	}

	public FeatureMap mapFeature( ObjectId projectId, FeatureMap map ) throws InvalidDataException
	{
		if( projectId == null || map == null )
		{
			throw new InvalidDataException( "Null project or featuretype not allowed." );
		}
		Project p = getProject( projectId );
		p.addFeatureMap( map );
		p.save();

		Job job = new PropagateFeatures( p, map );
		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id );
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR Mapping Feature Set to Project (" + projectId + "): " + e );
			e.printStackTrace();
		}
		return map;
	}

	public void mapFeatures( ObjectId projectId, Set< FeatureMap > maps ) throws InvalidDataException
	{
		if( projectId == null || maps == null || maps.isEmpty() )
		{
			throw new InvalidDataException( "Null project or featuretype not allowed." );
		}
		Project p = getProject( projectId );

		List< String > mapUids = new ArrayList< String >();
		for( FeatureMap map : maps )
		{
			p.addFeatureMap( map );
			mapUids.add( map.getUid() );
		}
		p.save();

		Job job = new PropagateFeatures( projectId, mapUids );
		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id ); // This could wait in the background
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR Mapping Feature Set to Project (" + projectId + "): " + e );
			e.printStackTrace();
		}
	}

	public FeatureMap updateFeatureText( ObjectId projectId, String featureMapId, String label, String description ) throws InvalidDataException
	{
		if( projectId == null || featureMapId == null || featureMapId.isEmpty() )
		{
			throw new InvalidDataException( "Null project or featureMapId not allowed." );
		}
		if( label == null || label.isEmpty() )
		{
			throw new InvalidDataException( "Features must have a name." );
		}

		Project p = getProject( projectId );
		FeatureMap fm = p.getFeatureMap( featureMapId );
		p.removeFeatureMap( fm );
		if( p.getFeatures().contains( label ) )
		{
			throw new InvalidDataException( "All features must have a unique name within the project." );
		}
		fm.setLabel( label );
		fm.setDescription( description );
		p.addFeatureMap( fm );
		p.save();
		return fm;
	}

	public FeatureMap updateFeature( ObjectId projectId, FeatureMap feature ) throws InvalidDataException
	{
		if( projectId == null || feature == null )
		{
			throw new InvalidDataException( "Null project or featureMap not allowed." );
		}
		if( feature.getType() == null )
		{
			throw new InvalidDataException( "Null FeatureType Id not allowed." );
		}

//		Project p=getProject(projectId);
//		FeatureMap previous=p.getFeatureMap(feature.getUid());
//
//		// Create a new FeatureMap and copy the text fields from it
//		FeatureMap fm=null;
//
//		Class<? extends Projector> projector=null;
//		if (feature.getProjector()!=null)
//			projector=pluginManager.getProjector(feature.getProjectorName()).getClass();
//		FeatureType featureType=pluginManager.getFeatureType(feature.getUid());
//
//		try {
//			fm=new FeatureMap(featureType, previous.getLabel(), previous.getDescription(), projector );
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e1) {
//			ACTIVITY_LOGGER.error("ERROR Mapping feature in Project ("+projectId+"): "+e1);
//			e1.printStackTrace();
//		}
////		fm.setFeatureTypeConfig(feature.getFeatureTypeConfig());
////		if(projector!=null) fm.setProjectorConfig(feature.getProjectorConfig());
//
//		p.addFeatureMap(fm);
//		p.save();
//
//		// Remove the previous feature from the Project
//		removeFeature(projectId,previous.getUid());
//
//		// Update the states with the new Feature
//		Job job=new PropagateFeatures(projectId,fm.getUid());
//		try {
//			JobId id = jm.submit(job);
//			jm.waitFor(id); // This could wait in the background
//		} catch (JobException |InterruptedException e) {
//			ACTIVITY_LOGGER.error("ERROR Updating feature ("+feature.getUid()+" to "+fm.getUid()+") in Project ("+projectId+"): "+e);
//			e.printStackTrace();
//		}

		final Project project = getProject( projectId );

		project.removeFeatureMap( feature );
		project.addFeatureMap( feature );
		project.save();

		return getFeature( projectId, feature.getUid() );
	}

	public void removeFeature( ObjectId projectId, String featureMapId ) throws InvalidDataException
	{
		if( projectId == null || featureMapId == null || featureMapId.isEmpty() )
		{
			throw new InvalidDataException( "Null project or featureMapId not allowed." );
		}
		Job job = new RemoveFeature( projectId, featureMapId );
		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id ); // This could wait in the background
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "Error removing Feature (" + featureMapId + ") from Project (" + projectId + "): " + e );
			e.printStackTrace();
		}
	}

	//
	// TIMELINE EVENTS
	//

	/**
	 * Adds a timeline event to a Project
	 *
	 * @param projectId
	 * @param timelineEvent
	 */
	public TimelineEvent addTimelineEvent( ObjectId projectId, TimelineEvent timelineEvent ) throws InvalidDataException
	{
		if( projectId == null || timelineEvent == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}

		timelineEvent.setProject( projectId );
		Database.save( timelineEvent );
		return timelineEvent;
	}

	/**
	 * Adds a set of timelines events to a Project
	 *
	 * @param projectId
	 * @param events
	 */
	public void addTimelineEvents( ObjectId projectId, List< TimelineEvent > events ) throws InvalidDataException
	{
		if( projectId == null || events == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}

		for( TimelineEvent e : events )
		{
			addTimelineEvent( projectId, e );
		}
	}

	/**
	 * Get all timeline events for a Project
	 *
	 * @param projectId
	 */
	public List< TimelineEvent > getTimelineEvents( ObjectId projectId ) throws InvalidDataException
	{
		if( projectId == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}

		Query< TimelineEvent > q = db.datastore.createQuery( TimelineEvent.class );
		q.criteria( "project" ).equal( projectId );
		return q.asList();
	}

	/**
	 * Get a timeline events from its id
	 *
	 * @param timelineEventId
	 *
	 * @return the timeline event
	 */
	public TimelineEvent getTimelineEvent( ObjectId timelineEventId ) throws InvalidDataException
	{
		if( timelineEventId == null )
		{
			throw new InvalidDataException( "ERROR: get timeline event by id passed a null id." );
		}

		return db.datastore.get( TimelineEvent.class, timelineEventId );
	}

	/**
	 * Updates a timeline event to a Project
	 *
	 * @param projectId
	 * @param timelineEvent
	 */
	public TimelineEvent updateTimelineEvent( ObjectId projectId, TimelineEvent timelineEvent ) throws InvalidDataException
	{
		if( projectId == null || timelineEvent == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}

		// use previous tle to find CEs that depend on it
		ObjectId previousId = timelineEvent.getId();

		// update the timeline event
		timelineEvent.setId( null );
		timelineEvent.setProject( projectId );
		Database.save( timelineEvent );

		if( previousId != null )
		{
			// Remove old timeline event with replacement of id in preconditions
			Job job = new RemoveTimelineEvent( projectId, previousId, timelineEvent.getId() );
			try
			{
				JobId id = jm.submit( job );
				jm.waitFor( id );
			}
			catch( JobException | InterruptedException e )
			{
				LOGGER.error( "ERROR Updating and Replacing TimelineEvent: " + e );
				e.printStackTrace();
			}
		}

		return timelineEvent;
	}

	/**
	 * Delete a timeline event
	 *
	 * @param event the event to delete
	 */
	public void removeTimelineEvent( ObjectId projectId, TimelineEvent event ) throws InvalidDataException
	{
		if( projectId == null || event == null )
		{
			throw new InvalidDataException( "ERROR: cannot remove a null event." );
		}
		removeTimelineEvent( projectId, event.getId() );
	}

	/**
	 * Delete a timeline event
	 *
	 * @param projectId
	 * @param eventId   the event to delete
	 */
	public void removeTimelineEvent( ObjectId projectId, ObjectId eventId ) throws InvalidDataException
	{
		if( projectId == null || eventId == null )
		{
			throw new InvalidDataException( "Error: null ids are not allowed." );
		}
		Job job = new RemoveTimelineEvent( projectId, eventId );
		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id );
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR Updating View: " + e );
			e.printStackTrace();
		}
	}

	//
	// VIEWS
	//

	/**
	 * Gets a list of Views for a specific Project.
	 *
	 * @param projectId The DB id of the Project to search.
	 *
	 * @return List of Views within a Project
	 * @throws InvalidDataException Checks for valid Project.
	 */
	public List< View > getViews( ObjectId projectId ) throws InvalidDataException
	{
		if( projectId == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}

		Query< View > q = db.datastore.createQuery( View.class );
		q.criteria( "project" ).equal( projectId );
		return q.asList();
	}

	/**
	 * Gets a specific View based on the Project id and View id.
	 *
	 * @param projectId The DB id of the Project to search.
	 * @param viewId    The View id to locate.
	 *
	 * @return A View.
	 * @throws InvalidDataException Checks for valid Project and View.
	 */
	public View getView( ObjectId projectId, ObjectId viewId ) throws InvalidDataException
	{
		if( projectId == null || viewId == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}
		View v = Database.get( View.class, viewId );
		if( v == null )
		{
			throw new InvalidDataException( "No such View (" + viewId + ") in Project (" + projectId + ")." );
		}
		return v;
	}

	public View addView( ObjectId projectId, View view ) throws InvalidDataException
	{
		if( projectId == null || view == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}
		view.save();

		Job job = null;
		if( view instanceof FBView )
		{
			job = new UpdateFBViewJob( projectId, view.getId() );
		}
		else if( view instanceof SQView )
		{
			job = new UpdateSQViewJob( projectId, view.getId() );
		}
		else
		{
			LOGGER.error( "ERROR: Cannot identify view type for " + view.getId().toHexString() + "." );
		}

		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id );
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR Updating View (" + view.getId() + "): " + e );
			e.printStackTrace();
		}
		return getView( projectId, view.getId() );
	}

	public View updateView( ObjectId projectId, View updated ) throws InvalidDataException
	{
		if( projectId == null || updated == null )
		{
			throw new InvalidDataException( "Null project or featureMap not allowed." );
		}
		if( updated.getId() == null )
		{
			throw new InvalidDataException( "View has no DB id, cannot update." );
		}

		// This assumes that the JSON is storing all of the critical information.
		updated.save();

		Job job = null;
		if( updated instanceof FBView )
		{
			job = new UpdateFBViewJob( projectId, updated.getId() );
		}
		else if( updated instanceof SQView )
		{
			job = new UpdateSQViewJob( projectId, updated.getId() );
		}
		else
		{
			LOGGER.error( "ERROR: Cannot identify view type for " + updated.getId().toHexString() + "." );
		}

		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id );
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR Updating View (" + updated.getId() + "): " + e );
		}
		return getView( projectId, updated.getId() );
	}

	public void removeView( ObjectId projectId, View view ) throws InvalidDataException
	{
		removeView( projectId, view.getId() );
	}

	public void removeView( ObjectId projectId, ObjectId viewId ) throws InvalidDataException
	{
		if( projectId == null || viewId == null )
		{
			throw new InvalidDataException( "Null project or view id not allowed!" );
		}

		View view = Database.get( View.class, viewId );

		if( view instanceof MasterView )
		{
			// Remove all views and their conditioning events
			for( View v : getViews( projectId ) )
			{
				Database.delete( View.class, v.getId() );
			}
			for( ConditioningEvent ce : getConditioningEventsForProjectId( projectId ) )
			{
				Database.delete( ConditioningEvent.class, ce.getId() );
			}
		}
		else
		{
			for( ConditioningEvent ce : getOriginConditioningEventsForViewId( viewId ) )
			{
				// only ces originating in this view
				removeConditioningEvent( projectId, viewId, ce.getId() );
			}
			Database.delete( View.class, viewId );
		}
	}

	//
	// CONDITIONING EVENTS
	//

	/**
	 * Sets all of the Conditioning Events that are part of a Project
	 *
	 * @param project
	 *
	 * @return conditioning events (sorted by label)
	 */
	public Set< ConditioningEvent > getConditioningEvents( Project project ) throws InvalidDataException
	{
		if( project == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}
		return getConditioningEventsForProjectId( project.getId() );
	}

	/**
	 * Gets all of the Conditioning Events that are part of a Project by the id
	 *
	 * @param projectId id of the Project
	 *
	 * @return conditioning events (sorted by label)
	 */
	public Set< ConditioningEvent > getConditioningEventsForProjectId( ObjectId projectId ) throws InvalidDataException
	{
		if( projectId == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}

		final TreeSet< ConditioningEvent > ret = new TreeSet<>();
		Query< ConditioningEvent > q = db.datastore.createQuery( ConditioningEvent.class );
		q.criteria( "project" ).equal( projectId );
		ret.addAll( q.asList() );
		return ret;
	}

	/**
	 * Gets all of the Conditioning Events that ORIGINATE from a view.
	 * For conditioning events assigned to a view see the View object.
	 *
	 * @param view
	 *
	 * @return conditioning events (sorted by label)
	 */
	public Set< ConditioningEvent > getOriginConditioningEvents( View view ) throws InvalidDataException
	{
		if( view == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}
		return getOriginConditioningEventsForViewId( view.getId() );
	}

	/**
	 * Gets all of the Conditioning Events that ORIGINATE from a view by the id.
	 * For conditioning events assigned to a view see the View object.
	 *
	 * @param viewId id of the view
	 *
	 * @return conditioning events (sorted by label)
	 */
	public SortedSet< ConditioningEvent > getOriginConditioningEventsForViewId( ObjectId viewId ) throws InvalidDataException
	{
		if( viewId == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}
		TreeSet< ConditioningEvent > ret = new TreeSet<>();
		Query< ConditioningEvent > q = db.datastore.createQuery( ConditioningEvent.class );
		q.criteria( ConditioningEvent.DATABASE_VIEW_KEY ).equal( viewId );
		ret.addAll( q.asList() );
		return ret;
	}

	public SortedSet< ConditioningEvent > getAssignedConditioningEventsforViewId( ObjectId viewId ) throws InvalidDataException
	{
		if( viewId == null )
		{
			throw new InvalidDataException( "null parameter not permitted." );
		}
		View v = Database.get( View.class, viewId );
		if( v == null )
		{
			throw new InvalidDataException( "No View for the DB id provided." );
		}
		TreeSet< ConditioningEvent > ret = new TreeSet<>();
		for( ObjectId vId : v.getAssignedConditioningEventIds() )
		{
			ConditioningEvent ce = Database.get( ConditioningEvent.class, vId );
			if( ce != null )
			{
				ret.add( ce );
			}
		}
		return ret;
	}

	/**
	 * Once the ConditioningEvent is setup kicking off this job updates the views.
	 *
	 * @param projectId the Project to put this to
	 * @param viewId    the view to which this CE is anchored
	 * @param event     the CE to put
	 */
	public ConditioningEvent addConditioningEvent( ObjectId projectId, ObjectId viewId, ConditioningEvent event ) throws InvalidDataException
	{
		if( projectId == null || viewId == null || event == null )
		{
			throw new InvalidDataException( "Null not allowed in adddConditioningEvent." );
		}
		if( event.getOutcomes() == null || event.getOutcomes().isEmpty() )
		{
			throw new InvalidDataException( "A Conditioning Event must have at least one outcome specified." );
		}

		event.setOriginView( viewId );
		event.setProject( projectId );
		event.save();

		Job job = new AddConditioningEvent( projectId, viewId, event.getId() );
		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id );
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR Adding ConditioningEvent (" + event.getId() + "): " + e );
			e.printStackTrace();
		}
		return event;
	}

	/**
	 * Assigns an existing conditioning event to a Project.
	 *
	 * @param projectId of the Project to add this to
	 * @param viewId    of view the view to which this CE should be added
	 * @param eventId   of event the CE to add, assumed to already be added to Project.
	 */
	public void assignConditioningEvent( ObjectId projectId, ObjectId viewId, ObjectId eventId ) throws InvalidDataException
	{
		if( projectId == null || viewId == null || eventId == null )
		{
			throw new InvalidDataException( "Null ids not allowed in adddConditioningEvent." );
		}
		View view = Database.get( View.class, viewId );
		view.assignConditioningEvent( eventId );
		view.save();
		Job job = new UpdateFBViewJob( projectId, viewId );
		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id );
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR Updating View (" + viewId + "): " + e );
			e.printStackTrace();
		}
	}

	/**
	 * Removes a conditioning event from the view, it is the origin, it removes it from all views.
	 *
	 * @param projectId the DB id of the Project from which to remove this.
	 * @param viewId    the DB id of the view from which the CE will be removed
	 * @param eventId   the CE to remove
	 */
	public void removeConditioningEvent( ObjectId projectId, ObjectId viewId, ObjectId eventId ) throws InvalidDataException
	{
		if( projectId == null || viewId == null || eventId == null )
		{
			throw new InvalidDataException( "Null is not valid for removeConditioningEvent." );
		}

		Job job = new RemoveConditioningEvent( projectId, viewId, eventId );
		try
		{
			JobId id = jm.submit( job );
			jm.waitFor( id );
		}
		catch( JobException | InterruptedException e )
		{
			LOGGER.error( "ERROR removing ConditioningEvent (" + eventId + ") from View (" + viewId + "): " + e );
			e.printStackTrace();
		}
	}

	public void setHistoricDataFile( ObjectId projectId, String fullPath ) throws InvalidDataException
	{
		if( projectId == null || fullPath == null )
		{
			throw new InvalidDataException( "Null is not valid for setHistoricDataFile" );
		}
		Project p = getProject( projectId );
		if( p == null )
		{
			throw new InvalidDataException( "No such project for setHistoricDataFile" );
		}

		StateManager sm = StateManager.getInstance();

		List< HistoricState > previous = null;
		try
		{
			previous = sm.getHistoricStates( p );
			sm.importHistoricStates( p, fullPath, false );
		}
		catch( IOException e )
		{
			throw new InvalidDataException( "Unable to import historic data from file (" + fullPath + "): " + e );
		}

		// removing old states if we get here
		for( HistoricState h : previous )
		{
			Database.delete( HistoricState.class, h.getId() );
		}
	}

	public void getHistoricDataFile( ObjectId projectId, String fullpath ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( projectId == null )
		{
			throw new InvalidDataException( "Null is not valid for setHistoricDataFile" );
		}
		Project p = getProject( projectId );
		if( p == null )
		{
			throw new InvalidDataException( "No such project for setHistoricDataFile" );
		}

		StateManager.getInstance().exportHistoricStates( p, fullpath );
	}

	public List< State > getTimeSeries( ObjectId projectId, ObjectId stateId, boolean includeHistoricData )
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ProjectTemplate getProjectTemplate( final ObjectId id ) throws InvalidDataException
	{
		if( id == null )
		{
			throw new InvalidDataException( "Project Template Id cannot be null!" );
		}

		return db.datastore.get( ProjectTemplate.class, id );
	}

	public SortedSet< ProjectTemplate > getProjectTemplates()
	{
		return new TreeSet<>( db.datastore.createQuery( ProjectTemplate.class ).asList() );
	}

	public ProjectTemplate createProjectTemplateFromProject( final ObjectId projectId ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( projectId == null )
		{
			throw new InvalidDataException( "Source Project Id cannot be null!" );
		}

		final Project sourceProject = getProject( projectId );
		if( sourceProject == null )
		{
			throw new InvalidDataException( String.format( "Unknown source project: %s", projectId.toHexString() ) );
		}

		return createProjectTemplate(
			sourceProject.getName(),
			sourceProject.getDescription(),
			sourceProject.getStart(),
			sourceProject.getEnd(),
			sourceProject.getDaysIncrement(),
			sourceProject.getOwner(),
			sourceProject.getId(),
			sourceProject.getFeatureMaps(),
			getTimelineEvents( sourceProject.getId() ) );
	}

	public ProjectTemplate createProjectTemplate(
		final String name,
		final String description,
		final LocalDateTime start,
		final LocalDateTime end,
		final int daysIncrement,
		final User creator,
		final ObjectId sourceProjectId,
		final Collection< FeatureMap > features,
		final Collection< TimelineEvent > timelineEvents ) throws InvalidDataException
	{
		final ProjectTemplate projectTemplate = new ProjectTemplate( name, description, start, end, daysIncrement, creator.getId(), sourceProjectId, features, timelineEvents );

		projectTemplate.save();

		return projectTemplate;
	}

	public void removeProjectTemplate( final ObjectId projectTemplateId ) throws InvalidDataException
	{
		if( projectTemplateId == null )
		{
			throw new InvalidDataException( "Project template id cannot be null!" );
		}

		final ProjectTemplate projectTemplate = getProjectTemplate( projectTemplateId );
		if( projectTemplate == null )
		{
			throw new InvalidDataException( String.format( "Unknown project template: %s", projectTemplateId.toHexString() ) );
		}

		Database.delete( ProjectTemplate.class, projectTemplateId );

		return;
	}

	public Project createProjectFromTemplate( final ObjectId projectTemplateId, final User owner ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( projectTemplateId == null )
		{
			throw new InvalidDataException( "Project Template Id cannot be null!" );
		}

		final ProjectTemplate projectTemplate = getProjectTemplate( projectTemplateId );
		if( projectTemplate == null )
		{
			throw new InvalidDataException( String.format( "Unknown project template: %s", projectTemplateId.toHexString() ) );
		}

		final Project clonedProject = createProject(
			projectTemplate.getName(),
			projectTemplate.getDescription(),
			projectTemplate.getStart(),
			projectTemplate.getEnd(),
			projectTemplate.getDaysIncrement(),
			owner );

		initProject( clonedProject.getId() );

		// TODO finish!

		NotificationsManager.updateProjectNotifications( clonedProject.getId() );

		return getProject( clonedProject.getId() );
	}
}
