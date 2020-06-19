package com.imaginationsupport.backend.jobs;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.imaginationsupport.Database;
import com.imaginationsupport.backend.Job;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.State;
import com.imaginationsupport.views.MasterView;

public class RebuildProject extends Job {

	private ObjectId projectId=null;
	
	public RebuildProject(ObjectId projectId) {
		this.projectId=projectId;
	}
	
	private Project project=null;
	private List<State> oldStates=new ArrayList<>();
	
	@Override
	public void execute() {
		if (project==null) {
			project=Database.get(Project.class, projectId);
		}
		
		MasterView mv=Database.get(MasterView.class, project.getMasterView());
		State now= project.getNow();
	}
}
