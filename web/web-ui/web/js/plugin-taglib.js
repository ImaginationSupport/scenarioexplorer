////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function PluginTagLib()
{
	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Fetches the feature type plugins and their source (if requested) from the API
 *
 * @param {Array< FeatureTypePlugin >} array
 * @param {boolean} loadPluginSource - true to load the plugin source code, false to skip
 * @param {function(Array< FeatureTypePlugin >)} listPluginsCallback - callback function when the plugin have been loaded
 * @param {function(id, FeatureTypePlugin)} loadPluginSourceCallback
 * @param {?string} projectId - the project unique id
 * @param {?string} viewId - the view unique id
 * @param {?function(string)} pluginReadyCallback - the callback function to call when the plugin is fully ready
 */
PluginTagLib.prototype.fetchFeatureTypePlugins = function( array, loadPluginSource, listPluginsCallback, loadPluginSourceCallback, projectId, viewId, pluginReadyCallback )
{
	if( array === null || array === undefined )
	{
		g_util.showError( 'Source array cannot be null!' );
	}

	g_api.listFeatureTypes(
		this._listPluginsCallback.bind(
			this,
			array,
			loadPluginSource,
			listPluginsCallback,
			loadPluginSourceCallback,
			projectId,
			viewId,
			pluginReadyCallback
		) );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Fetches the precondition plugins and their source (if requested) from the API
 *
 * @param {Array< PreconditionPlugin >} array
 * @param {boolean} loadPluginSource - true to load the plugin source code, false to skip
 * @param {function(Array< PreconditionPlugin >)} listPluginsCallback - callback function when the plugin have been loaded
 * @param {function(id, PreconditionPlugin)} loadPluginSourceCallback
 * @param {?string} projectId - the project unique id
 * @param {?string} viewId - the view unique id
 * @param {?function(string)} pluginReadyCallback - the callback function to call when the plugin is fully ready
 */
PluginTagLib.prototype.fetchPreconditionPlugins = function( array, loadPluginSource, listPluginsCallback, loadPluginSourceCallback, projectId, viewId, pluginReadyCallback )
{
	if( array === null || array === undefined )
	{
		g_util.showError( 'Source array cannot be null!' );
	}

	g_api.listPreconditions(
		this._listPluginsCallback.bind(
			this,
			array,
			loadPluginSource,
			listPluginsCallback,
			loadPluginSourceCallback,
			projectId,
			viewId,
			pluginReadyCallback
		) );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Fetches the outcome effect plugins and their source (if requested) from the API
 *
 * @param {Array< OutcomeEffectPlugin >} array
 * @param {boolean} loadPluginSource - true to load the plugin source code, false to skip
 * @param {function(Array< OutcomeEffectPlugin >)} listPluginsCallback - callback function when the plugin have been loaded
 * @param {function(id, OutcomeEffectPlugin)} loadPluginSourceCallback
 * @param {?string} projectId - the project unique id
 * @param {?string} viewId - the view unique id
 * @param {?function(string)} pluginReadyCallback - the callback function to call when the plugin is fully ready
 */
PluginTagLib.prototype.fetchOutcomeEffectPlugins = function( array, loadPluginSource, listPluginsCallback, loadPluginSourceCallback, projectId, viewId, pluginReadyCallback )
{
	if( array === null || array === undefined )
	{
		g_util.showError( 'Source array cannot be null!' );
	}

	g_api.listOutcomeEffects(
		this._listPluginsCallback.bind(
			this,
			array,
			loadPluginSource,
			listPluginsCallback,
			loadPluginSourceCallback,
			projectId,
			viewId,
			pluginReadyCallback
		) );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Fetches the projector plugins and their source (if requested) from the API
 *
 * @param {Array< ProjectorPlugin >} array
 * @param {boolean} loadPluginSource - true to load the plugin source code, false to skip
 * @param {function(Array< ProjectorPlugin >)} listPluginsCallback - callback function when the plugin have been loaded
 * @param {function(id, ProjectorPlugin)} loadPluginSourceCallback
 * @param {?string} projectId - the project unique id
 * @param {?string} viewId - the view unique id
 * @param {?function(string)} pluginReadyCallback - the callback function to call when the plugin is fully ready
 */
PluginTagLib.prototype.fetchProjectorPlugins = function( array, loadPluginSource, listPluginsCallback, loadPluginSourceCallback, projectId, viewId, pluginReadyCallback )
{
	if( array === null || array === undefined )
	{
		g_util.showError( 'Source array cannot be null!' );
	}

	g_api.listProjectors(
		this._listPluginsCallback.bind(
			this,
			array,
			loadPluginSource,
			listPluginsCallback,
			loadPluginSourceCallback,
			projectId,
			viewId,
			pluginReadyCallback
		) );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Runs the plugin API call to generate the UI for configuring the plugin
 *
 * @param {jQuery} configHolder
 * @param {ScenarioExplorerPlugin} plugin
 * @param {?Object} existingConfig - the existing config to pre-populate, or null to show the defaults
 * @param {string} projectId - the project id
 * @param {?string} viewId - the view id
 * @param {string} typeNotSelectedMessage - the string to show if no plugin is selected
 */
PluginTagLib.prototype.generateConfigUserInterface = function( configHolder, plugin, existingConfig, projectId, viewId, typeNotSelectedMessage )
{
	configHolder
		.empty()
		.toggleClass( 'disabled', plugin !== null )
		.toggleClass( 'bg-medium', plugin === null )
		.toggleClass( 'text-secondary', plugin === null );

	if( plugin === null )
	{
		configHolder.text( typeNotSelectedMessage );

		return;
	}

	const sandboxId = 'plugin-' + g_util.generateUniqueId().toString();
	configHolder.data( 'sandboxId', sandboxId );

	const sandboxDiv = $( '<div/>' )
		.attr( 'id', sandboxId )
		.appendTo( configHolder );

	plugin.src.apiGenerateConfigUserInterface( sandboxDiv, sandboxId, existingConfig );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Runs the plugin API call the get the config once the user is done -- the followup to .generateConfigUserInterface()
 *
 * @param {jQuery} configHolder
 * @param {ScenarioExplorerPlugin} plugin
 *
 * @returns {?Object}
 */
PluginTagLib.prototype.getConfigUserInterfaceData = function( configHolder, plugin )
{
	const config = plugin.src.apiGetConfigUserInterfaceData( configHolder, configHolder.data( 'sandboxId' ) );

	if( config === null )
	{
		g_util.showAlertDiv( g_ctrlAlert, [ 'Please fix the configuration errors!' ] );
		return null;
	}
	else
	{
		return config;
	}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

PluginTagLib.prototype.generateEntryUserInterface = function( configHolder, plugin, pluginConfig, entryConfig )
{
	configHolder
		.empty()
		.toggleClass( 'disabled', plugin !== null )
		.toggleClass( 'bg-medium', plugin === null )
		.toggleClass( 'text-secondary', plugin === null );

	const sandboxId = 'plugin-' + g_util.generateUniqueId().toString();
	configHolder.data( 'sandboxId', sandboxId );

	const sandboxDiv = $( '<div/>' )
		.attr( 'id', sandboxId )
		.appendTo( configHolder );

	plugin.src.apiGenerateEntryUserInterface( sandboxDiv, sandboxId, pluginConfig, entryConfig );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Runs the plugin API call the get the entry user interface value once the user is done -- the followup to .generateEntryUserInterface()
 *
 * @param {jQuery} configHolder
 * @param {ScenarioExplorerPlugin} plugin
 * @param {Object} pluginConfig
 *
 * @returns {?string}
 */
PluginTagLib.prototype.getEntryUserInterfaceData = function( configHolder, plugin, pluginConfig )
{
	const sandboxId = configHolder.data( 'sandboxId' );

	return plugin.src.apiGetEntryUserInterfaceData( configHolder, sandboxId, pluginConfig );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @param {ScenarioExplorerPlugin} plugin
 * @param {Object} config
 * @param {string} currentValue
 *
 * @returns {?string} the generated summary text
 */
PluginTagLib.prototype.generateCurrentValueSummary = function( plugin, config, currentValue )
{
	return plugin.src.apiGetCurrentValueSummary( config, currentValue );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Generates a summary of the given config
 *
 * @param {ScenarioExplorerPlugin} plugin
 * @param {Object} config
 *
 * @returns {string} the generated summary text
 */
PluginTagLib.prototype.generateConfigSummary = function( plugin, config )
{
	return plugin.src.apiGetConfigTextSummary( config );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Internal helper callback when the API loads the list of plugins
 *
 * @private
 * @param {Array< ScenarioExplorerPlugin >} array - the array to store the values into
 * @param {boolean} loadPluginSource - true to load the plugin source code, false to skip
 * @param {function(Array< ScenarioExplorerPlugin >)} listPluginsCallback - the user callback when the plugins have been loaded
 * @param {function(string, ScenarioExplorerPluginSource)} loadPluginSourceCallback - the user callback when the plugin source has been loaded
 * @param {?string} projectId - the project unique id
 * @param {?string} viewId - the view unique id
 * @param {?function(string)} pluginReadyCallback - the callback function to call when the plugin is fully ready
 * @param {Array< ScenarioExplorerPlugin >} plugins - the array from the API
 */
PluginTagLib.prototype._listPluginsCallback = function( array, loadPluginSource, listPluginsCallback, loadPluginSourceCallback, projectId, viewId, pluginReadyCallback, plugins )
{
	array.length = 0;

	for( let i = 0; i < plugins.length; ++i )
	{
		array.push( plugins[ i ] );

		if( loadPluginSource )
		{
			if( plugins[ i ] instanceof FeatureTypePlugin )
			{
				g_api.getFeatureTypePlugin( plugins[ i ].id, this._loadPluginSourceCallback.bind( this, array, loadPluginSourceCallback, projectId, viewId, pluginReadyCallback ) )
			}
			else if( plugins[ i ] instanceof PreconditionPlugin )
			{
				g_api.getPreconditionPlugin( plugins[ i ].id, this._loadPluginSourceCallback.bind( this, array, loadPluginSourceCallback, projectId, viewId, pluginReadyCallback ) )
			}
			else if( plugins[ i ] instanceof OutcomeEffectPlugin )
			{
				g_api.getOutcomeEffectPlugin( plugins[ i ].id, this._loadPluginSourceCallback.bind( this, array, loadPluginSourceCallback, projectId, viewId, pluginReadyCallback ) )
			}
			else if( plugins[ i ] instanceof ProjectorPlugin )
			{
				g_api.getProjectorPlugin( plugins[ i ].id, this._loadPluginSourceCallback.bind( this, array, loadPluginSourceCallback, projectId, viewId, pluginReadyCallback ) )
			}
			else
			{
				g_util.showError( 'Unknown plugin type: ' + plugins[ i ].id );
				return;
			}
		}
	}

	// call the user callback if it was given
	if( listPluginsCallback )
	{
		listPluginsCallback( plugins );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Internal helper callback when the API loads the plugin source
 *
 * @private
 * @param {Array< ScenarioExplorerPlugin >} array - the array to store the values into
 * @param {function(string, ScenarioExplorerPluginSource)} userCallback - the user callback when the plugin source has been loaded
 * @param {string} id - the plugin id
 * @param {?string} projectId - the project unique id
 * @param {?string} viewId - the view unique id
 * @param {?function(string)} pluginReadyCallback - the callback function to call when the plugin is fully ready
 * @param {ScenarioExplorerPluginSource} plugin
 */
PluginTagLib.prototype._loadPluginSourceCallback = function( array, userCallback, projectId, viewId, pluginReadyCallback, id, plugin )
{
	// update the source array entry
	for( let i = 0; i < array.length; ++i )
	{
		if( array[ i ].id === id )
		{
			// set the plugin source
			array[ i ].src = plugin;

			// run the init
			array[ i ].src.apiInit( g_api, g_pluginHelpers, id, projectId, viewId, pluginReadyCallback );

			// all done
			break;
		}
	}

	// call the user callback if it was given
	if( userCallback )
	{
		userCallback( id, plugin );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
