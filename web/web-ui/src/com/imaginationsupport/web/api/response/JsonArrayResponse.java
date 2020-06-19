package com.imaginationsupport.web.api.response;

import org.json.JSONArray;

public class JsonArrayResponse implements ApiResponse
{
	private final JSONArray mContent;

	public JSONArray getContent()
	{
		return mContent;
	}

	public JsonArrayResponse( final JSONArray content )
	{
		mContent = content;

		return;
	}
}
