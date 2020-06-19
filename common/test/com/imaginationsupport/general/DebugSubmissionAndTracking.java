package com.imaginationsupport.general;

import java.util.ArrayList;

import com.imaginationsupport.backend.Job;
import com.imaginationsupport.backend.JobId;
import com.imaginationsupport.backend.JobManager;
import com.imaginationsupport.backend.JobObserver;
import com.imaginationsupport.backend.JobStatus;
import com.imaginationsupport.exceptions.JobException;

public class DebugSubmissionAndTracking
{
	
	public static class TestJob extends Job{
		public String payload="NO PAYLOAD SET";
		public TestJob(String payload){
			super();
			this.payload=payload;			
		}
		@Override
		public void execute() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class TrackJob implements JobObserver{
		@Override
		public void update(Job job){
			if(job instanceof TestJob){
				TestJob t=(TestJob)job;
				System.out.println(t.payload+"\t"+t.getStatus()+"\t"+t.getPercentDone());
			} else {
				System.err.println("Got Incorrect Job Type Back");
			}
		}
	}

	public static void main(String[] args) {
		if (args.length!=1){
			System.err.println("Usage: DebugSubmissionAndTracking <count>");
			System.exit(-1);
		}
		
		try{
			int count=Integer.parseInt(args[0]);
			
			JobManager jm=JobManager.getInstance();
			ArrayList<JobId> list= new ArrayList<>( count );
			
			for (int i=0;i<count;i++){
				Job j=new DebugSubmissionAndTracking.TestJob("job#"+i);
				list.add(jm.submit(j));	
			}
			
			boolean running=true;
			int c=0;
			while(running){
				running=false;
				System.out.print(c+"\t");
				for (JobId id: list){
					JobStatus s=jm.getJobStatus(id);
					if(s.equals(JobStatus.SUBMITTED) || s.equals(JobStatus.ACTIVE))
						running=true;
					System.out.print(s+"\t");
				}
				System.out.println("");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}		
		} catch (JobException e){
			System.err.println(e);
			e.printStackTrace();
		}
	}

}
