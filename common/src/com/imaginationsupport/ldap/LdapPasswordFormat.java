package com.imaginationsupport.ldap;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;

public interface LdapPasswordFormat
{
	/**
	 * Checks if the given hashed credential is this password format
	 *
	 * @param hashedCredential the hashed credential to check
	 * @return true if the given hashed credential is this format, otherwise false
	 */
	boolean isFormat( final String hashedCredential ) throws GeneralScenarioExplorerException;

	/**
	 * Check if the given cleartext password matches the given hashed credential
	 *
	 * @param hashedCredential         the hashed credential to check against
	 * @param clearTextPasswordToCheck the cleartext password to check
	 * @return true if the password matches the hashed credential
	 */
	boolean matches( final String hashedCredential, final String clearTextPasswordToCheck ) throws GeneralScenarioExplorerException;
}
