package com.imaginationsupport.data.api;

import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.data.Outcome;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Effect;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class TestApiOutcome extends ApiTestCaseBase
{
	private static final String VALID_LABEL = "my outcome";
	private static final String VALID_DESCRIPTION = "my description";
	private static final double VALID_LIKELIHOOD = 0.5;

	@Test
	public void testToJson() throws InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		final Outcome testOutcome = createTestOutcome();

		final JSONObject jsonMin = testOutcome.toJSON();

		verifyStringParam( jsonMin, Outcome.JsonKeys.Label, VALID_LABEL );
		verifyDoubleParam( jsonMin, Outcome.JsonKeys.Likelihood, VALID_LIKELIHOOD );

		// check the effects
		final JSONArray effects = jsonMin.getJSONArray( Outcome.JsonKeys.Effects );
		for( final Effect effect : PlugInManager.getInstance().getEffects() )
		{
			boolean found = false;

			for( int i = 0; i < effects.length(); ++i )
			{
				if( effect.getId().equals( effect.getId() ) )
				{
					found = true;
				}
			}

			assertTrue( String.format( "Could not find effect (%s)", effect.getId() ), found );
		}

		// check for any other keys
		final Set< String > knownKeys = new HashSet<>();
		knownKeys.add( Outcome.JsonKeys.Label );
		knownKeys.add( Outcome.JsonKeys.Likelihood );
		knownKeys.add( Outcome.JsonKeys.Effects );
		verifyNoExtraEntries( jsonMin, knownKeys );

		final JSONObject jsonFull = testOutcome.toJSON();
		verifySameObject( jsonMin, jsonFull );

		return;
	}

	private Outcome createTestOutcome()
	{
		final Outcome outcome = new Outcome( VALID_LABEL, VALID_DESCRIPTION, VALID_LIKELIHOOD );

		for( final Effect effect : PlugInManager.getInstance().getEffects() )
		{
			outcome.addEffect( effect );
		}

		return outcome;
	}
}
