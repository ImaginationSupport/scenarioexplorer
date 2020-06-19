package com.imaginationsupport.data.api;

import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Outcome;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Precondition;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.Test;

public class TestApiConditioningEvent extends ApiTestCaseBase
{
	private static final String VALID_LABEL = "my label";
	private static final String VALID_DESCRIPTION = "my description";

	private static final int NUM_TEST_OUTCOMES = 3;

	@Test
	public void testToJson() throws InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		final ConditioningEvent testConditioningEvent = createTestConditioningEvent();

		final JSONObject json = testConditioningEvent.toJSON();

		testCoreEntries( json, testConditioningEvent );

//		final JSONArray preconditions = JsonHelper.getRequiredParameterJSONArray( json, ConditioningEvent.JsonKeys.PRECONDITIONS );
//		for( final Precondition precondition : PlugInManager.getInstance().getPreconditions() )
//		{
//			boolean found = false;
//
//			for( int i = 0; i < preconditions.length(); ++i )
//			{
//				final JSONObject entryRaw = preconditions.getJSONObject( i );
//				final Precondition entry = API.deserializePrecondition( entryRaw ); // TODO not implemented!
//
//				if( precondition.getId().equals( entry.getId() ) )
//				{
//					verifySameObject( precondition.toJSON( true ), entryRaw );
//					verifySameObject( precondition.toJSON( false ), entryRaw );
//					found = true;
//				}
//			}
//
//			assertTrue( String.format( "Could not find serialized entry (%s)", precondition.getId() ), found );
//		}
//
//		// check for any other keys
//		final Set< String > knownKeys = new HashSet<>();
//		knownKeys.add( ConditioningEvent.JsonKeys.ID );
//		knownKeys.add( ConditioningEvent.JsonKeys.LABEL );
//		knownKeys.add( ConditioningEvent.JsonKeys.DESCRIPTION );
//		knownKeys.add( ConditioningEvent.JsonKeys.PRECONDITIONS );
//		knownKeys.add( ConditioningEvent.JsonKeys.OUTCOMES );
//		verifyNoExtraEntries( json, knownKeys );

		return;
	}

	private ConditioningEvent createTestConditioningEvent()
	{
		final ObjectId projectId = getNextObjectId();
		final ObjectId viewId = getNextObjectId();

		final ConditioningEvent testConditioningEvent = new ConditioningEvent( projectId, viewId, VALID_LABEL, VALID_DESCRIPTION );

		for( final Precondition precondition : PlugInManager.getInstance().getPreconditions() )
		{
			testConditioningEvent.addPrecondition( precondition );
		}

		for( int i = 0; i < NUM_TEST_OUTCOMES; ++i )
		{
			final Outcome testOutcome = new Outcome();

			testConditioningEvent.addOutcome( testOutcome );
		}

		return testConditioningEvent;
	}

	private void testCoreEntries( final JSONObject json, final ConditioningEvent expected )
	{
		return;
	}
}
