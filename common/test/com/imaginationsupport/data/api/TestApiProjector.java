package com.imaginationsupport.data.api;

import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Projector;
import org.json.JSONObject;
import org.junit.Test;

public class TestApiProjector extends ApiTestCaseBase
{
	@Test
	public void testPluginDefinition() throws InvalidDataException
	{
		showMethodHeader();

		for( final Projector projector : PlugInManager.getInstance().getProjectors() )
		{
			final JSONObject pluginDefinition = projector.getPluginDefinition();

			LOGGER.debug( pluginDefinition.toString( 4 ) );

			verifyStringParam( pluginDefinition, Projector.JsonKeys.Id, projector.getClass().getCanonicalName() );
			verifyStringParam( pluginDefinition, Projector.JsonKeys.Name, projector.getName() );
			verifyStringParam( pluginDefinition, Projector.JsonKeys.HelpText, projector.getHelpText() );
			verifyStringParam( pluginDefinition, Projector.JsonKeys.AboutText, projector.getAboutText() );
		}

		return;
	}
}
