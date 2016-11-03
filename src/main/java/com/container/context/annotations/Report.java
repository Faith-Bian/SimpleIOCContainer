/**
 * This package contains annotations used by W1nter container.
 */
package com.container.context.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If class is marked with this annotation a report will be generated during instance creation.
 * Parameter "destinationFile" specifies the path to the report.
 */
@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Report {
    String destinationFile() default "";
}
