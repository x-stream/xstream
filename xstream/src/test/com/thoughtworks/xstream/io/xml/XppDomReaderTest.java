/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;
import com.thoughtworks.xstream.io.xml.xppdom.XppFactory;


public class XppDomReaderTest extends AbstractXMLReaderTest {
    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        return new Xpp3DomDriver().createReader(new StringReader(xml));
    }

    public void testCanReadFromElementOfLargerDocument() throws Exception {
        final String xml = "<big>"
            + "  <small>"
            + "    <tiny/>"
            + "  </small>"
            + "  <small-two>"
            + "  </small-two>"
            + "</big>";

        final XppDom document = XppFactory.buildDom(xml);

        final XppDom small = document.getChild("small");

        final HierarchicalStreamReader xmlReader = new XppDomReader(small);

        assertEquals("small", xmlReader.getNodeName());

        xmlReader.moveDown();

        assertEquals("tiny", xmlReader.getNodeName());
    }

    @Override
    public void testExposesAttributesKeysAndValuesByIndex() throws Exception {

        // overrides test in superclass, because XppDom does not retain order of actualAttributes.

        final HierarchicalStreamReader xmlReader = createReader("<node hello='world' a='b' c='d'><empty/></node>");

        assertEquals(3, xmlReader.getAttributeCount());

        final Map expectedAttributes = new HashMap();
        expectedAttributes.put("hello", "world");
        expectedAttributes.put("a", "b");
        expectedAttributes.put("c", "d");

        final Map actualAttributes = new HashMap();
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

    @Override
    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalGeneralEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (!message.contains("resolve entity")) {
                throw e;
            }
        }
    }

}
