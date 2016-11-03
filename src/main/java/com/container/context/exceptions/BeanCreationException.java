/**
 * The package contains exceptions that W1nter container may throw.
 */
package com.container.context.exceptions;

/**
 * Exception that will be thrown when bean instantiation fails.
 */
public class BeanCreationException extends Exception {
    public BeanCreationException() {
    }

    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(Throwable cause) {
        super(cause);
    }
}
