package com.imaginationsupport.plugins.effects;

import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.Feature;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.Effect;
import org.json.JSONObject;

public class FeatureSetEffect extends Effect
{
	private static final String JSON_KEY_FEATURE_ID = "feature";
	private static final String JSON_KEY_FEATURE_VALUE = "value";

//	public String feature=null;
//	public String value=null;
	
	public FeatureSetEffect() {

		final JSONObject configJSON = new JSONObject();

		JsonHelper.put( configJSON, JSON_KEY_FEATURE_ID, (String)null );
		JsonHelper.put( configJSON, JSON_KEY_FEATURE_VALUE, (String)null );

		setConfig( configJSON );

		return;
	}

	public FeatureSetEffect(FeatureMap map, String value) throws GeneralScenarioExplorerException
	{
//		this.feature=map.getUid();
//		this.value=map.getType().userToStorage(value);

		final JSONObject configJSON = new JSONObject();

		JsonHelper.put( configJSON, JSON_KEY_FEATURE_ID, map.getUid() );
		JsonHelper.put( configJSON, JSON_KEY_FEATURE_VALUE, map.getType().userToStorage( value ) );

		setConfig( configJSON );

		return;
	}

	public String getFeature() throws GeneralScenarioExplorerException
	{
		try
		{
			return JsonHelper.getRequiredParameterString( getConfig(), JSON_KEY_FEATURE_ID );
		}
		catch( final InvalidDataException e )
		{
			throw new GeneralScenarioExplorerException( "Error parsing config!", e );
		}
	}

	public String getValue() throws GeneralScenarioExplorerException
	{
		try
		{
			return JsonHelper.getRequiredParameterString( getConfig(), JSON_KEY_FEATURE_VALUE );
		}
		catch( final InvalidDataException e )
		{
			throw new GeneralScenarioExplorerException( "Error parsing config!", e );
		}
	}

	@Override
	public String getLabel() {
		return "Set Feature";
	}

	@Override
	public String getDescription() {
		return "This effect sets the value for a specific feature.";
	}

	@Override
	public void apply(State state) throws DatastoreException, GeneralScenarioExplorerException
	{
		final Feature f=state.getFeature(getFeature());
		f.setStorageReadyValue(getValue());
		state.updateFeature(f);
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/outcome-effects/featureSetOutcomeEffect.js";
	}
}
