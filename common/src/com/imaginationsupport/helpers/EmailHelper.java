package com.imaginationsupport.helpers;

import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import org.apache.logging.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public abstract class EmailHelper
{
	public static final String MIME_TYPE_HTML = "text/html";
	public static final String MIME_TYPE_PLAIN_TEXT = "text/plain";

	private static final Logger LOGGER = ImaginationSupportUtil.getMailLogger();

	public static void sendEmail(
		final String smtpHostname,
		final int smtpPort,
		final String smtpUsername,
		final String smtpPassword,
		final boolean useSSL,
		final String fromAddress,
		final String fromName,
		final String toAddresses,
		final String ccAddresses,
		final String bccAddresses,
		final String subject,
		final String mimeType,
		final String bodyText ) throws GeneralScenarioExplorerException
	{
		if( smtpHostname == null || smtpHostname.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "SMTP server hostname cannot be null or empty!" );
		}
		if( smtpUsername == null || smtpUsername.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "SMTP server username cannot be null or empty!" );
		}
		if( smtpPassword == null || smtpPassword.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "SMTP server password cannot be null or empty!" );
		}
		if( fromAddress == null || fromAddress.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "From email address cannot be null or empty!" );
		}
		if( fromName == null || fromName.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "From email name cannot be null or empty!" );
		}
		if( toAddresses == null || toAddresses.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "To email address cannot be null or empty!" );
		}
		if( subject == null || subject.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "Email subject cannot be null or empty!" );
		}
		if( mimeType == null || mimeType.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "Email MIME type cannot be null or empty!" );
		}
		if( bodyText == null || bodyText.isEmpty() )
		{
			throw new GeneralScenarioExplorerException( "Email body text cannot be null or empty!" );
		}

		LOGGER.debug( String.format( "SMTP Hostname: [%s]", smtpHostname ) );
		LOGGER.debug( String.format( "SMTP Port:     %d", smtpPort ) );
		LOGGER.debug( String.format( "SMTP Username: [%s]", smtpUsername ) );
		LOGGER.debug( String.format( "SMTP Password: [%s]", smtpPassword ) );
		LOGGER.debug( String.format( "SMTP Use SSL:  [%s]", useSSL ) );
		LOGGER.debug( String.format( "From Address:  [%s]", fromAddress ) );
		LOGGER.debug( String.format( "From Name:     [%s]", fromName ) );
		LOGGER.debug( String.format( "To Address:    [%s]", toAddresses ) );
		LOGGER.debug( String.format( "CC Address:    [%s]", ccAddresses ) );
		LOGGER.debug( String.format( "BCC Address:   [%s]", bccAddresses ) );
		LOGGER.debug( String.format( "Subject:       [%s]", subject ) );
		LOGGER.debug( String.format( "MIME type:     [%s]", mimeType ) );
		LOGGER.debug( String.format( "Body:          %d bytes", bodyText.length() ) );

		try
		{
			final Properties props = System.getProperties();
			props.put( "mail.transport.protocol", "smtp" );
			props.put( "mail.smtp.port", smtpPort );
			props.put( "mail.smtp.auth", "true" );

			if( useSSL )
			{
				props.put( "mail.smtp.starttls.enable", "true" );
				props.put( "mail.smtp.ssl.enable", "true" );
			}

			final Session session = Session.getDefaultInstance( props );

			// JavaMail: https://github.com/javaee/javamail/releases

			// NOTE:  If you get an error:
			//			java.lang.NoClassDefFoundError: javax/activation/DataHandler
			// 		  See this: https://javaee.github.io/javamail/FAQ.html#jdk9-jaf
			//		  So just add this when you run java:
			//			--add-modules java.activation

//			session.setDebug( true ); // TODO remove!

			final MimeMessage message = new MimeMessage( session );
			message.setFrom( new InternetAddress( fromAddress, fromName ) );
			message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( toAddresses ) );

			if( ccAddresses != null && !ccAddresses.isEmpty() )
			{
				message.addRecipients( Message.RecipientType.CC, InternetAddress.parse( ccAddresses ) );
			}

			if( bccAddresses != null && !bccAddresses.isEmpty() )
			{
				message.addRecipients( Message.RecipientType.BCC, InternetAddress.parse( bccAddresses ) );
			}

			message.setSubject( subject );
			message.setContent( bodyText, mimeType );

			LOGGER.info( String.format( "From: %s", fromAddress ) );
			LOGGER.info( String.format( "To: %s", toAddresses ) );
			LOGGER.info( String.format( "Subject: %s", subject ) );
			LOGGER.info( bodyText );

			try( final Transport transport = session.getTransport() )
			{
				transport.connect( smtpHostname, smtpUsername, smtpPassword );
				transport.sendMessage( message, message.getAllRecipients() );
			}
			catch( final Exception e )
			{
				LOGGER.error( "Error sending email:", e );
				throw new GeneralScenarioExplorerException( "Error sending email!", e );
			}
		}
		catch( final UnsupportedEncodingException | MessagingException e )
		{
			throw new GeneralScenarioExplorerException( "Error sending email!", e );
		}

		return;
	}
}
