package com.imaginationsupport;

import com.imaginationsupport.data.ConditioningEvent;
import com.imaginationsupport.data.Project;
import com.imaginationsupport.data.TimelineEvent;
import com.imaginationsupport.data.User;
import com.imaginationsupport.data.api.Notification;
import com.imaginationsupport.data.features.FeatureMap;
import com.imaginationsupport.exceptions.GeneralScenarioExplorerException;
import com.imaginationsupport.exceptions.InvalidDataException;
import com.imaginationsupport.plugins.Precondition;
import com.imaginationsupport.plugins.preconditions.TimelineEventPrecondition;
import com.imaginationsupport.views.MasterView;
import com.imaginationsupport.views.View;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;

import java.util.*;

public class NotificationsManager
{
	protected static final Logger LOGGER = ImaginationSupportUtil.getBackendLogger();

	/**
	 * Updates the given project notifications
	 *
	 * @param projectId the id of the project to update
	 */
	static void updateProjectNotifications( final ObjectId projectId ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final ProjectManager projectManager = ProjectManager.getInstance();

		if( projectId == null )
		{
			throw new InvalidDataException( "ProjectId cannot be null!" );
		}

		final Project project = projectManager.getProject( projectId );
		final Collection< User > users = UserManager.getInstance().getUsers();
		final Collection< TimelineEvent > timelineEvents = projectManager.getTimelineEvents( projectId );
		final Collection< ConditioningEvent > conditioningEvents = projectManager.getConditioningEvents( project );
		final Collection< View > views = projectManager.getViews( projectId );

		final SortedSet< Notification > notifications = new TreeSet<>();

		// check features
		notifications.addAll( checkFeatures( project ) );

		// check features
		notifications.addAll( checkAccess( project, users ) );

		// check timeline events
		notifications.addAll( checkTimelineEvents( timelineEvents, conditioningEvents ) );

		// check conditioning events
		notifications.addAll( checkConditioningEvents( conditioningEvents, views ) );

		// check views
		notifications.addAll( checkViews( views ) );

		LOGGER.debug( String.format( "Updating project %s = %s to %d notifications", project.getId(), project.getName(), notifications.size() ) );

		project.setNotifications( notifications );
		project.save();

		return;
	}

	/**
	 * Generate notifications for the features in the given project
	 *
	 * @param project the project with features to check
	 *
	 * @return the notifications generated
	 */
	private static Collection< Notification > checkFeatures( final Project project ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		final List< Notification > notifications = new LinkedList<>();

		if( project.getFeatureMaps().isEmpty() )
		{
			notifications.add( new Notification(
				Notification.Scope.Project,
				Project.Notifications.ADD_FEATURES,
				"The project should add a feature." ) );
		}
		else
		{
			for( final FeatureMap featureMap : project.getFeatureMaps() )
			{
				notifications.addAll( featureMap.generateNotifications() );
			}
		}

		return notifications;
	}

	/**
	 * Generate notifications for the features in the given project
	 *
	 * @param project the project with features to check
	 *
	 * @return the notifications generated
	 */
	private static Collection< Notification > checkAccess( final Project project, final Collection< User > users ) throws InvalidDataException
	{
		final List< Notification > notifications = new LinkedList<>();

		int numUsers = 0;
		for( final User user : users )
		{
			if( user.getAccess().contains( project.getId() ) )
			{
				++numUsers;
			}
		}

		if( numUsers == 0 || numUsers == 1 )
		{
			notifications.add( new Notification(
				Notification.Scope.Project,
				Project.Notifications.ADD_USERS,
				"The project should consider adding more users." ) );
		}

		return notifications;
	}

	/**
	 * Generate notifications for the given timeline events
	 *
	 * @param timelineEvents     the timeline events to check
	 * @param conditioningEvents the conditioning events in the project
	 *
	 * @return the notifications generated
	 */
	private static Collection< Notification > checkTimelineEvents(
		final Collection< TimelineEvent > timelineEvents,
		final Collection< ConditioningEvent > conditioningEvents ) throws InvalidDataException, GeneralScenarioExplorerException
	{
		final List< Notification > notifications = new LinkedList<>();

		if( timelineEvents.isEmpty() )
		{
			notifications.add( new Notification(
				Notification.Scope.Project,
				Project.Notifications.ADD_TIMELINE_EVENTS,
				"The project should consider adding timeline events." ) );
		}
		else
		{
			for( final TimelineEvent timelineEvent : timelineEvents )
			{
				boolean isUsed = false;

				for( final ConditioningEvent conditioningEvent : conditioningEvents )
				{
					for( final Precondition precondition : conditioningEvent.getPreconditions() )
					{
						if( precondition instanceof TimelineEventPrecondition )
						{
							final TimelineEventPrecondition timelineEventPrecondition = (TimelineEventPrecondition)precondition;
							if( timelineEventPrecondition.getTimelineEvent().equals( timelineEvent ) )
							{
								isUsed = true;
								break;
							}
						}
					}
				}

				if( !isUsed )
				{
					notifications.add( new Notification(
						Notification.Scope.TimelineEvent,
						TimelineEvent.Notifications.NEVER_USED,
						String.format( "The timeline event \"%s\" is never used.", timelineEvent.getLabel() ) ) );
				}
			}
		}

		return notifications;
	}

	/**
	 * Generate notifications for the given conditioning events
	 *
	 * @param conditioningEvents the conditioning events to check
	 * @param views              the views in the project
	 *
	 * @return the notifications generated
	 */
	private static Collection< Notification > checkConditioningEvents(
		final Collection< ConditioningEvent > conditioningEvents,
		final Collection< View > views ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		final List< Notification > notifications = new LinkedList<>();

		for( final ConditioningEvent conditioningEvent : conditioningEvents )
		{
			notifications.addAll( conditioningEvent.generateNotifications() );

			// now check to see if this conditioning event is assigned in any view
			boolean found = false;
			for( final View view : views )
			{
				if( view.getAssigned().contains( conditioningEvent.getId() ) )
				{
					found = true;
					break;
				}
			}

			if( !found )
			{
				notifications.add( new Notification(
					Notification.Scope.ConditioningEvent,
					ConditioningEvent.Notifications.NOT_IN_ANY_VIEW,
					String.format( "Conditioning event \"%s\" is not assigned in any view.", conditioningEvent.getLabel() ),
					null,
					null,
					conditioningEvent.getId(),
					null
				) );
			}
		}

		return notifications;
	}

	/**
	 * Generate notifications for the given views
	 *
	 * @param views the views to check
	 *
	 * @return the notifications generated
	 */
	private static Collection< Notification > checkViews( final Collection< View > views ) throws GeneralScenarioExplorerException, InvalidDataException
	{
		final List< Notification > notifications = new LinkedList<>();

		// Note: there will likely already be a master view, so handle that one appropriately

		if( views.isEmpty() || ( views.size() == 1 && views.iterator().next() instanceof MasterView ) )
		{
			notifications.add( new Notification(
				Notification.Scope.Project,
				Project.Notifications.ADD_VIEWS,
				"The project should add a view." ) );
		}
		else
		{
			for( final View view : views )
			{
				notifications.addAll( view.generateNotifications() );
			}
		}

		return notifications;
	}
}
