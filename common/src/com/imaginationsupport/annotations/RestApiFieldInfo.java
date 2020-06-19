package com.imaginationsupport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is for API object fields
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface RestApiFieldInfo
{
	/**
	 * This is a string that we will use as the default to mark that the field is optional since null is not allowed
	 */
	String USE_NAME_OF_MEMBER_VARIABLE = "=====USE MEMBER VARIABLE NAME=====";

	String jsonField() default USE_NAME_OF_MEMBER_VARIABLE;

	String description();

	boolean isRequired() default true;
}
