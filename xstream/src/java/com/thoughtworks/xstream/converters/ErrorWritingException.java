/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24. February 2016 by Joerg Schaible, factored out of ConversionException.
 */
package com.thoughtworks.xstream.converters;

import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;


/**
 * Abstract base class for exceptions supporting an ErrorWriter. It can be passed around to things accepting an
 * ErrorWriter to add diagnostics.
 *
 * @author J&ouml;rg Schaible
 * @see ErrorWriter
 * @since 1.4.9
 */
public abstract class ErrorWritingException extends XStreamException implements ErrorWriter {

    private static final String SEPARATOR = "\n-------------------------------";
    private final Map stuff = new OrderRetainingMap();

    /**
     * Constructs a ErrorWritingException.
     *
     * @param message the error message
     * @since 1.4.9
     */
    public ErrorWritingException(String message) {
        super(message);
        addData(message, null);
    }

    /**
     * Constructs a ErrorWritingException.
     *
     * @param cause the causing exception
     * @since 1.4.9
     */
    public ErrorWritingException(Throwable cause) {
        super(cause);
        addData(null, cause);
    }

    /**
     * Constructs a ErrorWritingException.
     *
     * @param message the error message
     * @param cause the causing exception
     * @since 1.4.9
     */
    public ErrorWritingException(String message, Throwable cause) {
        super(message, cause);
        addData(message, cause);
    }

    private void addData(String msg, Throwable cause) {
        if (msg != null) {
            add("message", msg);
        }
        if (cause != null) {
            add("cause-exception", cause.getClass().getName());
            add("cause-message", cause instanceof ErrorWritingException ? ((ErrorWritingException)cause).getShortMessage() :  cause.getMessage());
        }
    }

    public String get(String errorKey) {
        return (String) stuff.get(errorKey);
    }

    public void add(String name, String information) {
        String key = name;
        int i = 0;
        while (stuff.containsKey(key)) {
            String value = (String)stuff.get(key);
            if (information.equals(value))
                return;
            key = name + "[" + ++i +"]";
        }
        stuff.put(key, information);
    }

    public void set(String name, String information) {
        String key = name;
        int i = 0;
        stuff.put(key, information); // keep order
        while (stuff.containsKey(key)) {
            if (i != 0) {
                stuff.remove(key);
            }
            key = name + "[" + ++i +"]";
        }
    }

    public Iterator keys() {
        return stuff.keySet().iterator();
    }

    public String getMessage() {
        StringBuffer result = new StringBuffer();
        if (super.getMessage() != null) {
            result.append(super.getMessage());
        }
        if (!result.toString().endsWith(SEPARATOR)) {
            result.append("\n---- Debugging information ----");
        }
        for (Iterator iterator = keys(); iterator.hasNext();) {
            String k = (String) iterator.next();
            String v = get(k);
            result.append('\n').append(k);
            result.append("                    ".substring(Math.min(20, k.length())));
            result.append(": ").append(v);
        }
        result.append(SEPARATOR);
        return result.toString();
    }

    public String getShortMessage() {
        return super.getMessage();
    }

}
