package com.imaginationsupport.plugins;

import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.Persistent;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.data.api.NotificationSource;
import com.imaginationsupport.data.api.Plugin;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;

@RestApiObjectInfo(
	definitionName = "Precondition",
	tagName = RestApiHandlerInfo.CategoryNames.Plugin,
	description = "Scenario Explorer Conditioning Event Precondition" )
@RestApiRequestObjectPseudoFields( {
	@RestApiRequestObjectPseudoFieldInfo(
		name = Precondition.JsonKeys.Label,
		description = "The name of the precondition",
		rawType = String.class ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = Precondition.JsonKeys.Description,
		description = "The description of the precondition",
		rawType = String.class )
} )
public abstract class Precondition extends Plugin implements NotificationSource, Comparable< Precondition >
{
	public static class JsonKeys extends Plugin.JsonKeys
	{
		public static final String Label = "name";
		public static final String Description = "description";
	}

	public abstract String getLabel();

	public abstract String getDescription();

	public abstract boolean satisfied( State state ) throws InvalidDataException, GeneralScenarioExplorerException;

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
	public int compareTo( final Precondition other )
	{
		return this.getLabel().equals(other.getLabel())
				? this.getId().compareTo(other.getId())
				: this.getLabel().compareTo( other.getLabel() );
	}
}
