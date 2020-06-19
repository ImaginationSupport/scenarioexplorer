package com.imaginationsupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ConsoleUtil
{
	static
	{
		initConsoleTools();
	}

	/**
	 * Gets the log4j2 logger for unit test
	 *
	 * @return the log4j2 logger
	 */
	public static Logger getUnitTestsLogger()
	{
		return LogManager.getLogger( "unit.test.logger" );
	}

	/**
	 * Gets the log4j2 logger for console apps
	 *
	 * @return the log4j2 logger
	 */
	public static Logger getConsoleLogger()
	{
		return LogManager.getLogger( "console.logger" );
	}

	private static void initConsoleTools()
	{
//		System.err.println( "Working Directory = " + System.getProperty( "user.dir" ) );

		// first set the filename of the log4j2 config file that should be loaded
		System.getProperties().setProperty( "log4j.configurationFile", "log4j2.xml" );

//		final Logger logger = getConsoleLogger();
//		try
//		{
//			logger.trace( String.format( "Using %s / port %d / database %s",
//				ImaginationSupportConfig.getMongoDbHostname(),
//				ImaginationSupportConfig.getMongoDbPort(),
//				ImaginationSupportConfig.getMongoDbDatabase() ) );
//		}
//		catch( final GeneralScenarioExplorerException e )
//		{
//			logger.error( "Error loading properties from the config file!", e );
//		}

		// set the Mongo logging level
		java.util.logging.Logger.getLogger( "org.mongodb.driver" ).setLevel( java.util.logging.Level.WARNING );

		return;
	}
}
