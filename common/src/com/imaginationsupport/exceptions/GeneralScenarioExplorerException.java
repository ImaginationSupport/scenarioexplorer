package com.imaginationsupport.exceptions;

/**
 * A general imagination support application exception
 */
public class GeneralScenarioExplorerException extends Exception
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
	public GeneralScenarioExplorerException( final String message )
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
	public GeneralScenarioExplorerException( final String message, final Exception e )
	{
		super( message, e );

		return;
	}
}
