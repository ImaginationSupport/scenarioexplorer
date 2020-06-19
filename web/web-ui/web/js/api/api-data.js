////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: ?string,
 * 	name: string,
 * 	description: string,
 * 	start: string,
 * 	end: string,
 * 	increment: number,
 * 	owner: string,
 * 	createdOn: string,
 * 	lastEditOn: string,
 * 	lastEditBy: string,
 * 	features: Array< SerializedFeature >,
 * 	notifications: Array< SerializedNotification >
 * }} SerializedProject
 */

/**
 * Project
 *
 * @param {SerializedProject} source - the source object
 * @constructor
 */
function Project( source )
{
	/**
	 * Holds the id
	 * @type {?string}
	 */
	this.id = source && 'id' in source ? source.id : null;

	/**
	 * Holds the name
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the description
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * Holds the start date
	 * @type {?Date}
	 */
	this.start = source && 'start' in source ? g_util.parseDateTime( source.start ) : null;

	/**
	 * Holds the end date
	 * @type {?Date}
	 */
	this.end = source && 'end' in source ? g_util.parseDateTime( source.end ) : null;

	/**
	 * Holds the interval (in days)
	 * @type {number}
	 */
	this.increment = source && 'increment' in source ? source.increment : 30;

	/**
	 * Holds the owner id
	 *
	 * @type {?string}
	 */
	this.owner = source && 'owner' in source ? source.owner : null;

	/**
	 * Holds the created on date
	 *
	 * @type {?Date}
	 */
	this.createdOn = source && 'createdOn' in source ? g_util.parseDateTime( source.createdOn ) : null;

	/**
	 * @type {?Date}
	 */
	this.lastEditOn = source && 'lastEditOn' in source ? g_util.parseDateTime( source.lastEditOn ) : null;

	/**
	 * @type {?string}
	 */
	this.lastEditBy = source && 'lastEditBy' in source ? source.lastEditBy : null;

	/**
	 * @type {Array< Notification >}
	 */
	this.notifications = [];
	if( source && 'notifications' in source )
	{
		for( let i = 0; i < source.notifications.length; ++i )
		{
			this.notifications.push( new Notification( source.notifications[ i ] ) );
		}
	}

	return;
}

Project.prototype.toJSON = function()
{
	const util = new Util();

	return {
		id : this.id,
		name : this.name,
		description : this.description,
		start : util.formatDate( this.start, util.DEFAULT_DATETIME_FORMAT ),
		end : util.formatDate( this.end, util.DEFAULT_DATETIME_FORMAT ),
		increment : this.increment,
		owner : this.owner,
		createdOn : util.formatDate( this.createdOn === null ? new Date() : this.createdOn, util.DEFAULT_DATETIME_FORMAT ),
		lastEditOn : util.formatDate( this.lastEditOn === null ? new Date() : this.lastEditOn, util.DEFAULT_DATETIME_FORMAT ),
		lastEditBy : this.lastEditBy
	};
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: ?string,
 * 	name: string,
 * 	description: string,
 * 	start: string,
 * 	end: string,
 * 	increment: number,
 * 	createdOn: string,
 * 	creatorId: string,
 * 	sourceProjectId: string,
 * 	features: Array< SerializedFeature >,
 * 	timelineEvents: Array< SerializedTimelineEvent>
 * }} SerializedProjectTemplate
 */

/**
 * Project Template
 *
 * @param {SerializedProjectTemplate} source - the source object
 * @constructor
 */
function ProjectTemplate( source )
{
	/**
	 * Holds the id
	 * @type {?string}
	 */
	this.id = source && 'id' in source ? source.id : null;

	/**
	 * Holds the name
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the description
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * Holds the start date
	 * @type {?Date}
	 */
	this.start = source && 'start' in source ? g_util.parseDateTime( source.start ) : null;

	/**
	 * Holds the end date
	 * @type {?Date}
	 */
	this.end = source && 'end' in source ? g_util.parseDateTime( source.end ) : null;

	/**
	 * Holds the interval (in days)
	 * @type {number}
	 */
	this.increment = source && 'increment' in source ? source.increment : 30;

	/**
	 * Holds the created on date
	 *
	 * @type {?Date}
	 */
	this.createdOn = source && 'createdOn' in source ? g_util.parseDateTime( source.createdOn ) : null;

	/**
	 * Holds the creator id
	 *
	 * @type {?string}
	 */
	this.creatorId = source && 'creatorId' in source ? source.creatorId : null;

	/**
	 * Holds the source project id
	 *
	 * @type {?string}
	 */
	this.sourceProjectId = source && 'sourceProjectId' in source ? source.sourceProjectId : null;

	/**
	 * Holds the features
	 *
	 * @type {Array< Feature >}
	 */
	this.features = [];
	if( source && 'features' in source )
	{
		for( let i = 0; i < source.features.length; ++i )
		{
			this.features.push( new Feature( source.features[ i ] ) );
		}
	}

	/**
	 * Holds the timeline events
	 *
	 * @type {Array< TimelineEvent >}
	 */
	this.timelineEvents = [];
	if( source && 'timelineEvents' in source )
	{
		for( let i = 0; i < source.timelineEvents.length; ++i )
		{
			this.timelineEvents.push( new TimelineEvent( source.timelineEvents[ i ] ) );
		}
	}

	return;
}

