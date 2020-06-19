package com.imaginationsupport.web;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import org.junit.AfterClass;
import org.junit.Before;

public class TestRequestHandlerBasic extends ApiRequestTestBase
{
//	final static String INVALID_USERNAME = "evilhacker";

//	@BeforeClass
//	public static void setUpClass() throws Exception
//	{
//	}

	@Before
	public void setUp() throws Exception
	{
		cleanDatabase();

		return;
	}

//	@After
//	public void tearDown()
//	{
//		return;
//	}

	@AfterClass
	public static void tearDownClass() throws GeneralScenarioExplorerException
	{
		dropDatabase();

		return;
	}

//	@Test
//	public void testIsJsonRequest() throws InvalidDataException
//	{
//		showMethodHeader();
//
//		final SortedMap< String, Boolean > entries = getIsJsonJavaScriptSourceMap();
//		for( final String entry : entries.keySet() )
//		{
//			final JSONObject json = new JSONObject();
//			JsonHelper.put( json, ApiStrings.JsonKeys.Action, entry );
//
//			if( entries.get( entry ) )
//			{
//				assertTrue( entry, RequestHandler.isJsonRequest( json ) );
//			}
//			else
//			{
//				assertFalse( entry, RequestHandler.isJsonRequest( json ) );
//			}
//		}
//
//		return;
//	}

//	@Test
//	public void testIsJavascriptSourceRequest() throws InvalidDataException
//	{
//		showMethodHeader();
//
//		final SortedMap< String, Boolean > entries = getIsJsonJavaScriptSourceMap();
//		for( final String entry : entries.keySet() )
//		{
//			final JSONObject json = new JSONObject();
//			JsonHelper.put( json, ApiStrings.JsonKeys.Action, entry );
//
//			if( entries.get( entry ) )
//			{
//				assertFalse( entry, RequestHandler.isJavascriptSourceRequest( json ) );
//			}
//			else
//			{
//				assertTrue( entry, RequestHandler.isJavascriptSourceRequest( json ) );
//			}
//		}
//
//		return;
//	}

//	@Test (expected = Exception.class)

//	private SortedMap< String, Boolean > getIsJsonJavaScriptSourceMap()
//	{
//		final SortedMap< String, Boolean > entries = new TreeMap<>();
//
//		entries.put( ApiStrings.Requests.ListUsers, true );
//		entries.put( ApiStrings.Requests.GetUser, true );
//		entries.put( ApiStrings.Requests.NewUser, true );
//		entries.put( ApiStrings.Requests.UpdateUser, true );
//		entries.put( ApiStrings.Requests.DeleteUser, true );
//
//		entries.put( ApiStrings.Requests.ListProjects, true );
//		entries.put( ApiStrings.Requests.GetProject, true );
//		entries.put( ApiStrings.Requests.NewProject, true );
//		entries.put( ApiStrings.Requests.UpdateProject, true );
//		entries.put( ApiStrings.Requests.DeleteProject, true );
//
//		entries.put( ApiStrings.Requests.ListViews, true );
//		entries.put( ApiStrings.Requests.GetView, true );
//		entries.put( ApiStrings.Requests.NewView, true );
//		entries.put( ApiStrings.Requests.UpdateView, true );
//		entries.put( ApiStrings.Requests.DeleteView, true );
//
//		entries.put( ApiStrings.Requests.ListTimelineEvents, true );
//		entries.put( ApiStrings.Requests.GetTimelineEvent, true );
//		entries.put( ApiStrings.Requests.NewTimelineEvent, true );
//		entries.put( ApiStrings.Requests.UpdateTimelineEvent, true );
//		entries.put( ApiStrings.Requests.DeleteTimelineEvent, true );
//
//		entries.put( ApiStrings.Requests.ListConditioningEvents, true );
//		entries.put( ApiStrings.Requests.GetConditioningEvent, true );
//		entries.put( ApiStrings.Requests.NewConditioningEvent, true );
//		entries.put( ApiStrings.Requests.UpdateConditioningEvent, true );
//		entries.put( ApiStrings.Requests.DeleteConditioningEvent, true );
//
//		entries.put( ApiStrings.Requests.ListFeatureTypes, true );
//		entries.put( ApiStrings.Requests.GetFeatureType, true );
//		entries.put( ApiStrings.Requests.GetFeatureTypeSource, false );
//
//		entries.put( ApiStrings.Requests.ListPreconditions, true );
//		entries.put( ApiStrings.Requests.GetPrecondition, true );
//		entries.put( ApiStrings.Requests.GetPreconditionSource, false );
//
//		entries.put( ApiStrings.Requests.ListOutcomeEffects, true );
//		entries.put( ApiStrings.Requests.GetOutcomeEffect, true );
//		entries.put( ApiStrings.Requests.GetOutcomeEffectSource, false );
//
//		entries.put( ApiStrings.Requests.ListProjectors, true );
//		entries.put( ApiStrings.Requests.GetProjector, true );
//		entries.put( ApiStrings.Requests.GetProjectorSource, false );
//
//		entries.put( ApiStrings.Requests.ListDashboard, true );
//
//		return entries;
//	}
}
