package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
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

import java.util.Collection;
import java.util.Map;

@SuppressWarnings( { "unused", "Duplicates" } )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.Feature )
public class HandleFeatures extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of features for the specified project",
		request = RestApiRequestInfo.Request.ListFeatures,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project, ApiStrings.RestApiStrings.Parameters.ProjectId, ApiStrings.RestApiStrings.Feature },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Feature )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONArray listFeatures( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.ListFeatures, projectId, true );
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

		final Collection< FeatureMap > features;
		try
		{
			features = api.findProject( projectId, true ).getFeatureMaps();
		}
		catch( final InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error getting project features!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return JsonHelper.toJSONArray( features );
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error running request!", e );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified feature",
		request = RestApiRequestInfo.Request.GetFeature,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.Feature,
			ApiStrings.RestApiStrings.Parameters.FeatureId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Feature )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.FeatureId,
			description = "The unique id of the feature to get",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject handleGetFeature( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final String featureId = getStringParameter( uriParameters, ApiStrings.JsonKeys.FeatureId );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.GetFeature, projectId, featureId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to get the specified feature!", e );
		}

		/////////////// handle the action ///////////////

		final FeatureMap feature;
		try
		{
			feature = api.findFeature( projectId, featureId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Feature \"%s\" does not exist", featureId ), e );
		}

		/////////////// return the response ///////////////

		return feature.toJSON();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Creates the specified feature",
		request = RestApiRequestInfo.Request.NewFeature,
		method = RestApiRequestInfo.HttpMethod.Post,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.Feature },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Feature )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "feature",
			description = "The feature to create",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.Project )	} )
	public static JSONObject handleNewFeature( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final FeatureMap featureFromRequest = parseBody( body );

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.NewFeature, projectId, true );
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

		final FeatureMap newFeatureMap;
		try
		{
			newFeatureMap = api.addFeature( projectId, featureFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error creating feature!", e );
		}

		/////////////// return the response ///////////////

		return newFeatureMap.toJSON();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Updates the specified feature",
		request = RestApiRequestInfo.Request.UpdateFeature,
		method = RestApiRequestInfo.HttpMethod.Put,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.Feature,
			ApiStrings.RestApiStrings.Parameters.FeatureId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Feature )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.FeatureId,
			description = "The unique id of the feature to update",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = "feature",
			description = "The feature to update",
			in = RestApiRequestParameterInfo.InLocations.FormData,
			type = RestApiRequestParameterInfo.ParameterType.Feature )	} )
	public static JSONObject handleUpdateFeature( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final String featureId = getStringParameter( uriParameters, ApiStrings.JsonKeys.FeatureId );
		final FeatureMap featureFromRequest = parseBody( body );

		// make sure the id in the request is the same as the body
		if( !featureId.equals( featureFromRequest.getUid() ) )
		{
			throw new BadRequestException(
				String.format(
					"Feature id in URI \"%s\" does not match the request body \"%s\"",
					featureId,
					featureFromRequest.getUid() ) );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.UpdateFeature, projectId, featureId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to update specified feature!", e );
		}

		/////////////// handle the action ///////////////

		final FeatureMap updatedFeature;
		try
		{
			updatedFeature = api.updateFeature( projectId, featureFromRequest );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new ForbiddenException( "Error updating feature!", e );
		}

		/////////////// return the response ///////////////

		return updatedFeature.toJSON();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Deletes the specified feature",
		request = RestApiRequestInfo.Request.DeleteFeature,
		method = RestApiRequestInfo.HttpMethod.Delete,
		uriParts = { ApiStrings.RestApiStrings.Project,
			ApiStrings.RestApiStrings.Parameters.ProjectId,
			ApiStrings.RestApiStrings.Feature,
			ApiStrings.RestApiStrings.Parameters.FeatureId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Project )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectId,
			description = "The unique id of the project",
			in = RestApiRequestParameterInfo.InLocations.Path ),
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.FeatureId,
			description = "The unique id of the feature to delete",
			in = RestApiRequestParameterInfo.InLocations.Path )	} )
	public static JSONObject handleDeleteFeature( final API api, final User requestUser, final Map< String, String > uriParameters ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final ObjectId projectId = getObjectIdParameter( uriParameters, ApiStrings.JsonKeys.ProjectId );
		final String featureId = getStringParameter( uriParameters, ApiStrings.JsonKeys.FeatureId );

		// now verify that this feature actually exists
		final FeatureMap feature;
		try
		{
			feature = api.findFeature( projectId, featureId, true );
		}
		catch( final InvalidDataException e )
		{
			throw new NotFoundException( String.format( "Feature \"%s\" does not exist", featureId ), e );
		}

		/////////////// verify authorization ///////////////

		try
		{
			api.verifyAuthorization( requestUser, API.Authorization.DeleteFeature, projectId, featureId, true );
		}
//		catch( final InvalidDataException e )
//		{
//			throw new BadRequestException( "Bad Request!", e );
//		}
		catch( final NotAuthorizedException e )
		{
			throw new ForbiddenException( "User forbidden to delete the specified feature!", e );
		}

		/////////////// handle the action ///////////////

		try
		{
			api.deleteFeature( projectId, featureId );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error deleting feature!", e );
		}

		/////////////// return the response ///////////////

		return feature.toJSON();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// helper methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the feature from the request body
	 *
	 * @param body the request body to parse
	 *
	 * @return the parsed feature
	 */
	private static FeatureMap parseBody( final JSONObject body ) throws BadRequestException
	{
		try
		{
			return new FeatureMap( body );
		}
		catch( final InvalidDataException e )
		{
			throw new BadRequestException( "Bad request!", e );
		}
	}
}
