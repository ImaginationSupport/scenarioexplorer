/**
 * @file Feature Precondition plugin
 */
const plugin = {

	/**
	 * @typedef {{featureId: ?string, relation: ?string, value: string}} PluginConfig
	 */

	/**
	 * Format for the feature value relation
	 *
	 * @typedef {{id: string, label: string}} FeatureValueRelation
	 */

	/**
	 * @private
	 * @type {Array< FeatureValueRelation >}
	 */
	FEATURE_RELATIONSHIPS : [
		{ id : '=', label : 'Equal To' },
		{ id : '!=', label : 'Not Equal To' },
		{ id : '<', label : 'Less Than' },
		{ id : '<=te', label : 'Less Than or Equal To' },
		{ id : '>=', label : 'Greater Than or Equal To' },
		{ id : '>', label : 'Greater Than' }
	],

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
		this.mUniqueId = uniqueId;
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

		const feature = this._findFeature( config.featureId );
		if( feature === null )
		{
			return 'Unknown Feature: ' + config.featureId;
		}
		else
		{
			return 'When feature '
				+ feature.name
				+ ' is '
				+ this._findRelation( config.relation ).label
				+ ' "'
				+ config.value + '"';
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
		return 'Feature Precondition';
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
			'When Feature',
			'The feature to test',
			featuresDropdown,
			null,
			parent );

		// relation row
		const relationDropdown = this.mPluginHelpers.generateDropdown( sandboxId + '-relation', null );
		for( let i = 0; i < this.FEATURE_RELATIONSHIPS.length; ++i )
		{
			this.mPluginHelpers.generateDropdownEntry(
				relationDropdown,
				this.FEATURE_RELATIONSHIPS[ i ].label,
				this.FEATURE_RELATIONSHIPS[ i ].id,
				this.FEATURE_RELATIONSHIPS[ i ].id === config.relation,
				false,
				null,
				null );
		}

		this.mPluginHelpers.generateFormRow(
			sandboxId + '-relation-row',
			3,
			'Is',
			'The method to use to compare the feature value',
			relationDropdown,
			null,
			parent );

		// create the value sandbox
		const valueSandbox = $( '<div/>' ).attr( 'id', sandboxId + '-value-sandbox' );

		// value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-value-row',
			3,
			'To',
			'The value to compare the feature value to',
			valueSandbox,
			null,
			parent );

		// populate the features dropdown
		this._populateFeaturesDropdown( sandboxId, config );

		// populate the value sandbox
		this._populateFeatureSandbox(
			sandboxId,
			this._findFeature( config.featureId ),
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

		const relationId = this.mPluginHelpers.getDropdownSelectedValue( sandboxId + '-relation' );

		const valueSandbox = $( '#' + sandboxId + '-value-sandbox' );
		const feature = this._findFeature( featureId );
		const featureTypePlugin = this._findFeatureTypePlugin( feature.featureType );
		const value = this.mPluginHelpers.getEntryUserInterfacePluginSandboxData( valueSandbox, featureTypePlugin, feature.config );

		if( relationId === null || value == null )
		{
			return null;
		}
		else
		{
			return {
				'featureId' : featureId,
				'relation' : relationId,
				'value' : value
			};
		}
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
		this.mPluginHelpers.generateHelpSideBarEntry( 'Feature Precondition: When Feature', 'The feature to use to compare', helpSideBar );
		this.mPluginHelpers.generateHelpSideBarEntry( 'Feature Precondition: Is', 'The method to use to evaluate the feature value', helpSideBar );
		this.mPluginHelpers.generateHelpSideBarEntry( 'Feature Precondition: To', 'The value to use to compare to the feature value', helpSideBar );

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
		return /** @type {PluginConfig} */( raw && raw.featureId && raw.relation && raw.value !== null && raw.value !== undefined
			? raw
			: { 'featureId' : null, 'relation' : '', 'value' : '' } );
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
	 * @private
	 * @param {string} sandboxId - the sandbox id
	 * @param {PluginConfig} config
	 */
	_populateFeaturesDropdown : function( sandboxId, config )
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
				this.mFeatures[ i ].id === config.featureId,
				false,
				null,
				this._onSelectFeature.bind( this, sandboxId, this.mFeatures[ i ], config.value )
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
	},

	/**
	 * Finds the given relation
	 *
	 * @private
	 * @param {string} relationId - the relation id
	 * @returns {?FeatureValueRelation}
	 */
	_findRelation : function( relationId )
	{
		if( this.FEATURE_RELATIONSHIPS === null )
		{
			return null;
		}

		for( let i = 0; i < this.FEATURE_RELATIONSHIPS.length; ++i )
		{
			if( this.FEATURE_RELATIONSHIPS[ i ].id === relationId )
			{
				return this.FEATURE_RELATIONSHIPS[ i ];
			}
		}

		return null;
	}
};