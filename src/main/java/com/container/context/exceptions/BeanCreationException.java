package com.container.context.exceptions;

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
