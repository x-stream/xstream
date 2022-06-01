/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018, 2019, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.core.JVM;


/**
 * Converter for {@link StackTraceElement} (the lines of a stack trace) to a string.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 * @author Joe Walnes
 */
public class StackTraceElementConverter extends AbstractSingleValueConverter {

    // Regular expression to parse a line of a stack trace. Returns 12 groups.
    //
    // Examples:
    // com.blah.MyClass.doStuff(MyClass.java)
    // com.blah.MyClass.doStuff(MyClass.java:123)
    // module/com.blah.MyClass.doStuff(MyClass.java:123)
    // module@45/com.blah.MyClass.doStuff(MyClass.java:123)
    // loader/module@45/com.blah.MyClass.doStuff(MyClass.java:123)
    // loader//com.blah.MyClass.doStuff(MyClass.java:123)

    private static final Pattern PATTERN = Pattern
        .compile("^((([^/]+)/)??(([^/@]*)(@([^/]+))?)?/)?([^/]+)\\.([^./(]+)\\((.*?)(:(-?\\d+))?\\)$");
    private static final StackTraceElementFactory FACTORY = new StackTraceElementFactory();

    static class StackTraceElementFactory {

        /**
         * @deprecated As of 1.4.8, internal use only
         */
        @Deprecated
        public StackTraceElement nativeMethodElement(final String declaringClass, final String methodName) {
            return create(null, null, null, declaringClass, methodName, null, -2);
        }

        /**
         * @deprecated As of 1.4.8, internal use only
         */
        @Deprecated
        public StackTraceElement unknownSourceElement(final String declaringClass, final String methodName) {
            return create(null, null, null, declaringClass, methodName, null, -1);
        }

        /**
         * @deprecated As of 1.4.8, internal use only
         */
        @Deprecated
        public StackTraceElement element(final String declaringClass, final String methodName, final String fileName) {
            return create(null, null, null, declaringClass, methodName, fileName, -1);
        }

        /**
         * @deprecated As of 1.4.8, internal use only
         */
        @Deprecated
        public StackTraceElement element(final String declaringClass, final String methodName, final String fileName,
                final int lineNumber) {
            return create(null, null, null, declaringClass, methodName, fileName, lineNumber);
        }

        StackTraceElement create(final String classLoaderName, final String moduleName, final String moduleVersion,
                final String declaringClass, final String methodName, final String fileName, final int lineNumber) {
            if (JVM.isVersion(9) && (classLoaderName != null || moduleName != null || moduleVersion != null)) {
                Exception ex = null;
                try {
                    final Constructor<StackTraceElement> constructor = StackTraceElement.class
                        .getDeclaredConstructor(String.class, String.class, String.class, String.class, String.class,
                            String.class, int.class);
                    return constructor
                        .newInstance(classLoaderName, moduleName, moduleVersion, declaringClass, methodName, fileName,
                            lineNumber);
                } catch (final NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    ex = e;
                }
                if (ex != null) {
                    throw new ConversionException("Cannot construct instance of StackTraceElement.", ex);
                }
            }
            return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
        }

        private String toString(final Object obj) {
            if (obj == null) {
                return null;
            }
            final StackTraceElement element = StackTraceElement.class.cast(obj);
            final String result = obj.toString();
            return element.isNativeMethod() && element.getFileName() != null
                ? result.replace("(Native Method)", String.format("(%s:-2)", element.getFileName()))
                : result;
        }
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return StackTraceElement.class.equals(type);
    }

    @Override
    public String toString(final Object obj) {
        final String s = FACTORY.toString(obj);
        // JRockit adds ":???" for invalid line number
        return s.replaceFirst(":\\?\\?\\?", "");
    }

    @Override
    public Object fromString(final String str) {
        final Matcher matcher = PATTERN.matcher(str);
        if (matcher.matches()) {
            String classLoaderName = null;
            String moduleName = null;
            String moduleVersion = null;
            if (matcher.group(1) != null) {
                if (matcher.group(2) != null) {
                    classLoaderName = matcher.group(3);
                }
                if (matcher.group(4) != null) {
                    moduleName = matcher.group(5).length() == 0 ? null : matcher.group(5);
                    if (matcher.group(6) != null) {
                        moduleVersion = matcher.group(7);
                    }
                }
            }

            final String declaringClass = matcher.group(8);
            final String methodName = matcher.group(9);
            String fileName = matcher.group(10);
            int lineNumber = -1;
            if (fileName.equals("Unknown Source")) {
                fileName = null;
                lineNumber = -1;
            } else if (fileName.equals("Native Method")) {
                fileName = null;
                lineNumber = -2;
            } else {
                if (matcher.group(11) != null) {
                    lineNumber = Integer.parseInt(matcher.group(12));
                }
            }
            return FACTORY
                .create(classLoaderName, moduleName, moduleVersion, declaringClass, methodName, fileName, lineNumber);
        } else {
            throw new ConversionException("Could not parse StackTraceElement : " + str);
        }
    }
}
