package com.imaginationsupport.data;

import com.imaginationsupport.API;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.views.View;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestApiObjectInfo( definitionName = "ProjectBackup", tagName = RestApiHandlerInfo.CategoryNames.Project, description = "Scenario Explorer Project Backup" )
@RestApiRequestObjectPseudoFields( value = {
	@RestApiRequestObjectPseudoFieldInfo(
		name = ProjectBackup.KEY_FILE_FORMAT,
		rawType = Integer.class,
		description = "The file format version used" ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = ProjectBackup.KEY_EXPORTED,
		rawType = LocalDateTime.class,
		description = "The date and time the project was exported" ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = ProjectBackup.KEY_PROJECT,
		rawType = Project.class, description = "The project details" ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = ProjectBackup.KEY_FEATURES,
		rawType = JSONArray.class,
		genericInnerType = FeatureMap.class,
		description = "The project features" ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = ProjectBackup.KEY_TIMELINE_EVENTS,
		rawType = JSONArray.class,
		genericInnerType = TimelineEvent.class,
		description = "The project timeline events" ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = ProjectBackup.KEY_VIEWS,
		rawType = JSONArray.class,
		genericInnerType = View.class,
		description = "The project views" ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = ProjectBackup.KEY_CONDITIONING_EVENTS,
		rawType = JSONArray.class,
		genericInnerType = ConditioningEvent.class,
		description = "The project conditioning events" )
} )
public abstract class ProjectBackup
{
	private static final int CURRENT_FILE_FORMAT_VERSION = 1;

	static final String KEY_FILE_FORMAT = "fileFormat";
	static final String KEY_EXPORTED = "exported";
	static final String KEY_PROJECT = "project";
	static final String KEY_FEATURES = "features";
	static final String KEY_TIMELINE_EVENTS = "timelineEvents";
	static final String KEY_VIEWS = "views";
	static final String KEY_CONDITIONING_EVENTS = "conditioningEvents";

	public static JSONObject exportProject( final API api, final ObjectId projectId ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final JSONObject json = new JSONObject();

		json.put( KEY_FILE_FORMAT, CURRENT_FILE_FORMAT_VERSION );
		json.put( KEY_EXPORTED, String.format( "%s UTC", ImaginationSupportUtil.formatDateTime( LocalDateTime.now() ) ) );

		// project
		final Project project = api.findProject( projectId, true );
		json.put( KEY_PROJECT, project.toJSON() );

		// features
		json.put( KEY_FEATURES, JsonHelper.toJSONArray( project.getFeatureMaps() ) );

		// timeline events
		json.put( KEY_TIMELINE_EVENTS, JsonHelper.toJSONArray( api.getTimelineEvents( projectId ) ) );

		// views
		json.put( KEY_VIEWS, JsonHelper.toJSONArray( api.getViews( projectId, false ) ) );

		// conditioning events
		json.put( KEY_CONDITIONING_EVENTS, JsonHelper.toJSONArray( api.getConditioningEventsInProject( projectId ) ) );

		return json;
	}

	/**
	 * Clones the given project using the given user as the new owner
	 *
	 * @param api       the Imagination Support API
	 * @param projectId the unique id of the existing project
	 * @param newOwner  the id of the user to set as the new owner
	 *
	 * @return the new cloned project
	 */
	public static Project cloneProject( final API api, final ObjectId projectId, final User newOwner ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final Project sourceProject = api.findProject( projectId, true );

		final Project clonedProject = importProject(
			api,
			exportProject( api, projectId ),
			newOwner,
			true,
			true,
			true,
			true );

		clonedProject.setName( String.format( "%s - Cloned %s UTC", sourceProject.getName(), ImaginationSupportUtil.formatDateTime( LocalDateTime.now() ) ) );
		api.updateProject( clonedProject );

		return api.findProject( clonedProject.getId(), true );
	}

