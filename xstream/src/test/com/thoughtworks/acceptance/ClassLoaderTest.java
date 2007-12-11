/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;

public class ClassLoaderTest extends AbstractAcceptanceTest {

    private String classLoaderCall;

    public void testAllowsClassLoaderToBeOverriden() {
        xstream.setClassLoader(new MockClassLoader());
        assertEquals("hello", xstream.fromXML("<java.BANG.String>hello</java.BANG.String>"));
        assertEquals("java.BANG.String", classLoaderCall);
    }

    private class MockClassLoader extends ClassLoader {
        public Class loadClass(String name) {
            classLoaderCall = name;
            return String.class;
        }
    }
}
