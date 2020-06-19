package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.ProjectBackup;
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
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.Project )
public abstract class HandleProjects extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of projects",
		request = RestApiRequestInfo.Request.ListProjects,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	public static JSONArray listProjects( final API api, final User requestUser ) throws ApiException
	{
		/////////////// parse the request ///////////////

		// no parameters in request

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final SortedSet< Project > projects;
		try
		{
			projects = api.getProjectsForUser( requestUser );
		}
		catch( final InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error running request!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return JsonHelper.toJSONArray( projects );
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error running request!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified project",
		request = RestApiRequestInfo.Request.GetProject,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Parameters.ProjectId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project to get",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleGetProject( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );

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

		final Project project;
		try
		{
			project = api.findProject( projectId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Project \"%s\" does not exist", projectId.toHexString() ), e );
		}

		/////////////// return the response ///////////////

		try
		{
			return project.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Creates the specified project",
		request = RestApiRequestInfo.Request.NewProject,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.Project },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = "project",
			description = "The project to create",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.Project ) } )
	public static JSONObject handleNewProject( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final Project projectFromRequest = parseBody( body );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.NewProject, true );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden!", e );
		}

		/////////////// handle the action ///////////////

		final Project newProject;
		try
		{
			// the owner of the project should always be the user making the request
			projectFromRequest.setOwner( requestUser );

			newProject = api.createProject( projectFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating project!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return newProject.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Updates the specified project",
		request = RestApiRequestInfo.Request.UpdateProject,
		method = RestApiRequestInfo.HttpMethod.Put,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Parameters.ProjectId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "project",
			description = "The project to update",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.Project ) } )
	public static JSONObject handleUpdateProject( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );

		final Project projectFromRequest = parseBody( body );

		// make sure the project id in the request is the same as the body
		if( !projectId.equals( projectFromRequest.getId() ) )
		{
			throw new BadRequestException(
				String.format(
					"Project id in URI \"%s\" does not match the request body \"%s\"",
					projectId.toHexString(),
					projectFromRequest.getId().toHexString() ) );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.UpdateProject, projectId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to update specified project!", e );
		}

		/////////////// handle the action ///////////////

		final Project updatedProject;
		try
		{
			updatedProject = api.updateProject( projectFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error updating project!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return updatedProject.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Deletes the specified project",
		request = RestApiRequestInfo.Request.DeleteProject,
		method = RestApiRequestInfo.HttpMethod.Delete,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Parameters.ProjectId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project to delete",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleDeleteProject( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );

		// now verify that this project actually exists
		final Project project;
		try
		{
			project = api.findProject( projectId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Project \"%s\" does not exist", projectId.toHexString() ), e );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.DeleteProject, projectId, true );
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
			api.deleteProject( projectId );
		}
		catch( final InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error deleting project!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return project.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Exports the specified project",
		request = RestApiRequestInfo.Request.ExportProject,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Parameters.ProjectId, ApiStrings.RestApiStrings.Export },
		isDownload = true,
		downloadFilename = "ScenarioExplorerBackup.json",
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ProjectBackup )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project to export",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleExportProject( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );

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

		final JSONObject backup;
		try
		{
			backup = ProjectBackup.exportProject( api, projectId );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new NotFoundException( String.format( "Project \"%s\" does not exist", projectId.toHexString() ), e );
		}

		/////////////// return the response ///////////////

		return backup;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Imports the specified project",
		request = RestApiRequestInfo.Request.ImportProject,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Import },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = "project",
			description = "The project to import",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.ProjectBackup ) } )
	public static JSONObject handleImportProject( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.NewProject, true );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden!", e );
		}

		/////////////// handle the action ///////////////

		final Project importedProject;
		try
		{
			importedProject = ProjectBackup.importProject( api, body, requestUser, true, true, true, true );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating project!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return importedProject.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Clones the specified project",
		request = RestApiRequestInfo.Request.CloneProject,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Parameters.ProjectId, ApiStrings.RestApiStrings.Clone },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project to clone",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleCloneProject( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId sourceProjectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetProject, sourceProjectId, true );
			api.verifyAuthorization( requestUser, API.Authorization.NewProject, true );
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

		final Project clonedProject;
		try
		{
			clonedProject = api.cloneProject( sourceProjectId, requestUser );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating project!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return clonedProject.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// helper methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the project from the request body
	 *
	 * @param body the request body to parse
	 *
	 * @return the parsed project
	 */
	private static Project parseBody( final JSONObject body ) throws BadRequestException
	{
		try
		{
			return new Project( body );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad request!", e );
		}
	}
}
