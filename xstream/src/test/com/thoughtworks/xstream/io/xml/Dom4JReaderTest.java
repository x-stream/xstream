/*
 * Copyright (C) 2004 Joe Walnes.
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

import java.io.StringReader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Dom4JReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return new Dom4JDriver().createReader(new StringReader(xml));
    }

    public void testCanReadFromElementOfLargerDocument() throws DocumentException {
        Document document = DocumentHelper.parseText("" +
                "<big>" +
                "  <small>" +
                "    <tiny/>" +
                "  </small>" +
                "  <small-two>" +
                "  </small-two>" +
                "</big>");
        Element small = document.getRootElement().element("small");

        HierarchicalStreamReader xmlReader = new Dom4JReader(small);
        assertEquals("small", xmlReader.getNodeName());
        xmlReader.moveDown();
        assertEquals("tiny", xmlReader.getNodeName());
    }

    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalGeneralEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (message.indexOf("DOCTYPE") < 0) {
                if (JVM.is17() || (JVM.is16() && message.indexOf("Nested exception: null") < 0)) {
                    throw e;
                }
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
                if (JVM.is17() || (JVM.is16() && message.indexOf("Nested exception: null") < 0)) {
                    throw e;
                }
            }
        }
    }

}
