package com.imaginationsupport.data.features;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.FeatureType;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.NotSaved;

@Embedded
public class Feature {
	
	@NotSaved
	private FeatureMap map=null;
	
	private String value=null;
		
	public Feature(){
	}
	
	public Feature(FeatureMap map) throws InvalidDataException
	{
		this.map=map;
		value=map.getType().getDefaultValue();
	}	
	
	public void setValue(String value) throws GeneralScenarioExplorerException
	{
		this.value=map.getType().userToStorage(value);
	}
	
	public void setStorageReadyValue(String value){
		this.value=value;
	}

	public String getValue() throws InvalidDataException
	{
		if( map == null )
		{
			return null;
		}

		final FeatureType feature = map.getType();

		if( feature == null )
		{
			return null;
		}

		if( value == null )
		{
			return feature.getDefaultValue();
		}

		return feature.storageToUser( value );
	}
	
	public FeatureMap getMap() {
		return map;
	}

	public void setMap(FeatureMap map){
		this.map=map;
	}
	
	public String getLabel(){
		return this.map.getLabel();
	}

	public String getUid() {
		return this.map.getUid();
	}
}
