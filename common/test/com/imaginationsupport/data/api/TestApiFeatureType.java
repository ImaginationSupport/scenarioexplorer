package com.imaginationsupport.data.api;

import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.FeatureType;
import org.json.JSONObject;
import org.junit.Test;

public class TestApiFeatureType extends ApiTestCaseBase
{
	@Test
	public void testPluginDefinition() throws InvalidDataException
	{
		showMethodHeader();

		for( final FeatureType featureType : PlugInManager.getInstance().getFeatureTypes() )
		{
			final JSONObject pluginDefinition = featureType.getPluginDefinition();

			LOGGER.debug( pluginDefinition.toString( 4 ) );

			verifyStringParam( pluginDefinition, FeatureType.JsonKeys.Id, featureType.getClass().getCanonicalName() );
			verifyStringParam( pluginDefinition, FeatureType.JsonKeys.Name, featureType.getName() );
			verifyStringParam( pluginDefinition, FeatureType.JsonKeys.AboutText, featureType.getAboutText() );
			verifyStringParam( pluginDefinition, FeatureType.JsonKeys.HelpText, featureType.getHelpText() );
		}

		return;
	}
}
