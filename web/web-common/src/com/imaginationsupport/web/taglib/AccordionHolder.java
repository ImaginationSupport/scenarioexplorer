package com.imaginationsupport.web.taglib;

import java.util.HashMap;
import java.util.Map;

public class AccordionHolder extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * Holds the DOM id suffix (set by the JSP attribute)
	 */
	private String mId = null;

	private static final String TEMPLATE_FILENAME = "accordion-holder.html";
	private static final String TEMPLATE_PLACEHOLDER_BODY = "body";
	private static final String TEMPLATE_PLACEHOLDER_ID = "id";

	/**
	 * Sets the DOM id (called by the JSP)
	 *
	 * @param id - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setId( final String id )
	{
		mId = id == null || id.trim().isEmpty()
			? null
			: id.trim();

		return;
	}

	@Override
	public int doStartTag()
	{
		final Map< String, String > placeHolders = new HashMap<>();
		placeHolders.put( TEMPLATE_PLACEHOLDER_ID, mId == null ? "" : ( " id=\"" + mId + "\"" ) );

		showTemplate( true, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY, placeHolders, null );

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag()
	{
		showTemplate( false, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY, null, null );

		return EVAL_PAGE;
	}
}
