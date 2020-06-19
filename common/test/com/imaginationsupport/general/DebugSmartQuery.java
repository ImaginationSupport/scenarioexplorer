package com.imaginationsupport.general;

import com.imaginationsupport.API;
import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.UserManager;
import com.imaginationsupport.backend.JobManager;
import com.imaginationsupport.data.*;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Effect;
import com.imaginationsupport.plugins.effects.FeatureSetEffect;
import com.imaginationsupport.plugins.features.DecimalFeature;
import com.imaginationsupport.plugins.features.TextFeature;
import com.imaginationsupport.plugins.preconditions.TimelineEventPrecondition;
import com.imaginationsupport.plugins.projectors.CompoundingRate;
import com.imaginationsupport.views.FBView;
import com.imaginationsupport.views.SQView;
import com.imaginationsupport.views.View;
import org.bson.types.ObjectId;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DebugSmartQuery {

	private static final String TESTPROJECTNAME="SmartQuery_Test_Project";

	private Random rand=new Random();

	public static API api = null;
	private static Database dm=null;
	private static ProjectManager pm=ProjectManager.getInstance();
	private static JobManager jm=null;

	private static User user = null;
	private static User other=null;

	private static Project project=null;
	private static List<FeatureMap> features= new ArrayList<>();
	private static FeatureMap motd=null;
	private static FeatureMap cost=null;
	private static TimelineEvent tle=null;
	private static TimelineEvent tleB=null;
	private static ConditioningEvent ce=null;
	private static ConditioningEvent ceB=null;


	private static List<TimelineEvent> timelineEvents= new ArrayList<>();
	private static FBView fbView=null;
	private static SQView sqView=null;

	public static void main(String[] args) {
		DebugSmartQuery t=new DebugSmartQuery();
//		DebugProject.setup();
		t.A_createProject();

		DebugSmartQuery.teardown();
	}

	public DebugSmartQuery() {}
	
	@Test
	public void A_createProject() {
		try {
			project = api.createProject(DebugSmartQuery.TESTPROJECTNAME+"(SmartQuery)", "This is a test", LocalDateTime.now(), LocalDateTime.now().plusMonths(12), 30, user);
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
		assertNotNull("Failed to Create Project",project);
		assertNotNull("New Project does not have Master View",project.getMasterView());
	}
	
	@Test
	public void B_addFeatures_variety(){
		try {
			motd=api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"", "motd", "this is just text in project B", null,"");
			cost=api.addFeature(project.getId(), DecimalFeature.class.getCanonicalName(),"{\"min\": 0,\"max\": 100,\"defaultValue\": \"1.0\"}",
					"cost", "increasing",  CompoundingRate.class.getCanonicalName(),"{\"multiplier\": 1.1,\"timespan\": 10 }");
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
	}
	
	@Test
	public void C_addTimelineEvent(){
		{
			LocalDateTime t_start = project.getStart().plusDays(30);
			LocalDateTime t_end = t_start.plusDays(30);
			tle = new TimelineEvent(project.getId(), "Time Period A", "30 day interval 30 days from start.", t_start, t_end, "URL HERE");
			try {
				tle = api.createTimelineEvent(project.getId(), tle);
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
				fail("Caught Exception: " + e);
				e.printStackTrace();
			}
			assertNotNull("Timeline Event does not have db id.", tle.getId());
		}
		{
			LocalDateTime t_start = project.getStart().plusDays(90);
			LocalDateTime t_end = t_start.plusDays(30);
			tleB = new TimelineEvent(project.getId(), "Time Period B", "90 day interval 30 days from start.", t_start, t_end, "URL HERE");
			try {
				tleB = api.createTimelineEvent(project.getId(), tleB);
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
				fail("Caught Exception: " + e);
				e.printStackTrace();
			}
			assertNotNull("Timeline Event does not have db id.", tleB.getId());
		}
	}
	
	@Test
	public void D_addFbView() {
		try {
			fbView= (FBView) api.createView(project.getId(),new FBView(project.getId(),"Test View #1", "Future home of a futures building view"));
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
	}
	
	@Test
	public void E_addConditioningEvents() {
		ce=new ConditioningEvent(project.getId(), fbView.getId(), "CE for "+tle.getLabel(), "Timeline Event Triggered");
		ce.addPrecondition(new TimelineEventPrecondition(TemporalRelationship.DURING, tle.getId()));
		try{
			{
				Outcome o=new Outcome("Good","", 0.33);
				Effect e=new FeatureSetEffect(motd, "All is tots chillax!");
				o.addEffect(e);
				Effect e2=new FeatureSetEffect(cost, "10");
				o.addEffect(e2);
				ce.addOutcome(o);
			}
			{
				Outcome o=new Outcome("Bad","", 0.33);
				Effect e=new FeatureSetEffect(motd, "...meh..");
				o.addEffect(e);
				Effect e2=new FeatureSetEffect(cost, "20");
				o.addEffect(e2);
				ce.addOutcome(o);
			}
			{
				Outcome o=new Outcome("Ugly","", 0.34);
				Effect e=new FeatureSetEffect(motd, "Panic Now!");
				o.addEffect(e);
				Effect e2=new FeatureSetEffect(cost, "30");
				o.addEffect(e2);
				ce.addOutcome(o);
			}
			ce=api.createConditioningEvent(project.getId(), fbView.getId(), ce);
			api.assignConditioningEvent(project.getId(), fbView.getId(), ce.getId());
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
	}

	@Test
	public void F_addConditioningEventsB() {
		ceB=new ConditioningEvent(project.getId(), fbView.getId(), "CE for "+tleB.getLabel(), "Timeline Event Triggered");
		ceB.addPrecondition(new TimelineEventPrecondition(TemporalRelationship.DURING, tleB.getId()));
		try{
			{
				Outcome o=new Outcome("Good","", 0.22);
				Effect e=new FeatureSetEffect(motd, "Yeah Man!");
				o.addEffect(e);
				Effect e2=new FeatureSetEffect(cost, "40");
				o.addEffect(e2);
				ceB.addOutcome(o);
			}
			{
				Outcome o=new Outcome("Bad","", 0.52);
				Effect e=new FeatureSetEffect(motd, "...still blah...");
				o.addEffect(e);
				Effect e2=new FeatureSetEffect(cost, "500");
				o.addEffect(e2);
				ceB.addOutcome(o);
			}
			{
				Outcome o=new Outcome("Ugly","", 0.12);
				Effect e=new FeatureSetEffect(motd, "ARGH!!!");
				o.addEffect(e);
				Effect e2=new FeatureSetEffect(cost, "2000");
				o.addEffect(e2);
				ceB.addOutcome(o);
			}
			ceB=api.createConditioningEvent(project.getId(), fbView.getId(), ceB);
			api.assignConditioningEvent(project.getId(), fbView.getId(), ceB.getId());
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
	}

	@Test
	public void G_SQaddView_Enumerated() {
		try {
			sqView=new SQView(project.getId(),"SQ View #1", "a test smart query",motd.getUid());
			sqView= (SQView) api.createView(project.getId(),sqView);

//			// TEMPORARY ...
//			// clear out the generated one and rewrite it so we have an example generated
//			sqView.setStateGroupings(new ArrayList<StateGroup>());
//			{
//				CNode c=new CNode(ce,0);
//				List<CNode> path=new ArrayList<CNode>();
//				path.add(c);
//				Indicator i=new Indicator(path,0.81,0.1);
//				StateGroup sg=new StateGroup("all good stuff", "this includes states where Motd is all tots chillax!");
//				sg.addIndicator(i);
//				sqView.addStateGroup(sg);
//			}
//			{
//				CNode c=new CNode(ce,1);
//				List<CNode> path=new ArrayList<CNode>();
//				path.add(c);
//				Indicator i=new Indicator(path,0.82,0.2);
//				StateGroup sg=new StateGroup("mostly harmless", "this includes states where Motd is ...meh...");
//				sg.addIndicator(i);
//				sqView.addStateGroup(sg);
//			}
//			{
//				CNode c=new CNode(ce,2);
//				List<CNode> path=new ArrayList<CNode>();
//				path.add(c);
//				Indicator i=new Indicator(path,0.83,0.3);
//				StateGroup sg=new StateGroup("crap crap crap", "this includes states where Motd is Panic Now!");
//				sg.addIndicator(i);
//				sqView.addStateGroup(sg);
//			}
//			sqView.save();
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
	}

	@Test
	public void H_SQaddView_Continuous() {
		try {
			sqView=new SQView(project.getId(),"SQ View #2", "a real smart query",cost.getUid());
			sqView= (SQView) api.createView(project.getId(),sqView);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
	}

	//@Test
	public void X_print() {
		try {
			ObjectId vid=fbView.getId();
			View v=Database.get(View.class, vid);
			System.out.println(v.toJSON().toString(4));
		} catch ( GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
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
		} catch ( InvalidDataException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void teardown() {
		api.close();
		//Database.dropDatabase();
	}

	public static User getDefaultUser() throws InvalidDataException
	{
		final String TESTUSERNAME ="TestUser";
		final User existingUser = UserManager.getInstance().getUser(TESTUSERNAME);
		if( existingUser != null ) {
			return existingUser;
		} else {
			User temp=new User(TESTUSERNAME, TESTUSERNAME+"'s Name", true);
			try {
				return api.createUser(temp);
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			 	fail("Exception Caught: "+e);
			}
		}
		return null;
	}
	
}
