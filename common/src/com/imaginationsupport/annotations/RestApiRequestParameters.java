package com.imaginationsupport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This handler is simply a helper annotation so that we can have multiple parameter annotations
 *
 * @see RestApiRequestParameterInfo
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface RestApiRequestParameters
{
	RestApiRequestParameterInfo[] value();
}