ProjectTemplate.prototype.toJSON = function()
{
	const util = new Util();

	const serializedTimelineEvents = [];
	for( let i = 0; i < this.timelineEvents.length; ++i )
	{
		serializedTimelineEvents.push( this.timelineEvents[ i ].toJSON() );
	}

	return {
		id : this.id,
		name : this.name,
		description : this.description,
		start : util.formatDate( this.start, util.DEFAULT_DATETIME_FORMAT ),
		end : util.formatDate( this.end, util.DEFAULT_DATETIME_FORMAT ),
		increment : this.increment,
		creatorId : this.creatorId,
		createdOn : util.formatDate( this.createdOn === null ? new Date() : this.createdOn, util.DEFAULT_DATETIME_FORMAT ),
		sourceProjectId : this.sourceProjectId,
		features : this.features,
		timelineEvents : serializedTimelineEvents
	};
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	start: string,
 * 	end: string,
 * 	name: string,
 * 	description: string,
 * 	color: ?string,
 * 	features: Object.< string, string >
 * 	}} SerializedState
 */

/**
 * @param {SerializedState} source
 * @constructor
 * @extends {StateNode}
 */
function State( source )
{
	jQuery.extend( true, this, new StateNode( source ) ); // TODO use the closure compiler version

	this.type = TreeNodeType.STATE;

	/**
	 * Holds the feature values
	 *
	 * @const {Object.< string, string >}
	 */
	this.features = source && 'features' in source ? source.features : {};

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	name: string,
 * 	description: string,
 * 	originViewId: string,
 * 	preconditions: Array< SerializedPreconditionInstance >,
 * 	outcomes: Array< SerializedOutcome >,
 * 	}} SerializedConditioningEvent
 */

/**
 * @param {SerializedConditioningEvent} source
 * @constructor
 */
function ConditioningEvent( source )
{
	/**
	 * Holds the id
	 *
	 * @type {?string}
	 */
	this.id = source && 'id' in source ? source.id : null;

	/**
	 * Holds the name
	 *
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the description
	 *
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * Holds the origin view id
	 *
	 * @type {string}
	 */
	this.originViewId = source && 'originViewId' in source ? source.originViewId : '';

	/**
	 * @type {Array< PreconditionInstance >}
	 */
	this.preconditions = [];
	if( source && 'preconditions' in source )
	{
		for( let i = 0; i < source.preconditions.length; ++i )
		{
			this.preconditions.push( new PreconditionInstance( source.preconditions[ i ] ) );
		}
	}

	/**
	 * @type {Array< Outcome >}
	 */
	this.outcomes = [];
	if( source && 'outcomes' in source )
	{
		for( let i = 0; i < source.outcomes.length; ++i )
		{
			this.outcomes.push( new Outcome( source.outcomes[ i ] ) );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {View} view - the view to check for the conditioning event
 *
 * @returns {boolean} true if the conditioning event is assigned to the given view
 */
ConditioningEvent.prototype.assignedToView = function( view )
{
	for( let i = 0; i < view.assigned.length; ++i )
	{
		if( this.id === view.assigned[ i ] )
		{
			return true;
		}
	}

	return false;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: ?string,
 * 	name: string,
 * 	description: string,
 * 	config: Object
 * 	}} SerializedPreconditionInstance
 */

/**
 * This is a pseudo-object which doesn't exist in the Java version, but is better for JavaScript
 *
 * @param {SerializedPreconditionInstance} source
 * @constructor
 */
function PreconditionInstance( source )
{
	/**
	 * Holds the id
	 *
	 * @type {?string}
	 */
	this.id = source && 'id' in source ? source.id : null;

	/**
	 * Holds the config
	 *
	 * @type {Object}
	 */
	this.config = source && 'config' in source ? source.config : null;

	return;
}

PreconditionInstance.prototype.toJSON = function()
{
	return {
		'id' : this.id,
		'config' : this.config
	};
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: ?string,
 * 	parent: ?string,
 * 	dbId: string,
 * 	name: string,
 * 	description: string,
 * 	type: {TreeNode.NodeType},
 * 	color: ?string
 * 	}} SerializedTreeNode
 */

/**
 * Tree Node
 *
 * @protected
 * @param source
 * @constructor
 */
function TreeNode( source )
{
	/**
	 * Holds the id
	 *
	 * @type {?string}
	 */
	this.id = source && 'id' in source ? source.id : null;

	/**
	 * Holds the parent id
	 *
	 * @type {?string}
	 */
	this.parentId = source && 'parent' in source ? source.parent : null;

	/**
	 * Holds the DB id
	 *
	 * @type {?string}
	 */
	this.dbId = source && 'DBid' in source ? source.DBid : null;

	/**
	 * Holds the name
	 *
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the description
	 *
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * Holds the node type
	 *
	 * @type {TreeNodeType}
	 */
	this.type = source && 'type' in source ? source.type : TreeNodeType.UNKNOWN;

	/**
	 * Holds the color
	 *
	 * @type {string}
	 */
	this.color = source && 'color' in source ? source.color : 'rgb(100,100,100)'; // TODO this is never actually set... instead it is stored on the separate State / etc

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Enum for the different types of tree nodes
 *
 * @enum {string}
 */
const TreeNodeType = {
	UNKNOWN : 'Unknown!',
	STATE : 'S',
	CONDITIONING_EVENT : 'CE',
	OUTCOME : 'C'
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	start: string,
 * 	end: string,
 * 	name: string,
 * 	description: string,
 * 	color: ?string
 * 	}} SerializedStateNode
 */

/**
 * @param {SerializedStateNode} source
 * @constructor
 * @extends {TreeNode}
 */
function StateNode( source )
{
	jQuery.extend( true, this, new TreeNode( source ) ); // TODO use the closure compiler version

	/**
	 * Holds the start date
	 *
	 * @type {?Date}
	 */
	this.start = source && 'start' in source ? new Date( source.start ) : null;

	/**
	 * Holds the end time
	 *
	 * @type {?Date}
	 */
	this.end = source && 'end' in source ? new Date( source.end ) : null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	name: string,
 * 	description: string,
 * 	color: ?string
 * 	}} SerializedConditioningEventNode
 */

