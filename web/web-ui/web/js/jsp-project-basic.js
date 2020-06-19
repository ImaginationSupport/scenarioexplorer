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
let g_existingProject = null;

/**
 * Holds the User instance
 *
 * @private
 * @type {?User}
 */
let g_user = null;

/**
 * Holds the list of all users
 *
 * @private
 * @type {Array< User >}
 */
let g_users = null;

/**
 * Holds the project templates
 *
 * @private
 * @type {?Array< ProjectTemplate >}
 */
let g_projectTemplates = null;

/**
 * Holds the jQuery control for the project name (create section)
 *
 * @type {?jQuery}
 */
let g_ctrlCreateProjectName = null;

/**
 * Holds the jQuery control for the project description (create section)
 *
 * @type {?jQuery}
 */
let g_ctrlCreateProjectDescription = null;

/**
 * Holds the jQuery control for the project start date (create section)
 *
 * @type {?jQuery}
 */
let g_ctrlCreateProjectStartDate = null;

/**
 * Holds the jQuery control for the project start date icon (create section)
 *
 * @type {?jQuery}
 */
let g_ctrlCreateProjectStartDateIcon = null;

/**
 * Holds the jQuery control for the project end date (create section)
 *
 * @type {?jQuery}
 */
let g_ctrlCreateProjectEndDate = null;

/**
 * Holds the jQuery control for the project end date icon (create section)
 *
 * @type {?jQuery}
 */
let g_ctrlCreateProjectEndDateIcon = null;

/**
 * Holds the jQuery control for the project increment (create section)
 *
 * @type {?jQuery}
 */
let g_ctrlCreateProjectIncrement = null;

/**
 * Holds the jQuery control for the project name (edit section)
 *
 * @type {?jQuery}
 */
let g_ctrlEditProjectName = null;

/**
 * Holds the jQuery control for the project description (edit section)
 *
 * @type {?jQuery}
 */
let g_ctrlEditProjectDescription = null;

/**
 * Holds the jQuery control for the project start date (edit section)
 *
 * @type {?jQuery}
 */
let g_ctrlEditProjectStartDate = null;

/**
 * Holds the jQuery control for the project start date icon (edit section)
 *
 * @type {?jQuery}
 */
let g_ctrlEditProjectStartDateIcon = null;

/**
 * Holds the jQuery control for the project end date (edit section)
 *
 * @type {?jQuery}
 */
let g_ctrlEditProjectEndDate = null;

/**
 * Holds the jQuery control for the project end date icon (edit section)
 *
 * @type {?jQuery}
 */
let g_ctrlEditProjectEndDateIcon = null;

/**
 * Holds the jQuery control for the project increment (edit section)
 *
 * @type {?jQuery}
 */
let g_ctrlEditProjectIncrement = null;

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
let g_ctrlSave = null;

/**
 * Holds the jQuery control for the cancel button
 *
 * @type {?jQuery}
 */
let g_ctrlCancel = null;

/**
 * Holds the jQuery control for the delete button
 *
 * @type {?jQuery}
 */
let g_ctrlDelete = null;

/**
 * Holds the jQuery pane for the create project controls
 *
 * @type {?jQuery}
 */
let g_ctrlCreateProjectHolder = null;

/**
 * Holds the jQuery pane for the edit project controls
 *
 * @type {?jQuery}
 */
let g_ctrlEditProjectHolder = null;

/**
 * Holds the jQuery pane for the delete confirm controls
 *
 * @type {?jQuery}
 */
let g_ctrlDeleteProjectPane = null;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the JSP to initialize the page
 *
 * @param {string} username
 */
