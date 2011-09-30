/*
 * Copyright (C) 2007, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. November 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;

import org.apache.oro.text.perl.Perl5Util;

import javax.xml.namespace.QName;

import java.io.StringWriter;

public abstract class AbstractStaxWriterTest extends AbstractXMLWriterTest {

    protected StringWriter buffer;
    protected Perl5Util perlUtil;
    protected StaxDriver staxDriver;
    private X testInput;
    
    protected abstract String getXMLHeader();

    protected abstract StaxDriver getStaxDriver();

    protected void setUp() throws Exception {
        super.setUp();
        staxDriver = getStaxDriver();
        staxDriver.setRepairingNamespace(false);
        buffer = new StringWriter();
        writer = staxDriver.createWriter(buffer);
        perlUtil = new Perl5Util();

        testInput = new X();
        testInput.anInt = 9;
        testInput.aStr = "zzz";
        testInput.innerObj = new Y();
        testInput.innerObj.yField = "ooo";
    }

    public void testNamespacedXmlWithPrefix() throws Exception {
        QNameMap qnameMap = new QNameMap();
        QName qname = new QName("http://foo.com", "alias", "foo");
        qnameMap.registerMapping(qname, X.class);

        String expected = "<foo:alias xmlns:foo=\"http://foo.com\"><aStr xmlns=\"\">zzz</aStr><anInt xmlns=\"\">9</anInt><innerObj xmlns=\"\"><yField>ooo</yField></innerObj></foo:alias>";
        marshalWithBothRepairingModes(qnameMap, expected);
    }

    public void testNamespacedXmlWithoutPrefix() throws Exception {
        QNameMap qnameMap = new QNameMap();
        QName qname = new QName("http://foo.com", "bar");
        qnameMap.registerMapping(qname, X.class);

        String expected = "<bar xmlns=\"http://foo.com\"><aStr xmlns=\"\">zzz</aStr><anInt xmlns=\"\">9</anInt><innerObj xmlns=\"\"><yField>ooo</yField></innerObj></bar>";
        marshalWithBothRepairingModes(qnameMap, expected);
    }

    public void testNamespacedXmlWithPrefixTwice() throws Exception {
        QNameMap qnameMap = new QNameMap();
        QName qname = new QName("http://foo.com", "alias", "foo");
        qnameMap.registerMapping(qname, X.class);

        qname = new QName("http://bar.com", "alias1", "bar");
        qnameMap.registerMapping(qname, "aStr");

        qname = new QName("http://bar.com", "alias2", "bar");
        qnameMap.registerMapping(qname, "anInt");

        String expected = "<foo:alias xmlns:foo=\"http://foo.com\"><bar:alias1 xmlns:bar=\"http://bar.com\">zzz</bar:alias1><bar:alias2 xmlns:bar=\"http://bar.com\">9</bar:alias2><innerObj xmlns=\"\"><yField>ooo</yField></innerObj></foo:alias>";
        marshalWithBothRepairingModes(qnameMap, expected);
    }

    protected void marshalWithBothRepairingModes(QNameMap qnameMap, String expected) {
        marshalNonRepairing(qnameMap, expected);
        marshalRepairing(qnameMap, expected);
    }

    protected void marshalRepairing(QNameMap qnameMap, String expected) {
        marshall(qnameMap, true);
        assertXmlProducedIs(expected);
    }

    protected void marshalNonRepairing(QNameMap qnameMap, String expected) {
        marshall(qnameMap, false);
        assertXmlProducedIs(expected);
    }

    protected void marshall(QNameMap qnameMap, boolean repairNamespaceMode) {
        staxDriver.setRepairingNamespace(repairNamespaceMode);
        staxDriver.setQnameMap(qnameMap);
        XStream xstream = new XStream(staxDriver);
        buffer = new StringWriter();
        xstream.toXML(testInput, buffer);
    }

}