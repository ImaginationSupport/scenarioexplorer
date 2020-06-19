package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.User;
import com.imaginationsupport.data.api.Plugin;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Projector;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.exceptions.BadRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.SortedSet;

@SuppressWarnings( { "unused", "Duplicates" } )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.Plugin )
public class HandleProjectorPlugins extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of projector plugins",
		request = RestApiRequestInfo.Request.ListProjectors,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Projector },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Projector )
	public static JSONArray listProjectorPlugins( final API api, final User requestUser )
	{
		/////////////// parse the request ///////////////

		// no parameters in request

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final SortedSet< Projector > plugins = api.getProjectors();

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
		summary = "Gets the specified projector plugin",
		request = RestApiRequestInfo.Request.GetProjector,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Projector, ApiStrings.RestApiStrings.Parameters.ProjectorId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Projector )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectorId,
			description = "The unique id of the projector plugin",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject getProjectorPlugin(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters ) throws BadRequestException, InvalidDataException
	{
		/////////////// parse the request ///////////////

		final String projectorId = getStringParameter( uriParameters, ApiStrings.JsonKeys.ProjectorId );

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final Projector projector = api.findProjector( projectorId, true );

		/////////////// return the response ///////////////

		return projector.getPluginDefinition();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified projector plugin javascript source code",
		request = RestApiRequestInfo.Request.GetProjectorSource,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Projector, ApiStrings.RestApiStrings.Parameters.ProjectorId, ApiStrings.RestApiStrings.Src },
		responseSchemaType = RestApiRequestInfo.SchemaType.JavascriptSource,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.PluginSource )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.ProjectorId,
			description = "The unique id of the projector plugin",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static String getProjectorPluginSrc(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters ) throws BadRequestException, InvalidDataException
	{
		/////////////// parse the request ///////////////

		final String projectorId = getStringParameter( uriParameters, ApiStrings.JsonKeys.ProjectorId );

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final Projector projector = api.findProjector( projectorId, true );

		/////////////// return the response ///////////////

		return projector.getPluginJavaScriptSourceUriPath();
	}
}
