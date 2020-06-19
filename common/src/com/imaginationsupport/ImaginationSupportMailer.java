package com.imaginationsupport;

import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.helpers.EmailHelper;

public abstract class ImaginationSupportMailer
{
	private static final String NEW_USER_TEMPLATE = "Hello,\n"
		+ "\n"
		+ "You have been given access to Scenario Explorer:\n"
		+ "\n"
		+ "Username: {{uid}}\n"
		+ "Password: {{password}}\n"
		+ "\n"
		+ "You can access the site:\n"
		+ "<a href=\"{{web-ui-url}}\">{{web-ui-url}}</a>\n"
		+ "\n"
		+ "If you need to reset your password or have trouble logging in, please visit:\n"
		+ "<a href=\"{{web-user-support-url}}\">{{web-user-support-url}}</a>\n"
		+ "\n"
		+ "Thanks!\n";
	private static final String UPDATE_USER_TEMPLATE = "Hello,\n"
		+ "\n"
		+ "Your Scenario Explorer password has been updated by an administrator:\n"
		+ "\n"
		+ "Username: {{uid}}\n"
		+ "Password: {{password}}\n"
		+ "\n"
		+ "If you need to reset your password or have trouble logging in, please visit:\n"
		+ "<a href=\"{{web-user-support-url}}\">{{web-user-support-url}}</a>\n"
		+ "\n"
		+ "Thanks!\n";
	private static final String RESET_PASSWORD_TEMPLATE = "Hello,\n"
		+ "\n"
		+ "Your Scenario Explorer password has been reset:\n"
		+ "\n"
		+ "Username: {{uid}}\n"
		+ "Password: {{password}}\n"
		+ "\n"
		+ "If you would like to change your password or have trouble logging in, please visit:\n"
		+ "<a href=\"{{web-user-support-url}}\">{{web-user-support-url}}</a>\n"
		+ "\n"
		+ "Thanks!\n";

	public static void sendNewUserEmail(
		final String toEmailAddress,
		final String uid,
		final String displayName,
		final String password,
		final String webUiUri,
		final String webUserSupportUri ) throws GeneralScenarioExplorerException
	{
		sendEmail(
			NEW_USER_TEMPLATE,
			toEmailAddress,
			"Scenario Explorer New Account Information",
			uid,
			displayName,
			password,
			webUiUri,
			webUserSupportUri );

		return;
	}

	public static void sendUpdateUserEmail(
		final String toEmailAddress,
		final String uid,
		final String displayName,
		final String password,
		final String webUiUri,
		final String webUserSupportUri ) throws GeneralScenarioExplorerException
	{
		sendEmail(
			UPDATE_USER_TEMPLATE,
			toEmailAddress,
			"Scenario Explorer Account Update",
			uid,
			displayName,
			password,
			webUiUri,
			webUserSupportUri );
		return;
	}

	public static void sendPasswordResetEmail(
		final String toEmailAddress,
		final String uid,
		final String displayName,
		final String password,
		final String webUiUri,
		final String webUserSupportUri ) throws GeneralScenarioExplorerException
	{
		sendEmail(
			RESET_PASSWORD_TEMPLATE,
			toEmailAddress,
			"Scenario Explorer Account Update",
			uid,
			displayName,
			password,
			webUiUri,
			webUserSupportUri );
		return;
	}

	private static void sendEmail(
		final String templateText,
		final String toEmailAddress,
		final String emailSubject,
		final String uid,
		final String displayName,
		final String password,
		final String webUiUri,
		final String webUserSupportUri ) throws GeneralScenarioExplorerException
	{
		final String bodyText = updateTemplate(
			templateText,
			uid,
			displayName,
			password,
			webUiUri,
			webUserSupportUri );

		EmailHelper.sendEmail(
			ImaginationSupportConfig.getSmtpHostname(),
			ImaginationSupportConfig.getSmtpPort(),
			ImaginationSupportConfig.getSmtpUsername(),
			ImaginationSupportConfig.getSmtpPassword(),
			ImaginationSupportConfig.getSmtpUseSSL(),
			ImaginationSupportConfig.getSmtpFromEmail(),
			ImaginationSupportConfig.getSmtpFromName(),
			toEmailAddress,
			null,
			ImaginationSupportConfig.getSmtpBccAddresses(),
			emailSubject,
			EmailHelper.MIME_TYPE_HTML,
			bodyText );
	}

	private static String updateTemplate(
		final String templateText,
		final String userUID,
		final String userDisplayName,
		final String userPassword,
		final String webUiUri,
		final String webUserSupportUri
	)
	{
		return templateText
			.trim()
			.replaceAll( "\\{\\{uid}}", userUID )
			.replaceAll( "\\{\\{display-name}}", userDisplayName )
			.replaceAll( "\\{\\{password}}", userPassword )
			.replaceAll( "\\{\\{web-ui-url}}", webUiUri )
			.replaceAll( "\\{\\{web-user-support-url}}", webUserSupportUri )
			.replaceAll( "\r", "" )
			.replaceAll( "\n", "<br/>\n" );
	}
}
