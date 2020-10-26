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

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;


/**
 * @author J&ouml;rg Schaible
 */
public abstract class AbstractStaxReaderTest extends AbstractXMLReaderTest {
    /**
     * Create the StaxDriver of the current implementation.
     *
     * @param qnameMap the namespace mapping
     * @return the new StAX driver
     */
    protected abstract StaxDriver createDriver(QNameMap qnameMap);

    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        return createReader(new QNameMap(), xml);
    }

    private final HierarchicalStreamReader createReader(final QNameMap qnameMap, final String xml) {
        return createDriver(qnameMap).createReader(new StringReader(xml));
    }

    public void testUsingDifferentNamespacesSameAliases() {
        final QNameMap qnameMap = new QNameMap();
        qnameMap.setDefaultNamespace("uri:outer");
        qnameMap.registerMapping(new QName("uri:inner:string", "item"), "aStr");
        qnameMap.registerMapping(new QName("uri:inner:int", "item"), "anInt");

        final String xml = "" //
            + "<handler xmlns=\"uri:outer\">"
            + /**/ "<item xmlns=\"uri:inner:string\">foo</item>"
            + /**/ "<item xmlns=\"uri:inner:int\">42</item>"
            + /**/ "<protocol>"
            + /**//**/ "<yField>YField</yField>"
            + /**/ "</protocol>"
            + "</handler>";

        try (final HierarchicalStreamReader xmlReader = createReader(qnameMap, xml)) {
            assertEquals("handler", xmlReader.getNodeName());
            xmlReader.moveDown();
            assertEquals("aStr", xmlReader.getNodeName());
            assertEquals("foo", xmlReader.getValue());
            xmlReader.moveUp();
            xmlReader.moveDown();
            assertEquals("anInt", xmlReader.getNodeName());
            assertEquals("42", xmlReader.getValue());
            xmlReader.moveUp();
            xmlReader.moveDown();
            assertEquals("protocol", xmlReader.getNodeName());
            xmlReader.moveDown();
            assertEquals("yField", xmlReader.getNodeName());
            assertEquals("YField", xmlReader.getValue());
            xmlReader.moveUp();
            xmlReader.moveUp();
            xmlReader.moveUp();
        }
    }

    public void testNamespacesAtRoot() {
        final QNameMap qnameMap = new QNameMap();
        qnameMap.registerMapping(new QName("uri:outer", "root"), "root");
        qnameMap.registerMapping(new QName("uri:inner:string", "item"), "aStr");
        qnameMap.registerMapping(new QName("uri:inner:int", "item"), "anInt");

        final String xml = "" //
            + "<root xmlns=\"uri:outer\" xmlns:s=\"uri:inner:string\" xmlns:i=\"uri:inner:int\">"
            + /**/ "<s:item>foo</s:item>"
            + /**/ "<i:item>42</i:item>"
            + /**/ "<y>?</y>"
            + "</root>";

        try (final HierarchicalStreamReader xmlReader = createReader(qnameMap, xml)) {
            assertEquals("root", xmlReader.getNodeName());
            xmlReader.moveDown();
            assertEquals("aStr", xmlReader.getNodeName());
            assertEquals("foo", xmlReader.getValue());
            xmlReader.moveUp();
            xmlReader.moveDown();
            assertEquals("anInt", xmlReader.getNodeName());
            assertEquals("42", xmlReader.getValue());
            xmlReader.moveUp();
            xmlReader.moveDown();
            assertEquals("y", xmlReader.getNodeName());
            assertEquals("?", xmlReader.getValue());
            xmlReader.moveUp();
            xmlReader.moveUp();
        }
    }

    public void testPrefixDefinitionsAreIgnoredWhenReadingXML() {
        final QNameMap qnameMap = new QNameMap();
        qnameMap.registerMapping(new QName("uri:outer", "root", "A"), "root");
        qnameMap.registerMapping(new QName("uri:inner:string", "item", "B"), "aStr");
        qnameMap.registerMapping(new QName("uri:inner:int", "item", "C"), "anInt");

        final String xml = "" //
            + "<root xmlns=\"uri:outer\" xmlns:s=\"uri:inner:string\" xmlns:i=\"uri:inner:int\">"
            + /**/ "<s:item>foo</s:item>"
            + /**/ "<i:item>42</i:item>"
            + /**/ "<y>?</y>"
            + "</root>";

        try (final HierarchicalStreamReader xmlReader = createReader(qnameMap, xml)) {
            assertEquals("root", xmlReader.getNodeName());
            xmlReader.moveDown();
            assertEquals("aStr", xmlReader.getNodeName());
            assertEquals("foo", xmlReader.getValue());
            xmlReader.moveUp();
            xmlReader.moveDown();
            assertEquals("anInt", xmlReader.getNodeName());
            assertEquals("42", xmlReader.getValue());
            xmlReader.moveUp();
            xmlReader.moveDown();
            assertEquals("y", xmlReader.getNodeName());
            assertEquals("?", xmlReader.getValue());
            xmlReader.moveUp();
            xmlReader.moveUp();
        }
    }
}
