/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStreamException;


/**
 * Thrown by {@link Converter} implementations when they cannot convert an object to/from textual data. When this
 * exception is thrown it can be passed around to things that accept an {@link ErrorWriter}, allowing them to add
 * diagnostics to the stack trace.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @see ErrorWriter
 */
public class ConversionException extends XStreamException implements ErrorWriter {

    private static final String SEPARATOR = "\n-------------------------------";
    private final Map<String, String> stuff = new LinkedHashMap<String, String>();

    public ConversionException(final String msg, final Throwable cause) {
        super(msg, cause);
        if (msg != null) {
            add("message", msg);
        }
        if (cause != null) {
            add("cause-exception", cause.getClass().getName());
            add("cause-message", cause instanceof ConversionException
                ? ((ConversionException)cause).getShortMessage()
                : cause.getMessage());
        }
    }

    public ConversionException(final String msg) {
        super(msg);
    }

    public ConversionException(final Throwable cause) {
        this(cause.getMessage(), cause);
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
