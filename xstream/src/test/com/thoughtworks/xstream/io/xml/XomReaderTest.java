/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;

import java.io.StringReader;
import java.net.UnknownHostException;

public class XomReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return new XomDriver().createReader(new StringReader(xml));
    }

    public void testCanReadFromElementOfLargerDocument() throws Exception {
        String xml ="" +
                "<big>" +
                "  <small>" +
                "    <tiny/>" +
                "  </small>" +
                "  <small-two>" +
                "  </small-two>" +
                "</big>";
        Document document = new Builder().build(new StringReader(xml));
        Element element = document.getRootElement().getFirstChildElement("small");

        HierarchicalStreamReader xmlReader = new XomReader(element);
        assertEquals("small", xmlReader.getNodeName());
        xmlReader.moveDown();
        assertEquals("tiny", xmlReader.getNodeName());
    }

    @Override
    public void testIsXXEVulnerable() throws Exception {
        try {
            super.testIsXXEVulnerable();
            fail("Thrown " + UnknownHostException.class.getName() + " expected");
        } catch (final UnknownHostException e) {
            final String message = e.getMessage();
            if (message.contains("file")) {
                throw e;
            }
        }
    }

    // inherits tests from superclass

}
