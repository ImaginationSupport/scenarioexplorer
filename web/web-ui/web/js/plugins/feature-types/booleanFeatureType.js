/**
 * @file Boolean Feature Type plugin
 */
const plugin = {

	/**
	 * @typedef {{defaultValue: string}} PluginConfig
	 */

	/**
	 * Holds the API instance
	 *
	 * @private
	 * @type {?ScenarioExplorerAPI}
	 */
	mAPI : null,

	/**
	 * Holds the PluginHelpers instance
	 *
	 * @private
	 * @type {?PluginHelpers}
	 */
	mPluginHelpers : null,

	/**
	 * Holds the unique id
	 *
	 * @private
	 * @type {?string}
	 */
	mUniqueId : null,

	/**
	 * Holds the project id
	 *
	 * @private
	 * @type {?string}
	 */
	mProjectId : null,

	/**
	 * Holds the view id
	 *
	 * @private
	 * @type {?string}
	 */
	mViewId : null,

	/**
	 * Initializes the plugin
	 *
	 * @param {ScenarioExplorerAPI} api - the api library instance
	 * @param {PluginHelpers} pluginHelpers - the plugin helpers library instance
	 * @param {?string} uniqueId - the plugin id to use to refer to this plugin
	 * @param {?string} projectId - the project unique id
	 * @param {?string} viewId - the view unique id
	 * @param {?function(string)} pluginReadyCallback - the callback function to call when the plugin is fully initialized
	 */
	apiInit : function( api, pluginHelpers, uniqueId, projectId, viewId, pluginReadyCallback )
	{
		this.mAPI = api;
		this.mPluginHelpers = pluginHelpers;
		this.mUniqueId = uniqueId;
		this.mProjectId = projectId;
		this.mViewId = viewId;

		if( pluginReadyCallback )
		{
			pluginReadyCallback( this.mUniqueId );
		}

		return;
	},

	/**
	 * Formats the given plugin config into a nice human-readable summary string
	 *
	 * @param {Object} pluginInstanceConfig - the plugin configuration
	 *
	 * @returns {string} the config summary string
	 */
	apiGetConfigTextSummary : function( pluginInstanceConfig )
	{
		const config = this._parseConfig( pluginInstanceConfig );

		const defaultValueParsed = config.defaultValue.toLowerCase() === 'true';

		return 'Initial value: ' + defaultValueParsed;
	},

	/**
	 * Formats the given current value with the config to a nice human-readable string
	 *
	 * @param {Object} pluginInstanceConfig - the plugin configuration
	 * @param {string} currentValue - the current value
	 *
	 * @returns {string} the string to display to the user
	 */
	apiGetCurrentValueSummary : function( pluginInstanceConfig, currentValue )
	{
		return currentValue.toString();
	},

	/**
	 * Generates the user interface to configure the plugin
	 *
	 * @param {jQuery} parent - the holder to create all UI elements in
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {Object} pluginInstanceConfig
	 */
	apiGenerateConfigUserInterface : function( parent, sandboxId, pluginInstanceConfig )
	{
		const config = this._parseConfig( pluginInstanceConfig );

		const defaultValueParsed = config.defaultValue.toLowerCase() === 'true';

		// default value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-default-value-row',
			3,
			'Initial value',
			'The initial value',
			this.mPluginHelpers.generateBooleanField( sandboxId + '-default-value', defaultValueParsed, 'True', 'False', parent ),
			null,
			parent );

		return;
	},

	/**
	 * Gets the config data to serialize to the database using the user interface choices the user made
	 *
	 * @param {jQuery} parent - the holder to create all UI elements in
	 * @param {string} sandboxId - the sandbox unique id
	 *
	 * @returns {?Object} The object to serialize, or null if there were errors and the config is invalid
	 */
	apiGetConfigUserInterfaceData : function( parent, sandboxId )
	{
		return {
			defaultValue : this.mPluginHelpers.getBooleanFieldValue( sandboxId + '-default-value' ) ? 'true' : 'false'
		};
	},

	/**
	 * Generates the user interface to edit
	 *
	 * @param {jQuery} parent - the holder to create all UI elements in
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {Object} pluginInstanceConfig
	 * @param {string} currentValue
	 */
	apiGenerateEntryUserInterface : function( parent, sandboxId, pluginInstanceConfig, currentValue )
	{
		const parsedCurrentValue = currentValue !== null && currentValue.toLowerCase() === 'true';

		this.mPluginHelpers.generateBooleanField( sandboxId + '-value', parsedCurrentValue, 'True', 'False', parent );

		return;
	},

	/**
	 * Generates the user interface to edit
	 *
	 * @param {jQuery} parent - the holder to create all UI elements in
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {Object} pluginInstanceConfig
	 *
	 * @returns {?string} The object to serialize, or null if there were errors and the value is invalid
	 */
	apiGetEntryUserInterfaceData : function( parent, sandboxId, pluginInstanceConfig )
	{
		return this.mPluginHelpers.getBooleanFieldValue( sandboxId + '-value' ) ? 'true' : 'false';
	},

	/**
	 * Generates the help sidebar items
	 *
	 * @param {jQuery} helpSideBar
	 */
	apiGenerateSideBarHelpItems : function( helpSideBar )
	{
		this.mPluginHelpers.generateHelpSideBarEntry( 'Boolean Feature: Initial value', 'Assign true or false to the feature for the starting point (now state) of the project.', helpSideBar );

		return;
	},

	// ========================= internal functions =========================

	/**
	 * Parses the serialized config
	 *
	 * @private
	 *
	 * @param {Object} raw - the raw config to parse
	 *
	 * @returns {PluginConfig} the parsed config
	 */
	_parseConfig : function( raw )
	{
		return /** @type {PluginConfig} */( raw
			? raw
			: { 'defaultValue' : 'false' } );
	}
};