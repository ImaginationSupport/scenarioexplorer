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
 * Holds the project id
 *
 * @type {?string}
 */
let g_projectId = null;

/**
 * Holds the original list of existing features
 *
 * @type {?Array< Feature >}
 */
let g_originalFeatures = null;

/**
 * Holds the working array of features
 *
 * @type {?Array< Feature >}
 */
let g_workingFeatures = null;

/**
 * Holds the feature types
 *
 * @type {Array< FeatureTypePlugin >}
 */
let g_featureTypes = [];

/**
 * Holds the projectors
 *
 * @type {Array< ProjectorPlugin >}
 */
let g_projectors = [];

/**
 * Holds the feature index currently being edited
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
let g_ctrlAddUpdateFeatureButton = null;

/**
 * Holds the jQuery control for the delete button
 *
 * @type {?jQuery}
 */
let g_ctrlDeleteFeatureButton = null;

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
 * Holds the jQuery control for the working features
 *
 * @type {?jQuery}
 */
let g_ctrlWorkingFeaturesHolder = null;

/**
 * Holds the jQuery control for the working features
 *
 * @type {?jQuery}
 */
let g_ctrlWorkingFeatures = null;

/**
 * Holds the jQuery control for the feature name
 *
 * @type {?jQuery}
 */
let g_ctrlFeatureNameInput = null;

/**
 * Holds the jQuery control for the feature description
 *
 * @type {?jQuery}
 */
let g_ctrlFeatureDescriptionTextArea = null;

/**
 * Holds the jQuery control for the feature type
 *
 * @type {?jQuery}
 */
let g_ctrlFeatureTypeDropdown = null;

/**
 * Holds the jQuery control for the feature type configuration
 *
 * @type {?jQuery}
 */
let g_ctrlFeatureConfigHolder = null;

/**
 * Holds the jQuery control for the feature type help
 *
 * @type {?jQuery}
 */
let g_ctrlSideBarFeatureTypeHelpHolder = null;

/**
 * Holds the jQuery control for the projector
 *
 * @type {?jQuery}
 */
let g_ctrlProjectorDropdown = null;

/**
 * Holds the jQuery control for the projector config
 *
 * @type {?jQuery}
 */
let g_ctrlProjectorConfigHolder = null;

/**
 * Holds the jQuery control for the projector help
 *
 * @type {?jQuery}
 */
let g_ctrlSideBarProjectorHelpHolder = null;

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

	g_ctrlAlert = $( '#form-error-alert' )
		.removeClass( 'd-none' )
		.hide();
	g_ctrlAddUpdateFeatureButton = $( '#button-add-update-feature' )
		.on( 'click', runAddUpdateFeature.bind( this ) );
	g_ctrlDeleteFeatureButton = $( '#button-delete-feature' )
		.on( 'click', runDeleteFeature.bind( this ) );
	g_ctrlSaveButton = $( '#button-save' )
		.on( 'click', runSave.bind( this ) );
	g_ctrlCancelButton = $( '#button-cancel' )
		.on( 'click', g_nav.redirectToProjectDetails.bind( g_nav, g_projectId ) );
	g_ctrlWorkingFeaturesHolder = $( '#working-features-holder' )
		.addClass( 'clickable' )
		.on( 'click', runDeselectEdit.bind( this ) );
	g_ctrlWorkingFeatures = $( '#working-features' );

	g_ctrlFeatureNameInput = $( '#feature-name' );
	g_ctrlFeatureDescriptionTextArea = $( '#feature-description' );
	g_ctrlFeatureTypeDropdown = $( '#feature-type' );
	g_ctrlFeatureConfigHolder = $( '#feature-config' );
	g_ctrlProjectorDropdown = $( '#projector-type' );
	g_ctrlProjectorConfigHolder = $( '#projector-config' );

	g_ctrlSideBarFeatureTypeHelpHolder = $( '#sidebar-feature-type-config-help-holder' );
	g_ctrlSideBarProjectorHelpHolder = $( '#sidebar-projector-config-help-holder' );

	// run this now to show the "loading..." message
	populateSideBar();

	// load the project
	g_api.getProject( g_projectId, getProjectCallback.bind( this ) );

	// start all the API calls to populate the data
	g_api.listFeatures( g_projectId, listFeaturesCallback.bind( this ) );
	g_pluginTagLib.fetchFeatureTypePlugins( g_featureTypes, true, this.listFeatureTypesCallback.bind( this ), null, g_projectId, null, null );
	g_pluginTagLib.fetchProjectorPlugins( g_projectors, true, this.listProjectorsCallback.bind( this ), null, g_projectId, null, null );

	// initialize the feature type dropdown
	g_ctrlFeatureTypeDropdown.empty();
	g_taglib.attachDropdownEventHandlers( g_ctrlFeatureTypeDropdown );
	g_taglib.generateDropdownOption( g_ctrlFeatureTypeDropdown, 'Loading, please wait...', null, true, false, null, null );

	// initialize the feature type config
	onSelectFeatureType( null, null );

	// initialize the projector type dropdown
	g_ctrlProjectorDropdown.empty();
	g_taglib.attachDropdownEventHandlers( g_ctrlProjectorDropdown );
	g_taglib.generateDropdownOption( g_ctrlProjectorDropdown, 'Loading, please wait...', null, true, false, null, null );

	// initialize the projector config
	onSelectProjector( null, null );

	g_ctrlDeleteFeatureButton.prop( 'disabled', true );

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
 * Called when the features are loaded from the API
 *
 * @private
 * @param {Array< Feature >} features - the features for this project
 */
