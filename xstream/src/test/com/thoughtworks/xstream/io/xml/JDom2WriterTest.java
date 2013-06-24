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

import org.jdom2.Element;

public class JDom2WriterTest extends AbstractDocumentWriterTest {

    protected void setUp() throws Exception {
        super.setUp();
        writer = new JDom2Writer();
    }

    protected DocumentReader createDocumentReaderFor(final Object node) {
        return new JDom2Reader((Element)node);
    }

    // inherits tests from superclass
}
