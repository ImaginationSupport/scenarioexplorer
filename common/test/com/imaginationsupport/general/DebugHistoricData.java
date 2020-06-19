package com.imaginationsupport.general;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.junit.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;

import com.imaginationsupport.API;
import com.imaginationsupport.Database;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.User;
import com.imaginationsupport.plugins.features.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DebugHistoricData
{
	
	
	public static API api = null;	
	private static Project project=null;


	public static void main(String[] args) {
		DebugHistoricData test=new DebugHistoricData();
		test.preTest();
		test.A_createProject();
		test.B_addFeatures();
		test.C_exportfile();
		test.D_importfile();
		test.Z_removeProject();
		test.postTest();
	}
	
	public DebugHistoricData() {}

	@Test
	public void A_createProject() {	
		Project p=null;
		try {
			User user = api.createUser( "myuser", "sample user", true );
			p = api.createProject("Historic Data Tests", "This is a test", LocalDateTime.now(), LocalDateTime.now().plusMonths(12), 30, user);
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Exception Caught: "+e);
		}
		assertNotNull("Failed to Create Project",p);
		assertNotNull("New Project does not have Master View",p.getMasterView());
		project=p;
	}
	
	
	@Test
	public void B_addFeatures(){
		try {
			api.addFeature(project.getId(), TextFeature.class.getCanonicalName(),"" , "a_text", "this is just text", null,"");
			api.addFeature(project.getId(), IntegerFeature.class.getCanonicalName(),"" , "b_int", "this is just int", null,"");
			api.addFeature(project.getId(), DecimalFeature.class.getCanonicalName(),"" , "c_decimal", "this is just double", null,"");
			api.addFeature(project.getId(), BooleanFeature.class.getCanonicalName(),"" , "d_boolean", "this is just boolean", null,"");
		} catch ( InvalidDataException | GeneralScenarioExplorerException e) {
			fail("Caught Exception: "+e);
		}
	}
	
	@Test
	public void C_exportfile(){
		try {
			api.getHistoricDataFile(project.getId(), "./exported_historic_data_test.csv");
		} catch ( GeneralScenarioExplorerException | InvalidDataException e) {
			fail("Caught Exception: "+e);
		}
	}
	
	@Test
	public void D_importfile(){
		try {
			api.setHistoricDataFile(project.getId(), "./exported_historic_data_test.csv");
		} catch ( InvalidDataException e) {
			fail("Caught Exception: "+e);
		}
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
	}
	
	@AfterClass
	public static void teardown() {
		api.close();
		//Database.dropDatabase();
	}
	
}
