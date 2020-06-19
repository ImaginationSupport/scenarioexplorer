package com.imaginationsupport.web.taglib;

import com.imaginationsupport.ImaginationSupportConfig;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.data.User;
import com.imaginationsupport.web.WebCommon;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.HashMap;
import java.util.Map;

public class PageHeader extends SimpleTagSupport
{
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

			final User loggedInUser = TagLibHelpers.getLoggedInUser( request );

			// TODO links should retain appropriate parameters, like debugging, web sockets, etc

			final Map< String, String > placeHolders = new HashMap<>();
			placeHolders.put( "web-ui.deploy.path", ImaginationSupportConfig.getWebUiUrl() );
			placeHolders.put( "web-user-support.deploy.path", ImaginationSupportConfig.getWebUserSupportUrl() );

			if( WebCommon.USING_AUTHENTICATION )
			{
				if( loggedInUser == null )
				{
					// not logged in

					final String template = TagLibHelpers.loadTemplate( request.getServletContext(), "pageheader-loggedout.html" );
					out.write( TagLibHelpers.replaceTemplatePlaceHolders( template, placeHolders ) );
				}
				else
				{
					// logged in

					placeHolders.put( "userName", loggedInUser.getUserName() );
					placeHolders.put( "realName", loggedInUser.getFullName() );

					final String template = TagLibHelpers.loadTemplate( request.getServletContext(), "pageheader-loggedin.html" );
					out.write( TagLibHelpers.replaceTemplatePlaceHolders( template, placeHolders ) );
				}
			}
			else
			{
				// standalone

				final String template = TagLibHelpers.loadTemplate( request.getServletContext(), "pageheader-standalone.html" );
				out.write( TagLibHelpers.replaceTemplatePlaceHolders( template, placeHolders ) );
			}
		}
		catch( final Throwable error )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( error ) );
		}

		return;
	}
}