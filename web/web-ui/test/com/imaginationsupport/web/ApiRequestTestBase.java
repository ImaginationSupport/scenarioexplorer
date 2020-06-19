package com.imaginationsupport.web;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.data.ImaginationSupportTestCaseBase;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.web.api.RequestHandler;
import com.imaginationsupport.web.api.request.ApiRequest;
import com.imaginationsupport.web.api.response.ApiResponse;
import com.imaginationsupport.web.api.response.JsonArrayResponse;
import com.imaginationsupport.web.api.response.JsonObjectResponse;
import com.imaginationsupport.web.exceptions.ApiException;
import org.json.JSONArray;
import org.json.JSONObject;

abstract class ApiRequestTestBase extends ImaginationSupportTestCaseBase
{
	JSONObject runJsonObjectCommand(
		final String usernameToRunCommandAs,
		final ApiRequest request ) throws ApiException, GeneralScenarioExplorerException
	{
		LOGGER.info( "request:" );
		LOGGER.info( String.format( "    request id: %s", request.getRequestId() ) );
		LOGGER.info( String.format( "    uri params: %s", ImaginationSupportUtil.joinMap( request.getUriParameters(), ",", "=" ) ) );
		LOGGER.info( String.format( "    body:       %s", request.getBody() == null ? "(null)" : request.getBody().toString( 4 ) ) );

		// run the request handler as the test user
		final ApiResponse response = RequestHandler.handleRequest( usernameToRunCommandAs, request );

		if( response == null )
		{
			throw new GeneralScenarioExplorerException( "Received null response from API" );
		}
		else if( response instanceof JsonObjectResponse )
		{
			final JSONObject json = ( (JsonObjectResponse)response ).getContent();

			LOGGER.info( "response:" );
			LOGGER.info( json.toString( 4 ) );

			return json;
		}
		else if( response instanceof JsonArrayResponse )
		{
			throw new GeneralScenarioExplorerException( "Expected JSON Object but found JSON Array!" );
		}
		else
		{
			throw new GeneralScenarioExplorerException( "Invalid API response type!" );
		}
	}

	JSONArray runJsonArrayCommand(
		final String usernameToRunCommandAs,
		final ApiRequest request ) throws ApiException, GeneralScenarioExplorerException
	{
		LOGGER.info( "request:" );
		LOGGER.info( String.format( "    request id: %s", request.getRequestId() ) );
		LOGGER.info( String.format( "    uri params: %s", ImaginationSupportUtil.joinMap( request.getUriParameters(), ",", "=" ) ) );
		LOGGER.info( String.format( "    body:       %s", request.getBody() == null ? "(null)" : request.getBody().toString( 4 ) ) );

		// run the request handler as the test user
		final ApiResponse response = RequestHandler.handleRequest( usernameToRunCommandAs, request );

		if( response == null )
		{
			throw new GeneralScenarioExplorerException( "Received null response from API" );
		}
		else if( response instanceof JsonObjectResponse )
		{
			throw new GeneralScenarioExplorerException( "Expected JSON Array but found JSON Object!" );
		}
		else if( response instanceof JsonArrayResponse )
		{
			final JSONArray json = ( (JsonArrayResponse)response ).getContent();

			LOGGER.info( "response:" );
			LOGGER.info( json.toString( 4 ) );

			return json;
		}
		else
		{
			throw new GeneralScenarioExplorerException( "Invalid API response type!" );
		}
	}
}
