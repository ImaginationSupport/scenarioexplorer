package com.imaginationsupport.web.taglib;

import com.imaginationsupport.web.NavHelper;
import com.imaginationsupport.ImaginationSupportUtil;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreadCrumbs extends SimpleTagSupport
{
	private static final String TEMPLATE_FILENAME_BAR = "breadcrumbs-bar.html";
	private static final String TEMPLATE_FILENAME_ITEM_ACTIVE = "breadcrumbs-item-active.html";
	private static final String TEMPLATE_FILENAME_ITEM_INACTIVE = "breadcrumbs-item-inactive.html";

	private static final String TEMPLATE_BAR_ITEMS = "items";

	private static final String TEMPLATE_ITEM_URI = "uri";
	private static final String TEMPLATE_ITEM_TEXT = "text";

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	@Override
	public void doTag()
	{
		try
		{
			final JspWriter out = getJspContext().getOut();

			final PageContext pageContext = (PageContext)getJspContext();
			final HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

			final StringBuilder itemsHtml = new StringBuilder();

			final List< NavHelper.BreadCrumb > breadcrumbs = NavHelper.getBreadCrumbs( request );
			for( final NavHelper.BreadCrumb breadcrumb : breadcrumbs )
			{
				final Map< String, String > itemPlaceHolders = new HashMap<>();
				final String itemTemplateFilename;

				itemPlaceHolders.put( TEMPLATE_ITEM_TEXT, breadcrumb.getPrettyName() );

				if( breadcrumb.isActive() )
				{
					itemTemplateFilename = TEMPLATE_FILENAME_ITEM_ACTIVE;
				}
				else
				{
					itemTemplateFilename = TEMPLATE_FILENAME_ITEM_INACTIVE;

					itemPlaceHolders.put( TEMPLATE_ITEM_URI, breadcrumb.getUri() );
				}

				itemsHtml.append(
					TagLibHelpers.replaceTemplatePlaceHolders(
						TagLibHelpers.loadTemplate( pageContext.getServletContext(), itemTemplateFilename ),
						itemPlaceHolders ) );
			}

			final Map< String, String > barPlaceHolders = new HashMap<>();

			barPlaceHolders.put( TEMPLATE_BAR_ITEMS, itemsHtml.toString().replaceAll( "\\s+$", "" ) );

			out.println( TagLibHelpers.replaceTemplatePlaceHolders( TagLibHelpers.loadTemplate( pageContext.getServletContext(), TEMPLATE_FILENAME_BAR ), barPlaceHolders ) );
		}
		catch( final Throwable error )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( error ) );
		}

		return;
	}
}