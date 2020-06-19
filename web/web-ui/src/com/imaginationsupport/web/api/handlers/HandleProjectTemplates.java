package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.ProjectTemplate;
import com.imaginationsupport.data.TimelineEvent;
import com.imaginationsupport.data.User;
import com.imaginationsupport.data.features.FeatureMap;
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
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.ProjectTemplate )
public abstract class HandleProjectTemplates extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of project templates",
		request = RestApiRequestInfo.Request.ListProjectTemplates,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.ProjectTemplate },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ProjectTemplate )
	public static JSONArray listProjectTemplates( final API api, final User requestUser ) throws ApiException
	{
		/////////////// parse the request ///////////////

		// no parameters in request

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final SortedSet< ProjectTemplate > projectTemplates = api.getAllProjectTemplates();

		/////////////// return the response ///////////////

		try
		{
			return JsonHelper.toJSONArray( projectTemplates );
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error running request!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified project template",
		request = RestApiRequestInfo.Request.GetProjectTemplate,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.ProjectTemplate, ApiStrings.RestApiStrings.Parameters.ProjectTemplateId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ProjectTemplate )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectTemplateId,
			description = "The unique id of the project template to get",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleGetProjectTemplate( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectTemplateId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectTemplateId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetProjectTemplate, projectTemplateId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to get the specified project template!", e );
		}

		/////////////// handle the action ///////////////

		final ProjectTemplate projectTemplate;
		try
		{
			projectTemplate = api.findProjectTemplate( projectTemplateId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Project Template \"%s\" does not exist", projectTemplateId.toHexString() ), e );
		}

		/////////////// return the response ///////////////

		try
		{
			return projectTemplate.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project template", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Creates the specified project template",
		request = RestApiRequestInfo.Request.NewProjectTemplate,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.ProjectTemplate },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ProjectTemplate )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = "projectTemplate",
			description = "The project template to create",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.ProjectTemplate ) } )
	public static JSONObject handleNewProjectTemplate( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ProjectTemplate projectTemplateFromRequest = parseBody( body );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.NewProjectTemplate, true );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden!", e );
		}

		/////////////// handle the action ///////////////

		final ProjectTemplate newProjectTemplate;
		try
		{
			// the creator of the project template should always be the user making the request
			projectTemplateFromRequest.setCreator( requestUser );

			newProjectTemplate = api.createProjectTemplate( projectTemplateFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating project template!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return newProjectTemplate.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project template!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Updates the specified project template",
		request = RestApiRequestInfo.Request.UpdateProjectTemplate,
		method = RestApiRequestInfo.HttpMethod.Put,
		uriParts = { ApiStrings.RestApiStrings.ProjectTemplate, ApiStrings.RestApiStrings.Parameters.ProjectTemplateId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ProjectTemplate )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectTemplateId,
			description = "The unique id of the project template to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "projectTemplate",
			description = "The project template to update",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.ProjectTemplate ) } )
	public static JSONObject handleUpdateProjectTemplate(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters,
		final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectTemplateId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectTemplateId );

		final ProjectTemplate projectTemplateFromRequest = parseBody( body );

		// make sure the project template id in the request is the same as the body
		if( !projectTemplateId.equals( projectTemplateFromRequest.getId() ) )
		{
			throw new BadRequestException(
				String.format(
					"Project Template id in URI \"%s\" does not match the request body \"%s\"",
					projectTemplateId.toHexString(),
					projectTemplateFromRequest.getId().toHexString() ) );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.UpdateProjectTemplate, projectTemplateId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to update specified project template!", e );
		}

		/////////////// handle the action ///////////////

		final ProjectTemplate updatedProjectTemplate;
		try
		{
			updatedProjectTemplate = api.updateProjectTemplate( projectTemplateFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error updating project template!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return updatedProjectTemplate.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project template", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Deletes the specified project template",
		request = RestApiRequestInfo.Request.DeleteProjectTemplate,
		method = RestApiRequestInfo.HttpMethod.Delete,
		uriParts = { ApiStrings.RestApiStrings.ProjectTemplate, ApiStrings.RestApiStrings.Parameters.ProjectTemplateId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.ProjectTemplate )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectTemplateId,
			description = "The unique id of the project template to delete",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleDeleteProjectTemplate( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectTemplateId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectTemplateId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.DeleteProjectTemplate, projectTemplateId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to get the specified project template!", e );
		}

		/////////////// handle the action ///////////////

		// now verify that this project template actually exists
		final ProjectTemplate projectTemplate;
		try
		{
			projectTemplate = api.findProjectTemplate( projectTemplateId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Project Template \"%s\" does not exist", projectTemplateId.toHexString() ), e );
		}

		try
		{
			api.deleteProjectTemplate( projectTemplateId );
		}
		catch( final InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error deleting project template!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return projectTemplate.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project template", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Creates a project from the specified project template",
		request = RestApiRequestInfo.Request.NewProjectFromTemplate,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.ProjectTemplate, ApiStrings.RestApiStrings.Parameters.ProjectTemplateId, ApiStrings.RestApiStrings.FromTemplate },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectTemplateId,
			description = "The unique id of the project template to use",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleNewProjectFromTemplate(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters,
		final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectTemplateId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectTemplateId );

		// body parameters
		final String projectName;
		final String projectDescription;
		try
		{
			projectName = JsonHelper.getRequiredParameterString( body, ApiStrings.JsonKeys.FromTemplateName );
			projectDescription = JsonHelper.getOptionalParameterString( body, ApiStrings.JsonKeys.FromTemplateDescription, "" );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.NewProject, true );
			api.verifyAuthorization( requestUser, API.Authorization.GetProjectTemplate, projectTemplateId, true );
		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden!", e );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}

		/////////////// handle the action ///////////////

		// now verify that this project template actually exists
		final ProjectTemplate projectTemplate;
		try
		{
			projectTemplate = api.findProjectTemplate( projectTemplateId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Project Template \"%s\" does not exist", projectTemplateId.toHexString() ), e );
		}

		final Project newProject;
		try
		{
			newProject = api.createProject(
				projectName,
				projectDescription,
				projectTemplate.getStart(),
				projectTemplate.getEnd(),
				projectTemplate.getDaysIncrement(),
				requestUser );

			// add all the features
			for( final FeatureMap feature : projectTemplate.getFeatures() )
			{
				api.addFeature( newProject.getId(), feature );
			}

			// add all the timeline events
			for( final TimelineEvent timelineEvent : projectTemplate.getTimelineEvents() )
			{
				api.createTimelineEvent( newProject.getId(), timelineEvent );
			}
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating project template!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return newProject.toJSON();
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing project template!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// helper methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the project template from the request body
	 *
	 * @param body the request body to parse
	 *
	 * @return the parsed project
	 */
	private static ProjectTemplate parseBody( final JSONObject body ) throws BadRequestException
	{
		try
		{
			return new ProjectTemplate( body );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad request!", e );
		}
	}
}
