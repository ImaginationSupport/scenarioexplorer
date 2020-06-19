package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.exceptions.NotAuthorizedException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.exceptions.ApiException;
import com.imaginationsupport.web.exceptions.ForbiddenException;
import com.imaginationsupport.web.exceptions.InternalServerErrorException;
import com.imaginationsupport.web.exceptions.NotFoundException;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.SortedSet;

@SuppressWarnings( { "unused", "Duplicates" } )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.State )
public class HandleStates extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of states for the specified project view",
		request = RestApiRequestInfo.Request.ListStates,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.View,
			ApiStrings.RestApiStrings.Parameters.ViewId,
			ApiStrings.RestApiStrings.State },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.State )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ViewId,
			description = "The unique id of the view",
			in = RestApiRequestParameterInfo.InLocations.Path )	} )
	public static JSONArray listState( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId viewId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ViewId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.ListStates, projectId, viewId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to get the specified project!", e );
		}

		/////////////// handle the action ///////////////

		final SortedSet< State > states;
		try
		{
			states = api.getStatesInView( projectId, viewId );
		}
		catch( final InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error getting view states!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return JsonHelper.toJSONArray( states );
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error running request!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified state",
		request = RestApiRequestInfo.Request.GetState,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.View,
			ApiStrings.RestApiStrings.Parameters.ViewId,
			ApiStrings.RestApiStrings.State,
			ApiStrings.RestApiStrings.Parameters.StateId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.State )
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
			name = ApiStrings.JsonKeys.StateId,
			description = "The unique id of the state",
			in = RestApiRequestParameterInfo.InLocations.Path )	} )
	public static JSONObject handleGetState( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId viewId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ViewId );
		final ObjectId stateId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.StateId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetState, projectId, viewId, stateId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to get the specified state!", e );
		}

		/////////////// handle the action ///////////////

		final State state;
		try
		{
			state = api.findState( projectId, viewId, stateId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException(
				String.format( "Project \"%s\" / View \"%s\" / State \"%s\" does not exist", projectId.toHexString(), viewId.toHexString(), stateId.toHexString() ),
				e );
		}

		/////////////// return the response ///////////////

		try
		{
			return state.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing state", e );
		}
	}
}
