package com.imaginationsupport.web;

import com.imaginationsupport.API;
import com.imaginationsupport.ImaginationSupportConfig;
import com.imaginationsupport.ImaginationSupportMailer;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.ldap.*;
import com.imaginationsupport.web.exceptions.ApiException;
import com.imaginationsupport.web.exceptions.BadRequestException;
import com.imaginationsupport.web.exceptions.ForbiddenException;
import com.imaginationsupport.web.exceptions.InternalServerErrorException;
import com.imaginationsupport.web.ldap.LdapPasswordFormatTomcat;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@WebServlet( name = "backend", urlPatterns = { "/backend/*" } )
public class UserSupportServlet extends ImaginationSupportServletBase
{
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = 1L;

	private static final String RESET_PASSWORD_JSP = "reset-password.jsp";
	private static final String RESET_PASSWORD_SUCCESS_JSP = "reset-password-success.jsp";

	private static final String CHANGE_PASSWORD_JSP = "change-password.jsp";
	private static final String CHANGE_PASSWORD_SUCCESS_JSP = "change-password-success.jsp";

	private static final String DEFAULT_LDIF_EXPORT_PASSWORD = "passw0rd";

	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	@Override
	public void init( final ServletConfig config ) throws ServletException
	{
		super.init( config );

		try
		{
			initDatabaseConnection();
		}
		catch( GeneralScenarioExplorerException e )
		{
			LOGGER.error( ImaginationSupportUtil.formatStackTrace( e ) );
			throw new ServletException( "Error initializing the database connection", e );
		}

		LOGGER.debug( "User Support Servlet initialized" );

		return;
	}

	@Override
	protected void doGet( final HttpServletRequest request, final HttpServletResponse response )
	{
		try
		{
			final String action = getActionParameter( request );

			switch( action )
			{
				case UserSupportServletStrings.Actions.GetLdapUsers:
					handleGetLdapUsers( request, response );
					return;

				case UserSupportServletStrings.Actions.GetScenarioExplorerUsers:
					handleGetScenarioExplorerUsers( request, response );
					return;

				case UserSupportServletStrings.Actions.ExportLdapCSV:
					handleExportLdapCSV( request, response );
					return;

				case UserSupportServletStrings.Actions.ExportLdapLDIF:
					handleExportLdapLDIF( request, response );
					return;

				default:
					throw new BadRequestException( String.format( "Unknown Action: [%s]", action ) );
			}
		}
		catch( final ApiException e )
		{
			LOGGER.warn( "GET request exception!" );
			LOGGER.warn( ImaginationSupportUtil.formatStackTrace( e ) );
			LOGGER.warn( "Request: " + request.getQueryString() );

			sendErrorResponse( response, e.getCode() );
		}
	}

	@Override
	protected void doPost( final HttpServletRequest request, final HttpServletResponse response )
	{
		try
		{
			final String action = getActionParameter( request );
			switch( action )
			{
				case UserSupportServletStrings.Actions.ResetPassword:
					handleResetPassword( request, response );
					return;

				case UserSupportServletStrings.Actions.ChangePassword:
					handleChangePassword( request, response );
					return;

				case UserSupportServletStrings.Actions.NewLdapUser:
					handleNewLdapUser( request, response );
					break;

				case UserSupportServletStrings.Actions.UpdateLdapUser:
					handleUpdateLdapUser( request, response );
					return;

				case UserSupportServletStrings.Actions.DeleteLdapUser:
					handleDeleteLdapUser( request, response );
					return;

				case UserSupportServletStrings.Actions.NewScenarioExplorerUser:
					handleNewScenarioExplorerUser( request, response );
					break;

				default:
					throw new BadRequestException( String.format( "Unknown Action: [%s]", action ) );
			}
		}
		catch( final ApiException e )
		{
			LOGGER.warn( "POST request exception!" );
			LOGGER.warn( ImaginationSupportUtil.formatStackTrace( e ) );
			LOGGER.warn( "Request: " + request.getQueryString() );

			sendErrorResponse( response, e.getCode() );
		}

		return;
	}

