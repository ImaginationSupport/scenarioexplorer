package com.imaginationsupport.general;

import com.imaginationsupport.API;
import com.imaginationsupport.Database;
import com.imaginationsupport.ProjectManager;
import com.imaginationsupport.UserManager;
import com.imaginationsupport.data.*;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Effect;
import com.imaginationsupport.plugins.effects.FeatureSetEffect;
import com.imaginationsupport.plugins.features.IntegerFeature;
import com.imaginationsupport.plugins.preconditions.TimelineEventPrecondition;
import com.imaginationsupport.plugins.projectors.JavaScriptProjector;
import com.imaginationsupport.plugins.projectors.RandomProjector;
import com.imaginationsupport.views.FBView;
import com.imaginationsupport.views.View;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DebugScriptProjector
{

	private static final String TESTPROJECTNAME="Test Function Projector";

	private Random rand=new Random();

	public static API api = null;
	private static Database dm=null;
	private static ProjectManager pm=ProjectManager.getInstance();

	private static User user = null;
	private static User other=null;

	private static Project projectA=null;
	private static List<FeatureMap> features=new ArrayList<FeatureMap>();
	private static FeatureMap a=null;
	private static FeatureMap b=null;
	private static FeatureMap c=null;
	private static FeatureMap d=null;

	private static TimelineEvent tle=null;
	private static ConditioningEvent ce=null;

	private static View view=null;

	public static void main(String[] args) {
		DebugScriptProjector t=new DebugScriptProjector();
//		DebugProject.setup();



		t.Z_removeProject();
		DebugScriptProjector.teardown();
	}

	public DebugScriptProjector() {}
		
	@Test
	public void A_createProject() {
		try {
			projectA = api.createProject(DebugScriptProjector.TESTPROJECTNAME, "This is a test", LocalDateTime.now(), LocalDateTime.now().plusMonths(12), 30, user);
		} catch (GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Exception Caught: " + e);
		}
		assertNotNull("Failed to Create Project", projectA);
		assertNotNull("New Project does not have Master View", projectA.getMasterView());
	}

	@Test
	public void B_createTimelineEvent() {
		LocalDateTime t_start=projectA.getStart().plusDays(30);
		LocalDateTime t_end=t_start.plusDays(30);
		tle=new TimelineEvent(projectA.getId(),"Time Period A","30 day interval 30 days from start.", t_start, t_end, "URL HERE");
		try {
			tle=api.createTimelineEvent(projectA.getId(),tle);
		} catch (InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
		assertNotNull("Timeline Event does not have db id.", tle.getId());
	}

	
	@Test
	public void C_addFeatures(){
		FeatureMap a=null;
		try {
			a=api.addFeature(projectA.getId(), IntegerFeature.class.getCanonicalName(),"{\"min\": 0,\"max\": 1000,\"defaultValue\": \"1\"}",
					"a", "variable a", RandomProjector.class.getCanonicalName(),null);
			features.add(a);
		} catch (GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Caught Exception: "+e);
		}

		FeatureMap b=null;
		try {
			b=api.addFeature(projectA.getId(), IntegerFeature.class.getCanonicalName(),"{\"min\": 0,\"max\": 1000,\"defaultValue\": \"2\"}",
					"b", "variable b", RandomProjector.class.getCanonicalName(),null);
			features.add(b);
		} catch (GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Caught Exception: "+e);
		}

		FeatureMap c=null;
		try {
			c=api.addFeature(projectA.getId(), IntegerFeature.class.getCanonicalName(),"{\"min\": 0,\"max\": 1000,\"defaultValue\": \"3\"}",
					"c", "function should be c=a+b", JavaScriptProjector.class.getCanonicalName(),"{\"script\": \"currentA+currentB\"}");
			features.add(c);
		} catch (GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Caught Exception: "+e);
		}

		FeatureMap d=null;
		try {
			d=api.addFeature(projectA.getId(), IntegerFeature.class.getCanonicalName(),"{\"min\": 0,\"max\": 1000,\"defaultValue\": \"3\"}",
					"d", "set by scripted effect", null,null);
			features.add(c);
		} catch (GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Caught Exception: "+e);
		}
		
		Project project2=Database.get(Project.class, projectA.getId());
		Collection<FeatureMap> maps=project2.getFeatureMaps();
		if(maps.size()!=features.size()) fail ("Incorrect number of features (expected "+features.size()+" but got "+ maps.size()+").");
	}
	

	@Test
	public void D_addView() {
		
		try {
			view= api.createView(projectA.getId(),new FBView(projectA.getId(),"Test View #1", "Future home of a futures building view"));
		} catch (InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void E_addConditioningEvent_fromPrimatives() {
			ConditioningEvent ce=new ConditioningEvent(projectA.getId(), view.getId(), "CE for "+tle.getLabel(), "sets feature d from script");
			ce.addPrecondition(new TimelineEventPrecondition(TemporalRelationship.DURING, tle.getId()));
			try{
				{
					Outcome o=new Outcome("Good","", 0.33);
					Effect e=new FeatureSetEffect(a, "10");
					o.addEffect(e);
					ce.addOutcome(o);
				}
				{
					Outcome o=new Outcome("Bad","", 0.33);
					Effect e=new FeatureSetEffect(a, "20");
					o.addEffect(e);
					ce.addOutcome(o);
				}
				{
					Outcome o=new Outcome("Ugly","", 0.33);
					Effect e=new FeatureSetEffect(a, "30");
					o.addEffect(e);
					ce.addOutcome(o);
				}			
				ce=api.createConditioningEvent(projectA.getId(), view.getId(), ce);
			} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
				fail("Exception Caught: "+e);
			}
	}
	

	

	
	@Test
	public void Z_removeProject() {
		try {
			api.deleteProject(projectA.getId());
		} catch (InvalidDataException e) {
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
		} catch (GeneralScenarioExplorerException e) {
			e.printStackTrace();
		}
	}
	
	@AfterClass
	public static void teardown() {
		api.close();
		//Database.dropDatabase();
	}

	public static User getDefaultUser() throws GeneralScenarioExplorerException{
		final String TESTUSERNAME ="TestUser";
		final User existingUser = UserManager.getInstance().getUser(TESTUSERNAME);
		if( existingUser != null ) {
			return existingUser;
		} else {
			try {
				User temp=new User(TESTUSERNAME, TESTUSERNAME+"'s Name", true);
				return api.createUser(temp);
			} catch (GeneralScenarioExplorerException | InvalidDataException e) {
			 	fail("Exception Caught: "+e);
			}
		}
		return null;
	}
	
}
