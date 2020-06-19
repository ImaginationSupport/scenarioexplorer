package com.imaginationsupport.helpers;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;

public abstract class HttpHelper
{
	public enum Method
	{
		Get,
		Post,
		Put,
		Delete
	}

	/**
	 * Runs an HTTP call
	 *
	 * @param uri     the URI to use
	 * @param method  the HTTP method to use
	 * @param payload the payload
	 * @param logger  the log4j2 logger to use
	 * @return the response bytes
	 */
	public static byte[] runCall(
		final URI uri,
		final Method method,
		final byte[] payload,
		final String username,
		final String password,
		final Logger logger ) throws GeneralScenarioExplorerException
	{
		if( uri == null )
		{
			throw new GeneralScenarioExplorerException( "URI cannot be null!" );
		}
		if( logger == null )
		{
			throw new GeneralScenarioExplorerException( "Logger cannot be null!" );
		}

		if( payload != null )
		{
			logger.info( String.format( "payload:        |%s|", new String( payload ) ) );
		}

		return runHttpCall(
			uri,
			method,
			payload == null ? null : new ByteArrayEntity( payload, ContentType.APPLICATION_JSON ),
			username,
			password,
			logger,
			200
		);
	}

//	public static byte[] runMultipartPost(
//		final URI uri,
//		final byte[] payload,
//		final String filename,
//		final String username,
//		final String password,
//		final Logger logger ) throws GeneralScenarioExplorerException
//	{
//		logger.info( String.format( "payload:        |%s|", new String( payload ) ) );
//
//		final HttpEntity entity = MultipartEntityBuilder
//			.create()
//			.addBinaryBody( "file", payload, ContentType.APPLICATION_JSON, filename ) // TODO expose parameter name
//			.build();
//
//		return runHttpCall( uri, Method.Post, entity, username, password, logger, 201 );
//	}

	/**
	 * Run an HTTP call
	 *
	 * @param uri                  the URL to use
	 * @param method               the HTTP method to use
	 * @param entity               the request details to use
	 * @param username             the username to use
	 * @param password             the password to use
	 * @param logger               the log4j2 logger to use
	 * @param expectedResponseCode the expected HTTP code
	 * @return the response body
	 */
	private static byte[] runHttpCall(
		final URI uri,
		final Method method,
		final HttpEntity entity,
		final String username,
		final String password,
		final Logger logger,
		final int expectedResponseCode ) throws GeneralScenarioExplorerException
	{
		final HttpRequestBase request;
		switch( method )
		{
			case Get:
				request = new HttpGet( uri );
				break;

			case Post:
				request = new HttpPost( uri );
				break;

//			case Patch:
//				request = new HttpPatch( uri );
//				break;

			case Put:
				request = new HttpPut( uri );
				break;

			case Delete:
				request = new HttpDelete( uri );
				break;

			default:
				throw new GeneralScenarioExplorerException( "Unknown method: " + method.toString() );
		}

		if( method == Method.Post /*|| method == Method.Patch*/ || method == Method.Put )
		{
			( (HttpEntityEnclosingRequestBase)request ).setEntity( entity );
		}
		else if( entity != null )
		{
			throw new GeneralScenarioExplorerException( String.format( "Entity not allowed for method: %s", method.toString() ) );
		}

		logger.info( String.format( "uri:            %s", uri.toString() ) );
		logger.info( String.format( "method:         %s", method.toString() ) );

		if( username != null && password != null )
		{
			final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials( new AuthScope( uri.getHost(), uri.getPort() ), new UsernamePasswordCredentials( username, password ) );
			try( final CloseableHttpClient client = HttpClients.custom().setDefaultCredentialsProvider( credentialsProvider ).build() )
			{
				return runHttpCallHelper( client, request, logger, expectedResponseCode );
			}
			catch( final IOException e )
			{
				throw new GeneralScenarioExplorerException( "Error running HTTP call", e );
			}
		}
		else
		{
			try( final CloseableHttpClient client = HttpClients.createDefault() )
			{
				return runHttpCallHelper( client, request, logger, expectedResponseCode );
			}
			catch( final IOException e )
			{
				throw new GeneralScenarioExplorerException( "Error running HTTP call", e );
			}
		}
	}

	private static byte[] runHttpCallHelper(
		final CloseableHttpClient client,
		final HttpRequestBase request,
		final Logger logger,
		final int expectedResponseCode ) throws GeneralScenarioExplorerException
	{
		try(

			final CloseableHttpResponse response = client.execute( request ) )
		{
			final int responseCodeReceived = response.getStatusLine().getStatusCode();
			final byte[] responseBytes = EntityUtils.toByteArray( response.getEntity() );

			logger.debug( String.format( "code:           %d", responseCodeReceived ) );
			logger.debug( String.format( "response size:  %d", responseBytes.length ) );
			logger.debug( String.format( "response:       %s", new String( responseBytes ).substring( 0, Math.min( responseBytes.length, 400 ) ) ) );

			request.releaseConnection();

			if( responseCodeReceived != expectedResponseCode )
			{
				throw new GeneralScenarioExplorerException( String.format( "Expected response code %d but received: %d", expectedResponseCode, responseCodeReceived ) );
			}

			return responseBytes;
		}
		catch( final IOException e )
		{
			throw new GeneralScenarioExplorerException( "Error running HTTP call", e );
		}
	}
}
