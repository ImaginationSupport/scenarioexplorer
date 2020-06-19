package com.imaginationsupport.backend;

import com.imaginationsupport.exceptions.JobException;

public class JobId {
	protected String id=null;
	
	public boolean equals(JobId other) throws JobException{
		if(id==null) throw new JobException("Comparing with a null JobId");
		if(other.id==null) throw new JobException("Comparing to a null JobId");
		return this.id.equals(other.id);
	}

	public String toString(){
		return id;
	}
}
