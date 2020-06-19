package com.imaginationsupport.web.exceptions;

/**
 * Implements an HTTP 404 Not Found exception
 */
public class NotFoundException extends ApiException
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
	public NotFoundException( final String message )
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
	public NotFoundException( final String message, final Exception e )
	{
		super( message, e );

		return;
	}

	@Override
	public int getCode()
	{
		return 404;
	}
}
