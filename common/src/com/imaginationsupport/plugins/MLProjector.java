package com.imaginationsupport.plugins;

import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.FeatureMap;

import java.util.List;

public abstract class MLProjector extends Projector {

    public static final String LATEPROCESSING ="";

    public String project( FeatureMap map, State currentState, List<State> history){
        return LATEPROCESSING;
    }

    public abstract boolean isTrained();

    public abstract void train( FeatureMap map, List< List<State> > data );

}
