/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2014, 2015, 2016 XStream Committers.
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
import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStreamException;


/**
 * Abstract base class for exceptions supporting an ErrorWriter. It can be passed around to things accepting an
 * ErrorWriter to add diagnostics.
 *
 * @author J&ouml;rg Schaible
 * @see ErrorWriter
 * @since 1.4.9
 */
public abstract class ErrorWritingException extends XStreamException implements ErrorWriter {

    private static final long serialVersionUID = 20160226L;
    private static final String SEPARATOR = "\n-------------------------------";
    private final Map<String, String> stuff = new LinkedHashMap<>();

    /**
     * Constructs a ErrorWritingException.
     *
     * @param message the error message
     * @since 1.4.9
     */
    public ErrorWritingException(final String message) {
        super(message);
        addData(message, null);
    }

    /**
     * Constructs a ErrorWritingException.
     *
     * @param cause the causing exception
     * @since 1.4.9
     */
    public ErrorWritingException(final Throwable cause) {
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
    public ErrorWritingException(final String message, final Throwable cause) {
        super(message, cause);
        addData(message, cause);
    }

    private void addData(final String msg, final Throwable cause) {
        if (msg != null) {
            add("message", msg);
        }
        if (cause != null) {
            add("cause-exception", cause.getClass().getName());
            add("cause-message", cause instanceof ErrorWritingException
                ? ((ErrorWritingException)cause).getShortMessage()
                : cause.getMessage());
        }
    }

    @Override
    public String get(final String errorKey) {
        return stuff.get(errorKey);
    }

    @Override
    public void add(final String name, final String information) {
        String key = name;
        int i = 0;
        while (stuff.containsKey(key)) {
            final String value = stuff.get(key);
            if (information.equals(value)) {
                return;
            }
            key = name + "[" + ++i + "]";
        }
        stuff.put(key, information);
    }

    @Override
    public void set(final String name, final String information) {
        String key = name;
        int i = 0;
        stuff.put(key, information); // keep order
        while (stuff.containsKey(key)) {
            if (i != 0) {
                stuff.remove(key);
            }
            key = name + "[" + ++i + "]";
        }
    }

    @Override
    public Iterator<String> keys() {
        return stuff.keySet().iterator();
    }

    @Override
    public String getMessage() {
        final StringBuilder result = new StringBuilder();
        if (super.getMessage() != null) {
            result.append(super.getMessage());
        }
        if (!result.toString().endsWith(SEPARATOR)) {
            result.append("\n---- Debugging information ----");
        }
        for (final Iterator<String> iterator = keys(); iterator.hasNext();) {
            final String k = iterator.next();
            final String v = get(k);
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
