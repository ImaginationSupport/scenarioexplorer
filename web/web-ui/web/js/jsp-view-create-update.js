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
 * @private
 *
 * @const {Util}
 */
const g_util = new Util();

/**
 * Holds the Nav Helper instance
 *
 * @private
 *
 * @const {NavHelper}
 */
const g_nav = new NavHelper();

/**
 * Holds the TagLib library instance
 *
 * @private
 *
 * @const {TagLib}
 */
const g_taglib = new TagLib();

/**
 * Holds the existing project id, or null to create a new project
 *
 * @private
 *
 * @type {?string}
 */
let g_projectId = null;

/**
 * Holds the existing view id, or null to create a new view
 *
 * @private
 *
 * @type {?string}
 */
let g_viewId = null;

/**
 * Holds the existing view
 *
 * @private
 *
 * @type {?View}
 */
let g_existingView = null;

/**
 * Holds the jQuery control for the view name
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlViewName = null;

/**
 * Holds the jQuery control for the view description
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlViewDescription = null;

/**
 * Holds the jQuery control for the view type
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlViewType = null;

/**
 * Holds the jQuery control for the smart query view config
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlViewConfigSmartQuery = null;

let g_ctrlViewConfigSmartQueryFeatureDropdown = null;

let g_ctrlViewConfigHelpSmartQuery = null;

/**
 * Holds the jQuery control for the alert box
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlAlert = null;

/**
 * Holds the jQuery control for the delete button
 *
 * @private
 *
 * @type {?jQuery}
 */
let g_ctrlDelete = null;

/**
 * Holds the features
 *
 * @type {?Array< Feature >}
 */
let g_features = null;

/**
 * Holds the default view type
 *
 * @private
 *
 * @const {ViewType}
 */
const DEFAULT_VIEW_TYPE = ViewType.FUTURES_BUILDING;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called by the JSP to initialize the page
 *
 * @param {string} username
 */
