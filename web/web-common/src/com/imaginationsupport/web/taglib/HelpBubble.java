package com.imaginationsupport.web.taglib;

import com.imaginationsupport.ImaginationSupportUtil;
import org.apache.logging.log4j.Logger;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.HashMap;
import java.util.Map;

public class HelpBubble extends SimpleTagSupport
{
	/**
	 * Holds the hover popup text (set by the JSP attribute)
	 */
	private String mHoverPopupText = null;

//	/**
//	 * Holds the link URI (set by the JSP attribute)
//	 */
//	private String mHref = null;

	private static final String TEMPLATE_FILENAME = "help-bubble.html";

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	@SuppressWarnings( "unused" )
	public void setHoverPopup( final String newValue )
	{
		mHoverPopupText = newValue == null || newValue.trim().isEmpty()
			? null
			: newValue.trim();
		return;
	}

//	@SuppressWarnings( "unused" )
//	public void setHref( final String newValue )
//	{
//		mHref = newValue == null || newValue.trim().isEmpty()
//			? null
//			: newValue.trim();
//		return;
//	}

	@Override
	public void doTag()
	{
		try
		{
			final PageContext pageContext = (PageContext)getJspContext();

			final Map< String, String > placeHolders = new HashMap<>();
			placeHolders.put( "hover-popup-text", mHoverPopupText == null ? "" : mHoverPopupText );
//			placeHolders.put( "href", mHref == null ? "" : mHref );

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
