/**
 * This package contains annotations used by W1nter container.
 */
package com.container.context.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes marked with this annotation cannot be created by W1nter container.
 * DeniedBeanCreation exception will be thrown when getSnowflake(String name) of W1nter container is called.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Denied {
}