	/**
	 * Imports the given JSON export
	 *
	 * @param api                      the Imagination Support API
	 * @param backup                   the JSON object of the backup
	 * @param newOwner                 the id of the user to set as the new owner
	 * @param importTimelineEvents     true to import the timeline events, false to skip
	 * @param importFeatures           true to import the features, false to skip
	 * @param importConditioningEvents true to import the conditioning events, false to skip
	 * @param importViews              true to import the views, false to skip
	 *
	 * @return the new imported project
	 */
	public static Project importProject(
		final API api,
		final JSONObject backup,
		final User newOwner,
		final boolean importTimelineEvents,
		final boolean importFeatures,
		final boolean importConditioningEvents,
		final boolean importViews ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( !JsonHelper.checkParameterExists( backup, KEY_FILE_FORMAT ) )
		{
			throw new InvalidDataException( "Backup is not valid - Unknown file format!" );
		}

		final int backupFileFormatVersion = JsonHelper.getRequiredParameterInt( backup, KEY_FILE_FORMAT );
		if( backupFileFormatVersion == 1 )
		{
			return importV1( api, backup, newOwner, importTimelineEvents, importFeatures, importConditioningEvents, importViews );
		}

		throw new InvalidDataException( String.format( "Invalid backup version number: %d", backupFileFormatVersion ) );
	}

