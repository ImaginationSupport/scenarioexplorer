package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.exceptions.NotAuthorizedException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.exceptions.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.SortedSet;

@SuppressWarnings( { "unused", "Duplicates" } )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.User )
public abstract class HandleUsers extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of users",
		request = RestApiRequestInfo.Request.ListUsers,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.User },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.User )
	public static JSONArray listUsers( final API api ) throws ApiException
	{
		/////////////// parse the request ///////////////

		// no parameters in request

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final SortedSet< User > users = api.getUsers();

		/////////////// return the response ///////////////

		try
		{
			return JsonHelper.toJSONArray( users );
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error running request!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified user",
		request = RestApiRequestInfo.Request.GetUser,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.User, ApiStrings.RestApiStrings.Parameters.UserName },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.User )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.Username,
			description = "The username to get",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleGetUser( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final String username = getStringParameter( uriParameters, ApiStrings.JsonKeys.Username );

		/////////////// verify authorization ///////////////

		// No authorization needed

		/////////////// handle the action ///////////////

		final User user;
		try
		{
			user = api.findUser( username, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "User \"%s\" does not exist", username ), e );
		}

		/////////////// return the response ///////////////

		try
		{
			return user.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing user", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Creates the given user",
		request = RestApiRequestInfo.Request.NewUser,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.User },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.User )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = "user",
			description = "The user to create",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.User ) } )
	public static JSONObject handleNewUser( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final User userFromRequest = parseBody( body );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.NewUser, true );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden!", e );
		}

		/////////////// handle the action ///////////////

		final User newUser;
		try
		{
			newUser = api.createUser( userFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating user!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return newUser.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing user!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Updates the specified user",
		request = RestApiRequestInfo.Request.UpdateUser,
		method = RestApiRequestInfo.HttpMethod.Put,
		uriParts = { ApiStrings.RestApiStrings.User, ApiStrings.RestApiStrings.Parameters.UserName },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.User )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.Username,
			description = "The username to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "user",
			description = "The user to update",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.User ) } )
	public static JSONObject handleUpdateUser( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final String username = getStringParameter( uriParameters, ApiStrings.JsonKeys.Username );

		final User userFromRequest = parseBody( body );

		// make sure the username in the request is the same as the body (otherwise this would rename the user)
		if( !username.equalsIgnoreCase( userFromRequest.getUserName() ) )
		{
			throw new BadRequestException( String.format( "Username in URI \"%s\" does not match the request body \"%s\"", username, userFromRequest.getUserName() ) );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.UpdateUser, userFromRequest.getId(), true );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to update given user!", e );
		}

		/////////////// handle the action ///////////////

		final User updatedUser;
		try
		{
			updatedUser = api.updateUser( userFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error updating user!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return updatedUser.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing user", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Deletes the specified user",
		request = RestApiRequestInfo.Request.DeleteUser,
		method = RestApiRequestInfo.HttpMethod.Delete,
		uriParts = { ApiStrings.RestApiStrings.User, ApiStrings.RestApiStrings.Parameters.UserName },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.User )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.Username,
			description = "The username to delete",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleDeleteUser( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final String username = getStringParameter( uriParameters, ApiStrings.JsonKeys.Username );

		// now verify that this user actually exists
		final User user;
		try
		{
			user = api.findUser( username, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "User \"%s\" does not exist", username ), e );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.DeleteUser, user.getId(), true );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to delete given user!", e );
		}

		/////////////// handle the action ///////////////

		try
		{
			api.deleteUser( username );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error deleting user!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return user.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing user", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// helper methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the user from the request body
	 *
	 * @param body the request body to parse
	 *
	 * @return the parsed user
	 */
	private static User parseBody( final JSONObject body ) throws BadRequestException
	{
		try
		{
			return new User( body );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad request!", e );
		}
	}
}
