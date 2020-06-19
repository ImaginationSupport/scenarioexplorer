package com.imaginationsupport.plugins.effects;

import com.imaginationsupport.data.State;
import com.imaginationsupport.exceptions.DatastoreException;
import com.imaginationsupport.plugins.Effect;

public class ErrorEffect extends Effect
{
	public ErrorEffect(){}

	@Override
	public String getLabel() {
		return "Null Effect";
	}

	@Override
	public String getDescription() {
		return "This is an error condition when no other effects are given";
	}

	@Override
	public void apply(State state)
	{
		state.setLabel("ERROR");
	}

	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/outcome-effects/error.js";
	}
}
