package com.imaginationsupport.backend.jobs;

import com.imaginationsupport.backend.Job;

public class TestJob extends Job {
	public TestJob(){
		super();
		type="NULL";
	}
	
	public void execute(){
		System.out.println("\t Start Test Job\t"+id);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("\t End Test Job\t"+id);
	}
	
}