	private void handleResetPassword( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final String username = WebCommon.getOptionalParameterString( request, UserSupportServletStrings.Parameters.UserName, null );
		final String email = WebCommon.getOptionalParameterString( request, UserSupportServletStrings.Parameters.Email, null );

		LOGGER.info( String.format( "Processing reset: username:[%s] / email: [%s]", username, email ) );

		if( ( username == null || username.isEmpty() ) && ( email == null || email.isEmpty() ) )
		{
			LOGGER.error( "UserName and email are both missing!" );
			sendErrorResponse( response, RESET_PASSWORD_JSP, "UserName and email are missing", null, null );
			return;
		}
		else if( username != null && !username.isEmpty() && email != null && !email.isEmpty() )
		{
			LOGGER.error( "UserName and email are both given!" );
			sendErrorResponse( response, RESET_PASSWORD_JSP, "Please provide either the username OR the email, not both.", username, email );
			return;
		}

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		try
		{
			final LdapUser user;
			if( username == null )
			{
				// use email
				user = ImaginationSupportLdap.findUserByEmail( email.trim(), false );
			}
			else
			{
				// use username
				user = ImaginationSupportLdap.findUserByUsername( username.trim(), false );
			}

			if( user == null )
			{
				LOGGER.error( "User not found!" );
				sendErrorResponse( response, RESET_PASSWORD_JSP, "Invalid UserName", null, null );
				return;
			}

			final String newPassword = ImaginationSupportUtil.generateRandomPassword();

			ImaginationSupportLdap.modifyUserPassword( user.getUID(), newPassword );

			ImaginationSupportMailer.sendPasswordResetEmail(
				user.getMail(),
				user.getUID(),
				user.getDisplayName(),
				newPassword,
				ImaginationSupportConfig.getWebUiUrl(),
				ImaginationSupportConfig.getWebUserSupportUrl() );

		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error resetting password!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			response.sendRedirect( RESET_PASSWORD_SUCCESS_JSP );
		}
		catch( final IOException e )
		{
			throw new InternalServerErrorException( "Error sending redirect!", e );
		}

		return;
	}

	private void handleChangePassword( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		/////////////// parse the request ///////////////

		final String username;
		final String currentPassword;
		final String newPassword;
		final LdapUser user;
		try
		{
			username = WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.UserName );
			currentPassword = WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.CurrentPassword );
			newPassword = WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.NewPassword );

			final Set< LdapPasswordFormat > availableLdapPasswordFormats = new HashSet<>();
			availableLdapPasswordFormats.add( new LdapPasswordFormatPlainText() ); // TODO remove!
			availableLdapPasswordFormats.add( new LdapPasswordFormatTomcat() );

			// check the username
			user = ImaginationSupportLdap.findUserByUsername( username, false );
			if( user == null )
			{
				LOGGER.error( "User not found!" );
				sendErrorResponse( response, CHANGE_PASSWORD_JSP, "Invalid UserName", null, null );
				return;
			}

			// check the old password
//			LOGGER.info( String.format( "testing [%s]", currentPassword ) );
			if( !ImaginationSupportLdap.checkUserPassword( user.getUID(), currentPassword, availableLdapPasswordFormats ) )
			{
				LOGGER.error( "Invalid current password!" );
				sendErrorResponse( response, CHANGE_PASSWORD_JSP, "Invalid current password!", username, null );
				return;
			}

			// check the new password
