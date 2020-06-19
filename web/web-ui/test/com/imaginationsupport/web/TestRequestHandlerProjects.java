package com.imaginationsupport.web;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.web.exceptions.ApiException;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class TestRequestHandlerProjects extends ApiRequestTestBase
{
	/**
	 * Holds the valid value to use for testing the name field
	 */
	private static final String VALID_NAME = "myproject";

	/**
	 * Holds the valid value to use for testing the description field
	 */
	private static final String VALID_DESCRIPTION = "a description of my project";

	/**
	 * Holds the valid value to use for testing the start field
	 */
	private static final LocalDateTime VALID_START = LocalDateTime.now().minusWeeks( 1 ).withDayOfMonth( 1 ).withHour( 8 ).withMinute( 0 ).withSecond( 0 ).withNano( 0 );

	/**
	 * Holds the valid value to use for testing the end field
	 */
	private static final LocalDateTime VALID_END = LocalDateTime.now().plusYears( 2 ).withHour( 0 ).withMinute( 0 ).withSecond( 0 ).withNano( 0 );

	/**
	 * Holds the valid value to use for testing the daysIncrement field
	 */
	private static final int VALID_DAYS_INCREMENT = Project.DaysIncrementValues.Month;

	/**
	 * Holds the valid value to use for testing the owner field
	 */
	private static final ObjectId VALID_OWNER = new ObjectId( "000000000000000000000000" );

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
	// List Projects
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testListProjects() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final List< Project > entriesEmpty = runList();

			// make sure there are no entries
			assertTrue( String.format( "Should have zero entries, but found %d", entriesEmpty.size() ), entriesEmpty.isEmpty() );

			// now add an entry
			runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// make sure it is the only entry
			assertEquals( String.format( "Should have one entry, but found %d", entriesEmpty.size() ), 1, entriesEmpty.size() );

			// make sure that entry is what we expect
			// TODO ...

		}
		catch( final GeneralScenarioExplorerException | ApiException | InvalidDataException e )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( e ) );
			throw e;
		}

		return;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Get Project
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testGetProject() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );
			final Project fromGet = runGet( original.getId() );

			assertEquals(
				String.format( "Expected name to be (%s) but found (%s)", original.getName(), fromGet.getName() ),
				original.getName(),
				fromGet.getName() );
			assertEquals(
				String.format( "Expected description to be (%s) but found (%s)", original.getDescription(), fromGet.getDescription() ),
				original.getDescription(),
				fromGet.getDescription() );
			assertEquals(
				String.format( "Expected start to be (%s) but found (%s)", original.getStart(), fromGet.getStart() ),
				original.getStart(),
				fromGet.getStart() );
			assertEquals(
				String.format( "Expected end to be (%s) but found (%s)", original.getEnd(), fromGet.getEnd() ),
				original.getEnd(),
				fromGet.getEnd() );
			assertEquals(
				String.format( "Expected daysIncrement to be (%s) but found (%s)", original.getDaysIncrement(), fromGet.getDaysIncrement() ),
				original.getDaysIncrement(),
				fromGet.getDaysIncrement() );
			assertEquals(
				String.format( "Expected owner to be (%s) but found (%s)", original.getOwner(), fromGet.getOwner() ),
				original.getOwner(),
				fromGet.getOwner() );
		}
		catch( final GeneralScenarioExplorerException | ApiException | InvalidDataException e )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( e ) );
			throw e;
		}

		return;
	}

	@Test( expected = ApiException.class )
	public void testGetNonExistentFails() throws ApiException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		try
		{
			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.GetProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, "does-not-exist" );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, requestBuilder.build() );
		}
		catch( final GeneralScenarioExplorerException | ApiException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testGetNullFails() throws ApiException, GeneralScenarioExplorerException
	{
		try
		{
			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.GetProject );
			requestBuilder.addNullUriParameter( ApiStrings.JsonKeys.Project );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, requestBuilder.build() );
		}
		catch( final GeneralScenarioExplorerException | ApiException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}
	}

	@Test( expected = InvalidDataException.class )
	public void testGetBlankFails() throws ApiException, GeneralScenarioExplorerException
	{
		try
		{
			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.GetProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, "" );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, requestBuilder.build() );
		}
		catch( final GeneralScenarioExplorerException | ApiException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}
	}

	@Test( expected = InvalidDataException.class )
	public void testGetInvalidFails() throws ApiException, GeneralScenarioExplorerException
	{
		try
		{
			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.GetProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, 1234 );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, requestBuilder.build() );
		}
		catch( final GeneralScenarioExplorerException | ApiException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// New Project
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testNewProject() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project original = createValidEntry();

			final Project fromNew = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			// verify all of the fields
			assertEquals(
				String.format( "Expected name to be (%s) but found (%s)", original.getName(), fromNew.getName() ),
				original.getName(),
				fromNew.getName() );
			assertEquals(
				String.format( "Expected description to be (%s) but found (%s)", original.getDescription(), fromNew.getDescription() ),
				original.getDescription(),
				fromNew.getDescription() );
			assertEquals(
				String.format( "Expected start to be (%s) but found (%s)", original.getStart(), fromNew.getStart() ),
				original.getStart(),
				fromNew.getStart() );
			assertEquals(
				String.format( "Expected end to be (%s) but found (%s)", original.getEnd(), fromNew.getEnd() ),
				original.getEnd(),
				fromNew.getEnd() );
			assertEquals(
				String.format( "Expected daysIncrement to be (%s) but found (%s)", original.getDaysIncrement(), fromNew.getDaysIncrement() ),
				original.getDaysIncrement(),
				fromNew.getDaysIncrement() );
			assertEquals(
				String.format( "Expected owner to be (%s) but found (%s)", original.getOwner(), fromNew.getOwner() ),
				original.getOwner(),
				fromNew.getOwner() );

			// get it again from the API
			final Project fromGet = runGet( original.getId() );
			assertEquals(
				String.format( "Expected name to be (%s) but found (%s)", original.getName(), fromGet.getName() ),
				original.getName(),
				fromGet.getName() );
			assertEquals(
				String.format( "Expected description to be (%s) but found (%s)", original.getDescription(), fromGet.getDescription() ),
				original.getDescription(),
				fromGet.getDescription() );
			assertEquals(
				String.format( "Expected start to be (%s) but found (%s)", original.getStart(), fromGet.getStart() ),
				original.getStart(),
				fromGet.getStart() );
			assertEquals(
				String.format( "Expected end to be (%s) but found (%s)", original.getEnd(), fromGet.getEnd() ),
				original.getEnd(),
				fromGet.getEnd() );
			assertEquals(
				String.format( "Expected daysIncrement to be (%s) but found (%s)", original.getDaysIncrement(), fromGet.getDaysIncrement() ),
				original.getDaysIncrement(),
				fromGet.getDaysIncrement() );
			assertEquals(
				String.format( "Expected owner to be (%s) but found (%s)", original.getOwner(), fromGet.getOwner() ),
				original.getOwner(),
				fromGet.getOwner() );
		}
		catch( final GeneralScenarioExplorerException | ApiException | InvalidDataException e )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( e ) );
			throw e;
		}

		return;
	}

	///// test authentication /////

	@Test( expected = ApiException.class )
	public void testNewAuthorization() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			// running as the normal user should throw the not-authorized exception...
			runNew( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, createValidEntry() );
		}
		catch( final GeneralScenarioExplorerException | ApiException | InvalidDataException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test api errors /////
/*
	@SuppressWarnings( "Duplicates" )
	@Test( expected = ApiException.class )
	public void testNewDuplicateFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
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
			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}
	}

	///// test field: name /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingNameFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.set( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewBlankNameFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_NAME, "" );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullNameFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_NAME, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidNameFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_NAME, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: description /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingDescriptionFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_DESCRIPTION );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullDescriptionFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_DESCRIPTION, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidDescriptionFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_DESCRIPTION, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: createdOn /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingCreatedOnFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_CREATED_ON );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullCreatedOnFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_CREATED_ON, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidCreatedOnFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_CREATED_ON, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: lastEditOn /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingLastEditOnFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_LAST_EDIT_ON );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullLastEditOnFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_LAST_EDIT_ON, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidLastEditOnFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_LAST_EDIT_ON, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: start /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingStartFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_START );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullStartFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_START, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidStartFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_START, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: end /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingEndFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_END );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullEndFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_END, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidEndFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_END, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: daysIncrement /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingDaysIncrementFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_DAYS_INCREMENT );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullDaysIncrementFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_DAYS_INCREMENT, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidDaysIncrementFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_DAYS_INCREMENT, -1 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: owner /////

	@Test( expected = InvalidDataException.class )
	public void testNewMissingOwnerFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_OWNER );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewNullOwnerFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_OWNER, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testNewInvalidOwnerFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project entry = createValidEntry();

			final JSONObject json = entry.toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_OWNER, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Update Project
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testUpdateName() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// make the change
			original.setName( "myproject-updated" );

			// run the API call
			final Project fromUpdate = runUpdate( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			assertEquals(
				String.format( "Expected name to be (%s) but found (%s)", original.getName(), fromUpdate.getName() ),
				original.getName(),
				fromUpdate.getName() );
			assertEquals(
				String.format( "Expected description to be (%s) but found (%s)", original.getDescription(), fromUpdate.getDescription() ),
				original.getDescription(),
				fromUpdate.getDescription() );
			assertEquals(
				String.format( "Expected createdOn to be (%s) but found (%s)", original.getCreatedOn(), fromUpdate.getCreatedOn() ),
				original.getCreatedOn(),
				fromUpdate.getCreatedOn() );
			assertEquals(
				String.format( "Expected lastEditOn to be (%s) but found (%s)", original.getLastEditOn(), fromUpdate.getLastEditOn() ),
				original.getLastEditOn(),
				fromUpdate.getLastEditOn() );
			assertEquals(
				String.format( "Expected start to be (%s) but found (%s)", original.getStart(), fromUpdate.getStart() ),
				original.getStart(),
				fromUpdate.getStart() );
			assertEquals(
				String.format( "Expected end to be (%s) but found (%s)", original.getEnd(), fromUpdate.getEnd() ),
				original.getEnd(),
				fromUpdate.getEnd() );
			assertEquals(
				String.format( "Expected daysIncrement to be (%s) but found (%s)", original.getDaysIncrement(), fromUpdate.getDaysIncrement() ),
				original.getDaysIncrement(),
				fromUpdate.getDaysIncrement() );
			assertEquals(
				String.format( "Expected owner to be (%s) but found (%s)", original.getOwner(), fromUpdate.getOwner() ),
				original.getOwner(),
				fromUpdate.getOwner() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test
	public void testUpdateDescription() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// make the change
			original.setDescription( "a description of my project-updated" );

			// run the API call
			final Project fromUpdate = runUpdate( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			assertEquals(
				String.format( "Expected name to be (%s) but found (%s)", original.getName(), fromUpdate.getName() ),
				original.getName(),
				fromUpdate.getName() );
			assertEquals(
				String.format( "Expected description to be (%s) but found (%s)", original.getDescription(), fromUpdate.getDescription() ),
				original.getDescription(),
				fromUpdate.getDescription() );
			assertEquals(
				String.format( "Expected createdOn to be (%s) but found (%s)", original.getCreatedOn(), fromUpdate.getCreatedOn() ),
				original.getCreatedOn(),
				fromUpdate.getCreatedOn() );
			assertEquals(
				String.format( "Expected lastEditOn to be (%s) but found (%s)", original.getLastEditOn(), fromUpdate.getLastEditOn() ),
				original.getLastEditOn(),
				fromUpdate.getLastEditOn() );
			assertEquals(
				String.format( "Expected start to be (%s) but found (%s)", original.getStart(), fromUpdate.getStart() ),
				original.getStart(),
				fromUpdate.getStart() );
			assertEquals(
				String.format( "Expected end to be (%s) but found (%s)", original.getEnd(), fromUpdate.getEnd() ),
				original.getEnd(),
				fromUpdate.getEnd() );
			assertEquals(
				String.format( "Expected daysIncrement to be (%s) but found (%s)", original.getDaysIncrement(), fromUpdate.getDaysIncrement() ),
				original.getDaysIncrement(),
				fromUpdate.getDaysIncrement() );
			assertEquals(
				String.format( "Expected owner to be (%s) but found (%s)", original.getOwner(), fromUpdate.getOwner() ),
				original.getOwner(),
				fromUpdate.getOwner() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test
	public void testUpdateStart() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// make the change
			original.setStart( LocalDateTime.now().minusWeeks( 1 ).withDayOfMonth( 1 ).withHour( 8 ).withMinute( 0 ).withSecond( 0 ).withNano( 0 ).plusDays( 1 ) );

			// run the API call
			final Project fromUpdate = runUpdate( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			assertEquals(
				String.format( "Expected name to be (%s) but found (%s)", original.getName(), fromUpdate.getName() ),
				original.getName(),
				fromUpdate.getName() );
			assertEquals(
				String.format( "Expected description to be (%s) but found (%s)", original.getDescription(), fromUpdate.getDescription() ),
				original.getDescription(),
				fromUpdate.getDescription() );
			assertEquals(
				String.format( "Expected createdOn to be (%s) but found (%s)", original.getCreatedOn(), fromUpdate.getCreatedOn() ),
				original.getCreatedOn(),
				fromUpdate.getCreatedOn() );
			assertEquals(
				String.format( "Expected lastEditOn to be (%s) but found (%s)", original.getLastEditOn(), fromUpdate.getLastEditOn() ),
				original.getLastEditOn(),
				fromUpdate.getLastEditOn() );
			assertEquals(
				String.format( "Expected start to be (%s) but found (%s)", original.getStart(), fromUpdate.getStart() ),
				original.getStart(),
				fromUpdate.getStart() );
			assertEquals(
				String.format( "Expected end to be (%s) but found (%s)", original.getEnd(), fromUpdate.getEnd() ),
				original.getEnd(),
				fromUpdate.getEnd() );
			assertEquals(
				String.format( "Expected daysIncrement to be (%s) but found (%s)", original.getDaysIncrement(), fromUpdate.getDaysIncrement() ),
				original.getDaysIncrement(),
				fromUpdate.getDaysIncrement() );
			assertEquals(
				String.format( "Expected owner to be (%s) but found (%s)", original.getOwner(), fromUpdate.getOwner() ),
				original.getOwner(),
				fromUpdate.getOwner() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test
	public void testUpdateEnd() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// make the change
			original.setEnd( LocalDateTime.now().plusYears( 2 ).withHour( 0 ).withMinute( 0 ).withSecond( 0 ).withNano( 0 ).plusDays( 1 ) );

			// run the API call
			final Project fromUpdate = runUpdate( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			assertEquals(
				String.format( "Expected name to be (%s) but found (%s)", original.getName(), fromUpdate.getName() ),
				original.getName(),
				fromUpdate.getName() );
			assertEquals(
				String.format( "Expected description to be (%s) but found (%s)", original.getDescription(), fromUpdate.getDescription() ),
				original.getDescription(),
				fromUpdate.getDescription() );
			assertEquals(
				String.format( "Expected createdOn to be (%s) but found (%s)", original.getCreatedOn(), fromUpdate.getCreatedOn() ),
				original.getCreatedOn(),
				fromUpdate.getCreatedOn() );
			assertEquals(
				String.format( "Expected lastEditOn to be (%s) but found (%s)", original.getLastEditOn(), fromUpdate.getLastEditOn() ),
				original.getLastEditOn(),
				fromUpdate.getLastEditOn() );
			assertEquals(
				String.format( "Expected start to be (%s) but found (%s)", original.getStart(), fromUpdate.getStart() ),
				original.getStart(),
				fromUpdate.getStart() );
			assertEquals(
				String.format( "Expected end to be (%s) but found (%s)", original.getEnd(), fromUpdate.getEnd() ),
				original.getEnd(),
				fromUpdate.getEnd() );
			assertEquals(
				String.format( "Expected daysIncrement to be (%s) but found (%s)", original.getDaysIncrement(), fromUpdate.getDaysIncrement() ),
				original.getDaysIncrement(),
				fromUpdate.getDaysIncrement() );
			assertEquals(
				String.format( "Expected owner to be (%s) but found (%s)", original.getOwner(), fromUpdate.getOwner() ),
				original.getOwner(),
				fromUpdate.getOwner() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test
	public void testUpdateDaysIncrement() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// make the change
			original.setDaysIncrement( Project.DaysIncrementValues.Month + 1 );

			// run the API call
			final Project fromUpdate = runUpdate( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			assertEquals(
				String.format( "Expected name to be (%s) but found (%s)", original.getName(), fromUpdate.getName() ),
				original.getName(),
				fromUpdate.getName() );
			assertEquals(
				String.format( "Expected description to be (%s) but found (%s)", original.getDescription(), fromUpdate.getDescription() ),
				original.getDescription(),
				fromUpdate.getDescription() );
			assertEquals(
				String.format( "Expected createdOn to be (%s) but found (%s)", original.getCreatedOn(), fromUpdate.getCreatedOn() ),
				original.getCreatedOn(),
				fromUpdate.getCreatedOn() );
			assertEquals(
				String.format( "Expected lastEditOn to be (%s) but found (%s)", original.getLastEditOn(), fromUpdate.getLastEditOn() ),
				original.getLastEditOn(),
				fromUpdate.getLastEditOn() );
			assertEquals(
				String.format( "Expected start to be (%s) but found (%s)", original.getStart(), fromUpdate.getStart() ),
				original.getStart(),
				fromUpdate.getStart() );
			assertEquals(
				String.format( "Expected end to be (%s) but found (%s)", original.getEnd(), fromUpdate.getEnd() ),
				original.getEnd(),
				fromUpdate.getEnd() );
			assertEquals(
				String.format( "Expected daysIncrement to be (%s) but found (%s)", original.getDaysIncrement(), fromUpdate.getDaysIncrement() ),
				original.getDaysIncrement(),
				fromUpdate.getDaysIncrement() );
			assertEquals(
				String.format( "Expected owner to be (%s) but found (%s)", original.getOwner(), fromUpdate.getOwner() ),
				original.getOwner(),
				fromUpdate.getOwner() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test
	public void testUpdateOwner() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// make the change
			original.setOwner( getUser( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME ) );

			// run the API call
			final Project fromUpdate = runUpdate( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, original );

			assertEquals(
				String.format( "Expected name to be (%s) but found (%s)", original.getName(), fromUpdate.getName() ),
				original.getName(),
				fromUpdate.getName() );
			assertEquals(
				String.format( "Expected description to be (%s) but found (%s)", original.getDescription(), fromUpdate.getDescription() ),
				original.getDescription(),
				fromUpdate.getDescription() );
			assertEquals(
				String.format( "Expected createdOn to be (%s) but found (%s)", original.getCreatedOn(), fromUpdate.getCreatedOn() ),
				original.getCreatedOn(),
				fromUpdate.getCreatedOn() );
			assertEquals(
				String.format( "Expected lastEditOn to be (%s) but found (%s)", original.getLastEditOn(), fromUpdate.getLastEditOn() ),
				original.getLastEditOn(),
				fromUpdate.getLastEditOn() );
			assertEquals(
				String.format( "Expected start to be (%s) but found (%s)", original.getStart(), fromUpdate.getStart() ),
				original.getStart(),
				fromUpdate.getStart() );
			assertEquals(
				String.format( "Expected end to be (%s) but found (%s)", original.getEnd(), fromUpdate.getEnd() ),
				original.getEnd(),
				fromUpdate.getEnd() );
			assertEquals(
				String.format( "Expected daysIncrement to be (%s) but found (%s)", original.getDaysIncrement(), fromUpdate.getDaysIncrement() ),
				original.getDaysIncrement(),
				fromUpdate.getDaysIncrement() );
			assertEquals(
				String.format( "Expected owner to be (%s) but found (%s)", original.getOwner(), fromUpdate.getOwner() ),
				original.getOwner(),
				fromUpdate.getOwner() );
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
	public void testUpdateAuthorization() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final Project original = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// running as the normal user should throw the not-authorized exception...
			runUpdate( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, original );
		}
		catch( final GeneralScenarioExplorerException | ApiException | InvalidDataException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test api errors /////

	///// test field: name /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateNameMissingFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_NAME );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateNameNullFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_NAME, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateNameBlankFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_NAME, "" );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateNameInvalidFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_NAME, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: description /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateDescriptionMissingFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_DESCRIPTION );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateDescriptionNullFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_DESCRIPTION, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateDescriptionInvalidFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_DESCRIPTION, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: createdOn /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateCreatedOnMissingFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_CREATED_ON );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateCreatedOnNullFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_CREATED_ON, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateCreatedOnInvalidFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_CREATED_ON, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: lastEditOn /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateLastEditOnMissingFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_LAST_EDIT_ON );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateLastEditOnNullFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_LAST_EDIT_ON, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateLastEditOnInvalidFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_LAST_EDIT_ON, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: start /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateStartMissingFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_START );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateStartNullFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_START, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateStartInvalidFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_START, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: end /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateEndMissingFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_END );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateEndNullFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_END, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateEndInvalidFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_END, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: daysIncrement /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateDaysIncrementMissingFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_DAYS_INCREMENT );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateDaysIncrementNullFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_DAYS_INCREMENT, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateDaysIncrementInvalidFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_DAYS_INCREMENT, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	///// test field: owner /////

	@Test( expected = InvalidDataException.class )
	public void testUpdateOwnerMissingFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.remove( json, Project.JSON_KEY_OWNER );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.Project, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateOwnerNullFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			requestBuilder.addUriParameter( Project.JSON_KEY_OWNER, null );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	@Test( expected = InvalidDataException.class )
	public void testUpdateOwnerInvalidFails() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			final JSONObject json = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() ).toJSON( false );
			JsonHelper.put( json, Project.JSON_KEY_OWNER, 1234 );

			final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateUser );
			requestBuilder.addUriParameter( ApiStrings.JsonKeys.User, json );

			runJsonObjectCommand( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, requestBuilder.build() );
		}
		catch( final ApiException | InvalidDataException | GeneralScenarioExplorerException e )
		{
			LOGGER.info( "As expected: " + ImaginationSupportUtil.formatStackTrace( e, 1 ) );
			throw e;
		}

		return;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Delete Project
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testDeleteProject() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			// create the entry
			final Project entry = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// now delete it
			runDelete( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, entry );
		}
		catch( final GeneralScenarioExplorerException | ApiException | InvalidDataException e )
		{
			LOGGER.info( ImaginationSupportUtil.formatStackTrace( e ) );
			throw e;
		}

		return;
	}

	///// test authentication /////

	@Test( expected = ApiException.class )
	public void testDeleteAuthorization() throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		showMethodHeader();

		try
		{
			// create the entry
			final Project entry = runNew( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, createValidEntry() );

			// running as the normal user should throw the not-authorized exception...
			runDelete( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, entry );
		}
		catch( final GeneralScenarioExplorerException | ApiException | InvalidDataException e )
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

	private Project createValidEntry() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		return new Project(
			VALID_NAME,
			VALID_DESCRIPTION,
			getUser( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME ).getId(),
			VALID_START,
			VALID_END,
			VALID_DAYS_INCREMENT );
	}

	private List< Project > runList() throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.ListProjects );

		final JSONArray response = runJsonArrayCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, requestBuilder.build() );

		final List< Project > parsedEntries = new ArrayList<>();
		for( int i = 0; i < response.length(); ++i )
		{
			parsedEntries.add( new Project( response.getJSONObject( i ) ) );
		}

		return parsedEntries;
	}

	private Project runGet( final ObjectId id ) throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.GetProject );
		requestBuilder.addUriParameter( ApiStrings.JsonKeys.ProjectorId, id );

		final JSONObject response = runJsonObjectCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, requestBuilder.build() );

		// parse the entry
		return new Project( JsonHelper.getRequiredParameterJSONObject( response, ApiStrings.JsonKeys.Project ) );
	}

	private Project runNew(
		final String usernameToRunJsonObjectCommandAs,
		final Project entry ) throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.NewProject );
		requestBuilder.setBody( entry.toJSON() );

		final JSONObject response = runJsonObjectCommand( usernameToRunJsonObjectCommandAs, requestBuilder.build() );

		// parse the entry
		return new Project( JsonHelper.getRequiredParameterJSONObject( response, ApiStrings.JsonKeys.Project ) );
	}

	private Project runUpdate(
		final String usernameToRunJsonObjectCommandAs,
		final Project entry ) throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.UpdateProject );
		requestBuilder.setBody( entry.toJSON() );

		final JSONObject response = runJsonObjectCommand( usernameToRunJsonObjectCommandAs, requestBuilder.build() );

		// parse the entry
		return new Project( JsonHelper.getRequiredParameterJSONObject( response, ApiStrings.JsonKeys.Project ) );
	}

	private void runDelete(
		final String usernameToRunJsonObjectCommandAs,
		final Project entry ) throws ApiException, InvalidDataException, GeneralScenarioExplorerException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.DeleteProject );
		requestBuilder.setBody( entry.toJSON() );

		runJsonObjectCommand( usernameToRunJsonObjectCommandAs, requestBuilder.build() );

		return;
	}

	private User getUser( final String username ) throws ApiException, GeneralScenarioExplorerException, InvalidDataException
	{
		final ApiRequestBuilder requestBuilder = new ApiRequestBuilder( RestApiRequestInfo.Request.GetUser );
		requestBuilder.addUriParameter( ApiStrings.JsonKeys.Username, username );

		final JSONObject response = runJsonObjectCommand( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, requestBuilder.build() );

		// parse the entry
		return new User( JsonHelper.getRequiredParameterJSONObject( response, ApiStrings.JsonKeys.User ) );
	}
}
