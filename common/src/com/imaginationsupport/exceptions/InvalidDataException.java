package com.imaginationsupport.exceptions;

public final class InvalidDataException extends Exception
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
	public InvalidDataException( final String message )
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
	public InvalidDataException( final String message, final Exception e )
	{
		super( message, e );

		return;
	}
}
