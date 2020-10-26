/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        // kXml2 fails to replace tab characters in attributes to space as required by XML spec
        if (xml.indexOf('\t') >= 0) {
            xml = xml.replace('\t', ' ');
        }
        return new KXml2DomDriver().createReader(new StringReader(xml));
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

        final XppDom document = XppFactory.buildDom(xml);
        final XppDom small = document.getChild("small");

        try (final HierarchicalStreamReader xmlReader = new XppDomReader(small)) {
            assertEquals("small", xmlReader.getNodeName());
            xmlReader.moveDown();
            assertEquals("tiny", xmlReader.getNodeName());
        }
    }

    @Override
    public void testExposesAttributesKeysAndValuesByIndex() throws Exception {

        // overrides test in superclass, because XppDom does not retain order of actualAttributes.

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
            if (!message.contains("resolve")) {
                throw e;
            }
        }
    }

    // inherits tests from superclass
}
