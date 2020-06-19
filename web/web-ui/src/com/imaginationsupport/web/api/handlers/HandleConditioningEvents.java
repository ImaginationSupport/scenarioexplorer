package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.ConditioningEvent;
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
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.ConditioningEvent )
public class HandleConditioningEvents extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of conditioning events for the specified project",
		request = RestApiRequestInfo.Request.ListConditioningEvents,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Parameters.ProjectId, ApiStrings.RestApiStrings.ConditioningEvent },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ConditioningEvent )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONArray listConditioningEvents( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.ListConditioningEvents, projectId, true );
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

		final SortedSet< ConditioningEvent > conditioningEvents;
		try
		{
			conditioningEvents = api.getConditioningEventsInProject( projectId );
		}
		catch( final InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error getting project conditioning events!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return JsonHelper.toJSONArray( conditioningEvents );
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error running request!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified conditioning event",
		request = RestApiRequestInfo.Request.GetConditioningEvent,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.ConditioningEvent,
			ApiStrings.RestApiStrings.Parameters.ConditioningEventId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ConditioningEvent )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ConditioningEventId,
			description = "The unique id of the conditioning event to get",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleGetConditioningEvent( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId conditioningEventId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ConditioningEventId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetConditioningEvent, projectId, conditioningEventId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to get the specified conditioning event!", e );
		}

		/////////////// handle the action ///////////////

		final ConditioningEvent conditioningEvent;
		try
		{
			conditioningEvent = api.findConditioningEvent( projectId, conditioningEventId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Project \"%s\" / Conditioning event \"%s\" does not exist", projectId, conditioningEventId.toHexString() ), e );
		}

		/////////////// return the response ///////////////

		try
		{
			return conditioningEvent.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing conditioning event", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Creates the specified conditioning event",
		request = RestApiRequestInfo.Request.NewConditioningEvent,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.ConditioningEvent },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ConditioningEvent )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "conditioningEvent",
			description = "The conditioning event to create",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.Project ) } )
	public static JSONObject handleNewConditioningEvent(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters,
		final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ConditioningEvent conditioningEventFromRequest = parseBody( body );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.NewConditioningEvent, projectId, true );
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

		final ConditioningEvent newConditioningEvent;
		try
		{
			newConditioningEvent = api.createConditioningEvent( projectId, conditioningEventFromRequest.getOriginViewId(), conditioningEventFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating conditioning event!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return newConditioningEvent.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing conditioning event!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Updates the specified conditioning event",
		request = RestApiRequestInfo.Request.UpdateConditioningEvent,
		method = RestApiRequestInfo.HttpMethod.Put,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.ConditioningEvent,
			ApiStrings.RestApiStrings.Parameters.ConditioningEventId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ConditioningEvent )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ConditioningEventId,
			description = "The unique id of the conditioning event to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "conditioningEvent",
			description = "The conditioning event to update",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.ConditioningEvent ) } )
	public static JSONObject handleUpdateConditioningEvent( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId conditioningEventId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ConditioningEventId );
		final ConditioningEvent conditioningEventFromRequest = parseBody( body );

		// make sure the id in the request is the same as the body
		if( !conditioningEventId.equals( conditioningEventFromRequest.getId() ) )
		{
			throw new BadRequestException(
				String.format(
					"Conditioning event id in URI \"%s\" does not match the request body \"%s\"",
					conditioningEventId.toHexString(),
					conditioningEventFromRequest.getId().toHexString() ) );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.UpdateConditioningEvent, projectId, conditioningEventId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to update specified conditioning event!", e );
		}

		/////////////// handle the action ///////////////

		final ConditioningEvent updatedConditioningEvent;
		try
		{
			updatedConditioningEvent = api.updateConditioningEvent( projectId, conditioningEventFromRequest.getOriginViewId(), conditioningEventFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error updating conditioning event!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return updatedConditioningEvent.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing conditioning event", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Deletes the specified conditioning event",
		request = RestApiRequestInfo.Request.DeleteConditioningEvent,
		method = RestApiRequestInfo.HttpMethod.Delete,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.ConditioningEvent,
			ApiStrings.RestApiStrings.Parameters.ConditioningEventId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ConditioningEventId,
			description = "The unique id of the conditioning event to delete",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleDeleteConditioningEvent( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId conditioningEventId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ConditioningEventId );

		// now verify that this conditioning event actually exists
		final ConditioningEvent conditioningEvent;
		try
		{
			conditioningEvent = api.findConditioningEvent( projectId, conditioningEventId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Conditioning event \"%s\" does not exist", conditioningEventId.toHexString() ), e );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.DeleteConditioningEvent, projectId, conditioningEventId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to delete the specified conditioning event!", e );
		}

		/////////////// handle the action ///////////////

		try
		{
			api.deleteConditioningEvent( projectId, conditioningEvent.getOriginViewId(), conditioningEventId );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error deleting conditioning event!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return conditioningEvent.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing conditioning event", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Assigns the specified conditioning event to the specified view",
		request = RestApiRequestInfo.Request.AssignConditioningEvent,
		method = RestApiRequestInfo.HttpMethod.Put,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.View,
			ApiStrings.RestApiStrings.Parameters.ViewId,
			ApiStrings.RestApiStrings.ConditioningEvent,
			ApiStrings.RestApiStrings.Parameters.ConditioningEventId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ViewId,
			description = "The unique id of the view",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ConditioningEventId,
			description = "The unique id of the conditioning event",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleAssignConditioningEvent( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId conditioningEventId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ConditioningEventId );
		final ObjectId viewId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ViewId );

		// now verify that this conditioning event actually exists
		final ConditioningEvent conditioningEvent;
		try
		{
			conditioningEvent = api.findConditioningEvent( projectId, conditioningEventId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Conditioning event \"%s\" does not exist", conditioningEventId.toHexString() ), e );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.AssignConditioningEvent, projectId, viewId, conditioningEventId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to delete the specified conditioning event!", e );
		}

		/////////////// handle the action ///////////////

		try
		{
			api.assignConditioningEvent( projectId, viewId, conditioningEventId );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error deleting conditioning event!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return conditioningEvent.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing conditioning event", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// helper methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the conditioning event from the request body
	 *
	 * @param body the request body to parse
	 *
	 * @return the parsed conditioning event
	 */
	private static ConditioningEvent parseBody( final JSONObject body ) throws BadRequestException
	{
		try
		{
			return new ConditioningEvent( body );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad request!", e );
		}
	}
}
