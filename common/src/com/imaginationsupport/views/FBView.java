package com.imaginationsupport.views;

import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.exceptions.InvalidDataException;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.mongodb.morphia.annotations.Entity;

import java.util.HashSet;
import java.util.Set;

@Entity( value = "View" )
public class FBView extends View
{
	@SuppressWarnings( "WeakerAccess" )
	private abstract static class Notifications
	{
		public static final String ADD_CONDITIONING_EVENT = "add-conditioning-event";
	}

	public FBView()
	{
		super();
		return;
	}

	public FBView( ObjectId projectId, String label, String description )
	{
		super( projectId, label, description );
		return;
	}

	public FBView( final JSONObject source ) throws InvalidDataException
	{
		super( source );
		return;
	}

	@Override
	public JSONObject getStatsJSON()
	{
		return new JSONObject();
	}

	@Override
	public Set< Notification > generateNotifications() throws InvalidDataException
	{
		final Set< Notification > notifications = new HashSet<>();

		if( this.getAssigned().isEmpty() )
		{
			notifications.add( new Notification(
				Notification.Scope.View,
				Notifications.ADD_CONDITIONING_EVENT,
				String.format( "Futures building view \"%s\" should add a conditioning event.", this.getLabel() ),
				this.getId(),
				null,
				null,
				null ) );
		}

		return notifications;
	}
}
