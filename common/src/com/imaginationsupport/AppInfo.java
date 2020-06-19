package com.imaginationsupport;

/**
 * Helper class that holds variables that are filled in during the Ant build
 */
public abstract class AppInfo
{
	/**
	 * Holds the build time
	 */
	public static final String BUILD_TIME = "<filled in during ant build>";

	/**
	 * Holds the ap name
	 */
	public static final String APP_NAME = "<filled in during ant build>";

	/**
	 * Holds the app version
	 */
	public static final String APP_VERSION = "<filled in during ant build>";

	/**
	 * Holds the git branch name
	 */
	public static final String GIT_BRANCH = "<filled in during ant build>";

	/**
	 * Holds the git commit id
	 */
	public static final String GIT_COMMIT_ID = "<filled in during ant build>";

	/**
	 * Holds the build number
	 */
	public static final String BUILD_NUMBER = "<filled in during ant build>";
}
