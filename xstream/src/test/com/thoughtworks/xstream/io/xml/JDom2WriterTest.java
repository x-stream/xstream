/*
 * Copyright (C) 2013, 2018 XStream Committers.
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


public class JDom2WriterTest extends AbstractDocumentWriterTest<Element> {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        writer = new JDom2Writer();
    }

    @Override
    protected DocumentReader createDocumentReaderFor(final Element node) {
        return new JDom2Reader(node);
    }

    // inherits tests from superclass
}
