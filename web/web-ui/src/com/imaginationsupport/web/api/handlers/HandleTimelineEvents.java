package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.TimelineEvent;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.exceptions.NotAuthorizedException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.exceptions.*;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.SortedSet;

@SuppressWarnings( { "unused", "Duplicates" } )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.TimelineEvent )
public abstract class HandleTimelineEvents extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of timeline events for the specified project",
		request = RestApiRequestInfo.Request.ListTimelineEvents,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Parameters.ProjectId, ApiStrings.RestApiStrings.TimelineEvent },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.TimelineEvent )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONArray listTimelineEvents( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.ListTimelineEvents, projectId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to get the specified project!", e );
		}

		/////////////// handle the action ///////////////

		final SortedSet< TimelineEvent > timelineEvents;
		try
		{
			timelineEvents = api.getTimelineEvents( projectId );
		}
		catch( final InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error getting project timeline events!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return JsonHelper.toJSONArray( timelineEvents );
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error running request!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified timeline event",
		request = RestApiRequestInfo.Request.GetTimelineEvent,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.TimelineEvent,
			ApiStrings.RestApiStrings.Parameters.TimelineEventId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.TimelineEvent )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.TimelineEventId,
			description = "The unique id of the timeline event to get",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleGetTimelineEvent( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId timelineEventId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.TimelineEventId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetTimelineEvent, projectId, timelineEventId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to get the specified timeline event!", e );
		}

		/////////////// handle the action ///////////////

		final TimelineEvent timelineEvent;
		try
		{
			timelineEvent = api.findTimelineEvent( projectId, timelineEventId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Project \"%s\" / Timeline event \"%s\" does not exist", projectId, timelineEventId.toHexString() ), e );
		}

		/////////////// return the response ///////////////

		try
		{
			return timelineEvent.toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing timeline event", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Creates the specified timeline event",
		request = RestApiRequestInfo.Request.NewTimelineEvent,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.TimelineEvent },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.TimelineEvent )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "timelineEvent",
			description = "The timeline event to create",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.Project ) } )
	public static JSONObject handleNewTimelineEvent( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final TimelineEvent timelineEventFromRequest = parseBody( body );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.NewTimelineEvent, projectId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden!", e );
		}

		/////////////// handle the action ///////////////

		final TimelineEvent newTimelineEvent;
		try
		{
			newTimelineEvent = api.createTimelineEvent( projectId, timelineEventFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating timeline event!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return newTimelineEvent.toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing timeline event!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Updates the specified timeline event",
		request = RestApiRequestInfo.Request.UpdateTimelineEvent,
		method = RestApiRequestInfo.HttpMethod.Put,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.TimelineEvent,
			ApiStrings.RestApiStrings.Parameters.TimelineEventId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.TimelineEvent )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.TimelineEventId,
			description = "The unique id of the timeline event to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "timelineEvent",
			description = "The timeline event to update",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.TimelineEvent ) } )
	public static JSONObject handleUpdateTimelineEvent( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId timelineEventId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.TimelineEventId );
		final TimelineEvent timelineEventFromRequest = parseBody( body );

		// make sure the id in the request is the same as the body
		if( !timelineEventId.equals( timelineEventFromRequest.getId() ) )
		{
			throw new BadRequestException(
				String.format(
					"Timeline event id in URI \"%s\" does not match the request body \"%s\"",
					timelineEventId.toHexString(),
					timelineEventFromRequest.getId().toHexString() ) );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.UpdateTimelineEvent, projectId, timelineEventId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to update specified timeline event!", e );
		}

		/////////////// handle the action ///////////////

		final TimelineEvent updatedTimelineEvent;
		try
		{
			updatedTimelineEvent = api.updateTimelineEvent( projectId, timelineEventFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error updating timeline event!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return updatedTimelineEvent.toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing timeline event", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Deletes the specified timeline event",
		request = RestApiRequestInfo.Request.DeleteTimelineEvent,
		method = RestApiRequestInfo.HttpMethod.Delete,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.TimelineEvent,
			ApiStrings.RestApiStrings.Parameters.TimelineEventId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.TimelineEventId,
			description = "The unique id of the timeline event to delete",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleDeleteTimelineEvent( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId timelineEventId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.TimelineEventId );

		// now verify that this timeline event actually exists
		final TimelineEvent timelineEvent;
		try
		{
			timelineEvent = api.findTimelineEvent( projectId, timelineEventId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Timeline event \"%s\" does not exist", timelineEventId.toHexString() ), e );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.DeleteTimelineEvent, projectId, timelineEventId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to delete the specified timeline event!", e );
		}

		/////////////// handle the action ///////////////

		try
		{
			api.deleteTimelineEvent( projectId, timelineEventId );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error deleting timeline event!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return timelineEvent.toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing timeline event", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// helper methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the timeline event from the request body
	 *
	 * @param body the request body to parse
	 *
	 * @return the parsed timeline event
	 */
	private static TimelineEvent parseBody( final JSONObject body ) throws BadRequestException
	{
		try
		{
			return new TimelineEvent( body );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad request!", e );
		}
	}
}