	/**
	 * Imports the given JSON export in version 1 export format
	 *
	 * @param api                      the Imagination Support API
	 * @param backup                   the JSON object of the backup
	 * @param newOwner                 the id of the user to set as the new owner
	 * @param importTimelineEvents     true to import the timeline events, false to skip
	 * @param importFeatures           true to import the features, false to skip
	 * @param importConditioningEvents true to import the conditioning events (required features to be imported), false to skip
	 * @param importViews              true to import the views (requires conditioning events to be imported), false to skip
	 *
	 * @return the new imported project
	 */
	private static Project importV1(
		final API api,
		final JSONObject backup,
		final User newOwner,
		final boolean importFeatures,
		final boolean importTimelineEvents,
		final boolean importViews,
		final boolean importConditioningEvents ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final Map< String, String > idsToConvert = new HashMap<>();

		final JSONObject rawProject = JsonHelper.getRequiredParameterJSONObject( backup, KEY_PROJECT );
		final Project sourceProject = new Project( rawProject );

		final Project projectToCreate = new Project( sourceProject );
		projectToCreate.setId( null );
		projectToCreate.setOwner( newOwner );
		projectToCreate.setName( String.format( "%s - Imported %s UTC", projectToCreate.getName(), ImaginationSupportUtil.formatDateTime( LocalDateTime.now() ) ) );

		final Project projectCreated = api.createProject( projectToCreate );
		idsToConvert.put(
			JsonHelper.getRequiredParameterObjectId( rawProject, Project.JsonKeys.Id, false ).toHexString(),
			projectCreated.getId().toHexString() );

		// import the timeline events
		if( importTimelineEvents )
		{
			final JSONArray timelineEventsRaw = JsonHelper.getRequiredParameterJSONArray( backup, KEY_TIMELINE_EVENTS );
			for( int timelineEventIndex = 0; timelineEventIndex < timelineEventsRaw.length(); ++timelineEventIndex )
			{
				final JSONObject sourceRaw = timelineEventsRaw.getJSONObject( timelineEventIndex );
				final TimelineEvent timelineEventToCreate = new TimelineEvent( prepareImportObject( sourceRaw, idsToConvert, true ) );

				final TimelineEvent timelineEventCreated = api.createTimelineEvent( projectCreated.getId(), timelineEventToCreate );
				idsToConvert.put(
					JsonHelper.getRequiredParameterObjectId( sourceRaw, TimelineEvent.JsonKeys.Id, false ).toHexString(),
					timelineEventCreated.getId().toHexString() );
			}
		}

		// import the features
		if( importFeatures )
		{
			final JSONArray featuresRaw = JsonHelper.getRequiredParameterJSONArray( backup, KEY_FEATURES );
			for( int featureIndex = 0; featureIndex < featuresRaw.length(); ++featureIndex )
			{
				final JSONObject sourceRaw = featuresRaw.getJSONObject( featureIndex );
				final FeatureMap featureToCreate = new FeatureMap( prepareImportObject( sourceRaw, idsToConvert, true ) );

				final FeatureMap featureCreated = api.addFeature( projectCreated.getId(), featureToCreate );
				idsToConvert.put(
					JsonHelper.getRequiredParameterString( sourceRaw, FeatureMap.JsonKeys.Id ),
					featureCreated.getUid() );
			}

			// import the conditioning events
			if( importConditioningEvents )
			{
				final JSONArray conditioningEventsRaw = JsonHelper.getRequiredParameterJSONArray( backup, KEY_CONDITIONING_EVENTS );
				for( int conditioningEventIndex = 0; conditioningEventIndex < conditioningEventsRaw.length(); ++conditioningEventIndex )
				{
					final JSONObject sourceRaw = prepareImportObject( conditioningEventsRaw.getJSONObject( conditioningEventIndex ), idsToConvert, false );
					final ConditioningEvent conditioningEventToCreate = new ConditioningEvent( prepareImportObject( sourceRaw, idsToConvert, true ) );

					final ConditioningEvent conditioningEventCreated = api.createConditioningEvent( projectCreated.getId(),
						conditioningEventToCreate.getOriginViewId(),
						conditioningEventToCreate );
					idsToConvert.put(
						JsonHelper.getRequiredParameterObjectId( sourceRaw, ConditioningEvent.JsonKeys.Id, false ).toHexString(),
						conditioningEventCreated.getId().toHexString() );
				}

				// import the views
				if( importViews )
				{
					// when creating the views, we need a projectId object with the features loaded, so we need to get it again from the API
					final Project projectWithFeatures = api.findProject( projectCreated.getId(), true );

					// create the views
					final JSONArray viewsRaw = JsonHelper.getRequiredParameterJSONArray( backup, KEY_VIEWS );
					for( int viewIndex = 0; viewIndex < viewsRaw.length(); ++viewIndex )
					{
						final JSONObject sourceRaw = prepareImportObject( viewsRaw.getJSONObject( viewIndex ), idsToConvert, false );
						JsonHelper.put( sourceRaw, View.JsonKeys.Assigned, new JSONArray() );

						final View viewToCreate = View.fromJSON( prepareImportObject( sourceRaw, idsToConvert, true ), projectWithFeatures );

						// TODO this could fail because the actual backend is asynchronous... the previous entry (create the feature) hasn't come back before we try to do the next one (cerate the SQ view on this feature)

						final View viewCreated = api.createView( projectWithFeatures.getId(), viewToCreate );
						idsToConvert.put(
							JsonHelper.getRequiredParameterObjectId( sourceRaw, View.JsonKeys.Id, false ).toHexString(),
							viewCreated.getId().toHexString() );
					}
				}
			}
			else if( importViews )
			{
				throw new InvalidDataException( "Importing views requires importing conditioning events" );
			}

		}
		else
		{
			if( importConditioningEvents )
			{
				throw new InvalidDataException( "Importing conditioning events requires importing features" );
			}

			if( importViews )
			{
				throw new InvalidDataException( "Importing views requires importing features and conditioning events" );
			}
		}

		return api.findProject( projectCreated.getId(), true );
	}

	/**
	 * Helper function to prepare the given object
	 *
	 * @param source        the source object
	 * @param idsToConvert  the set of unique ids to convert
	 * @param removeIdField true to remove the "id" field
	 *
	 * @return the updated object
	 */
	private static JSONObject prepareImportObject( final JSONObject source, final Map< String, String > idsToConvert, final boolean removeIdField ) throws InvalidDataException
	{
		String workingJSON = source.toString();

		// replace all of the ids
		for( final String key : idsToConvert.keySet() )
		{
			workingJSON = workingJSON.replaceAll( "\"" + key + "\"", "\"" + idsToConvert.get( key ) + "\"" );
		}

		final JSONObject updated = JsonHelper.parseObject( workingJSON );

		if( removeIdField && JsonHelper.checkParameterExists( updated, "id" ) )
		{
			JsonHelper.remove( updated, "id" );
		}

		return updated;
	}
}
