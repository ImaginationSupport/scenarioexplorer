/**
 * @file Compounding Rate Projector plugin
 */
const plugin = {

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
		if( !pluginInstanceConfig )
		{
			console.warn( 'Plugin instance config missing!' );
			return '(invalid plugin)';
		}

		const compoundingRateConfig = /** @type {{multiplier: ?number, timespan: ?number}} */( pluginInstanceConfig );

		return 'Multiplier: '
			+ ( compoundingRateConfig.multiplier === null ? 'not set' : compoundingRateConfig.multiplier.toFixed( 2 ) )
			+ '\nTimespan (days): '
			+ ( compoundingRateConfig.timespan === null ? 'not set' : compoundingRateConfig.timespan.toString() );
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
		const compoundingRateConfig = /** @type {{multiplier: ?number, timespan: ?number}} */(
			pluginInstanceConfig
				? pluginInstanceConfig
				: { multiplier : 1.0, timespan : 365 }
		);

		// default value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-multiplier-row',
			3,
			'Multiplier',
			'The value to multiply the current feature value by',
			this.mPluginHelpers.createDecimalInput( sandboxId + '-multiplier', compoundingRateConfig.multiplier, 2, false, null ),
			null,
			parent );

		// timespan row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-timespan-row',
			3,
			'Time span',
			'The amount of time in days to wait between updates',
			this.mPluginHelpers.createIntegerInput( sandboxId + '-timespan', compoundingRateConfig.timespan, false, null ),
			'(days)',
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
		const multiplier = this.mPluginHelpers.getDecimalInputValue( sandboxId + '-multiplier', false );
		const timespan = this.mPluginHelpers.getIntegerInputValue( sandboxId + '-timespan', false );

		if( multiplier === null )
		{
			this.mPluginHelpers.setErrorHighlight( sandboxId + '-multiplier', true );
			return null;
		}
		else if( timespan === null || timespan < 1 )
		{
			this.mPluginHelpers.setErrorHighlight( sandboxId + '-timespan', true );
			return null;
		}

		return {
			'multiplier' : multiplier,
			'timespan' : timespan
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
		return;
	},

	/**
	 * Generates the user interface to edit
	 *
	 * @param {jQuery} parent - the holder to create all UI elements in
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {Object} pluginInstanceConfig
	 *
	 * @returns {?Object} The object to serialize, or null if there were errors and the value is invalid
	 */
	apiGetEntryUserInterfaceData : function( parent, sandboxId, pluginInstanceConfig )
	{
		return null;
	},

	/**
	 * Generates the help sidebar items
	 *
	 * @param {jQuery} helpSideBar
	 */
	apiGenerateSideBarHelpItems : function( helpSideBar )
	{
		this.mPluginHelpers.generateHelpSideBarEntry( 'Compounding Rate Projector: Multiplier', 'The value to multiple the previous value by', helpSideBar );
		this.mPluginHelpers.generateHelpSideBarEntry( 'Compounding Rate Projector: Time Span', 'The time in days between updates', helpSideBar );

		return;
	},
};
