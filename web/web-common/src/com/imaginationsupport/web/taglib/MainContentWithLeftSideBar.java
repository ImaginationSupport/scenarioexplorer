package com.imaginationsupport.web.taglib;

import java.util.HashMap;
import java.util.Map;

public class MainContentWithLeftSideBar extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * Holds the DOM id of the working entries holder (set by the JSP attribute)
	 */
	private String mWorkingEntriesHolderId = null;

	/**
	 * Holds the DOM id of the working entries (set by the JSP attribute)
	 */
	private String mWorkingEntriesId = null;

	private static final String TEMPLATE_FILENAME = "main-content-with-left-sidebar.html";
	private static final String TEMPLATE_PLACEHOLDER_BODY = "body";
	private static final String TEMPLATE_PLACEHOLDER_WORKING_ENTRIES_HOLDER_ID = "working-entries-holder-id";
	private static final String TEMPLATE_PLACEHOLDER_WORKING_ENTRIES_ID = "working-entries-id";

	/**
	 * Sets the DOM id of the working entries holder (called by the JSP)
	 *
	 * @param id - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setWorkingEntriesHolderId( final String id )
	{
		mWorkingEntriesHolderId = id == null || id.trim().isEmpty()
			? null
			: id.trim();

		return;
	}

	/**
	 * Sets the DOM id of the working entries (called by the JSP)
	 *
	 * @param id - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setWorkingEntriesId( final String id )
	{
		mWorkingEntriesId = id == null || id.trim().isEmpty()
			? null
			: id.trim();

		return;
	}

	@Override
	public int doStartTag()
	{
		final Map< String, String > placeHolders = new HashMap<>();
		placeHolders.put( TEMPLATE_PLACEHOLDER_WORKING_ENTRIES_HOLDER_ID, mWorkingEntriesHolderId == null ? "" : ( " id=\"" + mWorkingEntriesHolderId + "\"" ) );
		placeHolders.put( TEMPLATE_PLACEHOLDER_WORKING_ENTRIES_ID, mWorkingEntriesId == null ? "" : ( " id=\"" + mWorkingEntriesId + "\"" ) );

		showTemplate( true, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY, placeHolders, null );

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag()
	{
		showTemplate( false, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY );

		return EVAL_PAGE;
	}
}
