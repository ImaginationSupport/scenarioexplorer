package com.imaginationsupport.backend;

import com.imaginationsupport.backend.jobs.TestJob;
import com.imaginationsupport.exceptions.JobException;

public class JobTester implements JobObserver{

	public static JobManager jm=null;
	public static JobTester jt=null;
	public static final int load=1000;
	
	public static void main(String[] args) {
		jt=new JobTester();
		try{
			jm=JobManager.getInstance();
			for (int i=0; i<load; i++){
				Job j=new TestJob();
				try {
					JobId id= jm.submit(j);
					jm.observeJob(id, jt);
				} catch (JobException e) {
					e.printStackTrace();
				}
				
			}
		} finally {
			jm.close();
		}
	}

	@Override
	public void update(Job job) {
		System.out.println("Notified\t"+job.getId()+"\t"+job.getStatus());
		if (job.getStatus()==JobStatus.COMPLETED || job.getStatus()==JobStatus.ERROR) jm.clearJob(job.getId());
	}
	

}
