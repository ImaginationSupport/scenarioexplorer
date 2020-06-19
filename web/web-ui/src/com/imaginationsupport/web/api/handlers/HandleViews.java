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
import com.imaginationsupport.views.FBView;
import com.imaginationsupport.views.SQView;
import com.imaginationsupport.views.View;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.exceptions.*;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.SortedSet;

@SuppressWarnings( { "unused", "Duplicates" } )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.View )
public abstract class HandleViews extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of views for the specified project",
		request = RestApiRequestInfo.Request.ListViews,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Parameters.ProjectId, ApiStrings.RestApiStrings.View },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.View )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONArray listViews( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.ListViews, projectId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to get the specified view!", e );
		}

		/////////////// handle the action ///////////////

		final SortedSet< View > views;
		try
		{
			views = api.getViews( projectId, false );
		}
		catch( final InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error getting project views!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return JsonHelper.toJSONArray( views );
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error running request!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified view",
		request = RestApiRequestInfo.Request.GetView,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.View,
			ApiStrings.RestApiStrings.Parameters.ViewId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.View )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ViewId,
			description = "The unique id of the view to get",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleGetView( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId viewId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ViewId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetView, projectId, viewId, true );
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

		final View view;
		try
		{
			view = api.findView( projectId, viewId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "View \"%s\" does not exist", projectId.toHexString() ), e );
		}

		/////////////// return the response ///////////////

		try
		{
			return view.toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Creates the specified view",
		request = RestApiRequestInfo.Request.NewView,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.View },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.View )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "project",
			description = "The project to create",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.Project ) } )
	public static JSONObject handleNewView( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final View viewFromRequest = parseBody( api, body, projectId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.NewView, projectId, true );
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

		final View newView;
		try
		{
			newView = api.createView( projectId, viewFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating project!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return newView.toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Updates the specified view",
		request = RestApiRequestInfo.Request.UpdateView,
		method = RestApiRequestInfo.HttpMethod.Put,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.View,
			ApiStrings.RestApiStrings.Parameters.ViewId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.View )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ViewId,
			description = "The unique id of the view to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "view",
			description = "The view to update",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.View ) } )
	public static JSONObject handleUpdateView( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId viewId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ViewId );
		final View viewFromRequest = parseBody( api, body, projectId );

		// make sure the project id in the request is the same as the body
		if( !viewId.equals( viewFromRequest.getId() ) )
		{
			throw new BadRequestException(
				String.format(
					"View id in URI \"%s\" does not match the request body \"%s\"",
					viewId.toHexString(),
					viewFromRequest.getId().toHexString() ) );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.UpdateView, projectId, viewId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to update specified project!", e );
		}

		/////////////// handle the action ///////////////

		final View updatedView;
		try
		{
			updatedView = api.updateView( projectId, viewFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error updating project!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return updatedView.toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Deletes the specified view",
		request = RestApiRequestInfo.Request.DeleteView,
		method = RestApiRequestInfo.HttpMethod.Delete,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.View,
			ApiStrings.RestApiStrings.Parameters.ViewId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ViewId,
			description = "The unique id of the view to delete",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleDeleteView( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId viewId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ViewId );

		// now verify that this project actually exists
		final View view;
		try
		{
			view = api.findView( projectId, viewId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "View \"%s\" does not exist", viewId.toHexString() ), e );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetProject, projectId, true );
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

		try
		{
			api.deleteView( projectId, viewId );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error deleting project!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return view.toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the tree for the specified view",
		request = RestApiRequestInfo.Request.GetViewTree,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.View,
			ApiStrings.RestApiStrings.Parameters.ViewId,
			ApiStrings.RestApiStrings.Tree },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ViewTree )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ViewId,
			description = "The unique id of the view to get",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleGetViewTree( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId viewId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ViewId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetView, projectId, viewId, true );
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

		final View view;
		try
		{
			view = api.findView( projectId, viewId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "View \"%s\" does not exist", projectId.toHexString() ), e );
		}

		/////////////// return the response ///////////////

		try
		{
			return view.getTree().toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the stats for the specified view",
		request = RestApiRequestInfo.Request.GetViewStats,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.View,
			ApiStrings.RestApiStrings.Parameters.ViewId,
			ApiStrings.RestApiStrings.Stats },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.GenericObject )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ViewId,
			description = "The unique id of the view to get",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleGetViewStats( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final ObjectId viewId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ViewId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetView, projectId, viewId, true );
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

		final View view;
		try
		{
			view = api.findView( projectId, viewId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "View \"%s\" does not exist", projectId.toHexString() ), e );
		}

		/////////////// return the response ///////////////

		try
		{
			return view.getStatsJSON();
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error serializing project", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// helper methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the view from the request body
	 *
	 * @param body the request body to parse
	 *
	 * @return the parsed view
	 */
	private static View parseBody( final API api, final JSONObject body, final ObjectId projectId ) throws BadRequestException
	{
		try
		{
			final String viewType = JsonHelper.getRequiredParameterString( body, View.JsonKeys.Type );

			switch( viewType )
			{
				case View.ViewTypes.FuturesBuilding:
					return new FBView( body );

				case View.ViewTypes.SmartQuery:
					return new SQView( body, api.findProject( projectId, true ).getFeatureMaps() );

				case View.ViewTypes.WhatIf:
					throw new GeneralScenarioExplorerException( "What-If views not available!" ); // TODO finish!

				case View.ViewTypes.ExtremeState:
					throw new GeneralScenarioExplorerException( "Extreme State views not available!" ); // TODO finish!

				default:
					throw new GeneralScenarioExplorerException( String.format( "Unknown view type: %s", viewType ) );
			}
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new BadRequestException( "Bad request!", e );
		}
	}
}