/**
 * @param source
 * @constructor
 * @extends {TreeNode}
 */
function ConditioningEventNode( source )
{
	jQuery.extend( true, this, new TreeNode( source ) ); // TODO use the closure compiler version

	return;
}

/**
 * @typedef {{
 * 	id: string,
 * 	name: string,
 * 	description: string,
 * 	outcome: number,
 * 	outcomeLabel: string,
 * 	color: ?string
 * 	}} SerializedOutcomeNode
 */

/**
 * @param {SerializedOutcomeNode} source
 * @constructor
 * @extends {TreeNode}
 */
function OutcomeNode( source )
{
	jQuery.extend( true, this, new TreeNode( source ) ); // TODO use the closure compiler version

	/**
	 * Holds the outcome index
	 *
	 * @type {number}
	 */
	this.outcome = source && 'outcome' in source ? source.outcome : -1;

	/**
	 * Holds the outcome name
	 *
	 * @type {?string}
	 */
	this.outcomeLabel = source && 'outcomeLabel' in source ? source.outcomeLabel : null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Holds the available view types
 *
 * @enum {string}
 */
const ViewType = {

	/**
	 * Unknown view type
	 */
	UNKNOWN : 'U',

	/**
	 * Futures Building View
	 */
	FUTURES_BUILDING : 'FB',

	/**
	 * What-If View
	 */
	WHAT_IF : 'WI',

	/**
	 * Extreme State View
	 */
	EXTREME_STATE : 'ES',

	/**
	 * Smart Query View
	 */
	SMART_QUERY : 'SQ'
};

/**
 * Helper class to convert the view types
 *
 * @constructor
 */
function ViewTypeConverter()
{
	return;
}

/**
 * Converts the raw type to the pretty text version
 *
 * @param {string} raw - the raw version to convert
 *
 * @returns {string}
 */
ViewTypeConverter.prototype.getPrettyVersion = function( raw )
{
	switch( raw )
	{
		case 'FB':
			return 'Futures Building';

		case 'WI':
			return 'What-If?';

		case 'ES':
			return 'Extreme State';

		case 'SQ':
			return 'Smart Query';

		default:
			return 'Unknown!';
	}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 *	path: Array< { id:string, outcome:number } >,
 *  sensitivity: number,
 *  specificity: number
 * }} SmartQueryStatsIndicator
 */

/**
 * @typedef {{
 *	name: string,
 *	description: string,
 *	members: Array< string >,
 *	indicators: Array< SmartQueryStatsIndicator >
 * }} SmartQueryStatsGrouping
 */

/**
 * @typedef {{
 * 	groupings: Array<SmartQueryStatsGrouping>
 * }} SmartQueryStats
 */

/**
 * @typedef {{
 * 	id: string,
 * 	name: string,
 * 	description: string,
 * 	type: ViewType,
 * 	projectId: string,
 * 	assigned: Array< string >,
 * 	conditioningEvents: Array< SerializedConditioningEvent >
 * 	}} SerializedView
 */

/**
 * Base class for an Imagination Support View
 *
 * @param {SerializedView} source
 * @constructor
 */
