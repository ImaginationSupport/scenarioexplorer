////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Constructor
 *
 * @constructor
 */
function NavHelper()
{
	this.JSP_PAGES = {
		Index : 'index.jsp',

		ProjectDetails : 'project-details.jsp',
		CreateUpdateProjectBasic : 'project-basic.jsp',
		UpdateProjectFeatures : 'project-features.jsp',
		UpdateProjectTimelineEvents : 'project-timeline-events.jsp',
		// UpdateProjectHistoricalDatasets : 'project-historical-datasets.jsp',
		UpdateProjectAccess : 'project-access.jsp',
		DeleteProject : 'project-delete.jsp',

		ProjectTemplate : 'project-template.jsp',

		View : 'view.jsp',
		CreateUpdateView : 'view-create-update.jsp',
		DeleteView : 'view-delete.jsp',

		CreateUpdateConditioningEvent : 'conditioning-event-create-update.jsp',
		DeleteConditioningEvent : 'conditioning-event-delete.jsp'
	};

	this.URI_PARAMETERS = {
		Project : 'project',
		ProjectTemplate : 'projectTemplate',
		View : 'view',
		TimelineEvent : 'timelineEvent',
		ConditioningEvent : 'conditioningEvent'
	};

	this.NotificationScopes = {
		Project : 'project',
		View : 'view',
		Feature : 'feature',
		ConditioningEvent : 'ConditioningEvent',
		TimelineEvent : 'TimelineEvent'
	};

	this.NotificationKeys = {
		ProjectAddFeatures : 'add-features',
		ProjectAddTimelineEvents : 'add-timeline-events',
		ProjectAddUsers : 'add-users',
		ProjectAddViews : 'add-views',

		ViewAddConditioningEvent : 'add-conditioning-event',

		TimelineEventNeverUsed : 'timeline-event-never-used',

		ConditioningEventNotInAnyView : 'conditioning-event-not-in-any-view',
		ConditioningEventPreconditionOnHold : 'conditioning-event-precondition-on-hold',
		ConditioningEventOutcomeEffectError : 'conditioning-event-outcome-effect-error',

		FeatureNoChoices : 'no-choices'
	};

	this.NotificationStates = {
		New : 'new',
		Acknowledged : 'Acknowledged'
	};

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the project id from the URI
 *
 * @returns {?string} the project id if it was present, otherwise null
 */
NavHelper.prototype.getProjectIdUriParameter = function()
{
	return this.getUriParameter( this.URI_PARAMETERS.Project );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the project template id from the URI
 *
 * @returns {?string} the project template id if it was present, otherwise null
 */
NavHelper.prototype.getProjectTemplateIdUriParameter = function()
{
	return this.getUriParameter( this.URI_PARAMETERS.ProjectTemplate );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the view id from the URI
 *
 * @returns {?string} the view id if it was present, otherwise null
 */
NavHelper.prototype.getViewIdUriParameter = function()
{
	return this.getUriParameter( this.URI_PARAMETERS.View );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the timeline event id from the URI
 *
 * @returns {?string} the timeline event id if it was present, otherwise null
 */
NavHelper.prototype.getTimelineEventIdUriParameter = function()
{
	return this.getUriParameter( this.URI_PARAMETERS.TimelineEvent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the conditioning event id from the URI
 *
 * @returns {?string} the conditioning event id if it was present, otherwise null
 */
NavHelper.prototype.getConditioningEventIdUriParameter = function()
{
	return this.getUriParameter( this.URI_PARAMETERS.ConditioningEvent );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the index page
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToIndex = function()
{
	return this.JSP_PAGES.Index;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the given project page
 *
 * @param {string} projectId - the project id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToProjectDetails = function( projectId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = encodeURIComponent( projectId );

	return this.JSP_PAGES.ProjectDetails + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the create project page
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToCreateProject = function()
{
	return this.JSP_PAGES.CreateUpdateProjectBasic;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to edit the given project basic details page
 *
 * @param {string} projectId - the project id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToUpdateProjectBasic = function( projectId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;

	return this.JSP_PAGES.CreateUpdateProjectBasic + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to edit the given project features page
 *
 * @param {string} projectId - the project id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToUpdateProjectFeatures = function( projectId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;

	return this.JSP_PAGES.UpdateProjectFeatures + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to edit the given project timeline events page
 *
 * @param {string} projectId - the project id
 * @param {?string} viewId - the view id, or null
 * @param {?string} timelineEventId - the timeline event id to edit, or null to not pre-select any event
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToUpdateProjectTimelineEvents = function( projectId, viewId, timelineEventId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;

	if( viewId )
	{
		parameters[ this.URI_PARAMETERS.View ] = viewId;
	}

	if( timelineEventId )
	{
		parameters[ this.URI_PARAMETERS.TimelineEvent ] = timelineEventId;
	}

	return this.JSP_PAGES.UpdateProjectTimelineEvents + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * Generates the URI to edit the given project historical datasets page
//  *
//  * @param {string} projectId - the project id
//  *
//  * @returns {string} the generated URI
//  */
// NavHelper.prototype.uriToUpdateProjectHistoricalDatasets = function( projectId )
// {
// };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to edit the given project access page
 *
 * @param {string} projectId - the project id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToUpdateProjectAccess = function( projectId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;

	return this.JSP_PAGES.UpdateProjectAccess + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the delete project page
 *
 * @param {string} projectId - the project id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToDeleteProject = function( projectId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;

	return this.JSP_PAGES.DeleteProject + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the create a project template based on a project page
 *
 * @param {string} projectId - the project id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToCreateProjectTemplate = function( projectId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = encodeURIComponent( projectId );

	return this.JSP_PAGES.ProjectTemplate + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the given update project template page
 *
 * @param {string} projectTemplateId - the project template id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToUpdateProjectTemplate = function( projectTemplateId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.ProjectTemplate ] = encodeURIComponent( projectTemplateId );

	return this.JSP_PAGES.ProjectTemplate + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the create view page
 *
 * @param {string} projectId - project id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToCreateView = function( projectId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;

	return this.JSP_PAGES.CreateUpdateView + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the view page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToView = function( projectId, viewId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;
	parameters[ this.URI_PARAMETERS.View ] = viewId;

	return this.JSP_PAGES.View + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the update view page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToUpdateView = function( projectId, viewId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;
	parameters[ this.URI_PARAMETERS.View ] = viewId;

	return this.JSP_PAGES.CreateUpdateView + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the delete view page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToDeleteView = function( projectId, viewId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;
	parameters[ this.URI_PARAMETERS.View ] = viewId;

	return this.JSP_PAGES.DeleteView + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the create conditioning event page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToCreateConditioningEvent = function( projectId, viewId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;
	parameters[ this.URI_PARAMETERS.View ] = viewId;

	return this.JSP_PAGES.CreateUpdateConditioningEvent + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the update conditioning event page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {string} conditioningEventId - the conditioning event id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToUpdateConditioningEvent = function( projectId, viewId, conditioningEventId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;
	parameters[ this.URI_PARAMETERS.View ] = viewId;
	parameters[ this.URI_PARAMETERS.ConditioningEvent ] = conditioningEventId;

	return this.JSP_PAGES.CreateUpdateConditioningEvent + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the delete conditioning event page
 *
 * @param {string} projectId - the project id
 * @param {string} conditioningEventId - the conditioning event id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToDeleteConditioningEvent = function( projectId, conditioningEventId )
{
	const parameters = {};

	parameters[ this.URI_PARAMETERS.Project ] = projectId;
	parameters[ this.URI_PARAMETERS.ConditioningEvent ] = conditioningEventId;

	return this.JSP_PAGES.DeleteConditioningEvent + this.encodeParameters( parameters );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the URI to the appropriate notification
 *
 * @param {Notification} notification - the notification
 * @param {string} projectId - the project id
 *
 * @returns {string} the generated URI
 */
NavHelper.prototype.uriToNotificationHandler = function( notification, projectId )
{
	if( notification.scope === this.NotificationScopes.Project )
	{
		switch( notification.key )
		{
			case this.NotificationKeys.ProjectAddFeatures:
				return this.uriToUpdateProjectFeatures( projectId );

			case this.NotificationKeys.ProjectAddTimelineEvents:
				return this.uriToUpdateProjectTimelineEvents( projectId, null, null );

			case this.NotificationKeys.ProjectAddUsers:
				return this.uriToUpdateProjectAccess( projectId );

			case this.NotificationKeys.ProjectAddViews:
				return this.uriToCreateView( projectId );

			default:
				console.warn( 'Unable to generate URI for notification (Unknown key in scope Project): %o', notification );
				return this.uriToProjectDetails( projectId );
		}
	}
	else if( notification.scope === this.NotificationScopes.View )
	{
		switch( notification.key )
		{
			case this.NotificationKeys.ViewAddConditioningEvent:
				return this.uriToView( projectId, notification.viewId );

			default:
				console.warn( 'Unable to generate URI for notification (Unknown key in scope Project): %o', notification );
				return this.uriToProjectDetails( projectId );
		}
	}
	else if( notification.scope === this.NotificationScopes.ConditioningEvent )
	{
		switch( notification.key )
		{
			case this.NotificationKeys.ConditioningEventNotInAnyView:
				return this.uriToProjectDetails( projectId );

			case this.NotificationKeys.ConditioningEventPreconditionOnHold:
				return this.uriToUpdateConditioningEvent( projectId, notification.viewId, notification.conditioningEventId );

			case this.NotificationKeys.ConditioningEventOutcomeEffectError:
				return this.uriToProjectDetails( projectId );

			default:
				console.warn( 'Unable to generate URI for notification (Unknown key in scope Project): %o', notification );
				return this.uriToProjectDetails( projectId );
		}
	}
	else if( notification.scope === this.NotificationScopes.TimelineEvent )
	{
		switch( notification.key )
		{
			case this.NotificationKeys.TimelineEventNeverUsed:
				return this.uriToUpdateProjectTimelineEvents( projectId, notification.viewId, notification.timelineEventId );

			default:
				console.warn( 'Unable to generate URI for notification (Unknown key in scope Project): %o', notification );
				return this.uriToProjectDetails( projectId );
		}
	}
	else if( notification.scope === this.NotificationScopes.Feature )
	{
		switch( notification.key )
		{
			case this.NotificationKeys.FeatureNoChoices:
				return this.uriToUpdateProjectFeatures( projectId ); // TODO should use the notification.featureId to go straight to the feature

			default:
				console.warn( 'Unable to generate URI for notification (Unknown key in scope Project): %o', notification );
				return this.uriToProjectDetails( projectId );
		}
	}
	else
	{
		console.warn( 'Unable to generate URI for notification (Unknown scope): %o', notification );

		return this.uriToProjectDetails( projectId );
	}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the index page
 *
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToIndex = function( e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToIndex();
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the create project page
 *
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToCreateProject = function( e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToCreateProject();
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the project details page
 *
 * @param {string} projectId - the project id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToProjectDetails = function( projectId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToProjectDetails( projectId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the update project basic details page
 *
 * @param {string} projectId - the project id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToUpdateProjectBasic = function( projectId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToUpdateProjectBasic( projectId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the update project features page
 *
 * @param {string} projectId - the project id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToUpdateProjectFeatures = function( projectId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToUpdateProjectFeatures( projectId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the update project timeline events page
 *
 * @param {string} projectId - the project id
 * @param {?string} viewId - the view id, or null
 * @param {?string} timelineEventId - the timeline event id to edit, or null
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToUpdateProjectTimelineEvents = function( projectId, viewId, timelineEventId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToUpdateProjectTimelineEvents( projectId, viewId, timelineEventId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the update project access page
 *
 * @param {string} projectId - the project id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToUpdateProjectAccess = function( projectId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToUpdateProjectAccess( projectId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the delete project page
 *
 * @param {string} projectId - the project id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToDeleteProject = function( projectId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToDeleteProject( projectId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the create project template from project page
 *
 * @param {string} projectId - the project id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToCreateProjectTemplate = function( projectId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToCreateProjectTemplate( projectId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the update project template page
 *
 * @param {string} projectTemplateId - the project id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToUpdateProjectTemplate = function( projectTemplateId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToUpdateProjectTemplate( projectTemplateId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the create view page
 *
 * @param {string} projectId - the project id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToCreateView = function( projectId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToCreateView( projectId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the view page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToView = function( projectId, viewId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToView( projectId, viewId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the update view page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToUpdateView = function( projectId, viewId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToUpdateView( projectId, viewId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the create conditioning event page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToCreateConditioningEvent = function( projectId, viewId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToCreateConditioningEvent( projectId, viewId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the create conditioning event page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {string} conditioningEventId - the conditioning event id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToUpdateConditioningEvent = function( projectId, viewId, conditioningEventId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToUpdateConditioningEvent( projectId, viewId, conditioningEventId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the create conditioning event page
 *
 * @param {string} projectId - the project id
 * @param {string} conditioningEventId - the conditioning event id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToDeleteConditioningEvent = function( projectId, conditioningEventId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToDeleteConditioningEvent( projectId, conditioningEventId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the delete view page
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToDeleteView = function( projectId, viewId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToDeleteView( projectId, viewId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Redirects the browser to the appropriate URI to the given notification
 *
 * @param {Notification} notification - the notification
 * @param {string} projectId - the project id
 * @param {jQuery.Event=} e - the jQuery event
 */
NavHelper.prototype.redirectToNotificationHandler = function( notification, projectId, e )
{
	if( e )
	{
		e.stopPropagation();
	}

	window.location = this.uriToNotificationHandler( notification, projectId );
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the notifications in the breadcrumbs
 *
 * @param {string} projectId
 * @param {Array< Notification >} notifications
 */
NavHelper.prototype.updateBreadcrumbNotifications = function( projectId, notifications )
{
	const holder = $( '#breadcrumb-notifications' ).empty();

	if( notifications == null || notifications.length === 0 )
	{
		holder.hide();
	}
	else
	{
		holder
			.removeClass( 'd-none' )
			.addClass( 'clickable' )
			.on( 'click', g_nav.redirectToProjectDetails.bind( g_nav, projectId ) )
			.text( 'Notification' + ( notifications.length === 1 ? '' : 's' ) + ': ' + notifications.length.toString() );

		let hoverText = '';
		for( let i = 0; i < notifications.length; ++i )
		{
			if( hoverText.length > 0 )
			{
				hoverText += '\n';
			}

			hoverText += notifications[ i ].description;
		}

		holder.attr( 'title', hoverText );

		holder.show();
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Encodes the given URI parameters
 *
 * @param {Object.< string, string >} parameters - the parameters to encode
 *
 * @returns {string}
 */
NavHelper.prototype.encodeParameters = function( parameters )
{
	let encoded = '';

	for( let key in parameters )
	{
		if( parameters.hasOwnProperty( key ) )
		{
			let value = parameters[ key ];

			if( value )
			{
				encoded += encoded.length === 0
					? '?'
					: '&';

				encoded += key + '=' + encodeURIComponent( value );
			}
		}
	}

	return encoded;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {string} parameterName - the parameter to get
 *
 * @returns {string|null}
 */
NavHelper.prototype.getUriParameter = function( parameterName )
{
	const currentUri = window.location.href.toString();

	// parse the URI
	const queryStringStart = currentUri.indexOf( '?' );
	let queryString = queryStringStart === -1
		? ''
		: currentUri.substr( queryStringStart + 1 );

	const queryStringEnd = queryString.indexOf( '#' );
	if( queryStringEnd > -1 )
	{
		queryString = queryString.substr( 0, queryStringEnd );
	}

	if( queryString.length > 0 )
	{
		const queryStringSplit = queryString.split( '&' );
		for( let i = 0; i < queryStringSplit.length; ++i )
		{
			const paramSplit = queryStringSplit[ i ].split( '=' );
			if( paramSplit.length === 2 )
			{
				if( paramSplit[ 0 ] === parameterName )
				{
					return paramSplit[ 1 ];
				}
			}
		}
	}

	return null;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
