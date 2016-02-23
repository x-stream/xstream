/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.converters.ErrorWritingException;


public class ObjectAccessException extends ErrorWritingException {
    private static final long serialVersionUID = 20160226L;

    public ObjectAccessException(final String message) {
        super(message);
    }

    public ObjectAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
