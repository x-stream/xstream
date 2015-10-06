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

import java.io.StringReader;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;


public class XomReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        return new XomDriver().createReader(new StringReader(xml));
    }

    public void testCanReadFromElementOfLargerDocument() throws Exception {
        final String xml = ""
            + "<big>"
            + "  <small>"
            + "    <tiny/>"
            + "  </small>"
            + "  <small-two>"
            + "  </small-two>"
            + "</big>";
        final Document document = new Builder().build(new StringReader(xml));
        final Element element = document.getRootElement().getFirstChildElement("small");

        final HierarchicalStreamReader xmlReader = new XomReader(element);
        assertEquals("small", xmlReader.getNodeName());
        xmlReader.moveDown();
        assertEquals("tiny", xmlReader.getNodeName());
    }

    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        // No possibility to suppress support for external entities in XOM?
        // super.testIsXXEVulnerableWithExternalGeneralEntity();
    }

    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        // No possibility to suppress support for external entities in XOM?
        // super.testIsXXEVulnerableWithExternalParameterEntity();
    }

    // inherits tests from superclass

}
