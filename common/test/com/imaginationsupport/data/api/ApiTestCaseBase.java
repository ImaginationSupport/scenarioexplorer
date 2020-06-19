package com.imaginationsupport.data.api;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.data.ImaginationSupportTestCaseBase;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings( "SameParameterValue" )
abstract class ApiTestCaseBase extends ImaginationSupportTestCaseBase
{
	void verifyObjectIdParam( final JSONObject json, final String key, final ObjectId expectedValue ) throws InvalidDataException
	{
		final ObjectId serializedValue = JsonHelper.getRequiredParameterObjectId( json, key, false );
		assertEquals(
			String.format( "Expected serialized %s to be (%s) but found (%s)", key, expectedValue.toHexString(), serializedValue.toHexString() ),
			serializedValue,
			expectedValue );

		return;
	}

	void verifyStringParam( final JSONObject json, final String key, final String expectedValue ) throws InvalidDataException
	{
		final String serializedValue = JsonHelper.getRequiredParameterString( json, key );
		assertEquals(
			String.format( "Expected serialized %s to be (%s) but found (%s)", key, expectedValue, serializedValue ),
			serializedValue,
			expectedValue );

		return;
	}

	void verifyIntegerParam( final JSONObject json, final String key, final int expectedValue ) throws InvalidDataException
	{
		final int serializedValue = JsonHelper.getRequiredParameterInt( json, key );
		assertEquals(
			String.format( "Expected serialized %s to be (%d) but found (%d)", key, expectedValue, serializedValue ),
			serializedValue,
			expectedValue );

		return;
	}

	void verifyDoubleParam( final JSONObject json, final String key, final double expectedValue ) throws InvalidDataException
	{
		final double serializedValue = JsonHelper.getRequiredParameterDouble( json, key );
		assertTrue(
			String.format( "Expected serialized %s to be (%f) but found (%f)", key, expectedValue, serializedValue ),
			Math.abs( serializedValue - expectedValue ) < 0.001 );

		return;
	}

	void verifyLocalDateTimeParam( final JSONObject json, final String key, final LocalDateTime expectedValue ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final LocalDateTime serializedValue = JsonHelper.getRequiredParameterDateTime( json, key );
		final String serializedValueAsString = ImaginationSupportUtil.formatDateTime( serializedValue );
		final String expectedValueAsString = ImaginationSupportUtil.formatDateTime( expectedValue );
		assertEquals(
			String.format( "Expected serialized %s to be (%s) but found (%s)", key, expectedValue, serializedValue ),
			serializedValueAsString,
			expectedValueAsString );

		return;
	}

	void verifyBooleanParam( final JSONObject json, final String key, final boolean expectedValue ) throws InvalidDataException
	{
		final boolean serializedValue = JsonHelper.getRequiredParameterBoolean( json, key );
		assertEquals(
			String.format( "Expected serialized %s to be (%s) but found (%s)", key, expectedValue, serializedValue ),
			serializedValue,
			expectedValue );

		return;
	}

	void verifyStringArrayParam( final JSONObject json, final String key, final Collection< String > expectedValues ) throws InvalidDataException
	{
		final List< String > serialized = JsonHelper.getRequiredParameterStringArray( json, key );

		assertEquals(
			String.format( "Expected %d %s entries, but found %d", expectedValues.size(), key, serialized.size() ),
			serialized.size(),
			expectedValues.size() );

		for( final String expectedEntry : expectedValues )
		{
			assertTrue( String.format( "Could not find entry (%s) in serialized %s list!", expectedEntry, key ), serialized.contains( expectedEntry ) );
		}

		return;
	}

	void verifyObjectIdArrayParam( final JSONObject json, final String key, final ObjectId[] expectedValues ) throws InvalidDataException
	{
		final List< ObjectId > serialized = JsonHelper.getRequiredParameterObjectIdArray( json, key );

		assertEquals(
			String.format( "Expected %d %s entries, but found %d", expectedValues.length, key, serialized.size() ),
			serialized.size(),
			expectedValues.length );

		for( final ObjectId expectedEntry : expectedValues )
		{
			assertTrue( String.format( "Could not find entry (%s) in serialized %s list!", expectedEntry.toHexString(), key ), serialized.contains( expectedEntry ) );
		}

		return;
	}

	void verifyParamNotPresent( final JSONObject json, final String key ) throws InvalidDataException
	{
		assertFalse( String.format( "JSON should NOT contain %s!", key ), JsonHelper.checkParameterExists( json, key ) );

		return;
	}

	void verifyNoExtraEntries( final JSONObject json, final Set< String > knownKeys )
	{
		for( final String jsonKey : json.keySet() )
		{
			assertTrue( String.format( "Key (%s) not expected in api object!", jsonKey ), knownKeys.contains( jsonKey ) );
		}

		return;
	}

	void verifySameObject( final JSONObject a, final JSONObject b )
	{
		final Set< String > keysA = a.keySet();
		final Set< String > keysB = b.keySet();

		assertEquals( String.format( "Different number of keys in the objects (%d vs. %d)", keysA.size(), keysB.size() ), keysA.size(), keysB.size() );

		for( final String key : keysA )
		{
			assertTrue( String.format( "Key not in both objects: %s", key ), keysB.contains( key ) );
		}

		return;
	}
}
