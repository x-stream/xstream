package com.thoughtworks.xstream.converters.reflection;

public class ObjectAccessException extends RuntimeException {
    public ObjectAccessException(String message) {
        super(message);
    }

    public ObjectAccessException(String message, Throwable cause) {
        super(message + ": " + cause.getMessage());
    }
}
