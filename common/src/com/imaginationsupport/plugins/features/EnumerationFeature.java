package com.imaginationsupport.plugins.features;

import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.FeatureType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class EnumerationFeature extends FeatureType
{
	public static class JsonKeys extends FeatureType.JsonKeys
	{
		private static final String ConfigChoices = "choices";
		private static final String ConfigChoiceLabel = "name";
		private static final String ConfigChoiceValue = "value";
	}

	public static final String NAME="Multiple Choice";

	public static class EnumerationChoice
	{
		public String label;
		public String value;

		EnumerationChoice( final String label, final String value )
		{
			this.label = label;
			this.value = value;
		}
	}

	private abstract static class Notifications
	{
		public static final String NO_CHOICES = "no-choices";
	}

	public EnumerationFeature()
	{
		final JSONObject config = new JSONObject();

		JsonHelper.put( config, JsonKeys.ConfigDefaultValue, "" );
		JsonHelper.put( config, JsonKeys.ConfigChoices, new JSONArray() );

		setConfig( config );

		return;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getHelpText() {
		return "This feature holds one of several options.";
	}

	@Override
	public String getAboutText() {
		return "";
	}

	@Override
	public String userToStorage(String value) {
		return value;
	}

	@Override
	public String storageToUser(String value) {
		return value;
	}

	@Override
	public String getRandomValue()
	{
		final Random rand = new Random();

		try
		{
			final List< EnumerationChoice > choices = getConfigChoices();
			if( choices != null && choices.size() > 0 )
			{
				return choices.get( rand.nextInt( choices.size() ) ).value;
			}
		}
		catch( final InvalidDataException e )
		{
			return "";
		}

		return "";
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/feature-types/enumerationFeatureType.js";
	}

	public List< EnumerationChoice > getConfigChoices() throws InvalidDataException
	{
		final JSONArray choicesParsed = JsonHelper.getRequiredParameterJSONArray( this.getConfig(), "choices" );

		final List< EnumerationChoice > choices = new ArrayList<>();
		for( int i = 0; i < choicesParsed.length(); ++i )
		{
			final JSONObject parsedChoice = choicesParsed.getJSONObject( i );
			choices.add(
				new EnumerationChoice(
					JsonHelper.getRequiredParameterString( parsedChoice, JsonKeys.ConfigChoiceLabel ),
					JsonHelper.getRequiredParameterString( parsedChoice, JsonKeys.ConfigChoiceValue )
				) );
		}

		return choices;
	}

	public void setConfigChoices( final List< EnumerationChoice > choices ) throws InvalidDataException
	{
		final JSONArray newChoices = new JSONArray();
		for( final EnumerationChoice choice : choices )
		{
			final JSONObject newChoice = new JSONObject();
			newChoice.put( JsonKeys.ConfigChoiceLabel, choice.label );
			newChoice.put( JsonKeys.ConfigChoiceValue, choice.value );

			newChoices.put( newChoice );
		}

		final JSONObject config = this.getConfig();

		config.put( JsonKeys.ConfigChoices, newChoices );

		this.setConfig( config );

		return;
	}

	public void addConfigChoice( final String label, final String value ) throws InvalidDataException
	{
		final List< EnumerationChoice > choices = this.getConfigChoices();

		choices.add( new EnumerationChoice( label, value ) );

		this.setConfigChoices( choices );

		return;
	}

	public boolean isContinuousVariable()
	{
		return false;
	}

	@Override
	public Set< Notification > generateNotifications() throws InvalidDataException
	{
		final Set< Notification > notifications = new HashSet<>();

		if( getConfigChoices().isEmpty() )
		{
			notifications.add( new Notification(
				Notification.Scope.Feature,
				Notifications.NO_CHOICES,
				String.format( "%s feature \"%s\" does not have any choices.", NAME, getName() ),
				null,
				null,
				null,
				getId()
			) );
		}

		return notifications;
	}
}
