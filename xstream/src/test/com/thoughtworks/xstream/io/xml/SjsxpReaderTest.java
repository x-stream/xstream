/*
 * Copyright (C) 2011, 2015, 2017, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. September 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;


public class SjsxpReaderTest extends AbstractStaxReaderTest {
    final static String className = "com.sun.xml.internal.stream.XMLInputFactoryImpl";

    public static Test suite() {
        try {
            Class.forName(className);
            return new TestSuite(SjsxpReaderTest.class);
        } catch (final ClassNotFoundException e) {
            return new TestCase(SjsxpReaderTest.class.getName() + ": not available") {

                @Override
                public int countTestCases() {
                    return 1;
                }

                @Override
                public void run(final TestResult result) {
                }
            };
        }
    }

    @Override
    protected StaxDriver createDriver(final QNameMap qnameMap) {
        return new SjsxpDriver(qnameMap);
    }

    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        final String prefix = getName().endsWith("ISOControlCharactersInValue") ? XML_1_1_HEADER : "";
        return super.createReader(prefix + xml);
    }

    @Override
    protected String getSpecialCharsInJavaNamesForXml10() {
        return super.getSpecialCharsInJavaNamesForXml10_4th();
    }

    @Override
    public void testNullCharacterInValue() throws Exception {
        // not possible, null value is invalid in XML
    }

    @Override
    public void testNonUnicodeCharacterInValue() throws Exception {
        // not possible, character is invalid in XML
    }

    @Override
    public void testNonUnicodeCharacterInCDATA() throws Exception {
        // not possible, character is invalid in XML
    }

    // inherits tests from superclass
}
