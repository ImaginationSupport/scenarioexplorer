package com.imaginationsupport.web;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.web.api.RequestHandler;
import com.imaginationsupport.web.api.request.ApiRequest;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint( value = "/ws", configurator = WebSocketBackendConfigurator.class )
public class WebSocketBackend
{
	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	//	private EndpointConfig m_endpointConfig = null;
	private Session m_session;

	public WebSocketBackend()
	{
		LOGGER.debug( "WebSocket Backend initialized" );

		return;
	}

	@OnOpen
	public void onOpen( final Session session, final EndpointConfig endpointConfig )
	{
//		m_endpointConfig = endpointConfig;
		m_session = session;

		final HttpSession httpSession = (HttpSession)endpointConfig.getUserProperties().get( "http.session" );
		final ServletContext servletContext = httpSession.getServletContext();

		LOGGER.info( String.format( "User connected: %s - %s", session.getId(), "..." ) ); // TODO need to figure out how to get the user IP address for proper logging!

		return;
	}

	@OnClose
	public void onClose( final Session session )
	{
		LOGGER.info( String.format( "User disconnected: %s - %s", session.getId(), "..." ) ); // TODO need to figure out how to get the user IP address for proper logging!

		return;
	}

	@OnMessage
	public void onMessage( final Session session, final String message )
	{
		LOGGER.debug( String.format( "Message: %s - %s", session.getId(), message ) );

		final String userName = "defaultuser"; // TODO fill in!

		JSONObject response;
		try
		{
//			response = RequestHandler.handleJsonRequest( JsonHelper.parseObject( message ), userName );
			response = new JSONObject();
			response.put( "response", RequestHandler.handleRequest( userName, new ApiRequest( JsonHelper.parseObject( message ) ) ) );

			response.put( ApiStrings.JsonKeys.Success, true );
		}
		catch( final Exception e )
		{
			LOGGER.error( "Unknown exception!", e );

			response = new JSONObject();

			response.put( ApiStrings.JsonKeys.Success, false );
			response.put( ApiStrings.JsonKeys.ErrorMessage, ImaginationSupportUtil.formatStackTrace( e ) );
		}

		try
		{
			session.getBasicRemote().sendText( response.toString() );
		}
		catch( final IOException e )
		{
			LOGGER.error( e );
		}

		return;
	}

	@OnError
	public void onError( final Session session, final Throwable throwable )
	{
		LOGGER.error( "Error in session: " + session.getId() );
		LOGGER.error( throwable );

		return;
	}
}
