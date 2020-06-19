package com.imaginationsupport.web;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.web.api.RequestHandler;
import com.imaginationsupport.web.api.RestAPIConverter;
import com.imaginationsupport.web.api.request.ApiRequest;
import com.imaginationsupport.web.api.response.*;
import com.imaginationsupport.web.exceptions.ApiException;
import com.imaginationsupport.web.exceptions.BadRequestException;
import com.imaginationsupport.web.exceptions.InternalServerErrorException;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet( name = "api", urlPatterns = { "/api/*" } )
public class ImaginationSupportRestApiServlet extends ImaginationSupportServletBase
{
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	@Override
	public void init( final ServletConfig config ) throws ServletException
	{
		super.init( config );

		try
		{
			initDatabaseConnection();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( e ) );

			throw new ServletException( "Error initializing the database connection", e );
		}

		LOGGER.debug( "REST API Servlet initialized" );

		return;
	}

	@Override
	protected void doGet( final HttpServletRequest request, final HttpServletResponse response ) throws IOException
	{
		handleRequest( RestApiRequestInfo.HttpMethod.Get, request, response );
		return;
	}

	@Override
	protected void doPost( final HttpServletRequest request, final HttpServletResponse response ) throws IOException
	{
		handleRequest( RestApiRequestInfo.HttpMethod.Post, request, response );
		return;
	}

	@Override
	protected void doPut( final HttpServletRequest request, final HttpServletResponse response ) throws IOException
	{
		handleRequest( RestApiRequestInfo.HttpMethod.Put, request, response );
		return;
	}

	@Override
	protected void doDelete( final HttpServletRequest request, final HttpServletResponse response ) throws IOException
	{
		handleRequest( RestApiRequestInfo.HttpMethod.Delete, request, response );
		return;
	}

	/**
	 * Handles the incoming request
	 *
	 * @param method   the HTTP method used
	 * @param request  the incoming request
	 * @param response the outgoing response
	 */
	private void handleRequest( final RestApiRequestInfo.HttpMethod method, final HttpServletRequest request, final HttpServletResponse response ) throws IOException
	{
		final JSONObject payload = method == RestApiRequestInfo.HttpMethod.Get || method == RestApiRequestInfo.HttpMethod.Delete
			? null
			: new JSONObject( request.getReader().lines().collect( Collectors.joining( System.lineSeparator() ) ) );

		LOGGER.debug( String.format( "%s query string: %s", method.toString().toUpperCase(), request.getQueryString() ) );
		LOGGER.debug( String.format( "%s payload:      %s", method.toString().toUpperCase(), payload == null ? "(null)" : payload ) );

		final ApiRequest parsedRequest;
		try
		{
			if( request.getRequestURI().startsWith( RestAPIConverter.URI_PREFIX + "/" ) )
			{
				parsedRequest = RestAPIConverter.convertRestApiRequest( method, request.getRequestURI(), payload );
			}
			else
			{
				throw new BadRequestException( String.format( "Unable to parse URI: %s", request.getRequestURI() ) );
			}

			final ApiResponse apiResponse = RequestHandler.handleRequest( WebCommon.getCurrentUserId( request ), parsedRequest );
			if( apiResponse == null )
			{
				throw new InternalServerErrorException( "API response is null!" );
			}
			else if( apiResponse instanceof JsonObjectResponse )
			{
				sendResponseJson( response, ( (JsonObjectResponse)apiResponse ).getContent().toString() );
			}
			else if( apiResponse instanceof JsonArrayResponse )
			{
				sendResponseJson( response, ( (JsonArrayResponse)apiResponse ).getContent().toString() );
			}
			else if( apiResponse instanceof RedirectResponse )
			{
				forwardToInternalFile( request, response, ( (RedirectResponse)apiResponse ).getRedirectUri() );
			}
			else if( apiResponse instanceof JsonContentFileDownloadResponse )
			{
				final JsonContentFileDownloadResponse fileDownloadResponse = (JsonContentFileDownloadResponse)apiResponse;
				sendFileDownload( response, fileDownloadResponse.getFilename(), MIME_TYPE_JSON, fileDownloadResponse.getContent() );
			}
			else
			{
				LOGGER.error( String.format( "Unknown api response type: %s", apiResponse.toString() ) );
			}
		}
		catch( final ApiException e )
		{
			LOGGER.warn( "Bad Request!" );
			LOGGER.warn( "Request: " + request.getQueryString() );
			LOGGER.warn( ImaginationSupportUtil.formatStackTrace( e ) );

			sendErrorResponse( response, e.getCode() );
		}
		catch( final ServletException | IOException e )
		{
			LOGGER.warn( "GeneralScenarioExplorerException exception!" );
			LOGGER.warn( ImaginationSupportUtil.formatStackTrace( e ) );
			LOGGER.warn( "Request: " + request.getQueryString() );

			sendErrorResponse( response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
		}

		return;
	}

}
