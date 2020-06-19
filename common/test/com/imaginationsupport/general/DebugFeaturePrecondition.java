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
import com.imaginationsupport.plugins.features.TextFeature;
import com.imaginationsupport.plugins.preconditions.FeaturePrecondition;
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
public class DebugFeaturePrecondition
{

	private static final String TESTPROJECTNAME="FeaturePrecondition_Test_Project";

	private Random rand=new Random();

	public static API api = null;
	private static Database dm=null;
	private static ProjectManager pm=ProjectManager.getInstance();
	private static JobManager jm=null;

	private static User user = null;
	private static User other=null;

	private static Project project=null;
	private static List<FeatureMap> features= new ArrayList<>();
	private static FeatureMap textFeature=null;
	private static FeatureMap intFeature=null;
	private static FeatureMap decFeature=null;


	private static View fbView=null;
	private static View sqViewB=null;

	public static void main(String[] args) {
		DebugFeaturePrecondition t=new DebugFeaturePrecondition();
//		DebugProject.setup();
		t.A_createProject();

		DebugFeaturePrecondition.teardown();
	}

	public DebugFeaturePrecondition() {}
	
	@Test
	public void A_createProject() {
		try {
			project = api.createProject(DebugFeaturePrecondition.TESTPROJECTNAME+"(SmartQuery)", "This is a test", LocalDateTime.now(), LocalDateTime.now().plusMonths(12), 30, user);
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
		assertNotNull("Failed to Create Project",project);
		assertNotNull("New Project does not have Master View",project.getMasterView());
	}
	
	@Test
	public void B_addFeatures_variety(){
		try {
			textFeature=api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"", "textFeature", "textFeature Desc", null,"");
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}

		try {
			intFeature=api.addFeature(project.getId(), IntegerFeature.class.getCanonicalName(),"{\"min\": 0,\"max\": 100,\"defaultValue\": \"0\"}",
					"intFeature", "intFeature Desc", null,"");
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}

		// TODO: add compounding interest here and set a CE to a decimal feature to trigger on it...
		try {
			decFeature=api.addFeature(project.getId(), DecimalFeature.class.getCanonicalName(), null, "decFeature", "descFeature Desc", null,"");
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
	}

	@Test
	public void C_addView() {
		try {
			fbView= api.createView(project.getId(),new FBView(project.getId(),"FB View", "View to Build Tree"));
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
	}

	@Test
	public void D_addTimelineCE_A() {
		LocalDateTime t_start = project.getStart().plusDays(30);
		LocalDateTime t_end = t_start.plusDays(30);
		TimelineEvent t = new TimelineEvent(project.getId(), "Time Period A", "30 day interval 30 days from start.", t_start, t_end, "URL HERE");
		try {
			t = api.createTimelineEvent(project.getId(), t);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: " + e);
			e.printStackTrace();
		}
		assertNotNull("Timeline Event A does not have db id.", t.getId());

		ConditioningEvent ce=new ConditioningEvent(project.getId(), fbView.getId(), "CE for "+t.getLabel(), "Timeline A Event Triggered");
		ce.addPrecondition(new TimelineEventPrecondition(TemporalRelationship.DURING, t.getId()));
		try{
			{
				Outcome o=new Outcome("A","", 0.0);
				Effect e=new FeatureSetEffect(textFeature, "A");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			{
				Outcome o=new Outcome("B","", 0.0);
				Effect e=new FeatureSetEffect(textFeature, "B");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			{
				Outcome o=new Outcome("C","", 0.0);
				Effect e=new FeatureSetEffect(textFeature, "C");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			ce=api.createConditioningEvent(project.getId(), fbView.getId(), ce);
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
	}

	@Test
	public void E_addTimelineCE_B() {
		LocalDateTime t_start = project.getStart().plusDays(90);
		LocalDateTime t_end = t_start.plusDays(30);
		TimelineEvent t = new TimelineEvent(project.getId(), "Time Period B", "30 day interval 90 days from start.", t_start, t_end, "URL HERE");
		try {
			t = api.createTimelineEvent(project.getId(), t);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: " + e);
			e.printStackTrace();
		}
		assertNotNull("Timeline Event B does not have db id.", t.getId());

		ConditioningEvent ce=new ConditioningEvent(project.getId(), fbView.getId(), "CE for "+t.getLabel(), "Timeline B Event Triggered");
		//ce.addPrecondition(new TimelineEventPrecondition(TemporalRelationship.DURING, t.getId()));
		try {
			ce.addPrecondition(new FeaturePrecondition(textFeature,FeatureRelationship.EQ, "B"));
		} catch ( GeneralScenarioExplorerException e) {
			e.printStackTrace();
		}

		try{
			{
				Outcome o=new Outcome("1","", 0.0);
				Effect e=new FeatureSetEffect(intFeature, "100");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			{
				Outcome o=new Outcome("2", "", 0.0);
				Effect e=new FeatureSetEffect(intFeature, "200");
				o.addEffect(e);
				ce.addOutcome(o);
			}
			ce=api.createConditioningEvent(project.getId(), fbView.getId(), ce);
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: "+e);
		}
	}

	// TODO: Add CEs for feature values for each feature type

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
		user= getDefaultUser();
	}
	
	@AfterClass
	public static void teardown() {
		api.close();
		//Database.dropDatabase();
	}

	public static User getDefaultUser()
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
