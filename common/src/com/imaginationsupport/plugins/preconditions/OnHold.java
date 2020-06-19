package com.imaginationsupport.plugins.preconditions;

import com.imaginationsupport.data.State;
import com.imaginationsupport.plugins.Precondition;

public class OnHold extends Precondition {

	private String reason="Set by a user.";
	
	public OnHold() {}
	
	public OnHold(String rationale) {
		this.reason=reason;
	}
	
	@Override
	public String getPluginJavaScriptSourceUriPath()
	{
		return "/js/plugins/precondition/onhold.js";
	}

	@Override
	public String getLabel() {
		return "On Hold";
	}

	@Override
	public String getDescription() {
		return "This Conditioning Event is on hold and will not fire: "+reason;
	}

	@Override
	public boolean satisfied(State state) {
		return false; // this precondition is never satisified
	}
}
