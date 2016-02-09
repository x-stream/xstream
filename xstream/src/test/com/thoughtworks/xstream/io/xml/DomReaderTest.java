/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class DomReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return new DomDriver().createReader(new StringReader(xml));
    }

    private Document buildDocument(String xml) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Document document = documentBuilder.parse(inputStream);
        return document;
    }

    public void testCanReadFromElementOfLargerDocument() throws Exception {
        Document document = buildDocument("" +
                "<big>" +
                "  <small>" +
                "    <tiny/>" +
                "  </small>" +
                "  <small-two>" +
                "  </small-two>" +
                "</big>");
        Element small = (Element) document.getDocumentElement().getElementsByTagName("small").item(0);

        HierarchicalStreamReader xmlReader = new DomReader(small);
        assertEquals("small", xmlReader.getNodeName());
        xmlReader.moveDown();
        assertEquals("tiny", xmlReader.getNodeName());
    }

    public void testExposesAttributesKeysAndValuesByIndex() throws Exception {

        // overrides test in superclass, because DOM does not retain order of actualAttributes.

        HierarchicalStreamReader xmlReader = createReader("<node hello='world' a='b' c='d'><empty/></node>");

        assertEquals(3, xmlReader.getAttributeCount());

        Map expectedAttributes = new HashMap();
        expectedAttributes.put("hello", "world");
        expectedAttributes.put("a", "b");
        expectedAttributes.put("c", "d");

        Map actualAttributes = new HashMap();
        for (int i = 0; i < xmlReader.getAttributeCount(); i++) {
            String name = xmlReader.getAttributeName(i);
            String value = xmlReader.getAttribute(i);
            actualAttributes.put(name, value);
        }

        assertEquals(expectedAttributes, actualAttributes);

        xmlReader.moveDown();
        assertEquals("empty", xmlReader.getNodeName());
        assertEquals(0, xmlReader.getAttributeCount());
    }

    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalGeneralEntity();
            if (JVM.is16()) {
                fail("Thrown " + XStreamException.class.getName() + " expected");
            }
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (message.indexOf("DOCTYPE") < 0) {
                throw e;
            }
        } catch (final NullPointerException e) {
            // NPE only with Sun Java 1.6 runtime
            if (JVM.is17() || !JVM.is16()) {
                throw e;
            }
        }
    }

    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalParameterEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (message.indexOf("DOCTYPE") < 0) {
                // XXE vulnerable with Sun Java 1.6 runtime
                if (JVM.is16()) {
                    throw e;
                } else {
                    System.err.println("DomReader is vulnerable with Java 5 and 1.4!");
                }
            }
        } catch (final NullPointerException e) {
            // NPE only with Sun Java 1.6 runtime
            if (JVM.is17() || !JVM.is16()) {
                throw e;
            }
        }
    }
}
