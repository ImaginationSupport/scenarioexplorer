package com.imaginationsupport.ldap;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.LdapException;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.*;

@SuppressWarnings( "WeakerAccess" )
public class LdapConnection implements AutoCloseable
{
	public static final String ATTRIBUTE_UID = "uid";
	public static final String ATTRIBUTE_CN = "cn";
	public static final String ATTRIBUTE_SN = "sn";
	public static final String ATTRIBUTE_USER_PASSWORD = "userPassword";
	public static final String ATTRIBUTE_MAIL = "mail";
	public static final String ATTRIBUTE_DISPLAY_NAME = "displayName";

	private final LdapContext mContext;

	private final String mBaseDN;

	// reference: https://publib.boulder.ibm.com/tividd/td/IBMDS/guide322/en_US/HTML/Guide.html

	public LdapConnection( final String hostname, final int port, final String username, final String password, final String baseDN ) throws LdapException
	{
		if( baseDN == null || baseDN.trim().isEmpty() )
		{
			throw new LdapException( "BaseDN cannot be null or empty!" );
		}

		mBaseDN = baseDN;

		final Hashtable< String, String > env = new Hashtable<>();
		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );
		env.put( Context.PROVIDER_URL, String.format( "ldap://%s:%d", hostname, port ) );
		env.put( Context.SECURITY_AUTHENTICATION, "simple" );
		env.put( Context.SECURITY_PRINCIPAL, username );
		env.put( Context.SECURITY_CREDENTIALS, password );

