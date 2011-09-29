/*
 * Copyright (C) 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. November 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import javax.xml.stream.XMLOutputFactory;

public final class SjsxpStaxWriterTest extends AbstractStaxWriterTest {
    final static String className = "com.sun.xml.internal.stream.XMLOutputFactoryImpl";

    public static Test suite() {
        return createSuite(SjsxpStaxWriterTest.class, className);
    }

    protected static Test createSuite(Class test, String staxClassName) {
        try {
            Class.forName(staxClassName);
            return new TestSuite(test);
        } catch (ClassNotFoundException e) {
            return new TestCase(test.getName() + ": not available") {

                public int countTestCases() {
                    return 1;
                }

                public void run(TestResult result) {
                }
            };
        }
    }

    protected void assertXmlProducedIs(String expected) {
        if (!staxDriver.isRepairingNamespace()) {
            expected = perlUtil.substitute("s# xmlns=\"\"##g", expected);
        }
        expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2></$1>#g", expected);
        expected = replaceAll(expected, "&#xd;", "\r");
        // attributes are not properly escaped
        expected = replaceAll(expected, "&#xa;", "\n");
        expected = replaceAll(expected, "&#x9;", "\t");
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    protected String getXMLHeader() {
        return "<?xml version=\"1.0\" ?>";
    }

    protected StaxDriver getStaxDriver() {
        return new SjsxpDriver();
    }
}