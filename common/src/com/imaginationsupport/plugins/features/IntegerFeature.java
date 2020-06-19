package com.imaginationsupport.plugins.features;

import com.imaginationsupport.data.Persistent;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.FeatureType;
import org.json.JSONObject;

import java.util.Random;

public class IntegerFeature extends FeatureType
{
	public static class JsonKeys extends FeatureType.JsonKeys
	{
		private static final String ConfigMin = "min";
		private static final String ConfigMax = "max";
	}

	public static final String NAME="Integer";

	public IntegerFeature()
	{
		super();

		try
		{
			setDefaultValue( Integer.toString( 0 ) );
		}
		catch( final InvalidDataException e )
		{
			// ignore
		}

		return;
	}

	@Override
	public String userToStorage(String value) throws GeneralScenarioExplorerException
	{
		int i=0;
		try {
			i=Integer.parseInt(value);
		} catch (NumberFormatException e){
			throw new GeneralScenarioExplorerException("Value ("+value+") must be an integer format.");
		}
		return i+"";
	}

	@Override
	public String storageToUser(String value) {
		return value;
	}

	@Override
	public String getHelpText() {
		return "This feature holds an integer value, such as 42.";
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getAboutText() {
		return "Default";
	}

	@Override
	public String getRandomValue() {
		Random rand=new Random();
		int v=rand.nextInt(999);
		return v+"";
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/feature-types/integerFeatureType.js";
	}

	public int getMin() throws InvalidDataException
	{
		return JsonHelper.getOptionalParameterInt( this.getConfig(), JsonKeys.ConfigMin, Integer.MIN_VALUE );
	}

	public int getMax() throws InvalidDataException
	{
		return JsonHelper.getOptionalParameterInt( this.getConfig(), JsonKeys.ConfigMin, Integer.MIN_VALUE );
	}

	public void setDefaultValue( final int newDefaultValue ) throws InvalidDataException
	{
		final JSONObject config = getConfig();

		JsonHelper.put( config, JsonKeys.ConfigDefaultValue, newDefaultValue );

		this.setConfig( config );
	}

	public void setMin( final int newMin ) throws InvalidDataException
	{
		final JSONObject config = getConfig();

		JsonHelper.put( config, JsonKeys.ConfigMin, newMin );

		this.setConfig( config );

		return;
	}

	public void setMax( final int newMax ) throws InvalidDataException
	{
		final JSONObject config = getConfig();

		JsonHelper.put( config, JsonKeys.ConfigMax, newMax );

		this.setConfig( config );

		return;
	}

	public boolean isContinuousVariable(){
		return true;
	}
}
