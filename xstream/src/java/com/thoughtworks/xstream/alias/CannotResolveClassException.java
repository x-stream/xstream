package com.thoughtworks.xstream.alias;

public class CannotResolveClassException extends RuntimeException {
    public CannotResolveClassException(String className) {
        super(className);
    }
}