//			LOGGER.info( String.format( "setting [%s]", newPassword ) );
			if( newPassword == null || !newPassword.equals( newPassword.trim() ) || newPassword.trim().isEmpty() )
			{
				LOGGER.error( "Invalid new password!" );
				sendErrorResponse( response, CHANGE_PASSWORD_JSP, "Invalid new password!", username, null );
				return;
			}
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}

		/////////////// verify authorization ///////////////

		// no authorization needed

		/////////////// handle the action ///////////////

		try
		{
			ImaginationSupportLdap.modifyUserPassword( user.getUID(), newPassword );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error changing password!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			response.sendRedirect( CHANGE_PASSWORD_SUCCESS_JSP );
		}
		catch( final IOException e )
		{
			throw new InternalServerErrorException( "Error sending redirect!", e );
		}

		return;
	}

	/**
	 * Helper message to redirect the user to an error page with the given message instead of a basic HTTP error code
	 *
	 * @param response the response object
	 * @param jsp      the JSP to forward the user to
	 * @param message  the message to display
	 * @param username the username parameter given
	 * @param email    the email parameter given
	 */
	private void sendErrorResponse(
		final HttpServletResponse response,
		final String jsp,
		final String message,
		final String username,
		final String email ) throws InternalServerErrorException
	{
		final List< NameValuePair > queryStringEntries = new ArrayList<>();

		// TODO is this still the correct functionality?  username and email?

		if( username != null )
		{
			queryStringEntries.add( new BasicNameValuePair( "userName", username ) );
		}
		if( email != null )
		{
			queryStringEntries.add( new BasicNameValuePair( "email", email ) );
		}

		queryStringEntries.add( new BasicNameValuePair( "message", message ) );

		try
		{
			response.sendRedirect( jsp + "?" + URLEncodedUtils.format( queryStringEntries, StandardCharsets.UTF_8 ) );
		}
		catch( final IOException e )
		{
			throw new InternalServerErrorException( "Error sending redirect!", e );
		}

		return;
	}

	private void handleGetLdapUsers( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		try( final API api = new API() )
		{
			/////////////// parse the request ///////////////

			// no parameters in the request

			/////////////// verify authorization ///////////////

			verifySiteAdmin( api, request );

			/////////////// handle the action ///////////////

			final SortedSet< LdapUser > users;
			try
			{
				users = ImaginationSupportLdap.listUsers();
			}
			catch( final GeneralScenarioExplorerException e )
			{
				throw new InternalServerErrorException( "Error getting users in LDAP", e );
			}

			/////////////// return the response ///////////////

			try
			{
				sendResponseJson( response, JsonHelper.toJSONArray( users ).toString() );
			}
			catch( final InvalidDataException | GeneralScenarioExplorerException e )
			{
				throw new InternalServerErrorException( "Error sending response!", e );
			}
		}

		return;
	}

	private void handleNewLdapUser( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		try( final API api = new API() )
		{
			/////////////// parse the request ///////////////

			final LdapUser userFromRequest = parseLdapUserParameters( request );
			final boolean isSiteAdmin = getIsSiteAdminParameter( request );

			/////////////// verify authorization ///////////////

			verifySiteAdmin( api, request );

			/////////////// handle the action ///////////////

			final LdapUser userCreated;
			try
			{
				userCreated = ImaginationSupportLdap.addUser( userFromRequest );
				if( api.findUser( userFromRequest.getUID(), false ) == null )
				{
					api.createUser( userFromRequest.getUID(), userFromRequest.getDisplayName(), isSiteAdmin );
				}
			}
			catch( final GeneralScenarioExplorerException | InvalidDataException e )
			{
				throw new InternalServerErrorException( "Error creating LDAP user!", e );
			}

			/////////////// return the response ///////////////

			sendResponseJson( response, userCreated.toJSON() );
		}

		return;
	}

	private void handleUpdateLdapUser( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		try( final API api = new API() )
		{
			/////////////// parse the request ///////////////

			final LdapUser userFromRequest = parseLdapUserParameters( request );
			final boolean isSiteAdmin = getIsSiteAdminParameter( request );

			final User scenarioExplorerUser;
			try
			{
				// make sure it exists in LDAP
				ImaginationSupportLdap.findUserByUsername( userFromRequest.getUID(), true );

				// get it from the API if it exists there too
				scenarioExplorerUser = api.findUser( userFromRequest.getUID(), false );
			}
			catch( final InvalidDataException | GeneralScenarioExplorerException e )
			{
				throw new BadRequestException( "User does not exist!", e );
			}

			/////////////// verify authorization ///////////////

			verifySiteAdmin( api, request );

			/////////////// handle the action ///////////////

			try
			{
				ImaginationSupportLdap.modifyUser( userFromRequest );

				if( scenarioExplorerUser == null )
				{
					api.createUser( new User( userFromRequest.getUID(), userFromRequest.getDisplayName(), isSiteAdmin ) );
				}
				else
				{
					scenarioExplorerUser.setFullName( userFromRequest.getDisplayName() );
					scenarioExplorerUser.setSiteAdmin( isSiteAdmin );
					api.updateUser( scenarioExplorerUser );
				}
			}
			catch( final InvalidDataException | GeneralScenarioExplorerException e )
			{
				throw new InternalServerErrorException( "Error updating user!", e );
			}

			/////////////// return the response ///////////////

			sendResponseJson( response, userFromRequest.toJSON() );
		}

		return;
	}

	private void handleDeleteLdapUser( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		try( final API api = new API() )
		{
			/////////////// parse the request ///////////////

			final String uid;
			try
			{
				uid = WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.UserName );
			}
			catch( GeneralScenarioExplorerException e )
			{
				throw new BadRequestException( "Bad Request!", e );
			}

			final LdapUser ldapUser;
			try
			{
				ldapUser = ImaginationSupportLdap.findUserByUsername( uid, true );
			}
			catch( final GeneralScenarioExplorerException e )
			{
				throw new BadRequestException( "User does not exist!", e );
			}

			/////////////// verify authorization ///////////////

			verifySiteAdmin( api, request );

			/////////////// handle the action ///////////////

			try
			{
				// delete the LDAP user
				ImaginationSupportLdap.deleteUser( uid );

				// also delete the scenario explorer user, if it exists
				if( api.findUser( uid, false ) != null )
				{
					api.deleteUser( uid );
				}
			}
			catch( final InvalidDataException | GeneralScenarioExplorerException e )
			{
				throw new InternalServerErrorException( "Error deleting user!", e );
			}

			/////////////// return the response ///////////////

			sendResponseJson( response, ldapUser.toJSON() );
		}

		return;
	}

	private void handleExportLdapCSV( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		try( final API api = new API(); final StringWriter buffer = new StringWriter() )
		{
			/////////////// parse the request ///////////////

			// no parameters in request

			/////////////// verify authorization ///////////////

			verifySiteAdmin( api, request );

			/////////////// handle the action ///////////////

			try( final CSVPrinter csv = new CSVPrinter( buffer, CSVFormat.DEFAULT ) )
			{
				final SortedSet< LdapUser > users = ImaginationSupportLdap.listUsers();
				for( final LdapUser ldapUser : users )
				{
					csv.print( ldapUser.getUID() );
					csv.print( ldapUser.getDisplayName() );
					csv.print( ldapUser.getMail() );
					csv.print( ldapUser.getDistinguishedName() );

					csv.println();
				}
			}
			catch( final GeneralScenarioExplorerException e )
			{
				throw new InternalServerErrorException( "Error generating CSV!", e );
			}

			/////////////// return the response ///////////////

			sendFileDownload(
				response,
				"scenario-explorer-users.csv",
				"text/csv",
				buffer.toString().getBytes( StandardCharsets.UTF_8 ) );
		}
		catch( final IOException e )
		{
			throw new InternalServerErrorException( "Error exporting as CSV!", e );
		}

		return;
	}

	private void handleExportLdapLDIF( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		try( final API api = new API(); final StringWriter buffer = new StringWriter() )
		{
			/////////////// parse the request ///////////////

			// no parameters in request

			/////////////// verify authorization ///////////////

			verifySiteAdmin( api, request );

			/////////////// handle the action ///////////////

			try( final LdifWriter ldif = new LdifWriter( buffer ) )
			{
				boolean isFirst = true;
				final SortedSet< LdapUser > users = ImaginationSupportLdap.listUsers();
				for( final LdapUser user : users )
				{
					if( isFirst )
					{
						isFirst = false;
					}
					else
					{
						ldif.appendDividerLine();
						ldif.appendBlankLine();
					}

					ldif.appendDeleteEntry( user, true );

					ldif.appendAddEntry( user, DEFAULT_LDIF_EXPORT_PASSWORD, true );
				}
			}
			catch( final Exception e )
			{
				throw new InternalServerErrorException( "Error generating LDIF", e );
			}

			/////////////// return the response ///////////////

			sendFileDownload(
				response,
				"scenario-explorer-users.ldif",
				"text/plain",
				buffer.toString().getBytes( StandardCharsets.UTF_8 ) );
		}
		catch( final IOException e )
		{
			throw new InternalServerErrorException( "Error exporting as LDIF!", e );
		}

		return;
	}

	private void handleGetScenarioExplorerUsers( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		try( final API api = new API() )
		{
			/////////////// parse the request ///////////////

			// no parameters in request

			/////////////// verify authorization ///////////////

			verifySiteAdmin( api, request );

			/////////////// handle the action ///////////////

			final SortedSet< User > users = api.getUsers();

			/////////////// return the response ///////////////

			try
			{
				sendResponseJson( response, JsonHelper.toJSONArray( users ).toString() );
			}
			catch( final InvalidDataException | GeneralScenarioExplorerException e )
			{
				throw new InternalServerErrorException( "Error sending response!", e );
			}
		}

		return;
	}

	private void handleNewScenarioExplorerUser( final HttpServletRequest request, final HttpServletResponse response ) throws ApiException
	{
		try( final API api = new API() )
		{
			/////////////// parse the request ///////////////

			final String userName;
			final String fullName;
			final boolean isSiteAdmin;

			try
			{
				userName = WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.UserName );
				fullName = WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.FullName );
				isSiteAdmin = WebCommon.getRequiredParameterBoolean( request, UserSupportServletStrings.Parameters.IsSiteAdmin );
			}
			catch( final GeneralScenarioExplorerException e )
			{
				throw new BadRequestException( "Bad Request!", e );
			}

			/////////////// verify authorization ///////////////

			verifySiteAdmin( api, request );

			/////////////// handle the action ///////////////

			final User user;
			try
			{
				user = api.createUser( userName, fullName, isSiteAdmin );
			}
			catch( final InvalidDataException | GeneralScenarioExplorerException e )
			{
				throw new InternalServerErrorException( "Error creating user!", e );
			}

			/////////////// return the response ///////////////

			try
			{
				sendResponseJson( response, user.toJSON() );
			}
			catch( final GeneralScenarioExplorerException | InvalidDataException e )
			{
				throw new InternalServerErrorException( "Error serializing user!", e );
			}
		}

		return;
	}

	/**
	 * Helper method to verify the logged in user is a site admin
	 *
	 * @param api     The Imagination Support
	 * @param request the incoming HTTP request to parse
	 */
	private void verifySiteAdmin( final API api, final HttpServletRequest request ) throws ApiException
	{
		try
		{
			final User loggedInUser = api.findUser( WebCommon.getCurrentUserId( request ), true );
			if( !loggedInUser.isSiteAdmin() )
			{
				throw new ForbiddenException( "Not authorized for LDAP access!" );
			}
		}
		catch( InvalidDataException e )
		{
			throw new BadRequestException( "Error getting the logged in user!" );
		}
	}

	/**
	 * Parses the LDAP user from the request
	 *
	 * @param request the incoming HTTP request to parse
	 *
	 * @return the parsed LDAP user
	 */
	private LdapUser parseLdapUserParameters( final HttpServletRequest request ) throws ApiException
	{
		final String uid;
		final String displayName;
		final String mail;
		final String password;

		try
		{
			uid = WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.UserName );
			displayName = WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.FullName );
			mail = WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.Email );
			password = WebCommon.getOptionalParameterString( request, UserSupportServletStrings.Parameters.Password, null );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new BadRequestException( "Bad Request!", e );
		}

		try
		{
			return new LdapUser(
				uid,
				displayName,
				mail,
				password == null ? null : WebCommon.hashTomcatPassword( password ),
				ImaginationSupportConfig.getLdapBaseDN() );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error creating LDAP user instance!", e );
		}
	}

	/**
	 * Gets the isSiteAdmin parameter from the request
	 *
	 * @param request the incoming HTTP request to parse
	 *
	 * @return the parameter value
	 */
	private boolean getIsSiteAdminParameter( final HttpServletRequest request ) throws ApiException
	{
		try
		{
			return WebCommon.getRequiredParameterBoolean( request, UserSupportServletStrings.Parameters.IsSiteAdmin );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new BadRequestException( "Error getting site admin parameter!", e );
		}
	}

	/**
	 * Gets the action parameter from the request
	 *
	 * @param request the incoming HTTP request to parse
	 *
	 * @return the parameter value
	 */
	private String getActionParameter( final HttpServletRequest request ) throws ApiException
	{
		try
		{
			return WebCommon.getRequiredParameterString( request, UserSupportServletStrings.Parameters.Action );
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new BadRequestException( "Error getting site admin parameter!", e );
		}
	}
}
