/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 25. June 2006 by Guilherme Silveira
 */
package com.thoughtworks.acceptance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import javax.xml.stream.XMLInputFactory;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.bea.xml.stream.MXParserFactory;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.JDomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XomDriver;
import com.thoughtworks.xstream.io.xml.XppDomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;


/**
 * @author Sanjiv Jivan
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class EncodingTestSuite extends TestSuite {

    public static class TestObject extends StandardObject {
        private String data;
    }

    public EncodingTestSuite() {
        super(EncodingTestSuite.class.getName());
        addDriverTest(new Dom4JDriver());
        addDriverTest(new DomDriver());
        addDriverTest(new JDomDriver());
        addDriverTest(new StaxDriver());
        addDriverTest(new XppDomDriver());
        addDriverTest(new XppDriver());
        addDriverTest(new XomDriver());
    }

    private void test(HierarchicalStreamDriver driver, String encoding) throws IOException {
        String headerLine = encoding != null
            ? ("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n")
            : "";
        String xmlData = headerLine // force code format
            + "<test>\n"
            + "  <data>J\u00f6rg</data>\n"
            + "</test>";

        XStream xstream = new XStream(driver);
        xstream.alias("test", TestObject.class);
        TestObject obj = new TestObject();
        obj.data = "J\u00f6rg";

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        OutputStreamWriter writer = encoding != null
            ? new OutputStreamWriter(bos, encoding)
            : new OutputStreamWriter(bos);
        xstream.toXML(obj, writer);
        writer.close();

        String generated = encoding != null ? bos.toString(encoding) : bos.toString();
        Assert.assertTrue("'" + obj.data + "' was not found", generated.indexOf(obj.data) > 0);

        Object restored = xstream.fromXML(new ByteArrayInputStream(encoding != null ? xmlData
            .getBytes(encoding) : xmlData.getBytes()));
        Assert.assertEquals(obj, restored);
    }

    private void addDriverTest(final HierarchicalStreamDriver driver) {
        String testName = getShortName(driver);
        final String allEncodingTests = System.getProperty("xstream.test.encoding.all");
        if ("true".equals(allEncodingTests)) {
            // Native encoding normally fails on most systems!!
            addTest(new TestCase(testName + "Native") {
                protected void runTest() throws Throwable {
                    test(driver, null);
                }
            });
            // System encoding fails on US-ASCII systems, like Codehaus Bamboo
            final String systemEncoding = System.getProperty("file.encoding");
            addTest(new TestCase(testName + "With" + systemEncoding + "SystemEncoding") {
                protected void runTest() throws Throwable {
                    final Properties systemProperties = new Properties();
                    systemProperties.putAll(System.getProperties());
                    try {
                        // Use BEA reference implementation for StAX
                        // (Woodstox will fail on Windows because of unknown system encoding)
                        System.setProperty(
                            XMLInputFactory.class.getName(), MXParserFactory.class.getName());
                        test(driver, systemEncoding);
                    } finally {
                        System.setProperties(systemProperties);
                    }
                }
            });
        }
        addTest(new TestCase(testName + "WithUTF_8Encoding") {
            protected void runTest() throws Throwable {
                test(driver, "UTF-8");
            }
        });
        addTest(new TestCase(testName + "WithIS0_8859_1Encoding") {
            protected void runTest() throws Throwable {
                test(driver, "ISO-8859-1");
            }
        });
    }

    private String getShortName(HierarchicalStreamDriver driver) {
        String result = driver.getClass().getName();
        result = result.substring(result.lastIndexOf('.') + 1);
        return result;
    }

    public static Test suite() {
        return new EncodingTestSuite();
    }

}
