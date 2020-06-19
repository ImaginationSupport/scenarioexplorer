package com.imaginationsupport.web.api.request;

import com.imaginationsupport.API;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.annotations.RestApiRequestParameterInfo;
import com.imaginationsupport.annotations.RestApiRequestParameters;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.web.api.response.*;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class AvailableRequestInfo implements Comparable< AvailableRequestInfo >
{
	private final RestApiHandlerInfo mCategoryAnnotation;
	private final RestApiRequestInfo mRequestAnnotation;
	private final List< RestApiRequestParameterInfo > mParameterAnnotations;

	private final Method mApiHandlerClassMethod;

	/**
	 * Holds the log4j2 logger instance
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	public RestApiRequestInfo.HttpMethod getMethod()
	{
		return mRequestAnnotation.method();
	}

	public String[] getUriParts()
	{
		return mRequestAnnotation.uriParts();
	}

	public RestApiRequestInfo.Request getApiAction()
	{
		return mRequestAnnotation.request();
	}

	public String getApiSummary()
	{
		return mRequestAnnotation.summary();
	}

	public String getApiDescription()
	{
		return mRequestAnnotation.description();
	}

	public RestApiRequestInfo.SchemaType getResponseSchemaType()
	{
		return mRequestAnnotation.responseSchemaType();
	}

	public RestApiRequestInfo.SchemaDefinition getResponseSchemaDefinition()
	{
		return mRequestAnnotation.responseSchemaDefinition();
	}

	public List< RestApiRequestParameterInfo > getParameters()
	{
		return mParameterAnnotations;
	}

	public AvailableRequestInfo(
		final RestApiHandlerInfo categoryAnnotation,
		final RestApiRequestInfo requestAnnotation,
		final RestApiRequestParameters parameterAnnotations,
		final Method requestHandlerMethod )
	{
		mCategoryAnnotation = categoryAnnotation;
		mRequestAnnotation = requestAnnotation;
		mParameterAnnotations = parameterAnnotations == null
			? null
			: Arrays.asList( parameterAnnotations.value() );

		mApiHandlerClassMethod = requestHandlerMethod;

		return;
	}

	public RestApiHandlerInfo.CategoryNames getCategory()
	{
		return mCategoryAnnotation.name();
	}

	public String getInputMimeType() throws GeneralScenarioExplorerException
	{
		switch( mRequestAnnotation.method() )
		{
			case Get:
			case Put:
			case Delete:
				return "application/json";

			case Post:
				return "multipart/form-data";

			default:
				throw new GeneralScenarioExplorerException( String.format( "Unknown HTTP method: %s", mRequestAnnotation.method() ) );
		}
	}

	public ApiResponse run( final API api, final User requestUser, final Map< String, String > uriParameters, final JSONObject body ) throws GeneralScenarioExplorerException
	{
		try
		{
			if( !Modifier.isStatic( mApiHandlerClassMethod.getModifiers() ) )
			{
				throw new GeneralScenarioExplorerException( "Handler method must be static!" );
			}

			final Object resultRaw;
			switch( mApiHandlerClassMethod.getParameterCount() )
			{
				case 1:
					resultRaw = mApiHandlerClassMethod.invoke( null, api );
					break;

				case 2:
					resultRaw = mApiHandlerClassMethod.invoke( null, api, requestUser );
					break;

				case 3:
					resultRaw = mApiHandlerClassMethod.invoke( null, api, requestUser, uriParameters );
					break;

				case 4:
					resultRaw = mApiHandlerClassMethod.invoke( null, api, requestUser, uriParameters, body );
					break;

				default:
					throw new GeneralScenarioExplorerException( "Invalid handler function!" );
			}

			LOGGER.debug( "result:" );
			LOGGER.debug( resultRaw );

			if( resultRaw instanceof JSONObject )
			{
				if( mRequestAnnotation.isDownload() )
				{
					return new JsonContentFileDownloadResponse( mRequestAnnotation.downloadFilename(), (JSONObject)resultRaw );
				}
				else
				{
					return new JsonObjectResponse( (JSONObject)resultRaw );
				}
			}
			else if( resultRaw instanceof JSONArray )
			{
				return new JsonArrayResponse( (JSONArray)resultRaw );
			}
			else if( resultRaw instanceof String )
			{
				return new RedirectResponse( (String)resultRaw );
			}
			else
			{
				throw new GeneralScenarioExplorerException( String.format( "Unknown handler response type: %s", resultRaw ) );
			}
		}
		catch( final IllegalAccessException | InvocationTargetException e )
		{
			throw new GeneralScenarioExplorerException( "Error running handler!", e );
		}
	}

	@Override
	public String toString()
	{
		return String.format(
			"%s: %s = %s",
			mRequestAnnotation.method().toString().toUpperCase(),
			"/" + String.join( "/", mRequestAnnotation.uriParts() ),
			mRequestAnnotation.request().toString()
		);
	}

	@Override
	public int compareTo( final AvailableRequestInfo other )
	{
		if( mRequestAnnotation.method() == other.mRequestAnnotation.method() )
		{
			return mRequestAnnotation.request().compareTo( other.mRequestAnnotation.request() );
		}
		else
		{
			return mRequestAnnotation.method().compareTo( other.mRequestAnnotation.method() );
		}
	}
}
