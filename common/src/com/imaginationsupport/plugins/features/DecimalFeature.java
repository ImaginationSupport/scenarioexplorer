package com.imaginationsupport.plugins.features;

import com.imaginationsupport.data.Persistent;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.FeatureType;
import org.json.JSONObject;

import java.util.Random;

public class DecimalFeature extends FeatureType
{
	public static class JsonKeys extends FeatureType.JsonKeys
	{
		private static final String ConfigMin = "min";
		private static final String ConfigMax = "max";
		private static final String ConfigNumDecimalPlaces = "numDecimalPlaces";
	}

	public static final String NAME = "Decimal";

	public DecimalFeature()
	{
		super();

		try
		{
			setDefaultValue( Double.toString( 0 ) );
			setNumDecimalPlaces( 2 );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			// ignore
		}

		return;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getHelpText() {
		return "";
	}

	@Override
	public String getAboutText() {
		return "This feature holds a decimal value such as 3.14.";
	}

	@Override
	public String userToStorage(String value) throws GeneralScenarioExplorerException
	{
		double d = 0.0;
		try {
			d= Double.parseDouble(value);
		} catch (NumberFormatException e){
			throw new GeneralScenarioExplorerException("Value ("+value+") must be an decimal format.");
		}
		return d+"";
	}

	@Override
	public String storageToUser(String value) {
		return value;
	}

	@Override
	public String getRandomValue() {
		Random rand=new Random();
		double v=rand.nextDouble();
		return v+"";
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/feature-types/decimalFeatureType.js";
	}

	public double getMin() throws InvalidDataException
	{
		return JsonHelper.getOptionalParameterDouble( this.getConfig(), JsonKeys.ConfigMin, Double.MIN_VALUE );
	}

	public double getMax() throws InvalidDataException
	{
		return JsonHelper.getOptionalParameterDouble( this.getConfig(), JsonKeys.ConfigMin, Double.MIN_VALUE );
	}

	public int getNumDecimalPlaces() throws InvalidDataException
	{
		return JsonHelper.getOptionalParameterInt( this.getConfig(), JsonKeys.ConfigNumDecimalPlaces, 2 );
	}

	public void setMin( final double newMin ) throws InvalidDataException
	{
		final JSONObject config = getConfig();

		JsonHelper.put( config, JsonKeys.ConfigMin, newMin );

		this.setConfig( config );

		return;
	}

	public void setMax( final double newMax ) throws InvalidDataException
	{
		final JSONObject config = getConfig();

		JsonHelper.put( config, JsonKeys.ConfigMax, newMax );

		this.setConfig( config );

		return;
	}

	public void setNumDecimalPlaces( final int newNumDecimalPlaces ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( newNumDecimalPlaces < 0 || newNumDecimalPlaces > 8 )
		{
			throw new GeneralScenarioExplorerException( String.format( "Invalid number of decimal places: %d", newNumDecimalPlaces ) );
		}

		final JSONObject config = getConfig();

		JsonHelper.put( config, JsonKeys.ConfigNumDecimalPlaces, newNumDecimalPlaces );

		this.setConfig( config );

		return;
	}

	public boolean isContinuousVariable(){
		return true;
	}
}
