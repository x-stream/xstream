/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters;

/**
 * Thrown by {@link Converter} implementations when they cannot convert an object
 * to/from textual data.
 *
 * When this exception is thrown it can be passed around to things that accept an
 * {@link ErrorWriter}, allowing them to add diagnostics to the stack trace.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 *
 * @see ErrorWriter
 */
public class ConversionException extends ErrorWritingException {

    public ConversionException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ConversionException(String msg) {
        super(msg);
    }

    public ConversionException(Throwable cause) {
        super(cause);
    }
}
