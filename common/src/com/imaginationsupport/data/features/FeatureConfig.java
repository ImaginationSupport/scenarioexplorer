package com.imaginationsupport.data.features;

public class FeatureConfig {
	
	public FeatureConfigType type=FeatureConfigType.TEXT;
	public String key="no key set";
	public String value="no value set";
	
	public FeatureConfig(){}
	
	public FeatureConfig(FeatureConfigType type, String key, String value){
		this.key=key;
		this.type=type;
		this.value=value;
	}
}
