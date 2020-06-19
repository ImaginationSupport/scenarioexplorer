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
import com.imaginationsupport.plugins.features.IntegerFeature;
import com.imaginationsupport.plugins.features.ProbabilityFeature;
import com.imaginationsupport.plugins.preconditions.TimelineEventPrecondition;
import com.imaginationsupport.plugins.projectors.CompoundingRate;
import com.imaginationsupport.plugins.projectors.RandomProjector;
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
public class DebugProjectors {

	private static final String TESTPROJECTNAME="Projector Test";

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
	private static FeatureMap d=null;

	private static TimelineEvent tle=null;
	private static ConditioningEvent ce=null;
	private static FBView fbView=null;

	public static void main(String[] args) {
		DebugProjectors t=new DebugProjectors();
//		DebugProject.setup();
		t.A_createProject();

		DebugProjectors.teardown();
	}

	public DebugProjectors() {}
	
	@Test
	public void A_createProject() {
		try {
			project = api.createProject(DebugProjectors.TESTPROJECTNAME+"(Projector)", "This is a test",
					LocalDateTime.now(), LocalDateTime.now().plusDays(500), 30, user);
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
		assertNotNull("Failed to Create Project",project);
		assertNotNull("New Project does not have Master View",project.getMasterView());
	}
	
	@Test
	public void B_addFeatures_variety(){
		try {
			a=api.addFeature(project.getId(), IntegerFeature.class.getCanonicalName(),"{\"min\": 0,\"max\": 100,\"defaultValue\": \"1\"}",
					"a", "times 2",  CompoundingRate.class.getCanonicalName(),"{\"multiplier\": 2,\"timespan\": 100 }");
			b=api.addFeature(project.getId(), DecimalFeature.class.getCanonicalName(),"{\"min\": 0,\"max\": 100,\"defaultValue\": \"1.0\", \"numDecimalPlaces\":2}",
					"b", "increasing",  CompoundingRate.class.getCanonicalName(),"{\"multiplier\": 1.1,\"timespan\": 10 }");
			c=api.addFeature(project.getId(), ProbabilityFeature.class.getCanonicalName(),"{\"defaultValue\": \"0.5\"}",
					"c", "a random number", RandomProjector.class.getCanonicalName(),null);
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
		ce=new ConditioningEvent(project.getId(), fbView.getId(), "CE for "+tle.getLabel(), "Timeline Event Triggeded");
		ce.addPrecondition(new TimelineEventPrecondition(TemporalRelationship.DURING, tle.getId()));
		try{
			{
				Outcome o=new Outcome("a","", 0.0);
				Effect e=new FeatureSetEffect(a, "40");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			{
				Outcome o=new Outcome("b","", 0.0);
				Effect e=new FeatureSetEffect(b, "50");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			{
				Outcome o=new Outcome("c","", 0.0);
				Effect e=new FeatureSetEffect(c, ".5");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			ce=api.createConditioningEvent(project.getId(), fbView.getId(), ce);
			api.assignConditioningEvent(project.getId(), fbView.getId(), ce.getId());
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
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
			} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			 	fail("Exception Caught: "+e);
			}
		}
		return null;
	}
	
}
