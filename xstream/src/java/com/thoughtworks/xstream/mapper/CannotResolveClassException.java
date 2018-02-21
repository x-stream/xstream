/*
 * Copyright (C) 2003 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.XStreamException;


/**
 * Exception thrown if a mapper cannot locate the appropriate class for an element.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CannotResolveClassException extends XStreamException {
    private static final long serialVersionUID = 10400L;

    public CannotResolveClassException(final String className) {
        super(className);
    }

    /**
     * @since 1.4.2
     */
    public CannotResolveClassException(final String className, final Throwable cause) {
        super(className, cause);
    }
}