function initJSP( username )
{
	g_projectId = g_nav.getProjectIdUriParameter();

	g_viewId = g_nav.getViewIdUriParameter();
	if( g_viewId )
	{
		$( '#page-header' ).text( 'Update View' );
		g_api.getView( g_projectId, g_viewId, getViewCallback.bind( this ) );
	}

	g_ctrlViewName = $( '#view-name' );
	g_ctrlViewDescription = $( '#view-description' );
	g_ctrlViewType = $( '#view-type' )
		.data( 'previous-view-type', ViewType.UNKNOWN );
	g_ctrlViewConfigSmartQuery = $( '#view-config-smart-query' )
		.removeClass( 'd-none' )
		.hide();
	g_ctrlViewConfigHelpSmartQuery = $( '#view-config-help-smart-query' )
		.removeClass( 'd-none' )
		.hide();

	g_ctrlViewConfigSmartQueryFeatureDropdown = $( '#smart-query-feature' );

	g_ctrlAlert = $( '#form-error-alert' ).removeClass( 'd-none' ).hide();
	$( '#button-save' ).on( 'click', runSave.bind( this ) );
	$( '#button-cancel' ).on( 'click', g_nav.redirectToProjectDetails.bind( g_nav, g_projectId ) );
	g_ctrlDelete = $( '#button-delete' ).on( 'click', g_nav.redirectToDeleteView.bind( g_nav, g_projectId, g_viewId ) );

	// if we are creating a new view, hide the delete button
	if( !g_viewId )
	{
		g_ctrlDelete.hide();
	}

	g_api.getProject( g_projectId, getProjectCallback.bind( this ) );
	g_api.listFeatures( g_projectId, listFeaturesCallback.bind( this ) );
	g_api.listFeatureTypes( listFeatureTypesCallback.bind( this ) );

	// populate the view type dropdown
	g_taglib.generateDropdownOption(
		g_ctrlViewType,
		'Futures Building',
		ViewType.FUTURES_BUILDING,
		DEFAULT_VIEW_TYPE === ViewType.FUTURES_BUILDING,
		false,
		null,
		viewTypeSelected.bind( this, ViewType.FUTURES_BUILDING ) );
	g_taglib.generateDropdownOption(
		g_ctrlViewType,
		'Smart Query',
		ViewType.SMART_QUERY,
		DEFAULT_VIEW_TYPE === ViewType.SMART_QUERY,
		false,
		null,
		viewTypeSelected.bind( this, ViewType.SMART_QUERY ) );
	g_taglib.generateDropdownOption(
		g_ctrlViewType,
		'What-If? (coming soon!)',
		ViewType.WHAT_IF,
		DEFAULT_VIEW_TYPE === ViewType.WHAT_IF,
		true,
		null,
		viewTypeSelected.bind( this, ViewType.WHAT_IF ) );
	g_taglib.generateDropdownOption(
		g_ctrlViewType,
		'Extreme State (coming soon!)',
		ViewType.EXTREME_STATE,
		DEFAULT_VIEW_TYPE === ViewType.EXTREME_STATE,
		true,
		null,
		viewTypeSelected.bind( this, ViewType.EXTREME_STATE ) );
	g_taglib.attachDropdownEventHandlers( g_ctrlViewType );
	viewTypeSelected( DEFAULT_VIEW_TYPE );

	g_taglib.bindFormSubmitEnterEscape( 'view-form', runSave.bind( this ), g_nav.redirectToProjectDetails.bind( g_nav, g_projectId ) );

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
 * Called when the API gets the existing view
 *
 * @private
 *
 * @param {View} view - the view
 */
function getViewCallback( view )
{
	g_existingView = view;

	g_ctrlViewName.val( view.name );
	g_ctrlViewDescription.val( view.description );
	g_ctrlViewType.val( view.type )
		.prop( 'disabled', true );

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

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the user selects the view type
 *
 * @private
 *
 * @param {ViewType} viewType
 */
function viewTypeSelected( viewType )
{
	const previousViewType = g_ctrlViewType.data( 'previous-view-type' );

	if( previousViewType === viewType )
	{
		return;
	}

	g_ctrlViewConfigSmartQuery.hide();
	g_ctrlViewConfigHelpSmartQuery.hide();

	switch( viewType )
	{
		case ViewType.FUTURES_BUILDING:
			// no config needed for this view
			break;

		case ViewType.SMART_QUERY:
			populateConfigSmartQuery();
			g_ctrlViewConfigSmartQuery.show();
			g_ctrlViewConfigHelpSmartQuery.show();
			break;

		case ViewType.WHAT_IF:
			break;

		case ViewType.EXTREME_STATE:
			break;

		case ViewType.UNKNOWN:
		default:
			g_util.showError( 'Unknown view type: ' + viewType );
	}

	g_ctrlViewType.data( 'previous-view-type', viewType );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
function populateConfigSmartQuery()
{
	g_ctrlViewConfigSmartQueryFeatureDropdown.empty();

	if( g_features === null )
	{
		g_taglib.generateDropdownOption( g_ctrlViewConfigSmartQueryFeatureDropdown, 'Loading features, please wait...', null, false, true, null, null );
		return;
	}

	g_taglib.generateDropdownOption(
		g_ctrlViewConfigSmartQueryFeatureDropdown,
		'Select the feature to use',
		null,
		false,
		false,
		null,
		smartQueryFeatureTypeSelected.bind( this, null ) );

	for( let i = 0; i < g_features.length; ++i )
	{
		g_taglib.generateDropdownOption(
			g_ctrlViewConfigSmartQueryFeatureDropdown,
			g_features[ i ].name,
			g_features[ i ].id,
			false,
			false,
			null,
			smartQueryFeatureTypeSelected.bind( this, g_features[ i ] )
		);
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 *
 * @param {Feature} feature
 */
function smartQueryFeatureTypeSelected( feature )
{
	let smartQueryMode = '';

	if( feature != null )
	{
		const featureType = getFeatureType( feature.featureType );
		if( featureType === null )
		{
			g_util.showError( 'Unknown feature: ' + feature.id );
		}

		smartQueryMode = featureType.isContinuous ? 'Type: Continuous' : 'Type: Enumerated';
	}

	$( '#smart-query-feature-mode' ).text( smartQueryMode );

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
	const name = g_ctrlViewName.val().toString().trim();
	const description = g_ctrlViewDescription.val().toString().trim();
	const type = g_ctrlViewType.val().toString().trim();

	const errors = [];

	if( name.length === 0 )
	{
		errors.push( 'View name cannot be empty!' );
	}

	let config = null;
	switch( type )
	{
		case ViewType.FUTURES_BUILDING:
			break;

		case ViewType.SMART_QUERY:
			config = {
				'feature' : g_taglib.getDropdownSelectedValue( g_ctrlViewConfigSmartQueryFeatureDropdown )
			};

			if( config.feature === null )
			{
				errors.push( 'Please select the smart query feature.' );
			}
			break;

		case ViewType.WHAT_IF:
			errors.push( 'What-If view is not available yet.' );
			break;

		case ViewType.EXTREME_STATE:
			errors.push( 'Extreme state view is not available yet.' );
			break;

		case ViewType.UNKNOWN:
		default:
			errors.push( 'Please select the view type.' );
			break;
	}

	if( errors.length > 0 )
	{
		g_ctrlAlert.empty();

		$( '<span/>' )
			.text( 'Please fix the following error' + ( errors.length === 1 ? '' : 's' ) )
			.appendTo( g_ctrlAlert );

		let ul = $( '<ul/>' )
			.appendTo( g_ctrlAlert );

		for( let i = 0; i < errors.length; ++i )
		{
			ul = $( '<li/>' )
				.text( errors[ i ] )
				.appendTo( ul );
		}

		g_ctrlAlert.show();
		return;
	}

	const apiDataHelper = new ApiDataHelper();
	if( !g_viewId )
	{
		// create view
		g_api.newView( g_projectId, apiDataHelper.createViewInstance( name, description, type, g_projectId, config ), newViewCallback.bind( this ) );
	}
	else
	{
		// update view
		g_existingView.name = name;
		g_existingView.description = description;
		g_api.updateView( g_projectId, g_existingView, updateViewCallback.bind( this ) );
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API returns from creating the new view
 *
 * @private
 *
 * @param {View} view
 */
function newViewCallback( view )
{
	g_nav.redirectToView( g_projectId, view.id );

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Called when the API returns from updating the view
 *
 * @private
 */
function updateViewCallback()
{
	g_nav.redirectToView( g_projectId, g_viewId );

	return;
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
	if( !id )
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
