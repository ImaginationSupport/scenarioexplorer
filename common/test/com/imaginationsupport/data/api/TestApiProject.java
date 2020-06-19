package com.imaginationsupport.data.api;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.PlugInManager;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.FeatureType;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TestApiProject extends ApiTestCaseBase
{
	private static final String VALID_PROJECT_NAME = "Test Project";

	private static final String VALID_PROJECT_DESCRIPTION = "Test Project Description";

	private static final int DAYS_INCREMENT = 30;

	@Test
	public void testToJson() throws GeneralScenarioExplorerException, InvalidDataException
	{
		try
		{
			showMethodHeader();

			final Project testProject = createTestProject();

			final JSONObject json = testProject.toJSON();

			LOGGER.debug( json.toString( 4 ) );

			testCoreEntries( json, testProject );

			// check for any other keys
			final Set< String > knownKeys = new HashSet<>();
			knownKeys.add( Project.JsonKeys.Id );
			knownKeys.add( Project.JsonKeys.Name );
			knownKeys.add( Project.JsonKeys.Description );
			knownKeys.add( Project.JsonKeys.CreatedOn );
			knownKeys.add( Project.JsonKeys.LastEditOn );
			knownKeys.add( Project.JsonKeys.Start );
			knownKeys.add( Project.JsonKeys.End );
			knownKeys.add( Project.JsonKeys.DaysIncrement );
			knownKeys.add( Project.JsonKeys.Owner );
			knownKeys.add( Project.JsonKeys.Notifications );
			verifyNoExtraEntries( json, knownKeys );
		}
		catch( final Throwable t )
		{
			LOGGER.fatal( ImaginationSupportUtil.formatStackTrace( t ) );
			throw t;
		}

		return;
	}

	private Project createTestProject() throws GeneralScenarioExplorerException, InvalidDataException
	{
		final ObjectId id = getNextObjectId();
		final ObjectId ownerId = getNextObjectId();
		final LocalDateTime start = LocalDateTime.now().minusDays( 1 );
		final LocalDateTime end = start.plusYears( 3 );

		final Project testProject = new Project( VALID_PROJECT_NAME, VALID_PROJECT_DESCRIPTION, ownerId, start, end, DAYS_INCREMENT );
		testProject.setId( id );

		for( final FeatureType featureType : PlugInManager.getInstance().getFeatureTypes() )
		{
			final String featureName = "my " + featureType.getName();
			final String featureDescription = "description of " + featureType.getName();

			try
			{
				testProject.mapFeature( featureType, "{}", featureName, featureDescription, null, null );
			}
			catch( final ClassNotFoundException | InstantiationException | IllegalAccessException | InvalidDataException | GeneralScenarioExplorerException e )
			{
				throw new GeneralScenarioExplorerException( "Error mapping feature!", e );
			}
		}

		return testProject;
	}

	private void testCoreEntries( final JSONObject json, final Project expected ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		verifyObjectIdParam( json, Project.JsonKeys.Id, expected.getId() );
		verifyStringParam( json, Project.JsonKeys.Name, VALID_PROJECT_NAME );
		verifyStringParam( json, Project.JsonKeys.Description, VALID_PROJECT_DESCRIPTION );
		verifyLocalDateTimeParam( json, Project.JsonKeys.CreatedOn, expected.getCreatedOn() );
		verifyLocalDateTimeParam( json, Project.JsonKeys.LastEditOn, expected.getLastEditOn() );
		verifyLocalDateTimeParam( json, Project.JsonKeys.Start, expected.getStart() );
		verifyLocalDateTimeParam( json, Project.JsonKeys.End, expected.getEnd() );
		verifyIntegerParam( json, Project.JsonKeys.DaysIncrement, expected.getDaysIncrement() );
		verifyStringParam( json, Project.JsonKeys.Owner, expected.getOwnerId().toHexString() );
//		verifyStringArrayParam( json, Project.JsonKeys.Notifications, expected.getNotifications() ); // TODO check this list!

		return;
	}
}
