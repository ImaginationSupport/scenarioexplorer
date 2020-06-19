package com.imaginationsupport;

import com.imaginationsupport.backend.JobManager;
import com.imaginationsupport.data.*;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.exceptions.NotAuthorizedException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.Effect;
import com.imaginationsupport.plugins.FeatureType;
import com.imaginationsupport.plugins.Precondition;
import com.imaginationsupport.plugins.Projector;
import com.imaginationsupport.plugins.effects.ErrorEffect;
import com.imaginationsupport.plugins.effects.FeatureSetEffect;
import com.imaginationsupport.plugins.preconditions.FeaturePrecondition;
import com.imaginationsupport.plugins.preconditions.OnHold;
import com.imaginationsupport.plugins.preconditions.TimelineEventPrecondition;
import com.imaginationsupport.plugins.projectors.JavaScriptProjector;
import com.imaginationsupport.plugins.projectors.RandomProjector;
import com.imaginationsupport.views.View;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.io.Closeable;
import java.time.LocalDateTime;
import java.util.*;

public class API implements Closeable
{
	//	private final Database db = Database.getInstance();
	private final UserManager userManager = UserManager.getInstance();
	private final ProjectManager projectManager = ProjectManager.getInstance();

	private final JobManager jobManager = JobManager.getInstance();
	private final PlugInManager plugInManager = PlugInManager.getInstance();
	private final StateManager stateManager = StateManager.getInstance();

	public enum Authorization
	{
		ListUsers,
		GetUser,
		NewUser,
		UpdateUser,
		DeleteUser,

		ListProjects,
		GetProject,
		NewProject,
		UpdateProject,
		DeleteProject,
		ExportProject,
		ImportProject,

		ListProjectTemplates,
		GetProjectTemplate,
		NewProjectTemplate,
		UpdateProjectTemplate,
		DeleteProjectTemplate,

		ListViews,
		GetView,
		NewView,
		UpdateView,
		DeleteView,

		ListTimelineEvents,
		GetTimelineEvent,
		NewTimelineEvent,
		UpdateTimelineEvent,
		DeleteTimelineEvent,

		ListFeatures,
		GetFeature,
		NewFeature,
		UpdateFeature,
		DeleteFeature,

		ListConditioningEvents,
		GetConditioningEvent,
		NewConditioningEvent,
		UpdateConditioningEvent,
		DeleteConditioningEvent,
		AssignConditioningEvent,

		ListStates,
		GetState,
	}

	@Override
	public void close()
	{
		jobManager.close();

		return;
	}

	// ========================= Authorization =========================

//	public boolean verifyAuthorization(
//		final User userToTest,
//		final Authorization authorizationToTest,
//		final String id,
//		final boolean throwExceptionOnFailure ) throws NotAuthorizedException
//	{
//		final boolean authorized = true; // TODO finish!
//
//		if( throwExceptionOnFailure && !authorized )
//		{
//			throw new NotAuthorizedException( String.format(
//				"User %s does NOT have access to %s / %s",
//				userToTest.getUserName(),
//				authorizationToTest.toString(),
//				id ) );
//		}
//
//		return authorized;
//	}

	public boolean verifyAuthorization(
		final User userToTest,
		final Authorization authorizationToTest,
		final ObjectId id,
		final boolean throwExceptionOnFailure ) throws NotAuthorizedException, InvalidDataException
	{
		try
		{
			final boolean authorized;
			switch( authorizationToTest )
			{
				case UpdateProject:
					authorized = userToTest.getAccess().contains( id );
					break;

				case DeleteProject:
					authorized = userToTest.getAccess().contains( id )
						&& findProject( id, true ).getOwner().equals( userToTest );
					break;

				default:
					authorized = true; // TODO Matt - finish!
					break;
			}

			if( throwExceptionOnFailure && !authorized )
			{
				throw new NotAuthorizedException( String.format(
					"User %s does NOT have access to %s / %s",
					userToTest.getUserName(),
					authorizationToTest.toString(), id.toHexString() ) );
			}

			return authorized;
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new NotAuthorizedException( "Error checking authorization, defaulting to not-authorized!", e );
		}
	}

	public boolean verifyAuthorization(
		final User userToTest,
		final Authorization authorizationToTest,
		final ObjectId parent,
		final ObjectId child,
		final boolean throwExceptionOnFailure ) throws NotAuthorizedException
	{
		final boolean authorized = true; // TODO Matt - finish!

		if( throwExceptionOnFailure && !authorized )
		{
			throw new NotAuthorizedException( String.format(
				"User %s does NOT have access to %s / parent: %s / child: %s",
				userToTest.getUserName(),
				authorizationToTest.toString(),
				parent.toHexString(),
				child.toHexString() ) );
		}

		return authorized;
	}