function listFeaturesCallback( features )
{
	g_workingFeatures = features;
	g_originalFeatures = /** @type {Array< Feature >} */( g_util.cloneArray( features ) );

	// both features and feature type callback need to be complete before we can proceed
	if( g_featureTypes !== null )
	{
		populateSideBar();
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the feature types are loaded
 *
 * @private
 * @param {Array< FeatureTypePlugin >} featureTypes - the available feature types
 */
function listFeatureTypesCallback( featureTypes )
{
	g_ctrlFeatureTypeDropdown.empty();

	// add the top entry
	g_taglib.generateDropdownOption(
		g_ctrlFeatureTypeDropdown,
		'Please select...',
		null,
		false,
		false,
		null,
		onSelectFeatureType.bind( this, null, null ) );

	// add all the other entries
	for( let i = 0; i < g_featureTypes.length; ++i )
	{
		g_taglib.generateDropdownOption(
			g_ctrlFeatureTypeDropdown,
			g_featureTypes[ i ].name,
			g_featureTypes[ i ].id,
			false,
			false,
			null,
			onSelectFeatureType.bind( this, g_featureTypes[ i ], null ) );
	}

	// both features and feature type callback need to be complete before we can proceed
	if( g_workingFeatures !== null )
	{
		populateSideBar();
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the API when the projectors
 *
 * @private
 * @param {Array< Projector >} projectors
 */
function listProjectorsCallback( projectors )
{
	filterProjectors( null );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Add/Update Feature button
 *
 * @private
 */
function runAddUpdateFeature()
{
	const name = g_ctrlFeatureNameInput.val().toString().trim();
	const description = g_ctrlFeatureDescriptionTextArea.val().toString().trim();
	const featureTypeId = g_taglib.getDropdownSelectedValue( g_ctrlFeatureTypeDropdown );

	const errors = [];

	if( name.length === 0 )
	{
		errors.push( 'Feature name cannot be empty!' );
	}

	if( featureTypeId === null )
	{
		errors.push( 'Please select the feature type!' );
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

	// get the feature type plugin
	const featureType = getFeatureType( featureTypeId );
	if( featureType === null )
	{
		g_util.showError( 'Unknown feature type: ' + featureTypeId );
		return;
	}

	// get the feature type config
	const featureConfig = g_pluginTagLib.getConfigUserInterfaceData( g_ctrlFeatureConfigHolder, featureType );
	if( featureConfig === null )
	{
		return;
	}

	// get the projector
	const projectorId = g_taglib.getDropdownSelectedValue( g_ctrlProjectorDropdown );

	// get the projector config
	let projectorConfig = null;
	if( projectorId !== null )
	{
		let projector = getProjector( projectorId );
		if( projector === null )
		{
			g_util.showError( 'Unknown projector: ' + projectorId );
			return;
		}

		projectorConfig = g_pluginTagLib.getConfigUserInterfaceData( g_ctrlProjectorConfigHolder, projector );
		if( projectorConfig === null )
		{
			return;
		}
	}

	// everything is valid, so add/update the new feature
	const apiDataHelper = new ApiDataHelper();
	const feature = apiDataHelper.createFeatureInstance( name, description, featureTypeId, featureConfig, projectorId, projectorConfig );

	if( g_editIndex === -1 )
	{
		g_workingFeatures.push( feature );
	}
	else
	{
		g_workingFeatures[ g_editIndex ] = feature;
	}

	g_editIndex = -1;
	populateSideBar();

	initFormFields();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks the Delete button
 *
 * @private
 */
function runDeleteFeature()
{
	if( g_editIndex === -1 )
	{
		return;
	}

	g_workingFeatures.splice( g_editIndex, 1 );

	g_editIndex = -1;

	populateSideBar();

	initFormFields();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Initializes the form input fields
 *
 * @private
 */
function initFormFields()
{
	g_ctrlFeatureNameInput.val( '' );
	g_ctrlFeatureDescriptionTextArea.val( '' );
	g_taglib.setDropdownSelectedItem( g_ctrlFeatureTypeDropdown, null );
	g_taglib.setDropdownSelectedItem( g_ctrlProjectorDropdown, null );

	onSelectFeatureType( null, null );

	onSelectProjector( null, null );

	g_taglib.updateButtonText( g_ctrlAddUpdateFeatureButton, 'Add', g_taglib.Icons.CREATE );
	g_ctrlDeleteFeatureButton.prop( 'disabled', true );

	g_ctrlFeatureTypeDropdown.prop( 'disabled', false );

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
	// TODO check if we should add the existing entry first

	const changes = g_util.determineAddUpdateDelete( g_workingFeatures, g_originalFeatures );

	g_saveCountdownQueueRemaining = 0;

	// deletes
	for( let i = 0; i < changes.deletes.length; ++i )
	{
		g_api.deleteFeature( g_projectId, changes.deletes[ i ], saveCallback.bind( this ) );
		++g_saveCountdownQueueRemaining;
	}

	// adds
	for( let i = 0; i < changes.adds.length; ++i )
	{
		g_api.newFeature( g_projectId, /** @type{Feature} */( changes.adds[ i ] ), saveCallback.bind( this ) );
		++g_saveCountdownQueueRemaining;
	}

	// updates
	for( let i = 0; i < changes.updates.length; ++i )
	{
		g_api.updateFeature( g_projectId, /** @type{Feature} */( changes.updates[ i ] ), saveCallback.bind( this ) );
		++g_saveCountdownQueueRemaining;
	}

	if( g_saveCountdownQueueRemaining === 0 )
	{
		g_nav.redirectToProjectDetails( g_projectId );
	}

	g_ctrlCancelButton.prop( 'disabled', true );
	g_ctrlSaveButton.prop( 'disabled', true );
	g_ctrlAddUpdateFeatureButton.prop( 'disabled', true );
	g_ctrlDeleteFeatureButton.prop( 'disabled', true );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API completes one of the save calls (add/update/delete) and lets us track if all of the calls are complete
 *
 * @private
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

/**
 * Populates the left side bar with the working features list
 *
 * @private
 */
function populateSideBar()
{
	g_ctrlWorkingFeatures.empty();

	// add the header
	$( '<li/>' )
		.addClass( 'list-group-item' )
		.addClass( 'list-group-item-action' )
		.addClass( 'font-weight-bold' )
		.addClass( 'bg-secondary' )
		.addClass( 'text-light' )
		.addClass( 'p-2' )
		.text( 'Features' )
		.appendTo( g_ctrlWorkingFeatures );

	if( g_workingFeatures === null )
	{
		$( '<li/>' )
			.addClass( 'list-group-item' )
			.addClass( 'list-group-item-action' )
			.addClass( 'p-2' )
			.addClass( 'pl-3' )
			.text( 'Loading, please wait...' )
			.appendTo( g_ctrlWorkingFeatures );
	}
	else
	{
		for( let i = 0; i < g_workingFeatures.length; ++i )
		{
			let featureType = getFeatureType( g_workingFeatures[ i ].featureType );

			$( '<li/>' )
				.addClass( 'list-group-item' )
				.addClass( 'list-group-item-action' )
				.addClass( 'p-2' )
				.addClass( 'pl-3' )
				.addClass( 'clickable' )
				.text( g_workingFeatures[ i ].name + ' - ' + ( featureType === null ? 'UNKNOWN!' : featureType.name ) )
				.on( 'click', editExistingFeature.bind( this, i ) )
				.appendTo( g_ctrlWorkingFeatures );
		}

		if( g_workingFeatures.length === 0 )
		{
			$( '<li/>' )
				.addClass( 'list-group-item' )
				.addClass( 'list-group-item-action' )
				.addClass( 'p-2' )
				.addClass( 'pl-3' )
				.text( '(None)' )
				.appendTo( g_ctrlWorkingFeatures );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user clicks to edit an existing feature
 *
 * @private
 * @param {number} index - the working array index
 * @param {jQuery.Event} e - the jQuery event
 */
function editExistingFeature( index, e )
{
	e.stopPropagation();

	// mark that we are editing this index
	g_editIndex = index;

	// redraw the list (to reset any previous highlights) and highlight this one
	populateSideBar();
	g_ctrlWorkingFeatures.find( 'li:nth-child(' + ( index + 2 ).toString() + ')' ).addClass( 'bg-medium' );

	const feature = g_workingFeatures[ index ];

	// update all the basic form fields
	g_ctrlFeatureNameInput.val( feature.name );
	g_ctrlFeatureDescriptionTextArea.val( feature.description );

	// update the feature type dropdown
	g_taglib.setDropdownSelectedItem( g_ctrlFeatureTypeDropdown, feature.featureType );
	onSelectFeatureType( getFeatureType( feature.featureType ), feature.config );

	// update the project dropdown
	g_taglib.setDropdownSelectedItem( g_ctrlProjectorDropdown, feature.projectorId );
	if( feature.projectorId !== null )
	{
		onSelectProjector( getProjector( feature.projectorId ), feature.projectorConfig );
	}

	// update the button text
	g_taglib.updateButtonText( g_ctrlAddUpdateFeatureButton, 'Update', g_taglib.Icons.UPDATE );

	// enable the delete button
	g_ctrlDeleteFeatureButton.prop( 'disabled', false );

	// if the id is set, it is already in the database, so don't let the feature type be changed
	g_ctrlFeatureTypeDropdown.prop( 'disabled', feature.id !== null );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Deselects an existing edit
 *
 * @private
 */
function runDeselectEdit()
{
	// mark that we aren't editing
	g_editIndex = -1;

	// redraw the list (to reset any previous highlights)
	populateSideBar();

	// re-initialize the form fields
	initFormFields();

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user selects a feature type
 *
 * @private
 * @param {?FeatureTypePlugin} featureType
 * @param {Object} existingConfig
 */
function onSelectFeatureType( featureType, existingConfig )
{
	// TODO this is getting called multiple times

	g_pluginTagLib.generateConfigUserInterface(
		g_ctrlFeatureConfigHolder,
		featureType,
		existingConfig,
		g_projectId,
		null,
		'(Please select the feature type)' );

	// update the sidebar help
	g_ctrlSideBarFeatureTypeHelpHolder.empty();
	if( featureType != null )
	{
		featureType.src.apiGenerateSideBarHelpItems( g_ctrlSideBarFeatureTypeHelpHolder );
	}

	filterProjectors( featureType );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user selects a projector
 *
 * @private
 * @param {?ProjectorPlugin} projector
 * @param {Object} existingConfig
 */
function onSelectProjector( projector, existingConfig )
{
	g_pluginTagLib.generateConfigUserInterface(
		g_ctrlProjectorConfigHolder,
		projector,
		existingConfig,
		g_projectId,
		null,
		'(No configuration needed.)' );

	g_ctrlSideBarProjectorHelpHolder.empty();
	if( projector != null )
	{
		projector.src.apiGenerateSideBarHelpItems( g_ctrlSideBarProjectorHelpHolder );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {?FeatureTypePlugin} featureType
 */
function filterProjectors( featureType )
{
	g_ctrlProjectorDropdown.empty();

	if( featureType === null )
	{
		g_taglib.generateDropdownOption(
			g_ctrlProjectorDropdown,
			'(Select feature type first)',
			null,
			false,
			false,
			null,
			onSelectProjector.bind( this, null, null ) );
	}
	else
	{
		// add the top entry
		g_taglib.generateDropdownOption(
			g_ctrlProjectorDropdown,
			'No projector',
			null,
			false,
			false,
			null,
			onSelectProjector.bind( this, null, null ) );

		// add all the other entries
		let applicableProjectorsFound = false;
		for( let i = 0; i < g_projectors.length; ++i )
		{
			if( g_util.existsInArray( g_projectors[ i ].applicableFor, featureType.id ) )
			{
				applicableProjectorsFound = true;
				g_taglib.generateDropdownOption(
					g_ctrlProjectorDropdown,
					g_projectors[ i ].name,
					g_projectors[ i ].id,
					false,
					false,
					null,
					onSelectProjector.bind( this, g_projectors[ i ], null ) );
			}
		}

		if( !applicableProjectorsFound )
		{
			g_ctrlProjectorConfigHolder.text( '(No projectors available for ' + featureType.name + ' feature type.)' );
		}
		else
		{
			g_pluginTagLib.generateConfigUserInterface(
				g_ctrlProjectorConfigHolder,
				null,
				null,
				g_projectId,
				null,
				'(No configuration needed.)' );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Finds the feature type with the given id
 *
 * @private
 *
 * @param {string} id
 *
 * @returns {?FeatureTypePlugin}
 */
function getFeatureType( id )
{
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
 * Finds the projector with the given id
 *
 * @private
 *
 * @param {string} id
 *
 * @returns {?ProjectorPlugin}
 */
function getProjector( id )
{
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
