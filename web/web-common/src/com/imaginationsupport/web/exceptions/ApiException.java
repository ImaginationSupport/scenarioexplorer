package com.imaginationsupport.web.exceptions;

public abstract class ApiException extends Exception
{
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 *
	 * @param message the error message
	 */
	protected ApiException( final String message )
	{
		super( message );

		return;
	}

	/**
	 * Constructor from an another exception
	 *
	 * @param message the error message
	 * @param e       the root cause
	 */
	protected ApiException( final String message, final Exception e )
	{
		super( message, e );

		return;
	}

	/**
	 * Gets the HTTP response status code;
	 *
	 * @return the HTTP response status code
	 */
	public abstract int getCode();
}
