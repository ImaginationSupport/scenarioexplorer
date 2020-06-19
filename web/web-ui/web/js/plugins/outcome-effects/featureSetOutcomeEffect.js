/**
 * @file Feature-Set Outcome Effect plugin
 */
const plugin = {

	/**
	 * @typedef {{feature: ?string, value: string}} PluginConfig
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
	 * @private
	 * @type {?function(string)}
	 */
	mPluginReadyCallback : null,

	/**
	 * Holds the features from the API
	 *
	 * @private
	 * @type {Array< Feature >}
	 */
	mFeatures : [],

	/**
	 * Holds the feature type plugins from the API
	 *
	 * @private
	 * @type {Array< FeatureTypePlugin >}
	 */
	mFeatureTypes : [],

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
		this.mProjectId = projectId;
		this.mViewId = viewId;
		this.mPluginReadyCallback = pluginReadyCallback;

		api.listFeatures( projectId, this._listFeaturesCallback.bind( this ) );

		this.mPluginHelpers.loadFeatureTypePlugins( this.mFeatureTypes, this._listFeatureTypesCallback.bind( this ), null );

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

		const feature = this._findFeature( config.feature );
		if( feature === null )
		{
			return 'Unknown Feature: ' + config.feature;
		}
		else
		{
			return 'Set Feature ' + feature.name + ' to "' + config.value + '"';
		}
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
		return 'Set Feature';
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

		// features dropdown
		const featuresDropdown = this.mPluginHelpers.generateDropdown( sandboxId + '-feature', null );
		this.mPluginHelpers.generateDropdownEntry( featuresDropdown, 'Loading, please wait...', null, false, false, null, null );

		// feature row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-feature-row',
			3,
			'Set',
			'The feature to update',
			featuresDropdown,
			null,
			parent );

		// create the value sandbox
		const valueSandbox = $( '<div/>' ).attr( 'id', sandboxId + '-value-sandbox' );

		// value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-value-row',
			3,
			'To',
			'The new value to set the feature to',
			valueSandbox,
			null,
			parent );

		// populate the available features in the dropdown
		this._populateDropdown( sandboxId, config );

		// populate the value sandbox
		this._populateFeatureSandbox(
			sandboxId,
			this._findFeature( config.feature ),
			config.value );

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
		const featureId = this.mPluginHelpers.getDropdownSelectedValue( sandboxId + '-feature' );
		if( featureId === null )
		{
			return null;
		}

		const valueSandbox = $( '#' + sandboxId + '-value-sandbox' );
		const feature = this._findFeature( featureId );
		const featureTypePlugin = this._findFeatureTypePlugin( feature.featureType );

		const value = this.mPluginHelpers.getEntryUserInterfacePluginSandboxData( valueSandbox, featureTypePlugin, feature.config );
		if( value === null )
		{
			return null;
		}

		return {
			'feature' : featureId,
			'value' : value
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
		return /** @type {PluginConfig} */( raw && raw.feature && raw.value !== null && raw.value !== undefined
			? raw
			: { 'feature' : null, 'value' : '' } );
	},

	/**
	 * @private
	 * @param {Array< Feature >} features - the features from the API
	 */
	_listFeaturesCallback : function( features )
	{
		this.mFeatures = features;

		if( this.mPluginReadyCallback && this.mFeatureTypes !== null )
		{
			this.mPluginReadyCallback( this.mUniqueId );
		}

		return;
	},

	/**
	 * @private
	 * @param {Array< FeatureTypePlugin >} featureTypePlugins - the feature type plugins from the API
	 */
	_listFeatureTypesCallback : function( featureTypePlugins )
	{
		this.mFeatureTypes = featureTypePlugins;

		if( this.mPluginReadyCallback && this.mFeatures !== null )
		{
			this.mPluginReadyCallback( this.mUniqueId );
		}

		return;
	},

	/**
	 * Populates the dropdown
	 *
	 * @private
	 * @param {string} sandboxId - the sandbox id
	 * @param {PluginConfig} featureSetConfig
	 */
	_populateDropdown : function( sandboxId, featureSetConfig )
	{
		const dropdown = $( '#' + sandboxId + '-feature' )
			.empty();

		this.mPluginHelpers.generateDropdownEntry( dropdown, 'Please select...', null, false, false, null, this._onSelectFeature.bind( this, sandboxId, null, null ) );

		for( let i = 0; i < this.mFeatures.length; ++i )
		{
			this.mPluginHelpers.generateDropdownEntry(
				dropdown,
				this.mFeatures[ i ].name,
				this.mFeatures[ i ].id,
				this.mFeatures[ i ].id === featureSetConfig.feature,
				false,
				null,
				this._onSelectFeature.bind( this, sandboxId, this.mFeatures[ i ], featureSetConfig.value )
			);
		}

		return;
	},

	/**
	 * Called when the user selects the feature to set
	 *
	 * @private
	 * @param {string} sandboxId
	 * @param {Feature} feature
	 * @param {string} currentValue
	 * @param {jQuery.Event} e
	 */
	_onSelectFeature : function( sandboxId, feature, currentValue, e )
	{
		e.stopImmediatePropagation(); // TODO why isn't this working?  this function is getting called twice for each selection!

		this._populateFeatureSandbox( sandboxId, feature, currentValue );

		return;
	},

	/**
	 * Generates the plugin sandbox for the given feature into the value holder
	 *
	 * @private
	 * @param {string} sandboxId
	 * @param {Feature} feature
	 * @param {string} currentValue
	 */
	_populateFeatureSandbox : function( sandboxId, feature, currentValue )
	{
		const valueSandbox = $( '#' + sandboxId + '-value-sandbox' ).empty();

		if( feature === null )
		{
			$( '<div/>' )
				.addClass( 'p-2' )
				.addClass( 'border' )
				.addClass( 'rounded' )
				.addClass( 'bg-medium' )
				.addClass( 'text-secondary' )
				.appendTo( valueSandbox )
				.text( 'Please select the feature' );
			return;
		}

		this.mPluginHelpers.generateEntryUserInterfacePluginSandbox(
			valueSandbox,
			this._findFeatureTypePlugin( feature.featureType ),
			feature.config,
			currentValue );

		return;
	},

	/**
	 * Finds the given feature
	 *
	 * @private
	 * @param {string} featureId - the feature id
	 * @returns {?Feature}
	 */
	_findFeature : function( featureId )
	{
		if( this.mFeatures === null )
		{
			return null;
		}

		for( let i = 0; i < this.mFeatures.length; ++i )
		{
			if( this.mFeatures[ i ].id === featureId )
			{
				return this.mFeatures[ i ];
			}
		}

		return null;
	},

	/**
	 * Finds the given feature type
	 *
	 * @private
	 * @param {string} featureTypeId - the feature type id
	 * @returns {?FeatureTypePlugin}
	 */
	_findFeatureTypePlugin : function( featureTypeId )
	{
		if( this.mFeatureTypes === null )
		{
			return null;
		}

		for( let i = 0; i < this.mFeatureTypes.length; ++i )
		{
			if( this.mFeatureTypes[ i ].id === featureTypeId )
			{
				return this.mFeatureTypes[ i ];
			}
		}

		return null;
	}
};