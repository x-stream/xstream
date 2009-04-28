/*
 * Copyright (C) 2004, 2005      Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. October 2004 by James Strachan
 */
package com.thoughtworks.acceptance;

import com.bea.xml.stream.MXParserFactory;
import com.bea.xml.stream.XMLOutputFactoryBase;
import com.thoughtworks.acceptance.someobjects.Handler;
import com.thoughtworks.acceptance.someobjects.Protocol;
import com.thoughtworks.acceptance.someobjects.WithList;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import java.util.ArrayList;

public class QNameMappedConcreteClassesTest extends AbstractAcceptanceTest {

    public static final String XML_HEADER = "<?xml version='1.0' encoding='utf-8'?>";

    protected QNameMap qnameMap;
    protected String namespace = getDefaultNS(WithList.class);

    public void testUsingNamespace() {
        // lets register some qnames
        QName qname = new QName(namespace, "withList", "w");
        qnameMap.registerMapping(qname, WithList.class);

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected ="" +
                XML_HEADER +
                "<w:withList xmlns:w=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<things></things>" +
                "</w:withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDefaultNamespace() {
        qnameMap.setDefaultNamespace(namespace);
        xstream.alias("withList", WithList.class);

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                XML_HEADER +
                "<withList xmlns=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<things></things>" +
                "</withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDefaultNamespaceAndPrefix() {
        qnameMap.setDefaultNamespace(namespace);
        qnameMap.setDefaultPrefix("x");
        QName qname = new QName(namespace, "withList", "x");
        qnameMap.registerMapping(qname, WithList.class);

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                XML_HEADER +
                "<x:withList xmlns:x=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<x:things></x:things>" +
                "</x:withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDifferentNamespaces() {
        // lets register some qnames
        qnameMap.registerMapping(new QName(namespace, "withList", "w"), WithList.class);
        qnameMap.registerMapping(new QName("urn:foo", "things", "f"), "things");

        WithList withList = new WithList();
        withList.things = new ArrayList();

        String expected =
                XML_HEADER +
                "<w:withList xmlns:w=\"java://com.thoughtworks.acceptance.someobjects\">" +
                "<f:things xmlns:f=\"urn:foo\"></f:things>" +
                "</w:withList>";

        assertBothWays(withList, expected);
    }

    public void testUsingDifferentNamespacesWithAliases() {
        xstream.alias("handler", X.class);
        xstream.alias("protocol", Y.class);

        qnameMap.registerMapping(new QName(getDefaultNS(Handler.class)+1, "handler", "h"), "handler");
        qnameMap.registerMapping(new QName(getDefaultNS(Protocol.class)+2, "protocol", "p"), "innerObj");

        X x = new X();
        x.aStr = "foo";
        x.anInt = 42;
        x.innerObj = new Y();
        x.innerObj.yField = "YField";

        String expected =
                XML_HEADER +
                "<h:handler xmlns:h=\"java://com.thoughtworks.acceptance.someobjects1\">" +
                "<aStr>foo</aStr>" +
                "<anInt>42</anInt>" +
                "<p:protocol xmlns:p=\"java://com.thoughtworks.acceptance.someobjects2\">" +
                "<yField>YField</yField>" +
                "</p:protocol>" +
                "</h:handler>";

        assertBothWays(x, expected);
    }

    protected HierarchicalStreamDriver createDriver() {
        System.setProperty(XMLInputFactory.class.getName(), MXParserFactory.class.getName());
        System.setProperty(XMLOutputFactory.class.getName(), XMLOutputFactoryBase.class.getName());
        // careful, called from inside base class constructor
        qnameMap = new QNameMap();
        StaxDriver driver = new StaxDriver(qnameMap);
        driver.setRepairingNamespace(false);
        return driver;
    }

    protected String getDefaultNS(Class type) {
        return "java://" + type.getPackage().getName();
    }
}
