package com.imaginationsupport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is for a REST API request call
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface RestApiRequestInfo
{
	enum HttpMethod
	{
		/**
		 * list / get
		 */
		Get,

		/**
		 * create
		 */
		Post,

		/**
		 * update
		 */
		Put,

		/**
		 * delete
		 */
		Delete
	}

	enum Request
	{
		ListUsers,
		GetUser,
		NewUser,
		UpdateUser,
		DeleteUser,

		ListProjects,
		GetProject,
		NewProject,
		UpdateProject,
		DeleteProject,
		ExportProject,
		ImportProject,
		CloneProject,
		NewTemplateFromProject,

		ListProjectTemplates,
		GetProjectTemplate,
		NewProjectTemplate,
		UpdateProjectTemplate,
		DeleteProjectTemplate,
		NewProjectFromTemplate,

		ListViews,
		GetView,
		NewView,
		UpdateView,
		DeleteView,
		GetViewTree,
		GetViewStats,

		ListTimelineEvents,
		GetTimelineEvent,
		NewTimelineEvent,
		UpdateTimelineEvent,
		DeleteTimelineEvent,

		ListFeatures,
		GetFeature,
		NewFeature,
		UpdateFeature,
		DeleteFeature,

		ListConditioningEvents,
		GetConditioningEvent,
		NewConditioningEvent,
		UpdateConditioningEvent,
		DeleteConditioningEvent,
		AssignConditioningEvent,

		ListStates,
		GetState,
		UpdateState,

		//ListHistoricalDatasets,
		//GetHistoricalDataset,
		//NewHistoricalDataset,
		//UpdateHistoricalDataset,
		//DeleteHistoricalDataset,

		ListFeatureTypes,
		GetFeatureType,
		GetFeatureTypeSource,

		ListPreconditions,
		GetPrecondition,
		GetPreconditionSource,

		ListOutcomeEffects,
		GetOutcomeEffect,
		GetOutcomeEffectSource,

		ListProjectors,
		GetProjector,
		GetProjectorSource,

		ListDashboard,
	}

	Request request();

	String[] uriParts();

	HttpMethod method();

	String summary();

	String description() default "";

	boolean isDownload() default false;

	String downloadFilename() default "";

	enum SchemaType
	{
		JsonArray,
		JsonObject,

		JavascriptSource
	}

	SchemaType responseSchemaType();

	enum SchemaDefinition
	{
		User,
		Project,
		ProjectTemplate,
		View,
		TimelineEvent,
		Feature,
		ConditioningEvent,
		State,

		FeatureType,
		Precondition,
		OutcomeEffect,
		Projector,

		DashboardData,
		ViewTree,

		GenericObject,

		PluginSource,

		ProjectBackup
	}

	SchemaDefinition responseSchemaDefinition();
}
