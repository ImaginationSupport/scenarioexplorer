package com.imaginationsupport.web.taglib;

import java.util.HashMap;
import java.util.Map;

public class HelpSideBarEntry extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * Holds the entry title (set by the JSP attribute)
	 */
	private String mTitle = null;

	private static final String TEMPLATE_FILENAME = "help-sidebar-entry.html";
	private static final String TEMPLATE_PLACEHOLDER_TITLE = "title";
	private static final String TEMPLATE_PLACEHOLDER_DESCRIPTION = "description";

	/**
	 * Sets the DOM id of the collapsible body element (called by the JSP)
	 *
	 * @param title - the entry title
	 */
	@SuppressWarnings( "unused" )
	public void setTitle( final String title )
	{
		mTitle = title == null || title.trim().isEmpty()
			? null
			: title.trim();

		return;
	}

	@Override
	public int doStartTag()
	{
		final Map< String, String > placeHolders = new HashMap<>();
		placeHolders.put( TEMPLATE_PLACEHOLDER_TITLE, mTitle == null ? "" : mTitle );

		showTemplate( true, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_DESCRIPTION, placeHolders, null );

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag()
	{
		showTemplate( false, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_DESCRIPTION, null, null );

		return EVAL_PAGE;
	}
}
