package com.imaginationsupport.web;

import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.web.api.request.ApiRequest;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiRequestBuilder
{
	private final RestApiRequestInfo.Request mRequestId;

	private final Map< String, String > mUriParameters = new HashMap<>();

	private JSONObject mBody = null;

	public ApiRequestBuilder( final RestApiRequestInfo.Request requestId )
	{
		mRequestId = requestId;
		return;
	}

	public void addNullUriParameter( final String key )
	{
		mUriParameters.put( key, null );
		return;
	}

	public void addUriParameter( final String key, final String value )
	{
		mUriParameters.put( key, value );
		return;
	}

	public void addUriParameter( final String key, final int value )
	{
		mUriParameters.put( key, Integer.toString( value ) );
		return;
	}

	public void addUriParameter( final String key, final ObjectId value )
	{
		mUriParameters.put( key, value.toHexString() );
		return;
	}

	public void setBody( final JSONObject body )
	{
		mBody = body;

		return;
	}

	public ApiRequest build()
	{
		return new ApiRequest( mRequestId, mUriParameters, mBody );
	}
}
