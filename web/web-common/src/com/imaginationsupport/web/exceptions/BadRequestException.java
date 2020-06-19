package com.imaginationsupport.web.exceptions;

/**
 * Implements an HTTP 400 Bad Request exception
 */
public class BadRequestException extends ApiException
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
	public BadRequestException( final String message )
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
	public BadRequestException( final String message, final Exception e )
	{
		super( message, e );

		return;
	}

	@Override
	public int getCode()
	{
		return 400;
	}
}
