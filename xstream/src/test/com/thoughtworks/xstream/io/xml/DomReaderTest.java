/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015, 2016, 2017, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class DomReaderTest extends AbstractXMLReaderTest {

    // factory method
    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        final String prefix = getName().endsWith("ISOControlCharactersInValue") ? XML_1_1_HEADER : "";
        return new DomDriver().createReader(new StringReader(prefix + xml));
    }

    @Override
    protected String getSpecialCharsInJavaNamesForXml10() {
        return super.getSpecialCharsInJavaNamesForXml10_4th();
    }

    private Document buildDocument(final String xml) throws Exception {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        final Document document = documentBuilder.parse(inputStream);
        return document;
    }

    public void testCanReadFromElementOfLargerDocument() throws Exception {
        final Document document = buildDocument(""
            + "<big>"
            + "  <small>"
            + "    <tiny/>"
            + "  </small>"
            + "  <small-two>"
            + "  </small-two>"
            + "</big>");
        final Element small = (Element)document.getDocumentElement().getElementsByTagName("small").item(0);

        try (final HierarchicalStreamReader xmlReader = new DomReader(small)) {
            assertEquals("small", xmlReader.getNodeName());
            xmlReader.moveDown();
            assertEquals("tiny", xmlReader.getNodeName());
        }
    }

    @Override
    public void testExposesAttributesKeysAndValuesByIndex() throws Exception {

        // overrides test in superclass, because DOM does not retain order of actualAttributes.

        try (final HierarchicalStreamReader xmlReader = createReader(
            "<node hello='world' a='b' c='d'><empty/></node>")) {

            assertEquals(3, xmlReader.getAttributeCount());

            final Map<String, String> expectedAttributes = new HashMap<>();
            expectedAttributes.put("hello", "world");
            expectedAttributes.put("a", "b");
            expectedAttributes.put("c", "d");

            final Map<String, String> actualAttributes = new HashMap<>();
            for (int i = 0; i < xmlReader.getAttributeCount(); i++) {
                final String name = xmlReader.getAttributeName(i);
                final String value = xmlReader.getAttribute(i);
                actualAttributes.put(name, value);
            }

            assertEquals(expectedAttributes, actualAttributes);

            xmlReader.moveDown();
            assertEquals("empty", xmlReader.getNodeName());
            assertEquals(0, xmlReader.getAttributeCount());
        }
    }

    @Override
    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalGeneralEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (!message.contains("DOCTYPE")) {
                throw e;
            }
        }
    }

    @Override
    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalParameterEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (!message.contains("DOCTYPE")) {
                throw e;
            }
        }
    }

    @Override
    public void testNullCharacterInValue() throws Exception {
        // not possible, null value is invalid in XML
    }

    @Override
    public void testNonUnicodeCharacterInValue() throws Exception {
        // not possible, character is invalid in XML
    }

    @Override
    public void testNonUnicodeCharacterInCDATA() throws Exception {
        // not possible, character is invalid in XML
    }

    // inherits tests from superclass
}
