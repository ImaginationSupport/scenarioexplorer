////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @typedef {{
 *  webSocketAction: string,
 *  ajaxUriTemplate: string,
 *  ajaxMethod: string,
 *  payloadParameterName: ?string,
 *  isSynchronous: (boolean|undefined),
 *  isSourceRequest: (boolean|undefined)
 *  }} ApiRequestConfig
 */

/**
 * @typedef {{
 *  webSocketAction: string,
 *  ajaxUri: string,
 *  ajaxMethod: string,
 *  apiParameters: Object,
 *  apiHandlerCallback: function(ApiRequest, Object),
 *  userCallbacks: Array< function(...*) >
 *  }} ApiRequest
 */

/**
 * @typedef {{
 * 	apiRequestConfig: ApiRequestConfig,
 * 	parameters: Object.<string, *>,
 * 	apiHandlerCallback: function(ApiRequest, Object),
 * 	userCallback: function()
 * 	}} SynchronousRequestQueueEntry
 */

/**
 * Scenario Explorer API
 *
 * @constructor
 * @param {boolean} usingWebSockets - True to use web sockets, false to use AJAX/REST
 * @param {string} deployPath - The URI to scenario explorer
 */
function ScenarioExplorerAPI( usingWebSockets, deployPath )
{
	/**
	 * True to use web sockets, false to use AJAX/REST
	 *
	 * @type {boolean}
	 */
	this.mUsingWebSockets = usingWebSockets;

	/**
	 * The URI to scenario explorer
	 *
	 * @type {string}
	 */
	this.mDeployPath = deployPath;

	/**
	 * Holds the available HTTP methods for AJAX/REST calls
	 *
	 * @const {{Get: string, Post: string, Put: string, Delete: string}}
	 */
	this.METHODS = {
		Get : 'GET',
		Post : 'POST',
		Put : 'PUT',
		Delete : 'DELETE'
	};

	this.JSON_KEYS = {
		// Action:        'action',
		// IsDebugging:   'd',
		// IncludeAction: 'includeAction',
		// Success:       'success',
		// ErrorMessage:  'errorMessage',
		// TimeStamp:     'timestamp',
		// RequestId : 'requestId',
		Username : 'userName',
		ProjectId : 'projectId',
		ProjectTemplateId : 'projectTemplateId',
		ViewId : 'viewId',
		ConditioningEventId : 'conditioningEventId',
		TimelineEventId : 'timelineEventId',
		FeatureId : 'featureId',
		Users : 'users',
		User : 'user',
		Projects : 'projects',
		Project : 'project',
		ProjectTemplates : 'projectTemplates',
		ProjectTemplate : 'projectTemplate',
		Views : 'views',
		View : 'view',
		TimelineEvents : 'timelineEvents',
		TimelineEvent : 'timelineEvent',
		Features : 'features',
		Feature : 'feature',
		ConditioningEvents : 'conditioningEvents',
		ConditioningEvent : 'conditioningEvent',
		States : 'states',
		State : 'state',
		HistoricalDatasets : 'HistoricalDatasets',
		HistoricalDataset : 'historicalDataset',
		FeatureTypes : 'featureTypes',
		FeatureTypeId : 'featureTypeId',
		Preconditions : 'preconditions',
		Precondition : 'preconditionId',
		OutcomeEffects : 'outcomeEffects',
		OutcomeEffect : 'outcomeEffectId',
		Projectors : 'projectors',
		Projector : 'projectorId',
		Tree : 'tree',
		Stats : 'stats',
		FromTemplateDetails : 'fromTemplateDetails'
	};

	/**
	 * Holds the available backend requests
	 *
	 * @private
	 * @const {Object.<string, ApiRequestConfig>}
	 */
	this.REQUESTS = {

		///// users /////

		ListUsers : {
			webSocketAction : 'list-users',
			ajaxUriTemplate : '/user',
			ajaxMethod : this.METHODS.Get
		},
		GetUser : {
			webSocketAction : 'get-user',
			ajaxUriTemplate : '/user/{userName}',
			ajaxMethod : this.METHODS.Get
		},
		NewUser : {
			webSocketAction : 'new-user',
			ajaxUriTemplate : '/user',
			ajaxMethod : this.METHODS.Post,
			payloadParameterName : this.JSON_KEYS.User
		},
		UpdateUser : {
			webSocketAction : 'update-user',
			ajaxUriTemplate : '/user/{userName}',
			ajaxMethod : this.METHODS.Put,
			payloadParameterName : this.JSON_KEYS.User
		},
		DeleteUser : {
			webSocketAction : 'delete-user',
			ajaxUriTemplate : '/user/{userName}',
			ajaxMethod : this.METHODS.Delete
		},

		///// project /////

		ListProjects : {
			webSocketAction : 'list-projects',
			ajaxUriTemplate : '/project',
			ajaxMethod : this.METHODS.Get
		},
		GetProject : {
			webSocketAction : 'get-project',
			ajaxUriTemplate : '/project/{projectId}',
			ajaxMethod : this.METHODS.Get
		},
		NewProject : {
			webSocketAction : 'new-project',
			ajaxUriTemplate : '/project',
			ajaxMethod : this.METHODS.Post,
			payloadParameterName : this.JSON_KEYS.Project
		},
		UpdateProject : {
			webSocketAction : 'update-project',
			ajaxUriTemplate : '/project/{projectId}',
			ajaxMethod : this.METHODS.Put,
			payloadParameterName : this.JSON_KEYS.Project
		},
		DeleteProject : {
			webSocketAction : 'delete-project',
			ajaxUriTemplate : '/project/{projectId}',
			ajaxMethod : this.METHODS.Delete
		},
		ExportProject : {
			webSocketAction : 'export-project',
			ajaxUriTemplate : '/project/{projectId}/export',
			ajaxMethod : this.METHODS.Get
		},
		ImportProject : {
			webSocketAction : 'import-project',
			ajaxUriTemplate : '/project/import',
			ajaxMethod : this.METHODS.Post
		},
		CloneProject : {
			webSocketAction : 'clone-project',
			ajaxUriTemplate : '/project/{projectId}/clone',
			ajaxMethod : this.METHODS.Post
		},
		// CreateProjectTemplate : {
		// 	webSocketAction : 'create-project-template',
		// 	ajaxUriTemplate : '/project/{projectId}/createTemplate',
		// 	ajaxMethod : this.METHODS.Post
		// },

		///// project template /////

		ListProjectTemplates : {
			webSocketAction : 'list-project-templates',
			ajaxUriTemplate : '/projectTemplate',
			ajaxMethod : this.METHODS.Get
		},
		GetProjectTemplate : {
			webSocketAction : 'get-project-template',
			ajaxUriTemplate : '/projectTemplate/{projectTemplateId}',
			ajaxMethod : this.METHODS.Get
		},
		NewProjectTemplate : {
			webSocketAction : 'new-project-template',
			ajaxUriTemplate : '/projectTemplate',
			ajaxMethod : this.METHODS.Post,
			payloadParameterName : this.JSON_KEYS.ProjectTemplate
		},
		UpdateProjectTemplate : {
			webSocketAction : 'update-project-template',
			ajaxUriTemplate : '/projectTemplate/{projectTemplateId}',
			ajaxMethod : this.METHODS.Put,
			payloadParameterName : this.JSON_KEYS.ProjectTemplate
		},
		DeleteProjectTemplate : {
			webSocketAction : 'delete-project-template',
			ajaxUriTemplate : '/projectTemplate/{projectTemplateId}',
			ajaxMethod : this.METHODS.Delete
		},
		NewProjectFromTemplate : {
			webSocketAction : 'new-project-from-template',
			ajaxUriTemplate : '/projectTemplate/{projectTemplateId}/fromTemplate',
			ajaxMethod : this.METHODS.Post
		},

		///// views /////

		ListViews : {
			webSocketAction : 'list-views',
			ajaxUriTemplate : '/project/{projectId}/view',
			ajaxMethod : this.METHODS.Get
		},
		GetView : {
			webSocketAction : 'get-view',
			ajaxUriTemplate : '/project/{projectId}/view/{viewId}',
			ajaxMethod : this.METHODS.Get
		},
		NewView : {
			webSocketAction : 'new-view',
			ajaxUriTemplate : '/project/{projectId}/view',
			ajaxMethod : this.METHODS.Post,
			payloadParameterName : this.JSON_KEYS.View
		},
		UpdateView : {
			webSocketAction : 'update-view',
			ajaxUriTemplate : '/project/{projectId}/view/{viewId}',
			ajaxMethod : this.METHODS.Put,
			payloadParameterName : this.JSON_KEYS.View
		},
		DeleteView : {
			webSocketAction : 'delete-view',
			ajaxUriTemplate : '/project/{projectId}/view/{viewId}',
			ajaxMethod : this.METHODS.Delete
		},
		GetViewTree : {
			webSocketAction : 'get-view-tree',
			ajaxUriTemplate : '/project/{projectId}/view/{viewId}/tree',
			ajaxMethod : this.METHODS.Get
		},
		GetViewStats : {
			webSocketAction : 'get-view-stats',
			ajaxUriTemplate : '/project/{projectId}/view/{viewId}/stats',
			ajaxMethod : this.METHODS.Get
		},

		///// timeline events /////

		ListTimelineEvents : {
			webSocketAction : 'list-timeline-events',
			ajaxUriTemplate : '/project/{projectId}/timelineEvent',
			ajaxMethod : this.METHODS.Get
		},
		GetTimelineEvent : {
			webSocketAction : 'get-timeline-event',
			ajaxUriTemplate : '/project/{projectId}/timelineEvent/{timelineEventId}',
			ajaxMethod : this.METHODS.Get
		},
		NewTimelineEvent : {
			webSocketAction : 'new-timeline-event',
			ajaxUriTemplate : '/project/{projectId}/timelineEvent',
			ajaxMethod : this.METHODS.Post,
			payloadParameterName : this.JSON_KEYS.TimelineEvent
		},
		UpdateTimelineEvent : {
			webSocketAction : 'update-timeline-event',
			ajaxUriTemplate : '/project/{projectId}/timelineEvent/{timelineEventId}',
			ajaxMethod : this.METHODS.Put,
			payloadParameterName : this.JSON_KEYS.TimelineEvent
		},
		DeleteTimelineEvent : {
			webSocketAction : 'delete-timeline-event',
			ajaxUriTemplate : '/project/{projectId}/timelineEvent/{timelineEventId}',
			ajaxMethod : this.METHODS.Delete
		},

		///// features /////

		ListFeatures : {
			webSocketAction : 'list-features',
			ajaxUriTemplate : '/project/{projectId}/feature',
			ajaxMethod : this.METHODS.Get
		},
		GetFeature : {
			webSocketAction : 'get-feature',
			ajaxUriTemplate : '/project/{projectId}/feature/{featureId}',
			ajaxMethod : this.METHODS.Get
		},
		NewFeature : {
			webSocketAction : 'new-feature',
			ajaxUriTemplate : '/project/{projectId}/feature',
			ajaxMethod : this.METHODS.Post,
			payloadParameterName : this.JSON_KEYS.Feature,
			isSynchronous : true
		},
		UpdateFeature : {
			webSocketAction : 'update-feature',
			ajaxUriTemplate : '/project/{projectId}/feature/{featureId}',
			ajaxMethod : this.METHODS.Put,
			payloadParameterName : this.JSON_KEYS.Feature,
			isSynchronous : true
		},
		DeleteFeature : {
			webSocketAction : 'delete-feature',
			ajaxUriTemplate : '/project/{projectId}/feature/{featureId}',
			ajaxMethod : this.METHODS.Delete,
			isSynchronous : true
		},

		///// conditioning events /////

		ListConditioningEvents : {
			webSocketAction : 'list-conditioning-events',
			ajaxUriTemplate : '/project/{projectId}/conditioningEvent',
			ajaxMethod : this.METHODS.Get
		},
		GetConditioningEvent : {
			webSocketAction : 'get-conditioning-event',
			ajaxUriTemplate : '/project/{projectId}/conditioningEvent/{conditioningEventId}',
			ajaxMethod : this.METHODS.Get
		},
		NewConditioningEvent : {
			webSocketAction : 'new-conditioning-event',
			ajaxUriTemplate : '/project/{projectId}/conditioningEvent',
			ajaxMethod : this.METHODS.Post,
			payloadParameterName : this.JSON_KEYS.ConditioningEvent,
			isSynchronous : true
		},
		UpdateConditioningEvent : {
			webSocketAction : 'update-conditioning-event',
			ajaxUriTemplate : '/project/{projectId}/conditioningEvent/{conditioningEventId}',
			ajaxMethod : this.METHODS.Put,
			payloadParameterName : this.JSON_KEYS.ConditioningEvent,
			isSynchronous : true
		},
		DeleteConditioningEvent : {
			webSocketAction : 'delete-conditioning-event',
			ajaxUriTemplate : '/project/{projectId}/view/{viewId}/conditioningEvent/{conditioningEventId}',
			ajaxMethod : this.METHODS.Delete,
			isSynchronous : true
		},
		AssignConditioningEvent : {
			webSocketAction : 'assign-conditioning-event',
			ajaxUriTemplate : '/project/{projectId}/view/{viewId}/conditioningEvent/{conditioningEventId}',
			ajaxMethod : this.METHODS.Put
		},

		///// states /////

		ListStates : {
			webSocketAction : 'list-states',
			ajaxUriTemplate : '/project/{projectId}/view/{viewId}/state',
			ajaxMethod : this.METHODS.Get
		},
		// GetState : { webSocketAction : 'get-state', ajaxUriTemplate : '/project/{projectId}/view/{viewId}/state/{stateId}', ajaxMethod : this.METHODS.Get },
		// UpdateState : { webSocketAction : 'update-state', ajaxUriTemplate : '/project/{projectId}/view/{viewId}/state/{stateId}', ajaxMethod : this.METHODS.Post },

		///// project templates /////

		///// historical datasets /////

		// ListHistoricalDatasets:  { webSocketAction: 'list-historical-datasets', ajaxUriTemplate: '/project/{projectId}/historicalDatasets', ajaxMethod: this.METHODS.Get },
		// GetHistoricalDataset:    { webSocketAction: 'get-historical-dataset', ajaxUriTemplate: '', ajaxMethod: this.METHODS.Get },
		// NewHistoricalDataset:    { webSocketAction: 'new-historical-dataset', ajaxUriTemplate: '', ajaxMethod: this.METHODS.Put },
		// UpdateHistoricalDataset: { webSocketAction: 'update-historical-dataset', ajaxUriTemplate: '', ajaxMethod: this.METHODS.Post },
		// DeleteHistoricalDataset: { webSocketAction: 'delete-historical-dataset', ajaxUriTemplate: '', ajaxMethod: this.METHODS.Delete },

		///// plugin: feature types /////

		ListFeatureTypes : {
			webSocketAction : 'list-feature-types',
			ajaxUriTemplate : '/featureType',
			ajaxMethod : this.METHODS.Get
		},
		GetFeatureType : {
			webSocketAction : 'get-feature-type',
			ajaxUriTemplate : '/featureTypes/{featureTypeId}',
			ajaxMethod : this.METHODS.Get
		},
		GetFeatureTypeSource : {
			webSocketAction : 'get-feature-type-src',
			ajaxUriTemplate : '/featureType/{featureTypeId}/src',
			ajaxMethod : this.METHODS.Get,
			isSourceRequest : true
		},

		///// plugin: preconditions /////

		ListPreconditions : {
			webSocketAction : 'list-preconditions',
			ajaxUriTemplate : '/precondition',
			ajaxMethod : this.METHODS.Get
		},
		GetPrecondition : {
			webSocketAction : 'get-precondition',
			ajaxUriTemplate : '/precondition/{preconditionId}',
			ajaxMethod : this.METHODS.Get
		},
		GetPreconditionSource : {
			webSocketAction : 'get-precondition-src',
			ajaxUriTemplate : '/precondition/{preconditionId}/src',
			ajaxMethod : this.METHODS.Get,
			isSourceRequest : true
		},

		///// plugin: outcome effects /////

		ListOutcomeEffects : {
			webSocketAction : 'list-outcomes-effects',
			ajaxUriTemplate : '/outcomeEffect',
			ajaxMethod : this.METHODS.Get
		},
		GetOutcomeEffect : {
			webSocketAction : 'get-outcomes-effect',
			ajaxUriTemplate : '/outcomeEffect/{outcomeEffectId}',
			ajaxMethod : this.METHODS.Get
		},
		GetOutcomeEffectSource : {
			webSocketAction : 'get-outcome-effect-src',
			ajaxUriTemplate : '/outcomeEffect/{outcomeEffectId}/src',
			ajaxMethod : this.METHODS.Get,
			isSourceRequest : true
		},

		///// plugin: projectors /////

		ListProjectors : {
			webSocketAction : 'list-projectors',
			ajaxUriTemplate : '/projector',
			ajaxMethod : this.METHODS.Get
		},
		GetProjector : {
			webSocketAction : 'get-projector',
			ajaxUriTemplate : '/projector/{projectorId}',
			ajaxMethod : this.METHODS.Get
		},
		GetProjectorSource : {
			webSocketAction : 'get-projector-src',
			ajaxUriTemplate : '/projector/{projectorId}/src',
			ajaxMethod : this.METHODS.Get,
			isSourceRequest : true
		},

		///// dashboard /////

		ListDashboard : {
			webSocketAction : 'list-access-and-views',
			ajaxUriTemplate : '/dashboard',
			ajaxMethod : this.METHODS.Get
		}
	};

	/**
	 * Holds the URI to the API
	 * @type {string}
	 */
	this.AJAX_SERVER_URI_PATH = '/api'; // TODO this should be filled in during the build somehow

	/**
	 * @private
	 * @type {Object.<string, ApiRequest>}
	 */
	this.mRequestTracking = {};

	/**
	 * @private
	 * @type {number}
	 */
	this.mNextRequestUniqueId = 0;

	/**
	 * @private
	 * @type {?Array< SynchronousRequestQueueEntry >}
	 */
	this.mSynchronousRequestQueue = null;

	return
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets a list of the users
 *
 * @param {function(Array< User >)} callback
 */
ScenarioExplorerAPI.prototype.listUsers = function( callback )
{
	this.runRequest(
		this.REQUESTS.ListUsers,
		null,
		this.handleApiResponseArray.bind( this, User.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the given user
 *
 * @param {string} userName
 * @param {function(User)} callback
 */
ScenarioExplorerAPI.prototype.getUser = function( userName, callback )
{
	this.runRequest(
		this.REQUESTS.GetUser,
		{ 'userName' : userName },
		this.handleApiResponseSingleObject.bind( this, User.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// newUser

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the given user
 *
 * @param {User} user
 * @param {function(User)} callback
 */
ScenarioExplorerAPI.prototype.updateUser = function( user, callback )
{
	this.runRequest(
		this.REQUESTS.UpdateUser,
		{ 'userName' : user.userName, 'user' : user },
		this.handleApiResponseSingleObject.bind( this, User.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// deleteUser

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets all projects the user has access to
 *
 * @param {function(Array< Project >)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.listProjects = function( callback )
{
	this.runRequest(
		this.REQUESTS.ListProjects,
		null,
		this.handleApiResponseArray.bind( this, Project.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Get the project with the given id
 *
 * @param {string} projectId - the project id
 * @param {function(Project) } callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.getProject = function( projectId, callback )
{
	this.runRequest(
		this.REQUESTS.GetProject,
		{ 'projectId' : projectId },
		this.handleApiResponseSingleObject.bind( this, Project.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Creates a new project with the given setup
 *
 * @param {Project} project
 * @param {function(Project)} callback
 */
ScenarioExplorerAPI.prototype.newProject = function( project, callback )
{
	this.runRequest(
		this.REQUESTS.NewProject,
		{ 'project' : project },
		this.handleApiResponseSingleObject.bind( this, Project.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the given project
 *
 * @param {Project} project
 * @param {function(Project)} callback
 */
ScenarioExplorerAPI.prototype.updateProject = function( project, callback )
{
	this.runRequest(
		this.REQUESTS.UpdateProject,
		{ 'projectId' : project.id, 'project' : project },
		this.handleApiResponseSingleObject.bind( this, Project.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Deletes the given project
 *
 * @param {string} projectId
 * @param {function(string)} callback
 */
ScenarioExplorerAPI.prototype.deleteProject = function( projectId, callback )
{
	this.runRequest(
		this.REQUESTS.DeleteProject,
		{ 'projectId' : projectId },
		this.handleApiResponseSingleObject.bind( this, Project.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Imports the given project
 *
 * @param {object} backup
 * @param {function(string)} callback
 */
ScenarioExplorerAPI.prototype.importProject = function( backup, callback )
{
	this.runRequest(
		this.REQUESTS.ImportProject,
		backup,
		this.handleApiResponseSingleObject.bind( this, Project.bind( this ) ),
		callback
	);
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Exports the given project (not actually an API call, just initiates the browser to download the file)
 *
 * @param {string} projectId - the project id
 */
ScenarioExplorerAPI.prototype.exportProject = function( projectId )
{
	window.location = this.mDeployPath + this.AJAX_SERVER_URI_PATH + '/project/' + projectId + '/export';

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Clones the given project
 *
 * @param {string} projectId
 * @param {function(string)} callback
 */
ScenarioExplorerAPI.prototype.cloneProject = function( projectId, callback )
{
	this.runRequest(
		this.REQUESTS.CloneProject,
		{ 'projectId' : projectId },
		this.handleApiResponseSingleObject.bind( this, Project.bind( this ) ),
		callback
	);
	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets all project templates
 *
 * @param {function(Array< ProjectTemplate >)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.listProjectTemplates = function( callback )
{
	this.runRequest(
		this.REQUESTS.ListProjectTemplates,
		null,
		this.handleApiResponseArray.bind( this, ProjectTemplate.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Get the project template with the given id
 *
 * @param {string} projectTemplateId - the project template id
 * @param {function(ProjectTemplate) } callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.getProjectTemplate = function( projectTemplateId, callback )
{
	this.runRequest(
		this.REQUESTS.GetProjectTemplate,
		{ 'projectTemplateId' : projectTemplateId },
		this.handleApiResponseSingleObject.bind( this, ProjectTemplate.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Creates a new project template with the given setup
 *
 * @param {ProjectTemplate} projectTemplate
 * @param {function(ProjectTemplate)} callback
 */
ScenarioExplorerAPI.prototype.newProjectTemplate = function( projectTemplate, callback )
{
	this.runRequest(
		this.REQUESTS.NewProjectTemplate,
		{ 'projectTemplate' : projectTemplate },
		this.handleApiResponseSingleObject.bind( this, ProjectTemplate.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the given project template
 *
 * @param {ProjectTemplate} projectTemplate
 * @param {function(ProjectTemplate)} callback
 */
ScenarioExplorerAPI.prototype.updateProjectTemplate = function( projectTemplate, callback )
{
	this.runRequest(
		this.REQUESTS.UpdateProjectTemplate,
		{ 'projectTemplateId' : projectTemplate.id, 'projectTemplate' : projectTemplate },
		this.handleApiResponseSingleObject.bind( this, ProjectTemplate.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Deletes the given project template
 *
 * @param {string} projectTemplateId the project id
 * @param {function(string)} callback
 */
ScenarioExplorerAPI.prototype.deleteProjectTemplate = function( projectTemplateId, callback )
{
	this.runRequest(
		this.REQUESTS.DeleteProjectTemplate,
		{ 'projectTemplateId' : projectTemplateId },
		this.handleApiResponseSingleObject.bind( this, ProjectTemplate.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Creates a project based on the given project template
 *
 * @param {string} projectTemplateId
 * @param {string} projectName
 * @param {string} projectDescription
 * @param {function(Project)} callback
 */
ScenarioExplorerAPI.prototype.newProjectFromTemplate = function( projectTemplateId, projectName, projectDescription, callback )
{
	this.runRequest(
		this.REQUESTS.NewProjectFromTemplate,
		{ 'projectTemplateId' : projectTemplateId, 'name' : projectName, 'description' : projectDescription },
		this.handleApiResponseSingleObject.bind( this, Project.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets all views for the given access
 *
 * @param {string} projectId - the project id
 * @param {function(Array< View >)} callback
 */
ScenarioExplorerAPI.prototype.listViews = function( projectId, callback )
{
	this.runRequest(
		this.REQUESTS.ListViews,
		{ 'projectId' : projectId },
		this.handleApiResponseArray.bind( this, View.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the given view
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {function(View)} callback
 */
ScenarioExplorerAPI.prototype.getView = function( projectId, viewId, callback )
{
	this.runRequest(
		this.REQUESTS.GetView,
		{ 'projectId' : projectId, 'viewId' : viewId },
		this.handleApiResponseSingleObject.bind( this, View.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Creates a new view
 *
 * @param {string} projectId - the project id
 * @param {View} view - the view to create
 * @param {function(View)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.newView = function( projectId, view, callback )
{
	this.runRequest(
		this.REQUESTS.NewView,
		{ 'projectId' : projectId, 'view' : view },
		this.handleApiResponseSingleObject.bind( this, View.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the tree for the given view
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {function(View)} callback
 */
ScenarioExplorerAPI.prototype.getViewTree = function( projectId, viewId, callback )
{
	this.runRequest(
		this.REQUESTS.GetViewTree,
		{ 'projectId' : projectId, 'viewId' : viewId },
		this.handleApiResponseSingleObject.bind( this, ViewTree.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the tree for the given view
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {function(View)} callback
 */
ScenarioExplorerAPI.prototype.getViewStats = function( projectId, viewId, callback )
{
	this.runRequest(
		this.REQUESTS.GetViewStats,
		{ 'projectId' : projectId, 'viewId' : viewId },
		this.handleApiResponseSingleObject.bind( this, Object.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the given view
 *
 * @param {string} projectId - the project id
 * @param {View} view - the view to update
 * @param {function(View)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.updateView = function( projectId, view, callback )
{
	this.runRequest(
		this.REQUESTS.UpdateView,
		{ 'projectId' : projectId, 'viewId' : view.id, 'view' : view },
		this.handleApiResponseSingleObject.bind( this, View.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Deletes the given view
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id to delete
 * @param {function(string)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.deleteView = function( projectId, viewId, callback )
{
	this.runRequest(
		this.REQUESTS.DeleteView,
		{ 'projectId' : projectId, 'viewId' : viewId },
		this.handleApiResponseSingleObject.bind( this, View.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets all timeline events for the given project
 *
 * @param {string} projectId - the project id
 * @param {?function(Array< TimelineEvent >)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.listTimelineEvents = function( projectId, callback )
{
	this.runRequest(
		this.REQUESTS.ListTimelineEvents,
		{ 'projectId' : projectId },
		this.handleApiResponseArray.bind( this, TimelineEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the given timeline event
 *
 * @param {string} projectId - the project id
 * @param {string} timelineEventId - the timeline event id
 * @param {?function(TimelineEvent)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.getTimelineEvent = function( projectId, timelineEventId, callback )
{
	this.runRequest(
		this.REQUESTS.GetTimelineEvent,
		{ 'projectId' : projectId, 'timelineEventId' : timelineEventId },
		this.handleApiResponseSingleObject.bind( this, TimelineEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Creates a new timeline event
 *
 * @param {string} projectId - the project id
 * @param {TimelineEvent} timelineEvent - the timeline event
 * @param {?function(TimelineEvent)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.newTimelineEvent = function( projectId, timelineEvent, callback )
{
	this.runRequest(
		this.REQUESTS.NewTimelineEvent,
		{ 'projectId' : projectId, 'timelineEvent' : timelineEvent },
		this.handleApiResponseSingleObject.bind( this, TimelineEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the given timeline event
 *
 * @param {string} projectId - the project id
 * @param {TimelineEvent} timelineEvent - the timeline event
 * @param {function(TimelineEvent)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.updateTimelineEvent = function( projectId, timelineEvent, callback )
{
	this.runRequest(
		this.REQUESTS.UpdateTimelineEvent,
		{ 'projectId' : projectId, 'timelineEventId' : timelineEvent.id, 'timelineEvent' : timelineEvent },
		this.handleApiResponseSingleObject.bind( this, TimelineEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

ScenarioExplorerAPI.prototype.deleteTimelineEvent = function( projectId, timelineEventId, callback )
{
	this.runRequest(
		this.REQUESTS.DeleteTimelineEvent,
		{ 'projectId' : projectId, 'timelineEventId' : timelineEventId },
		this.handleApiResponseSingleObject.bind( this, TimelineEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the features for the given project
 *
 * @param {string} projectId - the project id
 * @param {?function(Array< Feature >)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.listFeatures = function( projectId, callback )
{
	this.runRequest(
		this.REQUESTS.ListFeatures,
		{ 'projectId' : projectId },
		this.handleApiResponseArray.bind( this, Feature.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} projectId - the project id
 * @param {string} featureId - the feature id
 * @param {function(Feature)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.getFeature = function( projectId, featureId, callback )
{
	this.runRequest(
		this.REQUESTS.GetFeature,
		{ 'projectId' : projectId, 'featureId' : featureId },
		this.handleApiResponseSingleObject.bind( this, Feature.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} projectId - the project id
 * @param {Feature} feature - the feature
 * @param {function(Feature)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.newFeature = function( projectId, feature, callback )
{
	this.runRequest(
		this.REQUESTS.NewFeature,
		{ 'projectId' : projectId, 'feature' : feature },
		this.handleApiResponseSingleObject.bind( this, Feature.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} projectId - the project id
 * @param {Feature} feature - the feature
 * @param {function(Feature)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.updateFeature = function( projectId, feature, callback )
{
	this.runRequest(
		this.REQUESTS.UpdateFeature,
		{ 'projectId' : projectId, 'feature' : feature },
		this.handleApiResponseSingleObject.bind( this, Feature.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Deletes the given feature
 *
 * @param {string} projectId - the project id
 * @param {string} featureId - the feature id
 * @param {function(string)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.deleteFeature = function( projectId, featureId, callback )
{
	this.runRequest(
		this.REQUESTS.DeleteFeature,
		{ 'projectId' : projectId, 'featureId' : featureId },
		this.handleApiResponseSingleObject.bind( this, Feature.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the list of conditioning events for the given project
 *
 * @param {string} projectId - the project id
 * @param {function(Array< ConditioningEvent >)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.listConditioningEvents = function( projectId, callback )
{
	this.runRequest(
		this.REQUESTS.ListConditioningEvents,
		{ 'projectId' : projectId },
		this.handleApiResponseArray.bind( this, ConditioningEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the given conditioning event
 *
 * @param {string} projectId - the project id
 * @param {string} conditioningEventId - the conditioning event id
 * @param {function(ConditioningEvent)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.getConditioningEvent = function( projectId, conditioningEventId, callback )
{
	this.runRequest(
		this.REQUESTS.GetConditioningEvent,
		{ 'projectId' : projectId, 'conditioningEventId' : conditioningEventId },
		this.handleApiResponseSingleObject.bind( this, ConditioningEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Creates the given conditioning event
 *
 * @param {string} projectId - the project id
 * @param {ConditioningEvent} conditioningEvent - the conditioning event to create
 * @param {function(ConditioningEvent)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.newConditioningEvent = function( projectId, conditioningEvent, callback )
{
	this.runRequest(
		this.REQUESTS.NewConditioningEvent,
		{ 'projectId' : projectId, 'conditioningEvent' : conditioningEvent },
		this.handleApiResponseSingleObject.bind( this, ConditioningEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Updates the given conditioning event
 *
 * @param {string} projectId - the project id
 * @param {ConditioningEvent} conditioningEvent - the conditioning event to update
 * @param {function(ConditioningEvent)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.updateConditioningEvent = function( projectId, conditioningEvent, callback )
{
	this.runRequest(
		this.REQUESTS.UpdateConditioningEvent,
		{ 'projectId' : projectId, 'conditioningEventId' : conditioningEvent.id, 'conditioningEvent' : conditioningEvent },
		this.handleApiResponseSingleObject.bind( this, ConditioningEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Creates the given conditioning event
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {string} conditioningEventId - the conditioning event id to delete
 * @param {function(ConditioningEvent)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.deleteConditioningEvent = function( projectId, viewId, conditioningEventId, callback )
{
	this.runRequest(
		this.REQUESTS.DeleteConditioningEvent,
		{ 'projectId' : projectId, 'viewId' : viewId, 'conditioningEventId' : conditioningEventId },
		this.handleApiResponseSingleObject.bind( this, ConditioningEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Assigns the given conditioning event to the view
 *
 * @param {string} projectId - the project id
 * @param {string} viewId - the view id
 * @param {string} conditioningEventId - the conditioning event id to delete
 * @param {function(ConditioningEvent)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.assignConditioningEvent = function( projectId, viewId, conditioningEventId, callback )
{
	this.runRequest(
		this.REQUESTS.AssignConditioningEvent,
		{ 'projectId' : projectId, 'viewId' : viewId, 'conditioningEventId' : conditioningEventId },
		this.handleApiResponseSingleObject.bind( this, ConditioningEvent.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

ScenarioExplorerAPI.prototype.listStates = function( projectId, viewId, callback )
{
	this.runRequest(
		this.REQUESTS.ListStates,
		{ 'projectId' : projectId, 'viewId' : viewId },
		this.handleApiResponseArray.bind( this, State.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// ScenarioExplorerAPI.prototype.getState = function( projectId, viewId, callback )

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// ScenarioExplorerAPI.prototype.updatesState = function( projectId, viewId, callback )

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// listHistoricalDatasets
// getHistoricalDataset
// newHistoricalDataset
// updateHistoricalDataset
// deleteHistoricalDataset

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the available feature types
 *
 * @param {?function(Array< FeatureTypePlugin >)} callback
 */
ScenarioExplorerAPI.prototype.listFeatureTypes = function( callback )
{
	this.runRequest(
		this.REQUESTS.ListFeatureTypes,
		{},
		this.handleApiResponseArray.bind( this, FeatureTypePlugin.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the available preconditions
 *
 * @param {?function(Array< PreconditionPlugin >)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.listPreconditions = function( callback )
{
	this.runRequest(
		this.REQUESTS.ListPreconditions,
		{},
		this.handleApiResponseArray.bind( this, PreconditionPlugin.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the available outcome effects
 *
 * @param {?function(Array< OutcomeEffectPlugin >)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.listOutcomeEffects = function( callback )
{
	this.runRequest(
		this.REQUESTS.ListOutcomeEffects,
		{},
		this.handleApiResponseArray.bind( this, OutcomeEffectPlugin.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the available projectors
 *
 * @param {?function(Array< ProjectorPlugin >)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.listProjectors = function( callback )
{
	this.runRequest(
		this.REQUESTS.ListProjectors,
		{},
		this.handleApiResponseArray.bind( this, ProjectorPlugin.bind( this ) ),
		callback
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the given feature type plugin
 *
 * @param {string} id
 * @param {function(string, ScenarioExplorerPluginSource)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.getFeatureTypePlugin = function( id, callback )
{
	this.runRequest(
		this.REQUESTS.GetFeatureTypeSource,
		{ 'featureTypeId' : id },
		this.getPluginSourceCallback.bind( this, id ),
		callback
	);
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the given precondition plugin
 *
 * @param {string} id
 * @param {function(string, ScenarioExplorerPluginSource)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.getPreconditionPlugin = function( id, callback )
{
	this.runRequest(
		this.REQUESTS.GetPreconditionSource,
		{ 'preconditionId' : id },
		this.getPluginSourceCallback.bind( this, id ),
		callback
	);
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the given outcome effect plugin
 *
 * @param {string} id
 * @param {function(string, ScenarioExplorerPluginSource)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.getOutcomeEffectPlugin = function( id, callback )
{
	this.runRequest(
		this.REQUESTS.GetOutcomeEffectSource,
		{ 'outcomeEffectId' : id },
		this.getPluginSourceCallback.bind( this, id ),
		callback
	);
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the given projector plugin
 *
 * @param {string} id
 * @param {function(string, ScenarioExplorerPluginSource)} callback - the callback function to run
 */
ScenarioExplorerAPI.prototype.getProjectorPlugin = function( id, callback )
{
	this.runRequest(
		this.REQUESTS.GetProjectorSource,
		{ 'projectorId' : id },
		this.getPluginSourceCallback.bind( this, id ),
		callback
	);
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Gets the lists of access and views for the current user
 *
 * @param {function(Array< Project >, Array< View >)} callback
 */
ScenarioExplorerAPI.prototype.listDashboard = function( callback )
{
	this.runRequest(
		this.REQUESTS.ListDashboard,
		{},
		this.listDashboardCallback.bind( this ),
		callback
	);

	return;
};

/**
 * @param {ApiRequest} apiRequest
 * @param {Object} apiResponse
 * @private
 */
ScenarioExplorerAPI.prototype.listDashboardCallback = function( apiRequest, apiResponse )
{
	if( !( this.JSON_KEYS.Projects in apiResponse ) )
	{
		console.warn( 'Could not find %s in API response: %O', this.JSON_KEYS.Projects, apiResponse );
		return;
	}
	if( !( this.JSON_KEYS.Views in apiResponse ) )
	{
		console.warn( 'Could not find %s in API response: %O', this.JSON_KEYS.Views, apiResponse );
		return;
	}

	const rawProjects = /** @type {Array< SerializedProject >} */( apiResponse[ this.JSON_KEYS.Projects ] );
	const rawViews = /** @type {Array< SerializedView >} */( apiResponse[ this.JSON_KEYS.Views ] );

	const projects = [];
	for( let i = 0; i < rawProjects.length; ++i )
	{
		projects.push( new Project( rawProjects[ i ] ) );
	}

	const views = [];
	for( let i = 0; i < rawViews.length; ++i )
	{
		views.push( new View( rawViews[ i ] ) );
	}

	// run the user callbacks
	for( let i = 0; i < apiRequest.userCallbacks.length; ++i )
	{
		apiRequest.userCallbacks[ i ]( projects, views );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {ApiRequestConfig} apiRequestConfig
 * @param {Object.<string, *>} parameters
 * @param {function(ApiRequest, Object)} apiHandlerCallback
 * @param {function} userCallback
 */
ScenarioExplorerAPI.prototype.runRequest = function( apiRequestConfig, parameters, apiHandlerCallback, userCallback )
{
	if( this.mSynchronousRequestQueue !== null )
	{
		// we are in synchronous mode, so just add an entry onto the queue

		this.mSynchronousRequestQueue.push( /** @type {SynchronousRequestQueueEntry} */( {
			'apiRequestConfig' : apiRequestConfig,
			'parameters' : parameters,
			'apiHandlerCallback' : apiHandlerCallback,
			'userCallback' : userCallback
		} ) );
	}
	else
	{
		// not in synchronous mode, so just run the command

		if( apiRequestConfig.isSynchronous )
		{
			// this call *is* synchronous though, so initialize the queue for following entries
			this.mSynchronousRequestQueue = [];
		}

		this.runRequestHelper( apiRequestConfig, parameters, apiHandlerCallback, userCallback );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {ApiRequestConfig} apiRequestConfig
 * @param {Object.<string, *>} parameters
 * @param {function(ApiRequest, Object)} apiHandlerCallback
 * @param {function} userCallback
 */
ScenarioExplorerAPI.prototype.runRequestHelper = function( apiRequestConfig, parameters, apiHandlerCallback, userCallback )
{
	// we need to make a new request, so update the id to assign to it
	++this.mNextRequestUniqueId;

	if( this.mUsingWebSockets )
	{
		this.runRequestWebSockets( this.mNextRequestUniqueId, apiRequestConfig, parameters, apiHandlerCallback, userCallback );
	}
	else
	{
		this.runRequestAjax( this.mNextRequestUniqueId, apiRequestConfig, parameters, apiHandlerCallback, userCallback );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {number} requestId
 * @param {ApiRequestConfig} apiRequestConfig
 * @param {Object.<string, *>} parameters
 * @param {function(ApiRequest, Object)} apiHandlerCallback
 * @param {function} userCallback
 */
ScenarioExplorerAPI.prototype.runRequestAjax = function( requestId, apiRequestConfig, parameters, apiHandlerCallback, userCallback )
{
	// first take the URI template and fill in the appropriate parameters, and keep the list of other parameters to send in the API call
	let ajaxUri = apiRequestConfig.ajaxUriTemplate;

	const apiRequestParameters = {};
	for( let key in parameters )
	{
		if( parameters.hasOwnProperty( key ) )
		{
			const templateKey = '{' + key + '}';
			const templateKeyLocation = ajaxUri.indexOf( templateKey );
			if( templateKeyLocation > -1 )
			{
				if( !( key in parameters ) )
				{
					console.log( 'API parameter[%s] missing from: %s', key, apiRequestConfig.ajaxUriTemplate );
					g_util.showError( 'URI parameter missing: ' + key );
				}

				ajaxUri = ajaxUri.substr( 0, templateKeyLocation )
					+ parameters[ key ].toString()
					+ ajaxUri.substr( templateKeyLocation + templateKey.length );
			}
			else
			{
				apiRequestParameters[ key ] = parameters[ key ];
			}
		}
	}

	// make sure we filled them all in, otherwise show a dev warning
	if( ajaxUri.indexOf( '{' ) > -1 )
	{
		console.warn( 'Unable to escape all parameters: %s', ajaxUri );
		console.warn( 'Source: %s with parameters %O', apiRequestConfig.ajaxUriTemplate, parameters );
	}

	// now check if there is already a request for this outstanding...
	for( let existingRequestKey in this.mRequestTracking )
	{
		if(
			this.mRequestTracking.hasOwnProperty( existingRequestKey )
			&& this.mRequestTracking[ existingRequestKey ].ajaxMethod === this.METHODS.Get
			&& this.mRequestTracking[ existingRequestKey ].ajaxUri === ajaxUri )
		{
			if( userCallback )
			{
				this.mRequestTracking[ existingRequestKey ].userCallbacks.push( userCallback );
			}

			return;
		}
	}

	let payload = null;
	if( apiRequestConfig.payloadParameterName )
	{
		if( apiRequestConfig.payloadParameterName in parameters )
		{
			payload = parameters[ apiRequestConfig.payloadParameterName ];
		}
		else
		{
			console.error( 'Payload parameter missing: %s in %o', apiRequestConfig.payloadParameterName, parameters );
			payload = apiRequestParameters;
		}
	}
	else
	{
		payload = apiRequestParameters;
	}

	// initialize the request tracking entry for this request
	const requestKey = 'req_' + requestId;
	this.mRequestTracking[ requestKey ] = {
		'webSocketAction' : apiRequestConfig.webSocketAction,
		'ajaxUri' : ajaxUri,
		'ajaxMethod' : apiRequestConfig.ajaxMethod,
		'apiParameters' : payload,
		'apiHandlerCallback' : apiHandlerCallback,
		'userCallbacks' : []
	};

	if( userCallback )
	{
		this.mRequestTracking[ requestKey ].userCallbacks.push( userCallback );
	}

	const ajaxData = apiRequestConfig.ajaxMethod === this.METHODS.Get
		? apiRequestParameters
		: JSON.stringify( payload, this.customJSONSerializer.bind( this ) );

	console.log( 'sending: %s %s %O', apiRequestConfig.ajaxMethod.toString(), ajaxUri, ajaxData );

	jQuery.ajax(
		{
			'url' : this.mDeployPath + this.AJAX_SERVER_URI_PATH + this.mRequestTracking[ requestKey ].ajaxUri,
			'method' : apiRequestConfig.ajaxMethod,
			'data' : ajaxData,
			'success' : this.onSendMessageSuccess.bind( this ),
			'error' : this.onSendMessageError.bind( this ),
			'cache' : false,
			'dataType' : apiRequestConfig.isSourceRequest ? 'text' : 'json',

			'requestId' : requestId,
			'beforeSend' : function( jqXHR, settings )
			{
				// copy the requestId variable into the jqXHR so we have it in the result
				jqXHR.requestId = settings.requestId;

				return;
			}
		}
	);

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {number} requestId
 * @param {ApiRequestConfig} apiRequestConfig
 * @param {Object.<string, *>} parameters
 * @param {function(ApiRequest, Object)} apiHandlerCallback
 * @param {function} userCallback
 */
ScenarioExplorerAPI.prototype.runRequestWebSockets = function( requestId, apiRequestConfig, parameters, apiHandlerCallback, userCallback )
{
	console.warn( 'web sockets not implemented!' ); // TODO finish

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 */
ScenarioExplorerAPI.prototype.runNextSynchronousQueueEntry = function()
{
	if( this.mSynchronousRequestQueue === null )
	{
		return;
	}
	else if( this.mSynchronousRequestQueue.length === 0 )
	{
		this.mSynchronousRequestQueue = null;
		return;
	}

	const nextEntry = this.mSynchronousRequestQueue.shift();

	this.runRequestHelper( nextEntry.apiRequestConfig, nextEntry.parameters, nextEntry.apiHandlerCallback, nextEntry.userCallback );

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param response
 * @param responseRaw
 * @param jqXHR
 */
ScenarioExplorerAPI.prototype.onSendMessageSuccess = function( response, responseRaw, jqXHR )
{
	// console.log( 'AJAX response: %O', response );

	const requestTrackingKey = 'req_' + jqXHR.requestId;
	if( requestTrackingKey in this.mRequestTracking )
	{
		const apiRequest = this.mRequestTracking[ requestTrackingKey ];

		apiRequest.apiHandlerCallback( apiRequest, response );

		delete this.mRequestTracking[ requestTrackingKey ];
	}
	else
	{
		console.warn( 'Could not find request tracking entry for: %O = %O', jqXHR.requestId, response );
	}

	this.runNextSynchronousQueueEntry();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @private
 * @param {{responseJSON: {errorMessage: string, stackTrace: string, isUserError: boolean}}} response
 * @param {string} responseRaw
 */
ScenarioExplorerAPI.prototype.onSendMessageError = function( response, responseRaw )
{
	console.error( response );
	console.error( responseRaw );

	if( !( 'responseJSON' in response ) )
	{
		g_util.showError( 'Error running API command!' );
		return;
	}

	console.error( 'message: %s', response.responseJSON.errorMessage );
	console.error( 'stack trace: %s', response.responseJSON.stackTrace );

	g_util.showException( response.responseJSON.errorMessage.toString(), response.responseJSON.stackTrace.toString() );

	this.runNextSynchronousQueueEntry();

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Handles the API response to parse single object and calling the user callbacks
 *
 * @param {function(Object)} sourceConstructor
 * @param {ApiRequest} apiRequest
 * @param {Object} apiResponse
 */
ScenarioExplorerAPI.prototype.handleApiResponseSingleObject = function( sourceConstructor, apiRequest, apiResponse )
{
	const parsedObject = new sourceConstructor( apiResponse );

	// run the user callbacks
	for( let i = 0; i < apiRequest.userCallbacks.length; ++i )
	{
		apiRequest.userCallbacks[ i ]( parsedObject );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Handles the API response to parse an array of items and calling the user callbacks
 *
 * @param {function(Object)} sourceConstructor
 * @param {ApiRequest} apiRequest
 * @param {Array<Object>} apiResponse
 */
ScenarioExplorerAPI.prototype.handleApiResponseArray = function( sourceConstructor, apiRequest, apiResponse )
{
	const parsedArray = [];
	for( let i = 0; i < apiResponse.length; ++i )
	{
		parsedArray.push( new sourceConstructor( apiResponse[ i ] ) );
	}

	// run the user callbacks
	for( let i = 0; i < apiRequest.userCallbacks.length; ++i )
	{
		apiRequest.userCallbacks[ i ]( parsedArray );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @param {string} pluginId
 * @param {ApiRequest} apiRequest
 * @param {string} apiResponse
 * @private
 */
ScenarioExplorerAPI.prototype.getPluginSourceCallback = function( pluginId, apiRequest, apiResponse )
{
	const plugin = this.evalPluginSource( pluginId, apiResponse );

	// run the user callbacks
	for( let i = 0; i < apiRequest.userCallbacks.length; ++i )
	{
		apiRequest.userCallbacks[ i ]( pluginId, plugin );
	}

	return;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * Initializes the web socket connection to the backend
//  * @private
//  */
// ScenarioExplorerAPI.prototype.initWebSocket = function()
// {
// 	var protocol = window.location.href.toString().substr( 0, 5 ) === 'https'
// 		? 'wss://'
// 		: 'ws://';
//
// 	// launch the web socket
// 	this.m_backend = new WebSocket( protocol + window.location.host + this.m_deployLocation + this.WEB_SOCKET_SERVER_URI_PATH );
// 	this.m_backend.onopen = this.webSocketConnected.bind( this );
// 	this.m_backend.onmessage = this.webSocketMessage.bind( this );
// 	this.m_backend.onclose = this.webSocketDisconnected.bind( this );
// 	this.m_backend.onerror = this.webSocketError.bind( this );
//
// 	return;
// };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * Callback when the web socket connects
//  * @private
//  */
// ScenarioExplorerAPI.prototype.webSocketConnected = function()
// {
// 	console.log( 'backend connected!' );
//
// 	this.m_webSocketBackedConnected = true;
//
// 	this.initializeFromURI( window.location.href.toString(), false );
//
// 	return;
// };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * Callback when the web socket disconnects
//  * @private
//  */
// ScenarioExplorerAPI.prototype.webSocketDisconnected = function()
// {
// 	this.m_webSocketBackedConnected = false;
// 	// this.showWarning( 'Disconnected from server!' ); // TODO re-enable
//
// 	return;
// };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * Callback when the web socket hits an error
//  * @private
//  */
// ScenarioExplorerAPI.prototype.webSocketError = function( e )
// {
// 	console.warn( e ); // TODO handle better!
//
// 	return;
// };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// /**
//  * Callback when the web socket receives a message
//  * @private
//  * @param {MessageEvent} e the incoming message
//  */
// ScenarioExplorerAPI.prototype.webSocketMessage = function( e )
// {
// 	console.log( 'incoming message raw: %O', e.data );
// 	var parsedMessage = JSON.parse( e.data );
// 	console.log( 'incoming web sockets message: %O', parsedMessage );
//
// 	this.runMessageCallback( parsedMessage );
//
// 	return;
// };

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * @static
 * @param {string} key the JSON key to serialize
 * @param {*} value the JSON value to serialize
 * @return {*} the correctly serialized value
 */
ScenarioExplorerAPI.prototype.customJSONSerializer = function( key, value )
{
	if( value && typeof value === 'object' && value.toJSON )
	{
		return value.toJSON();
	}
	else if( typeof value !== 'number' )
	{
		return value;
	}

	// we only want to keep a few decimal places for floats, everything else serialize as normal
	if(
		value !== undefined
		&& value !== null
		&& !isNaN( parseFloat( value.toString() ) )
		&& isFinite( value )
		&& parseInt( value.toString(), 10 ) !== value
	)
	{
		return Number( value.toFixed( 3 ) );
	}

	return value;
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Evaluates the given plugin
 *
 * @param {string} id - the plugin id
 * @param {string} src - the plugin source code
 * @returns {ScenarioExplorerPluginSource} the parsed javascript plugin
 * @private
 */
ScenarioExplorerAPI.prototype.evalPluginSource = function( id, src )
{
	if( src === null || src === undefined )
	{
		console.warn( 'Plugin source failed: %s', id );
	}

	return /** @type {ScenarioExplorerPluginSource} */( eval( '"use strict";' + src + ';plugin;' ) );
};

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
