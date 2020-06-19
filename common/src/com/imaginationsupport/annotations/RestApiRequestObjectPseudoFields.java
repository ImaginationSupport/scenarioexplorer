package com.imaginationsupport.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This handler is simply a helper annotation so that we can have multiple pseudo field annotations
 *
 * @see RestApiRequestObjectPseudoFieldInfo
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.TYPE )
public @interface RestApiRequestObjectPseudoFields
{
	RestApiRequestObjectPseudoFieldInfo[] value();
}
