package com.imaginationsupport.data;

import com.imaginationsupport.API;
import com.imaginationsupport.Database;
import com.imaginationsupport.ImaginationSupportConfig;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import java.util.Date;

public abstract class ImaginationSupportTestCaseBase
{
	protected static final Logger LOGGER = ImaginationSupportUtil.getUnitTestsLogger();

	protected static final String JUNIT_DATABASE_TESTING_ADMIN_USER_NAME = "admin-user";
	protected static final String JUNIT_DATABASE_TESTING_NORMAL_USER_NAME = "normal-user";

	private int m_NextObjectIdCounter = 0;

	protected ObjectId getNextObjectId()
	{
		++m_NextObjectIdCounter;

		return new ObjectId( new Date(), m_NextObjectIdCounter );
	}

	protected void showMethodHeader()
	{
		final StackTraceElement caller = Thread.currentThread().getStackTrace()[ 2 ];

		LOGGER.info( "" );
		LOGGER.info( String.format( "=========================[ %s: %s ]=========================", caller.getClassName(), caller.getMethodName() ) );
		LOGGER.info( "" );

		return;
	}

	protected static void dropDatabase() throws GeneralScenarioExplorerException
	{
		final MongoCredential credential = MongoCredential.createCredential(
			ImaginationSupportConfig.getMongoDbUsername(),
			"admin",
			ImaginationSupportConfig.getMongoDbPassword().toCharArray() );
		final MongoClient mongoClient = new MongoClient(
			new ServerAddress( ImaginationSupportConfig.getMongoDbHostname(), ImaginationSupportConfig.getMongoDbPort() ),
			credential,
			MongoClientOptions.builder().build() );
		mongoClient.dropDatabase( ImaginationSupportConfig.getMongoDbDatabase() );

		return;
	}

	protected static void initDatabase() throws InvalidDataException, GeneralScenarioExplorerException
	{
		Database.getInstance();

		final API api = new API();
		api.createUser( JUNIT_DATABASE_TESTING_ADMIN_USER_NAME, "Test Admin User", true );
		api.createUser( JUNIT_DATABASE_TESTING_NORMAL_USER_NAME, "Test Normal User", false );

		return;
	}

	protected static void cleanDatabase() throws InvalidDataException, GeneralScenarioExplorerException
	{
		dropDatabase();

		initDatabase();

		return;
	}
}
