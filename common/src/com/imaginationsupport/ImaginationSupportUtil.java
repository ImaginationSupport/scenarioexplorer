package com.imaginationsupport;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Random;

@SuppressWarnings( "WeakerAccess" )
public abstract class ImaginationSupportUtil
{
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	private static final String DATE_ONLY_FORMAT = "yyyy-MM-dd";

//	private static final SimpleDateFormat SIMPLE_DATE_FORMATTER = new SimpleDateFormat( DATE_FORMAT );

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern( DATE_TIME_FORMAT );

	private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern( DATE_ONLY_FORMAT );

	public static final int DEFAULT_PASSWORD_LENGTH = 8;

	public static final String PASSWORD_CHARSET = "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

	public static Logger getWebLogger()
	{
		return LogManager.getLogger( "web.logger" );
	}

	public static Logger getBackendLogger()
	{
		return LogManager.getLogger( "backend.logger" );
	}

	public static Logger getUnitTestsLogger()
	{
		return LogManager.getLogger( "unit.test.logger" );
	}

	public static Logger getMailLogger()
	{
		return LogManager.getLogger( "mail.logger" );
	}

//	public static Calendar parseDateTime( final String source ) throws GeneralScenarioExplorerException
//	{
//		final Calendar cal = new GregorianCalendar();
//
//		try
//		{
//			cal.setTime( DATE_FORMATTER.parse( source ) );
//		}
//		catch( ParseException e )
//		{
//			throw new GeneralScenarioExplorerException( String.format( "Error parsing datetime: [%s]", source ), e );
//		}
//
//		return cal;
//	}

//	public static Calendar parseDateTime( final long fromTimeMilliseconds )
//	{
//		Calendar cal = new GregorianCalendar();
//
//		cal.setTimeInMillis( fromTimeMilliseconds );
//
//		return cal;
//	}

	/**
	 * Parses the given string into a datetime
	 *
	 * @param source the source string
	 * @return the parsed datetime
	 */
	public static LocalDateTime parseDateTime( final String source ) throws InvalidDataException
	{
		try
		{
			return LocalDateTime.parse( source, DATE_TIME_FORMATTER );
		}
		catch( final DateTimeParseException e )
		{
			throw new InvalidDataException( String.format( "Error parsing datetime: %s", source ), e );
		}
	}

	/**
	 * Parses the given string into a datetime expecting only the date
	 *
	 * @param source the source string
	 * @return the parsed datetime
	 */
	public static LocalDateTime parseDateOnly( final String source )
	{
		return LocalDateTime.of( LocalDate.parse( source, DATE_ONLY_FORMATTER ), LocalTime.MIDNIGHT );
	}

	/**
	 * Formats the given datetime
	 *
	 * @param source the datetime to format
	 * @return the formatted string
	 */
	@SuppressWarnings( "unused" )
	public static String formatDateTime( final LocalDateTime source ) throws GeneralScenarioExplorerException
	{
		if( source == null )
		{
			throw new GeneralScenarioExplorerException( "Source cannot be null!" );
		}

		return source.format( DATE_TIME_FORMATTER );
	}

	/**
	 * Formats the given date
	 *
	 * @param source the datetime to format
	 * @return the formatted string
	 */
	@SuppressWarnings( "unused" )
	public static String formatDateOnly( final LocalDate source ) throws GeneralScenarioExplorerException
	{
		if( source == null )
		{
			throw new GeneralScenarioExplorerException( "Source cannot be null!" );
		}

		return source.format( DATE_ONLY_FORMATTER );
	}

	/**
	 * Formats the given date
	 *
	 * @param source the datetime to format
	 * @return the formatted string
	 */
	@SuppressWarnings( "unused" )
	public static String formatDateOnly( final LocalDateTime source ) throws GeneralScenarioExplorerException
	{
		if( source == null )
		{
			throw new GeneralScenarioExplorerException( "Source cannot be null!" );
		}

		return source.format( DATE_ONLY_FORMATTER );
	}

//	/**
//	 * Generates a new name to be used when copying an object and you want to assign a new name based on the given original name.  For instance if the original name is "test" then
//	 * the new name will be "test 2" and then if you copy "test 2" it will be "test 3" etc.
//	 *
//	 * @param original the original name
//	 *
//	 * @return the new name
//	 */
//	public static String generateCopyName( final String original )
//	{
//		if( original == null )
//		{
//			return null;
//		}
//		else if( original.isEmpty() )
//		{
//			return "copy";
//		}
//		else if( original.endsWith( " copy" ) )
//		{
//			return original + " 2";
//		}
//
//		final Matcher matcher = Pattern.compile( "^(.* copy )(\\d+)$" ).matcher( original );
//		if( matcher.find() )
//		{
//			return String.format(
//				"%s%d",
//				matcher.group( 1 ),
//				Integer.parseInt( matcher.group( 2 ) ) + 1
//			);
//		}
//
//		return original + " copy";
//	}

//	public static String formatDateTime( final Date source ) throws GeneralScenarioExplorerException
//	{
//		if( source == null )
//		{
//			throw new GeneralScenarioExplorerException( "Source cannot be null!" );
//		}
//
//		return SIMPLE_DATE_FORMATTER.format( source );
//	}

//	public static String formatDateTime( final long fromTimeMilliseconds ) throws ArgumentException
//	{
//		final Calendar source = Calendar.getInstance();
//
//		source.setTimeInMillis( fromTimeMilliseconds );
//
//		return DATE_FORMATTER.format( source.getTime() );
//	}

