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

package com.thoughtworks.xstream.io.xml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;


public final class SjsxpWriterTest extends AbstractStaxWriterTest {
    final static String className = "com.sun.xml.internal.stream.XMLOutputFactoryImpl";

    public static Test suite() {
        try {
            Class.forName(className);
            return new TestSuite(SjsxpWriterTest.class);
        } catch (final ClassNotFoundException e) {
            return new TestCase(SjsxpWriterTest.class.getName() + ": not available") {

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
    protected void assertXmlProducedIs(String expected) {
        if (!staxDriver.isRepairingNamespace()) {
            expected = expected.replaceAll(" xmlns=\"\"", "");
        }
        expected = expected.replaceAll("<(\\w+)([^>]*)/>", "<$1$2></$1>");
        expected = expected.replace("&#xd;", "\r");
        // attributes are not properly escaped
        expected = expected.replace("&#xa;", "\n");
        expected = expected.replace("&#x9;", "\t");
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    @Override
    protected String getXMLHeader() {
        return "<?xml version=\"1.0\" ?>";
    }

    @Override
    protected StaxDriver getStaxDriver() {
        return new SjsxpDriver();
    }
}
