package com.imaginationsupport;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class ImaginationSupportConfig
{
	@SuppressWarnings( "WeakerAccess" )
	public abstract static class Keys
	{
		public static final String URL_WEB_UI = "SE_URL_WEB_UI";
		public static final String URL_WEB_USER_SUPPORT = "SE_URL_WEB_USER_SUPPORT";

		public static final String MONGODB_HOSTNAME = "SE_MONGODB_HOSTNAME";
		public static final String MONGODB_PORT = "SE_MONGODB_PORT";
		public static final String MONGODB_DATABASE = "SE_MONGODB_DATABASE";
		public static final String MONGODB_USERNAME = "SE_MONGODB_USERNAME";
		public static final String MONGODB_PASSWORD = "SE_MONGODB_PASSWORD";

		public static final String SMTP_HOSTNAME = "SE_SMTP_HOSTNAME";
		public static final String SMTP_PORT = "SE_SMTP_PORT";
		public static final String SMTP_USERNAME = "SE_SMTP_USERNAME";
		public static final String SMTP_PASSWORD = "SE_SMTP_PASSWORD";
		public static final String SMTP_USE_SSL = "SE_SMTP_USE_SSL";
		public static final String SMTP_FROM_EMAIL = "SE_SMTP_FROM_EMAIL";
		public static final String SMTP_FROM_NAME = "SE_SMTP_FROM_NAME";
		public static final String SMTP_BCC_ADDRESSES = "SE_SMTP_BCC_ADDRESSES";

		public static final String LDAP_HOSTNAME = "SE_LDAP_SERVER";
		public static final String LDAP_PORT = "SE_LDAP_PORT";
		public static final String LDAP_PROTOCOL = "SE_LDAP_PROTOCOL";
		public static final String LDAP_ROOT_DN = "SE_LDAP_ROOT_DN";
		public static final String LDAP_ROOT_PW = "SE_LDAP_ROOT_PW";
		public static final String LDAP_BASE_DN = "SE_LDAP_BASE_DN";

		public static final String NUM_JOB_MANAGER_THREADS = "job.manager.threads";
	}

	private static Properties fileProperties = null;

	/**
	 * Constructor -- static class, so should not use the constructor
	 */
	private ImaginationSupportConfig()
	{
		return;
	}

	/**
	 * Gets the URL to the web UI
	 *
	 * @return the URL to the web UI
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getWebUiUrl() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.URL_WEB_UI );
	}

	/**
	 * Gets the URL to the web user support
	 *
	 * @return the URL to the web user support
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getWebUserSupportUrl() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.URL_WEB_USER_SUPPORT );
	}

	/**
	 * Gets the MongoDB hostname
	 *
	 * @return the MongoDB hostname
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getMongoDbHostname() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.MONGODB_HOSTNAME );
	}

	/**
	 * Gets the MongoDB port
	 *
	 * @return the MongoDB port
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static int getMongoDbPort() throws GeneralScenarioExplorerException
	{
		return Integer.parseInt( getValue( Keys.MONGODB_PORT ) );
	}

	/**
	 * Gets the MongoDB database name
	 *
	 * @return the MongoDB database name
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getMongoDbDatabase() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.MONGODB_DATABASE );
	}

	/**
	 * Gets the MongoDB database username
	 *
	 * @return the MongoDB database name
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getMongoDbUsername() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.MONGODB_USERNAME );
	}

	/**
	 * Gets the MongoDB database password
	 *
	 * @return the MongoDB database name
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getMongoDbPassword() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.MONGODB_PASSWORD );
	}

	/**
	 * Gets the SMTP hostname
	 *
	 * @return the SMTP hostname
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getSmtpHostname() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.SMTP_HOSTNAME );
	}

	/**
	 * Gets the SMTP port
	 *
	 * @return the SMTP port
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static int getSmtpPort() throws GeneralScenarioExplorerException
	{
		return Integer.parseInt( getValue( Keys.SMTP_PORT ) );
	}

	/**
	 * Gets the SMTP username
	 *
	 * @return the SMTP username
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getSmtpUsername() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.SMTP_USERNAME );
	}

	/**
	 * Gets the SMTP password
	 *
	 * @return the SMTP password
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getSmtpPassword() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.SMTP_PASSWORD );
	}

	/**
	 * Gets the SMTP use_ssl
	 *
	 * @return the SMTP use_ssl
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static boolean getSmtpUseSSL() throws GeneralScenarioExplorerException
	{
		return Boolean.parseBoolean( getValue( Keys.SMTP_USE_SSL ) );
	}

	/**
	 * Gets the SMTP from email
	 *
	 * @return the SMTP from email
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getSmtpFromEmail() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.SMTP_FROM_EMAIL );
	}

	/**
	 * Gets the SMTP from name
	 *
	 * @return the SMTP from name
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getSmtpFromName() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.SMTP_FROM_NAME );
	}

	/**
	 * Gets the SMTP BCC addresses
	 *
	 * @return the SMTP BCC addresses
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getSmtpBccAddresses() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.SMTP_BCC_ADDRESSES );
	}

	/**
	 * Gets the LDAP hostname
	 *
	 * @return the LDAP hostname
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getLdapHostname() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.LDAP_HOSTNAME );
	}

	/**
	 * Gets the LDAP port
	 *
	 * @return the LDAP port
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static int getLdapPort() throws GeneralScenarioExplorerException
	{
		return Integer.parseInt( getValue( Keys.LDAP_PORT ) );
	}

	/**
	 * Gets the LDAP protocol
	 *
	 * @return the LDAP protocol
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getLdapProtocol() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.LDAP_PROTOCOL );
	}

	/**
	 * Gets the LDAP root DN
	 *
	 * @return the LDAP root DN
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getLdapRootDN() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.LDAP_ROOT_DN );
	}

	/**
	 * Gets the LDAP root password
	 *
	 * @return the LDAP root password
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getLdapRootPassword() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.LDAP_ROOT_PW );
	}

	/**
	 * Gets the LDAP base DN
	 *
	 * @return the LDAP base DN
	 */
	@SuppressWarnings( "WeakerAccess" )
	public static String getLdapBaseDN() throws GeneralScenarioExplorerException
	{
		return getValue( Keys.LDAP_BASE_DN );
	}

	@SuppressWarnings( "WeakerAccess" )
	public static int getJobManagerThreads() throws GeneralScenarioExplorerException
	{
		return Integer.parseInt( getValue( Keys.NUM_JOB_MANAGER_THREADS ) );
	}

	private static String getValue( final String key ) throws GeneralScenarioExplorerException
	{
		if( System.getenv( key ) == null )
		{
			return getValueFromFile( key );
			//throw new GeneralScenarioExplorerException( String.format( "Environment variable \"%s\" not set!", key ) );
		}

		return System.getenv( key );
	}

	private static String getValueFromFile( final String key ) throws GeneralScenarioExplorerException
	{
		FileInputStream in = null;
		if( fileProperties == null )
		{
			try
			{
				fileProperties = new Properties();
				in = new FileInputStream( "../se_config.txt" );
				fileProperties.load( in );
			}
			catch( IOException e )
			{
				throw new GeneralScenarioExplorerException( String.format( "Environment variable \"%s\" not set!", key ) );
			}
			finally
			{
				if( in != null )
				{
					try
					{
						in.close();
					}
					catch( final IOException e )
					{
						// just ignore
					}
				}
			}
		}

		if( fileProperties.containsKey( key ) )
		{
			return fileProperties.getProperty( key );
		}
		else
		{
			throw new GeneralScenarioExplorerException( String.format( "Variable \"%s\" not set in environment or in se_config.txt (" + in + ") file.", key ) );
		}
	}
}
