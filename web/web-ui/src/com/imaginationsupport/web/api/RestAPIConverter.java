package com.imaginationsupport.web.api;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.web.api.request.ApiRequest;
import com.imaginationsupport.web.api.request.AvailableRequestInfo;
import com.imaginationsupport.web.exceptions.ApiException;
import com.imaginationsupport.web.exceptions.BadRequestException;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts the REST API requests to the WebSocket version
 */
public abstract class RestAPIConverter
{
	/**
	 * Holds the URI prefix
	 */
	public static final String URI_PREFIX = "/scenarioexplorer/api"; // TODO this should be part of the build process!

	/**
	 * Holds the log4j2 logger instance
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	public static ApiRequest convertRestApiRequest( final RestApiRequestInfo.HttpMethod method, final String uri, final JSONObject payload ) throws ApiException
	{
		if( !uri.startsWith( URI_PREFIX + "/" ) )
		{
			throw new BadRequestException( String.format( "Invalid URI prefix: %s", uri ) );
		}

		LOGGER.debug( String.format( "Converting (%s): %s", method.toString(), uri ) );

		final String[] requestUriParts = uri.substring( URI_PREFIX.length() + 1 ).split( "/" );
		for( final AvailableRequestInfo availableRequest : RequestHandler.getAvailableRequests() )
		{
			LOGGER.debug(
				String.format(
					"Looking at: %s = %s: %s",
					availableRequest.getApiAction(),
					availableRequest.getMethod().toString().toUpperCase(),
					"/" + String.join( "/", availableRequest.getUriParts() ) ) );

			if( method == availableRequest.getMethod() && requestUriParts.length == availableRequest.getUriParts().length )
			{
				boolean matches = true;

				final Map< String, String > uriParameters = new HashMap<>();
				for( int i = 0; i < requestUriParts.length && matches; ++i )
				{
//					LOGGER.debug( String.format( "  %2d: [%s] vs [%s]", i, requestUriParts[ i ], availableRequest.getUriParts()[ i ] ) );

					if( availableRequest.getUriParts()[ i ].startsWith( "{" ) && availableRequest.getUriParts()[ i ].endsWith( "}" ) )
					{
						uriParameters.put( availableRequest.getUriParts()[ i ].substring( 1, availableRequest.getUriParts()[ i ].length() - 1 ), requestUriParts[ i ] );
					}
					else if( !requestUriParts[ i ].equals( availableRequest.getUriParts()[ i ] ) )
					{
						matches = false;
					}
				}

				if( matches )
				{
					return new ApiRequest( availableRequest.getApiAction(), uriParameters, payload );
				}
			}
		}

		throw new BadRequestException( String.format( "Invalid URI (%s): %s", method.toString(), uri ) );
	}
}
