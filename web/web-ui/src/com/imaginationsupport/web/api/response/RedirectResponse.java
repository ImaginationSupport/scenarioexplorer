package com.imaginationsupport.web.api.response;

public class RedirectResponse implements ApiResponse
{
	private final String mRedirectUri;

	public String getRedirectUri()
	{
		return mRedirectUri;
	}

	public RedirectResponse( final String redirectUri )
	{
		mRedirectUri = redirectUri;
	}
}
