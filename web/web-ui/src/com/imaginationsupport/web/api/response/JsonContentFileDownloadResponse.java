package com.imaginationsupport.web.api.response;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class JsonContentFileDownloadResponse implements ApiResponse
{
	private final String mFilename;
	private final byte[] mContent;

	public JsonContentFileDownloadResponse( final String filename, final JSONObject json )
	{
		mFilename = filename;
		mContent = json.toString().getBytes( StandardCharsets.UTF_8 );

		return;
	}

	public String getFilename()
	{
		return mFilename;
	}

	public byte[] getContent()
	{
		return mContent;
	}
}
