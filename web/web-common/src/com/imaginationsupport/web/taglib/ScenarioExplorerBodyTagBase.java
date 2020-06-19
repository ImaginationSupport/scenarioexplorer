package com.imaginationsupport.web.taglib;

import com.imaginationsupport.ImaginationSupportUtil;
import org.apache.logging.log4j.Logger;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.Map;

@SuppressWarnings( "SameParameterValue" )
abstract class ScenarioExplorerBodyTagBase extends TagSupport
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * Holds the log4j2 logger
	 */
	protected static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	/**
	 * Shows the start or end of the given template
	 *
	 * @param isStart              true to show before the template break, false to show after the template break
	 * @param templateFilename     the template filename
	 * @param breakPlaceholderName the placeholder name of the break
	 */
	void showTemplate( final boolean isStart, final String templateFilename, final String breakPlaceholderName )
	{
		showTemplate( isStart, templateFilename, breakPlaceholderName, null, null );

		return;
	}

	/**
	 * Shows the start or end of the given template with the placeholders replaced and sections toggled
	 *
	 * @param isStart              true to show before the template break, false to show after the template break
	 * @param templateFilename     the template filename
	 * @param breakPlaceholderName the placeholder name of the break
	 * @param placeHolders         the placeholders to replace
	 * @param toggleableSections   the sections to toggle
	 */
	void showTemplate(
		final boolean isStart,
		final String templateFilename,
		final String breakPlaceholderName,
		final Map< String, String > placeHolders,
		final Map< String, Boolean > toggleableSections )
	{
		final JspWriter out = pageContext.getOut();

		try
		{
			final String template = isStart
				? TagLibHelpers.loadTemplateStart( pageContext.getServletContext(), templateFilename, breakPlaceholderName )
				: TagLibHelpers.loadTemplateEnd( pageContext.getServletContext(), templateFilename, breakPlaceholderName );

			out.write( TagLibHelpers.toggleSections( TagLibHelpers.replaceTemplatePlaceHolders( template, placeHolders ), toggleableSections ) );
		}
		catch( final Exception e )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( e ) );

			try
			{
				out.write( String.format( "<!-- Exception: %s -->\n", e.getMessage() ) );
			}
			catch( final IOException e2 )
			{
				e2.printStackTrace();
			}
		}

		return;
	}
}
