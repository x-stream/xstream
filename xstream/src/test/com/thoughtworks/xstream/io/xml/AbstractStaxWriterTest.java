/*
 * Copyright (C) 2007 XStream Committers.
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
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.oro.text.perl.Perl5Util;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.io.StringWriter;

public abstract class AbstractStaxWriterTest extends AbstractXMLWriterTest {

    protected StringWriter buffer;
    protected Perl5Util perlUtil;
    protected XMLOutputFactory outputFactory;
    private X testInput;

    protected static Test createSuite(Class test, String staxClassName) {
        try {
            Class.forName(staxClassName);
            return new TestSuite(test);
        } catch (ClassNotFoundException e) {
            return new TestCase(test.getName() + ": not available") {

                public int countTestCases() {
                    return 1;
                }

                public void run(TestResult result) {
                }
            };
        }
    }
    
    protected abstract String getXMLHeader();

    protected abstract XMLOutputFactory getOutputFactory();

    protected void setUp() throws Exception {
        super.setUp();
        outputFactory = getOutputFactory();
        outputFactory.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, Boolean.FALSE);
        buffer = new StringWriter();
        writer = new StaxWriter(new QNameMap(), outputFactory.createXMLStreamWriter(buffer));
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

    protected void marshalWithBothRepairingModes(QNameMap qnameMap, String expected)
                                                                                    throws XMLStreamException {
        marshall(qnameMap, true);
        assertXmlProducedIs(expected);

        marshall(qnameMap, false);
        assertXmlProducedIs(expected);
    }

    protected void marshall(QNameMap qnameMap, boolean repairNamespaceMode)
                                                                             throws XMLStreamException {
        outputFactory.setProperty(
            XMLOutputFactory.IS_REPAIRING_NAMESPACES, repairNamespaceMode
                ? Boolean.TRUE
                : Boolean.FALSE);
        XStream xstream = new XStream((HierarchicalStreamDriver)null);
        buffer = new StringWriter();
        XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter(buffer);
        xstream.marshal(testInput, new StaxWriter(qnameMap, xmlStreamWriter));
    }

}