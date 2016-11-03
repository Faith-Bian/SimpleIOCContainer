/**
 * This package contains annotations used by W1nter container.
 */
package com.container.context.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes that should be created inside W1nter container must be marked with this annotation.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Snowflake {
    String snowflakeName() default "";
}
