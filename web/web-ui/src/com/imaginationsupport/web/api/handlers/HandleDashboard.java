package com.imaginationsupport.web.api.handlers;

import com.imaginationsupport.API;
import com.imaginationsupport.ImaginationSupportUtil;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.annotations.RestApiRequestInfo;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.User;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.views.View;
import com.imaginationsupport.web.ApiStrings;
import com.imaginationsupport.web.api.data.DashboardData;
import com.imaginationsupport.web.exceptions.ApiException;
import com.imaginationsupport.web.exceptions.InternalServerErrorException;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings( "unused" )
@RestApiHandlerInfo( name = RestApiHandlerInfo.CategoryNames.Dashboard )
public class HandleDashboard extends RestApiRequestHandlerBase
{
	/**
	 * Holds the log4j2 logger
	 */
	private static final Logger LOGGER = ImaginationSupportUtil.getWebLogger();

	@RestApiRequestInfo(
		summary = "Gets the dashboard data",
		request = RestApiRequestInfo.Request.ListDashboard,
		method = RestApiRequestInfo.HttpMethod.Get,
		uriParts = { ApiStrings.RestApiStrings.Dashboard },
		responseSchemaType = RestApiRequestInfo.SchemaType.JsonObject,
		responseSchemaDefinition = RestApiRequestInfo.SchemaDefinition.DashboardData )
	public static JSONObject listDashboard( final API api, final User requestUser ) throws ApiException
	{
		/////////////// verify authorization ///////////////

		// No authorization needed

		/////////////// parse the request ///////////////

		// no parameters in request

		/////////////// handle the action ///////////////

		LOGGER.debug( String.format( "Getting dashboard for: %s", requestUser ) );

		final DashboardData dashboardData;
		try
		{
			final SortedSet< Project > projects = api.getProjectsForUser( requestUser );

			final SortedSet< View > views = new TreeSet<>();
			for( final Project project : projects )
			{
				views.addAll( api.getViews( project.getId(), false ) );
			}

			dashboardData = new DashboardData( projects, views );
		}
		catch( final InvalidDataException e )
		{
			throw new InternalServerErrorException( "Error getting dashboard data!", e );
		}

		/////////////// return the response ///////////////

		try
		{
			return dashboardData.toJSON();
		}
		catch( final GeneralScenarioExplorerException e )
		{
			throw new InternalServerErrorException( "Error serializing dashboard data!", e );
		}
	}
}
