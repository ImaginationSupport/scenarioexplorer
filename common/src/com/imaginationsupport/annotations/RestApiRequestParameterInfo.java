package com.imaginationsupport.annotations;

import java.lang.annotation.*;

/**
 * This annotation is for the incoming request parameters
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
@Repeatable( RestApiRequestParameters.class )
public @interface RestApiRequestParameterInfo
{
	enum InLocations
	{
		Path,
		FormData,
	}

	enum ParameterType
	{
		String,
		ObjectId,

		User,
		Project,
		ProjectTemplate,
		View,
		TimelineEvent,
		Feature,
		ConditioningEvent,

		ProjectBackup
	}

	String name();

	String description() default "";

	InLocations in();

	ParameterType type() default ParameterType.String;

	boolean required() default true;
}
