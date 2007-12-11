/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. April 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.io;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.JDomDriver;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.XomDriver;
import com.thoughtworks.xstream.io.xml.XppDomDriver;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class DriverEndToEndTestSuite extends TestSuite {

    public static Test suite() {
        return new DriverEndToEndTestSuite();
    }

    public DriverEndToEndTestSuite() {
        super(DriverEndToEndTestSuite.class.getName());
        addDriverTest(new Dom4JDriver());
        addDriverTest(new DomDriver());
        addDriverTest(new JDomDriver());
        addDriverTest(new StaxDriver());
        addDriverTest(new XppDomDriver());
        addDriverTest(new XppDriver());
        addDriverTest(new XomDriver());
        if (JVM.is14()) {
            JVM jvm = new JVM();
            Class driverType = jvm.loadClass("com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver");
            try {
                addDriverTest((HierarchicalStreamDriver)driverType.newInstance());
            } catch (InstantiationException e) {
                throw new AssertionFailedError("Cannot instantiate " + driverType.getName());
            } catch (IllegalAccessException e) {
                throw new AssertionFailedError("Cannot access default constructor of " + driverType.getName());
            }
        }
    }

    private void test(HierarchicalStreamDriver driver) {
        XStream xstream = new XStream(driver);

        Object in = new Software("some vendor", "some name");
        String xml = xstream.toXML(in);
        Object out = xstream.fromXML(xml);

        Assert.assertEquals(in, out);
    }

    private void addDriverTest(final HierarchicalStreamDriver driver) {
        String testName = getShortName(driver);
        addTest(new TestCase(testName) {
            protected void runTest() throws Throwable {
                test(driver);
            }
        });
    }

    private String getShortName(HierarchicalStreamDriver driver) {
        String result = driver.getClass().getName();
        result = result.substring(result.lastIndexOf('.') + 1);
        return result;
    }

}
