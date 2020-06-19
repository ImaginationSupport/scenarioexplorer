package com.imaginationsupport.ldap;

import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.exceptions.LdapException;
import com.imaginationsupport.helpers.JsonHelper;
import org.json.JSONObject;

public class LdapUser implements ApiObject, Comparable< LdapUser >
{
	@SuppressWarnings( "WeakerAccess" )
	public static final String JSON_KEY_UID = "uid";

	@SuppressWarnings( "WeakerAccess" )
	public static final String JSON_KEY_DISPLAY_NAME = "displayName";

	@SuppressWarnings( "WeakerAccess" )
	public static final String JSON_KEY_MAIL = "mail";

	@SuppressWarnings( "WeakerAccess" )
	public static final String JSON_KEY_DN = "dn";

	@SuppressWarnings( "WeakerAccess" )
	public static final String JSON_KEY_PASSWORD = "password";

	private final String mUID;
	private final String mDisplayName;
	private final String mMail;
	private final String mPassword;

	private final String mBaseDN;

	public LdapUser( final String uid, final String displayName, final String mail, final String password, final String baseDN ) throws LdapException
	{
		if( uid == null || uid.trim().isEmpty() )
		{
			throw new LdapException( "LDAP UID cannot be null or empty!" );
		}

		if( displayName == null || displayName.trim().isEmpty() )
		{
			throw new LdapException( "LDAP display name cannot be null or empty!" );
		}

		if( mail == null || mail.trim().isEmpty() )
		{
			throw new LdapException( "LDAP email address cannot be null or empty!" );
		}

		if( baseDN == null || baseDN.trim().isEmpty() )
		{
			throw new LdapException( "LDAP email address cannot be null or empty!" );
		}

		mUID = uid;
		mDisplayName = displayName;
		mMail = mail;
		mPassword = password;
		mBaseDN = baseDN;

		return;
	}

	public LdapUser(
		final JSONObject source,
		final boolean allowMissingUID,
		final boolean allowMissingPassword,
		final String baseDN ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		if( source == null )
		{
			throw new LdapException( "Source JSON object cannot be null!" );
		}

		if( baseDN == null || baseDN.trim().isEmpty() )
		{
			throw new LdapException( "LDAP email address cannot be null or empty!" );
		}
		mBaseDN = baseDN;

		mUID = allowMissingUID
			? JsonHelper.getOptionalParameterString( source, JSON_KEY_UID )
			: JsonHelper.getRequiredParameterString( source, JSON_KEY_UID );
		mDisplayName = JsonHelper.getRequiredParameterString( source, JSON_KEY_DISPLAY_NAME );
		mMail = JsonHelper.getRequiredParameterString( source, JSON_KEY_MAIL );
		mPassword = allowMissingPassword
			? JsonHelper.getOptionalParameterString( source, JSON_KEY_PASSWORD )
			: JsonHelper.getRequiredParameterString( source, JSON_KEY_PASSWORD );

		if( mUID != null && mUID.isEmpty() )
		{
			throw new LdapException( "LDAP UID cannot be null or empty!" );
		}

		if( mDisplayName.isEmpty() )
		{
			throw new LdapException( "LDAP display name cannot be null or empty!" );
		}

		if( mMail.isEmpty() )
		{
			throw new LdapException( "LDAP email address cannot be null or empty!" );
		}

		if( mPassword != null && mPassword.isEmpty() && !allowMissingPassword )
		{
			throw new LdapException( "LDAP password cannot be empty!" );
		}

		return;
	}

	public String getDistinguishedName()
	{
		return LdapConnection.generateDN( mUID, mBaseDN );
	}

	public String getUID()
	{
		return mUID;
	}

	public String getDisplayName()
	{
		return mDisplayName;
	}

	public String getMail()
	{
		return mMail;
	}

	public String getPassword()
	{
		return mPassword;
	}

	@SuppressWarnings( "NullableProblems" )
	@Override
	public int compareTo( final LdapUser other )
	{
		return mUID.compareTo( other.mUID );
	}

	@Override
	public boolean equals( final Object other )
	{
		return other instanceof LdapUser && mUID.equals( ( (LdapUser)other ).mUID );
	}

	@Override
	public int hashCode()
	{
		return mUID.hashCode();
	}

	@Override
	public JSONObject toJSON()
	{
		final JSONObject json = new JSONObject();

		JsonHelper.put( json, JSON_KEY_UID, mUID );
		JsonHelper.put( json, JSON_KEY_DISPLAY_NAME, mDisplayName );
		JsonHelper.put( json, JSON_KEY_MAIL, mMail );

		JsonHelper.put( json, JSON_KEY_DN, getDistinguishedName() );

		return json;
	}
}
