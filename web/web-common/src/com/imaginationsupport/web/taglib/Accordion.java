package com.imaginationsupport.web.taglib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Accordion extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * Holds the DOM id suffix (set by the JSP attribute)
	 */
	private String mIdSuffix = null;

	/**
	 * Holds the title (set by the JSP attribute)
	 */
	private String mTitle = null;

	/**
	 * Holds the accordion parent DOM id (set by the JSP attribute)
	 */
	private String mParentId = null;

	/**
	 * Holds if the accordion should be initially expanded (set by the JSP attribute)
	 */
	private boolean mExpanded = false;

	private static final String TEMPLATE_FILENAME = "accordion.html";
	private static final String TEMPLATE_PLACEHOLDER_BODY = "body";

	private static final String TEMPLATE_PLACEHOLDER_ID_SUFFIX = "idSuffix";
	private static final String TEMPLATE_PLACEHOLDER_TITLE = "title";
	private static final String TEMPLATE_PLACEHOLDER_PARENT_ID = "parentId";
	private static final String TEMPLATE_PLACEHOLDER_BODY_CSS_CLASSES = "bodyCssClasses";
	private static final String TEMPLATE_PLACEHOLDER_INITIALLY_EXPANDED = "initiallyExpanded";

	/**
	 * Sets the DOM id suffix (called by the JSP)
	 *
	 * @param idSuffix - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setIdSuffix( final String idSuffix )
	{
		mIdSuffix = idSuffix == null || idSuffix.trim().isEmpty()
			? null
			: idSuffix.trim();

		return;
	}

	/**
	 * Sets the title (called by the JSP)
	 *
	 * @param title - the accordion title
	 */
	@SuppressWarnings( "unused" )
	public void setTitle( final String title )
	{
		mTitle = title == null || title.trim().isEmpty()
			? null
			: title.trim();

		return;
	}

	/**
	 * Sets the title (called by the JSP)
	 *
	 * @param parentId - the accordion parent DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setParentId( final String parentId )
	{
		mParentId = parentId == null || parentId.trim().isEmpty()
			? null
			: parentId.trim();

		return;
	}

	/**
	 * Sets if the accordion is initially expanded (called by the JSP)
	 *
	 * @param expanded - True if the accordion should be initially expanded
	 */
	@SuppressWarnings( "unused" )
	public void setExpanded( final String expanded )
	{
		mExpanded = expanded != null && Boolean.parseBoolean( expanded );

		return;
	}

	@Override
	public int doStartTag()
	{
		final List< String > bodyCssClasses = new ArrayList<>();
		bodyCssClasses.add( "collapse" );
		if( mExpanded )
		{
			bodyCssClasses.add( "show" );
		}

		final Map< String, String > placeHolders = new HashMap<>();
		placeHolders.put( TEMPLATE_PLACEHOLDER_ID_SUFFIX, mIdSuffix );
		placeHolders.put( TEMPLATE_PLACEHOLDER_TITLE, mTitle == null ? "" : mTitle );
		placeHolders.put( TEMPLATE_PLACEHOLDER_PARENT_ID, mParentId );
		placeHolders.put( TEMPLATE_PLACEHOLDER_BODY_CSS_CLASSES, String.join( " ", bodyCssClasses ) );
		placeHolders.put( TEMPLATE_PLACEHOLDER_INITIALLY_EXPANDED, Boolean.toString( mExpanded ) );

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
