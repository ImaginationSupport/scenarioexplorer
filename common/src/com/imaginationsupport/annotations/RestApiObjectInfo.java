package com.imaginationsupport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is for an API object that is sent or returned from the API via JSON
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface RestApiObjectInfo
{
	String NO_DISCRIMINATOR = "===== NO DISCRIMINATOR =====";

	String definitionName();

	RestApiHandlerInfo.CategoryNames tagName();

	String description();

	String discriminator() default NO_DISCRIMINATOR;
}
