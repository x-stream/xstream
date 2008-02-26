/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.XStreamException;

/**
 * JDK1.3 friendly exception that retains cause.
 * @deprecated since 1.3, use {@link XStreamException} instead
 */
public abstract class BaseException extends RuntimeException {

    protected BaseException(String message) {
        super(message);
    }

    public abstract Throwable getCause();
}
