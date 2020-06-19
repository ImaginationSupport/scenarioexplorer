package com.imaginationsupport.web;

@SuppressWarnings( "WeakerAccess" )
public abstract class UserSupportServletStrings
{
	public abstract class Actions
	{
		public static final String GetLdapUsers = "get-ldap-users";
		public static final String ExportLdapCSV = "export-ldap-csv";
		public static final String ExportLdapLDIF = "export-ldap-ldif";

		public static final String GetScenarioExplorerUsers = "get-scenario-explorer-users";

		public static final String ResetPassword = "reset";
		public static final String ChangePassword = "change";

		public static final String NewLdapUser = "new-ldap-user";
		public static final String UpdateLdapUser = "update-ldap-user";
		public static final String DeleteLdapUser = "delete-ldap-user";

		public static final String NewScenarioExplorerUser = "new-scenario-explorer-user";
	}

	public abstract class Parameters
	{
		public static final String Action = "action";

		public static final String UserName = "userName";
		public static final String FullName = "fullName";
		public static final String Email = "email";
		public static final String IsSiteAdmin = "isSiteAdmin";

		public static final String Password = "password";
		public static final String CurrentPassword = "current";
		public static final String NewPassword = "new";

		public static final String User = "user";
		public static final String Users = "users";
	}
}
