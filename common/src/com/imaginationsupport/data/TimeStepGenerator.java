package com.imaginationsupport.data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;

import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;

public class TimeStepGenerator
{
	private HashSet<LocalDateTime> times= new HashSet<>();
	
	public TimeStepGenerator(){
	}

	public void addTime(LocalDateTime date, int days){
		if(times.contains(date)) return;
		for(LocalDateTime t: times){
			if(Duration.between(t, date).toDays()<days) return;
		}
		times.add(date);
	}
	
	public void addFullTimeline(ObjectId projectId){
		try {
			for(TimelineEvent e: ProjectManager.getInstance().getTimelineEvents(projectId)){
				addTimelineEvent(e);
			}
		} catch ( InvalidDataException e) {
			e.printStackTrace();
		}
	}
	
	public void addTimelineEvent(TimelineEvent e){
			addTime(e.getStart(),0); // start
			Duration d= Duration.between(e.getStart(),e.getEnd());
			d=d.dividedBy(2);
			addTime(e.getStart().plus(d),0); // middle
			addTime(e.getEnd(),0); // end
	}

	public void addIncrements(LocalDateTime start, LocalDateTime end, int days){
		LocalDateTime current=start.plusDays(days);
		while (current.isBefore(end)){
			addTime(current, days/3);
			current=current.plusDays(days);
		}
	}
	
	public List<LocalDateTime> getTimeSteps(){
		List<LocalDateTime> out = new ArrayList<LocalDateTime>(times.size());
		out.addAll(times);
		Collections.sort(out);
		return out;
	}
}
