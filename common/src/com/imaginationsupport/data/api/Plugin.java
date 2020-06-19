package com.imaginationsupport.data.api;

import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.Persistent;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.json.JSONObject;

@RestApiObjectInfo(
	definitionName = "Plugin",
	tagName = RestApiHandlerInfo.CategoryNames.Plugin,
	description = "Scenario Explorer Plugin" )
@RestApiRequestObjectPseudoFields( {
	@RestApiRequestObjectPseudoFieldInfo(
		name = Plugin.JsonKeys.Id,
		description = "The plugin unique id",
		rawType = String.class ),
//	@RestApiRequestObjectPseudoFieldInfo(
//		name = Plugin.JSON_KEY_CONFIG,
//		description = "The plugin config JSON",
//		rawType = JSONObject.class ),
} )
public abstract class Plugin implements ApiObject
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys
	{
		public static final String Id = "id";
		public static final String Config = "config";
	}

	private String config = "{}";

	public String getId()
	{
		return this.getClass().getCanonicalName();
	}

	/**
	 * Gets the configuration data
	 *
	 * @return configuration data
	 */
	public JSONObject getConfig() throws InvalidDataException
	{
		return JsonHelper.parseObject( config );
	}

	/**
	 * Sets the configuration data
	 */
	public void setConfig( final JSONObject newConfig )
	{
		this.config = newConfig.toString();

		return;
	}
	
	public abstract String getPluginJavaScriptSourceUriPath();

	public JSONObject getPluginDefinition()
	{
		final JSONObject json = new JSONObject();

		JsonHelper.put( json, JsonKeys.Id, this.getId() );

		return json;
	}

	@Override
	public JSONObject toJSON() throws InvalidDataException
	{
		final JSONObject json = getPluginDefinition();

		JsonHelper.put( json, JsonKeys.Config, this.getConfig() );

		return json;
	}
}
