/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * Created on 03. December 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

/**
 * @author J&ouml;rg Schaible
 *
 * @since upcoming
 */
class StackTraceElementFactory15 extends StackTraceElementFactory {

    @Override
    protected StackTraceElement create(final String declaringClass, final String methodName,
        final String fileName, final int lineNumber) {
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }
}
