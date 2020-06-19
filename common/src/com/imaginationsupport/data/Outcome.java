package com.imaginationsupport.data;

import com.imaginationsupport.API;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.data.api.NotificationSource;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.Effect;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Outcome implements ApiObject, NotificationSource
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys
	{
		public static final String Label = "name";
		public static final String Likelihood = "likelihood";
		public static final String Effects = "effects";
	}

	private String label;
	private String description;
	private double likelihood = 0.0;
	
	@Embedded
	private List<Effect> effects=new ArrayList<>();
	
	public Outcome(){}
	
	public Outcome(String label, String description, double likelihood){
		this.label=label;
		this.description=description;
		this.likelihood=likelihood;
	}

	public Outcome( final JSONObject source ) throws InvalidDataException
	{
		this.label = JsonHelper.getRequiredParameterString( source, JsonKeys.Label );
		this.description = ""; // TODO not used
		this.likelihood = JsonHelper.getRequiredParameterDouble( source, JsonKeys.Likelihood );

		final JSONArray effectsRaw = JsonHelper.getRequiredParameterJSONArray( source, JsonKeys.Effects );
		for( int i = 0; i < effectsRaw.length(); ++i )
		{
			this.effects.add( API.deserializeEffect( effectsRaw.getJSONObject( i ) ) );
		}

		return;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String newLabel) {
		this.label = newLabel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLikelihood() {
		return likelihood;
	}

	public List<Effect> getEffects(){
		return effects;
	}
	
	public void addEffect(Effect effect){
		effects.add(effect);
	}

	@Override
	public JSONObject toJSON() throws InvalidDataException, GeneralScenarioExplorerException
	{
		final JSONObject json = new JSONObject();

		JsonHelper.put( json, JsonKeys.Label, this.label );
		JsonHelper.put( json, JsonKeys.Likelihood, this.likelihood );

		JsonHelper.put( json, JsonKeys.Effects, this.effects );

		return json;
	}

	@Override
	public Set< Notification > generateNotifications() throws InvalidDataException, GeneralScenarioExplorerException
	{
		final Set< Notification > notifications = new HashSet<>();

		for( final Effect effect : effects )
		{
			notifications.addAll( effect.generateNotifications() );
		}

		return notifications;
	}
}