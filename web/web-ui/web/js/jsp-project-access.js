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
 *
 * @type {TagLib}
 */
const g_taglib = new TagLib();

/**
 * Holds the existing project id, or null to create a new project
 *
 * @type {?string}
 */
let g_projectId = null;

/**
 * Holds the existing project instance
 *
 * @type {?Project}
 */
let g_originalProject = null;

/**
 * Holds the working copy of the project
 *
 * @type {?Project}
 */
let g_workingProject = null;

/**
 * Holds the original list of users
 *
 * @type {?Array< User >}
 */
let g_originalUsers = null;

/**
 * Holds the working array of users
 *
 * @type {?Array< User >}
 */
let g_workingUsers = null;

/**
 * Holds the save queue countdown remaining count - When running the save, we need to wait for all the async requests to complete before forwarding the browser, or they can get
 * aborted and lost
 *
 * @type {number}
 */
let g_saveCountdownQueueRemaining = 0;

/**
 * Holds the jQuery control for the alert box
 *
 * @type {?jQuery}
 */
let g_ctrlAlert = null;

/**
 * Holds the jQuery control for the save button
 *
 * @type {?jQuery}
 */
let g_ctrlSaveButton = null;

/**
 * Holds the jQuery control for the cancel button
 *
 * @type {?jQuery}
 */
let g_ctrlCancelButton = null;

/**
 * Holds the jQuery control for the users table
 *
 * @type {?jQuery}
 */
let g_ctrlUsersTable = null;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the JSP to initialize the page
 *
 * @param {string} username
 */
function initJSP( username )
{
	g_projectId = g_nav.getProjectIdUriParameter();

	g_api.getProject( g_projectId, getProjectCallback );
	g_api.listUsers( listUsersCallback );

	g_ctrlSaveButton = $( '#button-save' )
		.on( 'click', runSave.bind( this ) );
	g_ctrlCancelButton = $( '#button-cancel' )
		.on( 'click', g_nav.redirectToProjectDetails.bind( g_nav, g_projectId ) );

	g_ctrlUsersTable = $( '#users' );

	// run this now to show the "loading..." message
	populateUsersTable();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API gets the existing project
 *
 * @param {Project} project - the project
 */
function getProjectCallback( project )
{
	g_workingProject = project;
	g_originalProject = /** @type {Project} */( g_util.cloneSingleObject( project ) );

	populateUsersTable();

	// update the notifications in the breadcrumbs
	g_nav.updateBreadcrumbNotifications( project.id, project.notifications );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API gets the list of users
 *
 * @param {Array< User >} users - the list of users
 */
function listUsersCallback( users )
{
	g_workingUsers = users;
	g_originalUsers = /** @type {Array< User >} */( g_util.cloneArray( users ) );

	populateUsersTable();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the users table
 */
function populateUsersTable()
{
	g_ctrlUsersTable.empty();

	const table = g_taglib.generateTable( null, g_ctrlUsersTable )
		.addClass( 'table-striped' )
		.addClass( 'table-bordered' );
	const thead = g_taglib.generateTableHead( table );
	const tbody = g_taglib.generateTableBody( null, table );

	// generate the header row
	let tr = g_taglib.generateTableRow( thead );
	g_taglib.generateTableColumnHeader( 'Name', tr );
	g_taglib.generateTableColumnHeader( 'Username', tr );
	g_taglib.generateTableColumnHeader( 'Access', tr );

	if( g_workingUsers === null || g_workingProject === null )
	{
		tr = g_taglib.generateTableRow( thead );
		g_taglib.generateTableCell( 'Loading, please wait...', tr )
			.attr( 'colspan', 3 );

		return;
	}

	for( let i = 0; i < g_workingUsers.length; ++i )
	{
		const isOwner = g_workingProject.owner === g_workingUsers[ i ].id;
		const isMember = !isOwner && g_util.existsInArray( g_workingUsers[ i ].access, g_projectId );

		tr = g_taglib.generateTableRow( tbody );

		g_taglib.generateTableCell( g_workingUsers[ i ].fullName, tr );
		g_taglib.generateTableCell( g_workingUsers[ i ].userName, tr );
		const actionsCell = g_taglib.generateTableCell( null, tr );

		if( isOwner )
		{
			g_taglib.generateIconWithText( g_taglib.Icons.OWNER, 'Owner', actionsCell, false );
		}
		else
		{
			const buttonBar = g_taglib.generateButtonBar( actionsCell );
			g_taglib.generateButtonBarButton( 'Member', isMember, setMember.bind( this, i ), buttonBar );
			g_taglib.generateButtonBarButton( 'None', !isOwner && !isMember, setNone.bind( this, i ), buttonBar );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function setMember( index, e )
{
	e.stopPropagation();

	// remove the user as an owner
	// TODO finish!

	// add the user to the members list
	if( !g_util.existsInArray( g_workingUsers[ index ].access, g_projectId ) )
	{
		g_workingUsers[ index ].access.push( g_projectId );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function setNone( index, e )
{
	e.stopPropagation();

	// remove the user as an owner
	// TODO finish!

	// remove the user as a member
	g_util.removeFromArray( g_workingUsers[ index ].access, g_projectId );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Save button
 *
 * @private
 */
function runSave()
{
	// TODO we should also save the project if we changed the owners

	g_saveCountdownQueueRemaining = 0;

	for( let i = 0; i < g_workingUsers.length; ++i )
	{
		if( g_workingUsers[ i ].access.join( ',' ) !== g_originalUsers[ i ].access.join( ',' ) )
		{
			g_api.updateUser( g_workingUsers[ i ], saveCallback );
			++g_saveCountdownQueueRemaining;
		}
	}

	if( g_saveCountdownQueueRemaining === 0 )
	{
		g_nav.redirectToProjectDetails( g_projectId );
	}

	g_ctrlCancelButton.prop( 'disabled', true );
	g_ctrlSaveButton.prop( 'disabled', true );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API completes the save call and lets us track if all of the calls are complete
 */
function saveCallback()
{
	--g_saveCountdownQueueRemaining;

	// console.log( g_saveCountdownQueueRemaining );

	if( g_saveCountdownQueueRemaining === 0 )
	{
		g_nav.redirectToProjectDetails( g_projectId );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
