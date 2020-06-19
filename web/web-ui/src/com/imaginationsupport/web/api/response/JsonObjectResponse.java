package com.imaginationsupport.web.api.response;

import org.json.JSONObject;

public class JsonObjectResponse implements ApiResponse
{
	private final JSONObject mContent;

	public JSONObject getContent()
	{
		return mContent;
	}

	public JsonObjectResponse( final JSONObject content )
	{
		mContent = content;

		return;
	}
}
