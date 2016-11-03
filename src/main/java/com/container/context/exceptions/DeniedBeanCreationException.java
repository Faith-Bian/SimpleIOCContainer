/**
 * The package contains exceptions that W1nter container may throw.
 */
package com.container.context.exceptions;

/**
 * This exception will be thrown if user of the container
 * tries to create an instance of  class marked with @Denied annotation.
 */
public class DeniedBeanCreationException extends Exception{
    public DeniedBeanCreationException() {
    }

    public DeniedBeanCreationException(String message) {
        super(message);
    }
}
