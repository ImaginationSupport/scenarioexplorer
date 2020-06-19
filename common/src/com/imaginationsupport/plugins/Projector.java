package com.imaginationsupport.plugins;

import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFieldInfo;
import com.imaginationsupport.annotations.RestApiRequestObjectPseudoFields;
import com.imaginationsupport.data.Persistent;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.api.Plugin;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.helpers.JsonHelper;
import org.json.JSONObject;

import java.util.Set;

@RestApiObjectInfo(
	definitionName = "Projector",
	tagName = RestApiHandlerInfo.CategoryNames.Plugin,
	description = "Scenario Explorer Projector" )
@RestApiRequestObjectPseudoFields( {
	@RestApiRequestObjectPseudoFieldInfo(
		name = Projector.JsonKeys.Name,
		description = "The name of the projector",
		rawType = String.class ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = Projector.JsonKeys.ApplicableFor,
		description = "The set of feature type plugin ids this projector is applicable for",
		rawType = Set.class,
		genericInnerType = String.class ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = Projector.JsonKeys.HelpText,
		description = "The help text",
		rawType = String.class ),
	@RestApiRequestObjectPseudoFieldInfo(
		name = Projector.JsonKeys.AboutText,
		description = "The about text",
		rawType = String.class )
} )
public abstract class Projector extends Plugin implements Comparable< Projector >
{
	@SuppressWarnings( "WeakerAccess" )
	public static class JsonKeys extends Persistent.JsonKeys
	{
		public static final String Name = "name";
		public static final String HelpText = "helpText";
		public static final String AboutText = "aboutText";
		public static final String ApplicableFor = "applicableFor";
	}

	public JSONObject config=new JSONObject();
	
	public abstract String getName();

	public abstract String getHelpText();

	public abstract String getAboutText();

	public abstract Set< String > applicableFor();

	//public Feature project(FeatureMap map, Feature previous, State previousState, State currentState);
	public abstract String project( FeatureMap map, String previousValue, State previousState, State currentState );

	//TODO load via: https://docs.oracle.com/javase/tutorial/ext/basics/spi.html

//	public int getRunLevel(){
//		return 0;
//	}

	@SuppressWarnings( "Duplicates" )
	@Override
	public JSONObject getPluginDefinition()
	{
		final JSONObject json = super.getPluginDefinition();

		JsonHelper.put( json, JsonKeys.Name, this.getName() );
		JsonHelper.put( json, JsonKeys.AboutText, this.getAboutText() );
		JsonHelper.put( json, JsonKeys.HelpText, this.getHelpText() );
		JsonHelper.put( json, JsonKeys.ApplicableFor, JsonHelper.toJSONArrayStrings( this.applicableFor() ) );

		return json;
	}

	@Override
	public int compareTo( final Projector other )
	{
		return this.getName().compareTo( other.getName() );
	}
}
