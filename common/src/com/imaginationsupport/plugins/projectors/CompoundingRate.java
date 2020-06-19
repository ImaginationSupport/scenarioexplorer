package com.imaginationsupport.plugins.projectors;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.plugins.Projector;
import com.imaginationsupport.plugins.features.DecimalFeature;
import com.imaginationsupport.plugins.features.IntegerFeature;

public class CompoundingRate extends Projector {
	
	public static final String NAME="Compounding Rate";
	public static final String JSON_CONFIG_MULTIPLIER="multiplier";
	public static final String JSON_CONFIG_TIMESPANDAYS="timespan";

	
	public double getMultiplier() {
		double m=1.0;
		try {
			m=JsonHelper.getOptionalParameterDouble( this.getConfig(), JSON_CONFIG_MULTIPLIER, m);
		} catch ( final InvalidDataException e) {
			//TODO log an error here
			return 1.0;
		}
		return m;
	}
	
	public int getTimespanDays() {
		int t=1;
		try {
			t=JsonHelper.getOptionalParameterInt( this.getConfig(), JSON_CONFIG_TIMESPANDAYS, t);
		} catch ( final InvalidDataException e) {
			//TODO log an error here
			return 1;
		}
		return t;
	}
	
	public CompoundingRate() {
	}
	
	public CompoundingRate(double multiplier, int timespanDays) {
		JsonHelper.put( config, JSON_CONFIG_MULTIPLIER, multiplier );	
		JsonHelper.put( config, JSON_CONFIG_TIMESPANDAYS, timespanDays );	
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getHelpText() {
		return "Multiplies a value by a (positive or negative) rate incrementally over time";
	}

	@Override
	public String getAboutText() {
		return "";
	}

	@Override
	public Set<String> applicableFor() {
		Set<String> out=new HashSet<String>();
		out.add(DecimalFeature.class.getCanonicalName());
		out.add(IntegerFeature.class.getCanonicalName());
		return out;
	}

	@Override
	public String project(FeatureMap map, String previousValue, State previousState, State currentState) {
		Duration d= Duration.between(currentState.getStart(),currentState.getEnd());
		int daysInDuration=(int)d.toDays();
		int daysPerIncrement=getTimespanDays();
		int increments=daysInDuration/daysPerIncrement;
		double value=Double.parseDouble(previousValue);
		double updatedValue=ammortize(value,getMultiplier(),increments);
		if (map.getType() instanceof IntegerFeature) {			
			return ""+((int)updatedValue);
		} else {
			return ""+updatedValue;
		}
	}
	
	private double ammortize(double value, double multiplier, int increments) {
		double out=value;
		for (int i=0;i<increments;i++) {
			out=out*multiplier;
		}
		return out;
	}
	
	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/projectors/compoundingRateProjector.js";
	}

}
