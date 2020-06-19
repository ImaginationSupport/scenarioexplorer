package com.imaginationsupport.web.taglib;

import java.util.HashMap;
import java.util.Map;

public class SideBar extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	private static final String TEMPLATE_FILENAME = "sidebar.html";
	private static final String TEMPLATE_PLACEHOLDER_BODY = "body";

	/**
	 * Holds if we should show the notifications section (set by the JSP attribute)
	 */
	private boolean mShowNotifications = false;

	/**
	 * Sets if the notifications section should be shown (called by the JSP)
	 *
	 * @param newValue - true if the notifications section should be shown
	 */
	@SuppressWarnings( "unused" )
	public void setNotifications( final String newValue )
	{
		mShowNotifications = newValue != null && Boolean.parseBoolean( newValue );
		return;
	}

	@Override
	public int doStartTag()
	{
		final Map< String, Boolean > toggleableSections = new HashMap<>();
		toggleableSections.put( "notifications", mShowNotifications );

		showTemplate( true, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY, null, toggleableSections );

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag()
	{
		showTemplate( false, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY );

		return EVAL_PAGE;
	}
}
