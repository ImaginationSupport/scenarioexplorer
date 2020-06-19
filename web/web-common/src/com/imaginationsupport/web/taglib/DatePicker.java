package com.imaginationsupport.web.taglib;

import com.imaginationsupport.ImaginationSupportUtil;
import org.apache.logging.log4j.Logger;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.HashMap;
import java.util.Map;

public class DatePicker extends SimpleTagSupport
{
	/**
	 * Holds the DOM id (set by the JSP attribute)
	 */
	private String mId = null;

	private static final String TEMPLATE_FILENAME = "datepicker.html";

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	@SuppressWarnings( "unused" )
	public void setId( final String newValue )
	{
		mId = newValue == null || newValue.trim().isEmpty()
			? null
			: newValue.trim();
		return;
	}

	@Override
	public void doTag()
	{
		try
		{
			final PageContext pageContext = (PageContext)getJspContext();

			final Map< String, String > placeHolders = new HashMap<>();
			placeHolders.put( "id", mId == null ? "" : String.format( " id=\"%s\"", mId ) );
			placeHolders.put( "icon id", mId == null ? "" : String.format( " id=\"%s-icon\"", mId ) );

			final String template = TagLibHelpers.replaceTemplatePlaceHolders(
				TagLibHelpers.loadTemplate( pageContext.getServletContext(), TEMPLATE_FILENAME ),
				placeHolders
			);

			getJspContext()
				.getOut()
				.print( template );
		}
		catch( final Throwable error )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( error ) );
		}

		return;
	}
}