function View( source )
{
	/**
	 * Holds the id
	 *
	 * @type {?string}
	 */
	this.id = source && 'id' in source ? source.id : null;

	/**
	 * Holds the name
	 *
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the description
	 *
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * @type {?string}
	 */
	this.projectId = source && 'projectId' in source ? source.projectId : null;

	/**
	 * @type {Array< string >}
	 */
	this.assigned = source && 'assigned' in source ? source.assigned : [];

	/**
	 * Holds the view type
	 *
	 * @type {ViewType}
	 */
	this.type = source && 'type' in source ? source.type : ViewType.UNKNOWN;

	/**
	 * Holds the config
	 *
	 * @type {?Object}
	 */
	this.config = source && 'config' in source ? source.config : {};

	/**
	 * Holds the stats
	 *
	 * @type {?Object}
	 */
	this.stats = null;

	/**
	 * Holds the tree nodes
	 *
	 * @type {Array< TreeNode >}
	 */
	this.treeNodes = [];

	/**
	 * Holds the id of the root node
	 *
	 * @type {?string}
	 */
	this.rootNodeId = null;

	/**
	 * Holds all the conditioning events, even if they aren't in the tree
	 *
	 * @type {Array< ConditioningEvent >}
	 */
	this.conditioningEvents = [];

	if( source && 'conditioningEvents' in source )
	{
		for( let i = 0; i < source.conditioningEvents.length; ++i )
		{
			this.conditioningEvents.push( new ConditioningEvent( source.conditioningEvents[ i ] ) );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	root: ?string,
 *  tree: Array< SerializedTreeNode >
 * }} SerializedViewTree
 */

/**
 * @constructor
 *
 * @param {SerializedViewTree} source
 */
function ViewTree( source )
{
	const parser = new TreeNodeParser();

	/**
	 * Holds the tree nodes
	 *
	 * @type {Array< TreeNode >}
	 */
	this.treeNodes = [];

	/**
	 * Holds the id of the root node
	 *
	 * @type {?string}
	 */
	this.rootNodeId = null;

	if( source )
	{
		if( 'nodes' in source && source.nodes )
		{
			for( let i = 0; i < source.nodes.length; ++i )
			{
				this.treeNodes.push( parser.parse( source.nodes[ i ] ) );
			}
		}

		if( 'root' in source && source.root )
		{
			this.rootNodeId = source.root;
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: ?string,
 * 	name: string,
 * 	description: string,
 * 	featureType: string,
 * 	config: Object,
 * 	projectorId: ?string,
 * 	projectorConfig: Object
 * }} SerializedFeature
 */

/**
 * Feature
 *
 * @param {SerializedFeature} source
 * @constructor
 */
function Feature( source )
{
	/**
	 * Holds the id
	 *
	 * @type {?string}
	 */
	this.id = source && 'id' in source ? source.id : null;

	/**
	 * Holds the name
	 *
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the description
	 *
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * Holds the id to the feature type
	 *
	 * @type {string}
	 */
	this.featureType = source && 'featureType' in source ? source.featureType : '';

	/**
	 * Holds the configuration
	 *
	 * @type {Object}
	 */
	this.config = source && 'config' in source ? source.config : {};

	/**
	 * Holds the projector id
	 *
	 * @type {?string}
	 */
	this.projectorId = source && 'projectorId' in source ? source.projectorId : null;

	/**
	 * Holds the projector config
	 *
	 * @type {Object}
	 */
	this.projectorConfig = source && 'projectorConfig' in source ? source.projectorConfig : null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 *  apiInit: function( ScenarioExplorerAPI, PluginHelpers, string, ?string, ?string, ?function(string) )
 *  apiGetConfigTextSummary: function( Object ): string,
 *  apiGetCurrentValueSummary: function( Object, string ): string,
 *  apiGenerateConfigUserInterface: function( jQuery, string, ?Object ),
 *  apiGetConfigUserInterfaceData: function( jQuery, string ): ?Object,
 *  apiGenerateEntryUserInterface: function( jQuery, string, Object, ?Object ),
 *  apiGetEntryUserInterfaceData: function( jQuery, string, Object ): ?string,
 *  apiGenerateSideBarHelpItems: function( jQuery )
 * }} ScenarioExplorerPluginSource
 */

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 *
 * @interface
 */
function ScenarioExplorerPlugin()
{
	/**
	 * @type {?ScenarioExplorerPluginSource}
	 */
	this.src = null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	name: string,
 * 	helpText: string,
 * 	aboutText: string,
 * 	isContinuous: boolean,
 * 	}} SerializedFeatureTypePlugin
 */

/**
 * Feature type
 *
 * @param {SerializedFeatureTypePlugin} source
 *
 * @constructor
 * @implements {ScenarioExplorerPlugin}
 */
function FeatureTypePlugin( source )
{
	/**
	 * Holds the id
	 *
	 * @type {string}
	 */
	this.id = source && 'id' in source ? source.id : '';

	/**
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * @type {string}
	 */
	this.helpText = source && 'helpText' in source ? source.helpText : '';

	/**
	 * @type {string}
	 */
	this.aboutText = source && 'aboutText' in source ? source.aboutText : '';

	/**
	 * @type {boolean}
	 */
	this.isContinuous = source && 'isContinuous' in source ? source.isContinuous : false;

	/**
	 * @type {?ScenarioExplorerPluginSource}
	 */
	this.src = null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	name: string,
 * 	description: string
 * 	}} SerializedPreconditionPlugin
 */

/**
 *
 * @param {SerializedPreconditionPlugin} source
 *
 * @constructor
 * @implements {ScenarioExplorerPlugin}
 */
function PreconditionPlugin( source )
{
	/**
	 * Holds the id
	 *
	 * @type {string}
	 */
	this.id = source && 'id' in source ? source.id : '';

	/**
	 * Holds the name
	 *
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the description
	 *
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * @type {?ScenarioExplorerPluginSource}
	 */
	this.src = null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	name: string,
 * 	likelihood: number,
 * 	effects: Array< SerializedOutcomeEffectInstance >
 * 	}} SerializedOutcome
 */

