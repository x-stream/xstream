/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3DomBuilder;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class XppDomReaderTest extends AbstractXMLReaderTest {
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return new XppDomDriver().createReader(new StringReader(xml));
    }

    public void testCanReadFromElementOfLargerDocument()
            throws Exception {
        String xml =
                "<big>" +
                "  <small>" +
                "    <tiny/>" +
                "  </small>" +
                "  <small-two>" +
                "  </small-two>" +
                "</big>";

        XppDom document = Xpp3DomBuilder.build(new StringReader(xml));

        XppDom small = document.getChild("small");

        HierarchicalStreamReader xmlReader = new XppDomReader(small);

        assertEquals("small", xmlReader.getNodeName());

        xmlReader.moveDown();

        assertEquals("tiny", xmlReader.getNodeName());
    }

    public void testExposesAttributesKeysAndValuesByIndex() throws Exception {

        // overrides test in superclass, because XppDom does not retain order of actualAttributes.

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

}
