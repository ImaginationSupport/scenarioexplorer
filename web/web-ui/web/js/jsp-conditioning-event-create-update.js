////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Holds the Scenario Explorer API library instance
 *
 * @const {ScenarioExplorerAPI}
 */
const g_api = new ScenarioExplorerAPI( false, '/scenarioexplorer' );

/**
 * Holds the Util library instance
 *
 * @const {Util}
 */
const g_util = new Util();

/**
 * Holds the Nav Helper instance
 *
 * @const {NavHelper}
 */
const g_nav = new NavHelper();

/**
 * Holds the TagLib library instance
 *
 * @const {TagLib}
 */
const g_taglib = new TagLib();

/**
 * Holds the PluginHelpers library instance
 *
 * @const {PluginHelpers}
 */
const g_pluginHelpers = new PluginHelpers();

/**
 * Holds the PluginTagLib library instance
 *
 * @const {PluginTagLib}
 */
const g_pluginTagLib = new PluginTagLib();

/**
 * Holds the API data helper
 *
 * @const {ApiDataHelper}
 */
const g_apiDataHelper = new ApiDataHelper();

/**
 * Holds the existing project id
 *
 * @type {?string}
 */
let g_projectId = null;

/**
 * Holds the existing view id
 *
 * @type {?string}
 */
let g_viewId = null;

/**
 * Holds the original list of existing conditioning events
 *
 * @type {?Array< ConditioningEvent >}
 */
let g_originalConditioningEvents = null;

/**
 * Holds the working array of conditioning events
 *
 * @type {?Array< ConditioningEvent >}
 */
let g_workingConditioningEvents = null;

/**
 * Holds the conditioning event array index currently being edited
 *
 * @type {number}
 */
let g_conditioningEventEditIndex = -1;

/**
 * Holds the conditioning event precondition array index currently being edited
 *
 * @type {number}
 */
let g_preconditionEditIndex = -1;

/**
 * Holds the conditioning event outcome array index currently being edited
 *
 * @type {number}
 */
let g_outcomeEditIndex = -1;

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
 * Holds the jQuery control for the working conditioning events
 *
 * @type {?jQuery}
 */
let g_ctrlWorkingConditioningEventsHolder = null;

/**
 * Holds the jQuery control for the working conditioning events
 *
 * @type {?jQuery}
 */
let g_ctrlWorkingConditioningEvents = null;

let g_ctrlConditioningEventPane = null;
let g_ctrlPreconditionPane = null;
let g_ctrlOutcomePane = null;

/**
 * Holds the jQuery control for the conditioning event name
 *
 * @type {?jQuery}
 */
let g_ctrlConditioningEventNameInput = null;

/**
 * Holds the jQuery control for the conditioning event description
 *
 * @type {?jQuery}
 */
let g_ctrlConditioningEventDescriptionTextArea = null;

/**
 * Holds the jQuery control for the outcomes summary on the conditioning event pane
 *
 * @type {jQuery}
 */
let g_ctrlConditioningEventOutcomesSummary = null;

let g_ctrlPreconditionTypeDropdown = null;
let g_ctrlPreconditionConfigHolder = null;

let g_ctrlOutcomeNameInput = null;
let g_ctrlOutcomeLikelihoodSlider = null;
let g_ctrlOutcomeLikelihoodValue = null;
let g_ctrlOutcomeEffectsHolder = null;

let g_ctrlHelpSideBarConditioningEvent = null;
let g_ctrlHelpSideBarPrecondition = null;
let g_ctrlHelpSideBarOutcome = null;
let g_ctrlHelpSideBarPreconditionConfig = null;

let g_ctrlSave = null;
let g_ctrlCancel = null;

/**
 * Holds the available precondition plugins
 *
 * @type {Array< PreconditionPlugin >}
 */
const g_preconditionPlugins = [];

/**
 * Holds the available outcome effect plugins
 *
 * @type {Array< OutcomeEffectPlugin >}
 */
const g_outcomeEffectPlugins = [];

const LIKELIHOOD_MARGIN_CHECK = 0.01;

/**
 * Holds the panes
 *
 * @enum {number}
 */