		try
		{
			mContext = new InitialLdapContext( env, null );
		}
		catch( final NamingException e )
		{
			throw new LdapException( "Error connecting to LDAP server!", e );
		}
		return;
	}

	@Override
	public void close() throws LdapException
	{
		try
		{
			mContext.close();
		}
		catch( final NamingException e )
		{
			throw new LdapException( "Error closing LDAP connection", e );
		}
	}

	public SortedSet< LdapUser > listUsers() throws LdapException
	{
		final String searchBase = "ou=people," + mBaseDN;

		final String searchFilter = "(objectClass=inetOrgPerson)";

		final SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );

		try
		{
			final SortedSet< LdapUser > users = new TreeSet<>();

			final NamingEnumeration< SearchResult > results = mContext.search( searchBase, searchFilter, searchControls );
			while( results.hasMoreElements() )
			{
				final SearchResult searchResult = results.nextElement();

				final Map< String, String > attributes = convertAttributes( searchResult );

				if( !searchResult.getNameInNamespace().equals( generateDN( attributes.get( ATTRIBUTE_UID ), mBaseDN ) ) )
				{
					throw new LdapException( String.format( "DN(%s) and UID(%s) no not match!", searchResult.getNameInNamespace(), attributes.get( ATTRIBUTE_UID ) ) );
				}

				users.add( new LdapUser(
					attributes.get( ATTRIBUTE_UID ),
					attributes.get( ATTRIBUTE_DISPLAY_NAME ),
					attributes.get( ATTRIBUTE_MAIL ),
					null,
					mBaseDN
				) );
			}

			return users;
		}
		catch( final NamingException e )
		{
			throw new LdapException( "Error running LDAP search", e );
		}
	}

	public LdapUser findUserByUid( final String uid, boolean throwExceptionIfMissing ) throws GeneralScenarioExplorerException
	{
		if( uid == null || uid.isEmpty() )
		{
			throw new LdapException( "LDAP UID cannot be null or empty!" );
		}

		return findUser( String.format( "(&(objectClass=inetOrgPerson)(%s=%s))", ATTRIBUTE_UID, uid ), throwExceptionIfMissing );
	}

	public LdapUser findUserByMail( final String mail, boolean throwExceptionIfMissing ) throws GeneralScenarioExplorerException
	{
		if( mail == null || mail.isEmpty() )
		{
			throw new LdapException( "Email cannot be null or empty!" );
		}

		return findUser( String.format( "(&(objectClass=inetOrgPerson)(%s=%s))", ATTRIBUTE_MAIL, mail ), throwExceptionIfMissing );
	}

	private LdapUser findUser( final String searchFilter, boolean throwExceptionIfMissing ) throws GeneralScenarioExplorerException
	{
		final String searchBase = "ou=people," + mBaseDN;

		final SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );

		try
		{
			final NamingEnumeration< SearchResult > results = mContext.search( searchBase, searchFilter, searchControls );
			if( results.hasMoreElements() )
			{
				final SearchResult searchResult = results.nextElement();

				final Map< String, String > attributes = convertAttributes( searchResult );

				if( !searchResult.getNameInNamespace().equals( generateDN( attributes.get( ATTRIBUTE_UID ), mBaseDN ) ) )
				{
					throw new LdapException( String.format( "DN(%s) and UID(%s) no not match!", searchResult.getNameInNamespace(), attributes.get( ATTRIBUTE_UID ) ) );
				}

				return new LdapUser(
					attributes.get( ATTRIBUTE_UID ),
					attributes.get( ATTRIBUTE_DISPLAY_NAME ),
					attributes.get( ATTRIBUTE_MAIL ),
					null,
					mBaseDN );
			}
			else if( throwExceptionIfMissing )
			{
				throw new LdapException( "Could not find specified user" );
			}
			else
			{
				return null;
			}
		}
		catch( final NamingException e )
		{
			throw new LdapException( "Error running LDAP search", e );
		}
	}

	public boolean checkUserPassword( final String uid, final String passwordToCheck, final Set< LdapPasswordFormat > availableFormats ) throws LdapException
	{
		if( uid == null || uid.isEmpty() )
		{
			throw new LdapException( "LDAP UID cannot be null or empty!" );
		}

		if( passwordToCheck == null || passwordToCheck.isEmpty() )
		{
			throw new LdapException( "LDAP password cannot be null or empty!" );
		}

		if( availableFormats == null || availableFormats.isEmpty() )
		{
			throw new LdapException( "LDAP available password formats cannot be null or empty!" );
		}

		final String searchFilter = String.format( "(&(objectClass=inetOrgPerson)(%s=%s))", ATTRIBUTE_UID, uid );
		final String searchBase = "ou=people," + mBaseDN;

		final SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope( SearchControls.SUBTREE_SCOPE );

		try
		{
			final NamingEnumeration< SearchResult > results = mContext.search( searchBase, searchFilter, searchControls );
			if( results.hasMoreElements() )
			{
				final SearchResult searchResult = results.nextElement();

				final String passwordInLDAP = new String( (byte[])searchResult.getAttributes().get( ATTRIBUTE_USER_PASSWORD ).get() );
//				System.out.println( String.format( "in ldap: [%s]", passwordInLDAP ) );

				for( final LdapPasswordFormat format : availableFormats )
				{
					if( format.isFormat( passwordInLDAP ) && format.matches( passwordInLDAP, passwordToCheck ) )
					{
						return true;
					}
				}

				return false;
			}
			else
			{
				throw new LdapException( String.format( "Unknown user: %s", uid ) );
			}
		}
		catch( final NamingException | GeneralScenarioExplorerException e )
		{
			throw new LdapException( "Error checking user password", e );
		}
	}

	public LdapUser addUser( final String uid, final String displayName, final String mail, final String password ) throws LdapException
	{
		if( uid == null || uid.isEmpty() )
		{
			throw new LdapException( "LDAP UID cannot be null or empty!" );
		}

		if( displayName == null || displayName.isEmpty() )
		{
			throw new LdapException( "LDAP display name cannot be null or empty!" );
		}

		if( mail == null || mail.isEmpty() )
		{
			throw new LdapException( "LDAP email address cannot be null or empty!" );
		}

		if( password == null || password.isEmpty() )
		{
			throw new LdapException( "LDAP password cannot be null or empty!" );
		}

		try
		{
			final BasicAttribute objClasses = new BasicAttribute( "objectclass" );
			objClasses.add( "person" );
			objClasses.add( "organizationalPerson" );
			objClasses.add( "inetOrgPerson" );

			final BasicAttributes attributes = new BasicAttributes();
			attributes.put( objClasses );
			attributes.put( ATTRIBUTE_CN, displayName );
			attributes.put( ATTRIBUTE_DISPLAY_NAME, displayName );
			attributes.put( ATTRIBUTE_SN, uid );
			attributes.put( ATTRIBUTE_MAIL, mail );
			attributes.put( ATTRIBUTE_USER_PASSWORD, password );

			mContext.createSubcontext( generateDN( uid, mBaseDN ), attributes );
		}
		catch( final NamingException e )
		{
			throw new LdapException( "Error adding LDAP user", e );
		}

		return new LdapUser( uid, displayName, mail, null, mBaseDN );
	}

	public void modifyUser( final LdapUser user ) throws GeneralScenarioExplorerException
	{
		if( user == null )
		{
			throw new LdapException( "LDAP user cannot be null!" );
		}

		try
		{
			final LdapUser existingUser = findUserByUid( user.getUID(), true );
			final String dn = generateDN( user.getUID(), mBaseDN );

			if( !existingUser.getDisplayName().equals( user.getDisplayName() ) )
			{
				mContext.modifyAttributes( dn, DirContext.REPLACE_ATTRIBUTE, new BasicAttributes( ATTRIBUTE_CN, user.getDisplayName() ) );
				mContext.modifyAttributes( dn, DirContext.REPLACE_ATTRIBUTE, new BasicAttributes( ATTRIBUTE_DISPLAY_NAME, user.getDisplayName() ) );
			}

			if( !existingUser.getMail().equals( user.getMail() ) )
			{
				mContext.modifyAttributes( dn, DirContext.REPLACE_ATTRIBUTE, new BasicAttributes( ATTRIBUTE_MAIL, user.getMail() ) );
			}

			if( user.getPassword() != null )
			{
				mContext.modifyAttributes( dn, DirContext.REPLACE_ATTRIBUTE, new BasicAttributes( ATTRIBUTE_USER_PASSWORD, user.getPassword() ) );
			}
		}
		catch( final NamingException e )
		{
			throw new LdapException( "Error deleting LDAP entry", e );
		}
	}

	public void deleteUser( final String uid ) throws LdapException
	{
		if( uid == null || uid.isEmpty() )
		{
			throw new LdapException( "LDAP UID cannot be null or empty!" );
		}

		try
		{
			mContext.destroySubcontext( generateDN( uid, mBaseDN ) );
		}
		catch( final NamingException e )
		{
			throw new LdapException( "Error deleting LDAP entry", e );
		}
	}

	public void modifyUserPassword( final String uid, final String newPassword ) throws LdapException
	{
		if( uid == null || uid.isEmpty() )
		{
			throw new LdapException( "LDAP UID cannot be null or empty!" );
		}

		if( newPassword == null || newPassword.isEmpty() )
		{
			throw new LdapException( "LDAP password cannot be null or empty!" );
		}

		try
		{
			mContext.modifyAttributes(
				generateDN( uid, mBaseDN ),
				DirContext.REPLACE_ATTRIBUTE,
				new BasicAttributes( ATTRIBUTE_USER_PASSWORD, newPassword ) );
		}
		catch( final NamingException e )
		{
			throw new LdapException( "Error deleting LDAP entry", e );
		}
	}

	private Map< String, String > convertAttributes( final SearchResult searchResult ) throws LdapException
	{
		try
		{
			final Map< String, String > converted = new HashMap<>();

			final NamingEnumeration< ? extends Attribute > attributes = searchResult.getAttributes().getAll();
			while( attributes.hasMoreElements() )
			{
				final Attribute attribute = attributes.next();

				if( !attribute.getID().equals( ATTRIBUTE_USER_PASSWORD ) )
				{
					converted.put( attribute.getID(), attribute.get().toString() );
				}
			}

			return converted;
		}
		catch( final NamingException e )
		{
			throw new LdapException( "Error converting LDAP attributes", e );
		}
	}

	public static String generateDN( final String uid, final String baseDN )
	{
		return String.format( "%s=%s,ou=people," + baseDN, ATTRIBUTE_UID, uid );
	}
}
