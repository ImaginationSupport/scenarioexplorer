/**
 * @file Multiple Choice (enumeration) Feature Type plugin
 */
const plugin = {

	/**
	 * @typedef {{defaultValue: string, choices: Array< string > }} PluginConfig
	 */

	/**
	 * Holds the API instance
	 *
	 * @private
	 *
	 * @type {?ScenarioExplorerAPI}
	 */
	mAPI : null,

	/**
	 * Holds the PluginHelpers instance
	 *
	 * @private
	 *
	 * @type {?PluginHelpers}
	 */
	mPluginHelpers : null,

	/**
	 * Holds the unique id
	 *
	 * @private
	 *
	 * @type {?string}
	 */
	mUniqueId : null,

	/**
	 * Holds the project id
	 *
	 * @private
	 *
	 * @type {?string}
	 */
	mProjectId : null,

	/**
	 * Holds the view id
	 *
	 * @private
	 *
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

		let summary = 'Initial value: "'
			+ config.defaultValue
			+ '" Choices: ';

		for( let i = 0; i < config.choices.length; ++i )
		{
			if( i > 0 )
			{
				summary += ' / ';
			}

			summary += '"' + config.choices[ i ].label + '"';
		}

		return summary;
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
		return currentValue;
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

		// store the config
		this._setConfig( sandboxId, config );

		const newEntryHolder = $( '<div/>' );
		this.mPluginHelpers.generateButton( null, 'Add', null, 'fa-plus', newEntryHolder )
			.addClass( 'float-right' )
			.addClass( 'display-inline-block' )
			.on( 'click', this._onAdd.bind( this, sandboxId ) );
		this.mPluginHelpers.createTextInput( sandboxId + '-add', '', true, newEntryHolder )
			.addClass( 'w-75' );

		// new value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-new-entries-row',
			3,
			'Add New',
			'New multiple choice value to add',
			newEntryHolder,
			null,
			parent );

		const table = this.mPluginHelpers.generateTable( null, null )
			.addClass( 'border table-striped' );
		this.mPluginHelpers.generateTableBody( sandboxId + '-existing-entries', table );

		// existing value row
		this.mPluginHelpers.generateFormRow(
			sandboxId + '-existing-entries-row',
			3,
			'Existing',
			'The existing multiple choice entries',
			table,
			null,
			parent );

		this._populateExisting( sandboxId );

		$( '<div/>' )
			.attr( 'id', sandboxId + '-alert' )
			.hide()
			.addClass( 'alert alert-danger border border-danger' )
			.appendTo( parent );

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
		const config = this._getConfig( sandboxId );

		if( config.choices.length === 0 )
		{
			this._showAlert( sandboxId, 'Please add at least one choice!' );
			return null;
		}
		else
		{
			return config;
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
		const config = this._parseConfig( pluginInstanceConfig );

		const dropdown = this.mPluginHelpers.generateDropdown( sandboxId + '-dropdown', parent )
			.addClass( 'w-100' );

		for( let i = 0; i < config.choices.length; ++i )
		{
			this.mPluginHelpers.generateDropdownEntry(
				dropdown,
				config.choices[ i ].label,
				config.choices[ i ].value,
				config.choices[ i ].value === currentValue,
				false,
				null,
				null );
		}

		return;
	},

	/**
	 * Generates the user interface to edit
	 *
	 * @param {jQuery} parent - the holder to create all UI elements in
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {Object} pluginInstanceConfig
	 *
	 * @returns {?string} The string to serialize, or null if there were errors and the value is invalid
	 */
	apiGetEntryUserInterfaceData : function( parent, sandboxId, pluginInstanceConfig )
	{
		return this.mPluginHelpers.getDropdownSelectedValue( sandboxId + '-dropdown' );
	},

	/**
	 * Generates the help sidebar items
	 *
	 * @param {jQuery} helpSideBar
	 */
	apiGenerateSideBarHelpItems : function( helpSideBar )
	{
		this.mPluginHelpers.generateHelpSideBarEntry( 'Multiple Choice Feature: Add New', 'The text of the choice to add', helpSideBar );
		this.mPluginHelpers.generateHelpSideBarEntry( 'Multiple Choice Feature: Existing', 'The existing entries', helpSideBar );

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
			: { 'defaultValue' : '', 'choices' : [] } );
	},

	/**
	 * Populates the list of existing entries
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 */
	_populateExisting : function( sandboxId )
	{
		const config = this._getConfig( sandboxId );

		const tbody = $( '#' + sandboxId + '-existing-entries' )
			.empty();

		for( let i = 0; i < config.choices.length; ++i )
		{
			let tr = this.mPluginHelpers.generateTableRow( tbody );

			// value cell
			this.mPluginHelpers.generateTableCell( config.choices[ i ].label + ( config.choices[ i ].value === config.defaultValue ? ' (default)' : '' ), tr );

			// actions cell
			let td = this.mPluginHelpers.generateTableCell( null, tr )
				.addClass( 'text-right' );

			// move up
			let upButton = this.mPluginHelpers.generateButton( null, null, null, 'fa-arrow-up', td )
				.addClass( 'p-1' )
				.on( 'click', this._onMoveUp.bind( this, sandboxId, i ) );
			if( i === 0 )
			{
				upButton.prop( 'disabled', true )
			}

			// move down
			let downButton = this.mPluginHelpers.generateButton( null, null, null, 'fa-arrow-down', td )
				.addClass( 'p-1' )
				.addClass( 'ml-1' )
				.on( 'click', this._onMoveDown.bind( this, sandboxId, i ) );
			if( i === config.choices.length - 1 )
			{
				downButton.prop( 'disabled', true )
			}

			// make default
			let makeDefaultButton = this.mPluginHelpers.generateButton( null, 'Default', null, 'fa-badge-check', td )
				.addClass( 'p-1' )
				.addClass( 'ml-1' )
				.on( 'click', this._onSetDefault.bind( this, sandboxId, i ) );
			if( config.choices[ i ].value === config.defaultValue )
			{
				makeDefaultButton.prop( 'disabled', true )
			}

			// delete
			this.mPluginHelpers.generateButton( null, null, null, 'fa-trash', td )
				.addClass( 'p-1' )
				.addClass( 'ml-1' )
				.on( 'click', this._onDelete.bind( this, sandboxId, i ) );
		}

		if( config.choices.length === 0 )
		{
			let tr = this.mPluginHelpers.generateTableRow( tbody );
			this.mPluginHelpers.generateTableCell( '(None)', tr );
		}

		this._hideAlert( sandboxId );

		return;
	},

	/**
	 * Called when the user clicks the Add button
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 */
	_onAdd : function( sandboxId )
	{
		const config = this._getConfig( sandboxId );
		const ctrlAddName = sandboxId + '-add';
		const raw = this.mPluginHelpers.getTextInputValue( ctrlAddName ).trim();

		// if the value is empty, set error and bail
		if( raw.length === 0 )
		{
			this.mPluginHelpers.setErrorHighlight( ctrlAddName, true );
			this._showAlert( sandboxId, 'New value cannot be blank!' );
			return;
		}

		// see if it's already in the list
		for( let i = 0; i < config.choices.length; ++i )
		{
			if( config.choices[ i ].value === raw )
			{
				this.mPluginHelpers.setErrorHighlight( ctrlAddName, true );
				this._showAlert( sandboxId, 'New value is already in the list!' );
				return;
			}
		}

		// add this new item to the list
		config.choices.push( { 'label' : raw, 'value' : raw } );

		// if the default value isn't set or there is only one choice, set it to this one
		if( config.defaultValue === '' || config.choices.length === 1 )
		{
			config.defaultValue = raw;
		}

		this._setConfig( sandboxId, config );

		this.mPluginHelpers.setErrorHighlight( ctrlAddName, false );

		this._populateExisting( sandboxId );

		this.mPluginHelpers.setTextInputValue( ctrlAddName, '' );

		$( '#' + ctrlAddName ).focus();

		return;
	},

	/**
	 * Called when the user clicks the Move Up button
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {number} index - the choices array index
	 */
	_onMoveUp : function( sandboxId, index )
	{
		const config = this._getConfig( sandboxId );

		if( index > 0 )
		{
			let swap = config.choices[ index - 1 ];
			config.choices[ index - 1 ] = config.choices[ index ];
			config.choices[ index ] = swap;
		}

		this._setConfig( sandboxId, config );

		this._populateExisting( sandboxId );

		return;
	},

	/**
	 * Called when the user clicks the Move Down button
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {number} index - the choices array index
	 */
	_onMoveDown : function( sandboxId, index )
	{
		const config = this._getConfig( sandboxId );

		if( index < config.choices.length - 1 )
		{
			let swap = config.choices[ index + 1 ];
			config.choices[ index + 1 ] = config.choices[ index ];
			config.choices[ index ] = swap;
		}

		this._setConfig( sandboxId, config );

		this._populateExisting( sandboxId );

		return;
	},

	/**
	 * Called when the user clicks the Set Default button
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {number} index - the choices array index
	 */
	_onSetDefault : function( sandboxId, index )
	{
		const config = this._getConfig( sandboxId );

		config.defaultValue = config.choices[ index ].value;

		this._setConfig( sandboxId, config );

		this._populateExisting( sandboxId );

		return;
	},

	/**
	 * Called when the user clicks the Delete button
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {number} index - the choices array index
	 */
	_onDelete : function( sandboxId, index )
	{
		const config = this._getConfig( sandboxId );

		const updateDefault = config.choices[ index ].value === config.defaultValue;

		config.choices.splice( index, 1 );

		if( updateDefault )
		{
			// item being deleted was the default entry, so set it to something else
			if( config.choices.length === 0 )
			{
				// no other choices will be available, so set it to blank
				config.defaultValue = '';
			}
			else
			{
				// just set it to the first entry in the list
				config.defaultValue = config.choices[ 0 ].value;
			}
		}

		this._setConfig( sandboxId, config );

		this._populateExisting( sandboxId );

		return;
	},

	/**
	 * Gets the current config
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 *
	 * @returns {PluginConfig} the current config
	 */
	_getConfig : function( sandboxId )
	{
		return /** @type {PluginConfig} */( $( '#' + sandboxId ).data( 'config' ) );
	},

	/**
	 * Sets the current config
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {PluginConfig} config - the config to set
	 */
	_setConfig : function( sandboxId, config )
	{
		$( '#' + sandboxId ).data( 'config', config );

		return;
	},

	/**
	 * Shows the given alert message
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 * @param {string} message - the message to display
	 */
	_showAlert : function( sandboxId, message )
	{
		$( '#' + sandboxId + '-alert' )
			.text( 'Error: ' + message )
			.show();

		return;
	},

	/**
	 * Hides the given alert message
	 *
	 * @private
	 *
	 * @param {string} sandboxId - the sandbox unique id
	 */
	_hideAlert : function( sandboxId )
	{
		$( '#' + sandboxId + '-alert' ).hide();
		return;
	}
};