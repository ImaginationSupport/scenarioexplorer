package com.imaginationsupport.exceptions;

/**
 * An exception when the user is not authorized to perform the given operation
 */
public final class NotAuthorizedException extends Exception
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
	public NotAuthorizedException( final String message )
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
	public NotAuthorizedException( final String message, final Exception e )
	{
		super( message, e );

		return;
	}
}
