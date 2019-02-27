/*
 * Copyright (C) 2011, 2015. 2017, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. September 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

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

    // inherits tests from superclass
}
