package com.container.exceptions;

public class SnowflakeDoesNotExistException extends Exception {
    public SnowflakeDoesNotExistException(String message) {
        super(message);
    }

    public SnowflakeDoesNotExistException() {

    }
}