const ConditioningEventJspPanes = {

	/**
	 * Unknown pane
	 */
	UNKNOWN : 0,

	/**
	 * Conditioning Event
	 */
	CONDITIONING_EVENT : 1,

	/**
	 * Precondition
	 */
	PRECONDITION : 2,

	/**
	 * Outcome
	 */
	OUTCOME : 3
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the JSP to initialize the page
 *
 * @param {string} username
 */
function initJSP( username )
{
	g_projectId = g_nav.getProjectIdUriParameter();
	if( g_projectId === null )
	{
		g_util.showError( 'Missing URL parameter: project' );
		return;
	}

	g_viewId = g_nav.getViewIdUriParameter();
	if( g_viewId === null )
	{
		g_util.showError( 'Missing URL parameter: view' );
		return;
	}

	g_ctrlAlert = $( '#form-error-alert' )
		.removeClass( 'd-none' )
		.hide();

	// initialize the left bar
	g_ctrlWorkingConditioningEventsHolder = $( '#working-conditioning-events-holder' )
		.addClass( 'clickable' )
		.on( 'click', runDeselectEdit.bind( this ) );
	g_ctrlWorkingConditioningEvents = $( '#working-conditioning-events' );

	// initialize the conditioning event pane
	g_ctrlConditioningEventPane = $( '#pane-conditioning-event' );
	initConditioningEventPane();

	// initialize the precondition pane
	g_ctrlPreconditionPane = $( '#pane-precondition' )
		.hide()
		.removeClass( 'd-none' );
	initPreconditionPane();

	// initialize the outcome pane
	g_ctrlOutcomePane = $( '#pane-outcome' )
		.hide()
		.removeClass( 'd-none' );
	initOutcomePane();

	// initialize the plugins
	g_pluginTagLib.fetchPreconditionPlugins(
		g_preconditionPlugins,
		true,
		listPreconditionsCallback.bind( this ),
		null,
		g_projectId,
		g_viewId,
		loadPreconditionPluginReadyCallback.bind( this ) );
	g_pluginTagLib.fetchOutcomeEffectPlugins(
		g_outcomeEffectPlugins,
		true,
		null,
		null,
		g_projectId,
		g_viewId,
		null );
	g_taglib.attachDropdownEventHandlers( g_ctrlPreconditionTypeDropdown );

	g_ctrlHelpSideBarConditioningEvent = $( '#help-sidebar-conditioning-event' )
		.removeClass( 'd-none' )
		.hide();
	g_ctrlHelpSideBarPrecondition = $( '#help-sidebar-precondition' )
		.removeClass( 'd-none' )
		.hide();
	g_ctrlHelpSideBarOutcome = $( '#help-sidebar-outcome' )
		.removeClass( 'd-none' )
		.hide();
	g_ctrlHelpSideBarPreconditionConfig = $( '#help-sidebar-precondition-config' );

	// load the project
	g_api.getProject( g_projectId, getProjectCallback.bind( this ) );

	// set up the left bar according to the URL parameters
	const conditioningEventId = g_nav.getConditioningEventIdUriParameter();
	if( conditioningEventId )
	{
		// conditioning event id was given, so edit only this entry

		$( '#page-header' ).text( 'Edit Conditioning Event' );

		// start the API call to populate the data
		g_api.getConditioningEvent( g_projectId, conditioningEventId, getConditioningEventCallback.bind( this ) );

		g_ctrlSave = $( '#button-save' )
			.on( 'click', runSaveEditSingle.bind( this ) );
	}
	else
	{
		// edit all conditioning events and add more

		// start the API call to populate the data
		g_api.listConditioningEvents( g_projectId, listConditioningEventsCallback.bind( this ) );

		g_ctrlSave = $( '#button-save' )
			.on( 'click', runSaveFull.bind( this ) );
	}

	g_ctrlCancel = $( '#button-cancel' )
		.on( 'click', g_nav.redirectToView.bind( g_nav, g_projectId, g_viewId ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API gets the existing project
 *
 * @private
 *
 * @param {Project} project - the project
 */
function getProjectCallback( project )
{
	// update the notifications in the breadcrumbs
	g_nav.updateBreadcrumbNotifications( project.id, project.notifications );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the conditioning events are loaded from the API
 *
 * @private
 *
 * @param {Array< ConditioningEvent >} conditioningEvents - the conditioning events for this project
 */
function listConditioningEventsCallback( conditioningEvents )
{
	g_workingConditioningEvents = conditioningEvents;
	g_originalConditioningEvents = /** @type {Array< ConditioningEvent >} */( g_util.cloneArray( conditioningEvents ) );

	// start the user editing a new conditioning event
	const conditioningEventId = g_nav.getConditioningEventIdUriParameter();
	if( conditioningEventId === null )
	{
		g_workingConditioningEvents.push( g_apiDataHelper.createConditioningEventInstance( '', '', g_viewId, [], [] ) );
		showConditioningEventsPane( g_workingConditioningEvents.length - 1 );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the conditioning event is loaded from the API
 *
 * @private
 *
 * @param {ConditioningEvent} conditioningEvent
 */
function getConditioningEventCallback( conditioningEvent )
{
	g_workingConditioningEvents = [ conditioningEvent ];
	populateSideBar();

	// select this conditioning event
	showConditioningEventsPane( 0 );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the preconditions are loaded from the API
 *
 * @private
 *
 * @param {Array< PreconditionPlugin >} preconditions - the preconditions
 */
function listPreconditionsCallback( preconditions )
{
	g_ctrlPreconditionTypeDropdown.empty();

	g_taglib.generateDropdownOption(
		g_ctrlPreconditionTypeDropdown,
		'Please select...',
		null,
		false,
		false,
		null,
		onSelectPreconditionType.bind( this, null, null ) );

	for( let i = 0; i < preconditions.length; ++i )
	{
		g_taglib.generateDropdownOption(
			g_ctrlPreconditionTypeDropdown,
			preconditions[ i ].name,
			preconditions[ i ].id,
			false,
			false,
			null,
			onSelectPreconditionType.bind( this, preconditions[ i ], null ) );
	}

	// update the left bar since the plugin names are used there
	populateSideBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {string} id
 */
function loadPreconditionPluginReadyCallback( id )
{
	populateSideBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the conditioning event pane
 *
 * @private
 */
function initConditioningEventPane()
{
	g_ctrlConditioningEventNameInput = $( '#conditioning-event-name' );
	g_ctrlConditioningEventDescriptionTextArea = $( '#conditioning-event-description' );
	g_ctrlConditioningEventOutcomesSummary = $( '#conditioning-event-outcomes-summary' );

	$( '#conditioning-event-delete-button' ).on( 'click', onDeleteConditioningEvent.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the precondition pane
 *
 * @private
 */
function initPreconditionPane()
{
	g_ctrlPreconditionTypeDropdown = $( '#precondition-type' );
	g_ctrlPreconditionConfigHolder = $( '#precondition-config' );

	$( '#precondition-delete-button' ).on( 'click', onDeletePrecondition.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the outcome pane
 *
 * @private
 */
function initOutcomePane()
{
	g_ctrlOutcomeNameInput = $( '#outcome-name' );
	g_ctrlOutcomeLikelihoodSlider = $( '#outcome-likelihood-slider' );
	g_ctrlOutcomeLikelihoodValue = $( '#outcome-likelihood-value' );
	g_ctrlOutcomeEffectsHolder = $( '#outcome-effects' );

	g_taglib.initSlider(
		g_ctrlOutcomeLikelihoodSlider,
		0.0,
		1.0,
		0.01,
		2,
		0.0,
		true,
		outcomePaneSliderChanged.bind( this ) );

	$( '#outcome-pane-delete-button' ).on( 'click', outcomePaneDelete.bind( this, false ) );
	$( '#outcome-pane-add-effect-button' ).on( 'click', outcomePaneAddEffect.bind( this, false ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Save button
 *
 * @private
 */
function runSaveFull()
{
	if( !verifyAllWorkingConditioningEvents() )
	{
		return;
	}

	// everything checks out, so figure out what actually changed
	const changes = g_util.determineAddUpdateDelete( g_workingConditioningEvents, g_originalConditioningEvents );
	g_saveCountdownQueueRemaining = 0;

	// deletes
	for( let i = 0; i < changes.deletes.length; ++i )
	{
		g_api.deleteConditioningEvent( g_projectId, g_viewId, changes.deletes[ i ], saveFullCallback.bind( this ) );
		++g_saveCountdownQueueRemaining;
	}

	// adds
	for( let i = 0; i < changes.adds.length; ++i )
	{
		g_api.newConditioningEvent( g_projectId, /** @type{ConditioningEvent} */( changes.adds[ i ] ), saveFullCallback.bind( this ) );
		++g_saveCountdownQueueRemaining;
	}

	// updates
	for( let i = 0; i < changes.updates.length; ++i )
	{
		g_api.updateConditioningEvent( g_projectId, /** @type{ConditioningEvent} */( changes.updates[ i ] ), saveFullCallback.bind( this ) );
		++g_saveCountdownQueueRemaining;
	}

	if( g_saveCountdownQueueRemaining === 0 )
	{
		g_nav.redirectToView( g_projectId, g_viewId );
	}

	g_ctrlCancel.prop( 'disabled', true );
	g_ctrlSave.prop( 'disabled', true );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API completes one of the save calls (add/update/delete) and lets us track if all of the calls are complete
 *
 * @private
 */
function saveFullCallback()
{
	--g_saveCountdownQueueRemaining;

	// console.log( g_saveCountdownQueueRemaining );

	if( g_saveCountdownQueueRemaining === 0 )
	{
		g_nav.redirectToView( g_projectId, g_viewId );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function runSaveEditSingle()
{
	if( !verifyAllWorkingConditioningEvents() )
	{
		return;
	}

	g_api.updateConditioningEvent( g_projectId, g_workingConditioningEvents[ 0 ], this.runSaveEditSingleCallback.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function runSaveEditSingleCallback()
{
	g_nav.redirectToView( g_projectId, g_viewId );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Verifies all working conditioning events
 *
 * @private
 *
 * @returns {boolean}
 */
function verifyAllWorkingConditioningEvents()
{
	if( !verifyCurrentPane() )
	{
		return false;
	}

	// also make sure all conditioning events have at least one outcome
	const errors = [];
	for( let i = 0; i < g_workingConditioningEvents.length; ++i )
	{
		if( g_workingConditioningEvents[ i ].outcomes.length === 0 )
		{
			errors.push( 'Conditioning event "' + g_workingConditioningEvents[ i ].name + '" does not have any outcomes.' );
		}
		else
		{
			let totalLikelihood = 0.0;

			for( let j = 0; j < g_workingConditioningEvents[ i ].outcomes.length; ++j )
			{
				totalLikelihood += g_workingConditioningEvents[ i ].outcomes[ j ].likelihood;
			}

			if( totalLikelihood < 1.0 - LIKELIHOOD_MARGIN_CHECK || totalLikelihood > 1.0 + LIKELIHOOD_MARGIN_CHECK )
			{
				errors.push( 'Conditioning event "' + g_workingConditioningEvents[ i ].name + '" has invalid likelihood values.' );
			}
		}
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
		return false;
	}
	else
	{
		g_ctrlAlert.hide();
	}

	return true;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the left side bar with the working conditioning event list
 *
 * @private
 */
function populateSideBar()
{
	g_ctrlWorkingConditioningEvents.empty();

	const currentPane = getCurrentPane();

	// add the header
	addLeftBarEntry( null, 'Entries' )
		.addClass( 'pl-3' )
		.addClass( 'font-weight-bold' )
		.addClass( 'bg-secondary' )
		.addClass( 'text-light' );

	if( g_workingConditioningEvents === null )
	{
		addLeftBarEntry( null, 'Loading, please wait...' )
			.addClass( 'pl-3' );
	}
	else
	{
		for( let conditioningEventIndex = 0; conditioningEventIndex < g_workingConditioningEvents.length; ++conditioningEventIndex )
		{
			const conditioningEventSideBarEntry = addLeftBarEntry( g_taglib.Icons.CONDITIONING_EVENT, g_workingConditioningEvents[ conditioningEventIndex ].name )
				.addClass( 'pl-3' )
				.addClass( 'clickable' )
				.on( 'click', editConditioningEvent.bind( this, conditioningEventIndex ) );

			if( conditioningEventIndex === g_conditioningEventEditIndex )
			{
				if( g_preconditionEditIndex === -1
					&& g_outcomeEditIndex === -1
					&& currentPane === ConditioningEventJspPanes.CONDITIONING_EVENT )
				{
					conditioningEventSideBarEntry.addClass( 'bg-medium' );
				}

				// add the preconditions
				for( let preconditionIndex = 0; preconditionIndex < g_workingConditioningEvents[ conditioningEventIndex ].preconditions.length; ++preconditionIndex )
				{
					const preconditionPlugin = getPreconditionPlugin( g_workingConditioningEvents[ conditioningEventIndex ].preconditions[ preconditionIndex ].id );

					const entryLabel = preconditionPlugin === null
						? '(Unknown plugin)'
						: preconditionPlugin.src === null
							? 'Loading, please wait...'
							: preconditionPlugin.src.apiGetConfigTextSummary( g_workingConditioningEvents[ conditioningEventIndex ].preconditions[ preconditionIndex ].config );

					const preconditionSideBarEntry = addLeftBarEntry( g_taglib.Icons.CONDITIONING_EVENT_PRECONDITION, entryLabel )
						.addClass( 'pl-4' )
						.addClass( 'clickable' )
						.on( 'click', editPrecondition.bind( this, conditioningEventIndex, preconditionIndex ) );

					if( conditioningEventIndex === g_conditioningEventEditIndex
						&& preconditionIndex === g_preconditionEditIndex
						&& currentPane === ConditioningEventJspPanes.PRECONDITION )
					{
						preconditionSideBarEntry.addClass( 'bg-medium' );
					}
				}

				const newPreconditionSideBarEntry = addLeftBarEntry( g_taglib.Icons.CREATE, 'New Precondition' )
					.on( 'click', addPrecondition.bind( this ) )
					.addClass( 'pl-5' );
				if( conditioningEventIndex === g_conditioningEventEditIndex
					&& g_preconditionEditIndex === -1
					&& currentPane === ConditioningEventJspPanes.PRECONDITION )
				{
					newPreconditionSideBarEntry.addClass( 'bg-medium' );
				}

				// add the outcomes
				for( let outcomeIndex = 0; outcomeIndex < g_workingConditioningEvents[ conditioningEventIndex ].outcomes.length; ++outcomeIndex )
				{
					const entryLabel = g_workingConditioningEvents[ conditioningEventIndex ].outcomes[ outcomeIndex ].name
						+ ' ('
						+ g_workingConditioningEvents[ conditioningEventIndex ].outcomes[ outcomeIndex ].effects.length.toString()
						+ ' effect'
						+ ( g_workingConditioningEvents[ conditioningEventIndex ].outcomes[ outcomeIndex ].effects.length === 1 ? '' : 's' )
						+ ')';

					const outcomeSideBarEntry = addLeftBarEntry( g_taglib.Icons.CONDITIONING_EVENT_OUTCOME, entryLabel )
						.addClass( 'pl-4' )
						.addClass( 'clickable' )
						.on( 'click', editOutcome.bind( this, conditioningEventIndex, outcomeIndex ) );

					if( conditioningEventIndex === g_conditioningEventEditIndex
						&& outcomeIndex === g_outcomeEditIndex
						&& currentPane === ConditioningEventJspPanes.OUTCOME )
					{
						outcomeSideBarEntry.addClass( 'bg-medium' );
					}
				}

				const newOutcomeSideBarEntry = addLeftBarEntry( g_taglib.Icons.CREATE, 'New Outcome' )
					.addClass( 'pl-5' )
					.on( 'click', addOutcome.bind( this ) );
				if( conditioningEventIndex === g_conditioningEventEditIndex
					&& g_preconditionEditIndex === -1
					&& g_outcomeEditIndex === -1
					&& currentPane === ConditioningEventJspPanes.OUTCOME )
				{
					newOutcomeSideBarEntry.addClass( 'bg-medium' );
				}
			}
		}

		if( g_workingConditioningEvents.length === 0 )
		{
			addLeftBarEntry( null, '(None)' )
				.addClass( 'pl-3' );
		}

		// show the "Add Conditioning Event" entry unless we are only editing this one conditioning event
		if( g_nav.getConditioningEventIdUriParameter() === null )
		{
			addLeftBarEntry( g_taglib.Icons.CREATE, 'New Conditioning Event' )
				.addClass( 'pl-3' )
				.on( 'click', addConditioningEvent.bind( this ) );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Adds an entry to the left bar
 *
 * @private
 *
 * @param {?string} iconClass
 * @param {string} text
 *
 * @returns {jQuery} the entry generated
 */
function addLeftBarEntry( iconClass, text )
{
	const entry = $( '<li/>' )
		.addClass( 'list-group-item' )
		.addClass( 'list-group-item-action' )
		.addClass( 'p-2' )
		.appendTo( g_ctrlWorkingConditioningEvents );

	if( iconClass )
	{
		g_taglib.generateIconWithText( iconClass, text.length === 0 ? '(New Entry)' : text, entry, true );
	}
	else
	{
		entry.text( text );
	}

	return entry;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {jQuery.Event} e - the jQuery event
 */
function addConditioningEvent( e )
{
	e.stopPropagation();

	if( !verifyCurrentPane() )
	{
		return;
	}

	g_workingConditioningEvents.push( g_apiDataHelper.createConditioningEventInstance( '', '', g_viewId, [], [] ) );
	showConditioningEventsPane( g_workingConditioningEvents.length - 1 );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks to edit a working conditioning event
 *
 * @private
 *
 * @param {number} index - the conditioning event array index
 * @param {jQuery.Event} e - the jQuery event
 */
function editConditioningEvent( index, e )
{
	e.stopPropagation();

	if( !verifyCurrentPane() )
	{
		return;
	}

	showConditioningEventsPane( index );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks to edit a working precondition
 *
 * @private
 *
 * @param {number} conditioningEventIndex - the conditioning event array index
 * @param {number} preconditionIndex - the precondition array index
 * @param {jQuery.Event} e - the jQuery event
 */
function editPrecondition( conditioningEventIndex, preconditionIndex, e )
{
	e.stopPropagation();

	if( !verifyCurrentPane() )
	{
		return;
	}

	showPreconditionsPane( conditioningEventIndex, preconditionIndex );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks to edit a working precondition
 *
 * @private
 *
 * @param {number} conditioningEventIndex - the conditioning event array index
 * @param {number} outcomeIndex - the outcome array index
 * @param {jQuery.Event} e - the jQuery event
 */
function editOutcome( conditioningEventIndex, outcomeIndex, e )
{
	e.stopPropagation();

	if( !verifyCurrentPane() )
	{
		return;
	}

	showOutcomePane( conditioningEventIndex, outcomeIndex );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Add Precondition button from the conditioning event pane
 *
 * @private
 *
 * @param {jQuery.Event} e - the jQuery event
 */
function addPrecondition( e )
{
	e.stopPropagation();

	if( !verifyCurrentPane() )
	{
		return;
	}

	showPreconditionsPane( g_conditioningEventEditIndex, -1 );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Add Outcome button
 *
 * @private
 *
 * @param {jQuery.Event} e - the jQuery event
 */
function addOutcome( e )
{
	e.stopPropagation();

	if( !verifyCurrentPane() )
	{
		return;
	}

	const defaultName = 'Outcome ' + ( g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes.length + 1 ).toString();

	g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes.push( g_apiDataHelper.createOutcomeInstance( defaultName, 1.0, [] ) );

	showOutcomePane( g_conditioningEventEditIndex, g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes.length - 1 );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Deselects an existing edit
 *
 * @private
 *
 * @param {jQuery.Event} e - the jQuery event
 */
function runDeselectEdit( e )
{
	e.stopPropagation();

	if( !verifyCurrentPane() )
	{
		return;
	}

	showConditioningEventsPane( -1 );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @returns {boolean}
 */
function verifyCurrentPane()
{
	switch( getCurrentPane() )
	{
		case ConditioningEventJspPanes.CONDITIONING_EVENT:
			return verifyConditioningEventPane();

		case ConditioningEventJspPanes.PRECONDITION:
			return verifyPreconditionPane();

		case ConditioningEventJspPanes.OUTCOME:
			return verifyOutcomePane( false );

		case ConditioningEventJspPanes.UNKNOWN:
		default:
			g_util.showError( 'Unknown conditioning event panel!' );
			return false;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @returns {boolean} True if the conditioning event was verified and saved to the working set, false if there were issues with the configuration
 */
function verifyConditioningEventPane()
{
	const name = g_ctrlConditioningEventNameInput.val().toString().trim();
	const description = g_ctrlConditioningEventDescriptionTextArea.val().toString().trim();

	// if this is a new entry and the fields are blank, just return true
	if( g_conditioningEventEditIndex === -1 && name.length === 0 && description.length === 0 )
	{
		return true;
	}

	const errors = [];

	if( name.length === 0 )
	{
		errors.push( 'Conditioning event name cannot be empty!' );
	}

	const selectedConditioningEvent = getSelectedConditioningEvent();
	let totalLikelihood = 0.0;
	if( selectedConditioningEvent !== null && selectedConditioningEvent.outcomes.length > 0 )
	{
		for( let i = 0; i < selectedConditioningEvent.outcomes.length; ++i )
		{
			totalLikelihood += selectedConditioningEvent.outcomes[ i ].likelihood;
		}

		if( totalLikelihood < 1.0 - LIKELIHOOD_MARGIN_CHECK || totalLikelihood > 1.0 + LIKELIHOOD_MARGIN_CHECK )
		{
			errors.push( 'Likelihood values are not valid, they should add to 1.0.  Use the "Normalize" button to automatically adjust the values.' );
		}
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
		return false;
	}
	else
	{
		g_ctrlAlert.hide();
	}

	// save it to the working set
	if( g_conditioningEventEditIndex === -1 )
	{
		g_workingConditioningEvents.push( g_apiDataHelper.createConditioningEventInstance( name, description, g_viewId, [], [] ) );
	}
	else
	{
		g_workingConditioningEvents[ g_conditioningEventEditIndex ].name = name;
		g_workingConditioningEvents[ g_conditioningEventEditIndex ].description = description;
	}

	return true;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Delete button
 *
 * @private
 *
 * @param {jQuery.Event} e - the jQuery event
 */
function onDeleteConditioningEvent( e )
{
	e.stopPropagation();

	const conditioningEventId = g_nav.getConditioningEventIdUriParameter();
	if( conditioningEventId === null )
	{
		// TODO confirm

		g_workingConditioningEvents.splice( g_conditioningEventEditIndex, 1 );

		showConditioningEventsPane( -1 );
	}
	else
	{
		// only showing this one, so just delete from here and return

		// TODO confirm

		g_api.deleteConditioningEvent( g_projectId, g_viewId, conditioningEventId, deleteConditioningEventCallback.bind( this ) );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function deleteConditioningEventCallback()
{
	g_nav.redirectToView( g_projectId, g_viewId );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the form input fields
 *
 * @private
 */
function initConditioningEventPaneFormFields()
{
	const selectedConditioningEvent = getSelectedConditioningEvent();

	if( g_conditioningEventEditIndex === -1 )
	{
		// nothing selected, so default to blanks
		g_ctrlConditioningEventNameInput.val( '' );
		g_ctrlConditioningEventDescriptionTextArea.val( '' );
	}
	else
	{
		// looking at an existing entry

		// update all the form fields
		g_ctrlConditioningEventNameInput.val( selectedConditioningEvent.name );
		g_ctrlConditioningEventDescriptionTextArea.val( selectedConditioningEvent.description );
	}

	g_ctrlConditioningEventOutcomesSummary.empty();

	g_ctrlAlert.hide();

	if( selectedConditioningEvent !== null && selectedConditioningEvent.outcomes.length > 0 )
	{
		// TODO add odd/even highlight?

		for( let i = 0; i < selectedConditioningEvent.outcomes.length; ++i )
		{
			const row = $( '<div/>' )
				.addClass( 'row' )
				.addClass( 'mb-2' )
				.appendTo( g_ctrlConditioningEventOutcomesSummary );

			// name column
			let col = $( '<div/>' )
				.text( selectedConditioningEvent.outcomes[ i ].name )
				.addClass( 'col-3' )
				.appendTo( row );
			g_taglib.addHoverOverFullTextDisplay( col );

			// slider column
			col = $( '<div/>' )
				.addClass( 'col-6' )
				.appendTo( row );
			g_taglib.generateSlider(
				'outcome-slider-' + i.toString(),
				0.0,
				1.0,
				0.01,
				2,
				selectedConditioningEvent.outcomes[ i ].likelihood,
				true,
				conditioningEventPaneSliderChanged.bind( this, i ),
				col );

			// value / lock column
			col = $( '<div/>' )
				.addClass( 'col-3' )
				.addClass( 'text-right' )
				.appendTo( row );

			$( '<span/>', { 'id' : 'outcome-slider-value-' + i.toString() } )
				.text( selectedConditioningEvent.outcomes[ i ].likelihood.toFixed( 2 ) )
				.appendTo( col );

			g_taglib.generateCheckbox( 'outcome-slider-lock-' + i.toString(), 'Lock', sliderLockChanged.bind( this, i ), col );
		}

		const buttonBar = $( '<div/>' )
			.addClass( 'text-right' )
			.addClass( 'mt-3' )
			.appendTo( g_ctrlConditioningEventOutcomesSummary );
		g_taglib.generateButton( null, 'Normalize', null, 'fa-wrench', buttonBar )
			.on( 'click', normalizeOutcomes.bind( this ) );
	}
	else
	{
		g_ctrlConditioningEventOutcomesSummary.text( '(None)' );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function normalizeOutcomes()
{
	const selectedConditioningEvent = getSelectedConditioningEvent();

	let lockedTotal = 0.0;
	let unlockedTotal = 0.0;

	// first go through and see what is locked
	for( let i = 0; i < selectedConditioningEvent.outcomes.length; ++i )
	{
		const outcome = selectedConditioningEvent.outcomes[ i ];
		if( $( '#outcome-slider-lock-' + i.toString() ).is( ':checked' ) )
		{
			lockedTotal += outcome.likelihood;
		}
		else
		{
			unlockedTotal += outcome.likelihood;
		}
	}

	if( lockedTotal > 1.0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, [ 'The locked sliders are invalid, already adding to more than 1.0 -- Please fix this before continuing.' ] );
		return;
	}

	// if there are no entries unlocked, there is nothing to do, so just bail
	if( unlockedTotal === 0.0 )
	{
		return;
	}

	// now make the changes to what is unchecked
	for( let i = 0; i < selectedConditioningEvent.outcomes.length; ++i )
	{
		let outcome = selectedConditioningEvent.outcomes[ i ];
		if( !$( '#outcome-slider-lock-' + i.toString() ).is( ':checked' ) )
		{
			g_taglib.setSliderValue( $( '#outcome-slider-' + i.toString() ), ( 1.0 - lockedTotal ) * ( outcome.likelihood / unlockedTotal ) );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {number} index - the outcome index
 * @param {number} value - the value
 */
function conditioningEventPaneSliderChanged( index, value )
{
	getSelectedConditioningEvent().outcomes[ index ].likelihood = value;

	$( '#outcome-slider-value-' + index.toString() ).text( value.toFixed( 2 ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {number} index - the outcome index
 * @param {jQuery.Event} e - the jQuery event
 */
function sliderLockChanged( index, e )
{
	g_taglib.setSliderEnabled( $( '#outcome-slider-' + index ), !e.target.checked );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @returns {boolean} True if the precondition was verified and saved to the working set, false if there were issues with the configuration
 */
function verifyPreconditionPane()
{
	const typeId = g_taglib.getDropdownSelectedValue( g_ctrlPreconditionTypeDropdown );

	const errors = [];

	if( typeId === null )
	{
		errors.push( 'Please select the precondition type!' );
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
		return false;
	}
	else
	{
		g_ctrlAlert.hide();
	}

	// get the precondition plugin
	const plugin = getPreconditionPlugin( typeId );
	if( plugin === null )
	{
		g_util.showError( 'Unknown precondition: ' + typeId );
		return false;
	}

	// get the precondition config
	const config = g_pluginTagLib.getConfigUserInterfaceData( g_ctrlPreconditionConfigHolder, plugin );
	if( config === null )
	{
		return false;
	}

	// everything is valid, so add/update the new precondition
	if( g_preconditionEditIndex === -1 )
	{
		// this is a new precondition, so add it to the end of the list
		g_workingConditioningEvents[ g_conditioningEventEditIndex ].preconditions.push( g_apiDataHelper.createPreconditionInstance( typeId, config ) );
		g_preconditionEditIndex = g_workingConditioningEvents[ g_conditioningEventEditIndex ].preconditions.length - 1;
	}
	else
	{
		// we are editing an existing precondition, so just replace that one
		g_workingConditioningEvents[ g_conditioningEventEditIndex ].preconditions[ g_preconditionEditIndex ] = g_apiDataHelper.createPreconditionInstance( typeId, config );
	}

	return true;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function initPreconditionPaneFormFields()
{
	const selectedConditioningEvent = getSelectedConditioningEvent();
	if( g_preconditionEditIndex === -1 )
	{
		// nothing selected, so default to blanks

		g_taglib.setDropdownSelectedItem( g_ctrlPreconditionTypeDropdown, null );

		onSelectPreconditionType( null, null );
	}
	else
	{
		// populate with the given precondition
		const selectedPrecondition = selectedConditioningEvent.preconditions[ g_preconditionEditIndex ];

		g_taglib.setDropdownSelectedItem( g_ctrlPreconditionTypeDropdown, selectedPrecondition.id );

		const plugin = getPreconditionPlugin( selectedPrecondition.id );
		if( plugin === null )
		{
			g_util.showError( 'Unknown precondition: ' + selectedPrecondition.id );
			return;
		}

		onSelectPreconditionType( plugin, selectedPrecondition.config );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user selects a precondition from the dropdown
 *
 * @private
 *
 * @param {?PreconditionPlugin} precondition - the precondition plugin
 * @param {Object} existingConfig
 */
function onSelectPreconditionType( precondition, existingConfig )
{
	g_pluginTagLib.generateConfigUserInterface(
		g_ctrlPreconditionConfigHolder,
		precondition,
		existingConfig,
		g_projectId,
		g_viewId,
		'(Please select the precondition type.)' );

	g_ctrlHelpSideBarPreconditionConfig.empty();
	if( precondition != null )
	{
		precondition.src.apiGenerateSideBarHelpItems( g_ctrlHelpSideBarPreconditionConfig );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function onDeletePrecondition()
{
	if( g_preconditionEditIndex !== -1 )
	{
		g_workingConditioningEvents[ g_conditioningEventEditIndex ].preconditions.splice( g_preconditionEditIndex, 1 );
	}

	showConditioningEventsPane( g_conditioningEventEditIndex );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {boolean} allowZeroEffects - True to allow zero effects (i.e. for an intermediate save) or false to throw an error and return false
 * @returns {boolean} True if the precondition was verified and saved to the working set, false if there were issues with the configuration
 */
function verifyOutcomePane( allowZeroEffects )
{
	const name = g_ctrlOutcomeNameInput.val().toString().trim();
	const rawLikelihood = g_ctrlOutcomeLikelihoodSlider.val().toString().trim();

	const parsedLikelihood = parseFloat( rawLikelihood );

	const errors = [];

	if( name.length === 0 )
	{
		errors.push( 'Outcome name cannot be empty!' );
	}

	if( rawLikelihood.length === 0 )
	{
		errors.push( 'Likelihood is missing!' );
	}
	else if( !g_util.verifyFloat( rawLikelihood ) )
	{
		errors.push( 'Could not understand the likelihood!' );
	}
	else if( parsedLikelihood < 0 || parsedLikelihood > 1.0 )
	{
		errors.push( 'Invalid likelihood (must be between 0.0 and 1.0)' );
	}

	for( let i = 0; i < g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes.length; ++i )
	{
		if( g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes[ i ].effects.length === 0 && !allowZeroEffects )
		{
			errors.push( 'Outcome ' + ( i + 1 ).toString() + ' (' + g_workingConditioningEvents[ g_conditioningEventEditIndex ].name + ') does not have any effects.' );
		}
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
		return false;
	}
	else
	{
		g_ctrlAlert.hide();
	}

	const effects = [];

	if( g_outcomeEditIndex !== -1 )
	{
		for( let i = 0; i < g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes[ g_outcomeEditIndex ].effects.length; ++i )
		{
			const effectId = g_taglib.getDropdownSelectedValue( 'outcome-effect-' + i );

			if( !effectId )
			{
				errors.push( 'Please finish configuring effect ' + ( i + 1 ).toString() );
			}
			else
			{
				plugin = getOutcomeEffectPlugin( effectId );

				const effectPluginSandbox = $( '#outcome-effect-plugin-sandbox-' + i.toString() );

				const effectConfig = g_pluginTagLib.getConfigUserInterfaceData( effectPluginSandbox, plugin );

				// if there were outcome effect errors, they are shown themselves, so don't even try to continue
				if( effectConfig === null )
				{
					return false;
				}

				effects.push( g_apiDataHelper.createOutcomeEffectInstance( effectId, effectConfig ) );
			}
		}
	}

	// TODO why are we checking for zero effects here and again in the above section?
	if( effects.length === 0 && !allowZeroEffects )
	{
		errors.push( 'Outcome does not have any effects.' );
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
		return false;
	}
	else
	{
		g_ctrlAlert.hide();
	}

	if( g_outcomeEditIndex === -1 )
	{
		g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes.push( g_apiDataHelper.createOutcomeInstance( name, parsedLikelihood, effects ) );
		g_outcomeEditIndex = g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes.length - 1;
	}
	else
	{
		g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes[ g_outcomeEditIndex ] = g_apiDataHelper.createOutcomeInstance( name, parsedLikelihood, effects );
	}

	return true;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function initOutcomePaneFormFields()
{
	let name;
	let likelihood;
	if( g_outcomeEditIndex === -1 )
	{
		// nothing selected, so use defaults

		name = '';
		likelihood = 1.0;
	}
	else
	{
		// populate with the given precondition
		let outcome = g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes[ g_outcomeEditIndex ];

		name = outcome.name;
		likelihood = outcome.likelihood;
	}

	g_ctrlOutcomeNameInput.val( name );
	g_taglib.setSliderValue( g_ctrlOutcomeLikelihoodSlider, likelihood );
	g_ctrlOutcomeLikelihoodValue.text( likelihood.toFixed( 2 ) );

	populateOutcomePaneEffects();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function populateOutcomePaneEffects()
{
	g_ctrlOutcomeEffectsHolder.empty();

	if( g_outcomeEditIndex === -1 || g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes[ g_outcomeEditIndex ].effects.length === 0 )
	{
		$( '<span/>' )
			.text( '(None)' )
			.appendTo( g_ctrlOutcomeEffectsHolder );

		return;
	}

	const outcome = g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes[ g_outcomeEditIndex ];

	for( let outcomeEffectIndex = 0; outcomeEffectIndex < outcome.effects.length; ++outcomeEffectIndex )
	{
		const holder = $( '<div/>' )
			.addClass( 'border' )
			.addClass( 'rounded' )
			.addClass( 'p-2' )
			.addClass( 'mb-1' )
			.appendTo( g_ctrlOutcomeEffectsHolder );
		const row = $( '<div/>' )
			.addClass( 'row' )
			.appendTo( holder );
		$( '<div/>' )
			.addClass( 'col-1' )
			.text( ( outcomeEffectIndex + 1 ).toString() )
			.appendTo( row );
		let column = $( '<div/>' )
			.addClass( 'col-9' )
			.appendTo( row );

		const dropdown = g_taglib.generateDropdown( 'outcome-effect-' + outcomeEffectIndex, column );
		g_taglib.generateDropdownOption(
			dropdown,
			'Please select...',
			null,
			false,
			false,
			null,
			onSelectOutcomeEffectType.bind( this ) );
		for( let i = 0; i < g_outcomeEffectPlugins.length; ++i )
		{
			g_taglib.generateDropdownOption(
				dropdown,
				g_outcomeEffectPlugins[ i ].name,
				g_outcomeEffectPlugins[ i ].id,
				g_outcomeEffectPlugins[ i ].id === outcome.effects[ outcomeEffectIndex ].id,
				false,
				null,
				onSelectOutcomeEffectType.bind( this, outcomeEffectIndex, g_outcomeEffectPlugins[ i ].id ) );
		}

		plugin = getOutcomeEffectPlugin( outcome.effects[ outcomeEffectIndex ].id );

		const effectPluginSandbox = $( '<div/>' )
			.attr( 'id', 'outcome-effect-plugin-sandbox-' + outcomeEffectIndex.toString() )
			.addClass( 'mt-2' )
			.addClass( 'p-2' )
			.addClass( 'border' )
			.addClass( 'rounded' )
			.appendTo( column );

		g_pluginTagLib.generateConfigUserInterface(
			effectPluginSandbox,
			plugin,
			outcome.effects[ outcomeEffectIndex ].config,
			g_projectId,
			g_viewId,
			'Please select the feature type' );

		column = $( '<div/>' )
			.addClass( 'col-2' )
			.appendTo( row );
		g_taglib.generateButton( null, null, null, g_taglib.Icons.DELETE, column )
			.on( 'click', outcomePaneDeleteEffect.bind( this, outcomeEffectIndex ) );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user selects the outcome effect from the dropdown
 *
 * @private
 * @param {number} outcomeEffectIndex - the array index into the current conditioning event and outcome effect
 * @param {string} pluginId - the id of the plugin selected
 */
function onSelectOutcomeEffectType( outcomeEffectIndex, pluginId )
{
	g_pluginTagLib.generateConfigUserInterface(
		$( '#outcome-effect-plugin-sandbox-' + outcomeEffectIndex ),
		getOutcomeEffectPlugin( pluginId ),
		null,
		g_projectId,
		g_viewId,
		'Please select the feature type' );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function outcomePaneDelete()
{
	// TODO add a confirm

	if( g_outcomeEditIndex !== -1 )
	{
		g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes.splice( g_outcomeEditIndex, 1 );
	}

	showConditioningEventsPane( g_conditioningEventEditIndex );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function outcomePaneAddEffect()
{
	if( !verifyOutcomePane( true ) )
	{
		return;
	}

	g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes[ g_outcomeEditIndex ].effects.push( g_apiDataHelper.createOutcomeEffectInstance( null, null ) );

	populateOutcomePaneEffects();
	populateSideBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {number} effectIndex - the array index to delete
 */
function outcomePaneDeleteEffect( effectIndex )
{
	g_workingConditioningEvents[ g_conditioningEventEditIndex ].outcomes[ g_outcomeEditIndex ].effects.splice( effectIndex, 1 );

	populateOutcomePaneEffects();
	populateSideBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {number} value
 */
function outcomePaneSliderChanged( value )
{
	g_ctrlOutcomeLikelihoodValue.text( value.toFixed( 2 ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets which panel is currently shown
 *
 * @private
 *
 * @returns {ConditioningEventJspPanes}
 */
function getCurrentPane()
{
	if( g_ctrlConditioningEventPane.is( ':visible' ) )
	{
		return ConditioningEventJspPanes.CONDITIONING_EVENT;
	}
	else if( g_ctrlPreconditionPane.is( ':visible' ) )
	{
		return ConditioningEventJspPanes.PRECONDITION;
	}
	else if( g_ctrlOutcomePane.is( ':visible' ) )
	{
		return ConditioningEventJspPanes.OUTCOME;
	}
	else
	{
		return ConditioningEventJspPanes.UNKNOWN;
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Shows the conditioning events pane for the given entry
 *
 * @private
 *
 * @param {number} workingConditioningEventIndex - the conditioning event array index, or -1 to show nothing selected
 */
function showConditioningEventsPane( workingConditioningEventIndex )
{
	// set up the edit index values
	g_conditioningEventEditIndex = workingConditioningEventIndex;
	g_preconditionEditIndex = -1;
	g_outcomeEditIndex = -1;

	// init the form fields for this conditioning event
	initConditioningEventPaneFormFields();

	// switch to the conditioning events pane
	g_ctrlPreconditionPane.hide();
	g_ctrlOutcomePane.hide();
	g_ctrlConditioningEventPane.show();
	populateSideBar();

	// show the appropriate sidebar help
	g_ctrlHelpSideBarConditioningEvent.show();
	g_ctrlHelpSideBarPrecondition.hide();
	g_ctrlHelpSideBarOutcome.hide();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Shows the preconditions pane for the given entry
 *
 * @private
 *
 * @param {number} workingConditioningEventIndex - the conditioning event array index
 * @param {number} preconditionIndex - the precondition array index, or -1 to show nothing selected
 */
function showPreconditionsPane( workingConditioningEventIndex, preconditionIndex )
{
	// set up the edit index values
	g_conditioningEventEditIndex = workingConditioningEventIndex;
	g_preconditionEditIndex = preconditionIndex;
	g_outcomeEditIndex = -1;

	// init the form fields for this precondition
	initPreconditionPaneFormFields();

	// switch to the precondition pane
	g_ctrlConditioningEventPane.hide();
	g_ctrlOutcomePane.hide();
	g_ctrlPreconditionPane.show();
	populateSideBar();

	// show the appropriate sidebar help
	g_ctrlHelpSideBarConditioningEvent.hide();
	g_ctrlHelpSideBarPrecondition.show();
	g_ctrlHelpSideBarOutcome.hide();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Shows the preconditions pane for the given entry
 *
 * @private
 *
 * @param {number} workingConditioningEventIndex - the conditioning event array index
 * @param {number} outcomeIndex - the outcome array index, or -1 to show nothing selected
 */
function showOutcomePane( workingConditioningEventIndex, outcomeIndex )
{
	// set up the edit index values
	g_conditioningEventEditIndex = workingConditioningEventIndex;
	g_preconditionEditIndex = -1;
	g_outcomeEditIndex = outcomeIndex;

	// init the form fields for this precondition
	initOutcomePaneFormFields();

	// switch to the precondition pane
	g_ctrlConditioningEventPane.hide();
	g_ctrlPreconditionPane.hide();
	g_ctrlOutcomePane.show();
	populateSideBar();

	// show the appropriate sidebar help
	g_ctrlHelpSideBarConditioningEvent.hide();
	g_ctrlHelpSideBarPrecondition.hide();
	g_ctrlHelpSideBarOutcome.show();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @returns {?ConditioningEvent}
 */
function getSelectedConditioningEvent()
{
	if( g_workingConditioningEvents === null || g_conditioningEventEditIndex === -1 )
	{
		return null;
	}
	else
	{
		return g_workingConditioningEvents[ g_conditioningEventEditIndex ];
	}
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Finds the precondition plugin with the given id
 *
 * @private
 *
 * @param {string} id
 *
 * @returns {?PreconditionPlugin}
 */
function getPreconditionPlugin( id )
{
	if( g_preconditionPlugins === null )
	{
		return null;
	}

	for( let i = 0; i < g_preconditionPlugins.length; ++i )
	{
		if( g_preconditionPlugins[ i ].id === id )
		{
			return g_preconditionPlugins[ i ];
		}
	}

	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Finds the outcome effect plugin with the given id
 *
 * @private
 *
 * @param {string} id
 *
 * @returns {?OutcomeEffectPlugin}
 */
function getOutcomeEffectPlugin( id )
{
	if( g_outcomeEffectPlugins === null )
	{
		return null;
	}

	for( let i = 0; i < g_outcomeEffectPlugins.length; ++i )
	{
		if( g_outcomeEffectPlugins[ i ].id === id )
		{
			return g_outcomeEffectPlugins[ i ];
		}
	}

	return null;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
