package com.imaginationsupport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is for a class that includes REST handler methods
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface RestApiHandlerInfo
{
	enum CategoryNames
	{
		None, // i.e. do not include this item

		User,
		Project,
		ProjectTemplate,
		View,
		TimelineEvent,
		Feature,
		ConditioningEvent,
		State,

		Plugin,

		Dashboard
	}

	CategoryNames name();
}
