package com.imaginationsupport.web.taglib;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HelpSideBarShowMore extends ScenarioExplorerBodyTagBase
{
	/**
	 * serial version UID
	 */
	public static final long serialVersionUID = 1L;

	private static final String TEMPLATE_FILENAME = "help-sidebar-showmore.html";
	private static final String TEMPLATE_PLACEHOLDER_SHOWMORE_ID = "showmoreId";
	private static final String TEMPLATE_PLACEHOLDER_SHOWLESS_ID = "showlessId";
	private static final String TEMPLATE_PLACEHOLDER_BODY_ID = "bodyId";
	private static final String TEMPLATE_PLACEHOLDER_BODY = "body";

	private static final Random RNG = new Random();
	private static final String VALID_ID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789";

	@Override
	public int doStartTag()
	{
		final String baseId = generateRandomId();
		final String bodyId = "help-show-more-" + baseId + "-body";

		final Map< String, String > placeHolders = new HashMap<>();
		placeHolders.put( TEMPLATE_PLACEHOLDER_SHOWMORE_ID, "help-show-more-" + baseId + "-showmore");
		placeHolders.put( TEMPLATE_PLACEHOLDER_SHOWLESS_ID, "help-show-more-" + baseId + "-showless");
		placeHolders.put( TEMPLATE_PLACEHOLDER_BODY_ID, bodyId );

		showTemplate( true, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY, placeHolders, null );

		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag()
	{
		showTemplate( false, TEMPLATE_FILENAME, TEMPLATE_PLACEHOLDER_BODY, null, null );

		return EVAL_PAGE;
	}

	private static String generateRandomId()
	{
		final StringBuilder baseId = new StringBuilder();
		for( int i = 0; i < 8; ++i )
		{
			baseId.append( VALID_ID_CHARACTERS.charAt( RNG.nextInt( VALID_ID_CHARACTERS.length() ) ) );
		}

		return baseId.toString();
	}
}
