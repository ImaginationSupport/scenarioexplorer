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
 * Holds the existing project id, or null if editing an existing project template
 *
 * @type {?string}
 */
let g_projectId = null;

/**
 * Holds the existing project template id, or null if creating from an existing project
 *
 * @type {?string}
 */
let g_projectTemplateId = null;

/**
 * Holds the existing project template
 *
 * @type {?ProjectTemplate}
 */
let g_projectTemplate = null;

/**
 * Holds the existing project
 *
 * @type {?Project}
 */
let g_project = null;

/**
 * Holds the existing features
 *
 * @type {?Array< Feature >}
 */
let g_features = null;

/**
 * Holds the existing timeline events
 *
 * @type {?Array< TimelineEvent >}
 */
let g_timelineEvents = null;

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
 * Holds the jQuery control for the project template name
 *
 * @type {?jQuery}
 */
let g_ctrlProjectTemplateName = null;

/**
 * Holds the jQuery control for the project template description
 *
 * @type {?jQuery}
 */
let g_ctrlProjectTemplateDescription = null;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the JSP to initialize the page
 *
 * @param {string} username
 */
function initJSP( username )
{
	g_projectId = g_nav.getProjectIdUriParameter();
	g_projectTemplateId = g_nav.getProjectTemplateIdUriParameter();

	g_ctrlAlert = $( '#form-error-alert' ).removeClass( 'd-none' ).hide();
	g_ctrlSave = $( '#button-save' ).on( 'click', runSave.bind( this ) );
	g_ctrlCancel = $( '#button-cancel' ).on( 'click', runCancel.bind( this ) );
	g_ctrlDelete = $( '#button-delete' ).on( 'click', onDelete.bind( this ) );

	g_ctrlProjectTemplateName = $( '#project-template-name' );
	g_ctrlProjectTemplateDescription = $( '#project-template-description' );

	const ctrlPageHeader = $( '#page-header' );

	if( g_projectId )
	{
		g_api.getProject( g_projectId, getProjectCallback.bind( this ) );
		g_api.listFeatures( g_projectId, listFeaturesCallback.bind( this ) );
		g_api.listTimelineEvents( g_projectId, listTimelineEventsCallback.bind( this ) );

		ctrlPageHeader.text( 'Create Project Template' );

		g_ctrlDelete.hide();
	}

	if( g_projectTemplateId )
	{
		g_api.getProjectTemplate( g_projectTemplateId, getProjectTemplateCallback.bind( this ) );

		ctrlPageHeader.text( 'Update Project Template' );
	}

	g_taglib.bindFormSubmitEnterEscape( 'project-template-form', runSave.bind( this ), runCancel.bind( this ) );

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
	g_project = project;

	g_ctrlProjectTemplateName.val( g_project.name );
	g_ctrlProjectTemplateDescription.val( g_project.description );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the features are loaded from the API
 *
 * @private
 * @param {Array< Feature >} features - the features for this project
 */
function listFeaturesCallback( features )
{
	g_features = features;

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
	g_timelineEvents = timelineEvents;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API gets the existing project template
 *
 * @private
 *
 * @param {ProjectTemplate} projectTemplate - the project template
 */
function getProjectTemplateCallback( projectTemplate )
{
	g_projectTemplate = projectTemplate;

	g_ctrlProjectTemplateName.val( g_projectTemplate.name );
	g_ctrlProjectTemplateDescription.val( g_projectTemplate.description );

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
	const name = g_ctrlProjectTemplateName.val().toString().trim();
	const description = g_ctrlProjectTemplateDescription.val().toString().trim();

	const errors = [];

	if( name.length === 0 )
	{
		errors.push( 'Project template name cannot be empty!' );
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

	if( !g_projectTemplateId )
	{
		// create a new project template
		const apiDataHelper = new ApiDataHelper();

		g_api.newProjectTemplate( apiDataHelper.createProjectTemplateInstance(
			name,
			description,
			g_project.start,
			g_project.end,
			g_project.increment,
			g_projectId,
			g_features,
			g_timelineEvents
		), createProjectTemplateCallback.bind( this ) );
	}
	else
	{
		// update the existing project template
		g_projectTemplate.name = name;
		g_projectTemplate.description = description;

		g_api.updateProjectTemplate( g_projectTemplate, updateProjectTemplateCallback.bind( this ) );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API returns from creating the new project template
 *
 * @private
 * @param {ProjectTemplate} projectTemplate
 */
function createProjectTemplateCallback( projectTemplate )
{
	g_nav.redirectToProjectDetails( g_projectId );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API returns from updating the project template
 *
 * @private
 * @param {ProjectTemplate} projectTemplate
 */
function updateProjectTemplateCallback( projectTemplate )
{
	if( g_projectId )
	{
		g_nav.redirectToProjectDetails( g_projectId );
	}
	else if( g_projectTemplate )
	{
		g_nav.redirectToProjectDetails( g_projectTemplate.sourceProjectId );
	}
	else
	{
		g_nav.redirectToIndex();
	}

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
 * Called when the user clicks the button to delete the project template
 *
 * @private
 */
function onDelete()
{
	// TODO finish!

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
