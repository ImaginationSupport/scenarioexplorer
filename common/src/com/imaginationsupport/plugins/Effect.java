package com.imaginationsupport.plugins;

import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.data.api.NotificationSource;
import com.imaginationsupport.data.api.Plugin;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.helpers.JsonHelper;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;

@RestApiObjectInfo(
	definitionName = "OutcomeEffect",
	tagName = RestApiHandlerInfo.CategoryNames.Plugin,
	description = "Scenario Explorer Conditioning Event Outcome Effect" )
@RestApiRequestObjectPseudoFields( {
	@RestApiRequestObjectPseudoFieldInfo(
		name = Effect.JsonKeys.Label,
		description = "The name of the outcome effect",
		rawType = String.class ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = Effect.JsonKeys.Description,
		description = "The description of the outcome effect",
		rawType = String.class )
} )
public abstract class Effect extends Plugin implements NotificationSource, Comparable< Effect >
{
	public static class JsonKeys extends Plugin.JsonKeys
	{
		public static final String Label = "name";
		public static final String Description = "description";
	}

	public abstract String getLabel();

	public abstract String getDescription();

	public abstract void apply( State state ) throws DatastoreException, GeneralScenarioExplorerException;

	@SuppressWarnings( "Duplicates" )
	@Override
	public JSONObject getPluginDefinition()
	{
		final JSONObject json = super.getPluginDefinition();

		JsonHelper.put( json, JsonKeys.Label, this.getLabel() );
		JsonHelper.put( json, JsonKeys.Description, this.getDescription() );

		return json;
	}

	@Override
	public Set< Notification > generateNotifications()
	{
		return Collections.emptySet();
	}

	@Override
	public int compareTo( final Effect other )
	{
		return this.getLabel().equals( other.getLabel() )
			? this.getId().compareTo( other.getId() )
			: this.getLabel().compareTo( other.getLabel() );
	}
}
