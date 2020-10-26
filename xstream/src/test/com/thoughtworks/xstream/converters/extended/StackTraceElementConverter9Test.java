/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
