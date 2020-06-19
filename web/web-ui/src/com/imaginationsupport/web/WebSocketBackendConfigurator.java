package com.imaginationsupport.web;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class WebSocketBackendConfigurator extends ServerEndpointConfig.Configurator
{
	@Override
	public void modifyHandshake( final ServerEndpointConfig config, final HandshakeRequest request, final HandshakeResponse response )
	{
		config.getUserProperties().put( "http.session", request.getHttpSession() );

		return;
	}
}
