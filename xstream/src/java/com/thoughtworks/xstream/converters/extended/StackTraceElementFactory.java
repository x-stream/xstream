/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Field;

import com.thoughtworks.xstream.core.util.Fields;


/**
 * Factory for creating StackTraceElements.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 * @author Joe Walnes
 * @deprecated As of 1.4.8, it is an internal helper class
 */
public class StackTraceElementFactory {

    public StackTraceElement nativeMethodElement(final String declaringClass, final String methodName) {
        return create(declaringClass, methodName, "Native Method", -2);
    }

    public StackTraceElement unknownSourceElement(final String declaringClass, final String methodName) {
        return create(declaringClass, methodName, "Unknown Source", -1);
    }

    public StackTraceElement element(final String declaringClass, final String methodName, final String fileName) {
        return create(declaringClass, methodName, fileName, -1);
    }

    public StackTraceElement element(final String declaringClass, final String methodName, final String fileName,
            final int lineNumber) {
        return create(declaringClass, methodName, fileName, lineNumber);
    }

    protected StackTraceElement create(final String declaringClass, final String methodName, final String fileName,
            final int lineNumber) {
        final StackTraceElement result = new Throwable().getStackTrace()[0];
        setField(result, "declaringClass", declaringClass);
        setField(result, "methodName", methodName);
        setField(result, "fileName", fileName);
        setField(result, "lineNumber", new Integer(lineNumber));
        return result;
    }

    private void setField(final StackTraceElement element, final String fieldName, final Object value) {
        final Field field = Fields.find(StackTraceElement.class, fieldName);
        Fields.write(field, element, value);
    }

}
