package com.thoughtworks.xstream.converters.reference;

import com.thoughtworks.xstream.converters.ConversionException;

public class CircularityException extends ConversionException {
    public CircularityException(String msg, Exception cause) {
        super(msg, cause);
    }

    public CircularityException(String msg) {
        super(msg);
    }
}
