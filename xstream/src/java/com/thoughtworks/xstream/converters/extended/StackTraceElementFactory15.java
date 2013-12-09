/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 03. December 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

/**
 * @author J&ouml;rg Schaible
 *
 * @since 1.4.6
 */
class StackTraceElementFactory15 extends StackTraceElementFactory {

    @Override
    protected StackTraceElement create(final String declaringClass, final String methodName,
        final String fileName, final int lineNumber) {
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }
}
