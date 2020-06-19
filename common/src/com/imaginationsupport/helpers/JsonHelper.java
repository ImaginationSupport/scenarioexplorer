package com.imaginationsupport.helpers;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings( { "unused", "WeakerAccess", "UnusedReturnValue" } )
public class JsonHelper
{
	public static JSONObject parseObject( final String jsonText ) throws InvalidDataException
	{
		if( jsonText == null || jsonText.isEmpty() )
		{
			throw new InvalidDataException( "JSON text cannot be null or empty!" );
		}

		return new JSONObject( new JSONTokener( jsonText ) );
	}

	public static JSONArray parseArray( final String jsonText ) throws InvalidDataException
	{
		if( jsonText == null || jsonText.isEmpty() )
		{
			throw new InvalidDataException( "JSON text cannot be null or empty!" );
		}

		return new JSONArray( new JSONTokener( jsonText ) );
	}

	private static void verifyType( final Object raw, final Class< ? > expectedType ) throws InvalidDataException
	{
		if(
			!( raw.getClass().equals( expectedType ) )
				&& !( raw.getClass().equals( Integer.class ) && expectedType.equals( Double.class ) )
				&& !( raw.getClass().equals( Double.class ) && expectedType.equals( Integer.class ) )
//				&& !( raw.getClass().equals( String.class ) && expectedType.equals( ObjectId.class ) )
		)
		{
			throw new InvalidDataException( String.format(
				"JSON value is the wrong type!  Expected %s but found %s for raw value %s",
				expectedType.getName(),
				raw.getClass().getName(),
				raw.toString()
			) );
		}
	}

	private static void verifyParameter( final JSONObject source, final String key, final Class< ? > expectedType ) throws InvalidDataException
	{
		if( source == null )
		{
			throw new InvalidDataException( "JSON source object cannot be null!" );
		}

		if( !checkParameterExists( source, key ) )
		{
			throw new InvalidDataException( String.format( "Key \"%s\" not found in JSON source object!", key ) );
		}

		if( expectedType == null )
		{
			throw new InvalidDataException( "Type cannot be null!" );
		}

		final Object raw = source.get( key );

//		System.out.println();
//		System.out.println( String.format( ">>> object:   %s", source.toString() ) );
//		System.out.println( String.format( ">>> key:      %s", key ) );
//		System.out.println( String.format( ">>> value:    %s", source.get( key ).toString() ) );
//		System.out.println( String.format( ">>> type:     %s", source.get( key ).getClass().getName() ) );
//		System.out.println( String.format( ">>> expected: %s", expectedType.getName() ) );
//		System.out.println( String.format( ">>> found:    %s", raw.getClass().getName() ) );

		verifyType( raw, expectedType );

		return;
	}

	public static String getRequiredParameterString( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, String.class );

