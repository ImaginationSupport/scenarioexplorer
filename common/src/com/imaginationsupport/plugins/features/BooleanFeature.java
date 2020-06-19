package com.imaginationsupport.plugins.features;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.FeatureType;

import java.util.Random;

public class BooleanFeature extends FeatureType {

	public static final String NAME="Boolean";

	public BooleanFeature()
	{
		super();

		try
		{
			setDefaultValue( Boolean.toString( false ) );
		}
		catch( final InvalidDataException e )
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
		return "This feature stores a boolean value of TRUE or FALSE";
	}

	@Override
	public String getAboutText() {
		return "";
	}

	@Override
	public String userToStorage(String value) throws GeneralScenarioExplorerException
	{
		if (value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("T") || value.equals("1")) return "TRUE";
		if (value.equalsIgnoreCase("FALSE")|| value.equalsIgnoreCase("F") || value.equals("0")) return "FALSE";
		throw new GeneralScenarioExplorerException("Not a valid boolean value ("+value+").");
	}

	@Override
	public String storageToUser(String value) {
		return value;
	}

	@Override
	public String getRandomValue() {
		Random rand=new Random();
		boolean v=rand.nextBoolean();
		if(v) {
			return "TRUE";
		} else {
			return "FALSE";
		}
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/feature-types/booleanFeatureType.js";
	}

	public boolean isContinuousVariable() {
		return false;
	}
}
