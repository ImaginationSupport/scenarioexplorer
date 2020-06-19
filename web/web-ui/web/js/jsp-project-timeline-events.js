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
 * Holds the User instance
 *
 * @private
 * @type {?User}
 */
let g_user = null;

/**
 * Holds the original list of existing timeline events
 *
 * @type {?Array< TimelineEvent >}
 */
let g_originalTimelineEvents = null;

/**
 * Holds the working array of timeline events
 *
 * @type {?Array< TimelineEvent >}
 */
let g_workingTimelineEvents = null;

/**
 * Holds the timeline event index currently being edited
 *
 * @type {number}
 */
let g_editIndex = -1;

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
 * Holds the jQuery control for the add/update button
 *
 * @type {?jQuery}
 */
let g_ctrlAddUpdateTimelineEventButton = null;

/**
 * Holds the jQuery control for the delete button
 *
 * @type {?jQuery}
 */
let g_ctrlDeleteTimelineEventButton = null;

/**
 * Holds the jQuery control for the working timeline events
 *
 * @type {?jQuery}
 */
let g_ctrlWorkingTimelineEventsHolder = null;

/**
 * Holds the jQuery control for the working timeline events
 *
 * @type {?jQuery}
 */
let g_ctrlWorkingTimelineEvents = null;

/**
 * Holds the jQuery control for the timeline event name
 *
 * @type {?jQuery}
 */
let g_ctrlTimelineEventNameInput = null;

/**
 * Holds the jQuery control for the timeline event description
 *
 * @type {?jQuery}
 */
let g_ctrlTimelineEventDescriptionTextArea = null;

/**
 * Holds the jQuery control for the timeline event start date
 *
 * @type {?jQuery}
 */
let g_ctrlTimelineEventStartDatePicker = null;

/**
 * Holds the jQuery control for the start icon
 *
 * @type {?jQuery}
 */
let g_ctrlTimelineEventStartDateIcon = null;

/**
 * Holds the jQuery control for the timeline event end date
 *
 * @type {?jQuery}
 */
let g_ctrlTimelineEventEndDatePicker = null;

/**
 * Holds the jQuery control for the end icon
 *
 * @type {?jQuery}
 */
let g_ctrlTimelineEventEndDateIcon = null;

/**
 * Holds the jQuery control for the timeline event URL
 *
 * @type {?jQuery}
 */
let g_ctrlTimelineEventUrlInput = null;

/**
 * Holds the jQuery control for the import holder
 *
 * @type {?jQuery}
 */
let g_ctrlImportHolder = null;

/**
 * Holds the jQuery control for the cancel button
 *
 * @type {?jQuery}
 */
let g_ctrlCancel = null;

/**
 * Holds the jQuery control for the save button
 *
 * @type {?jQuery}
 */
let g_ctrlSave = null;

/**
 * Holds the CSV import file reader
 *
 * @type {?FileReader}
 */
