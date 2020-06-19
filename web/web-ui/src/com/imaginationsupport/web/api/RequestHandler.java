package com.imaginationsupport.web.api;

import com.imaginationsupport.API;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.ReflectionHelper;
import com.imaginationsupport.web.api.request.ApiRequest;
import com.imaginationsupport.web.api.request.AvailableRequestInfo;
import com.imaginationsupport.web.api.response.ApiResponse;
import com.imaginationsupport.web.exceptions.ApiException;
import com.imaginationsupport.web.exceptions.BadRequestException;
import com.imaginationsupport.web.exceptions.InternalServerErrorException;
import com.imaginationsupport.web.exceptions.NotFoundException;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class RequestHandler
{
	/**
	 * Holds the set of available API requests
	 */
	private static final SortedSet< AvailableRequestInfo > mAvailableRequests;

	/**
	 * Holds the log4j2 logger instance
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	static
	{
		mAvailableRequests = new TreeSet<>();

//		LOGGER.debug( "Loading available API requests..." );
		try
		{
			final Set< Class< ? > > classesToScan = ReflectionHelper.listClassesInPackage( false, "com.imaginationsupport.web.api.handlers" );
			for( final Class< ? > classToScan : classesToScan )
			{
				final RestApiHandlerInfo categoryAnnotation = classToScan.getAnnotation( RestApiHandlerInfo.class );
				if( categoryAnnotation != null )
				{
					for( final Method method : classToScan.getDeclaredMethods() )
					{
						final RestApiRequestInfo requestAnnotation = method.getAnnotation( RestApiRequestInfo.class );
						if( requestAnnotation != null )
						{
							if( Modifier.isPrivate( method.getModifiers() ) || !Modifier.isStatic( method.getModifiers() ) )
							{
								LOGGER.warn( String.format(
									"Method inaccessible and will not be used: %s.%s = %s",
									classToScan.getSimpleName(),
									method.getName(),
									Modifier.toString( method.getModifiers() ) ) );
							}
							else
							{
								mAvailableRequests.add(
									new AvailableRequestInfo(
										categoryAnnotation,
										requestAnnotation,
										method.getAnnotation( RestApiRequestParameters.class ),
										method ) );
							}
						}
					}
				}
			}
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.fatal( "Error loading API requests!" );
			LOGGER.fatal( ImaginationSupportUtil.formatStackTrace( e ) );
		}

//		LOGGER.debug( "Available API Requests:" );
//		for( final AvailableRequestInfo availableRequestInfo : mAvailableRequests )
//		{
//			LOGGER.debug( availableRequestInfo.toString() );
//		}
	}

	public static Set< AvailableRequestInfo > getAvailableRequests()
	{
		return mAvailableRequests;
	}

	public static ApiResponse handleRequest( final String requestUsername, final ApiRequest request ) throws ApiException
	{
		try( final API api = new API() )
		{
			final User requestUser;

			try
			{
				requestUser = api.findUser( requestUsername, true );
			}
			catch( final InvalidDataException e )
			{
				throw new NotFoundException( String.format( "Could not find user: %s", requestUsername ), e );
			}

			for( final AvailableRequestInfo requestSetup : mAvailableRequests )
			{
				if( requestSetup.getApiAction() == request.getRequestId() )
				{
					try
					{
						return requestSetup.run( api, requestUser, request.getUriParameters(), request.getBody() );
					}
					catch( final GeneralScenarioExplorerException e )
					{
						throw new InternalServerErrorException( String.format( "Error running handler: %s", requestSetup.getApiAction() ), e );
					}
				}
			}
		}

		throw new BadRequestException( String.format( "Unknown API action: %s", request.getRequestId() ) );
	}
}