	public boolean verifyAuthorization(
		final User userToTest,
		final Authorization authorizationToTest,
		final ObjectId parent,
		final String child,
		final boolean throwExceptionOnFailure ) throws NotAuthorizedException
	{
		final boolean authorized = true; // TODO Matt - finish!

		if( throwExceptionOnFailure && !authorized )
		{
			throw new NotAuthorizedException( String.format(
				"User %s does NOT have access to %s / parent: %s / child: %s",
				userToTest.getUserName(),
				authorizationToTest.toString(),
				parent.toHexString(),
				child ) );
		}

		return authorized;
	}

	public boolean verifyAuthorization(
		final User userToTest,
		final Authorization authorizationToTest,
		final ObjectId parentOuter,
		final ObjectId parentInner,
		final ObjectId child,
		final boolean throwExceptionOnFailure ) throws NotAuthorizedException
	{
		final boolean authorized = true; // TODO Matt - finish!

		if( throwExceptionOnFailure && !authorized )
		{
			throw new NotAuthorizedException( String.format(
				"User %s does NOT have access to %s / parent: %s/%s / child: %s",
				userToTest.getUserName(),
				authorizationToTest.toString(),
				parentOuter.toHexString(),
				parentInner.toHexString(),
				child.toHexString() ) );
		}

		return authorized;
	}

	public boolean verifyAuthorization(
		final User userToTest,
		final Authorization authorizationToTest,
		final boolean throwExceptionOnFailure ) throws NotAuthorizedException
	{
		final boolean authorized; // TODO Matt - finish!
		switch( authorizationToTest )
		{
			case NewUser:
			case DeleteUser:
				authorized = userToTest.isSiteAdmin();
				break;

//			case UpdateUser:
//				authorized = userToTest.isSiteAdmin(); // TODO can't do this, or users can't update their own preferences... should only check isSiteAdmin if changing a different user
//				break;

			default:
				authorized = true; // TODO Matt - finish!
				break;
		}

		if( throwExceptionOnFailure && !authorized )
		{
			throw new NotAuthorizedException( String.format( "User %s does NOT have access %s", userToTest.getUserName(), authorizationToTest.toString() ) );
		}

		return authorized;
	}

	// ========================= User =========================

	/**
	 * Get the list of all users in the system.
	 *
	 * @return Set of user objects ordered alphabetically by name.
	 */
	public SortedSet< User > getUsers()
	{
		return userManager.getUsers();
	}

