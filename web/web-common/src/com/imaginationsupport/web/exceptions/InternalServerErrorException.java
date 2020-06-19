package com.imaginationsupport.web.exceptions;

/**
 * Implements an HTTP 500 Internal Server Error exception
 */
public class InternalServerErrorException extends ApiException
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
	public InternalServerErrorException( final String message )
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
	public InternalServerErrorException( final String message, final Exception e )
	{
		super( message, e );

		return;
	}

	@Override
	public int getCode()
	{
		return 500;
	}
}