let g_csvReader = null;

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

	g_ctrlSave = $( '#button-save' );
	g_ctrlCancel = $( '#button-cancel' );

	g_ctrlAlert = $( '#form-error-alert' )
		.removeClass( 'd-none' )
		.hide();
	g_ctrlAddUpdateTimelineEventButton = $( '#button-add-update' )
		.on( 'click', runAddUpdateTimelineEvent.bind( this ) );
	g_ctrlDeleteTimelineEventButton = $( '#button-delete' )
		.on( 'click', runDeleteTimelineEvent.bind( this ) );

	g_ctrlImportHolder = $( '#import-holder' )
		.on( 'dragover', onImportDragOver.bind( this ) )
		.on( 'drop', onImportDrop.bind( this ) );

	g_ctrlWorkingTimelineEventsHolder = $( '#working-timeline-events-holder' )
		.addClass( 'clickable' )
		.on( 'click', runDeselectEdit.bind( this ) );
	g_ctrlWorkingTimelineEvents = $( '#working-timeline-events' );

	// load the user
	g_api.getUser( username, getCurrentUserCallback.bind( this ) );

	// load the project
	g_api.getProject( g_projectId, getProjectCallback.bind( this ) );

	const timelineEventId = g_nav.getTimelineEventIdUriParameter();
	if( timelineEventId )
	{
		$( '#page-header' ).text( 'Edit Timeline Event' );

		// start the API call to populate the data
		g_api.getTimelineEvent( g_projectId, timelineEventId, getTimelineEventCallback.bind( this ) );

		g_ctrlSave.on( 'click', runSaveEditSingle.bind( this ) );
		g_ctrlCancel.on( 'click', runCancelReturn.bind( this ) );

		g_ctrlImportHolder.hide();

		$( '#divider-line' ).hide();

		// mark this as the one being edited
		g_editIndex = 0;
	}
	else
	{
		// start the API call to populate the data
		g_api.listTimelineEvents( g_projectId, listTimelineEventsCallback.bind( this ) );

		g_ctrlSave.on( 'click', runSaveFull.bind( this ) );
		g_ctrlCancel.on( 'click', g_nav.redirectToProjectDetails.bind( g_nav, g_projectId ) );
	}

	// run this now to show the "loading..." message
	populateSideBar();

	g_ctrlTimelineEventNameInput = $( '#timeline-event-name' );
	g_ctrlTimelineEventDescriptionTextArea = $( '#timeline-event-description' );
	g_ctrlTimelineEventStartDatePicker = $( '#timeline-event-start-date' );
	g_ctrlTimelineEventStartDateIcon = $( '#timeline-event-start-date-icon' );
	g_ctrlTimelineEventEndDatePicker = $( '#timeline-event-end-date' );
	g_ctrlTimelineEventEndDateIcon = $( '#timeline-event-end-date-icon' );
	g_ctrlTimelineEventUrlInput = $( '#timeline-event-url' );

	initFormFields( null );

	g_csvReader = new FileReader();
	g_csvReader.onloadend = onImportCSV.bind( this );

	g_taglib.bindFormSubmitEnterEscape( 'timeline-event-form', this.runAddUpdateTimelineEvent.bind( this ), this.runDeselectEdit.bind( this ) );

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

	g_taglib.initDatePicker( g_ctrlTimelineEventStartDatePicker, g_ctrlTimelineEventStartDateIcon, g_user.getDateFormatPreference(), new Date() );
	g_taglib.initDatePicker( g_ctrlTimelineEventEndDatePicker, g_ctrlTimelineEventEndDateIcon, g_user.getDateFormatPreference(), new Date() );

	populateSideBar();

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
 * Called when the timeline events are loaded from the API
 *
 * @param {Array< TimelineEvent >} timelineEvents - the timeline events for this project
 */
