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
 * Holds the PluginHelpers library instance
 *
 * @type {PluginHelpers}
 */
const g_pluginHelpers = new PluginHelpers();

/**
 * Holds the PluginTagLib library instance
 *
 * @type {PluginTagLib}
 */
const g_pluginTagLib = new PluginTagLib();

/**
 * Holds the ViewCanvas library instance
 *
 * @type {ViewCanvas}
 */
const g_canvas = new ViewCanvas();

/**
 * Holds the project id
 *
 * @type {?string}
 */
let g_projectId = null;

/**
 * Holds the view id
 *
 * @type {?string}
 */
let g_viewId = null;

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
 * Holds this view
 *
 * @type {?View}
 */
let g_view = null;

/**
 * Holds the list of views
 *
 * @type {Array< View >}
 */
let g_views = null;

/**
 * Holds the features
 *
 * @type {?Array< Feature >}
 */
let g_features = null;

/**
 * Holds the timeline events
 *
 * @type {?Array< TimelineEvent >}
 */
let g_timelineEvents = null;

/**
 * Holds the conditioning events
 *
 * @type {?Array< ConditioningEvent >}
 */
let g_conditioningEvents = null;

/**
 * Holds the feature type plugins
 *
 * @type {Array< FeatureTypePlugin >}
 */
let g_featureTypePlugins = [];

/**
 * Holds the precondition plugins
 *
 * @type {Array< PreconditionPlugin >}
 */
const g_preconditionPlugins = [];

/**
 * Holds the outcome effect plugins
 *
 * @type {Array< OutcomeEffectPlugin >}
 */
const g_outcomeEffectPlugins = [];

/**
 * Holds the projector plugins
 *
 * @type {Array< ProjectorPlugin >}
 */
const g_projectorPlugins = [];

/**
 * Holds the states
 *
 * @type {?Array< State >}
 */
let g_states = null;

let g_ctrlViewCanvasHolder = null;
let g_ctrlViewCanvasSideBar = null;
let g_ctrlViewStats = null;

let g_ctrlStatesTab = null;
let g_ctrlStatesTabBody = null;
let g_ctrlConditioningEventsTab = null;
let g_ctrlConditioningEventsTabBody = null;
let g_ctrlTimelineEventsTab = null;
let g_ctrlTimelineEventsTabBody = null;

let g_ctrlStatesList = null;
let g_ctrlStatesListTBody = null;
let g_ctrlStatesListSearchBox = null;
let g_ctrlStateDetails = null;
let g_ctrlStateDetailsTBody = null;

let g_ctrlConditioningEventsList = null;
let g_ctrlConditioningEventsListSearchBox = null;
let g_ctrlConditioningEventsListTBody = null;
let g_ctrlConditioningEventDetails = null;
let g_ctrlConditioningEventDetailsTBody = null;
let g_ctrlConditioningEventDetailsAssignButton = null;
let g_ctrlConditioningEventDetailsDeleteButton = null;
let g_ctrlConditioningEventDetailsEditButton = null;

let g_ctrlTimelineEventsList = null;
let g_ctrlTimelineEventsListSearchBox = null;
let g_ctrlTimelineEventsListTBody = null;
let g_ctrlTimelineEventDetails = null;
let g_ctrlTimelineEventDetailsTBody = null;
let g_ctrlTimelineEventDetailsEditButton = null;

let g_statsDataTable = null;
let g_updateStatsFilterSaveDelayTimerId = null;

const DEFAULT_SENSITIVITY = 0.0;
const DEFAULT_SPECIFICITY = 0.0;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the JSP to initialize the page
 *
 * @param {string} username
 */
