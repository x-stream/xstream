/*
 * Copyright (C) 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 04. May 2019 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;


/**
 * @author J&ouml;rg Schaible
 */
public class StackTraceElementConverter9Test extends AbstractAcceptanceTest {

    private final StackTraceElementConverter.StackTraceElementFactory factory =
            new StackTraceElementConverter.StackTraceElementFactory();

    public void testWithBuiltInClassLoaderAndInternalModule() {
        final StackTraceElement trace = factory.create(null, "deCologne", null, "com.blah.SomeClass", "someMethod", "SomeClass.java", 22);
        final String expectedXml = "<trace>deCologne/com.blah.SomeClass.someMethod(SomeClass.java:22)</trace>";
        final StackTraceElement unmarshalled = assertBothWays(trace, expectedXml);
        assertNull(unmarshalled.getClassLoaderName());
        assertNull(unmarshalled.getModuleVersion());
        assertEquals("deCologne", unmarshalled.getModuleName());
    }

    public void testWithBuiltInClassLoaderAndModule() {
        final StackTraceElement trace = factory.create(null, "deCologne", "47.11", "com.blah.SomeClass", "someMethod", "SomeClass.java", 22);
        final String expectedXml = "<trace>deCologne@47.11/com.blah.SomeClass.someMethod(SomeClass.java:22)</trace>";
        final StackTraceElement unmarshalled = assertBothWays(trace, expectedXml);
        assertNull(unmarshalled.getClassLoaderName());
        assertEquals("47.11", unmarshalled.getModuleVersion());
        assertEquals("deCologne", unmarshalled.getModuleName());
    }

    public void testWithClassLoaderAndModule() {
        final StackTraceElement trace = factory.create("Eau", "deCologne", "47.11", "com.blah.SomeClass", "someMethod", "SomeClass.java", 22);
        final String expectedXml = "<trace>Eau/deCologne@47.11/com.blah.SomeClass.someMethod(SomeClass.java:22)</trace>";
        final StackTraceElement unmarshalled = assertBothWays(trace, expectedXml);
        assertEquals("Eau", unmarshalled.getClassLoaderName());
        assertEquals("47.11", unmarshalled.getModuleVersion());
        assertEquals("deCologne", unmarshalled.getModuleName());
    }

    public void testWithClassLoaderAndUnnamedModule() {
        final StackTraceElement trace = factory.create("Eau", null, null, "com.blah.SomeClass", "someMethod", "SomeClass.java", 22);
        final String expectedXml = "<trace>Eau//com.blah.SomeClass.someMethod(SomeClass.java:22)</trace>";
        final StackTraceElement unmarshalled = assertBothWays(trace, expectedXml);
        assertEquals("Eau", unmarshalled.getClassLoaderName());
        assertNull(unmarshalled.getModuleVersion());
        assertNull(unmarshalled.getModuleName());
    }
}
