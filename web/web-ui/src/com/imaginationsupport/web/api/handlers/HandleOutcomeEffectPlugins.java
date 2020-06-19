package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.User;
import com.imaginationsupport.data.api.Plugin;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Effect;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.exceptions.BadRequestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.SortedSet;

@SuppressWarnings( { "unused", "Duplicates" } )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.Plugin )
public class HandleOutcomeEffectPlugins extends RestApiRequestHandlerBase
{
	@RestApiRequestInfo(
		summary = "Gets the list of outcome effect plugins",
		request = RestApiRequestInfo.Request.ListOutcomeEffects,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.OutcomeEffect },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonArray,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.OutcomeEffect )
	public static JSONArray listOutcomeEffectPlugins( final API api, final User requestUser )
	{
		/////////////// parse the request ///////////////

		// no parameters in request

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final SortedSet< Effect > plugins = api.getOutcomeEffects();

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
		summary = "Gets the specified outcome effect plugin",
		request = RestApiRequestInfo.Request.GetOutcomeEffect,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.OutcomeEffect, ApiStrings.RestApiStrings.Parameters.OutcomeEffectId },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.OutcomeEffect )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.OutcomeEffectId,
			description = "The unique id of the outcome effect plugin",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static JSONObject getOutcomeEffectPlugin(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters ) throws BadRequestException, InvalidDataException
	{
		/////////////// parse the request ///////////////

		final String outcomeEffectId = getStringParameter( uriParameters, ApiStrings.JsonKeys.OutcomeEffectId );

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final Effect outcomeEffect = api.findOutcomeEffect( outcomeEffectId, true );

		/////////////// return the response ///////////////

		return outcomeEffect.getPluginDefinition();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@RestApiRequestInfo(
		summary = "Gets the specified outcome effect plugin javascript source code",
		request = RestApiRequestInfo.Request.GetOutcomeEffectSource,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.OutcomeEffect, ApiStrings.RestApiStrings.Parameters.OutcomeEffectId, ApiStrings.RestApiStrings.Src },
		responseSchemaType = RestApiRequestInfo.SchemaType.JavascriptSource,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.PluginSource )
	@RestApiRequestParameters( {
		@RestApiRequestParameterInfo(
			name = ApiStrings.JsonKeys.OutcomeEffectId,
			description = "The unique id of the outcome effect plugin",
			in = RestApiRequestParameterInfo.InLocations.Path ) } )
	public static String getOutcomeEffectPluginSrc(
		final API api,
		final User requestUser,
		final Map< String, String > uriParameters ) throws BadRequestException, InvalidDataException
	{
		/////////////// parse the request ///////////////

		final String outcomeEffectId = getStringParameter( uriParameters, ApiStrings.JsonKeys.OutcomeEffectId );

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		final Effect outcomeEffect = api.findOutcomeEffect( outcomeEffectId, true );

		/////////////// return the response ///////////////

		return outcomeEffect.getPluginJavaScriptSourceUriPath();
	}
}