function initJSP( username )
{
	g_projectId = g_nav.getProjectIdUriParameter();
	if( !g_projectId )
	{
		g_util.showError( 'Missing URL parameter: project' );
		return;
	}

	g_viewId = g_nav.getViewIdUriParameter();
	if( !g_viewId )
	{
		g_util.showError( 'Missing URL parameter: view' );
		return;
	}

	g_ctrlViewCanvasHolder = $( '#view-canvas-holder' )
		.removeClass( 'd-none' )
		.hide();
	g_ctrlViewCanvasSideBar = $( '#view-canvas-side-bar' )
		.removeClass( 'd-none' )
		.hide();
	g_ctrlViewStats = $( '#stats' )
		.removeClass( 'd-none' )
		.hide();

	// fetch all the data from the API
	g_api.getUser( username, getCurrentUserCallback.bind( this ) );
	g_api.getProject( g_projectId, getProjectCallback.bind( this ) );
	g_api.getView( g_projectId, g_viewId, getViewCallback.bind( this ) );

	$.fn.dataTable.ext.search.push( this.runStatsFilterCallback.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the canvas and side-bar layout
 *
 * @private
 */
function initCanvasLayout()
{
	g_ctrlViewCanvasHolder.show();
	g_ctrlViewCanvasSideBar.show();

	$( document.body ).addClass( 'no-overflow' );

	g_canvas.init(
		$( '#view-canvas-holder' ),
		onSelectNode.bind( this ),
		onSelectTimelineEvent.bind( this ),
		onDeselect.bind( this ) );

	// tabs
	g_ctrlStatesTab = $( '#states-tab' );
	g_ctrlStatesTabBody = $( '#states-tab-body' );
	g_ctrlConditioningEventsTab = $( '#conditioning-events-tab' );
	g_ctrlConditioningEventsTabBody = $( '#conditioning-events-tab-body' );
	g_ctrlTimelineEventsTab = $( '#timeline-events-tab' );
	g_ctrlTimelineEventsTabBody = $( '#timeline-events-tab-body' );

	// states tab body
	g_ctrlStatesList = $( '#states-list' );
	g_ctrlStatesListTBody = $( '#states-tbody' );
	g_ctrlStatesListSearchBox = $( '#states-search-box' )
		.on( 'input propertychange paste', statesSearchCallback.bind( this ) );
	g_ctrlStateDetails = $( '#state-details' )
		.hide()
		.removeClass( 'd-none' );
	g_ctrlStateDetailsTBody = $( '#state-details-tbody' );
	$( '#state-details-back-to-list' )
		.on( 'click', populateStatesListTableFull.bind( this ) );

	// conditioning events tab body
	g_ctrlConditioningEventsList = $( '#conditioning-events-list' );
	g_ctrlConditioningEventsListTBody = $( '#conditioning-events-tbody' );
	g_ctrlConditioningEventsListSearchBox = $( '#conditioning-events-search-box' )
		.on( 'input propertychange paste', conditioningEventsSearchCallback.bind( this ) );
	g_ctrlConditioningEventDetails = $( '#conditioning-event-details' )
		.hide()
		.removeClass( 'd-none' );
	g_ctrlConditioningEventDetailsTBody = $( '#conditioning-event-details-tbody' );
	g_ctrlConditioningEventDetailsAssignButton = $( '#conditioning-event-details-assign' );
	g_ctrlConditioningEventDetailsDeleteButton = $( '#conditioning-event-details-delete' );
	g_ctrlConditioningEventDetailsEditButton = $( '#conditioning-event-details-edit' );
	$( '#conditioning-event-details-back-to-list' )
		.on( 'click', populateConditioningEventsListTableFull.bind( this ) );
	$( '#conditioning-event-new-button' )
		.on( 'click', g_nav.redirectToCreateConditioningEvent.bind( g_nav, g_projectId, g_viewId ) );

	// timeline events tab body
	g_ctrlTimelineEventsList = $( '#timeline-events-list' );
	g_ctrlTimelineEventsListTBody = $( '#timeline-events-tbody' );
	g_ctrlTimelineEventsListSearchBox = $( '#timeline-events-search-box' )
		.on( 'input propertychange paste', timelineEventsSearchCallback.bind( this ) );
	g_ctrlTimelineEventDetails = $( '#timeline-event-details' )
		.hide()
		.removeClass( 'd-none' );
	g_ctrlTimelineEventDetailsTBody = $( '#timeline-event-details-tbody' );
	g_ctrlTimelineEventDetailsEditButton = $( '#timeline-event-details-edit' );
	$( '#timeline-event-details-back-to-list' )
		.on( 'click', populateTimelineEventsListTableFull.bind( this ) );
	$( '#timeline-event-new-button' )
		.on( 'click', g_nav.redirectToUpdateProjectTimelineEvents.bind( g_nav, g_projectId, g_viewId, null ) );

	// populate all the tables now to show the "loading..." messages
	populateStatesListTable( null, false );
	populateTimelineEventsListTable( null, false );
	populateConditioningEventsListTable( null, false );

	g_api.listViews( g_projectId, listViewsCallback.bind( this ) );
	g_api.listStates( g_projectId, g_viewId, listStatesCallback.bind( this ) );
	g_api.listConditioningEvents( g_projectId, listConditioningEventsCallback.bind( this ) );
	g_api.listTimelineEvents( g_projectId, listTimelineEventsCallback.bind( this ) );
	g_api.listFeatures( g_projectId, listFeaturesCallback.bind( this ) );
	g_api.getViewTree( g_projectId, g_viewId, getViewTreeCallback.bind( this ) );
	g_pluginTagLib.fetchFeatureTypePlugins( g_featureTypePlugins, true, null, null, g_projectId, g_viewId, null );
	g_pluginTagLib.fetchPreconditionPlugins( g_preconditionPlugins, true, null, null, g_projectId, g_viewId, null );
	g_pluginTagLib.fetchOutcomeEffectPlugins( g_outcomeEffectPlugins, true, null, null, g_projectId, g_viewId, null );
	g_pluginTagLib.fetchProjectorPlugins( g_projectorPlugins, true, null, null, g_projectId, g_viewId, null );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the stats-only layout
 *
 * @private
 */
function initStatsLayout()
{
	g_ctrlViewStats
		.empty()
		.show();

	$( '<span/>' )
		.text( 'Loading, please wait...' )
		.appendTo( g_ctrlViewStats );

	g_api.getViewStats( g_projectId, g_viewId, getViewStatsCallback.bind( this ) );
	g_api.listConditioningEvents( g_projectId, listConditioningEventsCallback.bind( this ) );

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

	// make sure the filter values are set, or use the defaults
	const viewPreferences = /** @type {{sensitivityFilter:?number, specificityFilter:?number}} */( g_user.getViewPreferences( g_projectId, g_viewId ) );
	if( !( 'sensitivityFilter' in viewPreferences ) )
	{
		viewPreferences.sensitivityFilter = DEFAULT_SENSITIVITY;
	}
	if( !( 'specificityFilter' in viewPreferences ) )
	{
		viewPreferences.specificityFilter = DEFAULT_SPECIFICITY;
	}
	g_user.setViewPreferences( g_projectId, g_viewId, viewPreferences );

	// if this call comes back after the getView on a smart query view, we need to update the smart view stats
	if( g_view !== null && g_view.type === ViewType.SMART_QUERY )
	{
		populateSmartQueryStats();
	}

	if( g_user != null )
	{
		// NOTE: at the moment, we are only showing the date, not the time
		g_canvas.setDateTimeFormat( g_user.getDateFormatPreference() );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the project is loaded
 *
 * @private
 * @param {Project} project - the view
 */
function getProjectCallback( project )
{
	g_project = project;

	g_canvas.setBaseViewBounds( project.start, project.end );

	// update the notifications in the breadcrumbs
	g_nav.updateBreadcrumbNotifications( project.id, project.notifications );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the view is loaded
 *
 * @private
 * @param {View} view - the view
 */
function getViewCallback( view )
{
	g_view = view;

	switch( view.type )
	{
		case ViewType.FUTURES_BUILDING:
			initCanvasLayout();
			break;

		case ViewType.SMART_QUERY:
			initStatsLayout();
			break;

		case ViewType.WHAT_IF:
			break;

		case ViewType.EXTREME_STATE:
			break;

		case ViewType.UNKNOWN:
		default:
			g_util.showError( 'Unknown view type: ' + view.type );
			return;
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the views are loaded
 *
 * @private
 *
 * @param {Array< View >} views
 */
function listViewsCallback( views )
{
	g_views = views;

	populateConditioningEventsListTableFull();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the view tree is loaded
 *
 * @param {ViewTree} tree
 */
function getViewTreeCallback( tree )
{
	g_canvas.updateView( tree );

	g_view.rootNodeId = tree.rootNodeId;
	g_view.treeNodes = tree.treeNodes;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the view tree is loaded
 *
 * @param {Object} stats
 */
function getViewStatsCallback( stats )
{
	g_view.stats = stats;

	switch( g_view.type )
	{
		case ViewType.FUTURES_BUILDING:
			g_util.showError( 'Futures Building view stats not supported!' );
			break;

		case ViewType.SMART_QUERY:
			populateSmartQueryStats();
			break;

		case ViewType.WHAT_IF:
			g_util.showError( 'What-If view stats not supported!' );
			break;

		case ViewType.EXTREME_STATE:
			g_util.showError( 'Extreme state view stats not supported!' );
			break;

		case ViewType.UNKNOWN:
		default:
			g_util.showError( 'Unknown view type: ' + g_view.type );
			return;
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the states are loaded
 *
 * @private
 * @param {Array< State >} states - the states
 */
function listStatesCallback( states )
{
	g_states = states;

	switch( g_view.type )
	{
		case ViewType.FUTURES_BUILDING:
			populateStatesListTableFull();
			break;

		case ViewType.SMART_QUERY:
			populateSmartQueryStats();
			break;

		case ViewType.WHAT_IF:
			g_util.showError( 'What-If view stats not supported!' );
			break;

		case ViewType.EXTREME_STATE:
			g_util.showError( 'Extreme state view stats not supported!' );
			break;

		case ViewType.UNKNOWN:
		default:
			g_util.showError( 'Unknown view type: ' + g_view.type );
			return;
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the features loaded
 *
 * @private
 * @param {Array< Feature >} features
 */
function listFeaturesCallback( features )
{
	g_features = features;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the conditioning events are loaded
 *
 * @private
 * @param conditioningEvents
 */
function listConditioningEventsCallback( conditioningEvents )
{
	g_conditioningEvents = conditioningEvents;

	switch( g_view.type )
	{
		case ViewType.FUTURES_BUILDING:
			populateConditioningEventsListTableFull();
			break;

		case ViewType.SMART_QUERY:
			populateSmartQueryStats();
			break;

		case ViewType.WHAT_IF:
			g_util.showError( 'What-If view stats not supported!' );
			break;

		case ViewType.EXTREME_STATE:
			g_util.showError( 'Extreme state view stats not supported!' );
			break;

		case ViewType.UNKNOWN:
		default:
			g_util.showError( 'Unknown view type: ' + g_view.type );
			return;
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the timeline events are loaded
 *
 * @private
 * @param {Array< TimelineEvent >} timelineEvents
 */
function listTimelineEventsCallback( timelineEvents )
{
	g_timelineEvents = timelineEvents;

	populateTimelineEventsListTableFull();

	g_canvas.updateTimelineEvents( timelineEvents );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the canvas when the user selects a node
 *
 * @private
 * @param {TreeNode} treeNode
 */
function onSelectNode( treeNode )
{
	switch( treeNode.type )
	{
		case TreeNodeType.STATE:
			showStateDetails( getState( treeNode.id ), false );
			break;

		case TreeNodeType.CONDITIONING_EVENT:
			showConditioningEventDetails( getConditioningEvent( treeNode.dbId ), false );
			break;

		case TreeNodeType.OUTCOME:
			break;

		default:
			g_util.showError( 'Unknown node type:' + treeNode.type );
			break;
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the canvas when the user selects a timeline event
 *
 * @private
 * @param {TimelineEvent} timelineEvent
 */
function onSelectTimelineEvent( timelineEvent )
{
	showTimelineEventDetails( timelineEvent );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the canvas when the user selects off anything and clears the selection
 *
 * @private
 */
function onDeselect()
{
	if( g_ctrlStatesTabBody.is( ':visible' ) )
	{
		populateStatesListTableFull();
	}
	else if( g_ctrlConditioningEventsTabBody.is( ':visible' ) )
	{
		populateConditioningEventsListTableFull();
	}
	else if( g_ctrlTimelineEventsTabBody.is( ':visible' ) )
	{
		populateTimelineEventsListTableFull();
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the states table with the fill list
 *
 * @private
 */
function populateStatesListTableFull()
{
	populateStatesListTable( g_states, false );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the conditioning events with the given list
 *
 * @private
 *
 * @param {Array< State >} states - the states to populate
 * @param {boolean} isFiltered - True if this is a filtered list, false if it is the full list
 */
function populateStatesListTable( states, isFiltered )
{
	if( g_user === null )
	{
		return;
	}

	g_ctrlStatesListTBody.empty();

	g_ctrlStateDetails.hide();
	g_ctrlStatesList.show();

	let tr;

	if( states === null )
	{
		tr = g_taglib.generateTableRow( g_ctrlStatesListTBody );

		g_taglib.generateTableCell( 'Loading, please wait...', tr )
			.attr( 'colspan', 4 );

		return;
	}
	else if( states.length === 0 )
	{
		tr = g_taglib.generateTableRow( g_ctrlStatesListTBody );

		g_taglib.generateTableCell( isFiltered ? 'No matches' : 'No states', tr )
			.attr( 'colspan', 4 );

		return;
	}

	for( let i = 0; i < states.length; ++i )
	{
		tr = g_taglib.generateTableRow( g_ctrlStatesListTBody );

		// name column
		g_taglib.generateTableCell( states[ i ].name, tr )
			.addClass( 'clickable' )
			.on( 'mouseover', highlightState.bind( this, states[ i ].id ) )
			.on( 'click', showStateDetails.bind( this, states[ i ], true ) );

		// start date column
		g_taglib.generateTableCell( g_util.formatDate( states[ i ].start, g_user.getDateFormatPreference() ), tr )
			.addClass( 'clickable' )
			.on( 'mouseover', highlightState.bind( this, states[ i ].id ) )
			.on( 'click', showStateDetails.bind( this, states[ i ], true ) );

		// end date column
		g_taglib.generateTableCell( g_util.formatDate( states[ i ].end, g_user.getDateFormatPreference() ), tr )
			.addClass( 'clickable' )
			.on( 'mouseover', highlightState.bind( this, states[ i ].id ) )
			.on( 'click', showStateDetails.bind( this, states[ i ], true ) );
	}

	// show the totals row
	tr = g_taglib.generateTableRow( g_ctrlStatesListTBody );
	g_taglib.generateTableCell( ( isFiltered ? 'Matches: ' : 'Total: ' ) + states.length.toString() + ' state' + ( states.length === 1 ? '' : 's' ), tr )
		.attr( 'colspan', 3 )
		.addClass( 'text-right' );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates and shows the timeline event details tab section for the given timeline event
 *
 * @private
 *
 * @param {State} state - the state to display
 * @param {boolean} updateViewCanvas - true to tell the canvas to highlight this entry
 */
function showStateDetails( state, updateViewCanvas )
{
	if( g_features === null || state === null )
	{
		return;
	}

	g_ctrlStateDetailsTBody.empty();

	for( let i = 0; i < g_features.length; ++i )
	{
		let tr = g_taglib.generateTableRow( g_ctrlStateDetailsTBody );
		g_taglib.generateTableCell( g_features[ i ].name, tr );

		if( g_features[ i ].id in state.features )
		{
			const valueRaw = state.features[ g_features[ i ].id ];
			let featureTypePlugin = getFeatureTypePlugin( g_features[ i ].featureType );

			let valueFormatted;
			if( featureTypePlugin === null )
			{
				valueFormatted = '(unknown feature type: ' + g_features[ i ].featureType + ') ' + valueRaw;
			}
			else
			{
				valueFormatted = g_pluginTagLib.generateCurrentValueSummary( featureTypePlugin, g_features[ i ].config, valueRaw )
			}
			g_taglib.generateTableCell( valueFormatted, tr );
		}
		else
		{
			g_taglib.generateTableCell( 'Feature value missing!', tr );
		}
	}

	g_ctrlStatesList.hide();
	g_ctrlStateDetails.show();

	g_ctrlStatesTab.tab( 'show' );

	if( updateViewCanvas )
	{
		g_canvas.setSelectedElement( state.id );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user updates the timeline events search box
 *
 * @private
 */
function statesSearchCallback()
{
	if( g_states === null )
	{
		return;
	}

	const searchText = g_ctrlStatesListSearchBox.val().toString().trim().toLowerCase();

	if( searchText.length === 0 )
	{
		populateStatesListTableFull();
		return;
	}

	const matches = [];
	for( let i = 0; i < g_states.length; ++i )
	{
		// check the name
		if( g_states[ i ].name.toLowerCase().indexOf( searchText ) > -1 )
		{
			matches.push( g_states[ i ] );
		}
	}

	populateStatesListTable( matches, true );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user hovers over an item in the right side bar
 *
 * @private
 *
 * @param {string} stateId
 */
function highlightState( stateId )
{
	g_canvas.setHoveredElements( [ stateId ] );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the conditioning events with the full list
 *
 * @private
 */
function populateConditioningEventsListTableFull()
{
	populateConditioningEventsListTable( g_conditioningEvents, false );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the conditioning events with the given list
 *
 * @private
 *
 * @param {Array< ConditioningEvent >} conditioningEvents - the list to populate
 * @param {boolean} isFiltered - True if this is a filtered list, false if it is the full list
 */
function populateConditioningEventsListTable( conditioningEvents, isFiltered )
{
	g_ctrlConditioningEventsListTBody.empty();

	g_ctrlConditioningEventDetails.hide();
	g_ctrlConditioningEventsList.show();

	let tr;

	if( conditioningEvents === null )
	{
		tr = g_taglib.generateTableRow( g_ctrlConditioningEventsListTBody );

		g_taglib.generateTableCell( 'Loading, please wait...', tr )
			.attr( 'colspan', 2 );

		return;
	}
	else if( conditioningEvents.length === 0 )
	{
		tr = g_taglib.generateTableRow( g_ctrlConditioningEventsListTBody );

		g_taglib.generateTableCell( isFiltered ? 'No matches' : 'No conditioning events', tr )
			.attr( 'colspan', 2 );

		return;
	}

	for( let i = 0; i < conditioningEvents.length; ++i )
	{
		tr = g_taglib.generateTableRow( g_ctrlConditioningEventsListTBody );

		// name column
		g_taglib.generateTableCell( conditioningEvents[ i ].name, tr )
			.addClass( 'clickable' )
			.on( 'mouseover', highlightConditioningEvent.bind( this, conditioningEvents[ i ] ) )
			.on( 'click', showConditioningEventDetails.bind( this, conditioningEvents[ i ] ) );

		// view column
		const td = g_taglib.generateTableCell( null, tr )
			.addClass( 'clickable' )
			.on( 'mouseover', highlightConditioningEvent.bind( this, conditioningEvents[ i ] ) )
			.on( 'click', showConditioningEventDetails.bind( this, conditioningEvents[ i ] ) );

		if( conditioningEvents[ i ].originViewId === g_viewId )
		{
			g_taglib.generateIconWithText( g_taglib.Icons.VIEW + ' text-primary', 'This view', td, true )
		}
		else
		{
			const view = getView( conditioningEvents[ i ].originViewId );
			if( view !== null )
			{
				if( conditioningEvents[ i ].assignedToView( g_view ) )
				{
					g_taglib.generateIconWithText( g_taglib.Icons.ASSIGNED + ' text-primary', 'Assigned from ' + view.name, td, true )
				}
				else
				{
					g_taglib.generateIconWithText( g_taglib.Icons.CREATE + ' text-primary', view.name, td, true )
				}
			}
		}
	}

	// show the totals row
	tr = g_taglib.generateTableRow( g_ctrlConditioningEventsListTBody );
	g_taglib.generateTableCell( ( isFiltered ? 'Matches: ' : 'Total: ' ) + conditioningEvents.length.toString() + ' conditioning event' + ( conditioningEvents.length === 1 ? '' : 's' ), tr )
		.attr( 'colspan', 2 )
		.addClass( 'text-right' );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates and shows the conditioning event details tab section for the given conditioning event
 *
 * @private
 *
 * @param {ConditioningEvent} conditioningEvent - the conditioning event to display
 * @param {boolean} updateViewCanvas - true to tell the canvas to highlight this entry
 */
function showConditioningEventDetails( conditioningEvent, updateViewCanvas )
{
	if( g_view === null )
	{
		return;
	}

	let isConditioningEventInThisView = conditioningEvent.originViewId === g_viewId;
	for( let i = 0; i < g_view.assigned.length && !isConditioningEventInThisView; ++i )
	{
		if( g_view.assigned[ i ] === conditioningEvent.id )
		{
			isConditioningEventInThisView = true;
		}
	}

	g_ctrlConditioningEventDetailsTBody.empty();

	// name
	let tr = g_taglib.generateTableRow( g_ctrlConditioningEventDetailsTBody );
	g_taglib.generateTableCell( 'Name', tr );
	g_taglib.generateTableCell( conditioningEvent.name, tr );

	// description
	tr = g_taglib.generateTableRow( g_ctrlConditioningEventDetailsTBody );
	g_taglib.generateTableCell( 'Description', tr );
	g_taglib.generateTableCell( conditioningEvent.description, tr );

	// origin view
	const view = getView( conditioningEvent.originViewId );
	tr = g_taglib.generateTableRow( g_ctrlConditioningEventDetailsTBody );
	g_taglib.generateTableCell( 'Origin View', tr );
	g_taglib.generateTableCell( view === null ? null : g_taglib.generateIconWithText( g_taglib.Icons.VIEW + ' text-primary', view.name, null, false ), tr )
		.addClass( 'clickable' )
		.on( 'click', g_nav.redirectToView.bind( g_nav, g_projectId, conditioningEvent.originViewId ) );

	// preconditions
	tr = g_taglib.generateTableRow( g_ctrlConditioningEventDetailsTBody );
	g_taglib.generateTableCell( 'Preconditions', tr );
	let td = g_taglib.generateTableCell( null, tr );
	if( conditioningEvent.preconditions.length === 0 )
	{
		td.text( '(None)' );
	}
	else
	{
		let ul = $( '<ul/>' )
			.appendTo( td );
		for( let i = 0; i < conditioningEvent.preconditions.length; ++i )
		{
			let preconditionPlugin = getPreconditionPlugin( conditioningEvent.preconditions[ i ].id );

			if( preconditionPlugin === null )
			{
				g_util.showError( 'Unknown plugin: ' + conditioningEvent.preconditions[ i ].id );
				return;
			}

			$( '<li/>' )
				.text( g_pluginTagLib.generateConfigSummary( preconditionPlugin, conditioningEvent.preconditions[ i ].config ) )
				.appendTo( ul );
		}
	}

	// outcomes
	tr = g_taglib.generateTableRow( g_ctrlConditioningEventDetailsTBody );
	g_taglib.generateTableCell( 'Outcomes', tr );
	td = g_taglib.generateTableCell( null, tr );
	if( conditioningEvent.outcomes.length === 0 )
	{
		td.text( '(None)' );
	}
	else
	{
		let ul = $( '<ul/>' )
			.appendTo( td );
		for( let i = 0; i < conditioningEvent.outcomes.length; ++i )
		{
			const li = $( '<li/>' )
				.text( conditioningEvent.outcomes[ i ].name )
				.appendTo( ul );
			const ul2 = $( '<ul/>' )
				.appendTo( li );

			if( conditioningEvent.outcomes[ i ].effects.length === 0 )
			{
				$( '<li/>' )
					.text( '(No effects)' )
					.appendTo( ul2 );
			}
			else
			{
				for( let j = 0; j < conditioningEvent.outcomes[ i ].effects.length; ++j )
				{
					const outcomeEffectPlugin = getOutcomeEffectPlugin( conditioningEvent.outcomes[ i ].effects[ j ].id );

					if( outcomeEffectPlugin === null )
					{
						g_util.showError( 'Unknown plugin: ' + conditioningEvent.outcomes[ i ].effects[ j ].id );
						return;
					}

					$( '<li/>' )
						.text( g_pluginTagLib.generateConfigSummary( outcomeEffectPlugin, conditioningEvent.outcomes[ i ].effects[ j ].config ) )
						.appendTo( ul2 );
				}
			}
		}
	}

	if( isConditioningEventInThisView )
	{
		// already assigned, so hide the assign button
		g_ctrlConditioningEventDetailsAssignButton.hide();

		// update the click handlers on the delete button
		g_ctrlConditioningEventDetailsDeleteButton
			.off( 'click' )
			.on( 'click', g_api.deleteConditioningEvent.bind( g_api, g_projectId, g_viewId, conditioningEvent.id, viewUpdated.bind( this ) ) )
			.show();
	}
	else
	{
		// update the assign click handler and show it
		g_ctrlConditioningEventDetailsAssignButton
			.off( 'click' )
			.on( 'click', g_api.assignConditioningEvent.bind( g_api, g_projectId, g_viewId, conditioningEvent.id, viewUpdated.bind( this ) ) )
			.show();

		g_ctrlConditioningEventDetailsDeleteButton.hide();
	}

	// update the click handlers on the edit button
	g_ctrlConditioningEventDetailsEditButton
		.off( 'click' )
		.on( 'click', g_nav.redirectToUpdateConditioningEvent.bind( g_nav, g_projectId, g_viewId, conditioningEvent.id ) );

	g_ctrlConditioningEventsList.hide();

	g_ctrlConditioningEventDetails.show();

	g_ctrlConditioningEventsTab.tab( 'show' );

	if( updateViewCanvas )
	{
		// normally we would tell the tree to set the selected index, but we don't support more than one selection, so just use the hover version
		highlightConditioningEvent( conditioningEvent );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function conditioningEventsSearchCallback()
{
	if( g_conditioningEvents === null )
	{
		return;
	}

	const searchText = g_ctrlConditioningEventsListSearchBox.val().toString().trim().toLowerCase();

	if( searchText.length === 0 )
	{
		populateConditioningEventsListTableFull();
		return;
	}

	const matches = [];
	for( let i = 0; i < g_conditioningEvents.length; ++i )
	{
		// check the name
		if( g_conditioningEvents[ i ].name.toLowerCase().indexOf( searchText ) > -1 )
		{
			matches.push( g_conditioningEvents[ i ] );
			continue;
		}

		// check the description
		if( g_conditioningEvents[ i ].description.toLowerCase().indexOf( searchText ) > -1 )
		{
			matches.push( g_conditioningEvents[ i ] );
			continue;
		}

		// check the view name
		const view = getView( g_conditioningEvents[ i ].originViewId );
		if( view.name.toLowerCase().indexOf( searchText ) > -1 )
		{
			matches.push( g_conditioningEvents[ i ] );
			// continue;
		}
	}

	populateConditioningEventsListTable( matches, true );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Finds the given conditioning event instances in the view tree and tells the view canvas to highlight them
 *
 * @private
 *
 * @param {ConditioningEvent} conditioningEvent
 */
function highlightConditioningEvent( conditioningEvent )
{
	const conditioningEventTreeNodeIds = [];

	for( let i = 0; i < g_view.treeNodes.length; ++i )
	{
		if( g_view.treeNodes[ i ].dbId === conditioningEvent.id )
		{
			conditioningEventTreeNodeIds.push( g_view.treeNodes[ i ].id );
		}
	}

	g_canvas.setHoveredElements( conditioningEventTreeNodeIds );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the timeline events with the fill list
 *
 * @private
 */
function populateTimelineEventsListTableFull()
{
	populateTimelineEventsListTable( g_timelineEvents, false );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the conditioning events with the given list
 *
 * @private
 *
 * @param {Array< TimelineEvent >} timelineEvents - the list to populate
 * @param {boolean} isFiltered - True if this is a filtered list, false if it is the full list
 */
function populateTimelineEventsListTable( timelineEvents, isFiltered )
{
	if( g_user === null )
	{
		return;
	}

	g_ctrlTimelineEventsListTBody.empty();

	g_ctrlTimelineEventDetails.hide();
	g_ctrlTimelineEventsList.show();

	let tr;

	if( timelineEvents === null )
	{
		tr = g_taglib.generateTableRow( g_ctrlTimelineEventsListTBody );

		g_taglib.generateTableCell( 'Loading, please wait...', tr )
			.attr( 'colspan', 3 );

		return;
	}
	else if( timelineEvents.length === 0 )
	{
		tr = g_taglib.generateTableRow( g_ctrlTimelineEventsListTBody );

		g_taglib.generateTableCell( isFiltered ? 'No matches' : 'No timeline events', tr )
			.attr( 'colspan', 3 );

		return;
	}

	for( let i = 0; i < timelineEvents.length; ++i )
	{
		tr = g_taglib.generateTableRow( g_ctrlTimelineEventsListTBody );

		g_taglib.generateTableCell( timelineEvents[ i ].name, tr )
			.addClass( 'clickable' )
			.on( 'mouseover', highlightTimelineEvent.bind( this, timelineEvents[ i ].id ) )
			.on( 'click', showTimelineEventDetails.bind( this, timelineEvents[ i ] ) );
		g_taglib.generateTableCell( g_util.formatDate( timelineEvents[ i ].start, g_user.getDateFormatPreference() ), tr )
			.addClass( 'clickable' )
			.on( 'mouseover', highlightTimelineEvent.bind( this, timelineEvents[ i ].id ) )
			.on( 'click', showTimelineEventDetails.bind( this, timelineEvents[ i ] ) );
		g_taglib.generateTableCell( g_util.formatDate( timelineEvents[ i ].end, g_user.getDateFormatPreference() ), tr )
			.addClass( 'clickable' )
			.on( 'mouseover', highlightTimelineEvent.bind( this, timelineEvents[ i ].id ) )
			.on( 'click', showTimelineEventDetails.bind( this, timelineEvents[ i ] ) );
	}

	// show the totals row
	tr = g_taglib.generateTableRow( g_ctrlTimelineEventsListTBody );
	g_taglib.generateTableCell( ( isFiltered ? 'Matches: ' : 'Total: ' ) + timelineEvents.length.toString() + ' timeline event' + ( timelineEvents.length === 1 ? '' : 's' ), tr )
		.attr( 'colspan', 3 )
		.addClass( 'text-right' );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates and shows the timeline event details tab section for the given timeline event
 *
 * @private
 *
 * @param {TimelineEvent} timelineEvent - the timeline event to display
 */
function showTimelineEventDetails( timelineEvent )
{
	if( g_user === null )
	{
		return;
	}

	g_ctrlTimelineEventDetailsTBody.empty();

	// name
	let tr = g_taglib.generateTableRow( g_ctrlTimelineEventDetailsTBody );
	g_taglib.generateTableCell( 'Name', tr );
	g_taglib.generateTableCell( timelineEvent.name, tr );

	// description
	tr = g_taglib.generateTableRow( g_ctrlTimelineEventDetailsTBody );
	g_taglib.generateTableCell( 'Description', tr );
	g_taglib.generateTableCell( timelineEvent.description, tr );

	// start
	tr = g_taglib.generateTableRow( g_ctrlTimelineEventDetailsTBody );
	g_taglib.generateTableCell( 'Start', tr );
	g_taglib.generateTableCell( g_util.formatDate( timelineEvent.start, g_user.getDateFormatPreference() ), tr );

	// end
	tr = g_taglib.generateTableRow( g_ctrlTimelineEventDetailsTBody );
	g_taglib.generateTableCell( 'End', tr );
	g_taglib.generateTableCell( g_util.formatDate( timelineEvent.end, g_user.getDateFormatPreference() ), tr );

	// url
	tr = g_taglib.generateTableRow( g_ctrlTimelineEventDetailsTBody );
	g_taglib.generateTableCell( 'URL', tr );
	let td = g_taglib.generateTableCell( null, tr );
	if( g_util.verifyUri( timelineEvent.url ) )
	{
		g_taglib.generateLink( null, timelineEvent.url, timelineEvent.url, true, td );
	}
	else
	{
		td.text( timelineEvent.url );
	}

	// color
	tr = g_taglib.generateTableRow( g_ctrlTimelineEventDetailsTBody );
	g_taglib.generateTableCell( 'Color', tr );
	td = g_taglib.generateTableCell( null, tr );
	g_taglib.generateColorSwatch( timelineEvent.color, true, td );

	g_ctrlTimelineEventsList.hide();

	g_ctrlTimelineEventDetails.show();

	g_ctrlTimelineEventDetailsEditButton
		.off( 'click' )
		.on( 'click', g_nav.redirectToUpdateProjectTimelineEvents.bind( g_nav, g_projectId, g_viewId, timelineEvent.id ) );

	g_ctrlTimelineEventsTab.tab( 'show' );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user updates the timeline events search box
 *
 * @private
 */
function timelineEventsSearchCallback()
{
	if( g_timelineEvents === null )
	{
		return;
	}

	const searchText = g_ctrlTimelineEventsListSearchBox.val().toString().trim().toLowerCase();

	if( searchText.length === 0 )
	{
		populateTimelineEventsListTableFull();
		return;
	}

	const matches = [];
	for( let i = 0; i < g_timelineEvents.length; ++i )
	{
		// check the name
		if( g_timelineEvents[ i ].name.toLowerCase().indexOf( searchText ) > -1 )
		{
			matches.push( g_timelineEvents[ i ] );
			continue;
		}

		// check the description
		if( g_timelineEvents[ i ].description.toLowerCase().indexOf( searchText ) > -1 )
		{
			matches.push( g_timelineEvents[ i ] );
			continue;
		}

		// check the url
		if( g_timelineEvents[ i ].url.toLowerCase().indexOf( searchText ) > -1 )
		{
			matches.push( g_timelineEvents[ i ] );
			// continue;
		}
	}

	populateTimelineEventsListTable( matches, true );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user hovers over an item in the right side bar
 *
 * @private
 *
 * @param {string} timelineEventId
 */
function highlightTimelineEvent( timelineEventId )
{
	g_canvas.setHoveredElements( [ timelineEventId ] );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function viewUpdated()
{
	g_api.getView( g_projectId, g_viewId, getViewCallback );
	g_api.listStates( g_projectId, g_viewId, listStatesCallback );

	if( g_ctrlStatesTabBody.is( ':visible' ) )
	{
		populateStatesListTableFull();
	}
	else if( g_ctrlConditioningEventsTabBody.is( ':visible' ) )
	{
		populateConditioningEventsListTableFull();
	}
	else if( g_ctrlTimelineEventsTabBody.is( ':visible' ) )
	{
		populateTimelineEventsListTableFull();
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function populateSmartQueryStats()
{
	if( g_view === null || g_view.stats === null || g_conditioningEvents === null || g_user === null )
	{
		return;
	}

	const viewPreferences = /** @type {{sensitivityFilter:?number, specificityFilter:?number}} */( g_user.getViewPreferences( g_projectId, g_viewId ) );

	g_ctrlViewStats
		.empty();

	// add the filter bar at the top

	let initiallyExpanded = true;

	const filterBodyId = 'filter-body';
	g_taglib.generateCollapsibleSectionHeader( filterBodyId, 'Filter', initiallyExpanded, g_ctrlViewStats );

	const filterBody = $( '<div/>' )
		.attr( 'id', filterBodyId )
		.addClass( 'bg-light' )
		.addClass( 'border' )
		.addClass( 'border-secondary' )
		.addClass( 'rounded' )
		.addClass( 'p-3' )
		.addClass( 'mx-0' )
		.addClass( 'row' )
		.appendTo( g_ctrlViewStats );

	if( !initiallyExpanded )
	{
		filterBody.hide();
	}

	const sensitivityFilterHolder = $( '<div/>' )
		.addClass( 'col-6' )
		.appendTo( filterBody );
	const specificityFilterHolder = $( '<div/>' )
		.addClass( 'col-6' )
		.appendTo( filterBody );

	// add the labels
	$( '<div/>' )
		.attr( 'id', 'sensitivityFilterLabel' )
		.appendTo( sensitivityFilterHolder );
	$( '<div/>' )
		.attr( 'id', 'specificityFilterLabel' )
		.appendTo( specificityFilterHolder );

	// add the sliders
	g_taglib.generateSlider(
		'sensitivityFilter',
		0.0,
		1.0,
		0.01,
		2,
		viewPreferences.sensitivityFilter,
		true,
		this.adjustStatsFilter.bind( this, true ),
		sensitivityFilterHolder );
	g_taglib.generateSlider(
		'specificityFilter',
		0.0,
		1.0,
		0.01,
		2,
		viewPreferences.specificityFilter,
		true,
		this.adjustStatsFilter.bind( this, false ),
		specificityFilterHolder );

	// set the initial labels
	updateStatsFilterLabel( true );
	updateStatsFilterLabel( false );

	// generate the main datatable
	const smartQueryStats = /** @type {SmartQueryStats} */( g_view.stats );

	// convert the object-based data from the API into the format the datatable requires
	const dataTableData = [];
	for( let groupingIndex = 0; groupingIndex < smartQueryStats.groupings.length; ++groupingIndex )
	{
		for( let indicatorIndex = 0; indicatorIndex < smartQueryStats.groupings[ groupingIndex ].indicators.length; ++indicatorIndex )
		{
			const indicator = smartQueryStats.groupings[ groupingIndex ].indicators[ indicatorIndex ];

			// we can't use the taglib here since we are using datatables, we have to generate the HTML
			let indicatorsHTML = '';

			for( let conditioningEventIndex = 0; conditioningEventIndex < indicator.path.length; ++conditioningEventIndex )
			{
				const conditioningEvent = getConditioningEvent( indicator.path[ conditioningEventIndex ].id );

				indicatorsHTML += '<div>';
				if( conditioningEvent === null )
				{
					console.log( 'Unknown conditioning event: %o', indicator.path[ conditioningEventIndex ].id );
					indicatorsHTML += 'Unknown conditioning event: ' + indicator.path[ conditioningEventIndex ].id;
				}
				else
				{
					indicatorsHTML += '<div><i class="fas '
						+ g_taglib.Icons.CONDITIONING_EVENT
						+ ' float-left mt-1 ml-4"></i><div class="pl-2 text-truncate">'
						+ conditioningEvent.name
						+ '</div></div>';

					const outcome = conditioningEvent.outcomes[ indicator.path[ conditioningEventIndex ].outcome ];
					indicatorsHTML += '<div><i class="fas '
						+ g_taglib.Icons.CONDITIONING_EVENT_OUTCOME
						+ ' float-left mt-1 ml-5"></i><div class="pl-2 text-truncate">'
						+ outcome.name
						+ '</div></div>';
				}
				indicatorsHTML += '</div>';
			}

			let stateGroupName = smartQueryStats.groupings[ groupingIndex ].name;
			if( smartQueryStats.groupings[ groupingIndex ].description.length > 0 )
			{
				stateGroupName += ' (' + smartQueryStats.groupings[ groupingIndex ].description + ')';
			}

			dataTableData.push( {
				'stategroup' : stateGroupName,
				'indicators' : indicatorsHTML,
				'sensitivity' : indicator.sensitivity == null ? '' : indicator.sensitivity.toFixed( 2 ),
				'specificity' : indicator.specificity == null ? '' : indicator.specificity.toFixed( 2 )
			} );
		}
	}

	const table = $( '<table/>' )
		.addClass( 'table' )
		.addClass( 'table-hover' )
		.addClass( 'table-striped' )
		.addClass( 'table-bordered' )
		.appendTo( g_ctrlViewStats );

	g_statsDataTable = table.DataTable( {
		'columns' : [
			{
				'data' : 'indicators',
				'title' : 'Indicators'
			},
			{
				'data' : 'sensitivity',
				'title' : 'Sensitivity',
				'className' : 'text-right'
			},
			{
				'data' : 'specificity',
				'title' : 'Specificity',
				'className' : 'text-right'
			}
		],
		'rowGroup' : { 'dataSrc' : 'stategroup' },
		'data' : dataTableData,
		'ordering' : false,
		'paging' : false,
		'dom' : '<\'row\'<\'col-sm-12\'tr>><\'row\'<\'col-sm-12 col-md-5\'i>>'
	} );

	table.find( 'thead' ).addClass( 'thead-dark' );

	table.find( 'thead > tr > th' )
		.removeClass( 'text-right' )
		.addClass( 'text-center' );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the filter label
 *
 * @private
 *
 * @param {boolean} isSensitivity - true to update the sensitivity label, false to update the specificity label
 */
function updateStatsFilterLabel( isSensitivity )
{
	const viewPreferences = /** @type {{sensitivityFilter:?number, specificityFilter:?number}} */( g_user.getViewPreferences( g_projectId, g_viewId ) );

	if( isSensitivity )
	{
		$( '#sensitivityFilterLabel' ).text( 'Sensitivity > ' + viewPreferences.sensitivityFilter.toFixed( 2 ) );
	}
	else
	{
		$( '#specificityFilterLabel' ).text( 'Specificity > ' + viewPreferences.specificityFilter.toFixed( 2 ) );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Callback function when a slider is moved
 *
 * @private
 *
 * @param {boolean} isSensitivity - true if the sensitivity slider was moved, false if the specificity slider was moved
 * @param {number} value - the new value
 */
function adjustStatsFilter( isSensitivity, value )
{
	if( g_user === null )
	{
		return;
	}

	const viewPreferences = /** @type {{sensitivityFilter:?number, specificityFilter:?number}} */( g_user.getViewPreferences( g_projectId, g_viewId ) );

	if( isSensitivity )
	{
		viewPreferences.sensitivityFilter = value;
	}
	else
	{
		viewPreferences.specificityFilter = value;
	}

	g_user.setViewPreferences( g_projectId, g_viewId, viewPreferences );

	// update the label
	updateStatsFilterLabel( isSensitivity );

	// update the table
	g_statsDataTable.draw();

	if( g_updateStatsFilterSaveDelayTimerId !== null )
	{
		clearTimeout( g_updateStatsFilterSaveDelayTimerId );
	}
	g_updateStatsFilterSaveDelayTimerId = setTimeout( runSaveStatsFilterChanges.bind( this ), 1000 );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Timer callback function when we should save the user preferences
 */
function runSaveStatsFilterChanges()
{
	g_updateStatsFilterSaveDelayTimerId = null;

	g_api.updateUser( g_user, null );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Datatables callback function to test the filter
 *
 * @private
 *
 * @param {Object} settings
 * @param {Array} data
 *
 * @returns {boolean}
 */
function runStatsFilterCallback( settings, data )
{
	const sensitivity = parseFloat( data[ 1 ] );
	const specificity = parseFloat( data[ 2 ] );

	const viewPreferences = /** @type {{sensitivityFilter:?number, specificityFilter:?number}} */( g_user.getViewPreferences( g_projectId, g_viewId ) );

	return sensitivity >= viewPreferences.sensitivityFilter
		&& specificity >= viewPreferences.specificityFilter;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the view with the given id
 *
 * @private
 *
 * @param {string} id - the id of the entry
 *
 * @returns {View|null} The entry, or null if it could not found
 */
function getView( id )
{
	return /** @type {View|null} */( _getApiItem( g_views, id ) );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the feature with the given id
 *
 * @private
 *
 * @param {string} id - the id of the entry
 *
 * @returns {Feature|null} The entry, or null if it could not found
 */
function getFeature( id )
{
	return /** @type {Feature|null} */( _getApiItem( g_features, id ) );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the state with the given id
 *
 * @private
 *
 * @param {string} id - the id of the entry
 *
 * @returns {State|null} The entry, or null if it could not found
 */
function getState( id )
{
	return /** @type {State|null} */( _getApiItem( g_states, id ) );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the conditioning event with the given id
 *
 * @private
 *
 * @param {string} id - the id of the entry
 *
 * @returns {ConditioningEvent|null} The entry, or null if it could not found
 */
function getConditioningEvent( id )
{
	return /** @type {ConditioningEvent|null} */( _getApiItem( g_conditioningEvents, id ) );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the timeline event with the given id
 *
 * @private
 *
 * @param {string} id - the id of the entry
 *
 * @returns {TimelineEvent|null} The entry, or null if it could not found
 */
function getTimelineEvent( id )
{
	return /** @type {TimelineEvent|null} */( _getApiItem( g_timelineEvents, id ) );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the feature type plugin with the given id
 *
 * @private
 *
 * @param {string} id - the id of the entry
 *
 * @returns {FeatureTypePlugin|null} The plugin, or null if it could not found
 */
function getFeatureTypePlugin( id )
{
	return /** @type {FeatureTypePlugin|null} */( _getApiItem( g_featureTypePlugins, id ) );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the precondition plugin with the given id
 *
 * @private
 *
 * @param {string} id - the id of the entry
 *
 * @returns {PreconditionPlugin|null} The plugin, or null if it could not found
 */
function getPreconditionPlugin( id )
{
	return /** @type {PreconditionPlugin|null} */( _getApiItem( g_preconditionPlugins, id ) );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the outcome effect plugin with the given id
 *
 * @private
 *
 * @param {string} id - the id of the entry
 *
 * @returns {OutcomeEffectPlugin|null} The plugin, or null if it could not found
 */
function getOutcomeEffectPlugin( id )
{
	return /** @type {OutcomeEffectPlugin|null} */( _getApiItem( g_outcomeEffectPlugins, id ) );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the projector plugin with the given id
 *
 * @private
 *
 * @param {string} id - the id of the entry
 *
 * @returns {ProjectorPlugin|null} The plugin, or null if it could not found
 */
function getProjectorPlugin( id )
{
	return /** @type {ProjectorPlugin|null} */( _getApiItem( g_projectorPlugins, id ) );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {Array} sourceArray - the array to search through
 * @param {?string} id - the unique id of the entry to find
 *
 * @returns {Object|null}
 */
function _getApiItem( sourceArray, id )
{
	if( !id || sourceArray === null )
	{
		return null;
	}

	for( let i = 0; i < sourceArray.length; ++i )
	{
		if( sourceArray[ i ].id === id )
		{
			return sourceArray[ i ];
		}
	}

	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
