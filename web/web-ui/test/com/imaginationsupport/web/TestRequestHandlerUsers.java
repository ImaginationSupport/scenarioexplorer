package com.imaginationsupport.web;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.web.exceptions.ApiException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public final class TestRequestHandlerUsers extends ApiRequestTestBase
{
	/**
	 * Holds the valid value to use for testing the userName field
	 */
	private static final String VALID_USER_NAME = "myusername";

	/**
	 * Holds the valid value to use for testing the fullName field
	 */
	private static final String VALID_FULL_NAME = "My Full Name";

	@Before
	public void setUp() throws Exception
	{
		cleanDatabase();

		return;
	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
		dropDatabase();

		return;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// List Users
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testListUsers() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final List< User > users = runList();

			// make sure there are only the two users we expect
			assertEquals( String.format( "Should have exactly 2 users, but found %d", users.size() ), 2, users.size() );

			for( final User user : users )
			{
				// make sure the username was not null
				assertNotNull( "Username was null!", user.getUserName() );

				if( !user.getUserName().equals( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME ) && !user.getUserName().equals( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME ) )
				{
					fail(
						String.format(
							"Unknown username (%s) - should have been (%s) or (%s)",
							user.getUserName(),
							JUNIT_DATABASE_TESTING_ADMIN_USER_NAME,
							JUNIT_DATABASE_TESTING_NORMAL_USER_NAME ) );
				}
			}
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( e ) );
			throw e;
		}

		return;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Get User
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testGetUser() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );
			final User fromGet = runGet( original.getUserName() );

			assertEquals(
				String.format( "Expected userName to be (%s) but found (%s)", original.getUserName(), fromGet.getUserName() ),
				original.getUserName(),
				fromGet.getUserName() );
			assertEquals(
				String.format( "Expected fullName to be (%s) but found (%s)", original.getFullName(), fromGet.getFullName() ),
				original.getFullName(),
				fromGet.getFullName() );
			assertEquals(
				String.format( "Expected isSiteAdmin to be (%s) but found (%s)", original.isSiteAdmin(), fromGet.isSiteAdmin() ),
				original.isSiteAdmin(),
				fromGet.isSiteAdmin() );
			assertEquals(
				String.format( "Expected access to be (%s) but found (%s)", original.getAccess(), fromGet.getAccess() ),
				original.getAccess(),
				fromGet.getAccess() );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( e ) );
			throw e;
		}

		return;
	}

	@Test( expected = ApiException.class )
	public void testGetNonExistentFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			runGet( "does-not-exist" );
		}
		catch( final GeneralScenarioExplorerException | InvalidDataException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testGetNullFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		try
		{
			runGet( null );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}
	}

	@Test( expected = InvalidDataException.class )
	public void testGetBlankFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		try
		{
			runGet( "" );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}
	}
