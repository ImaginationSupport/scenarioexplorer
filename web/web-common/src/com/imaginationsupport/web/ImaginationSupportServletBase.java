package com.imaginationsupport.web;

import com.imaginationsupport.API;
import com.imaginationsupport.Database;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.web.exceptions.InternalServerErrorException;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet
public abstract class ImaginationSupportServletBase extends HttpServlet
{
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Holds the MIME type string fro JSON
	 */
	public static final String MIME_TYPE_JSON = "application/json";

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	/**
	 * Initializes the database connection for servlet operation
	 */
	void initDatabaseConnection() throws GeneralScenarioExplorerException
	{
		LOGGER.info( String.format( "Using authentication: %s", WebCommon.USING_AUTHENTICATION ) );

		try
		{
			// initialize the database instance
			Database.getInstance();

			if( !WebCommon.USING_AUTHENTICATION )
			{
				try
				{
					final API api = new API();
					final User debuggingUser = api.findOrCreateBasicUser( WebCommon.DEBUGGING_USER_NAME );

					LOGGER.debug( debuggingUser.toJSON() );
				}
				catch( final InvalidDataException | GeneralScenarioExplorerException e )
				{
					LOGGER.error( "Error creating default user!", e );
				}
			}
		}
		catch( final Exception e )
		{
			throw new GeneralScenarioExplorerException( "Error initializing database connection", e );
		}

		return;
	}

	/**
	 * Sends a successful javascript source response
	 *
	 * @param response the response to use
	 * @param jsonText the JSON text to send
	 */
	void sendResponseJson( final HttpServletResponse response, final String jsonText )
	{
		sendResponseRaw( response, MIME_TYPE_JSON, jsonText );

		return;
	}

	/**
	 * Sends a successful javascript source response
	 *
	 * @param response the response to use
	 * @param json     the JSON to send
	 */
	void sendResponseJson( final HttpServletResponse response, final JSONObject json )
	{
		sendResponseJson( response, json.toString() );

		return;
	}

	/**
	 * Sends a successful javascript source response
	 *
	 * @param response the response to use
	 * @param html     the javascript source to send
	 */
	void sendResponseHtml( final HttpServletResponse response, final String html )
	{
		sendResponseRaw( response, "text/html", html );

		return;
	}

	/**
	 * Sends a successful javascript source response
	 *
	 * @param response         the response object to use
	 * @param javascriptSource the javascript source to send
	 */
	void sendResponseJavascriptSource( final HttpServletResponse response, final String javascriptSource )
	{
		sendResponseRaw( response, "application/javascript", javascriptSource );

		return;
	}

	/**
	 * Forwards the request to the given internal URI
	 *
	 * @param request     the incoming request
	 * @param response    the outgoing response
	 * @param redirectUri the internal URI to forward to
	 */
	void forwardToInternalFile( final HttpServletRequest request, final HttpServletResponse response, final String redirectUri ) throws ServletException, IOException
	{
		getServletContext().getRequestDispatcher( redirectUri ).forward( request, response );

		return;
	}

	/**
	 * Sends the given binary data as a file download as the response
	 *
	 * @param response the response to use
	 * @param filename the filename to default to the user
	 * @param mimeType the MIME type to send
	 * @param data     the binary data to send
	 */
	void sendFileDownload( final HttpServletResponse response, final String filename, final String mimeType, final byte[] data ) throws InternalServerErrorException
	{
		response.addHeader( "content-disposition", String.format( "attachment; filename=\"%s\"", filename ) );

		sendBinaryData( response, mimeType, data );

		return;
	}

	/**
	 * Sends the given binary data as the response
	 *
	 * @param response the response to use
	 * @param mimeType the MIME type to send
	 * @param data     the binary data to send
	 */
	void sendBinaryData( final HttpServletResponse response, final String mimeType, final byte[] data ) throws InternalServerErrorException
	{
		response.setContentType( mimeType );

		try
		{
			response.getOutputStream().write( data );
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
		catch( final IOException e )
		{
			throw new InternalServerErrorException( "Error sending response", e );
		}

		return;
	}

	/**
	 * Send the given HTTP error response status code
	 *
	 * @param response the response object to use
	 */
	void sendErrorResponse( final HttpServletResponse response, final int statusCode )
	{
		try
		{
			response.sendError( statusCode );
		}
		catch( final IOException e )
		{
			LOGGER.error( "Error sending not authorized!", e );
		}

		return;
	}

	/**
	 * Sends the given MIME type and response text
	 *
	 * @param response     the response to use
	 * @param mimeType     the MIME type
	 * @param responseText the response text
	 */
	private void sendResponseRaw( final HttpServletResponse response, final String mimeType, final String responseText )
	{
		try( final PrintWriter out = response.getWriter() )
		{
			response.setContentType( mimeType );

			out.write( responseText );
		}
		catch( final IOException e )
		{
			LOGGER.error( "Error sending response!", e );

			try
			{
				response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
			}
			catch( final IOException e2 )
			{
				LOGGER.error( "Error sending SERVER ERROR!", e2 );
			}
		}

		return;
	}
}
