package com.imaginationsupport.data.api;

import com.imaginationsupport.data.TimelineEvent;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TestApiTimelineEvent extends ApiTestCaseBase
{
	private static final String VALID_LABEL = "Test Timeline Event";

	private static final String VALID_DESCRIPTION = "Test Description";

	private static final String VALID_URL = "http://whatever";

	@Test
	public void testToJson() throws InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		final ObjectId projectId = getNextObjectId();
		final LocalDateTime start = LocalDateTime.now().minusDays( 1 );
		final LocalDateTime end = start.plusYears( 3 );
		final ObjectId id = getNextObjectId();
		final TimelineEvent testTimelineEvent = new TimelineEvent( projectId, VALID_LABEL, VALID_DESCRIPTION, start, end, VALID_URL );
		testTimelineEvent.setId( id );

		final JSONObject jsonMin = testTimelineEvent.toJSON();

		LOGGER.debug( "min: " + jsonMin.toString( 4 ) );

		verifyObjectIdParam( jsonMin, TimelineEvent.JsonKeys.Id, testTimelineEvent.getId() );
		verifyStringParam( jsonMin, TimelineEvent.JsonKeys.Label, VALID_LABEL );
		verifyStringParam( jsonMin, TimelineEvent.JsonKeys.Description, VALID_DESCRIPTION );
		verifyLocalDateTimeParam( jsonMin, TimelineEvent.JsonKeys.Start, testTimelineEvent.getStart() );
		verifyLocalDateTimeParam( jsonMin, TimelineEvent.JsonKeys.End, testTimelineEvent.getEnd() );
		verifyStringParam( jsonMin, TimelineEvent.JsonKeys.Url, testTimelineEvent.getURL() );

		// check for any other keys
		final Set< String > knownKeys = new HashSet<>();
		knownKeys.add( TimelineEvent.JsonKeys.Id );
		knownKeys.add( TimelineEvent.JsonKeys.Label );
		knownKeys.add( TimelineEvent.JsonKeys.Description );
		knownKeys.add( TimelineEvent.JsonKeys.Start );
		knownKeys.add( TimelineEvent.JsonKeys.End );
		knownKeys.add( TimelineEvent.JsonKeys.Url );
		verifyNoExtraEntries( jsonMin, knownKeys );

		final JSONObject jsonFull = testTimelineEvent.toJSON();

		LOGGER.debug( "full: " + jsonFull.toString( 4 ) );

		verifySameObject( jsonFull, jsonMin );

		return;
	}
}