	/**
	 * Gets a specific User by username.
	 *
	 * @param userName                A unique identifier for each User.
	 * @param throwExceptionIfMissing Flag if exception thrown if no such user.
	 *
	 * @return The User with the specified username or null (if flag not set).
	 */
	public User findUser( final String userName, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		if( userName == null )
		{
			throw new InvalidDataException( "Username cannot be null!" );
		}

		for( final User user : getUsers() )
		{
			if( user.getUserName().equals( userName ) )
			{
				return user;
			}
		}

		if( throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "Unknown user: %s", userName ) );
		}
		else
		{
			return null;
		}
	}

	/**
	 * Creates a new User from a User object.
	 *
	 * @param user The User object to save.
	 *
	 * @return The resulting User object.
	 */
	public User createUser( final User user ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		return userManager.addUser( user );
	}

	/**
	 * Creates a new User from primitive attributes.
	 *
	 * @param userName
	 * @param fullName
	 * @param isSiteAdmin
	 */
	public User createUser( String userName, String fullName, boolean isSiteAdmin ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		return userManager.createUser( userName, fullName, isSiteAdmin );
	}

	public User findOrCreateBasicUser( final String userName ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final User existingUser = findUser( userName, false );
		if( existingUser != null )
		{
			return existingUser;
		}
		else
		{
			return createUser( userName, userName, false );
		}
	}

	/**
	 * Updates an existing User object
	 *
	 * @param user
	 */
	public User updateUser( final User user ) throws GeneralScenarioExplorerException, InvalidDataException
	{

		if( user == null )
		{
			throw new GeneralScenarioExplorerException( "Null User Passed to UpdateUser" );
		}

		final User databaseUser = userManager.getUser( user.getUserName() );
		if( databaseUser == null )
		{
			throw new GeneralScenarioExplorerException( "User does not exist!" );
		}
		databaseUser.setFullName( user.getFullName() );
		databaseUser.setAccess( new HashSet<>( user.getAccess() ) );
		databaseUser.setSiteAdmin( user.isSiteAdmin() ); // TODO restrict this to only admin users!
		databaseUser.setPreferences( user.getPreferences() );
		databaseUser.save();

		return databaseUser;
	}

	/**
	 * Removes a User from the DB based on their userName.
	 *
	 * @param userName
	 */
	public void deleteUser( final String userName ) throws GeneralScenarioExplorerException
	{
		User user = userManager.getUser( userName );
		if( user == null )
		{
			throw new GeneralScenarioExplorerException( "No such userName (" + userName + ") to remove." );
		}

		userManager.removeUser( user );
	}

	/**
	 * Exposes a Project to a User.
	 *
	 * @param user
	 * @param projectId
	 */
	public void addAccess( User user, ObjectId projectId ) throws GeneralScenarioExplorerException
	{
		userManager.addAccess( user, projectId );
	}

	// ========================= Project =========================

	/**
	 * Gets a list of Projects in the system.
	 *
	 * @return set of Projects ordered alphabetically by name.
	 */
	public SortedSet< Project > getAllProjects()
	{
		return projectManager.getProjects();
	}

	/**
	 * Gets a list of Projects that a given user can access.
	 *
	 * @param user The User for whom the accessible Projects should be filtered.
	 *
	 * @return Set of Projects ordered alphabetically by name.
	 */
	public SortedSet< Project > getProjectsForUser( final User user ) throws InvalidDataException
	{
		if( user == null )
		{
			throw new InvalidDataException( "User cannot be null!" );
		}

		final SortedSet< Project > filtered = new TreeSet<>();

		for( final Project project : getAllProjects() )
		{
			if( user.getAccess().contains( project.getId() ) )
			{
				filtered.add( project );
			}
		}

		return filtered;
	}

	/**
	 * Finds and loads a Project from the database id.
	 *
	 * @param id                      The database id of an existing projectId.
	 * @param throwExceptionIfMissing If true an exception will be thrown if no such projectId is found (otherwise null is returned).
	 *
	 * @return The Project loaded from the database or null if no such projectId (and throwExceptionIfMissing is false).
	 */
	public Project findProject( final ObjectId id, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		final Project project = projectManager.getProject( id );
		if( project == null && throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "Unknown projectId: %s", id ) );
		}

		return project;
	}

	/**
	 * Creates a new Project given the basic elements in a Project object.
	 *
	 * @param project The Project object to base the new Project on.
	 *
	 * @return The newly created Project.
	 */
	public Project createProject( final Project project ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		if( project == null )
		{
			throw new GeneralScenarioExplorerException( "Project cannot be null!" );
		}
		if( project.getId() != null )
		{
			throw new GeneralScenarioExplorerException( "Project already saved to database." );
		}

		return createProject( project.getName(), project.getDescription(), project.getStart(), project.getEnd(), project.getDaysIncrement(), project.getOwner() );
	}

	public Project createProject(
		final String name,
		final String description,
		final LocalDateTime start,
		final LocalDateTime end,
		final int daysIncrement,
		final User owner ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		final Project project = projectManager.createProject( name, description, start, end, daysIncrement, owner );

		projectManager.initProject( project.getId() );

		NotificationsManager.updateProjectNotifications( project.getId() );

		return projectManager.getProject( project.getId() );
	}

	public Project cloneProject( final ObjectId sourceProjectId, final User owner ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		return ProjectBackup.cloneProject( this, sourceProjectId, owner );
	}

	public Project createProjectFromTemplate( final ObjectId sourceProjectTemplateId, final User owner ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		return projectManager.createProjectFromTemplate( sourceProjectTemplateId, owner );
	}

	/**
	 * Given a Project, identify and implement the changes since it was previously saved.
	 *
	 * @param updatedProject The Project with attributes we want to update.
	 *
	 * @return The updated Project (based on the original not the one passed in).
	 */
	public Project updateProject( final Project updatedProject ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( updatedProject == null )
		{
			throw new InvalidDataException( "Project cannot be null!" );
		}
		if( updatedProject.getId() == null )
		{
			throw new InvalidDataException( "Project does not have database id." );
		}

		ObjectId projectId = updatedProject.getId();
		Project originalProject = projectManager.getProject( projectId );
		if( originalProject == null )
		{
			throw new InvalidDataException( "Project Id could not be found in database." );
		}

		if( !originalProject.getName().equals( updatedProject.getName() )
			|| !originalProject.getDescription().equals( updatedProject.getDescription() ) )
		{
			updateProjectText( projectId, updatedProject.getName(), updatedProject.getDescription() );
		}

		if( !originalProject.getStart().equals( updatedProject.getStart() )
			|| !originalProject.getEnd().equals( updatedProject.getEnd() )
			|| originalProject.getDaysIncrement() != updatedProject.getDaysIncrement() )
		{
			updateProjectTimes( projectId, updatedProject.getStart(), updatedProject.getEnd(), updatedProject.getDaysIncrement() );
		}

		// get the final projectId from database
		return projectManager.getProject( projectId );
	}

	/**
	 * Updates only the textual content of a Project, no changes to structural attributes.
	 *
	 * @param projectId
	 * @param name
	 * @param description
	 */
	private Project updateProjectText( final ObjectId projectId, final String name, final String description ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		// Updating the text attributes of a projectId is purely a database task
		Project project = projectManager.getProject( projectId );
		project.setName( name );
		project.setDescription( description );
		project.setLastEditOn( LocalDateTime.now() );
		project.save();

		NotificationsManager.updateProjectNotifications( project.getId() );

		return projectManager.getProject( project.getId() );
	}

	/**
	 * Updates to the Structural aspects of a Project, this causes the Project to be
	 *
	 * @param projectId
	 * @param start
	 * @param end
	 * @param daysIncrement
	 */
	private Project updateProjectTimes(
		final ObjectId projectId,
		final LocalDateTime start,
		final LocalDateTime end,
		final int daysIncrement ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		Project project = projectManager.getProject( projectId );

		project.setStart( start );
		project.setEnd( end );
		project.setDaysIncrement( daysIncrement );
		project.setLastEditOn( LocalDateTime.now() );
		project.save();

		// Updating the times will require rebuilding of the master view and all children views with the new timescales
		projectManager.rebuildAll( projectId );

		return projectManager.getProject( project.getId() );
	}

	/**
	 * Permanently removes an entire Project and all subordinate objects.
	 *
	 * @param id
	 */
	public void deleteProject( final ObjectId id ) throws InvalidDataException
	{
		projectManager.removeProject( id );

		return;
	}

	// ========================= Project Template =========================

	/**
	 * Gets a list of projects templates in the system
	 *
	 * @return set of project templates
	 */
	public SortedSet< ProjectTemplate > getAllProjectTemplates()
	{
		return projectManager.getProjectTemplates();
	}

	/**
	 * Finds and loads a Project from the database id.
	 *
	 * @param id                      The database id of an existing projectId.
	 * @param throwExceptionIfMissing If true an exception will be thrown if no such projectId is found (otherwise null is returned).
	 *
	 * @return The Project loaded from the database or null if no such projectId (and throwExceptionIfMissing is false).
	 */
	public ProjectTemplate findProjectTemplate( final ObjectId id, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		final ProjectTemplate projectTemplate = projectManager.getProjectTemplate( id );
		if( projectTemplate == null && throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "Unknown project template: %s", id ) );
		}

		return projectTemplate;
	}

	public ProjectTemplate createProjectTemplate( final ProjectTemplate projectTemplate ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		if( projectTemplate == null )
		{
			throw new GeneralScenarioExplorerException( "Project cannot be null!" );
		}
		if( projectTemplate.getId() != null )
		{
			throw new GeneralScenarioExplorerException( "Project already saved to database." );
		}

		return createProjectTemplate(
			projectTemplate.getName(),
			projectTemplate.getDescription(),
			projectTemplate.getStart(),
			projectTemplate.getEnd(),
			projectTemplate.getDaysIncrement(),
			projectTemplate.getCreator(),
			projectTemplate.getSourceProjectId(),
			projectTemplate.getFeatures(),
			projectTemplate.getTimelineEvents() );
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
		return projectManager.createProjectTemplate( name, description, start, end, daysIncrement, creator, sourceProjectId, features, timelineEvents );
	}

	public ProjectTemplate updateProjectTemplate( final ProjectTemplate updatedProjectTemplate ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( updatedProjectTemplate == null )
		{
			throw new InvalidDataException( "Project template cannot be null!" );
		}
		if( updatedProjectTemplate.getId() == null )
		{
			throw new InvalidDataException( "Project template does not have database id" );
		}

		final ObjectId projectTemplateId = updatedProjectTemplate.getId();
		final ProjectTemplate originalProjectTemplate = projectManager.getProjectTemplate( projectTemplateId );
		if( originalProjectTemplate == null )
		{
			throw new InvalidDataException( "Project Template Id could not be found in database" );
		}

		updatedProjectTemplate.save();

		// get the final projectId from database
		return projectManager.getProjectTemplate( projectTemplateId );
	}

	public void deleteProjectTemplate( final ObjectId projectTemplateId ) throws InvalidDataException
	{
		projectManager.removeProjectTemplate( projectTemplateId );

		return;
	}

	// ========================= Historic Data ======================

	/**
	 * Imports a CSV as Historic Data
	 *
	 * @param projectId Project to add Data to
	 * @param csvPath   The path and name of the csv file
	 */
	public void setHistoricDataFile( ObjectId projectId, String csvPath ) throws InvalidDataException
	{
		projectManager.setHistoricDataFile( projectId, csvPath );
	}

	/**
	 * Exports Historic Data for a Project as a CSV
	 *
	 * @param projectId The Project from which to get Historic Data
	 * @param csvPath   The path and filename to write the Historic Data to as CSV file
	 */
	public void getHistoricDataFile( ObjectId projectId, String csvPath ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		projectManager.getHistoricDataFile( projectId, csvPath );
	}

	/**
	 * Exports out a time series (ordered list of States) that included the states up to the provided state.
	 *
	 * @param projectId           The projectId from which to export
	 * @param stateId             Last state (node in tree) that is to be exported
	 * @param includeHistoricData If present, should historic data be prepended to time series
	 *
	 * @return list of states that are part of the time series
	 */
	public List< State > getTimeSeries( ObjectId projectId, ObjectId stateId, boolean includeHistoricData )
	{
		return projectManager.getTimeSeries( projectId, stateId, includeHistoricData );
	}

	// ========================= Features =========================

	/**
	 * Gets the list of Features for a Project.
	 *
	 * @param projectId The DB id of the Project to search.
	 *
	 * @return Set of Features sorted alphabetically by Feature label.
	 */
	public SortedSet< FeatureMap > getFeatures( final ObjectId projectId ) throws GeneralScenarioExplorerException
	{
		return new TreeSet<>( projectManager.getFeatures( projectId ) );
	}

	/**
	 * Gets a specific Feature based on its unique id.
	 *
	 * @param projectId The DB id of the Project to search.
	 * @param featureId The unique id assigned to a feature when it is first mapped.
	 *
	 * @return The Feature that is found.
	 */
	public FeatureMap findFeature( final ObjectId projectId, final String featureId, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		final FeatureMap feature = projectManager.getFeature( projectId, featureId );
		if( feature == null && throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "Feature with id %s does not exist!", featureId ) );
		}

		return feature;
	}

	/**
	 * Add a new Feature to a Project by primative attributes.
	 *
	 * @param projectId
	 * @param featureTypeId
	 * @param featureTypeConfig
	 * @param label
	 * @param description
	 * @param projectorId
	 * @param projectorConfig
	 */
	public FeatureMap addFeature(
		final ObjectId projectId,
		final String featureTypeId,
		final String featureTypeConfig,
		final String label,
		final String description,
		final String projectorId,
		final String projectorConfig ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		final FeatureMap newFeature = projectManager.mapFeature( projectId, featureTypeId, featureTypeConfig, label, description, projectorId, projectorConfig );

		NotificationsManager.updateProjectNotifications( projectId );

		return newFeature;
	}

	/**
	 * Add a new Feature to a Project by FeatureMap object
	 *
	 * @param projectId
	 * @param feature
	 */
	public FeatureMap addFeature( final ObjectId projectId, final FeatureMap feature ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final FeatureMap newFeature = projectManager.mapFeature( projectId, feature );

		NotificationsManager.updateProjectNotifications( projectId );

		return newFeature;
	}

	/**
	 * Adds multiple Features to a Project.
	 *
	 * @param projectId
	 * @param featureMaps
	 */
	public void addFeatures( final ObjectId projectId, Set< FeatureMap > featureMaps ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		projectManager.mapFeatures( projectId, featureMaps );

		NotificationsManager.updateProjectNotifications( projectId );

		return;
	}

	/**
	 * Update a Feature given an object.
	 *
	 * @param projectId
	 * @param updated
	 */
	public FeatureMap updateFeature( final ObjectId projectId, final FeatureMap updated ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final FeatureMap updatedFeature = projectManager.updateFeature( projectId, updated );

		NotificationsManager.updateProjectNotifications( projectId );

		return updatedFeature;
	}

	/**
	 * Update the textural attributes of a Feature without changing how it is used in Views.
	 *
	 * @param projectId
	 * @param featureMapId
	 * @param label
	 * @param description
	 */
	public FeatureMap updateFeatureText(
		final ObjectId projectId,
		final String featureMapId,
		final String label,
		final String description ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final FeatureMap updatedFeature = projectManager.updateFeatureText( projectId, featureMapId, label, description );

		NotificationsManager.updateProjectNotifications( projectId );

		return updatedFeature;
	}

	/**
	 * Removes a Feature from a Project
	 *
	 * @param projectId
	 * @param featureMapId
	 */
	public void deleteFeature( final ObjectId projectId, final String featureMapId ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		projectManager.removeFeature( projectId, featureMapId );

		NotificationsManager.updateProjectNotifications( projectId );

		return;
	}

	// ========================= Views =========================

	/**
	 * Get the set of Views for a specific Project.
	 *
	 * @param projectId         The DB id of the Project to search.
	 * @param includeMasterView true to include the master view, or false to filter it out
	 *
	 * @return Set of the Views sorted alphabetically by name.
	 */
	public SortedSet< View > getViews( final ObjectId projectId, final boolean includeMasterView ) throws InvalidDataException
	{
		if( includeMasterView )
		{
			return new TreeSet<>( projectManager.getViews( projectId ) );
		}
		else
		{
			final SortedSet< View > views = new TreeSet<>();

			for( final View view : projectManager.getViews( projectId ) )
			{
				if( !view.getLabel().equals( ProjectManager.MASTER_VIEW_NAME ) )
				{
					views.add( view );
				}
			}

			return views;
		}
	}

	/**
	 * Get a specific View based on the ViewId
	 *
	 * @param projectId               The DB id of the Project to search.
	 * @param viewId                  The DB id of the View to get.
	 * @param throwExceptionIfMissing Indicates if method should return null or exception on failure
	 *
	 * @return The View matching the ViewId given or null if not found (and exception flag not set).
	 */
	public View findView( final ObjectId projectId, final ObjectId viewId, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		View ret = Database.get( View.class, viewId );
		if( ret == null && throwExceptionIfMissing )
		{
			throw new InvalidDataException( "No such View" );
		}

		return ret;
	}

	/**
	 * Creates a new View given a View object.
	 *
	 * @param projectId
	 * @param view
	 *
	 * @return The updated View created.
	 */
	public View createView( final ObjectId projectId, final View view ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final View newView = projectManager.addView( projectId, view );

		NotificationsManager.updateProjectNotifications( projectId );

		return newView;
	}

	/**
	 * Updates a View given an updated View object, this View will be recomputed.
	 *
	 * @param projectId
	 * @param view
	 */
	public View updateView( final ObjectId projectId, final View view ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final View updatedView = projectManager.updateView( projectId, view );

		NotificationsManager.updateProjectNotifications( projectId );

		return findView( projectId, view.getId(), true );
	}

	/**
	 * Updates the text attributes of a View without changing the structural attributes.
	 *
	 * @param viewId
	 * @param label
	 * @param description
	 */
	public View updateViewText( ObjectId projectId, ObjectId viewId, String label, String description ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final View updatedView = Database.get( View.class, viewId );
		updatedView.setLabel( label );
		updatedView.setDescription( description );
		updatedView.save();

		NotificationsManager.updateProjectNotifications( projectId );

		return findView( projectId, viewId, true );
	}

	/**
	 * Removes a View and any ConditioningEvents that originate there.
	 *
	 * @param projectId
	 * @param viewId
	 */
	public void deleteView( final ObjectId projectId, final ObjectId viewId ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		projectManager.removeView( projectId, viewId );

		NotificationsManager.updateProjectNotifications( projectId );

		return;
	}

	// ========================= Timeline events =========================

	/**
	 * Gets the list of TimelineEvents for a specific Project.
	 *
	 * @param projectId The DB id of the Project to search.
	 *
	 * @return Set of TimelineEvents sorted by increasing start date.
	 */
	public SortedSet< TimelineEvent > getTimelineEvents( final ObjectId projectId ) throws InvalidDataException
	{
		// Note, this sort is based on the start date time
		return new TreeSet<>( projectManager.getTimelineEvents( projectId ) );
	}

	/**
	 * Get a specific TimelineEvent by its DB id.
	 *
	 * @param projectId               The DB id of the Project to search.
	 * @param timelineEventId         The DB id of the TimelineEvent to find.
	 * @param throwExceptionIfMissing Flag if exception is thrown when no such event exists.
	 *
	 * @return The TimeLineEvent or null of no such event (unless exception flag is set).
	 */
	public TimelineEvent findTimelineEvent( final ObjectId projectId, final ObjectId timelineEventId, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		final TimelineEvent timelineEvent = projectManager.getTimelineEvent( timelineEventId );
		if( timelineEvent == null && throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "Timeline event %s does not exist!", timelineEventId ) );
		}

		return timelineEvent;
	}

	/**
	 * Creates a new TimelineEvent from object.
	 *
	 * @param projectId
	 * @param timelineEvent
	 */
	public TimelineEvent createTimelineEvent( final ObjectId projectId, final TimelineEvent timelineEvent ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final TimelineEvent newTimelineEvent = projectManager.addTimelineEvent( projectId, timelineEvent );

		NotificationsManager.updateProjectNotifications( projectId );

		return newTimelineEvent;
	}

	/**
	 * Creates a new set of TimelineEvents from a list of objects.
	 *
	 * @param projectId
	 * @param timelineEvents
	 */
	public void createTimelineEvents( final ObjectId projectId, List< TimelineEvent > timelineEvents ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		projectManager.addTimelineEvents( projectId, timelineEvents );

		NotificationsManager.updateProjectNotifications( projectId );

		return;
	}

	/**
	 * Updates a TimelineEvent from an object.
	 *
	 * @param timelineEvent
	 */
	public TimelineEvent updateTimelineEvent( final ObjectId projectId, final TimelineEvent timelineEvent ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final TimelineEvent updatedTimelineEvent = projectManager.updateTimelineEvent( projectId, timelineEvent );

		NotificationsManager.updateProjectNotifications( projectId );

		return updatedTimelineEvent;
	}

	/**
	 * Removes the specified TimelineEvent from the Project.
	 *
	 * @param projectId
	 * @param id
	 */
	public void deleteTimelineEvent( final ObjectId projectId, final ObjectId id ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		projectManager.removeTimelineEvent( projectId, id );

		NotificationsManager.updateProjectNotifications( projectId );

		return;
	}

	// ========================= Conditioning Events =========================

	/**
	 * Returns the set of ConditioningEvents for the entire Project (all views).
	 *
	 * @param projectId The database id of the Project.
	 *
	 * @return Set of ConditioningEvents in Project.
	 */
	public SortedSet< ConditioningEvent > getConditioningEventsInProject( final ObjectId projectId ) throws InvalidDataException
	{
		return new TreeSet<>( projectManager.getConditioningEventsForProjectId( projectId ) );
	}

	/**
	 * Returns the set of ConditioningEvents available in a View (assigned or locally created).
	 *
	 * @param viewId The database id of the View.
	 *
	 * @return Set of ConditioningEvents in View.
	 */
	public SortedSet< ConditioningEvent > getConditioningEventsInView( final ObjectId projectId, final ObjectId viewId ) throws InvalidDataException
	{
		//TODO: should keep/verify projectid scope
		return getConditioningEventsInView( viewId, true );
	}

	/**
	 * Returns the set of ConditioningEvents available in a View, optionally filtered by assigned or locally created.
	 *
	 * @param viewId          The database id of the View.
	 * @param includeAssigned True if set should includes ConditioningEvents created in other views but assigned to this View.
	 *
	 * @return Set of ConditioningEvents in View.
	 */
	public SortedSet< ConditioningEvent > getConditioningEventsInView( final ObjectId viewId, final boolean includeAssigned ) throws InvalidDataException
	{
		//TODO: should keep/verify projectid scope
		if( includeAssigned )
		{
			return new TreeSet<>( projectManager.getAssignedConditioningEventsforViewId( viewId ) );
		}
		else
		{
			return new TreeSet<>( projectManager.getOriginConditioningEventsForViewId( viewId ) );
		}
	}

	/**
	 * Gets a specific ConditioningEvent by its DB id.
	 *
	 * @param projectId               The DB id of the Project to search.
	 * @param conditioningEventId     The DB id of the ConditioningEvent to find.
	 * @param throwExceptionIfMissing Flag if exception should be thrown when there is no such TimelineEvent.
	 *
	 * @return The TimelineEvent or null if not found (and flag is not set)
	 */
	public ConditioningEvent findConditioningEvent(
		final ObjectId projectId,
		final ObjectId conditioningEventId,
		final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		ConditioningEvent ce = null;
		ce = Database.get( ConditioningEvent.class, conditioningEventId );
		//TODO: should keep/verify projectid scope
		if( throwExceptionIfMissing && ce == null )
		{
			throw new InvalidDataException( String.format( "No such ConditioningEvent found for DB id \"%s\"", conditioningEventId ) );
		}

		return ce;
	}

	/**
	 * Creates a ConditioningEvent from object.
	 *
	 * @param projectId
	 * @param viewId
	 * @param conditioningEvent
	 */
	public ConditioningEvent createConditioningEvent(
		final ObjectId projectId,
		final ObjectId viewId,
		final ConditioningEvent conditioningEvent ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final ConditioningEvent newConditioningEvent = projectManager.addConditioningEvent( projectId, viewId, conditioningEvent );

		NotificationsManager.updateProjectNotifications( projectId );

		return newConditioningEvent;
	}

	/**
	 * Assigns a ConditioningEvent to a specific View, this allows it to appear in that View.
	 *
	 * @param projectId
	 * @param viewId
	 * @param eventId
	 */
	public void assignConditioningEvent( final ObjectId projectId, final ObjectId viewId, final ObjectId eventId ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		projectManager.assignConditioningEvent( projectId, viewId, eventId );

		NotificationsManager.updateProjectNotifications( projectId );

		return;
	}

	/**
	 * Updates an existing ConditioningEvent given a modified copy of the object.
	 *
	 * @param projectId
	 * @param viewId
	 * @param updatedConditioningEvent
	 */
	public ConditioningEvent updateConditioningEvent(
		final ObjectId projectId,
		final ObjectId viewId,
		final ConditioningEvent updatedConditioningEvent ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( updatedConditioningEvent == null )
		{
			throw new InvalidDataException( "ConditioningEvent cannot be null!" );
		}
		if( updatedConditioningEvent.getId() == null )
		{
			throw new InvalidDataException( "ConditioningEvent does not have database id." );
		}

		ObjectId originalId = updatedConditioningEvent.getId();
		updatedConditioningEvent.setId( null ); // the new CE will get a new database id.
		ConditioningEvent updated = projectManager.addConditioningEvent( projectId, viewId, updatedConditioningEvent );

		// Assign new CE to all the existing Views that the previous one was assigned
		for( View v : projectManager.getViews( projectId ) )
		{
			if( v.isAssigned( originalId ) )
			{
				v.assign( updated );
				v.save();
			}
		}
		projectManager.removeConditioningEvent( projectId, viewId, originalId );

		NotificationsManager.updateProjectNotifications( projectId );

		return updated;
	}

	/**
	 * Removes a ConditioningEvent from a View, if the View is its origin the ConditioningEvent is deleted entire.
	 *
	 * @param projectId
	 * @param viewId
	 * @param eventId
	 */
	public void deleteConditioningEvent( final ObjectId projectId, final ObjectId viewId, final ObjectId eventId ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		projectManager.removeConditioningEvent( projectId, viewId, eventId );

		NotificationsManager.updateProjectNotifications( projectId );

		return;
	}

	// ========================= States =========================

	public SortedSet< State > getStatesInView( final ObjectId projectId, final ObjectId viewId ) throws InvalidDataException
	{
		return stateManager.getStates( projectId, viewId );
	}

	public State findState( final ObjectId projectId, final ObjectId viewId, final ObjectId stateId, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		final State state = stateManager.getState( projectId, stateId ); // TODO viewId is not used in call!

		if( state == null && throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "State \"%s\" in project \"%s\" / view \"%s\" does not exist!", stateId, projectId, viewId ) );
		}

		return state;
	}

	// NO Create State allowed at this time

	public State updateState( final ObjectId projectId, final State updated ) throws GeneralScenarioExplorerException
	{
		return stateManager.updateState( projectId, updated );
	}

	// No Remove State allowed at this time

	// ========================= Plugins =========================

	// ========================== Plugins: FeatureType ============

	public SortedSet< FeatureType > getFeatureTypes()
	{
		// filter out any?

		return plugInManager.getFeatureTypes();
	}

	public FeatureType findFeatureType( final String id, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		// use the filtered set that the API has access to instead of the full set in the plugin manager
		for( final FeatureType featureType : getFeatureTypes() )
		{
			if( featureType.getId().equals( id ) )
			{
				return featureType;
			}
		}

		if( throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "Unknown feature type: %s", id ) );
		}
		else
		{
			return null;
		}
	}

	// ========================== Plugins: Preconditions ==========

	public SortedSet< Precondition > getPreconditions()
	{
		final SortedSet< Precondition > filteredPreconditions = new TreeSet<>();

		for( final Precondition precondition : plugInManager.getPreconditions() )
		{
			if( !( precondition instanceof OnHold ) )
			{
				filteredPreconditions.add( precondition );
			}
		}

		return filteredPreconditions;

	}

	public Precondition findPrecondition( final String id, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		// use the filtered set that the API has access to instead of the full set in the plugin manager
		for( final Precondition precondition : getPreconditions() )
		{
			if( precondition.getId().equals( id ) )
			{
				return precondition;
			}
		}

		if( throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "Unknown precondition: %s", id ) );
		}
		else
		{
			return null;
		}
	}

	// ========================== Plugins: Effects  ==========

	public SortedSet< Effect > getOutcomeEffects()
	{
		final SortedSet< Effect > filteredOutcomeEffects = new TreeSet<>();

		for( final Effect effect : plugInManager.getEffects() )
		{
			if( !( effect instanceof ErrorEffect ) )
			{
				filteredOutcomeEffects.add( effect );
			}
		}

		return filteredOutcomeEffects;
	}

	public Effect findOutcomeEffect( final String id, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		// use the filtered set that the API has access to instead of the full set in the plugin manager
		for( final Effect outcomeEffect : getOutcomeEffects() )
		{
			if( outcomeEffect.getId().equals( id ) )
			{
				return outcomeEffect;
			}
		}

		if( throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "Unknown outcome effect: %s", id ) );
		}
		else
		{
			return null;
		}
	}

	// ========================== Plugins: Projectors ==========

	public SortedSet< Projector > getProjectors()
	{
		final SortedSet< Projector > filteredProjectors = new TreeSet<>();

		for( final Projector projector : plugInManager.getProjectors() )
		{
			if( !( projector instanceof RandomProjector ) && !( projector instanceof JavaScriptProjector ) )
			{
				filteredProjectors.add( projector );
			}
		}

		return filteredProjectors;
	}

	public Projector findProjector( final String id, final boolean throwExceptionIfMissing ) throws InvalidDataException
	{
		// use the filtered set that the API has access to instead of the full set in the plugin manager
		for( final Projector projector : getProjectors() )
		{
			if( projector.getId().equals( id ) )
			{
				return projector;
			}
		}

		if( throwExceptionIfMissing )
		{
			throw new InvalidDataException( String.format( "Unknown projector: %s", id ) );
		}
		else
		{
			return null;
		}
	}

	// ========================= Factory methods =========================

	public static Precondition deserializePrecondition( final JSONObject source ) throws InvalidDataException
	{
		final String preconditionId = JsonHelper.getRequiredParameterString( source, Precondition.JsonKeys.Id );

		// TODO this should use recursion and PlugInManager.getPreconditions()

		if( preconditionId.equals( TimelineEventPrecondition.class.getCanonicalName() ) )
		{
			final Precondition precondition = new TimelineEventPrecondition();

			precondition.setConfig( JsonHelper.getRequiredParameterJSONObject( source, Precondition.JsonKeys.Config ) ); // TODO make this into a constructor!

			return precondition;
		}
		else if( preconditionId.equals( FeaturePrecondition.class.getCanonicalName() ) )
		{
			final Precondition precondition = new FeaturePrecondition();

			precondition.setConfig( JsonHelper.getRequiredParameterJSONObject( source, Precondition.JsonKeys.Config ) ); // TODO make this into a constructor!

			return precondition;
		}
		else
		{
			throw new InvalidDataException( String.format( "Could not find precondition with id: %s", preconditionId ) );
		}
	}

	public static Effect deserializeEffect( final JSONObject source ) throws InvalidDataException
	{
		final String outcomeEffectId = JsonHelper.getRequiredParameterString( source, Effect.JsonKeys.Id );

		if( outcomeEffectId.equals( FeatureSetEffect.class.getCanonicalName() ) )
		{
			final Effect effect = new FeatureSetEffect();

			effect.setConfig( JsonHelper.getRequiredParameterJSONObject( source, Effect.JsonKeys.Config ) ); // TODO make this into a constructor!

			return effect;
		}
		else
		{
			throw new InvalidDataException( String.format( "Could not find outcome effect with id: %s", outcomeEffectId ) );
		}
	}
}
