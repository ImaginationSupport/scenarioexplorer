////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Holds the Scenario Explorer API library instance
 *
 * @type {ScenarioExplorerAPI}
 */
const g_api = new ScenarioExplorerAPI( false, '/scenarioexplorer' );

/**
 * Holds the Util library instance
 *
 * @type {Util}
 */
const g_util = new Util();

/**
 * Holds the Nav Helper instance
 *
 * @type {NavHelper}
 */
const g_nav = new NavHelper();

/**
 * Holds the TagLib library instance
 * @type {TagLib}
 */
const g_taglib = new TagLib();

/**
 * Holds the jQuery pane for the loading div
 *
 * @type {?jQuery}
 */
let g_ctrlLoadingPane = null;

/**
 * Holds the jQuery pane for the project details
 *
 * @type {?jQuery}
 */
let g_ctrlProjectDetailsPane = null;

/**
 * Holds the jQuery pane for clone project
 *
 * @type {?jQuery}
 */
let g_ctrlCloneProjectPane = null;

/**
 * Holds the jQuery pane for the delete confirm controls
 *
 * @type {?jQuery}
 */
let g_ctrlDeleteConfirmPane = null;

/**
 * Holds the jQuery control for the project basic details
 *
 * @type {?jQuery}
 */
let g_ctrlProjectBasicDetails = null;

/**
 * Holds the jQuery control for the project views
 *
 * @type {?jQuery}
 */
let g_ctrlProjectViews = null;

/**
 * Holds the jQuery control for the project features
 *
 * @type {?jQuery}
 */
let g_ctrlProjectFeatures = null;

/**
 * Holds the jQuery control for the project timeline events
 *
 * @type {?jQuery}
 */
let g_ctrlProjectTimelineEvents = null;

/**
 * Holds the jQuery control for the project access
 *
 * @type {?jQuery}
 */
let g_ctrlProjectAccess = null;

/**
 * Holds the jQuery control for the project templates
 *
 * @type {?jQuery}
 */
let g_ctrlProjectTemplates = null;

/**
 * Holds the jQuery control for the help sidebar notifications div
 *
 * @type {?jQuery}
 */
let g_ctrlHelpSideBarNotifications = null;

/**
 * Holds the jQuery control for the name in the delete confirm pane
 *
 * @type {?jQuery}
 */
let g_ctrlDeleteConfirmName = null;

/**
 * Holds the jQuery control for the confirm button in the delete confirm pane
 *
 * @type {?jQuery}
 */
let g_ctrlDeleteConfirm = null;

/**
 * Holds the jQuery control for the cancel button in the delete confirm pane
 *
 * @type {?jQuery}
 */
let g_ctrlDeleteCancel = null;

/**
 * Holds the project id from the URI
 *
 * @type {?string}
 */
let g_projectId = null;

/**
 * Holds the User instance
 *
 * @private
 * @type {?User}
 */
let g_user = null;

/**
 * Holds the project
 *
 * @type {?Project}
 */
let g_project = null;

/**
 * Holds the views
 *
 * @type {?Array< View >}
 */
let g_views = null;

/**
 * Holds the features
 *
 * @type {?Array< Feature >}
 */
let g_features = null;

/**
 * @private
 *
 * @type {?Array< TimelineEvent >}
 */
let g_timelineEvents = null;

/**
 * @private
 *
 * @type {?Array< User >}
 */
let g_users = null;

/**
 * Holds the feature types
 *
 * @type {?Array< FeatureTypePlugin >}
 */
let g_featureTypes = null;

/**
 * Holds the projectors
 *
 * @type {?Array< ProjectorPlugin >}
 */
let g_projectors = null;

/**
 * Holds the project templates
 *
 * @type {?Array< ProjectTemplate >}
 */
let g_projectTemplates = null;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the JSP to initialize the page
 *
 * @param {string} username
 */
