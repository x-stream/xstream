package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.XStreamException;

/**
 * JDK1.3 friendly exception that retains cause.
 * @deprecated since upcoming, use {@link XStreamException} instead
 */
public abstract class BaseException extends RuntimeException {

    protected BaseException(String message) {
        super(message);
    }

    public abstract Throwable getCause();
}
