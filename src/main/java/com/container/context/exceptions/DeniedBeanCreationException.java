package com.container.context.exceptions;

public class DeniedBeanCreationException extends Exception{
    public DeniedBeanCreationException() {
    }

    public DeniedBeanCreationException(String message) {
        super(message);
    }
}
