package com.imaginationsupport.plugins;

import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.Persistent;
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
	definitionName = "FeatureType",
	tagName = RestApiHandlerInfo.CategoryNames.Plugin,
	description = "Scenario Explorer Feature Type" )
@RestApiRequestObjectPseudoFields( {
	@RestApiRequestObjectPseudoFieldInfo(
		name = FeatureType.JsonKeys.Name,
		description = "The name of the feature type",
		rawType = String.class ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = FeatureType.JsonKeys.IsContinuous,
		description = "True if this is a continuous variable, otherwise false",
		rawType = Boolean.class ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = FeatureType.JsonKeys.HelpText,
		description = "The help text",
		rawType = String.class ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = FeatureType.JsonKeys.AboutText,
		description = "The about text",
		rawType = String.class )
} )
public abstract class FeatureType extends Plugin implements NotificationSource, Comparable< FeatureType >
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys extends Persistent.JsonKeys
	{
		public static final String ConfigDefaultValue = "defaultValue";
		public static final String Name = "name";
		public static final String HelpText = "helpText";
		public static final String AboutText = "aboutText";
		public static final String IsContinuous = "isContinuous";
	}

	protected FeatureType()
	{
		super();

		return;
	}

	/**
	 * Each Feature Type needs a unique user-focused name by which it can be identified.
	 * @return name of feature type
	 */
	public abstract String getName();

	/**
	 * Each Feature Type must provide a <200 character user-focused help description.
	 * @return help of feature type
	 */
	public abstract String getHelpText();

	/**
	 * Each Feature Type must provide a <200 character user-focused description and source attribution.
	 * @return about of feature type
	 */
	public abstract String getAboutText();

	/**
	 * Translates an input String to a storage String, while checking values.
	 * This is used in case the data storage requires a different format than the GUI.
	 * @return the storage value as string
	 */
	public abstract String userToStorage(String value) throws GeneralScenarioExplorerException;

	/**
	 * Translates an storage String back to an output String.
	 * This is used in case the data storage requires a different format than the GUI.
	 * @return value
	 */
	public abstract String storageToUser(String value);

	public String getDefaultValue() throws InvalidDataException
	{
		return JsonHelper.getRequiredParameterString( getConfig(), JsonKeys.ConfigDefaultValue );
	}

	public void setDefaultValue( final String newDefaultValue ) throws InvalidDataException
	{
		setConfig( JsonHelper.put( getConfig(), JsonKeys.ConfigDefaultValue, newDefaultValue ) );

		return;
	}

	/**
	 * Gets a Random input value of the feature as a string.
	 * @return a random value
	 */
	public abstract String getRandomValue();

	@SuppressWarnings( "Duplicates" )
	@Override
	public JSONObject getPluginDefinition()
	{
		final JSONObject json = super.getPluginDefinition();

		JsonHelper.put( json, JsonKeys.Name, this.getName() );
		JsonHelper.put( json, JsonKeys.AboutText, this.getAboutText() );
		JsonHelper.put( json, JsonKeys.HelpText, this.getHelpText() );
		JsonHelper.put( json, JsonKeys.IsContinuous, this.isContinuousVariable() );

		return json;
	}

	public abstract boolean isContinuousVariable();

	@Override
	public Set< Notification > generateNotifications() throws InvalidDataException
	{
		return Collections.emptySet();
	}

	@Override
	public int compareTo( final FeatureType other )
	{
		return this.getName().equals( other.getName() )
			? this.getId().compareTo( other.getId() )
			: this.getName().compareTo( other.getName() );
	}
}
