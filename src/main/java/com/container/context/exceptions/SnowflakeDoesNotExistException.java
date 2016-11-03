/**
 * The package contains exceptions that W1nter container may throw.
 */
package com.container.context.exceptions;

public class SnowflakeDoesNotExistException extends Exception {
    public SnowflakeDoesNotExistException(String message) {
        super(message);
    }

    public SnowflakeDoesNotExistException() {

    }
}
