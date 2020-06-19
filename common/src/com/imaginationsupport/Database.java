package com.imaginationsupport;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.logging.Level;

import static com.mongodb.client.model.Filters.eq;

public class Database
{
	private static final String COLLECTION_NAME_SCENARIO_EXPLORER_CONFIG = "ScenarioExplorerConfig";

	private static final String SCENARIO_EXPLORER_CONFIG_KEY = "key";
	private static final String SCENARIO_EXPLORER_CONFIG_VALUE = "value";
	private static final String SCENARIO_EXPLORER_CONFIG_KEY_SCHEMA_REVISION = "SchemaRevision";

	/**
	 * Singleton
	 */
	private static Database instance = null;

	/**
	 * Holds the Morphia datastore
	 */
	Datastore datastore;

	/**
	 * Holds the MongoDB database instance
	 */
	private MongoDatabase db;

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getBackendLogger();

	public static Database getInstance()
	{
		if( instance == null )
		{
			try
			{
				LOGGER.trace( String.format( "Mongo hostname: %s", ImaginationSupportConfig.getMongoDbHostname() ) );
				LOGGER.trace( String.format( "Mongo port:     %d", ImaginationSupportConfig.getMongoDbPort() ) );
				LOGGER.trace( String.format( "Mongo database: %s", ImaginationSupportConfig.getMongoDbDatabase() ) );
				LOGGER.trace( String.format( "Mongo username: %s", ImaginationSupportConfig.getMongoDbUsername() ) );
				LOGGER.trace( String.format( "Mongo password: %s", ImaginationSupportConfig.getMongoDbPassword() ) );

				instance = new Database(
					ImaginationSupportConfig.getMongoDbHostname(),
					ImaginationSupportConfig.getMongoDbPort(),
					ImaginationSupportConfig.getMongoDbDatabase(),
					ImaginationSupportConfig.getMongoDbUsername(),
					ImaginationSupportConfig.getMongoDbPassword()
				);
			}
			catch( final GeneralScenarioExplorerException e )
			{
				LOGGER.error( "Error initializing the instance!", e );
			}
		}

		return instance;
	}

	protected Database( final String hostname, final int port, final String databaseName, final String username, final String password )
	{
		java.util.logging.Logger.getLogger( "org.mongodb.driver" ).setLevel( Level.WARNING );
		final MongoCredential credential = MongoCredential.createCredential( username, "admin", password.toCharArray() );
		final MongoClient client = new MongoClient( new ServerAddress( hostname, port ), credential, MongoClientOptions.builder().build() );
		db = client.getDatabase( databaseName );

		// run any database upgrades needed
		initDatabase();

		// initialize morphia
		final Morphia morphia = new Morphia();
		morphia.mapPackage( "com.imaginationsupport" );

		datastore = morphia.createDatastore( client, databaseName );
		datastore.ensureIndexes();

		return;
	}

	public static void save( Object o )
	{
		Database.getInstance().datastore.save( o );
	}

//	public static JSONObject toJSON( Object o )
//	{
//		return new JSONObject(  Database.getInstance().morphia.toDBObject( o )  );
//	}

	public static < T > T get( Class< T > c, ObjectId id )
	{
		return Database.getInstance().datastore.get( c, id );
	}

	public static < T > WriteResult delete( Class< T > c, ObjectId id )
	{
		return Database.getInstance().datastore.delete( c, id );
	}

//	public static void dropCollection( String collectionName )
//	{
//		Database.getInstance().db.getCollection( collectionName ).drop();
//	}

	public static void dropDatabase()
	{
		Database.getInstance().db.drop();
	}

	private void initDatabase()
	{
		final int schemaRevision = getSchemaRevision();
		LOGGER.info( String.format( "Database schema revision: %d", schemaRevision ) );

		if( schemaRevision < 1 )
		{
			updateSchemaToRevision1();
			LOGGER.info( String.format( ">>> updated database revision reported: %d", getSchemaRevision() ) );
		}

		return;
	}

	@SuppressWarnings( "WeakerAccess" )
	public int getSchemaRevision()
	{
		final String raw = getSystemConfigProperty( SCENARIO_EXPLORER_CONFIG_KEY_SCHEMA_REVISION );
		if( raw == null || raw.trim().isEmpty() )
		{
			return 0;
		}

		return Integer.parseInt( raw );
	}

	@SuppressWarnings( "SameParameterValue" )
	private String getSystemConfigProperty( final String key )
	{
		final MongoCollection scenarioExplorerConfig = db.getCollection( COLLECTION_NAME_SCENARIO_EXPLORER_CONFIG );

		final Document raw = (Document)scenarioExplorerConfig.find( eq( SCENARIO_EXPLORER_CONFIG_KEY, key ) ).first();
		if( raw == null )
		{
			return null;
		}

		return raw.get( SCENARIO_EXPLORER_CONFIG_VALUE ).toString();
	}

	@SuppressWarnings( "SameParameterValue" )
	private void setSystemConfigProperty( final String key, final String value )
	{
		final MongoCollection scenarioExplorerConfig = db.getCollection( COLLECTION_NAME_SCENARIO_EXPLORER_CONFIG );

		final Bson filter = eq( SCENARIO_EXPLORER_CONFIG_KEY, key );
		final Object raw = scenarioExplorerConfig.find( filter ).first();

		if( raw == null )
		{
			//noinspection unchecked
			scenarioExplorerConfig.insertOne( new Document( SCENARIO_EXPLORER_CONFIG_KEY, key ).append( SCENARIO_EXPLORER_CONFIG_VALUE, value ) );
		}
		else
		{
			scenarioExplorerConfig.findOneAndUpdate( filter, new Document( "$set", new Document( SCENARIO_EXPLORER_CONFIG_VALUE, value ) ) );
		}

		return;
	}

	/**
	 * Updates the schema to version 1
	 *
	 * Changes:
	 * 	- add the new "ScenarioExplorerConfig" collection (created automatically when first accessed)
	 * 	- add the new config entry SchemaRevision to hold the current schema revision
	 */
	private void updateSchemaToRevision1()
	{
		LOGGER.info( "Upgrading to schema revision 1..." );

		setSystemConfigProperty( SCENARIO_EXPLORER_CONFIG_KEY_SCHEMA_REVISION, "1" );

		LOGGER.info( "Schema revision upgrade complete." );

		return;
	}
}
