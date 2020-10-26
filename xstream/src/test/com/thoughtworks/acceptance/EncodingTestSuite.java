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

package com.thoughtworks.acceptance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;

import org.apache.commons.lang3.SystemUtils;

import com.bea.xml.stream.MXParserFactory;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.BEAStaxDriver;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.JDom2Driver;
import com.thoughtworks.xstream.io.xml.JDomDriver;
import com.thoughtworks.xstream.io.xml.KXml2DomDriver;
import com.thoughtworks.xstream.io.xml.KXml2Driver;
import com.thoughtworks.xstream.io.xml.StandardStaxDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.WstxDriver;
import com.thoughtworks.xstream.io.xml.XomDriver;
import com.thoughtworks.xstream.io.xml.XppDomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * @author Sanjiv Jivan
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class EncodingTestSuite extends TestSuite {

    public static class TestObject extends StandardObject {
        private static final long serialVersionUID = 200801L;
        private String data;
    }

    public EncodingTestSuite() {
        super(EncodingTestSuite.class.getName());
        addDriverTest(new Dom4JDriver());
        addDriverTest(new DomDriver());
        addDriverTest(new JDomDriver());
        addDriverTest(new JDom2Driver());
        addDriverTest(new StaxDriver());
        if (!SystemUtils.IS_OS_WINDOWS) { // see comment below for Windows
            addDriverTest(new StandardStaxDriver());
            addDriverTest(new BEAStaxDriver());
            addDriverTest(new WstxDriver());
        }
        addDriverTest(new KXml2DomDriver());
        addDriverTest(new KXml2Driver());
        addDriverTest(new XppDomDriver());
        addDriverTest(new XppDriver());
        addDriverTest(new XomDriver());
    }

    private void test(final HierarchicalStreamDriver driver, final String encoding) throws IOException {
        final String headerLine = encoding != null ? "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n" : "";
        final String xmlData = headerLine // force code format
            + "<test>\n"
            + "  <data>J\u00f6rg</data>\n"
            + "</test>";

        final XStream xstream = new XStream(driver);
        xstream.allowTypes(TestObject.class);
        xstream.alias("test", TestObject.class);
        final TestObject obj = new TestObject();
        obj.data = "J\u00f6rg";

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final OutputStreamWriter writer = encoding != null
            ? new OutputStreamWriter(bos, encoding)
            : new OutputStreamWriter(bos);
        xstream.toXML(obj, writer);
        writer.close();

        final String generated = encoding != null ? bos.toString(encoding) : bos.toString();
        Assert.assertTrue("'" + obj.data + "' was not found", generated.indexOf(obj.data) > 0);

        final Object restored = xstream.fromXML(new ByteArrayInputStream(encoding != null
            ? xmlData.getBytes(encoding)
            : xmlData.getBytes()));
        Assert.assertEquals(obj, restored);
    }

    private void addDriverTest(final HierarchicalStreamDriver driver) {
        final String testName = getShortName(driver);
        final String allEncodingTests = System.getProperty("xstream.test.encoding.all");
        if ("true".equals(allEncodingTests)) {
            // Native encoding normally fails on most systems!!
            addTest(new TestCase(testName + "Native") {
                @Override
                protected void runTest() throws Throwable {
                    test(driver, null);
                }
            });
            // System encoding fails on US-ASCII systems, like Codehaus Bamboo
            final String systemEncoding = System.getProperty("file.encoding");
            addTest(new TestCase(testName + "With" + systemEncoding + "SystemEncoding") {
                @Override
                protected void runTest() throws Throwable {
                    final Properties systemProperties = new Properties();
                    systemProperties.putAll(System.getProperties());
                    try {
                        // Use BEA reference implementation for StAX
                        // (Woodstox will fail on Windows because of unknown system encoding)
                        System.setProperty(XMLInputFactory.class.getName(), MXParserFactory.class.getName());
                        test(driver, systemEncoding);
                    } finally {
                        System.setProperties(systemProperties);
                    }
                }
            });
        }
        addTest(new TestCase(testName + "WithUTF_8Encoding") {
            @Override
            protected void runTest() throws Throwable {
                test(driver, "UTF-8");
            }
        });
        addTest(new TestCase(testName + "WithIS0_8859_1Encoding") {
            @Override
            protected void runTest() throws Throwable {
                test(driver, "ISO-8859-1");
            }
        });
    }

    private String getShortName(final HierarchicalStreamDriver driver) {
        String result = driver.getClass().getName();
        result = result.substring(result.lastIndexOf('.') + 1);
        return result;
    }

    public static Test suite() {
        return new EncodingTestSuite();
    }

}
