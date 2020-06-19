package com.imaginationsupport.web.taglib;

import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DropdownButton
{
	private static final String TEMPLATE_FILENAME = "dropdown-button.html";
	private static final String TEMPLATE_FILENAME_ENTRY = "dropdown-button-entry.html";

	private static final String TEMPLATE_ID = "id";
	private static final String TEMPLATE_TEXT = "text";
	private static final String TEMPLATE_CSS_CLASSES = "cssclasses";
	private static final String TEMPLATE_FONT_AWESOME_CLASS_NAME = "fontawesomeclassname";
	private static final String TEMPLATE_ENTRIES = "entries";

	private static final String TEMPLATE_ENTRY_ID = "id";
	private static final String TEMPLATE_ENTRY_TEXT = "text";
	private static final String TEMPLATE_ENTRY_CSS_CLASSES = "cssclasses";
	private static final String TEMPLATE_ENTRY_FONT_AWESOME_CLASS_NAME = "fontawesomeclassname";

	static class DropdownButtonEntry
	{
		private final String mText;
		private final Collection< String > mCssClasses;
		private final String mId;
		private final String mFontAwesomeClass;

		DropdownButtonEntry( final String id, final Collection< String > CssClasses, final String fontAwesomeClass, final String text )
		{
			mId = id;
			mCssClasses = CssClasses;
			mFontAwesomeClass = fontAwesomeClass;
			mText = text;

			return;
		}

		String getText()
		{
			return mText;
		}

		Collection< String > getCssClasses()
		{
			return mCssClasses;
		}

		String getId()
		{
			return mId;
		}

		String getFontAwesomeClass()
		{
			return mFontAwesomeClass;
		}
	}

	static String generateHtml(
		final ServletContext servletContext,
		final String id,
		final List< String > cssClasses,
		String fontAwesomeClassName,
		final String text,
		final Collection< DropdownButtonEntry > entries ) throws IOException
	{
		final String entryTemplate = TagLibHelpers.loadTemplate( servletContext, TEMPLATE_FILENAME_ENTRY );
		final StringBuilder entriesJoined = new StringBuilder();
		for( final DropdownButtonEntry entry : entries )
		{
			final Map< String, String > entryPlaceHolders = new HashMap<>();
			final String entryCssClasses = entry.getCssClasses() == null || entry.getCssClasses().isEmpty()
				? ""
				: " " + String.join( " ", entry.getCssClasses() );

			entryPlaceHolders.put( TEMPLATE_ENTRY_ID, entry.getId() == null ? "" : ( " id=\"" + entry.getId().trim() + "\"" ) );
			entryPlaceHolders.put( TEMPLATE_ENTRY_TEXT, entry.getText() == null ? "" : entry.getText() );
			entryPlaceHolders.put( TEMPLATE_ENTRY_CSS_CLASSES, entryCssClasses );
			entryPlaceHolders.put( TEMPLATE_ENTRY_FONT_AWESOME_CLASS_NAME, entry.getFontAwesomeClass() == null ? "" : entry.getFontAwesomeClass() );

			entriesJoined.append( TagLibHelpers.replaceTemplatePlaceHolders( entryTemplate, entryPlaceHolders ) );
		}

		final Map< String, String > placeHolders = new HashMap<>();
		final String dropdownCssClasses = cssClasses == null || cssClasses.isEmpty()
			? ""
			: " " + String.join( " ", cssClasses );

		placeHolders.put( TEMPLATE_ID, id == null ? "" : id.trim() );
		placeHolders.put( TEMPLATE_TEXT, StringEscapeUtils.escapeHtml4( text == null ? "" : text.trim() ) );
		placeHolders.put( TEMPLATE_CSS_CLASSES, dropdownCssClasses );
		placeHolders.put( TEMPLATE_FONT_AWESOME_CLASS_NAME, fontAwesomeClassName == null ? "" : fontAwesomeClassName.trim() );
		placeHolders.put( TEMPLATE_ENTRIES, entriesJoined.toString() );

		return TagLibHelpers.replaceTemplatePlaceHolders( TagLibHelpers.loadTemplate( servletContext, TEMPLATE_FILENAME ), placeHolders );
	}
}