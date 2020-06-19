package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.User;
import com.imaginationsupport.data.api.Plugin;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.FeatureType;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.exceptions.BadRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.SortedSet;

@SuppressWarnings( { "unused", "Duplicates" } )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.Plugin )
public class HandleFeatureTypePlugins extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of feature type plugins",
		request = RestApiRequestInfo.Request.ListFeatureTypes,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.FeatureType },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.FeatureType )
	public static JSONArray listFeatureTypePlugins( final API api, final User requestUser )
	{
		/////////////// parse the request ///////////////

		// no parameters in request

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final SortedSet< FeatureType > plugins = api.getFeatureTypes();

		/////////////// return the response ///////////////

		final JSONArray pluginDefinitions = new JSONArray();
		for( final Plugin plugin : plugins )
		{
			pluginDefinitions.put( plugin.getPluginDefinition() );
		}

		return pluginDefinitions;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified feature type plugin",
		request = RestApiRequestInfo.Request.GetFeatureType,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.FeatureType, ApiStrings.RestApiStrings.Parameters.FeatureTypeId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.FeatureType )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.FeatureTypeId,
			description = "The unique id of the feature type plugin",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject getFeatureTypePlugin(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters ) throws BadRequestException, InvalidDataException
	{
		/////////////// parse the request ///////////////

		final String featureTypeId = getStringParameter( uriParameters, ApiStrings.JsonKeys.FeatureTypeId );

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final FeatureType featureType = api.findFeatureType( featureTypeId, true );

		/////////////// return the response ///////////////

		return featureType.getPluginDefinition();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified feature type plugin javascript source code",
		request = RestApiRequestInfo.Request.GetFeatureTypeSource,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.FeatureType, ApiStrings.RestApiStrings.Parameters.FeatureTypeId, ApiStrings.RestApiStrings.Src },
		responseSchemaType = RestApiRequestInfo.SchemaType.JavascriptSource,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.PluginSource )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.FeatureTypeId,
			description = "The unique id of the feature type plugin",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static String getFeatureTypePluginSrc(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters ) throws BadRequestException, InvalidDataException
	{
		/////////////// parse the request ///////////////

		final String featureTypeId = getStringParameter( uriParameters, ApiStrings.JsonKeys.FeatureTypeId );

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final FeatureType featureType = api.findFeatureType( featureTypeId, true );

		/////////////// return the response ///////////////

		return featureType.getPluginJavaScriptSourceUriPath();
	}
}
