package com.imaginationsupport.plugins;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import com.imaginationsupport.data.features.FeatureConfig;

public abstract class ConfigurableFeatureType {
	
	private Hashtable<String,FeatureConfig> configs=null;
	
	public List<FeatureConfig> getConfigs() {
		check();
		return Collections.list(configs.elements());
	}
	
	public List<String> getConfigKeys(){
		check();
		return Collections.list(configs.keys());
	}

	public void setConfig(FeatureConfig config) {
		check();
		if (configs.containsKey(config.key))
			configs.replace(config.key, config);
		else
			configs.put(config.key, config);
	}

	public abstract List<FeatureConfig> initConfigs();
	
	private void check(){
		if (configs==null) {
			configs=new Hashtable<String,FeatureConfig>();
			List<FeatureConfig> init=initConfigs();
			if(init!=null) {
				for(FeatureConfig c: init){
					setConfig(c);
				}
			}
		}
	}
	
	public String getConfig(String key){
		check();
		if(configs.containsKey(key))
			return configs.get(key).value;
		else
			return "";
	}

	public void setConfigs(List<FeatureConfig> configs) {
		for(FeatureConfig c: configs){
			setConfig(c);
		}
	}
	
}
