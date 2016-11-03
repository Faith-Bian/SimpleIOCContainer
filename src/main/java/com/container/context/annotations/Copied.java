/**
 * This package contains annotations used by W1nter container.
 */
package com.container.context.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation is used to mark classes that may contain a lot of instances inside container.
 * If class is not marked with this annotation only one instance of the class will be created.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Copied {
}
