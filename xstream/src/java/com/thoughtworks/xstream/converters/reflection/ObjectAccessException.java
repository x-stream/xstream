package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.core.BaseException;

public class ObjectAccessException extends BaseException {
    public ObjectAccessException(String message) {
        super(message);
    }

    public ObjectAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