function initJSP( username )
{
	g_projectId = g_nav.getProjectIdUriParameter();

	// create controls
	g_ctrlCreateProjectName = $( '#create-project-name' );
	g_ctrlCreateProjectDescription = $( '#create-project-description' );
	g_ctrlCreateProjectStartDate = $( '#create-project-start-date' );
	g_ctrlCreateProjectStartDateIcon = $( '#create-project-start-date-icon' );
	g_ctrlCreateProjectEndDate = $( '#create-project-end-date' );
	g_ctrlCreateProjectEndDateIcon = $( '#create-project-end-date-icon' );
	g_ctrlCreateProjectIncrement = $( '#create-project-increment' );

	// edit controls
	g_ctrlEditProjectName = $( '#edit-project-name' );
	g_ctrlEditProjectDescription = $( '#edit-project-description' );
	g_ctrlEditProjectStartDate = $( '#edit-project-start-date' );
	g_ctrlEditProjectStartDateIcon = $( '#edit-project-start-date-icon' );
	g_ctrlEditProjectEndDate = $( '#edit-project-end-date' );
	g_ctrlEditProjectEndDateIcon = $( '#edit-project-end-date-icon' );
	g_ctrlEditProjectIncrement = $( '#edit-project-increment' );

	g_ctrlAlert = $( '#form-error-alert' ).removeClass( 'd-none' ).hide();
	g_ctrlSave = $( '#button-save' ).on( 'click', runSave.bind( this ) );
	g_ctrlCancel = $( '#button-cancel' ).on( 'click', runCancel.bind( this ) );
	g_ctrlDelete = $( '#button-delete' ).on( 'click', onDeleteProject.bind( this ) );

	g_ctrlCreateProjectHolder = $( '#create-project-holder' ).hide();
	g_ctrlEditProjectHolder = $( '#edit-project-holder' ).hide();
	g_ctrlDeleteProjectPane = $( '#delete-project-confirm' ).hide();

	$( '#delete-project-confirm-delete' ).on( 'click', onDeleteProjectConfirm.bind( this ) );
	$( '#delete-project-confirm-cancel' ).on( 'click', onDeleteProjectCancel.bind( this ) );

	g_taglib.bindFormSubmitEnterEscape( 'create-project-form', runSave.bind( this ), runCancel.bind( this ) );
	g_taglib.bindFormSubmitEnterEscape( 'edit-project-form', runSave.bind( this ), runCancel.bind( this ) );

	if( g_projectId )
	{
		// project already exists
		g_ctrlEditProjectHolder.show();

		// show this as an update
		$( '#page-header' ).text( 'Update Project' );
		g_api.getProject( g_projectId, getProjectCallback.bind( this ) );
	}
	else
	{
		// project does not exist
		g_ctrlCreateProjectHolder.show();

		// hide the delete button
		g_ctrlDelete.hide();

		g_taglib.initializeAccordion( 'create-project-accordion' );

		// connect the drag/drop handlers
		g_taglib.initDragDropTarget(
			$( '#import-drag-drop-holder' ),
			'If you would like to import a project you previously exported, you can use the file selection or simply drag/drop the file onto the page.',
			null,
			null,
			this.onImportDrop.bind( this ) );

		g_api.listProjectTemplates( listProjectTemplates.bind( this ) );
	}

	// load the user
	g_api.getUser( username, getCurrentUserCallback.bind( this ) );

	// list all the users
	g_api.listUsers( listUsersCallback.bind( this ) );

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
	g_existingProject = project;

	// update the notifications in the breadcrumbs
	g_nav.updateBreadcrumbNotifications( project.id, project.notifications );

	populateEditExisting();

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

	if( !g_projectId )
	{
		// set a default start date
		const defaultStartDate = new Date();
		defaultStartDate.setDate( 1 );
		g_taglib.initDatePicker( g_ctrlCreateProjectStartDate, g_ctrlCreateProjectStartDateIcon, g_user.getDateFormatPreference(), defaultStartDate );

		// set a default end date
		const defaultEndDate = new Date();
		defaultEndDate.setTime( defaultStartDate.getTime() );
		defaultEndDate.setFullYear( defaultEndDate.getFullYear() + 1 );
		g_taglib.initDatePicker( g_ctrlCreateProjectEndDate, g_ctrlCreateProjectEndDateIcon, g_user.getDateFormatPreference(), defaultEndDate );
	}

	populateEditExisting();

	populateProjectTemplates();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {Array<ProjectTemplate>} templates
 */
function listProjectTemplates( templates )
{
	g_projectTemplates = templates;

	populateProjectTemplates();

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

	populateProjectTemplates();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the edit exiting controls
 */
function populateEditExisting()
{
	if( g_existingProject === null || g_user === null )
	{
		return;
	}

	g_ctrlEditProjectName.val( g_existingProject.name );
	g_ctrlEditProjectDescription.val( g_existingProject.description );

	g_taglib.initDatePicker( g_ctrlEditProjectStartDate, g_ctrlEditProjectStartDateIcon, g_user.getDateFormatPreference(), g_existingProject.start );
	g_taglib.initDatePicker( g_ctrlEditProjectEndDate, g_ctrlEditProjectEndDateIcon, g_user.getDateFormatPreference(), g_existingProject.end );

	g_ctrlEditProjectIncrement.val( g_existingProject.increment.toString() );

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
	const name = !g_projectId
		? g_ctrlCreateProjectName.val().toString().trim()
		: g_ctrlEditProjectName.val().toString().trim();
	const description = !g_projectId
		? g_ctrlCreateProjectDescription.val().toString().trim()
		: g_ctrlEditProjectDescription.val().toString().trim();
	const rawStartDate = !g_projectId
		? g_ctrlCreateProjectStartDate.val().toString().trim()
		: g_ctrlEditProjectStartDate.val().toString().trim();
	const rawEndDate = !g_projectId
		? g_ctrlCreateProjectEndDate.val().toString().trim()
		: g_ctrlEditProjectEndDate.val().toString().trim();
	const increment = !g_projectId
		? parseInt( g_ctrlCreateProjectIncrement.val().toString().trim(), 10 )
		: parseInt( g_ctrlEditProjectIncrement.val().toString().trim(), 10 );

	const selectedAccordionId = g_taglib.getActiveAccordion( 'create-project-accordion' );
	const selectedProjectTemplate = g_taglib.getSelectedRadioButton( 'available-project-template' );

	const errors = [];

	const parsedStartDate = g_util.parseDateOnly( rawStartDate );
	const parsedEndDate = g_util.parseDateOnly( rawEndDate );

	if( name.length === 0 )
	{
		errors.push( 'Project name cannot be empty!' );
	}

	switch( selectedAccordionId )
	{
		case 'accordion-heading-empty':
			if( rawStartDate === '' )
			{
				errors.push( 'Please enter a start date!' )
			}
			else if( parsedStartDate === null )
			{
				errors.push( 'Start date has invalid format.  Please use: YYYY-MM-DD' )
			}

			if( rawEndDate === '' )
			{
				errors.push( 'Please enter an end date!' )
			}
			else if( parsedEndDate === null )
			{
				errors.push( 'End date has invalid format.  Please use: YYYY-MM-DD' )
			}
			else if( parsedEndDate <= parsedStartDate )
			{
				errors.push( 'End date must be after the start date!' );
			}
			break;

		case 'accordion-heading-from-template' :
			if( !selectedProjectTemplate )
			{
				errors.push( 'Please select the project template you wish to use.' );
			}
			break;

		default:
			break;
	}

	if( errors.length > 0 )
	{
		g_util.showAlertDiv( g_ctrlAlert, errors );
		return;
	}
	else
	{
		g_ctrlAlert.hide();
	}

	if( !g_projectId )
	{
		// create project
		const apiDataHelper = new ApiDataHelper();

		switch( selectedAccordionId )
		{
			case 'accordion-heading-empty':
				g_api.newProject( apiDataHelper.createProjectInstance( name, description, parsedStartDate, parsedEndDate, increment ), createProjectCallback.bind( this ) );
				break;

			case 'accordion-heading-from-template' :
				g_api.newProjectFromTemplate( selectedProjectTemplate, name, description, createProjectCallback.bind( this ) );
				break;

			default:
				break;
		}
	}
	else
	{
		// update project
		g_existingProject.name = name;
		g_existingProject.description = description;
		g_existingProject.start = parsedStartDate;
		g_existingProject.end = parsedEndDate;
		g_existingProject.increment = increment;

		g_api.updateProject( g_existingProject, updateProjectCallback.bind( this ) );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API returns from creating the new project
 *
 * @private
 *
 * @param {Project} project
 */
function createProjectCallback( project )
{
	g_projectId = project.id;

	g_nav.redirectToProjectDetails( g_projectId );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API returns from updating the project
 *
 * @private
 */
function updateProjectCallback()
{
	g_nav.redirectToProjectDetails( g_projectId );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Cancel button
 *
 * @private
 */
function runCancel()
{
	if( !g_projectId )
	{
		g_nav.redirectToIndex();
	}
	else
	{
		g_nav.redirectToProjectDetails( g_projectId );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the button to delete the project
 *
 * @private
 */
function onDeleteProject()
{
	g_ctrlEditProjectHolder.hide();
	g_ctrlDeleteProjectPane.show();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the button to confirm deleting the project
 *
 * @private
 */
function onDeleteProjectConfirm()
{
	g_api.deleteProject( g_projectId, this.onDeleteProjectConfirmCallback.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function onDeleteProjectConfirmCallback()
{
	g_nav.redirectToIndex();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the button to cancel deleting the project
 *
 * @private
 */
function onDeleteProjectCancel()
{
	g_ctrlDeleteProjectPane.hide();
	g_ctrlEditProjectHolder.show();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user drops files onto the import holder
 *
 * @private
 * @param {jQuery} target
 * @param {string} filename
 * @param {string} fileContents
 */
function onImportDrop( target, filename, fileContents )
{
	g_api.importProject( JSON.parse( fileContents ), onImportCompleteCallback.bind( this ) );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {Project} project
 */
function onImportCompleteCallback( project )
{
	g_nav.redirectToProjectDetails( project.id );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Populates the available project templates
 *
 * @private
 */
function populateProjectTemplates()
{
	// we need the user and project templates to be loaded first
	if( !g_user || !g_users || !g_projectTemplates )
	{
		return;
	}

	const projectTemplatesTable = $( '#project-template' ).empty();

	for( let i = 0; i < g_projectTemplates.length; ++i )
	{
		const tr = g_taglib.generateTableRow( projectTemplatesTable )
			.addClass( 'clickable' );

		const creator = getUser( g_projectTemplates[ i ].creatorId );

		let radioCell = g_taglib.generateTableCell( null, tr ).addClass( 'text-center' );
		const radioButton = $( '<input/>', { 'type' : 'radio', 'value' : g_projectTemplates[ i ].id } )
			.addClass( 'mt-2' )
			.attr( 'id', 'available-project-template-' + i.toString() )
			.attr( 'name', 'available-project-template' )
			.appendTo( radioCell );

		tr.on( 'click', onClickProjectTemplateRow.bind( this, radioButton ) );

		g_taglib.generateTableCell( g_taglib.generateIconWithText( g_taglib.Icons.PROJECT, g_projectTemplates[ i ].name, null, false ), tr );
		g_taglib.generateTableCell( g_util.formatDate( g_projectTemplates[ i ].createdOn, g_user.getDateFormatPreference() ), tr );
		g_taglib.generateTableCell( creator === null ? 'Unknown user' : creator.fullName, tr );
		g_taglib.generateTableCell( g_projectTemplates[ i ].description, tr );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Callback when the user clicks an existing project template row
 *
 * @param {jQuery} radioButton
 * @param e
 */
function onClickProjectTemplateRow( radioButton, e )
{
	e.stopPropagation();

	radioButton.prop( 'checked', true );

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