function listTimelineEventsCallback( timelineEvents )
{
	g_workingTimelineEvents = timelineEvents;
	g_originalTimelineEvents = /** @type {Array< TimelineEvent >} */( g_util.cloneArray( timelineEvents ) );

	populateSideBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the timeline event is loaded from the API
 *
 * @param {TimelineEvent} timelineEvent
 */
function getTimelineEventCallback( timelineEvent )
{
	g_workingTimelineEvents = [ timelineEvent ];
	populateSideBar();

	initFormFields( timelineEvent );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the timeline event from the for fields
 *
 * @private
 * @returns {TimelineEvent|null} The timeline event instance, or null if there were errors
 */
function getTimelineEventFromFormFields()
{
	const name = g_ctrlTimelineEventNameInput.val().toString().trim();
	const description = g_ctrlTimelineEventDescriptionTextArea.val().toString().trim();
	const rawStartDate = g_ctrlTimelineEventStartDatePicker.val().toString().trim();
	const rawEndDate = g_ctrlTimelineEventEndDatePicker.val().toString().trim();
	const url = g_ctrlTimelineEventUrlInput.val().toString().trim();

	const parsedStartDate = g_util.parseDateOnly( rawStartDate );
	const parsedEndDate = g_util.parseDateOnly( rawEndDate );

	const errors = [];

	if( name.length === 0 )
	{
		errors.push( 'Timeline event name cannot be empty!' );
	}

	if( rawStartDate === '' )
	{
		errors.push( 'Please enter the start date!' )
	}

	if( parsedStartDate === null )
	{
		errors.push( 'Start date has invalid format.  Please use: ' + g_user.getDateFormatPreference() )
	}

	if( rawEndDate === '' )
	{
		errors.push( 'Please enter the end date!' )
	}

	if( parsedEndDate === null )
	{
		errors.push( 'End date has invalid format.  Please use: ' + g_user.getDateFormatPreference() )
	}

	if( parsedEndDate <= parsedStartDate )
	{
		errors.push( 'End date must be after the start date!' );
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
		return null;
	}
	else
	{
		g_ctrlAlert.hide();
	}

	const apiDataHelper = new ApiDataHelper();

	return apiDataHelper.createTimelineEventInstance( name, description, parsedStartDate, parsedEndDate, url );
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Add/Update Timeline Event button
 *
 * @private
 */
function runAddUpdateTimelineEvent()
{
	const timelineEvent = getTimelineEventFromFormFields();
	if( timelineEvent === null )
	{
		// there was an error with the user inputs, so bail
		return;
	}

	// everything is valid, so add/update the new timeline event
	if( g_editIndex === -1 )
	{
		g_workingTimelineEvents.push( timelineEvent );
	}
	else
	{
		g_workingTimelineEvents[ g_editIndex ].name = timelineEvent.name;
		g_workingTimelineEvents[ g_editIndex ].description = timelineEvent.description;
		g_workingTimelineEvents[ g_editIndex ].start = timelineEvent.start;
		g_workingTimelineEvents[ g_editIndex ].end = timelineEvent.end;
		g_workingTimelineEvents[ g_editIndex ].url = timelineEvent.url;
	}

	runDeselectEdit();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Delete button
 *
 * @private
 */
function runDeleteTimelineEvent()
{
	if( g_editIndex === -1 )
	{
		return;
	}

	g_workingTimelineEvents.splice( g_editIndex, 1 );

	g_editIndex = -1;

	populateSideBar();

	initFormFields( null );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the form input fields
 *
 * @private
 * @param {TimelineEvent} timelineEvent - the timeline event to initialize to, or null for blanks
 */
function initFormFields( timelineEvent )
{
	if( timelineEvent === null )
	{
		g_ctrlTimelineEventNameInput.val( '' );
		g_ctrlTimelineEventDescriptionTextArea.val( '' );
		g_ctrlTimelineEventUrlInput.val( '' );

		// set a default start date
		g_taglib.setDatePickerDate( g_ctrlTimelineEventStartDatePicker, new Date() );

		// set a default end date
		let defaultEndDate = new Date();
		defaultEndDate.setDate( defaultEndDate.getDate() + 7 );
		g_taglib.setDatePickerDate( g_ctrlTimelineEventEndDatePicker, defaultEndDate );

		g_ctrlAddUpdateTimelineEventButton.text( 'Add' );
		g_ctrlDeleteTimelineEventButton.prop( 'disabled', true );
	}
	else
	{
		// update all the form fields
		g_ctrlTimelineEventNameInput.val( timelineEvent.name );
		g_ctrlTimelineEventDescriptionTextArea.val( timelineEvent.description );
		g_taglib.setDatePickerDate( g_ctrlTimelineEventStartDatePicker, timelineEvent.start );
		g_taglib.setDatePickerDate( g_ctrlTimelineEventEndDatePicker, timelineEvent.end );
		g_ctrlTimelineEventUrlInput.val( timelineEvent.url );

		// update the button text
		g_ctrlAddUpdateTimelineEventButton.text( 'Update' );

		// enable the delete button
		g_ctrlDeleteTimelineEventButton.prop( 'disabled', false );
	}

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
	// TODO check if we should add the existing entry first

	const changes = g_util.determineAddUpdateDelete( g_workingTimelineEvents, g_originalTimelineEvents );
	// console.log( changes );

	g_saveCountdownQueueRemaining = 0;

	// deletes
	for( let i = 0; i < changes.deletes.length; ++i )
	{
		g_api.deleteTimelineEvent( g_projectId, changes.deletes[ i ], saveFullCallback.bind( this ) );
		++g_saveCountdownQueueRemaining;
	}

	// adds
	for( let i = 0; i < changes.adds.length; ++i )
	{
		g_api.newTimelineEvent( g_projectId, /** @type{TimelineEvent} */( changes.adds[ i ] ), saveFullCallback.bind( this ) );
		++g_saveCountdownQueueRemaining;
	}

	// updates
	for( let i = 0; i < changes.updates.length; ++i )
	{
		g_api.updateTimelineEvent( g_projectId, /** @type{TimelineEvent} */( changes.updates[ i ] ), saveFullCallback.bind( this ) );
		++g_saveCountdownQueueRemaining;
	}

	if( g_saveCountdownQueueRemaining === 0 )
	{
		g_nav.redirectToProjectDetails( g_projectId );
	}

	g_ctrlCancel.prop( 'disabled', true );
	g_ctrlSave.prop( 'disabled', true );
	g_ctrlAddUpdateTimelineEventButton.prop( 'disabled', true );
	g_ctrlDeleteTimelineEventButton.prop( 'disabled', true );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API completes one of the save calls (add/update/delete) and lets us track if all of the calls are complete
 */
function saveFullCallback()
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

function runSaveEditSingle()
{
	if( g_workingTimelineEvents.length === 0 )
	{
		g_api.deleteTimelineEvent( g_projectId, g_nav.getTimelineEventIdUriParameter(), deleteTimelineEventCallback.bind( this ) );
	}
	else
	{
		let timelineEvent = getTimelineEventFromFormFields();
		if( timelineEvent === null )
		{
			// there was an error with the user inputs, so bail
			return;
		}

		timelineEvent.id = g_nav.getTimelineEventIdUriParameter();

		g_api.updateTimelineEvent( g_projectId, timelineEvent, updateTimelineEventCallback.bind( this ) );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function updateTimelineEventCallback()
{
	g_nav.redirectToView( g_projectId, g_nav.getViewIdUriParameter() );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function deleteTimelineEventCallback()
{
	g_nav.redirectToView( g_projectId, g_nav.getViewIdUriParameter() );
	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the user button when editing a single timeline event
 */
function runCancelReturn()
{
	g_nav.redirectToView( g_projectId, g_nav.getViewIdUriParameter() );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the left-hand column that shows the timeline events
 */
function populateSideBar()
{
	if( g_user === null )
	{
		return;
	}

	g_ctrlWorkingTimelineEvents.empty();

	// add the header
	$( '<li/>' )
		.addClass( 'list-group-item' )
		.addClass( 'list-group-item-action' )
		.addClass( 'font-weight-bold' )
		.addClass( 'bg-secondary' )
		.addClass( 'text-light' )
		.addClass( 'p-2' )
		.text( 'Timeline Events' )
		.appendTo( g_ctrlWorkingTimelineEvents );

	if( g_workingTimelineEvents === null )
	{
		$( '<li/>' )
			.addClass( 'list-group-item' )
			.addClass( 'list-group-item-action' )
			.addClass( 'p-2' )
			.addClass( 'pl-3' )
			.text( 'Loading, please wait...' )
			.appendTo( g_ctrlWorkingTimelineEvents );
	}
	else
	{
		for( let i = 0; i < g_workingTimelineEvents.length; ++i )
		{
			$( '<li/>' )
				.addClass( 'list-group-item' )
				.addClass( 'list-group-item-action' )
				.addClass( 'p-2' )
				.addClass( 'pl-3' )
				.addClass( 'clickable' )
				.text( g_workingTimelineEvents[ i ].name
					+ ' ('
					+ g_util.formatDate( g_workingTimelineEvents[ i ].start, g_user.getDateFormatPreference() )
					+ ' to '
					+ g_util.formatDate( g_workingTimelineEvents[ i ].end, g_user.getDateFormatPreference() )
					+ ')' )
				.on( 'click', editExistingTimelineEvent.bind( this, i ) )
				.appendTo( g_ctrlWorkingTimelineEvents );
		}

		if( g_workingTimelineEvents.length === 0 )
		{
			$( '<li/>' )
				.addClass( 'list-group-item' )
				.addClass( 'list-group-item-action' )
				.addClass( 'p-2' )
				.addClass( 'pl-3' )
				.text( '(None)' )
				.appendTo( g_ctrlWorkingTimelineEvents );
		}
	}

	if( g_editIndex > -1 )
	{
		g_ctrlWorkingTimelineEvents.find( 'li:nth-child(' + ( g_editIndex + 2 ).toString() + ')' ).addClass( 'bg-medium' );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks to edit an existing timeline event
 *
 * @param {number} index - the working array index
 * @param {jQuery.Event} e - the jQuery event
 * @private
 */
function editExistingTimelineEvent( index, e )
{
	e.stopPropagation();

	// mark that we are editing this index
	g_editIndex = index;

	// redraw the list (to reset any previous highlights) and highlight this one
	populateSideBar();

	initFormFields( g_workingTimelineEvents[ index ] );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Deselects an existing edit
 */
function runDeselectEdit()
{
	// if we are only editing the one timeline event, don't allow the user to deselect
	if( !g_nav.getTimelineEventIdUriParameter() )
	{
		// mark that we aren't editing
		g_editIndex = -1;

		// re-initialize the form fields
		initFormFields( null );
	}

	// redraw the list (to reset any previous highlights)
	populateSideBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user is dragging files over the import holder
 *
 * @private
 * @param {jQuery.Event} e
 */
function onImportDragOver( e )
{
	e.stopPropagation();
	e.preventDefault();
	e.originalEvent.dataTransfer.dropEffect = 'copy';

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user drops files onto the import holder
 *
 * @private
 * @param {jQuery.Event} e
 */
function onImportDrop( e )
{
	e.stopPropagation();
	e.preventDefault();

	const files = e.originalEvent.dataTransfer.files;

	if( files.length === 0 )
	{
		return;
	}

	for( let i = 0; i < files.length; ++i )
	{
		g_csvReader.readAsText( files[ i ] );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the file importer parses the drag/drop file
 *
 * @private
 * @param e
 */
function onImportCSV( e )
{
	const lines = g_util.parseCSV( e.target.result.toString() );

	// col 0 - name
	// col 1 - description
	// col 2 - start date
	// col 3 - end date
	// col 4 - url

	const errors = [];

	const parsedTimelineEvents = [];

	const apiDataHelper = new ApiDataHelper();

	for( let i = 0; i < lines.length; ++i )
	{
		if( lines[ i ].length < 4 )
		{
			errors.push( 'Row ' + ( i + 1 ).toString() + ' - Invalid number of columns!  Name, Description, Start Date and End Date are required!' );
		}
		else if( lines[ i ][ 0 ].length === 0 )
		{
			errors.push( 'Row ' + ( i + 1 ).toString() + ' - Name cannot be blank!' );
		}
		else if( lines[ i ][ 2 ].length === 0 )
		{
			errors.push( 'Row ' + ( i + 1 ).toString() + ' - Start Date cannot be blank!' );
		}
		else if( lines[ i ][ 3 ].length === 0 )
		{
			errors.push( 'Row ' + ( i + 1 ).toString() + ' - End Date cannot be blank!' );
		}
		else
		{
			const parsedStartDate = g_util.parseDateOnly( lines[ i ][ 2 ] );
			const parsedEndDate = g_util.parseDateOnly( lines[ i ][ 3 ] );

			if( parsedStartDate === null )
			{
				errors.push( 'Row ' + ( i + 1 ).toString() + ' - Unable to parse Start Date: ' + lines[ i ][ 2 ] );
			}
			else if( parsedEndDate === null )
			{
				errors.push( 'Row ' + ( i + 1 ).toString() + ' - Unable to parse End Date: ' + lines[ i ][ 3 ] );
			}
			else
			{
				parsedTimelineEvents.push( apiDataHelper.createTimelineEventInstance(
					lines[ i ][ 0 ],
					lines[ i ][ 1 ],
					parsedStartDate,
					parsedEndDate,
					lines[ i ].length > 4 ? lines[ i ][ 4 ] : null
				) );
			}
		}
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
		return null;
	}
	else
	{
		g_ctrlAlert.hide();
	}

	g_workingTimelineEvents = g_workingTimelineEvents.concat( parsedTimelineEvents );

	g_editIndex = -1;
	populateSideBar();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