		return source.getString( key );
	}

	public static ObjectId getRequiredParameterObjectId( final JSONObject source, final String key, final boolean allowNull ) throws InvalidDataException
	{
		if( allowNull && ( !checkParameterExists( source, key ) || checkParameterJsonNull( source, key ) ) )
		{
			return null;
		}
		else
		{
			return getRequiredParameterObjectId( source, key );
		}
	}

	public static ObjectId getRequiredParameterObjectId( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, String.class );

		return new ObjectId( source.getString( key ) );
	}

	public static int getRequiredParameterInt( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, Integer.class );

		return source.getInt( key );
	}

	public static boolean getRequiredParameterBoolean( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, Boolean.class );

		return source.getBoolean( key );
	}

	public static double getRequiredParameterDouble( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, Double.class );

		return source.getDouble( key );
	}

	public static LocalDateTime getRequiredParameterDateTime( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, String.class );

		return ImaginationSupportUtil.parseDateTime( source.getString( key ) );
	}

	public static LocalDateTime getRequiredParameterDateOnly( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, String.class );

		return ImaginationSupportUtil.parseDateOnly( source.getString( key ) );
	}

	public static JSONArray getRequiredParameterJSONArray( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, JSONArray.class );

		return source.getJSONArray( key );
	}

	public static List< String > getRequiredParameterStringArray( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, JSONArray.class );

		final List< String > parsed = new ArrayList<>();

		final JSONArray rawArray = source.getJSONArray( key );
		for( int i = 0; i < rawArray.length(); ++i )
		{
			final Object rawItem = rawArray.get( i );

			verifyType( rawItem, String.class );

			parsed.add( rawArray.getString( i ) );
		}

		return parsed;
	}

	public static List< ObjectId > getRequiredParameterObjectIdArray( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, JSONArray.class );

		final List< ObjectId > parsed = new ArrayList<>();

		final JSONArray rawArray = source.getJSONArray( key );
		for( int i = 0; i < rawArray.length(); ++i )
		{
			final Object rawItem = rawArray.get( i );

			verifyType( rawItem, String.class );

			parsed.add( new ObjectId( rawArray.getString( i ) ) );
		}

		return parsed;
	}

	public static JSONObject getRequiredParameterJSONObject( final JSONObject source, final String key ) throws InvalidDataException
	{
		verifyParameter( source, key, JSONObject.class );

		return source.getJSONObject( key );
	}

	public static boolean checkParameterExists( final JSONObject source, final String key ) throws InvalidDataException
	{
		if( source == null )
		{
			throw new InvalidDataException( "JSON source object cannot be null!" );
		}

		return source.has( key );
	}

	@SuppressWarnings( { "unused", "WeakerAccess" } )
	public static boolean checkParameterJsonNull( final JSONObject source, final String key ) throws InvalidDataException
	{
		return checkParameterExists( source, key ) && source.isNull( key );
	}

	public static String getOptionalParameterString( final JSONObject source, final String key ) throws InvalidDataException
	{
		return getOptionalParameterString( source, key, null );
	}

	public static String getOptionalParameterString( final JSONObject source, final String key, final String defaultValue ) throws InvalidDataException
	{
		if( source == null )
		{
			throw new InvalidDataException( "JSON source object cannot be null!" );
		}

		if( source.has( key ) && !source.isNull( key ) )
		{
			verifyParameter( source, key, String.class );

			return source.getString( key );
		}
		else
		{
			return defaultValue;
		}
	}

	public static boolean getOptionalParameterBoolean( final JSONObject source, final String key, final boolean defaultValue ) throws InvalidDataException
	{
		if( source == null )
		{
			throw new InvalidDataException( "JSON source object cannot be null!" );
		}

		if( source.has( key ) && !source.isNull( key ) )
		{
			verifyParameter( source, key, Boolean.class );

			return source.getBoolean( key );
		}
		else
		{
			return defaultValue;
		}
	}

	public static int getOptionalParameterInt( final JSONObject source, final String key, final int defaultValue ) throws InvalidDataException
	{
		if( source == null )
		{
			throw new InvalidDataException( "JSON source object cannot be null!" );
		}

		if( source.has( key ) && !source.isNull( key ) )
		{
			verifyParameter( source, key, Integer.class );

			return source.getInt( key );
		}
		else
		{
			return defaultValue;
		}
	}

	public static double getOptionalParameterDouble( final JSONObject source, final String key, final double defaultValue ) throws InvalidDataException
	{
		if( source == null )
		{
			throw new InvalidDataException( "JSON source object cannot be null!" );
		}

		if( source.has( key ) && !source.isNull( key ) )
		{
			verifyParameter( source, key, Double.class );

			return source.getDouble( key );
		}
		else
		{
			return defaultValue;
		}
	}

	public static List< ObjectId > getOptionalParameterObjectIdArray( final JSONObject source, final String key ) throws InvalidDataException
	{
		if( source == null )
		{
			throw new InvalidDataException( "JSON source object cannot be null!" );
		}

		if( source.has( key ) && !source.isNull( key ) )
		{
			return getRequiredParameterObjectIdArray( source, key );
		}
		else
		{
			return null;
		}
	}

	public static JSONObject getOptionalParameterJSONObject( final JSONObject source, final String key, final JSONObject defaultValue ) throws InvalidDataException
	{
		if( source == null )
		{
			throw new InvalidDataException( "JSON source object cannot be null!" );
		}

		if( source.has( key ) && !source.isNull( key ) )
		{
			final Object raw = source.get( key );

			if( !( raw instanceof JSONObject ) )
			{
				throw new InvalidDataException( String.format(
					"Key \"%s\" in JSON source object wrong type!  Expected %s, found %s for %s",
					key,
					JSONObject.class.getName(),
					raw.getClass().getName(),
					raw.toString()
				) );
			}

			return (JSONObject)raw;
		}
		else
		{
			return defaultValue;
		}
	}

	/**
	 * @param collection the items to convert
	 * @param <T>        Must implement ApiObject
	 *
	 * @return the generated JSON Array
	 * @see ApiObject
	 */
	public static < T extends ApiObject > JSONArray toJSONArray( final Collection< T > collection ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final JSONArray jsonArray = new JSONArray();

		if( collection != null )
		{
			for( final ApiObject entry : collection )
			{
				jsonArray.put( entry.toJSON() );
			}
		}

		return jsonArray;
	}

	@SuppressWarnings( { "unused", "WeakerAccess" } )
	public static JSONArray toJSONArrayObjectIds( final Collection< ObjectId > collection )
	{
		final JSONArray jsonArray = new JSONArray();

		if( collection != null )
		{
			for( final ObjectId entry : collection )
			{
				jsonArray.put( entry.toHexString() );
			}
		}

		return jsonArray;
	}

	public static JSONArray toJSONArrayStrings( final Collection< String > collection )
	{
		final JSONArray jsonArray = new JSONArray();

		if( collection != null )
		{

			for( final String entry : collection )
			{
				jsonArray.put( entry );
			}
		}

		return jsonArray;
	}

	public static JSONObject putNull( final JSONObject json, final String key )
	{
		json.put( key, JSONObject.NULL );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final String value )
	{
		json.put( key, value == null ? JSONObject.NULL : value );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final int value )
	{
		json.put( key, value );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final double value )
	{
		json.put( key, value );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final boolean value )
	{
		json.put( key, value );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final LocalDateTime value ) throws GeneralScenarioExplorerException
	{
		json.put( key, value == null ? JSONObject.NULL : ImaginationSupportUtil.formatDateTime( value ) );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final ObjectId value )
	{
		json.put( key, value == null ? JSONObject.NULL : value.toHexString() );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final LocalDate value )
	{
		json.put( key, value == null ? JSONObject.NULL : value );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final JSONObject value )
	{
		json.put( key, value == null ? JSONObject.NULL : value );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final JSONArray value )
	{
		json.put( key, value == null ? JSONObject.NULL : value );
		return json;
	}

	public static JSONObject put( final JSONObject json, final String key, final ApiObject value )
	{
		json.put( key, value == null ? JSONObject.NULL : value );
		return json;
	}

	public static < T extends ApiObject > JSONObject put(
		final JSONObject json,
		final String key,
		final Collection< T > value ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		json.put( key, value == null ? JSONObject.NULL : toJSONArray( value ) );
		return json;
	}

	public static JSONObject putStrings( final JSONObject json, final String key, final Collection< String > value )
	{
		json.put( key, value == null ? JSONObject.NULL : toJSONArrayStrings( value ) );
		return json;
	}

	public static JSONObject putObjectIds( final JSONObject json, final String key, final Collection< ObjectId > value )
	{
		json.put( key, value == null ? JSONObject.NULL : toJSONArrayObjectIds( value ) );
		return json;
	}

//	public static JSONObject put( final JSONObject json, final String key, final Map< String, String > value )
//	{
//		final JSONObject map = new JSONObject();
//		if( value != null )
//		{
//			for( final String mapKey : value.keySet() )
//			{
//				map.put( mapKey, value.get( mapKey ) );
//			}
//		}
//
//		json.put( key, map );
//
//		return json;
//	}

//	public static < T extends ApiObject > JSONObject put( final JSONObject json, final String key, final Map< String, T > value ) throws InvalidDataException, GeneralScenarioExplorerException
//	{
//		final JSONObject map = new JSONObject();
//		if( value != null )
//		{
//			for( final String mapKey : value.keySet() )
//			{
//				map.put( mapKey, value.get( mapKey ).toJSON() );
//			}
//		}
//
//		json.put( key, map );
//
//		return json;
//	}

	public static JSONObject remove( final JSONObject json, final String key )
	{
		json.remove( key );
		return json;
	}
}
