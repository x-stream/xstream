package com.thoughtworks.xstream.converters;

public class ConversionException extends RuntimeException {
    public ConversionException(String msg, Exception cause) {
        super(msg, cause);
    }
}