/**
 *
 * @param {SerializedOutcome} source
 *
 * @constructor
 */
function Outcome( source )
{
	/**
	 * Holds the id
	 *
	 * @type {string}
	 */
	this.id = source && 'id' in source ? source.id : '';

	/**
	 * Holds the name
	 *
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the likelihood
	 *
	 * @type {number}
	 */
	this.likelihood = source && 'likelihood' in source ? source.likelihood : 0.0;

	/**
	 * @type {Array< OutcomeEffectInstance >}
	 */
	this.effects = [];
	if( source && 'effects' in source )
	{
		for( let i = 0; i < source.effects.length; ++i )
		{
			this.effects.push( new OutcomeEffectInstance( source.effects[ i ] ) );
		}
	}

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: ?string,
 * 	config: Object
 * 	}} SerializedOutcomeEffectInstance
 */

/**
 * This is a pseudo-object which doesn't exist in the Java version, but is better for JavaScript
 *
 * @param {SerializedOutcomeEffectInstance} source
 * @constructor
 */
function OutcomeEffectInstance( source )
{
	/**
	 * Holds the id
	 *
	 * @type {?string}
	 */
	this.id = source && 'id' in source ? source.id : null;

	/**
	 * Holds the config
	 *
	 * @type {Object}
	 */
	this.config = source && 'config' in source ? source.config : null;

	return;
}

OutcomeEffectInstance.prototype.toJSON = function()
{
	return {
		'id' : this.id,
		'config' : this.config
	};
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	name: string,
 * 	description: string
 * 	}} SerializedOutcomeEffectPlugin
 */

/**
 *
 * @param {SerializedOutcomeEffectPlugin} source
 *
 * @constructor
 * @implements {ScenarioExplorerPlugin}
 */
function OutcomeEffectPlugin( source )
{
	/**
	 * Holds the id
	 *
	 * @type {string}
	 */
	this.id = source && 'id' in source ? source.id : '';

	/**
	 * Holds the name
	 *
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the description
	 *
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * @type {?ScenarioExplorerPluginSource}
	 */
	this.src = null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	name: string,
 * 	helpText: string,
 * 	aboutText: string,
 * 	applicableFor: Array< string >
 * 	}} SerializedProjectorPlugin
 */

/**
 *
 * @param {SerializedProjectorPlugin} source
 * @constructor
 * @implements {ScenarioExplorerPlugin}
 */
function ProjectorPlugin( source )
{
	/**
	 * Holds the id
	 *
	 * @type {string}
	 */
	this.id = source && 'id' in source ? source.id : '';

	/**
	 * Holds the name
	 *
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the help text
	 *
	 * @type {string}
	 */
	this.helpText = source && 'helpText' in source ? source.helpText : '';

	/**
	 * Holds the about text
	 *
	 * @type {string}
	 */
	this.aboutText = source && 'aboutText' in source ? source.aboutText : '';

	/**
	 * Holds the help text
	 *
	 * @type {Array<string>}
	 */
	this.applicableFor = source && 'applicableFor' in source ? source.applicableFor : [];

	/**
	 * @type {?ScenarioExplorerPluginSource}
	 */
	this.src = null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 * 	id: string,
 * 	start: string,
 * 	end: string,
 * 	name: string,
 * 	description: string,
 * 	url: string
 * 	}} SerializedTimelineEvent
 */

/**
 * @param {SerializedTimelineEvent} source
 * @constructor
 */
function TimelineEvent( source )
{
	/**
	 * Holds the id
	 *
	 * @type {?string}
	 */
	this.id = source && 'id' in source ? source.id : null;

	/**
	 * Holds the start
	 *
	 * @type {?Date}
	 */
	this.start = source && 'start' in source ? g_util.parseDateTime( source.start ) : null;

	/**
	 * Holds the end
	 *
	 * @type {?Date}
	 */
	this.end = source && 'end' in source ? g_util.parseDateTime( source.end ) : null;

	/**
	 * Holds the name
	 *
	 * @type {string}
	 */
	this.name = source && 'name' in source ? source.name : '';

	/**
	 * Holds the description
	 *
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * Holds the URL
	 *
	 * @type {string}
	 */
	this.url = source && 'url' in source ? source.url : '';

	return;
}

TimelineEvent.prototype.toJSON = function()
{
	return {
		'id' : this.id,
		'name' : this.name,
		'description' : this.description,
		'start' : g_util.formatDate( this.start, g_util.DEFAULT_DATETIME_FORMAT ),
		'end' : g_util.formatDate( this.end, g_util.DEFAULT_DATETIME_FORMAT ),
		'url' : this.url
	};
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 *	id: string,
 * 	fullName: string,
 * 	userName: string,
 * 	isSiteAdmin: boolean,
 * 	lastLogin: string,
 * 	access: Array< string >
 * 	}} SerializedUser
 */

/**
 * @typedef {{
 *	dateFormat: ?string,
 *	timeFormat: ?string,
 *	projects: ?Object
 * }} SerializedUserPreferences
 */

/**
 * @param {SerializedUser} source
 * @constructor
 */
