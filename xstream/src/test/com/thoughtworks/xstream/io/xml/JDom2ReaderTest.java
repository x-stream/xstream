/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. June 2012 by Joerg Schaible 
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;

import java.io.StringReader;

public class JDom2ReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        Document document = new SAXBuilder().build(new StringReader(xml));
        return new JDom2Reader(document);
    }

    // inherits tests from superclass

}
