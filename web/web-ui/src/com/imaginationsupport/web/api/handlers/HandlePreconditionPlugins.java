package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.User;
import com.imaginationsupport.data.api.Plugin;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Precondition;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.exceptions.BadRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.SortedSet;

@SuppressWarnings( { "unused", "Duplicates" } )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.Plugin )
public class HandlePreconditionPlugins extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of precondition plugins",
		request = RestApiRequestInfo.Request.ListPreconditions,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Precondition },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Precondition )
	public static JSONArray listPreconditionPlugins( final API api, final User requestUser )
	{
		/////////////// parse the request ///////////////

		// no parameters in request

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final SortedSet< Precondition > plugins = api.getPreconditions();

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
		summary = "Gets the specified precondition plugin",
		request = RestApiRequestInfo.Request.GetPrecondition,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Precondition, ApiStrings.RestApiStrings.Parameters.PreconditionId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.Precondition )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.PreconditionId,
			description = "The unique id of the precondition plugin",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject getPreconditionPlugin(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters ) throws BadRequestException, InvalidDataException
	{
		/////////////// parse the request ///////////////

		final String preconditionId = getStringParameter( uriParameters, ApiStrings.JsonKeys.PreconditionId );

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final Precondition precondition = api.findPrecondition( preconditionId, true );

		/////////////// return the response ///////////////

		return precondition.getPluginDefinition();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified precondition plugin javascript source code",
		request = RestApiRequestInfo.Request.GetPreconditionSource,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Precondition, ApiStrings.RestApiStrings.Parameters.PreconditionId, ApiStrings.RestApiStrings.Src },
		responseSchemaType = RestApiRequestInfo.SchemaType.JavascriptSource,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.PluginSource )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.PreconditionId,
			description = "The unique id of the precondition plugin",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static String getPreconditionPluginSrc(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters ) throws BadRequestException, InvalidDataException
	{
		/////////////// parse the request ///////////////

		final String preconditionId = getStringParameter( uriParameters, ApiStrings.JsonKeys.PreconditionId );

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final Precondition precondition = api.findPrecondition( preconditionId, true );

		/////////////// return the response ///////////////

		return precondition.getPluginJavaScriptSourceUriPath();
	}
}
