package com.imaginationsupport.web.taglib;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.web.WebCommon;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Head extends SimpleTagSupport
{
	/**
	 * Holds if the page should only include the minimal JS
	 */
	private boolean mMinimalJS = false;

	/**
	 * Holds the main javascript path
	 */
	private String mPageJavaScriptPath = null;

	/**
	 * Holds if the page should load the datepicker library
	 */
	private boolean mUseDatePicker = false;

	/**
	 * Holds if the page should load the colorpicker library
	 */
	private boolean mUseColorPicker = false;

	/**
	 * Holds if the page should load the slider library
	 */
	private boolean mUseSlider = false;

	/**
	 * Holds if the page should load the datatables library
	 */
	private boolean mUseDataTables = false;

	/**
	 * Holds if the page should load the plugins library
	 */
	private boolean mUsePlugins = false;

	/**
	 * Holds if the page should load the view canvas library
	 */
	private boolean mUseViewCanvas = false;

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	/**
	 * Sets if we should use no JavaScript files
	 *
	 * @param value - true if we should not use JavaScript files
	 */
	@SuppressWarnings( "unused" )
	public void setMinimalJS( final String value )
	{
		mMinimalJS = value != null && !value.trim().isEmpty() && Boolean.parseBoolean( value );

		return;
	}

	/**
	 * Sets if the date picker library is needed (called by the JSP)
	 *
	 * @param value - true if the date picker library should be included
	 */
	@SuppressWarnings( "unused" )
	public void setPageJavaScriptPath( final String value )
	{
		if( value != null && !value.trim().isEmpty() )
		{
			mPageJavaScriptPath = value.trim();
		}

		return;
	}

	/**
	 * Sets if the date picker library is needed (called by the JSP)
	 *
	 * @param value - true if the date picker library should be included
	 */
	@SuppressWarnings( "unused" )
	public void setDatePicker( final String value )
	{
		mUseDatePicker = value != null && !value.trim().isEmpty() && Boolean.parseBoolean( value );

		return;
	}

	/**
	 * Sets if the color picker library is needed (called by the JSP)
	 *
	 * @param value - true if the color picker library should be included
	 */
	@SuppressWarnings( "unused" )
	public void setColorPicker( final String value )
	{
		mUseColorPicker = value != null && !value.trim().isEmpty() && Boolean.parseBoolean( value );

		return;
	}

	/**
	 * Sets if the slider library is needed (called by the JSP)
	 *
	 * @param value - true if the slider library should be included
	 */
	@SuppressWarnings( "unused" )
	public void setSlider( final String value )
	{
		mUseSlider = value != null && !value.trim().isEmpty() && Boolean.parseBoolean( value );

		return;
	}

	/**
	 * Sets if the datatables library is needed (called by the JSP)
	 *
	 * @param value - true if the slider library should be included
	 */
	@SuppressWarnings( "unused" )
	public void setDatatables( final String value )
	{
		mUseDataTables = value != null && !value.trim().isEmpty() && Boolean.parseBoolean( value );

		return;
	}

	/**
	 * Sets if the plugins library is needed (called by the JSP)
	 *
	 * @param value - true if the plugins library should be included
	 */
	@SuppressWarnings( "unused" )
	public void setPlugins( final String value )
	{
		mUsePlugins = value != null && !value.trim().isEmpty() && Boolean.parseBoolean( value );

		return;
	}

	/**
	 * Sets if the view canvas library is needed (called by the JSP)
	 *
	 * @param value - true if the view canvas library should be included
	 */
	@SuppressWarnings( "unused" )
	public void setViewCanvas( final String value )
	{
		mUseViewCanvas = value != null && !value.trim().isEmpty() && Boolean.parseBoolean( value );

		return;
	}

	@Override
	public void doTag()
	{
		try
		{
			final JspWriter out = getJspContext().getOut();

			final PageContext pageContext = (PageContext)getJspContext();
			final HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();

			final String username = WebCommon.getCurrentUserId( request );

			final Set< String > javascriptScriptFiles = new LinkedHashSet<>();
			final Set< String > cssScriptFiles = new LinkedHashSet<>();

			cssScriptFiles.add( "css/bootstrap.min.css" );
//			cssScriptFiles.add( "css/fa-svg-with-js-pro-5.0.13.css" );

			javascriptScriptFiles.add( "js/third-party/jquery-3.4.1.min.js" );
			javascriptScriptFiles.add( "js/third-party/bootstrap.bundle.min.js" );
			javascriptScriptFiles.add( "js/third-party/fontawesome-pro-5.3.1-all.min.js" );
			javascriptScriptFiles.add( "js/third-party/datatables.min.js" );
			javascriptScriptFiles.add( "js/third-party/date_fns-1.28.3.min.js" );

			javascriptScriptFiles.add( "js/util.js" );
			javascriptScriptFiles.add( "js/taglib.js" );

			if( !mMinimalJS )
			{
				javascriptScriptFiles.add( "js/nav-helper.js" );
				javascriptScriptFiles.add( "js/api/api-data.js" );
				javascriptScriptFiles.add( "js/api/api.js" );

				if( mUseDatePicker )
				{
					javascriptScriptFiles.add( "js/third-party/bootstrap-datepicker.min.js" );
					cssScriptFiles.add( "css/bootstrap-datepicker3.min.css" );
				}

				if( mUseColorPicker )
				{
					javascriptScriptFiles.add( "js/third-party/bootstrap-colorpicker.min.js" );
					cssScriptFiles.add( "css/bootstrap-colorpicker.min.css" );
				}

				if( mUseSlider )
				{
					javascriptScriptFiles.add( "js/third-party/bootstrap-slider.min.js" );
					cssScriptFiles.add( "css/bootstrap-slider.min.css" );
				}

				if( mUseDataTables )
				{
					javascriptScriptFiles.add( "js/third-party/datatables.min.js" );
					cssScriptFiles.add( "css/datatables.min.css" );
				}

				if( mUsePlugins )
				{
					javascriptScriptFiles.add( "js/plugin-helpers.js" );
					javascriptScriptFiles.add( "js/plugin-taglib.js" );
				}

				if( mUseViewCanvas )
				{
					javascriptScriptFiles.add( "js/view-canvas/point.js" );
					javascriptScriptFiles.add( "js/view-canvas/node.js" );
					javascriptScriptFiles.add( "js/view-canvas/timeline-event.js" );
					javascriptScriptFiles.add( "js/view-canvas/item.js" );
					javascriptScriptFiles.add( "js/view-canvas/view-canvas.js" );
				}
			}

			if( mPageJavaScriptPath != null )
			{
				javascriptScriptFiles.add( mPageJavaScriptPath );
			}

			cssScriptFiles.add( "css/scenarioexplorer.min.css" );

			// generate the HTML from the javascript files list
			final StringBuilder javascriptScriptFilesHtml = new StringBuilder();
			for( final String entry : javascriptScriptFiles )
			{
				if( javascriptScriptFilesHtml.length() > 0 )
				{
					javascriptScriptFilesHtml.append( "\t" );
				}

				javascriptScriptFilesHtml.append( "<script src=\"" )
					.append( entry )
					.append( "\"></script>\n" );
			}

			// generate the HTML from the css files list
			final StringBuilder cssScriptFilesHtml = new StringBuilder();
			for( final String entry : cssScriptFiles )
			{
				if( cssScriptFilesHtml.length() > 0 )
				{
					cssScriptFilesHtml.append( "\t" );
				}

				cssScriptFilesHtml.append( "<link rel=\"stylesheet\" type=\"text/css\" href=\"" )
					.append( entry )
					.append( "\" />\n" );
			}

			// add all the placeholder entries needed
			final Map< String, String > placeHolders = new HashMap<>();
			placeHolders.put( "javascript-libraries", javascriptScriptFilesHtml.toString() );
			placeHolders.put( "css-libraries", cssScriptFilesHtml.toString() );
			placeHolders.put( "init-jsp", mPageJavaScriptPath == null ? "" : "<script type=\"text/javascript\">$(initJSP.bind(this,'" + username + "'));</script>" );

			final String template = TagLibHelpers.loadTemplate( request.getServletContext(), "head.html" );
			out.write( TagLibHelpers.replaceTemplatePlaceHolders( template, placeHolders ) );
		}
		catch( final Throwable error )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( error ) );
		}

		return;
	}
}