package com.imaginationsupport.general;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;
import org.junit.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.imaginationsupport.API;
import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.UserManager;
import com.imaginationsupport.backend.JobManager;
import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Outcome;
import com.imaginationsupport.data.Project;
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
import com.imaginationsupport.plugins.projectors.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DebugProject
{
	
	private static final String TESTPROJECTNAME="Junit_Test_Project";
	
	private Random rand=new Random();
	
	public static API api = null;
	private static Database dm=null;
	private static ProjectManager pm=ProjectManager.getInstance(); 
	private static JobManager jm=null;
	
	private static User user = null;
	private static User other=null;
	
	private static Project projectA=null;
	private static Project projectB=null;
	private static List<FeatureMap> features= new ArrayList<>();
	private static FeatureMap motd=null;
	
	private static List<TimelineEvent> timelineEvents= new ArrayList<>();
	private static View view=null;
	private static View viewB=null;
	private static List<View> views= new ArrayList<>();
	private static List<ConditioningEvent> conditioningEvents= new ArrayList<>();

	public static void main(String[] args) {	
		DebugProject t=new DebugProject();
//		DebugProject.setup();
		t.A_getAllProjects_empty();
		t.B_createProject_fromPrimatives();
		t.C_createProject_fromObject();
		t.D_getAllProjects_withProjects();
		
		
		t.Z_removeProject();
		DebugProject.teardown();
	}
	
	public DebugProject() {}
		
	@Test
	public void A_getAllProjects_empty(){
		 SortedSet< Project > response = api.getAllProjects();
		 if (!response.isEmpty())
			 fail("Projects found in [should be] empty database.");
	}

	
	@Test
	public void B_createProject_fromPrimatives() {	
		Project p=null;
		try {
			p = api.createProject(DebugProject.TESTPROJECTNAME+"(fromPrimatives)", "This is a test", LocalDateTime.now(), LocalDateTime.now().plusMonths(12), 30, user);
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
		assertNotNull("Failed to Create Project",p);
		assertNotNull("New Project does not have Master View",p.getMasterView());
		projectA=p;
	}
	
	
	@Test
	public void C_createProject_fromObject() {
		try {
			Project p = new Project(DebugProject.TESTPROJECTNAME+"(fromObject)","This is a test", user.getId(),
				LocalDateTime.now(), LocalDateTime.now().plusMonths(12),30);		assertNotNull("Failed to Generate New Random Project Object",p);

			Project n=null;
			n = api.createProject(p);
			assertNotNull("Failed to Create Project",n);
			assertNotNull("New Project does not have Master View",n.getMasterView());
			projectB=n;
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
	}
	
	@Test
	public void D_getAllProjects_withProjects(){
		 SortedSet< Project > response = api.getAllProjects();
		 if (response.size()!=2)
			 fail("Found incorrect number of Projects ("+response.size()+").");
	}
	
	@Test
	public void E_createUser_fromObject() {
		User u=null;
		try {
			User temp=new User("fred", "Fred's Name", false);
			u = api.createUser(temp);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			 fail("Exception Caught: "+e);
		}
		other=u;
	}
	
	@Test
	public void F_addAccess() {
		if (other==null) fail("Required user not created.");
		try {
			api.addAccess(other, projectA.getId());
		} catch ( GeneralScenarioExplorerException e) {
			 fail("Exception Caught: "+e);
		}
	}
	
	@Test
	public void G_addFeatures_variety(){
		FeatureMap things=null;
		try {
			things=api.addFeature(projectA.getId(), IntegerFeature.class.getCanonicalName(),"{\"min\": 60,\"max\": 275,\"defaultValue\": \"148\"}",
					"random", "a random number", RandomProjector.class.getCanonicalName(),null);
			features.add(things);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
		
		// Use class one... FeatureMap motd=null;
		try {
			motd=api.addFeature(projectA.getId(), TextFeature.class.getCanonicalName(),"{\"defaultValue\": \"This is my initial value.\"}", "motd", "this is just text", null,null);
			features.add(motd);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
		
		FeatureMap rain=null;
		try {
			rain=api.addFeature(projectA.getId(), ProbabilityFeature.class.getCanonicalName(), null, "rain", "probability of rain", RandomProjector.class.getCanonicalName(),null);
			features.add(rain);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
		
		Project project2=Database.get(Project.class, projectA.getId());
		Collection<FeatureMap> maps=project2.getFeatureMaps();
		if(maps.size()!=features.size()) fail ("Incorrect number of features (expected "+features.size()+" but got "+ maps.size()+").");
		
		
		// Adding a feature to Project B too
		FeatureMap otherMotd=null;
		try {
			otherMotd=api.addFeature(projectB.getId(), TextFeature.class.getCanonicalName(),"", "motd", "this is just text in project B", null,"");
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
		
//		final EnumerationFeature f = new EnumerationFeature();
//		f.addConfigChoice( "chocolate", "CHOC" );
//		f.addConfigChoice( "vanilla", "VAN" );
//		f.addConfigChoice( "strawberry", "STW" );
//		f.addConfigChoice( "chocolate chip", "CCHIP" );
//		f.addConfigChoice( "mint chocolate chip", "MCCHIP" );
//		project.mapFeature( f, "flavor", "Favorite Flavor", RandomProjector.class );
//
//		project.mapFeature(new DecimalFeature(), "length", "how many inches",RandomProjector.class);
//		project.save();
//		projectManager.rebuildAll(project.getId());
	}
	
	@Test
	public void H_addTimelineEvent_setOf6() {
		{
			LocalDateTime t_start=projectA.getStart().plusDays(30);
			LocalDateTime t_end=t_start.plusDays(30);
			TimelineEvent t=new TimelineEvent(projectA.getId(),"Time Period A","30 day interval 30 days from start.", t_start, t_end, "URL HERE");
			try {
				t=api.createTimelineEvent(projectA.getId(),t);
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
				fail("Caught Exception: "+e);
				e.printStackTrace();
			}	
			assertNotNull("Timeline Event does not have db id.", t.getId());
			timelineEvents.add(t);
		}
		{
			LocalDateTime t_start=projectA.getStart().plusDays(60);
			LocalDateTime t_end=t_start.plusDays(1);
			TimelineEvent t=new TimelineEvent(projectA.getId(),"Time Period B","1 day interval 60 days from start", t_start, t_end, "URL HERE");
			try {
				t=api.createTimelineEvent(projectA.getId(),t);
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
				fail("Caught Exception: "+e);
				e.printStackTrace();
			}
			assertNotNull("Timeline Event does not have db id.", t.getId());
			timelineEvents.add(t);
		}
		{
			LocalDateTime t_start=projectA.getStart().plusDays(120);
			LocalDateTime t_end=t_start.plusDays(60);
			TimelineEvent t=new TimelineEvent(projectA.getId(),"Time Period C","60 day interval 120 days from start.", t_start, t_end, "URL HERE");
			try {
				t=api.createTimelineEvent(projectA.getId(),t);
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
				fail("Caught Exception: "+e);
				e.printStackTrace();
			}
			assertNotNull("Timeline Event does not have db id.", t.getId());
			timelineEvents.add(t);
		}

		List<TimelineEvent> l=new ArrayList<TimelineEvent>();
		for (int i=1;i<=3;i++) {
			LocalDateTime t_start=projectA.getStart().plusDays(120+(30*i));
			LocalDateTime t_end=t_start.plusDays(30);
			TimelineEvent t=new TimelineEvent(projectA.getId(),"Time Period D"+i,"30 day interval at "+t_start.toString(), t_start, t_end, "URL HERE");
			l.add(t);
		}
		try {
			api.createTimelineEvents(projectA.getId(), l);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
		timelineEvents.addAll(l);
		
		// Adding timeline events to ProjectB
		for (int i=1;i<=3;i++) {
			LocalDateTime t_start=projectB.getStart().plusDays(120+(30*i));
			LocalDateTime t_end=t_start.plusDays(30);
			TimelineEvent t=new TimelineEvent(projectB.getId(),"Time Period D"+i,"30 day interval at "+t_start.toString(), t_start, t_end, "URL HERE");
			l.add(t);
			try {
				api.createTimelineEvent(projectB.getId(),t);
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
				fail("Caught Exception: "+e);
				e.printStackTrace();
			}
		}
		
		SortedSet<TimelineEvent> tles=null;
		try {
			tles = api.getTimelineEvents(projectA.getId());
		} catch ( InvalidDataException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
		if (tles==null || tles.size()!=timelineEvents.size())
			fail("Incorrect number of timeline events (expected "+timelineEvents.size()+", but got "+tles.size()+").");
	}
	
	@Test
	public void I_addView() {
		
		try {
			view= api.createView(projectA.getId(),new FBView(projectA.getId(),"Test View #1", "Future home of a futures building view"));
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
		views.add(view);

		try {
			views.add(api.createView(projectA.getId(),new FBView(projectA.getId(),"Test View #2", "Another Future home of a futures building view")));
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
		
		View v2=new FBView(projectB.getId(),"Test View for Project B", "Future home of a futures building view");
		try {
			viewB= api.createView(projectB.getId(),v2);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void J_addConditioningEvent_fromPrimatives() {
		for(TimelineEvent t: timelineEvents) {
			ConditioningEvent ce=new ConditioningEvent(projectA.getId(), view.getId(), "CE for "+t.getLabel(), "Timeline Event Triggeded");
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
				ce=api.createConditioningEvent(projectA.getId(), view.getId(), ce);
				for(View v: views) {
					api.assignConditioningEvent(projectA.getId(), v.getId(), ce.getId());
				}
			} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
				fail("Exception Caught: "+e);
			}
			conditioningEvents.add(ce);
		}
	}
	
	public void K_addConditioningEvent_fromObject() {
		for(TimelineEvent t: timelineEvents) {
			ConditioningEvent ce=new ConditioningEvent(projectB.getId(), view.getId(), "CE for "+t.getLabel(), "Timeline Event Triggeded");
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
				ce=api.createConditioningEvent(projectB.getId(), viewB.getId(), ce);
				for(View v: views) {
					api.assignConditioningEvent(projectB.getId(), v.getId(), ce.getId());
				}
			} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
				fail("Exception Caught: "+e);
			}
			conditioningEvents.add(ce);
		}

	}
	
	//@Test
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
			beforeStates=api.getStatesInView(projectA.getId(),view.getId()).size();
		} catch ( InvalidDataException e) {
			 fail("Exception Caught: "+e);
		}
		
		try {
			//TODO: determine if we should expose the "rebuildall" to the API
			pm.rebuildAll(projectA.getId());
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			 fail("Exception Caught: "+e);
		}
		
		View afterView=Database.get(View.class, view.getId());
		List<Trajectory> after=afterView.getTree().getTrajectories();
		Collections.sort(after);
		
		int afterStates=-1;
		try {
			afterStates=api.getStatesInView(projectA.getId(),view.getId()).size();
		} catch ( InvalidDataException e) {
			 fail("Exception Caught: "+e);
		}
		
		if (before.size() != after.size())
			fail("Rebuilding resulted in a different number of Trajectories: Before="+before.size()+" and After="+after.size()+".");
		
		if (beforeStates!= afterStates)
			fail("Rebuilding resulted in a different number of States: Before="+beforeStates+" and After="+afterStates+".");
		
		// TODO: only CE's need to match, but order of the trajectories will be based on when added...
//		for(int i=0; i<before.size(); i++) {
//			List<TreeNode> beforeNi=before.get(i).getNodes();
//			List<TreeNode> afterNi=after.get(i).getNodes();
//			boolean done=false;
//			if(beforeNi.size()!=afterNi.size())
//				fail
//		}
	}
	
	@Test
	public void Z_removeProject() {
		try {
			api.deleteProject(projectB.getId());
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
		try {
			user= getDefaultUser();
		} catch ( GeneralScenarioExplorerException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void teardown() {
		api.close();
		//Database.dropDatabase();
	}

	public static User getDefaultUser() throws GeneralScenarioExplorerException
	{
		final String TESTUSERNAME ="TestUser";
		final User existingUser = UserManager.getInstance().getUser(TESTUSERNAME);
		if( existingUser != null ) {
			return existingUser;
		} else {
			try {
				User temp=new User(TESTUSERNAME, TESTUSERNAME+"'s Name", true);
				return api.createUser(temp);
			} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			 	fail("Exception Caught: "+e);
			}
		}
		return null;
	}
	
}
