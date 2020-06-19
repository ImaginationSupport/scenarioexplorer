package com.imaginationsupport.web.ldap;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.ldap.LdapPasswordFormat;
import com.imaginationsupport.web.WebCommon;

import java.util.regex.Pattern;

public class LdapPasswordFormatTomcat implements LdapPasswordFormat
{
	/**
	 * Holds the regex matcher if the given string is this password format
	 */
	private final static Pattern MATCHER = Pattern.compile( "^[0-9a-f]{32}[$]100000[$][0-9a-f]{64}$" );

	@Override
	public boolean isFormat( final String hashedCredential ) throws GeneralScenarioExplorerException
	{
		if( hashedCredential == null )
		{
			throw new GeneralScenarioExplorerException( "Hashed credential cannot be null!" );
		}

		return MATCHER.matcher( hashedCredential ).matches();
	}

	@Override
	public boolean matches( final String hashedCredential, final String clearTextPasswordToCheck ) throws GeneralScenarioExplorerException
	{
		if( hashedCredential == null )
		{
			throw new GeneralScenarioExplorerException( "Hashed credential cannot be null!" );
		}

		if( clearTextPasswordToCheck == null )
		{
			throw new GeneralScenarioExplorerException( "Plaintext password to check cannot be null!" );
		}

		return WebCommon.checkTomcatPassword( hashedCredential, clearTextPasswordToCheck );
	}
}
