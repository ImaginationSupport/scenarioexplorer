package com.imaginationsupport.plugins.features;

import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.FeatureType;

import java.util.Random;

public class TextFeature extends FeatureType {
	
	public static final String NAME="Text";

	public TextFeature()
	{
		super();

		try
		{
			setDefaultValue( "" );
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
		return "This feature holds a free text entry.";
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
	public String getRandomValue() {
		Random rand=new Random();
		String alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789 abcdefghijklmnopqrstuvwxyz '.!-$%#&@*";
		int l=rand.nextInt(50);
		StringBuilder sb=new StringBuilder();
		for (int i=0;i<l;i++){
			sb.append(alphabet.charAt(rand.nextInt(alphabet.length())));
		}
		return sb.toString();
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/feature-types/textFeatureType.js";
	}

	public boolean isContinuousVariable(){
		return false;
	}
}
