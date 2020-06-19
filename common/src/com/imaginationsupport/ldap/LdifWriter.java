package com.imaginationsupport.ldap;

import com.imaginationsupport.exceptions.LdapException;

import java.io.IOException;

public class LdifWriter implements AutoCloseable
{
	private final Appendable mOut;

	private static final String DIVIDER_LINE = "################################################################################\n";

	public LdifWriter( final Appendable out ) throws LdapException
	{
		if( out == null )
		{
			throw new LdapException( "Out cannot be null!" );
		}

		mOut = out;

		return;
	}

	public void appendBlankLine() throws LdapException
	{
		try
		{
			mOut.append( "\n" );
		}
		catch( final IOException e )
		{
			throw new LdapException( "Error creating LDIF comment", e );
		}
	}

	public void appendComment( final String comment ) throws LdapException
	{
		try
		{
			if( comment == null || comment.trim().isEmpty() )
			{
				appendBlankLine();
			}
			else
			{
				mOut.append( String.format( "# %s\n", comment ) );
			}
		}
		catch( final IOException e )
		{
			throw new LdapException( "Error creating LDIF comment", e );
		}
	}

	public void appendDividerLine() throws LdapException
	{
		try
		{
			mOut.append( DIVIDER_LINE );
		}
		catch( final IOException e )
		{
			throw new LdapException( "Error creating LDIF divider line", e );
		}

		return;
	}

	public void appendAddEntry( final LdapUser userToAdd, final boolean withComment ) throws LdapException
	{
		appendAddEntry( userToAdd, userToAdd.getPassword(), withComment );

		return;
	}

	public void appendAddEntry( final LdapUser userToAdd, final String password, final boolean withComment ) throws LdapException
	{
		if( userToAdd == null )
		{
			throw new LdapException( "User to add cannot be null!" );
		}

		try
		{
			if( withComment )
			{
				appendComment( String.format( "Add user: %s (%s)", userToAdd.getUID(), userToAdd.getDisplayName() ) );
				appendBlankLine();
			}

			mOut.append( String.format( "dn: %s\n", userToAdd.getDistinguishedName() ) );
			mOut.append( "changetype: add\n" );
			mOut.append( "objectclass: inetOrgPerson\n" );
			mOut.append( String.format( "cn: %s\n", userToAdd.getDisplayName() ) );
			mOut.append( String.format( "sn: %s\n", userToAdd.getUID() ) );
			mOut.append( String.format( "userpassword: %s\n", password ) );
			mOut.append( String.format( "mail: %s\n", userToAdd.getMail() ) );
			mOut.append( String.format( "displayName: %s\n", userToAdd.getDisplayName() ) );

			mOut.append( "\n" );
		}
		catch( final IOException e )
		{
			throw new LdapException( "Error creating LDIF delete entry", e );
		}

		return;
	}

	public void appendDeleteEntry( final LdapUser userToDelete, final boolean withComment ) throws LdapException
	{
		if( userToDelete == null )
		{
			throw new LdapException( "User to delete cannot be null!" );
		}

		try
		{
			if( withComment )
			{
				appendComment( String.format( "Delete user: %s (%s)", userToDelete.getUID(), userToDelete.getDisplayName() ) );
				appendBlankLine();
			}

			mOut.append( String.format( "dn: %s\n", userToDelete.getDistinguishedName() ) );
			mOut.append( "changetype: delete\n" );
			mOut.append( "\n" );
		}
		catch( final IOException e )
		{
			throw new LdapException( "Error creating LDIF delete entry", e );
		}

		return;
	}

	@Override
	public void close() throws LdapException
	{
		try
		{
			if( mOut instanceof AutoCloseable )
			{
				( (AutoCloseable)mOut ).close();
			}
		}
		catch( final Exception e )
		{
			throw new LdapException( "Error closing LDIF writer", e );
		}

		return;
	}
}
