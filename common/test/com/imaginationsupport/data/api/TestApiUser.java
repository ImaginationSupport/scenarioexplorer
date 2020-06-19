package com.imaginationsupport.data.api;

import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class TestApiUser extends ApiTestCaseBase
{
	private static final String VALID_USERNAME = "testUser";

	private static final String VALID_FULL_NAME = "Test User";

	@Test
	public void testToJson() throws InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		final User testUser = createTestUser();

		final JSONObject json = testUser.toJSON();

		LOGGER.debug( json.toString( 4 ) );

		testCoreEntries( json, testUser );

		// test projects NOT serialized
		verifyObjectIdArrayParam( json, User.JsonKeys.Access, new ObjectId[]{} );

		// check for any other keys
		final Set< String > knownKeys = new HashSet<>();
		knownKeys.add( User.JsonKeys.Id );
		knownKeys.add( User.JsonKeys.UserName );
		knownKeys.add( User.JsonKeys.FullName );
		knownKeys.add( User.JsonKeys.Access );
		knownKeys.add( User.JsonKeys.IsSiteAdmin );
		knownKeys.add( User.JsonKeys.LastLogin );
		knownKeys.add( User.JsonKeys.Preferences );
		verifyNoExtraEntries( json, knownKeys );

		return;
	}

	private User createTestUser() throws InvalidDataException
	{
		final ObjectId id = getNextObjectId();
		final User testUser = new User( VALID_USERNAME, VALID_FULL_NAME, true );
		testUser.setId( id );
		testUser.setLastLogin();

		// TODO put test project access!

		return testUser;
	}

	private void testCoreEntries( final JSONObject json, final User expected ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		verifyObjectIdParam( json, User.JsonKeys.Id, expected.getId() );
		verifyStringParam( json, User.JsonKeys.UserName, VALID_USERNAME );
		verifyStringParam( json, User.JsonKeys.FullName, VALID_FULL_NAME );
		verifyLocalDateTimeParam( json, User.JsonKeys.LastLogin, expected.getLastLogin() );
		verifyBooleanParam( json, User.JsonKeys.IsSiteAdmin, expected.isSiteAdmin() );

		return;
	}
}
