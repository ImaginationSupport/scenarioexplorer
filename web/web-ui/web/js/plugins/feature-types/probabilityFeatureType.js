/**
 * @file Probability Feature Type plugin
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
		const config = this._parseConfig( pluginInstanceConfig );

		return 'Initial value: '
			+ config.defaultValue.toFixed( config.numDecimalPlaces )
			+ '\nDecimal places: '
			+ ( config.numDecimalPlaces.toString() );
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
		const config = this._parseConfig( pluginInstanceConfig );
		const parsedCurrentValue = parseFloat( currentValue );

		return parsedCurrentValue.toFixed( config.numDecimalPlaces );
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

		const defaultValueParsed = parseFloat( config.defaultValue );

		// default value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-default-value-row',
			4,
			'Initial value',
			'The initial value',
			this.mPluginHelpers.createDecimalInput( sandboxId + '-default-value', defaultValueParsed, config.numDecimalPlaces, false, null ),
			'(range 0.0 - 1.0)',
			parent );

		// num decimal places row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-decimal-places-row',
			4,
			'Decimal Places',
			'The number of decimal places to show',
			this.mPluginHelpers.createIntegerInput( sandboxId + '-num-decimal-places', config.numDecimalPlaces, false, null ),
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
		const ctrlDefaultValue = $( '#' + sandboxId + '-default-value' );
		const ctrlNumDecimalPlaces = $( '#' + sandboxId + '-num-decimal-places' );

		const defaultValue = this.mPluginHelpers.getDecimalInputValue( ctrlDefaultValue, false );
		const numDecimalPlaces = this.mPluginHelpers.getIntegerInputValue( ctrlNumDecimalPlaces, false );

		// error if any of the values don't parse or are invalid
		if( defaultValue === null || defaultValue < 0.0 || defaultValue > 1.0 )
		{
			g_taglib.setErrorHighlight( ctrlDefaultValue, true );
			return null;
		}
		else if( numDecimalPlaces === null )
		{
			g_taglib.setErrorHighlight( ctrlNumDecimalPlaces, true );
			return null;
		}

		g_taglib.setErrorHighlight( ctrlDefaultValue, false );
		g_taglib.setErrorHighlight( ctrlNumDecimalPlaces, false );

		return {
			'defaultValue' : defaultValue.toFixed( numDecimalPlaces ),
			'numDecimalPlaces' : numDecimalPlaces
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
		const config = this._parseConfig( pluginInstanceConfig );

		const currentValueParsed = parseFloat( currentValue === null || currentValue.length === 0 ? config.defaultValue : currentValue );

		this.mPluginHelpers.createDecimalInput( sandboxId + '-value', currentValueParsed, config.numDecimalPlaces, false, parent )
			.addClass( 'w-100' );

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
		const config = this._parseConfig( pluginInstanceConfig );
		const value = this.mPluginHelpers.getDecimalInputValue( sandboxId + '-value', false );
		if( value === null )
		{
			this.mPluginHelpers.setErrorHighlight( sandboxId + '-value', true );
			return null;
		}
		else
		{
			if( value < 0.0 || value > 1.0 )
			{
				this.mPluginHelpers.setErrorHighlight( sandboxId + '-value', true );
				return null;
			}
			else
			{
				this.mPluginHelpers.setErrorHighlight( sandboxId + '-value', false );
				return value.toFixed( config.numDecimalPlaces );
			}
		}
	},

	/**
	 * Generates the help sidebar items
	 *
	 * @param {jQuery} helpSideBar
	 */
	apiGenerateSideBarHelpItems : function( helpSideBar )
	{
		this.mPluginHelpers.generateHelpSideBarEntry( 'Probability Feature: Initial value', 'The value to use for the "now" state', helpSideBar );
		this.mPluginHelpers.generateHelpSideBarEntry( 'Probability Feature: Decimal Places', 'The number of decimal places to show', helpSideBar );

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
	 * @returns {{defaultValue: string, numDecimalPlaces: number}} the parsed config
	 */
	_parseConfig : function( raw )
	{
		return /** @type {{defaultValue: string, numDecimalPlaces: number}} */( raw
			? raw
			: { 'defaultValue' : '1.0', 'numDecimalPlaces' : 2 } );
	}
};