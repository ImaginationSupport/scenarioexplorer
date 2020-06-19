package com.imaginationsupport.web;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import org.apache.catalina.realm.SecretKeyCredentialHandler;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

public abstract class WebCommon
{
	/**
	 * Holds the username to use for debugging
	 */
	public static final String DEBUGGING_USER_NAME = "ScenarioExplorerUser";

	/**
	 * Holds if authentication is enabled (updated automatically by the build script)
	 */
	public static final boolean USING_AUTHENTICATION = false;

	public static String getCurrentUserId( final HttpServletRequest request )
	{
		if( USING_AUTHENTICATION )
		{
			if( request == null )
			{
				return null;
			}

			return request.getUserPrincipal() == null
				? null
				: request.getUserPrincipal().getName();
		}
		else
		{
			return DEBUGGING_USER_NAME;
		}
	}

	/**
	 * Gets a string value coming in from the HTTP request, or throws an exception if the parameter could not be found
	 *
	 * @param request       the HttpServletRequest coming in
	 * @param parameterName the parameter name to find
	 * @return the value of the parameter
	 */
	public static String getRequiredParameterString( final HttpServletRequest request, final String parameterName ) throws GeneralScenarioExplorerException
	{
		final String parameterValue = request.getParameter( parameterName );

		if( parameterValue == null )
		{
			throw new GeneralScenarioExplorerException( "Missing required parameter: " + parameterName );
		}

		return parameterValue;
	}

	/**
	 * Gets an optional string value coming in from the HTTP request, or returns the default value if the parameter could not be found
	 *
	 * @param request       the HttpServletRequest coming in
	 * @param parameterName the parameter name to find
	 * @param defaultValue  the default value to use if it was not present
	 * @return the value of the parameter, or the default if it wasn't found
	 */
	public static String getOptionalParameterString( final HttpServletRequest request, final String parameterName, final String defaultValue )
	{
		if( request.getParameterMap().containsKey( parameterName ) )
		{
			return request.getParameter( parameterName );
		}
		else
		{
			return defaultValue;
		}
	}

	/**
	 * Gets a boolean value coming in from the HTTP request, or throws an exception if the parameter could not be found
	 *
	 * @param request       the HttpServletRequest coming in
	 * @param parameterName the parameter name to find
	 * @return the value of the parameter
	 */
	public static boolean getRequiredParameterBoolean( final HttpServletRequest request, final String parameterName ) throws GeneralScenarioExplorerException
	{
		final String parameterValue = request.getParameter( parameterName );

		if( parameterValue == null )
		{
			throw new GeneralScenarioExplorerException( "Missing required parameter: " + parameterName );
		}

		return Boolean.parseBoolean( parameterValue );
	}

	/**
	 * Hashes the given string
	 *
	 * @param clearTextPassword the password to hash
	 * @return the hashed password
	 */
	public static String hashTomcatPassword( final String clearTextPassword ) throws GeneralScenarioExplorerException
	{
//		if( clearTextPassword == null || clearTextPassword.length() == 0 )
//		{
//			throw new GeneralScenarioExplorerException( "Password cannot be null or blank!" );
//		}
//
//		return getTomcatCredentialHandler().mutate( clearTextPassword );
		return clearTextPassword;
	}

	/**
	 * Checks if the given cleartext password matches the given stored hashed password
	 *
	 * @param hashedCredential  the hashed password to check against
	 * @param clearTextPassword the cleartext password to check
	 * @return true if they match, otherwise false
	 */
	public static boolean checkTomcatPassword( final String hashedCredential, final String clearTextPassword ) throws GeneralScenarioExplorerException
	{
		return getTomcatCredentialHandler().matches( clearTextPassword, hashedCredential );
	}

	/**
	 * Gets the tomcat credential handler to use
	 *
	 * @return the tomcat credential handler
	 */
	private static SecretKeyCredentialHandler getTomcatCredentialHandler() throws GeneralScenarioExplorerException
	{
		try
		{
			final SecretKeyCredentialHandler generator = new SecretKeyCredentialHandler();

			generator.setAlgorithm( "PBKDF2WithHmacSHA512" );
			generator.setIterations( 100000 );
			generator.setKeyLength( 256 );
			generator.setSaltLength( 16 );

			return generator;
		}
		catch( final NoSuchAlgorithmException e )
		{
			throw new GeneralScenarioExplorerException( "Error hashing password!", e );
		}
	}
}