function User( source )
{
	/**
	 * Holds the user's id
	 *
	 * @type {string}
	 */
	this.id = source && 'id' in source ? source.id : '';

	/**
	 * Holds the user's full name
	 *
	 * @type {string}
	 */
	this.fullName = source && 'fullName' in source ? source.fullName : '';

	/**
	 * Holds the user's userName
	 *
	 * @type {string}
	 */
	this.userName = source && 'userName' in source ? source.userName : '';

	/**
	 * Holds if the user is a site admin
	 *
	 * @type {boolean}
	 */
	this.siteAdmin = source && 'isSiteAdmin' in source ? source.isSiteAdmin : false;

	/**
	 * Holds the last time the user logged in
	 *
	 * @type {?Date}
	 */
	this.lastLogin = source && 'lastLogin' in source ? g_util.parseDateTime( source.lastLogin ) : null;

	/**
	 * Holds the list of project ids the user has access
	 *
	 * @type {Array< string >}
	 */
	this.access = source && 'access' in source ? source.access : [];

	/**
	 * Holds the user preferences
	 *
	 * @type {{SerializedUserPreferences}}
	 */
	this.preferences = source && 'preferences' in source ? source.preferences : {};

	return;
}

/**
 * Gets the date format preference
 *
 * @returns {string}
 */
User.prototype.getDateFormatPreference = function()
{
	return this.preferences && 'dateFormat' in this.preferences
		? this.preferences.dateFormat
		: g_util.DEFAULT_DATE_FORMAT;
};

/**
 * Sets the date format preference
 *
 * @param {string} dateFormat
 */
User.prototype.setDateFormatPreference = function( dateFormat )
{
	if( !this.preferences )
	{
		this.preferences = {};
	}

	this.preferences.dateFormat = dateFormat;
	return;
};

/**
 * Gets the time format preference
 *
 * @returns {string}
 */
User.prototype.getTimeFormatPreference = function()
{
	return this.preferences && 'timeFormat' in this.preferences
		? this.preferences.timeFormat
		: g_util.DEFAULT_TIME_FORMAT;
};

/**
 * Sets the time format preference
 *
 * @param {string} timeFormat
 */
User.prototype.setTimeFormatPreference = function( timeFormat )
{
	if( !this.preferences )
	{
		this.preferences = {};
	}

	this.preferences.timeFormat = timeFormat;
	return;
};

/**
 * Gets the datetime format preference
 *
 * @returns {string}
 */
User.prototype.getDateTimeFormatPreference = function()
{
	return this.getDateFormatPreference() + ' ' + this.getTimeFormatPreference();
};

/**
 * Gets the given project preferences
 *
 * @param {string} projectId
 *
 * @returns {Object}
 */
User.prototype.getProjectPreferences = function( projectId )
{
	if( !this.preferences || !( 'projects' in this.preferences ) )
	{
		return {};
	}

	const projects = /** @type {Array< {id:string, preferences:Object} >} */( this.preferences.projects );
	for( let i = 0; i < projects.length; ++i )
	{
		if( projects[ i ].id === projectId )
		{
			return projects[ i ].preferences;
		}
	}

	return {};
};

/**
 * Sets the preferences for the given project
 *
 * @param {string} projectId
 * @param {Object} preferences
 */
User.prototype.setProjectPreferences = function( projectId, preferences )
{
	// default the root preferences if it does not exist
	if( !this.preferences )
	{
		this.preferences = {};
	}

	// default the projects array if it does not exist
	if( !( 'projects' in this.preferences ) )
	{
		this.preferences.projects = [];
	}

	// find the existing preferences
	for( let i = 0; i < this.preferences.projects.length; ++i )
	{
		if( this.preferences.projects[ i ].id === projectId )
		{
			this.preferences.projects[ i ].preferences = preferences;
			return;
		}
	}

	// project does not exist in the array, so add it
	this.preferences.projects.push( { 'id' : projectId, 'preferences' : preferences, 'views' : [] } );

	return;
};

/**
 * Gets the preferences for the given project and view id
 *
 * @param {string} projectId
 * @param {string} viewId
 *
 * @returns {Object}
 */
User.prototype.getViewPreferences = function( projectId, viewId )
{
	const projectPreferences = this.getProjectPreferences( projectId );

	if( !( 'views' in projectPreferences ) )
	{
		return {};
	}

	const views = /** @type {Array< {id:string, preferences:Object} >} */( projectPreferences.views );
	for( let viewIndex = 0; viewIndex < views.length; ++viewIndex )
	{
		if( views[ viewIndex ].id === viewId )
		{
			return views[ viewIndex ].preferences;
		}
	}

	return {};
};

/**
 * Sets the preferences for the given project and view id
 *
 * @param {string} projectId
 * @param {string} viewId
 * @param {Object} preferences
 */
User.prototype.setViewPreferences = function( projectId, viewId, preferences )
{
	// get the existing project preferences
	const projectPreferences = this.getProjectPreferences( projectId );

	if( !( 'views' in projectPreferences ) )
	{
		projectPreferences.views = [];
	}

	// update the existing entry
	let found = false;
	const views = /** @type {Array< {id:string, preferences:Object} >} */( projectPreferences.views );
	for( let i = 0; i < views.length && !found; ++i )
	{
		if( views[ i ].id === viewId )
		{
			views[ i ].preferences = preferences;
			found = true;
		}
	}

	// entry did not exist, so add it
	if( !found )
	{
		views.push( { 'id' : viewId, 'preferences' : preferences } );
	}

	// make sure the project preferences are set first
	this.setProjectPreferences( projectId, projectPreferences );

	return;
};

