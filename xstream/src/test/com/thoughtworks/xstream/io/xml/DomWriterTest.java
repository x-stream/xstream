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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;


public class DomWriterTest extends AbstractDocumentWriterTest<Element> {

    private Document document;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.newDocument();
        writer = new DomWriter(document);
    }

    @Override
    protected DocumentReader createDocumentReaderFor(final Element node) {
        return new DomReader(node);
    }

    // inherits tests from superclass

    public void testCanWriteIntoArbitraryNode() {
        final Element root = document.createElement("root");
        document.appendChild(root);
        final Element a = document.createElement("a");
        root.appendChild(a);
        writer = new DomWriter(a, document, new XmlFriendlyNameCoder());

        final XppDom xppRoot = new XppDom("root");
        final XppDom xppA = new XppDom("a");
        xppRoot.addChild(xppA);
        final XppDom xppB = new XppDom("b");
        xppA.addChild(xppB);
        xppB.setAttribute("attr", "foo");

        assertDocumentProducedIs(xppA, xppB);
        try (final XppDomWriter xppDomWriter = new XppDomWriter()) {
            new HierarchicalStreamCopier().copy(createDocumentReaderFor(document.getDocumentElement()), xppDomWriter);
            assertTrue(equals(xppRoot, xppDomWriter.getConfiguration()));
        }
    }

    public void testCanWriteIntoArbitraryNodeAgain() {
        final Element root = document.createElement("root");
        document.appendChild(root);
        final Element a = document.createElement("a");
        root.appendChild(a);
        writer = new DomWriter(a);

        final XppDom xppRoot = new XppDom("root");
        final XppDom xppA = new XppDom("a");
        xppRoot.addChild(xppA);
        final XppDom xppB = new XppDom("b");
        xppA.addChild(xppB);
        xppB.setAttribute("attr", "foo");

        assertDocumentProducedIs(xppA, xppB);
        try (final XppDomWriter xppDomWriter = new XppDomWriter()) {
            new HierarchicalStreamCopier().copy(createDocumentReaderFor(document.getDocumentElement()), xppDomWriter);
            assertTrue(equals(xppRoot, xppDomWriter.getConfiguration()));
        }
    }
}
