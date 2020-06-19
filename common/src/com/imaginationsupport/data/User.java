package com.imaginationsupport.data;

import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Indexed;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestApiObjectInfo( definitionName = "User", tagName = RestApiHandlerInfo.CategoryNames.User, description = "Scenario Explorer User" )
public class User extends Persistent implements ApiObject, Comparable< User >
{
	public static class JsonKeys extends Persistent.JsonKeys
	{
		public static final String UserName = "userName";
		public static final String FullName = "fullName";
		public static final String IsSiteAdmin = "isSiteAdmin";
		public static final String LastLogin = "lastLogin";
		public static final String Access = "access";
		public static final String Preferences = "preferences";
	}

	private static final String DEFAULT_PREFERENCES_JSON = "{}";

	@Indexed
	@RestApiFieldInfo( description = "The username to display" )
	private String userName;

	@RestApiFieldInfo( description = "The user's full name" )
	private String fullName;

	@RestApiFieldInfo( description = "True if the user is an admin, false if the user is a normal user" )
	private boolean isSiteAdmin;

	@RestApiFieldInfo( description = "The date and time the user last logged in", isRequired = false )
	private LocalDateTime lastLogin;

	@Embedded
	@RestApiFieldInfo( description = "The set of unique ids the user has access to", isRequired = false )
	private Set< ObjectId > access;

	/**
	 * Holds the JSON of the preferences object
	 */
	private String preferencesJSON;

	public User()
	{
		this.access = new LinkedHashSet<>();
		this.preferencesJSON = DEFAULT_PREFERENCES_JSON;
		
		markModified();

		return;
	}

	public User( String userName, String fullName, boolean isSiteAdmin ) throws InvalidDataException
	{
		if( userName == null || userName.trim().isEmpty() )
		{
			throw new InvalidDataException( "Username cannot be null or empty!" );
		}

		if( fullName == null )
		{
			throw new InvalidDataException( "Full name cannot be null!" );
		}

		this.userName = userName;
		this.fullName = fullName;
		this.isSiteAdmin = isSiteAdmin;
		this.access = new LinkedHashSet<>();
		this.preferencesJSON = DEFAULT_PREFERENCES_JSON;
		markModified();

		return;
	}

	public User( final JSONObject source ) throws InvalidDataException
	{
		super( source );

		final List< ObjectId > accessRaw = JsonHelper.getOptionalParameterObjectIdArray( source, JsonKeys.Access );

		this.userName = JsonHelper.getRequiredParameterString( source, JsonKeys.UserName );
		this.fullName = JsonHelper.getRequiredParameterString( source, JsonKeys.FullName );
		this.isSiteAdmin = JsonHelper.getRequiredParameterBoolean( source, JsonKeys.IsSiteAdmin );
		this.access = accessRaw == null ? new LinkedHashSet<>() : new LinkedHashSet<>( accessRaw );
		this.preferencesJSON = JsonHelper.getRequiredParameterJSONObject( source, JsonKeys.Preferences ).toString();

		if( userName == null || userName.trim().isEmpty() )
		{
			throw new InvalidDataException( "Username cannot be null or empty!" );
		}

		markModified();

		return;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String newUserName ) throws InvalidDataException
	{
		if( userName == null || userName.trim().isEmpty() )
		{
			throw new InvalidDataException( "Username cannot be null or empty!" );
		}

		this.userName = newUserName;
		markModified();
	}

	public String getFullName()
	{
		return fullName;
	}

	public void setFullName( String newFullName ) throws InvalidDataException
	{
		if( fullName == null )
		{
			throw new InvalidDataException( "Full name cannot be null!" );
		}

		this.fullName = newFullName;
		markModified();
	}

	public boolean isSiteAdmin()
	{
		return isSiteAdmin;
	}

	public void setSiteAdmin( boolean newIsSiteAdmin )
	{
		this.isSiteAdmin = newIsSiteAdmin;
		markModified();
	}

	public Set< ObjectId > getAccess()
	{
		return access;
	}

	public void setAccess( Set< ObjectId > newAccess )
	{
		this.access = newAccess;
		markModified();
	}

	public void addAccess( ObjectId projectId )
	{
		access.add( projectId );
		markModified();
	}

	public void removeAccess( ObjectId projectId )
	{
		if( access.contains( projectId ) )
		{
			access.remove( projectId );
			markModified();
		}
	}

	public LocalDateTime getLastLogin()
	{
		return lastLogin;
	}

	public void setLastLogin()
	{
		lastLogin = LocalDateTime.now();
		markModified();
	}

	public JSONObject getPreferences() throws InvalidDataException
	{
		return JsonHelper.parseObject( this.preferencesJSON );
	}

	public void setPreferences( final JSONObject preferences )
	{
		if( preferences == null )
		{
			this.preferencesJSON = DEFAULT_PREFERENCES_JSON;
		}
		else
		{
			this.preferencesJSON = preferences.toString();
		}

		markModified();
		return;
	}

	@Override
	public JSONObject toJSON() throws GeneralScenarioExplorerException, InvalidDataException
	{
		final JSONObject json = super.getBaseJson();

		JsonHelper.put( json, JsonKeys.UserName, this.userName );
		JsonHelper.put( json, JsonKeys.FullName, this.fullName );
		JsonHelper.put( json, JsonKeys.IsSiteAdmin, this.isSiteAdmin );
		JsonHelper.put( json, JsonKeys.LastLogin, this.lastLogin );

		JsonHelper.putObjectIds( json, JsonKeys.Access, this.access );
		JsonHelper.put( json, JsonKeys.Preferences, this.getPreferences() == null ? new JSONObject() : this.getPreferences() );

		return json;
	}

	@Override
	public int compareTo( final User other )
	{
		return this.userName.equals( other.userName )
			? this.getId().compareTo( other.getId() )
			: this.userName.toLowerCase().compareTo( other.userName.toLowerCase() );
	}

	@Override
	public String toString()
	{
		return String.format( "%s (%s)", this.userName, this.fullName );
	}

	/**
	 * When comparing Users, only use the username
	 *
	 * @param other the User to compare to
	 *
	 * @return True if the usernames match, otherwise false
	 */
	public boolean equals( final User other )
	{
		return this.getUserName().equals( other.getUserName() );
	}
}
