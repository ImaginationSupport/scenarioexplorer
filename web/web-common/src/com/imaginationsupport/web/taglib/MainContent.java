package com.imaginationsupport.web.taglib;

import java.io.IOException;
import java.util.*;

public class MainContent extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	/**
	 * Holds the default DOM id
	 */
	private static final String DEFAULT_ID = "main-content-holder";

	/**
	 * Holds the DOM id
	 */
	private String mId = DEFAULT_ID;

	/**
	 * Holds the title (set by the JSP attribute)
	 */
	private String mTitle = null;

	private List< String > mCssClasses = new ArrayList<>();

	private String mSaveId = null;
	private String mSaveLabel = null;

	private String mCancelId = null;
	private String mCancelLabel = null;

	private String mDeleteId = null;
	private String mDeleteLabel = null;

	private String mNewId = null;
	private String mNewLabel = null;

	private String mExportId = null;
	private String mExportLabel = null;

	private String mImportId = null;
	private String mImportLabel = null;

	private static final String TEMPLATE_FILENAME = "main-content.html";
	private static final String TEMPLATE_PLACEHOLDER_BODY = "body";

	/**
	 * Sets the title (called by the JSP)
	 *
	 * @param id - the section id
	 */
	@SuppressWarnings( "unused" )
	public void setId( final String id )
	{
		mId = id == null || id.trim().isEmpty()
			? DEFAULT_ID
			: id.trim();

		return;
	}

	/**
	 * Sets the title (called by the JSP)
	 *
	 * @param title - the section title
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

	/**
	 * Sets the DOM id of the save button (called by the JSP)
	 *
	 * @param id - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setSaveId( final String id )
	{
		mSaveId = id == null || id.trim().isEmpty()
			? null
			: id.trim();

		return;
	}

	/**
	 * Sets the label of the save button (called by the JSP)
	 *
	 * @param newLabel - the new label
	 */
	@SuppressWarnings( "unused" )
	public void setSaveLabel( final String newLabel )
	{
		mSaveLabel = newLabel == null || newLabel.trim().isEmpty()
			? null
			: newLabel.trim();

		return;
	}

	/**
	 * Sets the DOM id of the cancel button (called by the JSP)
	 *
	 * @param newId - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setCancelId( final String newId )
	{
		mCancelId = newId == null || newId.trim().isEmpty()
			? null
			: newId.trim();

		return;
	}

	/**
	 * Sets the label of the cancel button (called by the JSP)
	 *
	 * @param newLabel - the new label
	 */
	@SuppressWarnings( "unused" )
	public void setCancelLabel( final String newLabel )
	{
		mCancelLabel = newLabel == null || newLabel.trim().isEmpty()
			? null
			: newLabel.trim();

		return;
	}

	/**
	 * Sets the DOM id of the delete button (called by the JSP)
	 *
	 * @param newId - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setDeleteId( final String newId )
	{
		mDeleteId = newId == null || newId.trim().isEmpty()
			? null
			: newId.trim();

		return;
	}

	/**
	 * Sets the label of the delete button (called by the JSP)
	 *
	 * @param newLabel - the new label
	 */
	@SuppressWarnings( "unused" )
	public void setDeleteLabel( final String newLabel )
	{
		mDeleteLabel = newLabel == null || newLabel.trim().isEmpty()
			? null
			: newLabel.trim();

		return;
	}

	/**
	 * Sets the DOM id of the new button (called by the JSP)
	 *
	 * @param newId - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setNewId( final String newId )
	{
		mNewId = newId == null || newId.trim().isEmpty()
			? null
			: newId.trim();

		return;
	}

	/**
	 * Sets the label of the label button (called by the JSP)
	 *
	 * @param newLabel - the new label
	 */
	@SuppressWarnings( "unused" )
	public void setNewLabel( final String newLabel )
	{
		mNewLabel = newLabel == null || newLabel.trim().isEmpty()
			? null
			: newLabel.trim();

		return;
	}

	/**
	 * Sets the DOM id of the export button (called by the JSP)
	 *
	 * @param exportId - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setExportId( final String exportId )
	{
		mExportId = exportId == null || exportId.trim().isEmpty()
			? null
			: exportId.trim();
	}

	/**
	 * Sets the label of the label button (called by the JSP)
	 *
	 * @param exportLabel - the export label
	 */
	@SuppressWarnings( "unused" )
	public void setExportLabel( final String exportLabel )
	{
		mExportLabel = exportLabel == null || exportLabel.trim().isEmpty()
			? null
			: exportLabel.trim();
	}

	/**
	 * Sets the DOM id of the import button (called by the JSP)
	 *
	 * @param importId - the DOM id
	 */
	@SuppressWarnings( "unused" )
	public void setImportId( final String importId )
	{
		mImportId = importId == null || importId.trim().isEmpty()
			? null
			: importId.trim();
	}

	/**
	 * Sets the label of the label button (called by the JSP)
	 *
	 * @param importLabel - the import label
	 */
	@SuppressWarnings( "unused" )
	public void setImportLabel( final String importLabel )
	{
		mImportLabel = importLabel == null || importLabel.trim().isEmpty()
			? null
			: importLabel.trim();
	}

	@Override
	public int doStartTag()
	{
		final StringBuilder buttonsBar = new StringBuilder();
		try
		{
			if( mSaveId != null )
			{
				buttonsBar.append( Button.generateHtml(
					pageContext.getServletContext(),
					mSaveId,
					null,
					TagLibHelpers.FontAwesomeCssClasses.Save,
					mSaveLabel == null ? "Save" : mSaveLabel ) );
			}

			if( mCancelId != null )
			{
				buttonsBar.append( Button.generateHtml(
					pageContext.getServletContext(),
					mCancelId,
					null,
					TagLibHelpers.FontAwesomeCssClasses.Cancel,
					mCancelLabel == null ? "Cancel" : mCancelLabel ) );
			}

			if( mDeleteId != null )
			{
				buttonsBar.append( Button.generateHtml(
					pageContext.getServletContext(),
					mDeleteId,
					null,
					TagLibHelpers.FontAwesomeCssClasses.Delete,
					mDeleteLabel == null ? "Delete" : mDeleteLabel ) );
			}

			if( mNewId != null )
			{
				buttonsBar.append( Button.generateHtml(
					pageContext.getServletContext(),
					mNewId,
					null,
					TagLibHelpers.FontAwesomeCssClasses.Create,
					mNewLabel == null ? "New" : mNewLabel ) );
			}

			if( mExportId != null )
			{
				final List< DropdownButton.DropdownButtonEntry > entries = new ArrayList<>();

				entries.add( new DropdownButton.DropdownButtonEntry(
					"export-file-download",
					null,
					TagLibHelpers.FontAwesomeCssClasses.DownloadFile + "  text-primary",
					"A File to Download (.json)" ) );
				entries.add( new DropdownButton.DropdownButtonEntry(
					"export-clone-project",
					null,
					TagLibHelpers.FontAwesomeCssClasses.Clone + "  text-primary",
					"Clone This Project" ) );
				entries.add( new DropdownButton.DropdownButtonEntry(
					"export-create-template",
					null,
					TagLibHelpers.FontAwesomeCssClasses.CreateTemplate + "  text-primary",
					"Create a Project Template" ) );

				buttonsBar.append( DropdownButton.generateHtml(
					pageContext.getServletContext(),
					mExportId,
					null,
					TagLibHelpers.FontAwesomeCssClasses.Export,
					mExportLabel == null ? "Export" : mExportLabel,
					entries
				) );
			}

			if( mImportId != null )
			{
				buttonsBar.append( Button.generateHtml(
					pageContext.getServletContext(),
					mImportId,
					null,
					TagLibHelpers.FontAwesomeCssClasses.Import,
					mImportLabel == null ? "Import" : mImportLabel ) );
			}
		}
		catch( final IOException e )
		{
			LOGGER.error( "Error generating button bar!", e );
		}

		final Map< String, String > placeHolders = new HashMap<>();
		placeHolders.put( "id", mId );
		placeHolders.put( "title", mTitle == null ? "" : mTitle );
		placeHolders.put( "buttons", buttonsBar.toString() );
		placeHolders.put( "css classes", mCssClasses.isEmpty() ? "" : ( " " + String.join( " ", mCssClasses ) ) );

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