/*
	@Test( expected = InvalidDataException.class )
	public void testGetInvalidFails() throws ApiException, GeneralScenarioExplorerException
	{
		try
		{
			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.GetUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.Username, 1234 );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, request );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// New User
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testNewUser() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			// test admin user
			final User original = createValidEntry();

			final User fromNew = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			// verify all of the fields
			assertEquals(
				String.format( "Expected userName to be (%s) but found (%s)", original.getUserName(), fromNew.getUserName() ),
				original.getUserName(),
				fromNew.getUserName() );
			assertEquals(
				String.format( "Expected fullName to be (%s) but found (%s)", original.getFullName(), fromNew.getFullName() ),
				original.getFullName(),
				fromNew.getFullName() );
			assertEquals(
				String.format( "Expected isSiteAdmin to be (%s) but found (%s)", original.isSiteAdmin(), fromNew.isSiteAdmin() ),
				original.isSiteAdmin(),
				fromNew.isSiteAdmin() );
			assertEquals(
				String.format( "Expected access to be (%s) but found (%s)", original.getAccess(), fromNew.getAccess() ),
				original.getAccess(),
				fromNew.getAccess() );

			// get it again from the API
			final User fromGet = runGet( original.getUserName() );
			assertEquals(
				String.format( "Expected userName to be (%s) but found (%s)", original.getUserName(), fromGet.getUserName() ),
				original.getUserName(),
				fromGet.getUserName() );
			assertEquals(
				String.format( "Expected fullName to be (%s) but found (%s)", original.getFullName(), fromGet.getFullName() ),
				original.getFullName(),
				fromGet.getFullName() );
			assertEquals(
				String.format( "Expected isSiteAdmin to be (%s) but found (%s)", original.isSiteAdmin(), fromGet.isSiteAdmin() ),
				original.isSiteAdmin(),
				fromGet.isSiteAdmin() );
			assertEquals(
				String.format( "Expected access to be (%s) but found (%s)", original.getAccess(), fromGet.getAccess() ),
				original.getAccess(),
				fromGet.getAccess() );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( e ) );
			throw e;
		}

		return;
	}

	///// test authentication /////

	@Test( expected = ApiException.class )
	public void testNewAuthorization() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			// running as the normal user should throw the not-authorized exception...
			runNew( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, createValidEntry() );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test api errors /////

	@SuppressWarnings( "Duplicates" )
	@Test( expected = ApiException.class )
	public void testNewDuplicateFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			// create the entry
			runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// try adding the same entry again
			runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = ApiException.class )
	public void testNewMissingEntryFails() throws ApiException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | GeneralScenarioExplorerException  e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}
	}

	///// test field: userName /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingUserNameFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, User.JSON_KEY_USER_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException  e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewBlankUserNameFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_USER_NAME, "" );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException  e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullUserNameFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.putNull( json, User.JSON_KEY_USER_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException  e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidUserNameFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_USER_NAME, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: fullName /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingFullNameFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, User.JSON_KEY_FULL_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullFullNameFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.putNull( json, User.JSON_KEY_FULL_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidFullNameFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_FULL_NAME, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: isSiteAdmin /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingIsSiteAdminFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, User.JSON_KEY_IS_SITE_ADMIN );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullIsSiteAdminFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.putNull( json, User.JSON_KEY_IS_SITE_ADMIN );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidIsSiteAdminFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_IS_SITE_ADMIN, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: access /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingAccessFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, User.JSON_KEY_ACCESS );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullAccessFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.putNull( json, User.JSON_KEY_ACCESS );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidAccessFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_ACCESS, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update User
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUpdateFullName() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// make the change
			original.setFullName( "My Full Name-updated" );

			// run the API call
			final User fromUpdate = runUpdate( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			assertEquals(
				String.format( "Expected userName to be (%s) but found (%s)", original.getUserName(), fromUpdate.getUserName() ),
				original.getUserName(),
				fromUpdate.getUserName() );
			assertEquals(
				String.format( "Expected fullName to be (%s) but found (%s)", original.getFullName(), fromUpdate.getFullName() ),
				original.getFullName(),
				fromUpdate.getFullName() );
			assertEquals(
				String.format( "Expected isSiteAdmin to be (%s) but found (%s)", original.isSiteAdmin(), fromUpdate.isSiteAdmin() ),
				original.isSiteAdmin(),
				fromUpdate.isSiteAdmin() );
			assertEquals(
				String.format( "Expected access to be (%s) but found (%s)", original.getAccess(), fromUpdate.getAccess() ),
				original.getAccess(),
				fromUpdate.getAccess() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test
	public void testUpdateIsSiteAdmin() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// make the change
			original.setSiteAdmin( false );

			// run the API call
			final User fromUpdate = runUpdate( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			assertEquals(
				String.format( "Expected userName to be (%s) but found (%s)", original.getUserName(), fromUpdate.getUserName() ),
				original.getUserName(),
				fromUpdate.getUserName() );
			assertEquals(
				String.format( "Expected fullName to be (%s) but found (%s)", original.getFullName(), fromUpdate.getFullName() ),
				original.getFullName(),
				fromUpdate.getFullName() );
			assertEquals(
				String.format( "Expected isSiteAdmin to be (%s) but found (%s)", original.isSiteAdmin(), fromUpdate.isSiteAdmin() ),
				original.isSiteAdmin(),
				fromUpdate.isSiteAdmin() );
			assertEquals(
				String.format( "Expected access to be (%s) but found (%s)", original.getAccess(), fromUpdate.getAccess() ),
				original.getAccess(),
				fromUpdate.getAccess() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test
	public void testUpdateAccess() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			// create the project owner
			final User owner = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, new User( "owner", "owner", true ) );

			final Project newProject = runNewProject( owner );

			final User anotherUser = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, new User( "normaluser", "normal user", false ) );

			final Set< ObjectId > newAccess = new HashSet<>();
			newAccess.add( newProject.getId() );

			// make the change
			anotherUser.setAccess( newAccess );

			// run the API call
			final User fromUpdate = runUpdate( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, anotherUser );

			assertEquals(
				String.format( "Expected userName to be (%s) but found (%s)", anotherUser.getUserName(), fromUpdate.getUserName() ),
				anotherUser.getUserName(),
				fromUpdate.getUserName() );
			assertEquals(
				String.format( "Expected fullName to be (%s) but found (%s)", anotherUser.getFullName(), fromUpdate.getFullName() ),
				anotherUser.getFullName(),
				fromUpdate.getFullName() );
			assertEquals(
				String.format( "Expected isSiteAdmin to be (%s) but found (%s)", anotherUser.isSiteAdmin(), fromUpdate.isSiteAdmin() ),
				anotherUser.isSiteAdmin(),
				fromUpdate.isSiteAdmin() );
			assertEquals(
				String.format( "Expected access to be (%s) but found (%s)", anotherUser.getAccess(), fromUpdate.getAccess() ),
				anotherUser.getAccess(),
				fromUpdate.getAccess() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test authentication /////

	@Test( expected = ApiException.class )
	public void testUpdateAuthorization() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final User original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// running as the normal user should throw the not-authorized exception...
			runUpdate( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, original );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test api errors /////

	///// test field: userName /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateUserNameMissingFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, User.JSON_KEY_USER_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateUserNameNullFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.putNull( json, User.JSON_KEY_USER_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateUserNameBlankFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_USER_NAME, "" );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateUserNameInvalidFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_USER_NAME, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: fullName /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateFullNameMissingFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, User.JSON_KEY_FULL_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateFullNameNullFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.putNull( json, User.JSON_KEY_FULL_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateFullNameInvalidFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_FULL_NAME, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: isSiteAdmin /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateIsSiteAdminMissingFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, User.JSON_KEY_IS_SITE_ADMIN );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateIsSiteAdminNullFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.putNull( json, User.JSON_KEY_IS_SITE_ADMIN );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateIsSiteAdminInvalidFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_IS_SITE_ADMIN, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: access /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateAccessMissingFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, User.JSON_KEY_ACCESS );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateAccessNullFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.putNull( json, User.JSON_KEY_ACCESS );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateAccessInvalidFails() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, User.JSON_KEY_ACCESS, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			JsonHelper.put( request, ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, request );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete User
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testDeleteUser() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			// create the entry
			final User entry = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// now delete it
			runDelete( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, entry );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.info( ImaginationSupportUtil.formatStackTrace( e ) );
			throw e;
		}

		return;
	}

	///// test authentication /////

	@Test( expected = ApiException.class )
	public void testDeleteAuthorization() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			// create the entry
			final User entry = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// running as the normal user should throw the not-authorized exception...
			runDelete( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, entry );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}
*/
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Functions
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private User createValidEntry() throws InvalidDataException
	{
		return new User( VALID_USER_NAME, VALID_FULL_NAME, true );
	}

	private List< User > runList() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.ListUsers );

		final JSONArray rawEntries = runJsonArrayCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, requestBuilder.build() );
		final List< User > parsedEntries = new ArrayList<>();
		for( int i = 0; i < rawEntries.length(); ++i )
		{
			parsedEntries.add( new User( rawEntries.getJSONObject( i ) ) );
		}

		return parsedEntries;
	}

	private User runGet( final String usernameToGet ) throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.GetUser );
		requestBuilder.addUriParameter( ApiStrings.JsonKeys.Username, usernameToGet );

		final JSONObject response = runJsonObjectCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, requestBuilder.build() );

		// parse the entry
		return new User( JsonHelper.getRequiredParameterJSONObject( response, ApiStrings.JsonKeys.User ) );
	}

	private User runNew(
		final String usernameToRunJsonObjectCommandAs,
		final User entry ) throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewUser );
		requestBuilder.setBody( entry.toJSON() );

		final JSONObject response = runJsonObjectCommand( usernameToRunJsonObjectCommandAs, requestBuilder.build() );

		// parse the entry
		return new User( JsonHelper.getRequiredParameterJSONObject( response, ApiStrings.JsonKeys.User ) );
	}

	private User runUpdate(
		final String usernameToRunJsonObjectCommandAs,
		final User entry ) throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
		requestBuilder.addUriParameter( ApiStrings.JsonKeys.Username, entry.getUserName() );
		requestBuilder.setBody( entry.toJSON() );

		final JSONObject response = runJsonObjectCommand( usernameToRunJsonObjectCommandAs, requestBuilder.build() );

		// parse the entry
		return new User( JsonHelper.getRequiredParameterJSONObject( response, ApiStrings.JsonKeys.User ) );
	}

	private void runDelete(
		final String usernameToRunJsonObjectCommandAs,
		final User entry ) throws ApiException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.DeleteUser );
		requestBuilder.addUriParameter( ApiStrings.JsonKeys.Username, entry.getUserName() );

		runJsonObjectCommand( usernameToRunJsonObjectCommandAs, requestBuilder.build() );

		return;
	}

	private Project runNewProject( final User owner ) throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final LocalDateTime start = LocalDateTime.now().minusWeeks( 1 ).withDayOfMonth( 1 ).withHour( 8 ).withMinute( 0 ).withSecond( 0 ).withNano( 0 );
		final Project entry = new Project(
			"JUnit Test Project",
			"Description for JUnit Test Project",
			owner.getId(),
			start,
			start.plusYears( 2 ),
			Project.DaysIncrementValues.Month );

		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
		requestBuilder.setBody( entry.toJSON() );

		final JSONObject response = runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );

		// parse the user
		return new Project( JsonHelper.getRequiredParameterJSONObject( response, ApiStrings.JsonKeys.Project ) );
	}
}
