package com.imaginationsupport;

import java.util.SortedSet;
import java.util.TreeSet;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.*;

public class UserManager {
	
	/*
	 * Singleton
	 * User Manager is a singleton class
	 */
	private static UserManager instance=null;
	
	protected UserManager(){
	}
	
	public static UserManager getInstance(){
		if(instance==null){
			instance=new UserManager();
		}
		return instance;
	}
	
	private Database db=Database.getInstance();
	
	public User createUser(String userName, String fullName, boolean isSiteAdmin) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( userName == null )
		{
			throw new InvalidDataException( "Username cannot be null or empty!" );
		}

		if( fullName == null )
		{
			throw new InvalidDataException( "Full name cannot be null!" );
		}

		User user = new User( userName, fullName, isSiteAdmin );

		return addUser( user );
	}
	
	/**
	 * Add User
	 * @param user
	 */
	public User addUser(User user) throws InvalidDataException, GeneralScenarioExplorerException
	{
		if( user == null )
		{
			throw new InvalidDataException( "User cannot be null!" );
		}

		if( exists( user.getUserName() ) )
		{
			throw new InvalidDataException( "Username must be unique!" );
		}

		db.datastore.save(user);

		return user;
	}
	
	/**
	 * Remove User
	 * @param user
	 * @throws GeneralScenarioExplorerException
	 */
	public void removeUser(User user) throws GeneralScenarioExplorerException
	{
		if(user==null) throw new GeneralScenarioExplorerException("ERROR: cannot remove a null user.");
		if(!exists(user.getUserName())) throw new GeneralScenarioExplorerException("ERROR: removing a non-existant user.");
		db.datastore.delete(user);
	}

	/**
	 * Check is a username exists
	 * @param username
	 * @return
	 */
	public boolean exists(String username) throws GeneralScenarioExplorerException
	{
		if(username==null) throw new GeneralScenarioExplorerException("ERROR: exists passed a null user.");
		Query<User> q=db.datastore.createQuery(User.class);
		q.criteria("userName").equal(username);
		User u=q.get(); // TODO: should be able to check this without loading it up or cache it..
		if (u==null) return false;
		else return true;
	}

	/**
	 * Get User by username
	 * @param username
	 * @return User or null if does not exist
	 */
	public User getUser(String username) {
		User user=db.datastore.find(User.class).field("userName").equal(username).get();
		return user;
	}
	
	/**
	 * Get a User by the DB id
	 * @param id
	 * @return User or null if does not exist
	 */
	public User getUser( ObjectId id ) throws GeneralScenarioExplorerException
	{
		if( id == null )
		{
			throw new GeneralScenarioExplorerException( "ERROR: get user by id passed a null id." );
		}
		return db.datastore.get( User.class, id );
	}

	/**
	 * Get all Users
	 *
	 * @return Set of all Users
	 */
	public SortedSet< User > getUsers()
	{
		return new TreeSet<>( db.datastore.createQuery( User.class ).asList() );
	}
	
	/**
	 * Add access to project
	 * @param user
	 * @param projectId
	 */
	public void addAccess(User user, ObjectId projectId) throws GeneralScenarioExplorerException
	{
		if( user == null )
		{
			throw new GeneralScenarioExplorerException( "Null user passed to addAccess." );
		}
		if( !ProjectManager.getInstance().exists( projectId ) )
		{
			throw new GeneralScenarioExplorerException( "No valid Project found with id (" + projectId.toHexString() + ")" );
		}
		user.addAccess( projectId );
		db.datastore.save( user );
	}

}
