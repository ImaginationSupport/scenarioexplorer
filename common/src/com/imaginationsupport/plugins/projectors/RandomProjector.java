package com.imaginationsupport.plugins.projectors;

import java.util.List;
import java.util.Set;

import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.plugins.Projector;

public class RandomProjector extends Projector {

	public static final String NAME="Random Values";
	
//	@Override
//	public Feature project(FeatureMap map, Feature previous, State previousState, State currentState) {
//		Feature f;
//		f = new Feature(map);
//		try {
//			f.setValue(map.getType().getRandomValue());
//		} catch (UserInputException e) {
//			e.printStackTrace();
//		}
//		return f;
//	}
	
	@Override
	public String project(FeatureMap map, String previousValue, State previousState, State currentState) {
		return map.getType().getRandomValue();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getHelpText() {
		return "This projector assigns random values for Integers features regardless of the state.";	}

	@Override
	public String getAboutText() {
		return "This projector is intended to primarily for testing purposes.";
	}

	@Override
	public Set<String> applicableFor() { // This Projector is good for any feature type...
		return PlugInManager.getInstance().getFeatureTypeIds();
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/projectors/randomProjector.js";
	}

}