	/**
	 * Formats the given exception stack trace using the default number of previous lines to display
	 *
	 * @param error the exception to format
	 * @return the formatted stack trace
	 */
	public static String formatStackTrace( final Throwable error )
	{
		int DEFAULT_NUM_PREVIOUS_STACK_CALLS_TO_DISPLAY = 4;
		return formatStackTrace( error, DEFAULT_NUM_PREVIOUS_STACK_CALLS_TO_DISPLAY );
	}

	/**
	 * Formats the given exception stack trace using the given number of previous lines to display
	 *
	 * @param error                 the exception to format
	 * @param numPreviousStackCalls the previous number of lines to display
	 * @return the formatted stack trace to use
	 */
	public static String formatStackTrace( final Throwable error, final int numPreviousStackCalls )
	{
		if( error == null )
		{
			return "Exception is null?!";
		}

		try( final StringWriter sw = new StringWriter() )
		{
			try( final PrintWriter pw = new PrintWriter( sw ) )
			{
				error.printStackTrace( pw );

//				return sw.toString();
				final String[] lines = sw.toString().split( "\n" );

//				for( int i = 0; i < lines.length; ++i )
//				{
//					System.out.println( String.format( "%2d | %s", i, lines[ i ] ) );
//				}
//				System.out.println();

				int numLinesToReturn;
				for( numLinesToReturn = lines.length - 1; numLinesToReturn >= 0; --numLinesToReturn )
				{
					if( lines[ numLinesToReturn ].contains( "com.imaginationsupport" ) )
					{
//						System.out.println( String.format( "Found at: %d", numLinesToReturn ) );

						numLinesToReturn = Math.min( lines.length, numLinesToReturn + numPreviousStackCalls + 1 );
						break;
					}
				}

//				System.out.println( String.format( "Previous calls to include: %d", numPreviousStackCalls ) );
//				System.out.println( String.format( "Returning %d lines...", numLinesToReturn ) );

				final StringBuilder out = new StringBuilder();
				for( int i = 0; i < numLinesToReturn; ++i )
				{
					if( i > 0 )
					{
						out.append( "\n" );
					}
					out.append( lines[ i ] );
				}
				if( numLinesToReturn < lines.length )
				{
					out.append( String.format( "\n\t... %d more", lines.length - numLinesToReturn ) );
				}

				return out.toString();
			}
		}
		catch( final IOException e2 )
		{
			return String.format( "Unable to format exception %s due to internal exception: %s", error.getMessage(), e2.getMessage() );
		}
	}

	public static String generateRandomPassword() throws GeneralScenarioExplorerException
	{
		return generateRandomPassword( DEFAULT_PASSWORD_LENGTH );
	}

	public static String generateRandomPassword( final int length ) throws GeneralScenarioExplorerException
	{
		if( length < 1 )
		{
			throw new GeneralScenarioExplorerException( "Invalid password length!" );
		}

		final StringBuilder password = new StringBuilder();

		final Random rng = new Random();
		for( int i = 0; i < length; ++i )
		{
			password.append( PASSWORD_CHARSET.charAt( rng.nextInt( PASSWORD_CHARSET.length() ) ) );
		}

		return password.toString();
	}

	public static String joinMap( final Map< ?, ? > map, final String betweenEntries, final String betweenKeyValue )
	{
		if( map == null )
		{
			return null;
		}

		final StringBuilder joined = new StringBuilder();

		for( final Object key : map.keySet() )
		{
			if( joined.length() > 0 && betweenEntries != null )
			{
				joined.append( betweenEntries );
			}

			joined.append( key );
			if( betweenKeyValue != null )
			{
				joined.append( betweenKeyValue );
			}
			joined.append( map.get( key ) );
		}

		return joined.toString();
	}
}
