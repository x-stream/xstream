/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converter for {@link StackTraceElement} (the lines of a stack trace) to a string.
 * 
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 * @author Joe Walnes
 */
public class StackTraceElementConverter extends AbstractSingleValueConverter {

    // Regular expression to parse a line of a stack trace. Returns 4 groups.
    //
    // Example:
    // com.blah.MyClass.doStuff(MyClass.java:123)
    // |-------1------| |--2--| |----3-----| |4|
    // (Note group 4 is optional is optional and only present if a colon char exists.)

    private static final Pattern PATTERN = Pattern.compile("^(.+)\\.([^\\(]+)\\(([^:]*)(:(\\d+))?\\)$");
    private static final StackTraceElementFactory FACTORY = new StackTraceElementFactory();

    static class StackTraceElementFactory {

        public StackTraceElement nativeMethodElement(String declaringClass, String methodName) {
            return create(declaringClass, methodName, "Native Method", -2);
        }

        public StackTraceElement unknownSourceElement(String declaringClass, String methodName) {
            return create(declaringClass, methodName, "Unknown Source", -1);
        }

        public StackTraceElement element(String declaringClass, String methodName, String fileName) {
            return create(declaringClass, methodName, fileName, -1);
        }

        public StackTraceElement element(String declaringClass, String methodName, String fileName, int lineNumber) {
            return create(declaringClass, methodName, fileName, lineNumber);
        }

        private StackTraceElement create(String declaringClass, String methodName, String fileName, int lineNumber) {
            return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
        }
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return StackTraceElement.class.equals(type);
    }

    @Override
    public String toString(final Object obj) {
        final String s = super.toString(obj);
        // JRockit adds ":???" for invalid line number
        return s.replaceFirst(":\\?\\?\\?", "");
    }

    @Override
    public Object fromString(final String str) {
        final Matcher matcher = PATTERN.matcher(str);
        if (matcher.matches()) {
            final String declaringClass = matcher.group(1);
            final String methodName = matcher.group(2);
            final String fileName = matcher.group(3);
	    switch (fileName) {
	    	case "Unknown Source":
		    return FACTORY.unknownSourceElement(declaringClass, methodName);
	    	case "Native Method":
		    return FACTORY.nativeMethodElement(declaringClass, methodName);
	    	default:
		    if (matcher.group(4) != null) {
			final int lineNumber = Integer.parseInt(matcher.group(5));
			return FACTORY.element(declaringClass, methodName, fileName, lineNumber);
		    } else {
			return FACTORY.element(declaringClass, methodName, fileName);
		    }
	    }
        } else {
            throw new ConversionException("Could not parse StackTraceElement : " + str);
        }
    }
}
