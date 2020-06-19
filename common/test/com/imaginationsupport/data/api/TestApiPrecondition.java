package com.imaginationsupport.data.api;

import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Precondition;
import org.json.JSONObject;
import org.junit.Test;

public class TestApiPrecondition extends ApiTestCaseBase
{
	@Test
	public void testPluginDefinition() throws InvalidDataException
	{
		showMethodHeader();

		for( final Precondition precondition : PlugInManager.getInstance().getPreconditions() )
		{
			final JSONObject pluginDefinition = precondition.getPluginDefinition();

			LOGGER.debug( pluginDefinition.toString( 4 ) );

			verifyStringParam( pluginDefinition, Precondition.JsonKeys.Id, precondition.getClass().getCanonicalName() );
			verifyStringParam( pluginDefinition, Precondition.JsonKeys.Label, precondition.getLabel() );
			verifyStringParam( pluginDefinition, Precondition.JsonKeys.Description, precondition.getDescription() );
		}

		return;
	}
}