function initJSP( username )
{
	g_ctrlLoadingPane = $( '#loading-project' );
	g_ctrlProjectDetailsPane = $( '#project-details' ).removeClass( 'd-none' ).hide();
	g_ctrlCloneProjectPane = $( '#clone-project' ).removeClass( 'd-none' ).hide();
	g_ctrlDeleteConfirmPane = $( '#delete-confirm' ).removeClass( 'd-none' ).hide();

	g_ctrlProjectBasicDetails = $( '#project-basic-details' );
	g_ctrlProjectViews = $( '#project-views' );
	g_ctrlProjectFeatures = $( '#project-features' );
	g_ctrlProjectTimelineEvents = $( '#project-timeline-events' );
	g_ctrlProjectAccess = $( '#project-access' );
	g_ctrlProjectTemplates = $( '#project-templates' );
	g_ctrlHelpSideBarNotifications = $( '#help-sidebar-notifications' );

	g_ctrlDeleteConfirmName = $( '#delete-confirm-name' );
	g_ctrlDeleteConfirm = $( '#delete-confirm-delete' );
	g_ctrlDeleteCancel = $( '#delete-confirm-cancel' )
		.on( 'click', onDeleteCancel.bind( this ) );

	// attach the export button handlers
	$( '#export-file-download' ).on( 'click', this.exportFileDownload.bind( this ) );
	$( '#export-clone-project' ).on( 'click', this.exportCloneProject.bind( this ) );
	$( '#export-create-template' ).on( 'click', this.exportCreateProjectTemplate.bind( this ) );

	g_projectId = g_nav.getProjectIdUriParameter();

	g_api.getUser( username, getCurrentUserCallback.bind( this ) );
	g_api.getProject( g_projectId, getProjectCallback.bind( this ) );
	g_api.listViews( g_projectId, listViewsCallback.bind( this ) );
	g_api.listFeatures( g_projectId, listFeaturesCallback.bind( this ) );
	g_api.listFeatureTypes( listFeatureTypesCallback.bind( this ) );
	g_api.listProjectors( listProjectorsCallback.bind( this ) );
	g_api.listTimelineEvents( g_projectId, listTimelineEventsCallback.bind( this ) );
	g_api.listUsers( listUsersCallback.bind( this ) );
	g_api.listProjectTemplates( listProjectTemplatesCallback.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {User} user
 */
function getCurrentUserCallback( user )
{
	g_user = user;

	// populate the basic details
	populateBasic();

	// populate the timeline events
	populateTimelineEvents();

	// populate the project templates
	populateProjectTemplates();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the project is loaded from the API
 *
 * @private
 *
 * @param {Project} project - this project
 */
function getProjectCallback( project )
{
	g_project = project;

	// populate the basic details
	populateBasic();

	// also try to redraw the access list
	populateAccess();

	// update the notifications
	g_taglib.updateSideBarNotifications( g_projectId, g_project.notifications );

	// update the notifications in the breadcrumbs
	g_nav.updateBreadcrumbNotifications( g_project.id, project.notifications );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the views are loaded from the API
 *
 * @private
 *
 * @param {Array< View >} views - the views for this project
 */
function listViewsCallback( views )
{
	g_views = views;

	// populate the views
	populateViews();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the features are loaded from the API
 *
 * @private
 *
 * @param {Array< Feature >} features - the features for this project
 */
function listFeaturesCallback( features )
{
	g_features = features;

	// populate the features
	populateFeatures();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the timeline events are loaded from the API
 *
 * @private
 *
 * @param {Array< TimelineEvent >} timelineEvents - the timeline events for this project
 */
function listTimelineEventsCallback( timelineEvents )
{
	g_timelineEvents = timelineEvents;

	// populate the timeline events
	populateTimelineEvents();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the users are loaded from the API
 *
 * @private
 *
 * @param {Array< User >} users - the list of users
 */
function listUsersCallback( users )
{
	g_users = users;

	// populate the access
	populateAccess();

	// also try to repopulate the basic details
	populateBasic();

	// also try to populate the project templates
	populateProjectTemplates();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the feature types are loaded from the API
 *
 * @private
 *
 * @param {Array< FeatureTypePlugin >} featureTypes
 */
function listFeatureTypesCallback( featureTypes )
{
	g_featureTypes = featureTypes;

	populateFeatures();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the projectors are loaded from the API
 *
 * @private
 *
 * @param {Array< ProjectorPlugin >} projectors
 */
function listProjectorsCallback( projectors )
{
	g_projectors = projectors;

	populateFeatures();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the project templates are loaded from the API
 *
 * @private
 *
 * @param {Array< ProjectTemplate >} projectTemplates
 */
function listProjectTemplatesCallback( projectTemplates )
{
	g_projectTemplates = projectTemplates;

	populateProjectTemplates();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function exportFileDownload()
{
	g_api.exportProject( g_projectId );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function exportCloneProject()
{
	g_ctrlProjectDetailsPane.hide();
	g_ctrlCloneProjectPane.show();

	g_api.cloneProject( g_projectId, exportCloneProjectCallback.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {Project} clonedProject
 */
function exportCloneProjectCallback( clonedProject )
{
	g_nav.redirectToProjectDetails( clonedProject.id );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function exportCreateProjectTemplate()
{
	g_nav.redirectToCreateProjectTemplate( g_projectId );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the basic details pane
 *
 * @private
 */
function populateBasic()
{
	if( g_project === null || g_user === null )
	{
		return;
	}

	$( '#page-header' ).text( 'Project: ' + g_project.name );

	g_ctrlLoadingPane.hide();

	g_ctrlProjectBasicDetails.empty();

	const table = g_taglib.generateTable( null, g_ctrlProjectBasicDetails )
		.addClass( 'table-striped' )
		.addClass( 'table-bordered' );
	const tbody = g_taglib.generateTableBody( null, table );

	// name
	let tr = g_taglib.generateTableRow( tbody );
	g_taglib.generateTableCell( 'Name', tr );
	g_taglib.generateTableCell( g_project.name, tr );

	// description
	tr = g_taglib.generateTableRow( tbody );
	g_taglib.generateTableCell( 'Description', tr );
	g_taglib.generateTableCell( g_project.description, tr );

	// start
	tr = g_taglib.generateTableRow( tbody );
	g_taglib.generateTableCell( 'Start Date', tr );
	g_taglib.generateTableCell( g_util.formatDate( g_project.start, g_user.getDateFormatPreference() ), tr );

	// end
	tr = g_taglib.generateTableRow( tbody );
	g_taglib.generateTableCell( 'End Date', tr );
	g_taglib.generateTableCell( g_util.formatDate( g_project.end, g_user.getDateFormatPreference() ), tr );

	// increment
	tr = g_taglib.generateTableRow( tbody );
	g_taglib.generateTableCell( 'Increment', tr );
	let incrementPretty = null;
	switch( g_project.increment )
	{
		case 1:
			incrementPretty = 'Day';
			break;

		case 7:
			incrementPretty = 'Week';
			break;

		case 30:
			incrementPretty = 'Month';
			break;

		case 365:
			incrementPretty = 'Year';
			break;

		default:
			incrementPretty = 'Unknown: ' + g_project.increment.toString() + ' days';
			break;
	}
	g_taglib.generateTableCell( incrementPretty, tr );

	// owner
	tr = g_taglib.generateTableRow( tbody );
	g_taglib.generateTableCell( 'Owner', tr );
	const owner = getUser( g_project.owner );
	g_taglib.generateTableCell( owner === null ? g_project.owner : ( owner.fullName + ' (' + owner.userName + ')' ), tr );

	// created on
	tr = g_taglib.generateTableRow( tbody );
	g_taglib.generateTableCell( 'Created', tr );
	g_taglib.generateTableCell( g_util.formatDate( g_project.createdOn, g_user.getDateTimeFormatPreference() ), tr );

	// last edited
	tr = g_taglib.generateTableRow( tbody );
	g_taglib.generateTableCell( 'Last Edited', tr );
	// g_taglib.generateTableCell( g_util.formatDate(g_project.lastEditOn, g_user.getDateTimeFormatPreference() ) + ' by ' +g_project.lastEditBy, tr );
	g_taglib.generateTableCell( g_util.formatDate( g_project.lastEditOn, g_user.getDateTimeFormatPreference() ), tr ); // TODO for now, ignore the lastEditBy

	// edit button
	const buttonBar = $( '<div/>' )
		.addClass( 'text-right' )
		.appendTo( g_ctrlProjectBasicDetails );
	g_taglib.generateButton( null, 'Edit', null, g_taglib.Icons.UPDATE, buttonBar )
		.on( 'click', g_nav.redirectToUpdateProjectBasic.bind( g_nav, g_projectId ) );

	g_ctrlProjectDetailsPane.show();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the views pane
 *
 * @private
 */
function populateViews()
{
	g_ctrlProjectViews.empty();

	const table = g_taglib.generateTable( null, g_ctrlProjectViews )
		.addClass( 'table-striped' )
		.addClass( 'table-bordered' );
	const thead = g_taglib.generateTableHead( table );
	const tbody = g_taglib.generateTableBody( null, table );

	// generate the header row
	let tr = g_taglib.generateTableRow( thead );
	g_taglib.generateTableColumnHeader( 'Name', tr );
	g_taglib.generateTableColumnHeader( 'Type of Analysis', tr );
	g_taglib.generateTableColumnHeader( 'Description', tr );
	g_taglib.generateTableColumnHeader( 'Actions', tr );

	const viewTypeConverter = new ViewTypeConverter();
	for( let i = 0; i < g_views.length; ++i )
	{
		tr = g_taglib.generateTableRow( tbody )
			.addClass( 'clickable' )
			.on( 'click', g_nav.redirectToView.bind( g_nav, g_projectId, g_views[ i ].id ) );

		// view name
		let td = g_taglib.generateTableCell( null, tr );
		g_taglib.generateIconWithText( g_taglib.Icons.VIEW + ' text-primary', g_views[ i ].name, td, true );

		// view type
		g_taglib.generateTableCell( viewTypeConverter.getPrettyVersion( g_views[ i ].type ), tr );

		// description
		g_taglib.generateTableCell( g_views[ i ].description, tr );

		td = g_taglib.generateTableCell( null, tr )
			.addClass( 'text-right' );
		g_taglib.generateButton( null, 'Edit', 'ml-2', g_taglib.Icons.UPDATE, td )
			.on( 'click', g_nav.redirectToUpdateView.bind( g_nav, g_projectId, g_views[ i ].id ) );
		g_taglib.generateButton( null, 'Delete', 'ml-2', g_taglib.Icons.DELETE, td )
			.on( 'click', onDelete.bind( this, true, g_views[ i ] ) );
	}

	// if there aren't any, show a nice message
	if( g_views.length === 0 )
	{
		tr = g_taglib.generateTableRow( tbody );
		g_taglib.generateTableCell( 'No Views', tr )
			.attr( 'colspan', 4 );
	}

	// new view button
	const buttonBar = $( '<div/>' )
		.addClass( 'text-right' )
		.appendTo( g_ctrlProjectViews );
	g_taglib.generateButton( null, 'New View', null, g_taglib.Icons.CREATE, buttonBar )
		.on( 'click', g_nav.redirectToCreateView.bind( g_nav, g_projectId ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the features pane
 *
 * @private
 */
function populateFeatures()
{
	// we need the features, feature types and projectors, so make sure they are all loaded
	if( g_featureTypes === null || g_features === null || g_projectors === null )
	{
		return;
	}

	g_ctrlProjectFeatures.empty();

	const table = g_taglib.generateTable( null, g_ctrlProjectFeatures )
		.addClass( 'table-striped' )
		.addClass( 'table-bordered' );
	const thead = g_taglib.generateTableHead( table );
	const tbody = g_taglib.generateTableBody( null, table );

	// generate the header row
	let tr = g_taglib.generateTableRow( thead );
	g_taglib.generateTableColumnHeader( 'Name', tr );
	g_taglib.generateTableColumnHeader( 'Type', tr );
	g_taglib.generateTableColumnHeader( 'Projector', tr );

	for( let i = 0; i < g_features.length; ++i )
	{
		tr = g_taglib.generateTableRow( tbody );

		const featureType = getFeatureType( g_features[ i ].featureType );
		const projector = !g_features[ i ].projectorId
			? null
			: getProjector( g_features[ i ].projectorId );

		g_taglib.generateTableCell( g_features[ i ].name, tr );
		g_taglib.generateTableCell( featureType === null ? '(Unknown)' : featureType.name, tr );
		g_taglib.generateTableCell( g_features[ i ].projectorId === null ? '(none)' : ( projector === null ? '(unknown)' : projector.name ), tr );
	}

	// if there aren't any, show a nice message
	if( g_features.length === 0 )
	{
		tr = g_taglib.generateTableRow( tbody );
		g_taglib.generateTableCell( 'No Features', tr )
			.attr( 'colspan', 3 );
	}

	// edit button
	const buttonBar = $( '<div/>' )
		.addClass( 'text-right' )
		.appendTo( g_ctrlProjectFeatures );
	g_taglib.generateButton( null, 'Edit', null, g_taglib.Icons.UPDATE, buttonBar )
		.on( 'click', g_nav.redirectToUpdateProjectFeatures.bind( g_nav, g_projectId ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the timeline events pane
 *
 * @private
 */
function populateTimelineEvents()
{
	if( g_user === null || g_timelineEvents === null )
	{
		return;
	}

	g_ctrlProjectTimelineEvents.empty();

	const table = g_taglib.generateTable( null, g_ctrlProjectTimelineEvents )
		.addClass( 'table-striped' )
		.addClass( 'table-bordered' );
	const thead = g_taglib.generateTableHead( table );
	const tbody = g_taglib.generateTableBody( null, table );

	// generate the header row
	let tr = g_taglib.generateTableRow( thead );
	g_taglib.generateTableColumnHeader( 'Name', tr );
	g_taglib.generateTableColumnHeader( 'Start', tr );
	g_taglib.generateTableColumnHeader( 'End', tr );
	g_taglib.generateTableColumnHeader( 'URL', tr );

	for( let i = 0; i < g_timelineEvents.length; ++i )
	{
		tr = g_taglib.generateTableRow( tbody );

		g_taglib.generateTableCell( g_timelineEvents[ i ].name, tr );
		g_taglib.generateTableCell( g_util.formatDate( g_timelineEvents[ i ].start, g_user.getDateFormatPreference() ), tr );
		g_taglib.generateTableCell( g_util.formatDate( g_timelineEvents[ i ].end, g_user.getDateFormatPreference() ), tr );
		let td = g_taglib.generateTableCell( null, tr );
		if( g_timelineEvents[ i ].url && g_timelineEvents[ i ].url.length > 0 )
		{
			if( g_util.verifyUri( g_timelineEvents[ i ].url ) )
			{
				// uri, so add as a link
				g_taglib.generateLink( null, g_timelineEvents[ i ].url, g_timelineEvents[ i ].url, true, td );
			}
			else
			{
				// something else, so just show the text
				$( '<span/>' )
					.text( g_timelineEvents[ i ].url )
					.appendTo( td );
			}
		}
		else
		{
			td.text( '(none)' );
		}
	}

	// if there aren't any, show a nice message
	if( g_timelineEvents.length === 0 )
	{
		tr = g_taglib.generateTableRow( tbody );
		g_taglib.generateTableCell( 'No Timeline Events', tr )
			.attr( 'colspan', 4 );
	}

	// edit button
	const buttonBar = $( '<div/>' )
		.addClass( 'text-right' )
		.appendTo( g_ctrlProjectTimelineEvents );
	g_taglib.generateButton( null, 'Edit', null, g_taglib.Icons.UPDATE, buttonBar )
		.on( 'click', g_nav.redirectToUpdateProjectTimelineEvents.bind( g_nav, g_projectId, null, null ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the user access pane
 *
 * @private
 */
function populateAccess()
{
	// we need the project too, so if it's not back yet, bail, and it call this again
	if( g_project === null || g_users === null )
	{
		return;
	}

	g_ctrlProjectAccess.empty();

	const table = g_taglib.generateTable( null, g_ctrlProjectAccess )
		.addClass( 'table-striped' )
		.addClass( 'table-bordered' );
	const thead = g_taglib.generateTableHead( table );
	const tbody = g_taglib.generateTableBody( null, table );

	// generate the header row
	let tr = g_taglib.generateTableRow( thead );
	g_taglib.generateTableColumnHeader( 'Name', tr );
	g_taglib.generateTableColumnHeader( 'Username', tr );
	g_taglib.generateTableColumnHeader( 'Access Level', tr );

	// generate the rows
	for( let i = 0; i < g_users.length; ++i )
	{
		if( g_util.existsInArray( g_users[ i ].access, g_projectId ) )
		{
			tr = g_taglib.generateTableRow( tbody );

			g_taglib.generateTableCell( g_users[ i ].fullName, tr );
			g_taglib.generateTableCell( g_users[ i ].userName, tr );
			g_taglib.generateTableCell( g_project.owner === g_users[ i ].id ? 'Owner' : 'Member', tr )
		}
	}

	// edit button
	const buttonBar = $( '<div/>' )
		.addClass( 'text-right' )
		.appendTo( g_ctrlProjectAccess );
	g_taglib.generateButton( null, 'Edit', null, g_taglib.Icons.UPDATE, buttonBar )
		.on( 'click', g_nav.redirectToUpdateProjectAccess.bind( g_nav, g_projectId ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the project templates pane
 *
 * @private
 */
function populateProjectTemplates()
{
	// we need the project too, so if it's not back yet, bail, and it call this again
	if( g_user === null || g_users === null || g_projectTemplates === null )
	{
		return;
	}

	g_ctrlProjectTemplates.empty();

	const table = g_taglib.generateTable( null, g_ctrlProjectTemplates )
		.addClass( 'table-striped' )
		.addClass( 'table-bordered' );
	const thead = g_taglib.generateTableHead( table );
	const tbody = g_taglib.generateTableBody( null, table );

	// generate the header row
	let tr = g_taglib.generateTableRow( thead );
	g_taglib.generateTableColumnHeader( 'Name', tr );
	g_taglib.generateTableColumnHeader( 'Created', tr );
	g_taglib.generateTableColumnHeader( 'Creator', tr );
	g_taglib.generateTableColumnHeader( 'Description', tr );
	g_taglib.generateTableColumnHeader( 'Actions', tr );

	// generate the rows
	let found = false;
	for( let i = 0; i < g_projectTemplates.length; ++i )
	{
		if( g_projectTemplates[ i ].sourceProjectId === g_projectId )
		{
			tr = g_taglib.generateTableRow( tbody );

			const creator = getUser( g_projectTemplates[ i ].creatorId );

			g_taglib.generateTableCell( g_taglib.generateIconWithText( g_taglib.Icons.PROJECT, g_projectTemplates[ i ].name, null, false ), tr );
			g_taglib.generateTableCell( g_util.formatDate( g_projectTemplates[ i ].createdOn, g_user.getDateFormatPreference() ), tr );
			g_taglib.generateTableCell( creator === null ? 'Unknown user' : creator.fullName, tr );
			g_taglib.generateTableCell( g_projectTemplates[ i ].description, tr );

			const td = g_taglib.generateTableCell( null, tr )
				.addClass( 'text-right' );
			g_taglib.generateButton( null, 'Edit', 'ml-2', g_taglib.Icons.UPDATE, td )
				.on( 'click', g_nav.redirectToUpdateProjectTemplate.bind( g_nav, g_projectTemplates[ i ].id ) );
			g_taglib.generateButton( null, 'Delete', 'ml-2', g_taglib.Icons.DELETE, td )
				.on( 'click', onDelete.bind( this, false, g_projectTemplates[ i ] ) );

			found = true;
		}
	}
	if( !found )
	{
		tr = g_taglib.generateTableRow( tbody );

		g_taglib.generateTableCell( 'No templates', tr ).prop( 'colspan', 5 );
	}

	// edit button
	// const buttonBar = $( '<div/>' )
	// 	.addClass( 'text-right' )
	// 	.appendTo( g_ctrlProjectTemplates );
	// g_taglib.generateButton( null, 'Edit', null, g_taglib.Icons.UPDATE, buttonBar )
	// 	.on( 'click', g_nav.redirectToUpdateProjectAccess.bind( g_nav, g_projectId ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the button to delete the view
 *
 * @private
 * @param {boolean} isView - true if this is a view, false if it's a project template
 * @param {View|ProjectTemplate} item
 * @param {jQuery.Event} e
 */
function onDelete( isView, item, e )
{
	e.stopPropagation();

	g_ctrlProjectDetailsPane.hide();
	g_ctrlDeleteConfirmPane.show();

	g_ctrlDeleteConfirm
		.off( 'click' );

	if( isView )
	{
		const view = /** {View} */( item );

		g_ctrlDeleteConfirmName.text( 'view ' + ( view.name.length === 0 ? '(no name)' : ( '"' + view.name + '"' ) ) );

		g_ctrlDeleteConfirm.on( 'click', onDeleteConfirm.bind( this, true, view.id ) );
	}
	else
	{
		const projectTemplate = /** {ProjectTemplate} */( item );

		g_ctrlDeleteConfirmName.text( 'project template ' + ( projectTemplate.name.length === 0 ? '(no name)' : ( '"' + projectTemplate.name + '"' ) ) );

		g_ctrlDeleteConfirm.on( 'click', onDeleteConfirm.bind( this, false, projectTemplate.id ) );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the button to cancel deleting
 *
 * @private
 */
function onDeleteCancel()
{
	g_ctrlDeleteConfirmPane.hide();
	g_ctrlProjectDetailsPane.show();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the button to confirm deleting the view
 *
 * @private
 * @param {boolean} isView
 * @param {string} id
 */
function onDeleteConfirm( isView, id )
{
	if( isView )
	{
		g_api.deleteView( g_projectId, id, this.onDeleteConfirmCallback.bind( this ) );
	}
	else
	{
		g_api.deleteProjectTemplate( id, this.onDeleteConfirmCallback.bind( this ) );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function onDeleteConfirmCallback()
{
	g_nav.redirectToProjectDetails( g_projectId );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function populateNotificationsHelpSideBar()
{
	g_ctrlHelpSideBarNotifications.empty();

	if( g_project.notifications.length > 0 )
	{
		$( '<hr/>' )
			.addClass( 'mt-4' )
			.addClass( 'mb-4' )
			.appendTo( g_ctrlHelpSideBarNotifications );

		$( '<div/>' )
			.text( 'Notifications:' )
			.addClass( 'font-weight-bold' )
			.appendTo( g_ctrlHelpSideBarNotifications );

		for( let i = 0; i < g_project.notifications.length; ++i )
		{
			let holder = $( '<div/>' )
				.addClass( 'clickable' )
				.on( 'click', g_nav.redirectToNotificationHandler.bind( g_nav, g_project.notifications[ i ], g_project.id ) )
				.appendTo( g_ctrlHelpSideBarNotifications );
			g_taglib.generateNotification( g_project.notifications[ i ], holder );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the user with the given id
 *
 * @private
 *
 * @param {string} id - the id of the user
 *
 * @returns {?User}
 */
function getUser( id )
{
	if( id === null )
	{
		return null;
	}

	if( g_users === null )
	{
		return null;
	}

	for( let i = 0; i < g_users.length; ++i )
	{
		if( g_users[ i ].id === id )
		{
			return g_users[ i ];
		}
	}

	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the feature type plugin with the given id
 *
 * @private
 *
 * @param {string} id - the id of the feature type
 *
 * @returns {?FeatureTypePlugin}
 */
function getFeatureType( id )
{
	if( id === null )
	{
		return null;
	}

	if( g_featureTypes === null )
	{
		return null;
	}

	for( let i = 0; i < g_featureTypes.length; ++i )
	{
		if( g_featureTypes[ i ].id === id )
		{
			return g_featureTypes[ i ];
		}
	}

	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the projector plugin with the given id
 *
 * @private
 *
 * @param {string} id - the id of the projector
 *
 * @returns {?ProjectorPlugin}
 */
function getProjector( id )
{
	if( id === null )
	{
		return null;
	}

	if( g_projectors === null )
	{
		return null;
	}

	for( let i = 0; i < g_projectors.length; ++i )
	{
		if( g_projectors[ i ].id === id )
		{
			return g_projectors[ i ];
		}
	}

	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
