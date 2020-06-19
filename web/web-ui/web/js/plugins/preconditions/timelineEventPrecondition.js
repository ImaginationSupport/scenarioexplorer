/**
 * @file Timeline Event Precondition plugin
 */
const plugin = {

	/**
	 * @typedef {{timelineEventId: ?string, relation: ?string}} PluginConfig
	 */

	/**
	 * Format for the timeline event relation
	 *
	 * @typedef {{id: string, label: string}} TimelineEventRelation
	 */

	/**
	 * @private
	 * @type {Array< TimelineEventRelation >}
	 */
	TEMPORAL_RELATIONS : [
		{ id : 'preceeds', label : 'Preceeds' },
		{ id : 'starts', label : 'Starts at' },
		{ id : 'during', label : 'During' },
		{ id : 'finishes', label : 'Finishes at' },
		{ id : 'preceded by', label : 'Preceded by' }
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
	 * @private
	 * @type {Array< TimelineEvent >}
	 */
	mTimelineEvents : [],

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

		g_api.listTimelineEvents( projectId, this._listTimelineEventsCallback.bind( this ) );

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

		const timelineEvent = this._findTimelineEvent( config.timelineEventId );
		if( timelineEvent === null )
		{
			return 'Unknown timeline event: ' + config.timelineEventId;
		}

		const relation = this._findRelation( config.relation );
		if( relation === null )
		{
			return 'Unknown relation: ' + config.relation;
		}

		return relation.label + ' timeline event "' + timelineEvent.name + '"';
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
		return 'Timeline Event Precondition';
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

		// relation row
		const relationDropdown = this.mPluginHelpers.generateDropdown( sandboxId + '-relation', null );
		for( let i = 0; i < this.TEMPORAL_RELATIONS.length; ++i )
		{
			this.mPluginHelpers.generateDropdownEntry(
				relationDropdown,
				this.TEMPORAL_RELATIONS[ i ].label,
				this.TEMPORAL_RELATIONS[ i ].id,
				this.TEMPORAL_RELATIONS[ i ].id === config.relation,
				false,
				null,
				null );
		}

		this.mPluginHelpers.generateFormRow(
			sandboxId + '-relation-row',
			3,
			'Can occur',
			'The method to compare the timeline event to',
			relationDropdown,
			null,
			parent );

		// timeline event row
		const timelineEventDropdown = this.mPluginHelpers.generateDropdown( sandboxId + '-timeline-event', null );

		this.mPluginHelpers.generateDropdownEntry( timelineEventDropdown, 'Loading, please wait...', null, false, false, null, null );

		this.mPluginHelpers.generateFormRow(
			sandboxId + '-timeline-event-row',
			3,
			'the interval',
			'The timeline event to test',
			timelineEventDropdown,
			null,
			parent );

		this._populateDropdown( sandboxId, config );

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
		const timelineEventId = this.mPluginHelpers.getDropdownSelectedValue( sandboxId + '-timeline-event' );
		const relationId = this.mPluginHelpers.getDropdownSelectedValue( sandboxId + '-relation' );

		if( timelineEventId === null || relationId === null )
		{
			return null;
		}
		else
		{
			return {
				'timelineEventId' : timelineEventId,
				'relation' : relationId
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
		this.mPluginHelpers.generateHelpSideBarEntry( 'Timeline Event Precondition: Can occur', 'The method to use to evaluate the timeline event', helpSideBar );
		this.mPluginHelpers.generateHelpSideBarEntry( 'Timeline Event Precondition: the interval', 'The timeline event to use', helpSideBar );

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
			: { 'timelineEventId' : null, 'relation' : null } );
	},

	/**
	 * @private
	 * @param {Array< TimelineEvent >} timelineEvents - the timeline events from the API
	 */
	_listTimelineEventsCallback : function( timelineEvents )
	{
		this.mTimelineEvents = timelineEvents;

		if( this.mPluginReadyCallback )
		{
			this.mPluginReadyCallback( this.mUniqueId );
		}

		return;
	},

	/**
	 * @private
	 * @param {string} sandboxId - the sandbox id
	 * @param {PluginConfig} timelineEventConfig
	 */
	_populateDropdown : function( sandboxId, timelineEventConfig )
	{
		const dropdown = $( '#' + sandboxId + '-timeline-event' )
			.empty();

		if( this.mTimelineEvents.length === 0 )
		{
			this.mPluginHelpers.generateDropdownEntry( dropdown, '(No timeline events in project)', null, true, false, null, null );
		}
		else
		{
			for( let i = 0; i < this.mTimelineEvents.length; ++i )
			{
				this.mPluginHelpers.generateDropdownEntry(
					dropdown,
					this.mTimelineEvents[ i ].name,
					this.mTimelineEvents[ i ].id,
					this.mTimelineEvents[ i ].id === timelineEventConfig.timelineEventId,
					false,
					null,
					null
				);
			}
		}

		return;
	},

	/**
	 * Finds the given timeline event
	 *
	 * @private
	 * @param {string} timelineEventId - the timeline event id
	 * @returns {?TimelineEvent}
	 */
	_findTimelineEvent : function( timelineEventId )
	{
		if( this.mTimelineEvents === null )
		{
			return null;
		}

		for( let i = 0; i < this.mTimelineEvents.length; ++i )
		{
			if( this.mTimelineEvents[ i ].id === timelineEventId )
			{
				return this.mTimelineEvents[ i ];
			}
		}

		return null;
	},

	/**
	 * Finds the given relation
	 *
	 * @private
	 * @param {string} relationId - the relation id
	 * @returns {?TimelineEventRelation}
	 */
	_findRelation : function( relationId )
	{
		if( this.TEMPORAL_RELATIONS === null )
		{
			return null;
		}

		for( let i = 0; i < this.TEMPORAL_RELATIONS.length; ++i )
		{
			if( this.TEMPORAL_RELATIONS[ i ].id === relationId )
			{
				return this.TEMPORAL_RELATIONS[ i ];
			}
		}

		return null;
	}

};