package com.imaginationsupport.web.taglib;

import com.imaginationsupport.ImaginationSupportUtil;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.*;

public class Button extends SimpleTagSupport
{
	/**
	 * Holds the DOM id (set by the JSP attribute)
	 */
	private String mId = null;

	/**
	 * Holds the CSS classes to add (set by the JSP attribute)
	 */
	private String mCSSClassesRaw = null;

	/**
	 * Holds the font awesome class name (set by the JSP attribute)
	 */
	private String mFontAwesomeClassName = null;

	/**
	 * Holds the button text (set by the JSP attribute)
	 */
	private String mText = "";

	private static final String TEMPLATE_FILENAME_BASIC = "button-basic.html";
	private static final String TEMPLATE_FILENAME_WITH_ICON = "button-with-icon.html";

	private static final String TEMPLATE_ID = "id";
	private static final String TEMPLATE_TEXT = "text";
	private static final String TEMPLATE_CSS_CLASSES = "cssclasses";
	private static final String TEMPLATE_FONT_AWESOME_CLASS_NAME = "fontawesomeclassname";

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	@SuppressWarnings( "unused" )
	public void setId( final String newValue )
	{
		mId = newValue == null || newValue.trim().isEmpty()
			? null
			: newValue.trim();
		return;
	}

	@SuppressWarnings( "unused" )
	public void setCssClasses( final String newValue )
	{
		mCSSClassesRaw = newValue == null || newValue.trim().isEmpty()
			? null
			: newValue.trim();
		return;
	}

	@SuppressWarnings( "unused" )
	public void setFontAwesomeIconClassName( final String newValue )
	{
		mFontAwesomeClassName = newValue == null || newValue.trim().isEmpty()
			? null
			: newValue.trim();
		return;
	}

	@SuppressWarnings( "unused" )
	public void setText( final String newValue )
	{
		mText = newValue == null || newValue.trim().isEmpty()
			? null
			: newValue.trim();
		return;
	}

	@Override
	public void doTag()
	{
		try
		{
			final List< String > cssClasses = new ArrayList<>();
			cssClasses.add( "btn" );
			cssClasses.add( "btn-primary" );

			if( mCSSClassesRaw != null )
			{
				cssClasses.addAll( Arrays.asList( mCSSClassesRaw.split( " " ) ) );
			}

			final PageContext pageContext = (PageContext)getJspContext();

			getJspContext()
				.getOut()
				.print( generateHtml( pageContext.getServletContext(), mId, cssClasses, mFontAwesomeClassName, mText ) );
		}
		catch( final Throwable error )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( error ) );
		}

		return;
	}

	static String generateHtml(
		final ServletContext servletContext,
		final String id,
		final List< String > cssClasses,
		String fontAwesomeClassName,
		final String text ) throws IOException
	{
		final boolean includeIcon = fontAwesomeClassName != null && !fontAwesomeClassName.trim().isEmpty();

		final Map< String, String > placeHolders = new HashMap<>();

		placeHolders.put( TEMPLATE_ID, id == null ? "" : ( " id=\"" + id.trim() + "\"" ) );
		placeHolders.put( TEMPLATE_TEXT, StringEscapeUtils.escapeHtml4( text == null ? "" : text.trim() ) );
		placeHolders.put( TEMPLATE_CSS_CLASSES, cssClasses == null ? "" : String.join( " ", cssClasses ) );
		placeHolders.put( TEMPLATE_FONT_AWESOME_CLASS_NAME, fontAwesomeClassName == null ? "" : fontAwesomeClassName.trim() );

		return TagLibHelpers.replaceTemplatePlaceHolders(
			TagLibHelpers.loadTemplate( servletContext, includeIcon ? TEMPLATE_FILENAME_WITH_ICON : TEMPLATE_FILENAME_BASIC ),
			placeHolders
		);
	}
}