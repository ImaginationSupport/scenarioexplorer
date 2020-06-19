package com.imaginationsupport.plugins.features;

import com.imaginationsupport.data.Persistent;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.FeatureType;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Random;

public class ProbabilityFeature extends FeatureType
{
	// TODO this should really derive off DecimalFeature...

	public static class JsonKeys extends FeatureType.JsonKeys
	{
		private static final String ConfigNumDecimalPlaces = "numDecimalPlaces";
	}

	private static final DecimalFormat df= new DecimalFormat("0.00##");
	public static final String NAME="Probability";

	public ProbabilityFeature()
	{
		super();

		try
		{
			setDefaultValue( Double.toString( 0.5 ) );
			setNumDecimalPlaces( 2 );
		}
		catch( final InvalidDataException | GeneralScenarioExplorerException e )
		{
			// ignore
		}

		return;
	}
	
	@Override
	public String userToStorage(String value) throws GeneralScenarioExplorerException
	{
		Double d=0.5;
		try {
			d= Double.parseDouble(value);
			if (d<0.0 || d>1.0)
				throw new GeneralScenarioExplorerException("Value ("+df.format(d)+") is not between 0 and 1.");
		} catch (NumberFormatException e){
			throw new GeneralScenarioExplorerException("Value ("+value+") is not in decimal format.");
		}
		return df.format(d);
	}

	@Override
	public String storageToUser(String value) {
		return value;
	}

	@Override
	public String getHelpText() {
		return "This feature holds a real number between 0.0 and 1.0 representing a probablity.";
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getAboutText() {
		return "";
	}

	@Override
	public String getRandomValue() {
		Random rand=new Random();
		double v=rand.nextDouble();
		return df.format(v);
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/feature-types/probabilityFeatureType.js";
	}

	public int getNumDecimalPlaces() throws InvalidDataException
	{
		return JsonHelper.getOptionalParameterInt( this.getConfig(), JsonKeys.ConfigNumDecimalPlaces, 2 );
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
