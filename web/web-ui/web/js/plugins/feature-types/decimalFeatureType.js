/**
 * @file Decimal Feature Type plugin
 */
const plugin = {

	/**
	 * @typedef {{defaultValue: string, min: ?number, max: ?number, numDecimalPlaces: number}} PluginConfig
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

		const defaultValueParsed = parseFloat( config.defaultValue );

		if( config.numDecimalPlaces === null || config.numDecimalPlaces === undefined )
		{
			config.numDecimalPlaces = 2;
		}

		return 'Initial value: '
			+ defaultValueParsed.toFixed( config.numDecimalPlaces )
			+ '\nMin: '
			+ ( config.min === null || config.min === undefined ? 'not set' : config.min.toFixed( config.numDecimalPlaces ) )
			+ '\nMax: '
			+ ( config.max === null || config.max === undefined ? 'not set' : config.max.toFixed( config.numDecimalPlaces ) )
			+ '\nDecimal places: '
			+ ( config.numDecimalPlaces === null || config.numDecimalPlaces === undefined ? 'not set' : config.numDecimalPlaces.toString() );
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

		const parsedDefaultValue = parseFloat( config.defaultValue );

		// default value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-default-value-row',
			4,
			'Initial value',
			'Assign the value to the root node of the project',
			this.mPluginHelpers.createDecimalInput( sandboxId + '-default-value', parsedDefaultValue, config.numDecimalPlaces, false, null ),
			null,
			parent );

		// min value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-min-row',
			4,
			'Min',
			'The minimum allowed value (optional)',
			this.mPluginHelpers.createDecimalInput( sandboxId + '-min', config.min, config.numDecimalPlaces, true, null ),
			null,
			parent );

		// max value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-max-row',
			4,
			'Max',
			'The maximum allowed value (optional)',
			this.mPluginHelpers.createDecimalInput( sandboxId + '-max', config.max, config.numDecimalPlaces, true, null ),
			null,
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
		const ctrlMin = $( '#' + sandboxId + '-min' );
		const ctrlMax = $( '#' + sandboxId + '-max' );
		const ctrlNumDecimalPlaces = $( '#' + sandboxId + '-num-decimal-places' );

		const defaultValue = this.mPluginHelpers.getDecimalInputValue( ctrlDefaultValue, false );
		const min = this.mPluginHelpers.getDecimalInputValue( ctrlMin, true );
		const max = this.mPluginHelpers.getDecimalInputValue( ctrlMax, true );
		const numDecimalPlaces = this.mPluginHelpers.getIntegerInputValue( ctrlNumDecimalPlaces, false );

		// error immediately if any of the values don't parse
		if( defaultValue === null || min === null || max === null || numDecimalPlaces === null )
		{
			return null;
		}

		const config = {
			'defaultValue' : defaultValue.toFixed( numDecimalPlaces ),
			'numDecimalPlaces' : numDecimalPlaces
		};

		// check if the min was set
		if( min !== undefined )
		{
			// if the default is less than the min, set the error highlights and return error
			if( defaultValue < min )
			{
				this.mPluginHelpers.setErrorHighlight( ctrlDefaultValue, true );
				this.mPluginHelpers.setErrorHighlight( ctrlMin, true );
				return null;
			}

			config.min = min;
		}

		// check if the max was set
		if( max !== undefined )
		{
			// if the default is greater than the max, set the error highlights and return error
			if( defaultValue > max )
			{
				this.mPluginHelpers.setErrorHighlight( ctrlDefaultValue, true );
				this.mPluginHelpers.setErrorHighlight( ctrlMax, true );
				return null;
			}

			config.max = max;
		}

		// make sure the min is not greater than the max
		if( min !== undefined && max !== undefined && min > max )
		{
			this.mPluginHelpers.setErrorHighlight( ctrlMin, true );
			this.mPluginHelpers.setErrorHighlight( ctrlMax, true );
			return null;
		}

		this.mPluginHelpers.setErrorHighlight( ctrlDefaultValue, false );
		this.mPluginHelpers.setErrorHighlight( ctrlMin, false );
		this.mPluginHelpers.setErrorHighlight( ctrlMax, false );

		return config;
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
			if( config.min !== null && value < config.min )
			{
				this.mPluginHelpers.setErrorHighlight( sandboxId + '-value', true );
				return null;
			}
			else if( config.max !== null && value > config.max )
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
		this.mPluginHelpers.generateHelpSideBarEntry( 'Decimal Feature: Initial value', 'Assigns the feature value at the starting point (now state) of the project.', helpSideBar );
		this.mPluginHelpers.generateHelpSideBarEntry( 'Decimal Feature: Min', 'If applicable, assign the minimum value allowed for the feature (optional).', helpSideBar );
		this.mPluginHelpers.generateHelpSideBarEntry( 'Decimal Feature: Max', 'If applicable, assign the maximum value allowed for the feature (optional).', helpSideBar );
		this.mPluginHelpers.generateHelpSideBarEntry( 'Decimal Feature: Decimal Places', 'Select the number of decimal places to show.', helpSideBar );

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
			: { 'defaultValue' : '0.0', 'min' : null, 'max' : null, 'numDecimalPlaces' : 2 } );
	}
};