/*
 * Copyright (C) 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. October 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream;

import com.thoughtworks.xstream.core.BaseException;


/**
 * Base exception for all thrown exceptions with XStream. JDK 1.3 friendly cause handling.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class XStreamException extends BaseException {

    private Throwable cause;

    /**
     * Default constructor.
     * 
     * @since 1.3
     */
    protected XStreamException() {
        this("", null);
    }

    /**
     * Constructs an XStreamException with a message.
     * 
     * @param message
     * @since 1.3
     */
    public XStreamException(String message) {
        this(message, null);
    }

    /**
     * Constructs an XStreamException as wrapper for a different causing {@link Throwable}.
     * 
     * @param cause
     * @since 1.3
     */
    public XStreamException(Throwable cause) {
        this("", cause);
    }

    /**
     * Constructs an XStreamException with a message as wrapper for a different causing
     * {@link Throwable}.
     * 
     * @param message
     * @param cause
     * @since 1.3
     */
    public XStreamException(String message, Throwable cause) {
        super(message + (cause == null ? "" : " : " + cause.getMessage()));
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }

}
