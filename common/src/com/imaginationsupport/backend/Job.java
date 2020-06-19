package com.imaginationsupport.backend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import org.apache.logging.log4j.Logger;

import com.imaginationsupport.ImaginationSupportUtil;

public abstract class Job {
	protected JobId id;
	protected String type;
	protected JobStatus status;
	public double percentDone=0.0;
	private List<JobObserver> observers = new ArrayList<>();

	public Date submitted=null;
	public Date completed=null;

	protected static final Logger LOGGER = ImaginationSupportUtil.getBackendLogger();
	
	protected JobId getId() {
		return id;
	}
	
	protected void setJobId(JobId id) {
		this.id=id;
	}

	public String getType() {
		return type;
	}

	public JobStatus getStatus() {
		return status;
	}
	public void setStatus(JobStatus status) {
		LOGGER.info("Job "+this.getClass().getSimpleName()+": "+status);
		this.status = status;
		notifyObservers();
	}
	public double getPercentDone() {
		return percentDone;
	}
	public void setPercentDone(double percentDone) {
		this.percentDone = percentDone;
		notifyObservers();
	}	
		
	public void observe(JobObserver observer){
		observers.add(observer);		
	}
	
	public void notifyObservers(){
		for (JobObserver observer : observers) {
			observer.update(this);
		}
	} 	
	
	public abstract void execute() throws GeneralScenarioExplorerException;
}
