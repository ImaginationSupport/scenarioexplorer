package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.web.exceptions.BadRequestException;
import org.bson.types.ObjectId;

import java.util.Map;

public abstract class RestApiRequestHandlerBase
{
	/**
	 * Helper method to get a string URI parameter
	 *
	 * @param uriParameters the parameter list from the request
	 * @return the value
	 */
	static String getStringParameter( final Map< String, String > uriParameters, final String key ) throws BadRequestException
	{
		if( uriParameters == null || !uriParameters.containsKey( key ) )
		{
			throw new BadRequestException( String.format( "Parameter missing: %s", key ) );
		}

		final String valueRaw = uriParameters.get( key );

		if( valueRaw == null || valueRaw.trim().isEmpty() )
		{
			throw new BadRequestException( String.format( "Parameter \"%s\" cannot be null or empty!", key ) );
		}

		return valueRaw;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Helper method to get an ObjectId URI parameter
	 *
	 * @param uriParameters the parameter list from the request
	 * @return the value
	 */
	static ObjectId getObjectIdParameter( final Map< String, String > uriParameters, final String key ) throws BadRequestException
	{
		return new ObjectId( getStringParameter( uriParameters, key ) );
	}
}
