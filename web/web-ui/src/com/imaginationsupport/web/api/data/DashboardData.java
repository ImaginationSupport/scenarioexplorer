package com.imaginationsupport.web.api.data;

import com.imaginationsupport.annotations.RestApiFieldInfo;
import com.imaginationsupport.annotations.RestApiObjectInfo;
import com.imaginationsupport.annotations.RestApiHandlerInfo;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.api.ApiObject;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.helpers.JsonHelper;
import com.imaginationsupport.views.View;
import org.json.JSONObject;

import java.util.Set;

@RestApiObjectInfo( definitionName = "DashboardData", tagName = RestApiHandlerInfo.CategoryNames.Dashboard, description = "Dashboard Data" )
public class DashboardData implements ApiObject
{
	@SuppressWarnings( "WeakerAccess" )
	public static final String JSON_KEY_PROJECTS = "projects";

	@SuppressWarnings( "WeakerAccess" )
	public static final String JSON_KEY_VIEWS = "views";

	@RestApiFieldInfo( jsonField = JSON_KEY_PROJECTS, description = "The list of projects" )
	private final Set< Project > mProjects;

	@RestApiFieldInfo( jsonField = JSON_KEY_VIEWS, description = "The list of views" )
	private final Set< View > mViews;

	public DashboardData( final Set< Project > projects, final Set< View > views )
	{
		mProjects = projects;
		mViews = views;

		return;
	}

	@Override
	public JSONObject toJSON() throws GeneralScenarioExplorerException
	{
		final JSONObject json = new JSONObject();

		try
		{
			JsonHelper.put( json, JSON_KEY_PROJECTS, mProjects );
			JsonHelper.put( json, JSON_KEY_VIEWS, mViews );
		}
		catch( InvalidDataException e )
		{
			throw new GeneralScenarioExplorerException( "Error serializing dashboard data!", e );
		}

		return json;
	}
}
