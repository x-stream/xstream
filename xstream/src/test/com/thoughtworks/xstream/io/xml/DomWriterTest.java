/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 05. September 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;

public class DomWriterTest extends AbstractDocumentWriterTest {

    private Document document;

    protected void setUp() throws Exception {
        super.setUp();
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.newDocument();
        writer = new DomWriter(document);
    }

    protected DocumentReader createDocumentReaderFor(final Object node) {
        return new DomReader((Element)node);
    }

    // inherits tests from superclass
    
    public void testCanWriteIntoArbitraryNode() {
        Element root = document.createElement("root"); 
        document.appendChild(root);
        Element a = document.createElement("a");
        root.appendChild(a);
        writer = new DomWriter(a, document, new XmlFriendlyNameCoder());
        
        final XppDom xppRoot = new XppDom("root");
        XppDom xppA = new XppDom("a");
        xppRoot.addChild(xppA);
        XppDom xppB = new XppDom("b");
        xppA.addChild(xppB);
        xppB.setAttribute("attr", "foo");
        
        assertDocumentProducedIs(xppA, xppB);
        XppDomWriter xppDomWriter = new XppDomWriter();
        new HierarchicalStreamCopier().copy(createDocumentReaderFor(document.getDocumentElement()), xppDomWriter);
        assertTrue(equals(xppRoot, xppDomWriter.getConfiguration()));
    }

    public void testCanWriteIntoArbitraryNodeAgain() {
        Element root = document.createElement("root"); 
        document.appendChild(root);
        Element a = document.createElement("a");
        root.appendChild(a);
        writer = new DomWriter(a);
        
        final XppDom xppRoot = new XppDom("root");
        XppDom xppA = new XppDom("a");
        xppRoot.addChild(xppA);
        XppDom xppB = new XppDom("b");
        xppA.addChild(xppB);
        xppB.setAttribute("attr", "foo");
        
        assertDocumentProducedIs(xppA, xppB);
        XppDomWriter xppDomWriter = new XppDomWriter();
        new HierarchicalStreamCopier().copy(createDocumentReaderFor(document.getDocumentElement()), xppDomWriter);
        assertTrue(equals(xppRoot, xppDomWriter.getConfiguration()));
    }
}
