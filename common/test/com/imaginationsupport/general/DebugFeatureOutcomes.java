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
import com.imaginationsupport.plugins.features.TextFeature;
import com.imaginationsupport.plugins.preconditions.TimelineEventPrecondition;
import com.imaginationsupport.views.FBView;
import com.imaginationsupport.views.View;
import org.bson.types.ObjectId;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DebugFeatureOutcomes {

	private static final String TESTPROJECTNAME="FeatureOutcomes_Test_Project";

	private Random rand=new Random();

	public static API api = null;
	private static Database dm=null;
	private static ProjectManager pm=ProjectManager.getInstance();
	private static JobManager jm=null;

	private static User user = null;
	private static User other=null;

	private static Project project=null;
	private static List<FeatureMap> features= new ArrayList<>();
	private static FeatureMap a=null;
	private static FeatureMap b=null;
	private static FeatureMap c=null;
	private static TimelineEvent tle=null;
	private static ConditioningEvent ce=null;

	private static List<TimelineEvent> timelineEvents= new ArrayList<>();
	private static FBView fbView=null;

	public static void main(String[] args) {
		DebugFeatureOutcomes t=new DebugFeatureOutcomes();
//		DebugProject.setup();
		t.A_createProject();

		DebugFeatureOutcomes.teardown();
	}

	public DebugFeatureOutcomes() {}
	
	@Test
	public void A_createProject() {
		try {
			project = api.createProject(DebugFeatureOutcomes.TESTPROJECTNAME+"(SmartQuery)", "This is a test", LocalDateTime.now(), LocalDateTime.now().plusMonths(12), 30, user);
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
		assertNotNull("Failed to Create Project",project);
		assertNotNull("New Project does not have Master View",project.getMasterView());
	}
	
	@Test
	public void B_addFeatures_variety(){
		try {
			a=api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"", "A", "?", null,"");
		} catch ( InvalidDataException |GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
		try {
			b=api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"", "B", "?", null,"");
		} catch ( InvalidDataException |GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
		try {
			c=api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"", "C", "?", null,"");
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
	}
	
	@Test
	public void C_addTimelineEvent(){
		{
			LocalDateTime t_start = project.getStart().plusDays(30);
			LocalDateTime t_end = t_start.plusDays(30);
			tle = new TimelineEvent(project.getId(), "Time Period", "30 day interval 30 days from start.", t_start, t_end, "URL HERE");
			try {
				tle = api.createTimelineEvent(project.getId(), tle);
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
				fail("Caught Exception: " + e);
				e.printStackTrace();
			}
			assertNotNull("Timeline Event does not have db id.", tle.getId());
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
				Outcome o=new Outcome("A","", 0.0);
				Effect e=new FeatureSetEffect(a, "A is set!");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			{
				Outcome o=new Outcome("B","", 0.0);
				Effect e=new FeatureSetEffect(b, "B is set!");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			{
				Outcome o=new Outcome("C","", 0.0);
				Effect e=new FeatureSetEffect(c, "C is set!");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			ce=api.createConditioningEvent(project.getId(), fbView.getId(), ce);
			api.assignConditioningEvent(project.getId(), fbView.getId(), ce.getId());
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
	}

	@Test
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
			} catch ( GeneralScenarioExplorerException e) {
			 	fail("Exception Caught: "+e);
			}
			catch( InvalidDataException e )
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
