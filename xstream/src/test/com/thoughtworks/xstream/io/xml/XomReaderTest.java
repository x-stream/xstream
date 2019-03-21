/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015, 2018, 2019 XStream Committers.
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

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;


public class XomReaderTest extends AbstractXMLReaderTest {

    // factory method
    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        return new XomDriver().createReader(new StringReader(xml));
    }
    
    @Override
    protected String getSpecialCharsInJavaNamesForXml10() {
        return super.getSpecialCharsInJavaNamesForXml10_4th();
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

        try (final HierarchicalStreamReader xmlReader = new XomReader(element)) {
            assertEquals("small", xmlReader.getNodeName());
            xmlReader.moveDown();
            assertEquals("tiny", xmlReader.getNodeName());
        }
    }

    @Override
    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        // No possibility to suppress support for external entities in XOM?
        // super.testIsXXEVulnerableWithExternalGeneralEntity();
    }

    @Override
    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        // No possibility to suppress support for external entities in XOM?
        // super.testIsXXEVulnerableWithExternalParameterEntity();
    }

    @Override
    public void testNullCharacterInValue() throws Exception {
        // Is not possible, null value is invalid in XML
    }
    
    @Override
    public void testSupportsFieldsWithSpecialCharsInXml11() throws Exception {
        // no support for XML 1.1
    }

    @Override
    public void testNonUnicodeCharacterInValue() throws Exception {
        // not possible, character is invalid in XML
    }

    @Override
    public void testNonUnicodeCharacterInCDATA() throws Exception {
        // not possible, character is invalid in XML
    }
    
    @Override
    public void testISOControlCharactersInValue() throws Exception {
        // not possible, only supported in XML 1.1
    }

    // inherits tests from superclass
}
