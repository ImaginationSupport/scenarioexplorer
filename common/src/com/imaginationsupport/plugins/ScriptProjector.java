package com.imaginationsupport.plugins;

import com.imaginationsupport.data.State;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

import static javax.script.ScriptContext.ENGINE_SCOPE;

public abstract class ScriptProjector extends Projector {
    public static final String JSON_CONFIG_SCRIPT = "script";
    public static final String JSON_CONFIG_LANGUAGE = "lang";
    public static final String JSON_CONFIG_RUNLEVEL = "runlevel";

    public abstract String project(FeatureMap map,ScriptContext scriptContext);

    public String project( FeatureMap map, String previousValue, State previousState, State currentState ){
        ScriptContext scriptContext=new SimpleScriptContext();
        Bindings binding=new SimpleBindings();
        // bind variables from current state - NOTE some may not be set yet...
        binding.put(toCurrentVariableName("start date"), currentState.getStart() );
        binding.put(toCurrentVariableName("end date"), currentState.getEnd() );
        for (FeatureMap otherMap: currentState.getProject().getFeatureMaps()){
            if(otherMap.compareTo(map)==0) continue; // do not try to copy the current value that is being computing
            try{
                binding.put(toCurrentVariableName(otherMap), currentState.getFeature(otherMap.getUid()).getValue());
            } catch (DatastoreException | InvalidDataException e) {
                System.err.println("ERROR: Failed to bind feature in new/current state for scripting "+otherMap.getLabel()+"("+otherMap.getUid()+"): "+e);
                e.printStackTrace();
            }
        }
        // bind variables from previous state
        binding.put(toPreviousVariableName("start date"), previousState.getStart() );
        binding.put(toPreviousVariableName("end date"), previousState.getEnd() );
        for (FeatureMap otherMap: previousState.getProject().getFeatureMaps()){
            try{
                binding.put(toPreviousVariableName(otherMap), previousState.getFeature(otherMap.getUid()).getValue());
            } catch (DatastoreException | InvalidDataException e) {
                System.err.println("ERROR: Failed to bind feature in previous state for scripting "+otherMap.getLabel()+"("+otherMap.getUid()+"): "+e);
                e.printStackTrace();
            }
        }

        scriptContext.setBindings(binding,ENGINE_SCOPE);
        try {
            String value = project(map, scriptContext);
            return value;
        } catch (Exception e){
            System.err.println(e);
            //TODO: add processing and logging here for uncaught exceptions
            return null;
        }
    }

    public String getLanguage(){
        String m="unknown";
        try {
            m=JsonHelper.getOptionalParameterString( this.getConfig(), JSON_CONFIG_LANGUAGE, m);
        } catch (InvalidDataException e) {
            return "error";
        }
        return m;
    }

    public void setLanguage(String language){
        JsonHelper.put( config, JSON_CONFIG_LANGUAGE, language );
    }

    public String getScript(){
        String m="unknown";
        try {
            m=JsonHelper.getOptionalParameterString( this.getConfig(), JSON_CONFIG_SCRIPT, m);
        } catch (InvalidDataException e) {
            return "error";
        }
        return m;
    }

    public void setScript(String script){
        JsonHelper.put( config, JSON_CONFIG_SCRIPT, script );
    }

    public int getRunLevel(){
        int m=1;
        try {
            m=JsonHelper.getOptionalParameterInt( this.getConfig(), JSON_CONFIG_RUNLEVEL, m);
        } catch ( InvalidDataException e) {
            return 1;
        }
        return m;
    }

    public void setRunLevel(int runLevel){
        JsonHelper.put( config, JSON_CONFIG_RUNLEVEL, runLevel );
    }

    private String toPreviousVariableName(FeatureMap map){
        return toPreviousVariableName(map.getLabel());
    }

    private String toPreviousVariableName(String name){
        return strip("previous "+name); // may make this an object?
    }

    private String toCurrentVariableName(FeatureMap map){
        return toCurrentVariableName(map.getLabel());
    }

    private String toCurrentVariableName(String name){
        return strip("current "+name); // may make this an object?
    }

    private String strip(String in){
        String out=camelcase(in.toLowerCase());
        out=out.replaceAll("[^a-zA-Z0-9]","");
        return out;
    }

    public static String camelcase(final String str) {
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;

        boolean capitalizeNext = false;
        for (int index = 0; index < strLen;) {
            final int codePoint = str.codePointAt(index);
            if (codePoint==' ' || codePoint=='_' || codePoint=='-') {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if (capitalizeNext) {
                final int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }
        return new String(newCodePoints, 0, outOffset);
    }


}
