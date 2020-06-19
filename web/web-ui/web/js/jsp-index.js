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
 * Holds the jQuery control for the loading div
 *
 * @type {?jQuery}
 */
let g_ctrlLoading = null;

/**
 * Holds the jQuery control for the dashboard
 *
 * @type {?jQuery}
 */
let g_ctrlDashboard = null;

/**
 * Holds the jQuery control for no access
 *
 * @type {?jQuery}
 */
let g_ctrlNoProjects = null;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the JSP to initialize the page
 *
 * @param {string} username
 */
function initJSP( username )
{
	g_ctrlLoading = $( '#loading-dashboard' );
	g_ctrlDashboard = $( '#dashboard' ).hide();
	g_ctrlNoProjects = $( '#no-projects' ).hide();

	g_api.listDashboard( listDashboardCallback );

	$( '#no-access-create-project' ).on( 'click', g_nav.redirectToCreateProject.bind( g_nav ) );
	$( '#new-project' ).on( 'click', g_nav.redirectToCreateProject.bind( g_nav ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the projects and views for the dashboard are loaded from the API
 *
 * @param {Array< Project >} projects - the access the user has access to
 * @param {Array< View >} views - the views the user has access to
 */
function listDashboardCallback( projects, views )
{
	g_ctrlLoading.hide();

	if( projects.length === 0 )
	{
		g_ctrlNoProjects.removeClass( 'd-none' ).show();
	}
	else
	{
		redrawDashboardTable( projects, views );
		g_ctrlDashboard.removeClass( 'd-none' ).show();
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates the access and views table
 *
 * @param {Array< Project >} projects - the access the user has access to
 * @param {Array< View >} views - the views the user has access to
 */
function redrawDashboardTable( projects, views )
{
	g_ctrlDashboard.empty();

	const viewTypeConverter = new ViewTypeConverter();

	for( let projectIndex = 0; projectIndex < projects.length; ++projectIndex )
	{
		const card = $( '<div/>' )
			.addClass( 'card' )
			.addClass( 'mb-2' )
			.appendTo( g_ctrlDashboard );
		const cardHeader = $( '<div/>' )
			.addClass( 'card-header' )
			.addClass( 'bg-dark' )
			.addClass( 'text-light' )
			.on( 'click', g_nav.redirectToProjectDetails.bind( g_nav, projects[ projectIndex ].id ) )
			.addClass( 'clickable' )
			.appendTo( card );
		g_taglib.generateIconWithText( g_taglib.Icons.PROJECT, projects[ projectIndex ].name, cardHeader, false )
			.addClass( 'align-middle' )
			.addClass( 'pt-3' );

		if( projects[ projectIndex ].notifications != null && projects[ projectIndex ].notifications.length > 0 )
		{
			let tooltipText = '';
			for( let notificationIndex = 0; notificationIndex < projects[ projectIndex ].notifications.length; ++notificationIndex )
			{
				if( tooltipText.length > 0 )
				{
					tooltipText += '\n';
				}

				tooltipText += ( notificationIndex + 1 ).toString() + ': ' + projects[ projectIndex ].notifications[ notificationIndex ].description;
			}

			$( '<span/>' )
				.text( projects[ projectIndex ].notifications.length.toString() )
				.addClass( 'badge' )
				.addClass( 'badge-primary' )
				.addClass( 'ml-1' )
				.prop( 'title', tooltipText )
				.appendTo( cardHeader );
		}

		const buttonsBar = $( '<div/>' )
			.addClass( 'float-right' )
			.appendTo( cardHeader );
		g_taglib.generateButton( null, 'New View', null, g_taglib.Icons.CREATE, buttonsBar )
			.on( 'click', g_nav.redirectToCreateView.bind( g_nav, projects[ projectIndex ].id ) );
		const cardBody = $( '<div/>' )
			.addClass( 'card-body' )
			.appendTo( card );

		let numProjectViews = 0;
		for( let viewIndex = 0; viewIndex < views.length; ++viewIndex )
		{
			if( views[ viewIndex ].projectId === projects[ projectIndex ].id )
			{
				++numProjectViews;

				let viewText = viewTypeConverter.getPrettyVersion( views[ viewIndex ].type ) + ': ' + views[ viewIndex ].name;
				if( views[ viewIndex ].description.length > 0 )
				{
					viewText += ' - ' + views[ viewIndex ].description;
				}

				const viewEntry = generateViewEntry( cardBody )
					.on( 'click', g_nav.redirectToView.bind( g_nav, projects[ projectIndex ].id, views[ viewIndex ].id ) );
				g_taglib.generateIconWithText( g_taglib.Icons.VIEW, viewText, viewEntry, false );
			}
		}

		if( numProjectViews === 0 )
		{
			const viewEntry = generateViewEntry( cardBody );

			$( '<span/>' )
				.text( 'No views for this project.' )
				.appendTo( viewEntry );

			viewEntry.removeClass( 'clickable' );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a view entry
 *
 * @param {jQuery} cardBody - the cardbody to append this entry
 *
 * @returns {jQuery}
 */
function generateViewEntry( cardBody )
{
	return $( '<div/>' )
		.addClass( 'bg-light' )
		.addClass( 'p-2' )
		.addClass( 'border' )
		.addClass( 'rounded' )
		.addClass( 'clickable' )
		.addClass( 'mb-2' )
		.appendTo( cardBody );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
