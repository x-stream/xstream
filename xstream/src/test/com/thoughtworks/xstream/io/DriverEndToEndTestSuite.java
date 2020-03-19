/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2013, 2014, 2016, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;

import com.bea.xml.stream.MXParserFactory;
import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.io.binary.BinaryStreamDriver;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.*;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class DriverEndToEndTestSuite extends TestSuite {

    public static Test suite() {
        return new DriverEndToEndTestSuite();
    }

    public DriverEndToEndTestSuite() {
        super(DriverEndToEndTestSuite.class.getName());
        addDriverTest(new BEAStaxDriver());
        addDriverTest(new BinaryStreamDriver());
        addDriverTest(new Dom4JDriver());
        addDriverTest(new DomDriver());
        addDriverTest(new JDomDriver());
        addDriverTest(new JDom2Driver());
        addDriverTest(new KXml2DomDriver());
        addDriverTest(new KXml2Driver());
        addDriverTest(new StaxDriver());
        addDriverTest(new StandardStaxDriver());
        addDriverTest(new SimpleStaxDriver());
        addDriverTest(new WstxDriver());
        addDriverTest(new XomDriver());
        addDriverTest(new Xpp3DomDriver());
        addDriverTest(new Xpp3Driver());
        addDriverTest(new XppDomDriver());
        addDriverTest(new XppDriver());
        addDriverTest(new JettisonMappedXmlDriver());
    }

    private void testObject(final HierarchicalStreamDriver driver) {
        final XStream xstream = new XStream(driver);
        xstream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*objects.**");
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.registerConverter(new CollectionConverter(xstream.getMapper()) {

            @Override
            public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
                if (reader.peekNextChild() == null) {
                    return new ArrayList<Object>();
                }
                return super.unmarshal(reader, context);
            }

        });

        final SampleLists<String, Boolean> in = new SampleLists<String, Boolean>();
        in.good.add("one");
        in.good.add("two");
        in.good.add("three");
        in.bad.add(Boolean.TRUE);
        in.bad.add(Boolean.FALSE);
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        xstream.toXML(in, buffer);
        final Object out = xstream.fromXML(new ByteArrayInputStream(buffer.toByteArray()));

        Assert.assertEquals(in, out);
    }

    private void testStream(final HierarchicalStreamDriver driver) {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final HierarchicalStreamWriter writer = driver.createWriter(buffer);
        writer.startNode("root");
        writer.startNode("child1");
        writer.startNode("baby");
        writer.endNode();
        writer.endNode();
        writer.startNode("child2");
        writer.addAttribute("A", "a");
        writer.setValue("v");
        writer.endNode();
        writer.endNode();
        writer.close();

        final HierarchicalStreamReader reader = driver.createReader(new ByteArrayInputStream(buffer.toByteArray()));
        Assert.assertEquals("root", reader.getNodeName());
        Assert.assertTrue(reader.hasMoreChildren());
        reader.moveDown();
        Assert.assertEquals("child1", reader.getNodeName());
        Assert.assertEquals(0, reader.getAttributeCount());
        Assert.assertNull(reader.getAttribute("foo"));
        Assert.assertTrue(reader.hasMoreChildren());
        reader.moveUp();
        Assert.assertTrue(reader.hasMoreChildren());
        reader.moveDown();
        Assert.assertEquals("child2", reader.getNodeName());
        Assert.assertEquals(1, reader.getAttributeCount());
        Assert.assertEquals("a", reader.getAttribute("A"));
        Assert.assertNull(reader.getAttribute("foo"));
        // Assert.assertNull(reader.getAttribute(1));
        // Assert.assertNull(reader.getAttributeName(1));
        Assert.assertFalse(reader.hasMoreChildren());
        reader.moveUp();
        Assert.assertFalse(reader.hasMoreChildren());
        reader.close();
    }

    static class Phone {
        String name;
        int number;
    }

    private void testDriverFromFile(final HierarchicalStreamDriver driver, final File file) throws Exception {
        final XStream xStream = new XStream(driver);
        xStream.alias("phone", Phone.class);
        xStream.allowTypesByWildcard(this.getClass().getName() + "$*");

        final Phone phone = xStream.fromXML(file);
        Assert.assertEquals("apple", phone.name);
        Assert.assertEquals(20200317, phone.number);
    }

    private void testDriverFromURL(final HierarchicalStreamDriver driver, final URL url, final String expect) {
        final XStream xStream = new XStream(driver);
        xStream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xStream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*Object.**");
        xStream.alias("url", URL.class);
        String result = xStream.toXML(url);
        // Coding questions not in the scope of this use case test, igone for now
        Assert.assertEquals(replaceEncodeAndEscape(expect), replaceEncodeAndEscape(result));

        final URL resultURL= xStream.fromXML(result);
        Assert.assertEquals(url, resultURL);
    }

    private void testBinaryStreamDriverFromURL(final HierarchicalStreamDriver driver, final URL url) {
        final XStream xStream = new XStream(driver);
        xStream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xStream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*Object.**");
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        xStream.toXML(url, buff);

        final URL resultURL= xStream.fromXML(new ByteArrayInputStream(buff.toByteArray()));
        Assert.assertEquals(url, resultURL);
    }

    private void addDriverTest(final HierarchicalStreamDriver driver) {
        final String testName = getShortName(driver);
        addTest(new TestCase(testName + "_Object") {
            @Override
            protected void runTest() throws Throwable {
                testObject(driver);
            }
        });
        addTest(new TestCase(testName + "_Stream") {
            @Override
            protected void runTest() throws Throwable {
                testStream(driver);
            }
        });
        addTest(new TestCase(testName + "_File") {
            @Override
            protected void runTest() throws Throwable {
                if(driver instanceof BEAStaxDriver || driver instanceof BinaryStreamDriver
                        || (driver instanceof StaxDriver && ((StaxDriver)driver).getInputFactory() instanceof MXParserFactory)) {
                    // igone for now
                } else if(driver instanceof JettisonMappedXmlDriver) {
                    testDriverFromFile(driver, createTestJsonFile());
                } else {
                    testDriverFromFile(driver, createTestFile());
                }
            }
        });

        addTest(new TestCase(testName + "_URL") {
            @Override
            protected void runTest() throws Throwable {
                runDriverFromURLTest(driver, new URL("http://x-stream.github.io"), "<url>http://x-stream.github.io</url>");
                runDriverFromURLTest(driver, new URL("file:/c:/winnt/blah.txt"), "<url>file:/c:/winnt/blah.txt</url>");
            }
        });
    }

    private void runDriverFromURLTest(final HierarchicalStreamDriver driver, final URL url, final String expect) {
        if (driver instanceof BinaryStreamDriver) {
            testBinaryStreamDriverFromURL(driver, url);
        } else if (driver instanceof BEAStaxDriver) {
            testDriverFromURL(driver, url, "<?xml version='1.0' encoding='utf-8'?>" + expect);
        } else if (driver instanceof StandardStaxDriver) {
            testDriverFromURL(driver, url, "<?xml version=\"1.0\" ?>" + expect);
        } else if (driver instanceof WstxDriver || driver instanceof StaxDriver) {
            testDriverFromURL(driver, url, "<?xml version='1.0' encoding='UTF-8'?>" + expect);
        } else if (driver instanceof Dom4JDriver) {
            testDriverFromURL(driver, url, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" + expect);
        } else if (driver instanceof JettisonMappedXmlDriver) {
            final String expectJson = "<url>http://x-stream.github.io</url>".equals(expect)
                    ? "{\"url\":\"http:\\/\\/x-stream.github.io\"}"
                    : "{\"url\":\"file:\\/c:\\/winnt\\/blah.txt\"}";
            testDriverFromURL(driver, url, expectJson);
        } else {
            testDriverFromURL(driver, url, expect);
        }
    }

    private String replaceEncodeAndEscape(String str){
        return str.replace("utf-8","UTF-8").replace("\\","");
    }

    private String getShortName(final HierarchicalStreamDriver driver) {
        String result = driver.getClass().getName();
        result = result.substring(result.lastIndexOf('.') + 1);
        return result;
    }

    private File createTestFile() throws Exception {
        final String xml = "" //
                + "<phone>\n"
                + "  <name>apple</name>\n"
                + "  <number>20200317</number>\n"
                + "</phone>";

        final File dir = new File("target/test-data");
        dir.mkdirs();
        final File file = new File(dir, "test.xml");
        final FileOutputStream fos = new FileOutputStream(file);
        fos.write(xml.getBytes("UTF-8"));
        fos.close();
        return file;
    }

    private File createTestJsonFile() throws Exception {
        final String json = "{'phone':{'name':'apple','number':20200317}}".replace('\'','"');

        final File dir = new File("target/test-data");
        dir.mkdirs();
        final File file = new File(dir, "test.json");
        final FileOutputStream fos = new FileOutputStream(file);
        fos.write(json.getBytes("UTF-8"));
        fos.close();
        return file;
    }

}
