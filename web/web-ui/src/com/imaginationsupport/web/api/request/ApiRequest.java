package com.imaginationsupport.web.api.request;

import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.json.JSONObject;

import java.util.Map;

public class ApiRequest
{
	private final RestApiRequestInfo.Request mRequestId;

	private final Map< String, String > mUriParameters;

	private final JSONObject mBody;

	public ApiRequest( final RestApiRequestInfo.Request requestId, final Map< String, String > uriParameters, final JSONObject body )
	{
		mRequestId = requestId;
		mUriParameters = uriParameters;
		mBody = body;

		return;
	}

	public ApiRequest( final JSONObject webSocketMessage ) throws InvalidDataException
	{
		throw new InvalidDataException( "Not implemented!" ); // TODO finish!
	}

	public RestApiRequestInfo.Request getRequestId()
	{
		return mRequestId;
	}

	public Map< String, String > getUriParameters()
	{
		return mUriParameters;
	}

	public JSONObject getBody()
	{
		return mBody;
	}
}
