package com.imaginationsupport.backend;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.imaginationsupport.ImaginationSupportConfig;
import com.imaginationsupport.exceptions.JobException;
import com.imaginationsupport.data.Uid;

public class JobManager {
	
	private ExecutorService threadPool=null;
	
	private static final int default_thread_count=2;
	private int threadCount=default_thread_count;
	
	private boolean threaded=false;
	
	/*
	 * Singleton
	 * Job Manager is a singleton class
	 */
	private static JobManager instance=null;

	public static JobManager getInstance(){
		if(instance==null){
			instance=new JobManager();
		}
		return instance;
	}
	
	protected JobManager(){
		if(!threaded) return;
		
		threadCount=default_thread_count;
		try{
			threadCount=ImaginationSupportConfig.getJobManagerThreads();
		} catch (Exception e){
			threadCount=default_thread_count;
		}
		threadPool=Executors.newFixedThreadPool(threadCount);
		for (int i=0;i<threadCount;i++){
			JobAgent agent=new JobAgent("JobAgent"+i,this);
			threadPool.submit(agent);
		}
	}
	
	// Keeps a local copy of Jobs that can be observed
	private Hashtable<JobId,Job> jobs = new Hashtable<JobId,Job>();
	
	private Queue<Job> queue=new LinkedList<Job>();
	
	private int count=0;
	
	private JobId getNewJobId(Job job){
		JobId jobId=new JobId();
		jobId.id="job:"+(count++)+":"+Uid.getUid();
		job.setJobId(jobId);
		return jobId;
	}
		
	/*
	 * Submit a Job to the JobManager
	 * returns a JobId
	 */
	public synchronized JobId submit(Job job) throws JobException{
		if (job.getId()!=null) throw new JobException("Submitted a Job that already had a JobId.");
		JobId id=getNewJobId(job);
		jobs.put(id, job);
		job.percentDone=0.0;
		
		if(threaded) {
			queue.add(job);
			job.status=JobStatus.SUBMITTED;
		} else {// Run immediately rather then queue job
			try{ 
				job.setStatus(JobStatus.ACTIVE);
				job.execute();
				job.setStatus(JobStatus.COMPLETED);
			} catch (Exception e){
				e.printStackTrace();
				updateJob(job.getId(), JobStatus.ERROR);
			}
		}
		
		return id;
	}
	
	/*
	 * Clears old Jobs
	 * When Jobs are completed and the WebApp no longer needs to track them they should be cleared.
	 * Otherwise, these will stick around until the system is restarted.	 * 
	 */
	public synchronized void clearJob(JobId id){
		if (jobs.containsKey(id)){
			jobs.remove(id);
		}
	}
	
	public synchronized void clearJob(Job job){
		if (jobs.containsKey(job.getId())){
			jobs.remove(job.getId());
		}
	}
	
	public synchronized Job getJob(JobId id) throws JobException{
		if(jobs.containsKey(id)){
			return jobs.get(id);
		}
		throw new JobException("No such Job");
	}
	
	public synchronized JobStatus getJobStatus(JobId id) throws JobException{
		if(jobs.containsKey(id)){
			return jobs.get(id).getStatus();
		}
		throw new JobException("No such Job");
	}
	
	public synchronized void observeJob(JobId id, JobObserver observer) throws JobException{
		if(jobs.containsKey(id)){
			jobs.get(id).observe(observer);
		} else {
			throw new JobException("No such Job");
		}
	}
	
	protected synchronized void updateJob(JobId id, JobStatus status){
		if(jobs.containsKey(id)){
			jobs.get(id).setStatus(status);
		}
	}
	
	protected synchronized void updateJob(JobId id, double percentDone){
		if(jobs.containsKey(id)){
			jobs.get(id).setPercentDone(percentDone);
		}
	}
	
	
	protected synchronized boolean jobAvailible(){
		return !queue.isEmpty();
	}
	
	protected synchronized Job getNextJob(){
		Job job=null;
		if(jobAvailible()){// potential race condition with multiple threads
			job=queue.poll();
		} else {
			job=null;
		}
		return job;
	}
	
	/*
	 * Don't use this unless you have to... but it seemed useful to have a blocking technique
	 */
	public Job waitFor(JobId id) throws JobException, InterruptedException{
		while(true){
			if (!jobs.containsKey(id)) throw new JobException("No such JobId");
			Job j=jobs.get(id);
			if (j.getStatus().equals(JobStatus.COMPLETED) || j.getStatus().equals(JobStatus.ERROR) ){
				clearJob(id);
				return j;
			}
			Thread.sleep(1000);
		}
	}
	
	protected class KillJob extends Job{
		@Override
		public void execute() {}
	}
	
    public void close() {
    	for(int i=0; i<threadCount; i++){
    		Job k=new KillJob();
    		try {
				submit(k);
			} catch (JobException e) {
				e.printStackTrace();
			}
    	}
    	if(threadPool!=null){
	    	threadPool.shutdown();
	        try {
	        	threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
    	}
    }
}
