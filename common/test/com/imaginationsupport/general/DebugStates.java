package com.imaginationsupport.general;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;

import com.imaginationsupport.API;
import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.UserManager;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;
import org.junit.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.imaginationsupport.backend.JobManager;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Outcome;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.State;
import com.imaginationsupport.data.TimelineEvent;
import com.imaginationsupport.data.User;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.data.tree.Trajectory;
import com.imaginationsupport.views.FBView;
import com.imaginationsupport.views.View;
import com.imaginationsupport.plugins.Effect;
import com.imaginationsupport.plugins.effects.FeatureSetEffect;
import com.imaginationsupport.plugins.features.*;
import com.imaginationsupport.data.TemporalRelationship;
import com.imaginationsupport.plugins.preconditions.TimelineEventPrecondition;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DebugStates
{
	
	private static final String TESTPROJECTNAME="Junit_Test_States";
	
	private Random rand=new Random();
	
	public static API api = null;
	private static Database dm=null;
	private static ProjectManager pm=ProjectManager.getInstance();
	private static JobManager jm=null;
	
	private static User user = null;
	private static User other=null;
	
	private static Project project=null;
	private static FeatureMap motd=null;
	private static FeatureMap motd2=null;

	
	private static List<TimelineEvent> timelineEvents= new ArrayList<>();
	private static View view=null;
	private static List<ConditioningEvent> conditioningEvents= new ArrayList<>();

	public static void main(String[] args) {	
		DebugStates t=new DebugStates();
		t.A_createProject();
		t.Z_removeProject();
		DebugStates.teardown();
	}
	
	public DebugStates() {}

	
	@Test
	public void A_createProject() {	
		Project p=null;
		try {
			p = api.createProject(DebugStates.TESTPROJECTNAME+"(fromPrimatives)", "This is a test", LocalDateTime.now(), LocalDateTime.now().plusMonths(12), 30, user);
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
		assertNotNull("Failed to Create Project",p);
		assertNotNull("New Project does not have Master View",p.getMasterView());
		project=p;
	}
	
	
	@Test
	public void B_addFeatures_variety(){
		try {
			motd=api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"{\"defaultValue\":\"def1\"}" , "motd", "this is just text", null,"");
			motd2=api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"{\"defaultValue\":\"def2\"}" , "motd2", "this is also just text", null,"");
			api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"{\"defaultValue\":\"def3\"}" , "motd3", "this is also just text", null,"");
			api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"{\"defaultValue\":\"def4\"}" , "motd4", "this is also just text", null,"");
			api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"{\"defaultValue\":\"def5\"}" , "motd5", "this is also just text", null,"");
			api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"{\"defaultValue\":\"def6\"}" , "motd6", "this is also just text", null,"");

		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
	}
	
	
	@Test
	public void H_addTimelineEvents() {
		// Adding timeline events to ProjectB
		for (int i=1;i<=1;i++) {
			LocalDateTime t_start=project.getStart().plusDays(120+(30*i));
			LocalDateTime t_end=t_start.plusDays(30);
			TimelineEvent t=new TimelineEvent(project.getId(),"Time Period D"+i,"30 day interval at "+t_start.toString(), t_start, t_end, "URL HERE");
			try {
				api.createTimelineEvent(project.getId(),t);
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
				fail("Caught Exception: "+e);
				e.printStackTrace();
			}
			timelineEvents.add(t);
		}
	}
	
	@Test
	public void I_addView() {
		try {
			view= api.createView(project.getId(),new FBView(project.getId(),"Test View #1", "Future home of a futures building view"));
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
	}
	
	@Test
	public void J_addConditioningEvents() {
		for(TimelineEvent t: timelineEvents) {
			ConditioningEvent ce=new ConditioningEvent(project.getId(), view.getId(), "CE for "+t.getLabel(), "Timeline Event Triggeded");
			ce.addPrecondition(new TimelineEventPrecondition(TemporalRelationship.DURING, t.getId()));
			try{
				{
					Outcome o=new Outcome("Good","", 0.0);
					Effect e=new FeatureSetEffect(motd, "Everything is cool!");
					o.addEffect(e);
					ce.addOutcome(o);
				}
				{
					Outcome o=new Outcome("Bad","", 0.0);
					Effect e=new FeatureSetEffect(motd, "This is the worst state ever!");
					o.addEffect(e);
					ce.addOutcome(o);
				}
				{
					Outcome o=new Outcome("Ugly","", 0.0);
					Effect e=new FeatureSetEffect(motd, "Everybody find cover!");
					o.addEffect(e);
					ce.addOutcome(o);
				}			
				ce=api.createConditioningEvent(project.getId(), view.getId(), ce);
			} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
				fail("Exception Caught: "+e);
			}
			conditioningEvents.add(ce);
		}
	}
	
	@Test
	public void K_countStates() {
		int count=-1;
		try {
			SortedSet<State> states= api.getStatesInView(project.getId(),view.getId());
			count=states.size();
			System.out.println("Produced "+count+" states.");
		} catch ( InvalidDataException e) {
			 fail("Exception Caught: "+e);
		}
	}
	
	
	@Test
	public void L_remove_feature(){
		try {
			api.deleteFeature(project.getId(), motd2.getUid());
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
	}
	
	@Test
	public void X_print() {
		try {
			ObjectId vid=view.getId();
			View v=Database.get(View.class, vid);
			System.out.println(v.toJSON().toString(4));
		} catch ( GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
	}
	
	@Test
	public void Y_RebuildProject() {
		
		View beforeView=Database.get(View.class, view.getId());
		List<Trajectory> before=beforeView.getTree().getTrajectories();
		Collections.sort(before);
		
		int beforeStates=-1;
		try {
			beforeStates=api.getStatesInView(project.getId(),view.getId()).size();
		} catch ( InvalidDataException e) {
			 fail("Exception Caught: "+e);
		}
		
		try {
			//TODO: determine if we should expose the "rebuildall" to the API
			pm.rebuildAll(project.getId());
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			 fail("Exception Caught: "+e);
		}
		
		View afterView=Database.get(View.class, view.getId());
		List<Trajectory> after=afterView.getTree().getTrajectories();
		Collections.sort(after);
		
		int afterStates=-1;
		try {
			afterStates=api.getStatesInView(project.getId(),view.getId()).size();
		} catch ( InvalidDataException e) {
			 fail("Exception Caught: "+e);
		}

		if (before.size() != after.size())
			fail("Rebuilding resulted in a different number of Trajectories: Before="+before.size()+" and After="+after.size()+".");
		
		if (beforeStates!= afterStates)
			fail("Rebuilding resulted in a different number of States: Before="+beforeStates+" and After="+afterStates+".");
		
	}
	
	//@Test
	public void Z_removeProject() {
		try {
			api.deleteProject(project.getId());
		} catch ( InvalidDataException e) {
			 fail("Exception Caught: "+e);
		}
	}
 
	@Before
	public void preTest() {
	}
	
	@After
	public void postTest() {
	}
	
	
	@BeforeClass
	public static void setup(){
		api=new API();
		Database.dropDatabase();
		user= getDefaultUser();

	}
	
	@AfterClass
	public static void teardown() {
		api.close();
		//Database.dropDatabase();
	}

	public static User getDefaultUser()  {
		final String TESTUSERNAME ="TestUser";
		final User existingUser = UserManager.getInstance().getUser(TESTUSERNAME);
		if( existingUser != null ) {
			return existingUser;
		} else {
			try {
				User temp=new User(TESTUSERNAME, TESTUSERNAME+"'s Name", true);
				return api.createUser(temp);
			} catch (InvalidDataException | GeneralScenarioExplorerException e) {
				fail("Exception Caught: "+e);
			}
		}
		return null;
	}

}
