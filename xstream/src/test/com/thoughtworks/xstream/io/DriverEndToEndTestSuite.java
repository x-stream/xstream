/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2013, 2016, 2017, 2021 XStream Committers.
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
import java.util.ArrayList;

import com.thoughtworks.acceptance.objects.SampleLists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.binary.BinaryStreamDriver;
import com.thoughtworks.xstream.io.xml.BEAStaxDriver;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.JDomDriver;
import com.thoughtworks.xstream.io.xml.KXml2DomDriver;
import com.thoughtworks.xstream.io.xml.KXml2Driver;
import com.thoughtworks.xstream.io.xml.MXParserDomDriver;
import com.thoughtworks.xstream.io.xml.MXParserDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.WstxDriver;
import com.thoughtworks.xstream.io.xml.XomDriver;
import com.thoughtworks.xstream.io.xml.Xpp3DomDriver;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import com.thoughtworks.xstream.io.xml.XppDomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
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
        if (JVM.is15()) {
            final Class driverType = JVM.loadClassForName("com.thoughtworks.xstream.io.xml.JDom2Driver");
            try {
                addDriverTest((HierarchicalStreamDriver)driverType.newInstance());
            } catch (final InstantiationException e) {
                throw new AssertionFailedError("Cannot instantiate " + driverType.getName());
            } catch (final IllegalAccessException e) {
                throw new AssertionFailedError("Cannot access default constructor of " + driverType.getName());
            }
        }
        addDriverTest(new KXml2DomDriver());
        addDriverTest(new KXml2Driver());
        addDriverTest(new StaxDriver());
        if (JVM.is16()) {
            final Class driverType = JVM.loadClassForName("com.thoughtworks.xstream.io.xml.StandardStaxDriver");
            try {
                addDriverTest((HierarchicalStreamDriver)driverType.newInstance());
            } catch (final InstantiationException e) {
                throw new AssertionFailedError("Cannot instantiate " + driverType.getName());
            } catch (final IllegalAccessException e) {
                throw new AssertionFailedError("Cannot access default constructor of " + driverType.getName());
            }
        }
        addDriverTest(new WstxDriver());
        addDriverTest(new XomDriver());
        addDriverTest(new MXParserDomDriver());
        addDriverTest(new MXParserDriver());
        addDriverTest(new Xpp3DomDriver());
        addDriverTest(new Xpp3Driver());
        addDriverTest(new XppDomDriver());
        addDriverTest(new XppDriver());
        if (JVM.is14()) {
            final Class driverType = JVM.loadClassForName("com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver");
            try {
                addDriverTest((HierarchicalStreamDriver)driverType.newInstance());
            } catch (final InstantiationException e) {
                throw new AssertionFailedError("Cannot instantiate " + driverType.getName());
            } catch (final IllegalAccessException e) {
                throw new AssertionFailedError("Cannot access default constructor of " + driverType.getName());
            }
        }
    }

    private void testObject(final HierarchicalStreamDriver driver) {
        final XStream xstream = new XStream(driver);
        xstream.setupDefaultSecurity(xstream);
        xstream.allowTypes(new Class[] { SampleLists.class });
        xstream.registerConverter(new CollectionConverter(xstream.getMapper()) {

            public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
                final ExtendedHierarchicalStreamReader exReader = (ExtendedHierarchicalStreamReader)reader;
                if (exReader.peekNextChild() == null) {
                    return new ArrayList();
                }
                return super.unmarshal(reader, context);
            }

        });

        final SampleLists in = new SampleLists();
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
        //Assert.assertNull(reader.getAttribute(1));
        Assert.assertFalse(reader.hasMoreChildren());
        reader.moveUp();
        Assert.assertFalse(reader.hasMoreChildren());
        reader.close();
    }

    private void addDriverTest(final HierarchicalStreamDriver driver) {
        final String testName = getShortName(driver);
        addTest(new TestCase(testName + "_Object") {
            protected void runTest() throws Throwable {
                testObject(driver);
            }
        });
        addTest(new TestCase(testName + "_Stream") {
            protected void runTest() throws Throwable {
                testStream(driver);
            }
        });
    }

    private String getShortName(final HierarchicalStreamDriver driver) {
        String result = driver.getClass().getName();
        result = result.substring(result.lastIndexOf('.') + 1);
        return result;
    }
}