/**
 * The JSON version to serialize
 *
 * @returns {Object}
 */
User.prototype.toJSON = function()
{
	return {
		'userName' : this.userName,
		'fullName' : this.fullName,
		'isSiteAdmin' : this.siteAdmin,
		'lastLogin' : g_util.formatDate( this.lastLogin, g_util.DEFAULT_DATETIME_FORMAT ),
		'access' : this.access,
		'preferences' : this.preferences
	};
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Helper class to do the tree node parsing
 *
 * @constructor
 */
function TreeNodeParser()
{
	return;
}

/**
 * Parses the given source JSON object into the correct tree node
 *
 * @param {SerializedTreeNode} source
 * @return {TreeNode}
 */
TreeNodeParser.prototype.parse = function( source )
{
	if( source === null || !( 'type' in source ) )
	{
		console.warn( 'Invalid node source: %O', source );
		return null;
	}

	switch( source.type )
	{
		case TreeNodeType.STATE:
			return new StateNode( /** @type {SerializedStateNode} */( source ) );

		case TreeNodeType.CONDITIONING_EVENT:
			return new ConditioningEventNode( /** @type {SerializedConditioningEventNode} */( source ) );

		case TreeNodeType.OUTCOME:
			return new OutcomeNode( /** @type {SerializedOutcomeNode} */( source ) );

		default:
			console.warn( 'Unknown node type: %O in %O', source.type, source );
			return null;
	}
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 *  scope: string,
 *  key: string,
 *  description: string,
 *  state: string,
 *  viewId: ?string,
 *  timelineEventId: ?string,
 *  conditioningEventId: ?string,
 *  featureId: ?string,
 * }} SerializedNotification
 */

/**
 * Notification
 *
 * @param {SerializedNotification} source - the source object
 * @constructor
 */
