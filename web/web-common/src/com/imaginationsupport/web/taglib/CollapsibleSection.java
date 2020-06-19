package com.imaginationsupport.web.taglib;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CollapsibleSection extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	private static final String TEMPLATE_FILENAME = "collapsible-section.html";
	private static final String TEMPLATE_PLACEHOLDER_BODY = "body";
	private static final String TEMPLATE_PLACEHOLDER_HEADER_CSS_CLASSES = "header-css-classes";
	private static final String TEMPLATE_PLACEHOLDER_BODY_CSS_CLASSES = "body-css-classes";
	private static final String TEMPLATE_PLACEHOLDER_ICON_CSS_CLASSES = "icon-css-classes";
	private static final String TEMPLATE_PLACEHOLDER_BODY_ID = "body-id";
	private static final String TEMPLATE_PLACEHOLDER_TITLE = "title";
	private static final String TEMPLATE_PLACEHOLDER_BODY_STYLES = "body-styles";

	/**
	 * Holds the text to use in the header (set by the JSP attribute)
	 */
	private String mTitle = null;

	/**
	 * Holds the DOM id of the collapsible body element (set by the JSP attribute)
	 */
	private String mBodyId = null;

	/**
	 * Holds the CSS classes to add to the header (set by the JSP attribute)
	 */
	private Set< String > mExtraCssClassesHeader = new LinkedHashSet<>();

	/**
	 * Holds the CSS classes to add to the body (set by the JSP attribute)
	 */
	private Set< String > mExtraCssClassesBody = new LinkedHashSet<>();

	/**
	 * Holds if the collapsible section is initially expanded
	 */
	private boolean mInitiallyExpanded = true;

	/**
	 * Sets the DOM id of the collapsible body element (called by the JSP)
	 *
	 * @param title - the title to use
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
	 * Sets the DOM id of the collapsible body element (called by the JSP)
	 *
	 * @param bodyId - the body DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setBodyId( final String bodyId )
	{
		mBodyId = bodyId == null || bodyId.trim().isEmpty()
			? null
			: bodyId.trim();

		return;
	}

	/**
	 * Sets the CSS classes to add for the header (called by the JSP)
	 *
	 * @param cssClasses - the header CSS classes
	 */
	@SuppressWarnings( "unused" )
	public void setHeaderCssClasses( final String cssClasses )
	{
		mExtraCssClassesHeader.addAll( parseCssClasses( cssClasses ) );

		return;
	}

	/**
	 * Sets the CSS classes to add for the body (called by the JSP)
	 *
	 * @param cssClasses - the body CSS classes
	 */
	@SuppressWarnings( "unused" )
	public void setBodyCssClasses( final String cssClasses )
	{
		mExtraCssClassesBody.addAll( parseCssClasses( cssClasses ) );

		return;
	}

	/**
	 * Sets if the collapsible section is initially expanded (called by the JSP)
	 *
	 * @param initiallyExpanded - True if the collapsible section is initially expanded
	 */
	@SuppressWarnings( "unused" )
	public void setInitiallyExpanded( final String initiallyExpanded )
	{
		mInitiallyExpanded = initiallyExpanded == null || Boolean.parseBoolean( initiallyExpanded.trim() );

		return;
	}

	@Override
	public int doStartTag()
	{
		final Set< String > iconCssClasses = new LinkedHashSet<>();
		iconCssClasses.add( "fas" );
		iconCssClasses.add( mInitiallyExpanded ? "fa-minus-circle" : "fa-plus-circle" );
		iconCssClasses.add( "text-primary" );
		iconCssClasses.add( "mb-1" );
		iconCssClasses.add( "mr-1" );

		final Map< String, String > placeHolders = new HashMap<>();
		placeHolders.put( TEMPLATE_PLACEHOLDER_HEADER_CSS_CLASSES, String.join( " ", mExtraCssClassesHeader ) );
		placeHolders.put( TEMPLATE_PLACEHOLDER_BODY_CSS_CLASSES, String.join( " ", mExtraCssClassesBody ) );
		placeHolders.put( TEMPLATE_PLACEHOLDER_ICON_CSS_CLASSES, String.join( " ", iconCssClasses ) );
		placeHolders.put( TEMPLATE_PLACEHOLDER_BODY_ID, mBodyId );
		placeHolders.put( TEMPLATE_PLACEHOLDER_TITLE, mTitle );
		placeHolders.put( TEMPLATE_PLACEHOLDER_BODY_STYLES, mInitiallyExpanded ? "" : "display:none" );

		showTemplate( true, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY, placeHolders, null );

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag()
	{
		showTemplate( false, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY, null, null );
		return EVAL_PAGE;
	}

	private Set< String > parseCssClasses( final String cssClasses )
	{
		final Set< String > parsed = new LinkedHashSet<>();

		if( cssClasses != null )
		{
			for( final String entry : cssClasses.trim().split( "\\s" ) )
			{
				final String trimmed = entry.trim();
				if( !trimmed.isEmpty() )
				{
					parsed.add( trimmed );
				}
			}
		}

		return parsed;
	}
}
