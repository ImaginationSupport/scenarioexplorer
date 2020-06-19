package com.imaginationsupport.web.taglib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainContentSection extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * Holds the DOM id (set by the JSP attribute)
	 */
	private String mId = null;

	private List< String > mCssClasses = new ArrayList<>();

	private static final String TEMPLATE_FILENAME = "page-section.html";
	private static final String TEMPLATE_PLACEHOLDER_CSS_CLASSES = "cssclasses";
	private static final String TEMPLATE_PLACEHOLDER_ATTRIBUTES = "attributes";
	private static final String TEMPLATE_PLACEHOLDER_BODY = "body";

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

	/**
	 * Sets the CSS class(es) (called by the JSP)
	 *
	 * @param cssClasses - the CSS classes, separated by spaces
	 */
	@SuppressWarnings( "unused" )
	public void setCssClasses( final String cssClasses )
	{
		for( final String entry : cssClasses.split( " " ) )
		{
			if( !mCssClasses.contains( entry ) )
			{
				mCssClasses.add( entry );
			}
		}

		return;
	}

	@Override
	public int doStartTag()
	{
		final Map< String, String > placeHolders = new HashMap<>();
		placeHolders.put( TEMPLATE_PLACEHOLDER_ATTRIBUTES, mId == null ? "" : ( " id=\"" + mId + "\"" ) );
		placeHolders.put( TEMPLATE_PLACEHOLDER_CSS_CLASSES, mCssClasses.isEmpty() ? "" : ( " " + String.join( " ", mCssClasses ) ) );

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
