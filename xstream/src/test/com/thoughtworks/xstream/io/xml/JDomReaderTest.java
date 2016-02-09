/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.StringReader;

public class JDomReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return new JDomDriver().createReader(new StringReader(xml));
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
        Document document = new SAXBuilder().build(new StringReader(xml));
        Element element = document.getRootElement().getChild("small");

        HierarchicalStreamReader xmlReader = new JDomReader(element);
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
                throw e;
            }
        } catch (final NullPointerException e) {
            // NPE only with Sun Java 1.6 runtime
            if (JVM.is17()) {
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
                throw e;
            }
        } catch (final NullPointerException e) {
            // NPE only with Sun Java 1.6 runtime
            if (JVM.is17()) {
                throw e;
            }
        }
    }

    // inherits tests from superclass

}
