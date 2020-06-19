package com.imaginationsupport.views;

import com.imaginationsupport.data.api.Notification;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Entity;

import java.util.Collections;
import java.util.Set;

@Entity( value = "View" )
public class MasterView extends View
{

	public MasterView()
	{
	}

	public MasterView( ObjectId projectId, String label, String description )
	{
		super( projectId, label, description );
	}

	@Override
	public JSONObject getStatsJSON()
	{
		return null;
	}

	@Override
	public Set< Notification > generateNotifications()
	{
		return Collections.emptySet();
	}
}
