/*
 * Copyright (C) 2006, 2007, 2009, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 19. October 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.xml.xppdom.XppDom;


/**
 * @author J&ouml;rg Schaible
 */
public class XppDomWriterTest extends AbstractDocumentWriterTest<XppDom> {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        writer = new XppDomWriter();
    }

    @Override
    protected DocumentReader createDocumentReaderFor(final XppDom node) {
        return new XppDomReader(node);
    }

    // inherits tests from superclass
}
