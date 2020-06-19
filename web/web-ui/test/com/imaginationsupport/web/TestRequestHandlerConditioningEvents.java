package com.imaginationsupport.web;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.NotAuthorizedException;
import org.junit.*;

import static junit.framework.TestCase.fail;

public class TestRequestHandlerConditioningEvents extends ApiRequestTestBase
{
	final static String INVALID_USERNAME = "evilhacker";

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
//	public void tearDown() throws Exception
//	{
//		return;
//	}

	@AfterClass
	public static void tearDownClass() throws Exception
	{
		dropDatabase();

		return;
	}

//	@Test (expected = Exception.class)

	@Test
	public void testListViews() throws NotAuthorizedException, GeneralScenarioExplorerException
	{
		showMethodHeader();

		fail( "Not implemented." );

		return;
	}
}
