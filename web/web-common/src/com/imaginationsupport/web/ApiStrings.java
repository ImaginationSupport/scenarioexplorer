package com.imaginationsupport.web;

public abstract class ApiStrings
{
	public abstract static class RestApiStrings
	{
		public static final String User = "user";
		public static final String Project = "project";
		public static final String ProjectTemplate = "projectTemplate";
		public static final String View = "view";
		public static final String TimelineEvent = "timelineEvent";
		public static final String Feature = "feature";
		public static final String ConditioningEvent = "conditioningEvent";
		public static final String State = "state";
		public static final String FeatureType = "featureType";
		public static final String Precondition = "precondition";
		public static final String OutcomeEffect = "outcomeEffect";
		public static final String Projector = "projector";
		public static final String Dashboard = "dashboard";

		public static final String Src = "src";

		public static final String Tree = "tree";
		public static final String Stats = "stats";

		public static final String Export = "export";
		public static final String Import = "import";

		public static final String Clone = "clone";
//		public static final String CreateTemplate = "createTemplate";
		public static final String FromTemplate = "fromTemplate";

		public abstract static class Parameters
		{
			public static final String UserName = "{userName}";
			public static final String ProjectId = "{projectId}";
			public static final String ProjectTemplateId = "{projectTemplateId}";
			public static final String ViewId = "{viewId}";
			public static final String TimelineEventId = "{timelineEventId}";
			public static final String FeatureId = "{featureId}";
			public static final String ConditioningEventId = "{conditioningEventId}";
			public static final String StateId = "{stateId}";

			public static final String FeatureTypeId = "{featureTypeId}";
			public static final String PreconditionId = "{preconditionId}";
			public static final String OutcomeEffectId = "{outcomeEffectId}";
			public static final String ProjectorId = "{projectorId}";
		}
	}

	public abstract static class JsonKeys
	{
		public static final String Success = "success";
		public static final String ErrorMessage = "errorMessage";

		public static final String Username = "userName";
		public static final String ProjectId = "projectId";
		public static final String ProjectTemplateId = "projectTemplateId";
		public static final String ViewId = "viewId";
		public static final String TimelineEventId = "timelineEventId";
		public static final String FeatureId = "featureId";
		public static final String ConditioningEventId = "conditioningEventId";
		public static final String StateId = "stateId";

		public static final String User = "user";
		public static final String Project = "project";
		public static final String View = "view";
		public static final String TimelineEvent = "timelineEvent";
		public static final String Feature = "feature";
		public static final String ConditioningEvent = "conditioningEvent";
		public static final String State = "state";
		// public static final String HistoricalDataset = "historicalDataset";
		public static final String FeatureTypeId = "featureTypeId";
		public static final String PreconditionId = "preconditionId";
		public static final String OutcomeEffectId = "outcomeEffectId";
		public static final String ProjectorId = "projectorId";

		public static final String Tree = "tree";
		public static final String Stats = "stats";

		public static final String FromTemplateName = "name";
		public static final String FromTemplateDescription = "description";
	}
}
