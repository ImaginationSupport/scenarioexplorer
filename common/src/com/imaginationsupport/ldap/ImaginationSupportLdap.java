package com.imaginationsupport.ldap;

import com.imaginationsupport.ImaginationSupportConfig;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;

import java.util.Set;
import java.util.SortedSet;

public abstract class ImaginationSupportLdap
{
	/**
	 * Gets a list of users in the LDAP
	 *
	 * @return list of users in the LDAP
	 */
	public static SortedSet< LdapUser > listUsers() throws GeneralScenarioExplorerException
	{
		try( final LdapConnection ldap = getConnection() )
		{
			return ldap.listUsers();
		}
	}

	/**
	 * Finds the user with the given username
	 *
	 * @param username                the username
	 * @param throwExceptionIfMissing true to throw an exception if the username does not exist, false to return null
	 * @return the user, or null if not found and throwExceptionIfMissing is false
	 */
	public static LdapUser findUserByUsername( final String username, boolean throwExceptionIfMissing ) throws GeneralScenarioExplorerException
	{
		try( final LdapConnection ldap = getConnection() )
		{
			return ldap.findUserByUid( username, throwExceptionIfMissing );
		}
	}

	/**
	 * Finds the user with the given email
	 *
	 * @param username                the username
	 * @param throwExceptionIfMissing true to throw an exception if the username does not exist, false to return null
	 * @return the user, or null if not found and throwExceptionIfMissing is false
	 */
	public static LdapUser findUserByEmail( final String username, boolean throwExceptionIfMissing ) throws GeneralScenarioExplorerException
	{
		try( final LdapConnection ldap = getConnection() )
		{
			return ldap.findUserByMail( username, throwExceptionIfMissing );
		}
	}

	/**
	 * Checks if the given password is correct for the given username
	 *
	 * @param username                the username
	 * @param clearTextPasswordToTest the password to test
	 * @param availableFormats        the available password formats
	 * @return true if the password matches, otherwise false
	 */
	public static boolean checkUserPassword(
		final String username,
		final String clearTextPasswordToTest,
		final Set< LdapPasswordFormat > availableFormats ) throws GeneralScenarioExplorerException
	{
		try( final LdapConnection ldap = getConnection() )
		{
			return ldap.checkUserPassword( username, clearTextPasswordToTest, availableFormats );
		}
	}

	/**
	 * Adds the given user
	 *
	 * @param username          the username
	 * @param realName          the real name
	 * @param email             the email
	 * @param clearTextPassword the raw password (it will be SHA 512 encoded)
	 * @return the user created
	 */
	public static LdapUser addUser( final String username, final String realName, final String email, final String clearTextPassword ) throws GeneralScenarioExplorerException
	{
		try( final LdapConnection ldap = getConnection() )
		{
			return ldap.addUser( username, realName, email, clearTextPassword );
		}
	}

	/**
	 * Adds the given user
	 *
	 * @param user the user info
	 * @return the user created
	 */
	public static LdapUser addUser( final LdapUser user ) throws GeneralScenarioExplorerException
	{
		try( final LdapConnection ldap = getConnection() )
		{
			return ldap.addUser( user.getUID(), user.getDisplayName(), user.getMail(), user.getPassword() );
		}
	}

	/**
	 * Modifies the given user
	 *
	 * @param user the user to modify (note: the username cannot be changed)
	 */
	public static void modifyUser( final LdapUser user ) throws GeneralScenarioExplorerException
	{
		try( final LdapConnection ldap = getConnection() )
		{
			ldap.modifyUser( user );
		}
	}

	/**
	 * Deletes the given user
	 *
	 * @param username the username
	 */
	public static void deleteUser( final String username ) throws GeneralScenarioExplorerException
	{
		try( final LdapConnection ldap = getConnection() )
		{
			ldap.deleteUser( username );
		}
	}

	/**
	 * Modifies only the user password
	 *
	 * @param username             the username
	 * @param newClearTextPassword the new password
	 */
	public static void modifyUserPassword( final String username, final String newClearTextPassword ) throws GeneralScenarioExplorerException
	{
		try( final LdapConnection ldap = getConnection() )
		{
			ldap.modifyUserPassword( username, newClearTextPassword );
		}
	}

	/**
	 * Gets the connection to the LDAP server
	 *
	 * @return the connection
	 */
	private static LdapConnection getConnection() throws GeneralScenarioExplorerException
	{
		return new LdapConnection(
			ImaginationSupportConfig.getLdapHostname(),
			ImaginationSupportConfig.getLdapPort(),
			ImaginationSupportConfig.getLdapRootDN(),
			ImaginationSupportConfig.getLdapRootPassword(),
			ImaginationSupportConfig.getLdapBaseDN() );
	}
}
