package com.thoughtworks.xstream.converters;

public class ConversionException extends RuntimeException {
    public ConversionException(String msg, Exception cause) {
        super(msg + ": " + cause.getMessage());
    }

    public ConversionException(String msg) {
        super(msg);
    }

    public ConversionException(Exception cause) {
        super(cause.getMessage());
    }
}
