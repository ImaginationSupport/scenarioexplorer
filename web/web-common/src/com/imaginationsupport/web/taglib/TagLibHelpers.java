package com.imaginationsupport.web.taglib;

import com.imaginationsupport.web.WebCommon;
import com.imaginationsupport.API;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.data.User;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

abstract class TagLibHelpers
{
	@SuppressWarnings( { "unused", "WeakerAccess" } )
	public abstract static class FontAwesomeCssClasses
	{
		public static String Base = "fas";

		public static String Project = "fa-project-diagram";
		public static String View = "fa-book-open";

		public static String Create = "fa-plus";
		public static String Update = "fa-edit";
		public static String Delete = "fa-trash";

		public static String Export = "fa-file-export";
		public static String DownloadFile = "fa-download";
		public static String Clone = "fa-clone";
		public static String CreateTemplate = "fa-cubes";
		public static String Import = "fa-file-import";

		public static String Save = "fa-check"; // "fa-check-circle"
		public static String Cancel = "fa-times"; // "fa-times-circle"

		public static String Owner = "fa-user-circle";

		public static String Assigned = "fa-paperclip";

		public static String ConditioningEvent = "fa-code-merge fa-rotate-270";
		public static String ConditioningEventPrecondition = "fa-clipboard-check";
		public static String ConditioningEventOutcome = "fa-flag-checkered";
		public static String ConditioningEventOutcomeEffect = "fa-star-exclamation";
	}

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	static User getLoggedInUser( final HttpServletRequest request )
	{
		final String userName = WebCommon.getCurrentUserId( request );

		if( userName == null )
		{
			return null;
		}
		else
		{
			try( final API api = new API() )
			{
				return api.findOrCreateBasicUser( userName );
			}
			catch( final Exception e )
			{
				LOGGER.fatal( "Error getting logged in user: " + e.getMessage() );
				LOGGER.fatal( ImaginationSupportUtil.formatStackTrace( e ) );

				return null;
			}
		}
	}

	/**
	 * Loads the template with the given filename
	 *
	 * @param context  the Servlet context
	 * @param fileName the template filename
	 *
	 * @return the file contents
	 */
	static String loadTemplate( final ServletContext context, final String fileName ) throws IOException
	{
		final StringBuilder templateText = new StringBuilder();
		final String templatePath = "/WEB-INF/classes/templates/" + fileName;

		try( final InputStream inputStream = context.getResourceAsStream( templatePath ) )
		{
			if( inputStream == null )
			{
				return "<!-- [Template not found: " + templatePath + " ] -->";
			}

			try( final InputStreamReader streamReader = new InputStreamReader( inputStream, StandardCharsets.UTF_8 ) )
			{
				try( final BufferedReader bufferedReader = new BufferedReader( streamReader ) )
				{
					String line;
					while( ( line = bufferedReader.readLine() ) != null )
					{
						templateText.append( line.replaceAll( "([\\n\\r])", "" ) ).append( "\n" );
					}
				}
			}
		}

		return templateText.toString();
	}

	/**
	 * Loads the template with the given filename, returning all text before the given placeholder key
	 *
	 * @param context         the Servlet context
	 * @param fileName        the template filename
	 * @param placeholderName the template placeholder to use for the break
	 *
	 * @return the template text before the key
	 */
	static String loadTemplateStart( final ServletContext context, final String fileName, final String placeholderName ) throws IOException
	{
		final String template = loadTemplate( context, fileName );

		final int breakLocation = template.indexOf( "{{" + placeholderName + "}}" );
		return breakLocation == -1
			? template
			: template.substring( 0, breakLocation );
	}

	/**
	 * Loads the template with the given filename, returning all text after the given placeholder key
	 *
	 * @param context         the Servlet context
	 * @param fileName        the template filename
	 * @param placeholderName the template placeholder to use for the break
	 *
	 * @return the template text before the key
	 */
	static String loadTemplateEnd( final ServletContext context, final String fileName, final String placeholderName ) throws IOException
	{
		final String template = loadTemplate( context, fileName );

		final String breakString = "{{" + placeholderName + "}}";
		final int breakLocation = template.indexOf( breakString );
		return breakLocation == -1
			? template
			: template.substring( breakLocation + breakString.length() );
	}

	/**
	 * Replaces the template placeholders between {{ and }} in the given template
	 *
	 * @param template     the text to search through and replace placeholders
	 * @param placeHolders the map of placeholder to replacement text
	 *
	 * @return the updated template text
	 */
	static String replaceTemplatePlaceHolders( final String template, final Map< String, String > placeHolders )
	{
		if( placeHolders == null || placeHolders.size() == 0 )
		{
			return template;
		}

		String working = template;

		for( final String key : placeHolders.keySet() )
		{
			final String replaceThis = "{{" + key + "}}";
			int location = working.indexOf( replaceThis );
			while( location > -1 )
			{
				working = working.substring( 0, location )
					+ placeHolders.get( key )
					+ working.substring( location + replaceThis.length() );

				location = working.indexOf( replaceThis );
			}

//			working = working.replaceAll( "\\{\\{" + key + "}}", placeHolders.get( key ) );
		}

		return working;
	}

	/**
	 * Replaces the toggleable sections in the template appropriately
	 *
	 * @param template the text to search through and replace the toggleable sections
	 * @param toggleableSections the map of placeholder to if it should should be shown
	 *
	 * @return the updated template text
	 */
	static String toggleSections( final String template, final Map< String, Boolean > toggleableSections )
	{
		if( toggleableSections == null || toggleableSections.size() == 0 )
		{
			return template;
		}

		StringBuilder working = new StringBuilder( template );

		for( final String key : toggleableSections.keySet() )
		{
			final String startMarker = String.format( "{{%s-start}}", key );
			final String endMarker = String.format( "{{%s-end}}", key );
			final int startLocation = working.indexOf( startMarker );
			final int endLocation = working.indexOf( endMarker );

			if( startLocation > -1 && endLocation > -1 && endLocation > startLocation )
			{
				if( toggleableSections.get( key ) )
				{
					// show the section, so just remove the markers (do the end FIRST so that the indexes aren't changed when the start marker is removed)
					working.delete( endLocation, endLocation + endMarker.length() );
					working.delete( startLocation, startLocation + startMarker.length() );
				}
				else
				{
					// hide the section
					working.delete( startLocation, endLocation + endMarker.length() );
				}
			}
		}

		return working.toString();
	}
}