function Notification( source )
{
	/**
	 * Holds the scope
	 * @type {string}
	 */
	this.scope = source && 'scope' in source ? source.scope : '';

	/**
	 * Holds the key
	 * @type {string}
	 */
	this.key = source && 'key' in source ? source.key : '';

	/**
	 * Holds the description
	 * @type {string}
	 */
	this.description = source && 'description' in source ? source.description : '';

	/**
	 * Holds the state
	 * @type {string}
	 */
	this.state = source && 'state' in source ? source.state : '';

	/**
	 * Holds the view id
	 * @type {?string}
	 */
	this.viewId = source && 'viewId' in source ? source.viewId : null;

	/**
	 * Holds the timeline event id
	 * @type {?string}
	 */
	this.timelineEventId = source && 'timelineEventId' in source ? source.timelineEventId : null;

	/**
	 * Holds the conditioning event id
	 * @type {?string}
	 */
	this.conditioningEventId = source && 'conditioningEventId' in source ? source.conditioningEventId : null;

	/**
	 * Holds the feature id
	 * @type {?string}
	 */
	this.featureId = source && 'featureId' in source ? source.featureId : null;

	return;
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * API Data Helper library
 *
 * @constructor
 */
function ApiDataHelper()
{
	return;
}

/**
 * Creates a new user instance
 *
 * @param {string} userName - the userName
 * @param {string} fullName - the full name
 * @param {boolean} siteAdmin - the start date
 * @param {Array< string >} access - the list of project ids
 *
 * @returns {User} the user created
 */
ApiDataHelper.prototype.createUserInstance = function( userName, fullName, siteAdmin, access )
{
	return new User( /** @type {SerializedUser} */( {
		'fullName' : fullName,
		'userName' : userName,
		'siteAdmin' : siteAdmin,
		'access' : access,
		'lastLogin' : null
	} ) );
};

/**
 * Creates a new project instance
 *
 * @param {string} name - the project name
 * @param {string} description - the project description
 * @param {Date|string} startDate - the start date
 * @param {Date|string} endDate - the end date
 * @param {number} increment - the increment
 *
 * @returns {Project} the project instance
 */
ApiDataHelper.prototype.createProjectInstance = function( name, description, startDate, endDate, increment )
{
	return new Project( /** @type {SerializedProject} */( {
		'id' : null,
		'name' : name,
		'description' : description,
		'start' : typeof startDate === 'string' ? startDate : g_util.formatDate( startDate, g_util.DEFAULT_DATETIME_FORMAT ),
		'end' : typeof endDate === 'string' ? endDate : g_util.formatDate( endDate, g_util.DEFAULT_DATETIME_FORMAT ),
		'increment' : increment,
		'owner' : null,
		'createdOn' : null,
		'lastUpdateOn' : null,
		'lastUpdateBy' : null
	} ) );
};

/**
 * Creates a project template instance
 *
 * @param {string} name
 * @param {string} description
 * @param {Date|string} startDate
 * @param {Date|string} endDate
 * @param {number} increment
 * @param {string} sourceProjectId
 * @param {Array< SerializedFeature >} features
 * @param {Array< SerializedTimelineEvent >} timelineEvents
 *
 * @returns {ProjectTemplate}
 */
ApiDataHelper.prototype.createProjectTemplateInstance = function( name, description, startDate, endDate, increment, sourceProjectId, features, timelineEvents )
{
	return new ProjectTemplate( /** @type {SerializedProjectTemplate} */( {
		'id' : null,
		'name' : name,
		'description' : description,
		'start' : startDate,
		'end' : endDate,
		'increment' : increment,
		'createdOn' : null,
		'creatorId' : null,
		'sourceProjectId' : sourceProjectId,
		'features' : features,
		'timelineEvents' : timelineEvents
	} ) );
};

/**
 * Creates a new view instance
 *
 * @param {string} name
 * @param {string} description
 * @param {string} viewType
 * @param {string} projectId
 * @param {?Object} config
 *
 * @returns {View}
 */
ApiDataHelper.prototype.createViewInstance = function( name, description, viewType, projectId, config )
{
	return new View( /** @type {SerializedView} */( {
		'id' : null,
		'name' : name,
		'description' : description,
		'type' : viewType,
		'root' : null,
		'projectId' : projectId,
		'config' : config,
		'assigned' : [],
		'tree' : [],
		'conditioningEvents' : []
	} ) );
};

/**
 * Creates a new feature instance
 *
 * @param {string} name
 * @param {string} description
 * @param {string} featureTypeId
 * @param {Object} featureConfig
 * @param {?string} projectorId
 * @param {?Object} projectorConfig
 *
 * @returns {Feature}
 */
ApiDataHelper.prototype.createFeatureInstance = function( name, description, featureTypeId, featureConfig, projectorId, projectorConfig )
{
	return new Feature( /** @type {SerializedFeature} */( {
		'id' : null,
		'name' : name,
		'description' : description,
		'featureType' : featureTypeId,
		'config' : featureConfig,
		'projectorId' : projectorId,
		'projectorConfig' : projectorConfig
	} ) );
};

/**
 * Creates a new timeline event instance
 *
 * @param {string} name - the timeline event name
 * @param {string} description - the timeline event description
 * @param {Date|string} startDate - the start date
 * @param {Date|string} endDate - the end date
 * @param {?string} url - the URL
 *
 * @returns {TimelineEvent} the timeline event created
 */
ApiDataHelper.prototype.createTimelineEventInstance = function( name, description, startDate, endDate, url )
{
	return new TimelineEvent( /** @type {SerializedTimelineEvent} */( {
		'id' : null,
		'name' : name,
		'description' : description,
		'start' : typeof startDate === 'string' ? startDate : g_util.formatDate( startDate, g_util.DEFAULT_DATE_FORMAT ),
		'end' : typeof endDate === 'string' ? endDate : g_util.formatDate( endDate, g_util.DEFAULT_DATE_FORMAT ),
		'url' : url
	} ) );
};

/**
 * Creates a new conditioning event instance
 *
 * @param {string} name - the conditioning event name
 * @param {string} description - the conditioning event description
 * @param {string} originViewId - the origin view id
 * @param {Array< Precondition >} preconditions - the preconditions
 * @param {Array< Outcome >} outcomes - the outcomes
 *
 * @returns {ConditioningEvent} the conditioning event created
 */
ApiDataHelper.prototype.createConditioningEventInstance = function( name, description, originViewId, preconditions, outcomes )
{
	return new ConditioningEvent( /** @type {SerializedConditioningEvent} */( {
		'id' : null,
		'name' : name,
		'description' : description,
		'originViewId' : originViewId,
		'preconditions' : preconditions,
		'outcomes' : outcomes
	} ) );
};

/**
 * Creates a new precondition instance
 *
 * @param {string} preconditionId - the precondition id
 * @param {Object} config - the config
 *
 * @returns {PreconditionInstance}
 */
ApiDataHelper.prototype.createPreconditionInstance = function( preconditionId, config )
{
	return new PreconditionInstance( /** @type {SerializedPreconditionInstance} */( {
		'id' : preconditionId,
		'config' : config
	} ) );
};

/**
 * Creates a new outcome instance
 *
 * @param {string} name - the name
 * @param {number} likelihood - the likelihood
 * @param {Array< OutcomeEffectInstance >} effects - the list of outcome effects
 * @returns {Outcome}
 */
ApiDataHelper.prototype.createOutcomeInstance = function( name, likelihood, effects )
{
	// TODO convert the effects to the correct form

	return new Outcome( /** @type {SerializedOutcome} */( {
		'id' : null,
		'name' : name,
		'likelihood' : likelihood,
		'effects' : effects
	} ) );
};

/**
 * Creates a new outcome effect instance
 *
 * @param {?string} outcomeEffectId - the id of the outcome effect
 * @param {?Object} config - the config
 *
 * @returns {OutcomeEffectInstance}
 */
ApiDataHelper.prototype.createOutcomeEffectInstance = function( outcomeEffectId, config )
{
	return new OutcomeEffectInstance( /** @type {SerializedOutcomeEffectInstance} */( {
		'id' : outcomeEffectId,
		'config' : config
	} ) );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
