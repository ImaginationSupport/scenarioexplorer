package com.imaginationsupport.data.api;

import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Effect;
import org.json.JSONObject;
import org.junit.Test;

public class TestApiOutcomeEffect extends ApiTestCaseBase
{
	@Test
	public void testPluginDefinition() throws InvalidDataException
	{
		showMethodHeader();

		for( final Effect effect : PlugInManager.getInstance().getEffects() )
		{
			final JSONObject pluginDefinition = effect.getPluginDefinition();

			LOGGER.debug( pluginDefinition.toString( 4 ) );

			verifyStringParam( pluginDefinition, Effect.JsonKeys.Id, effect.getClass().getCanonicalName() );
			verifyStringParam( pluginDefinition, Effect.JsonKeys.Label, effect.getLabel() );
			verifyStringParam( pluginDefinition, Effect.JsonKeys.Description, effect.getDescription() );
		}

		return;
	}
}
