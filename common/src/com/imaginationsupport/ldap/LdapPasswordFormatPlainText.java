package com.imaginationsupport.ldap;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;

public class LdapPasswordFormatPlainText implements LdapPasswordFormat
{
	@Override
	public boolean isFormat( final String hashedCredential ) throws GeneralScenarioExplorerException
	{
		if( hashedCredential == null )
		{
			throw new GeneralScenarioExplorerException( "Hashed credential cannot be null!" );
		}

		return true;
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

		return hashedCredential.equals( clearTextPasswordToCheck );
	}
}
