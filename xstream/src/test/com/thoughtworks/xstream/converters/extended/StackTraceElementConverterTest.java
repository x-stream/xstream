/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;


/**
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 * @author Joe Walnes
 */
public class StackTraceElementConverterTest extends AbstractAcceptanceTest {

    private final StackTraceElementConverter.StackTraceElementFactory factory =
            new StackTraceElementConverter.StackTraceElementFactory();

    public void testSerializesStackTraceElement() {
        final StackTraceElement trace = factory.unknownSourceElement("com.blah.SomeClass", "someMethod");
        final String expectedXml = "<trace>com.blah.SomeClass.someMethod(Unknown Source)</trace>";
        final StackTraceElement unmarshalled = assertBothWays(trace, expectedXml);
        assertNull(unmarshalled.getFileName());
        assertEquals(-1, unmarshalled.getLineNumber());
    }

    public void testWithFileBasedSourceCodeUnitAndLineNumber() {
        final StackTraceElement trace = factory.element("com.blah.SomeClass", "someMethod", "SomeClass.java", 22);
        final String expectedXml = "<trace>com.blah.SomeClass.someMethod(SomeClass.java:22)</trace>";
        assertBothWays(trace, expectedXml);
    }

    public void testWithFileBasedSourceCodeUnitOnly() {
        final StackTraceElement trace = factory.element("com.blah.SomeClass", "someMethod", "SomeClass.java");
        final String expectedXml = "<trace>com.blah.SomeClass.someMethod(SomeClass.java)</trace>";
        final StackTraceElement unmarshalled = assertBothWays(trace, expectedXml);
        assertEquals(-1, unmarshalled.getLineNumber());
    }

    public void testWithArbitraryourceCodeUnitAndLineNumber() {
        final StackTraceElement trace = factory
            .element("com.blah.SomeClass", "someMethod", "jar:file:/tmp/x-1.0.jar!/com/blah/SomeClass.groovy", 22);
        final String expectedXml =
                "<trace>com.blah.SomeClass.someMethod(jar:file:/tmp/x-1.0.jar!/com/blah/SomeClass.groovy:22)</trace>";
        assertBothWays(trace, expectedXml);
    }

    public void testNativeMethodsWithoutSourceCodeUnit() {
        final StackTraceElement trace = factory.nativeMethodElement("com.blah.SomeClass", "someMethod");
        final String expectedXml = "<trace>com.blah.SomeClass.someMethod(Native Method)</trace>";
        assertBothWays(trace, expectedXml);
        final StackTraceElement unmarshalled = assertBothWays(trace, expectedXml);
        assertNull(unmarshalled.getFileName());
        assertEquals(-2, unmarshalled.getLineNumber());
    }

    public void testNativeMethodsWithSourceCodeUnit() {
        final StackTraceElement trace = factory
            .nativeMethodElement("com.blah.SomeClass", "someMethod", "SomeClass.java");
        final String expectedXml = "<trace>com.blah.SomeClass.someMethod(SomeClass.java:-2)</trace>";
        assertBothWays(trace, expectedXml);
        final StackTraceElement unmarshalled = assertBothWays(trace, expectedXml);
        assertEquals("SomeClass.java", unmarshalled.getFileName());
        assertEquals(-2, unmarshalled.getLineNumber());
    }

    public void testSupportsInnerClasses() {
        final StackTraceElement trace = factory.unknownSourceElement("com.blah.SomeClass$Inner$2", "someMethod");
        final String expectedXml = "<trace>com.blah.SomeClass$Inner$2.someMethod(Unknown Source)</trace>";
        assertBothWays(trace, expectedXml);
    }

}
