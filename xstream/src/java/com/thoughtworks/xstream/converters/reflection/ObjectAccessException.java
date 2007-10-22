package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.XStreamException;

public class ObjectAccessException extends XStreamException {
    public ObjectAccessException(String message) {
        super(message);
    }

    public ObjectAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
