package com.imaginationsupport.web.taglib;

import java.util.HashMap;
import java.util.Map;

public class HelpTip extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	private static final String TEMPLATE_FILENAME = "help-tip.html";
	private static final String TEMPLATE_PLACEHOLDER_BODY = "body";

	private String mIntro = "Tip";
	private String mIconClasses = "fas fa-info-circle fa-2x text-primary";

	@SuppressWarnings( "unused" )
	public void setIntro( final String newValue )
	{
		mIntro = newValue == null || newValue.trim().isEmpty()
			? null
			: newValue.trim();

		return;
	}

	@SuppressWarnings( "unused" )
	public void setIconClasses( final String newValue )
	{
		mIconClasses = newValue == null || newValue.trim().isEmpty()
			? null
			: newValue.trim();

		return;
	}

	@Override
	public int doStartTag()
	{
		final Map< String, String > placeHolders = new HashMap<>();
		placeHolders.put( "intro", mIntro );
		placeHolders.put( "icon classes", mIconClasses );

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
