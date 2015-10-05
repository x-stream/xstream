/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. September 2004 by James Strachan
 */
package com.thoughtworks.xstream.io.xml;

import java.io.StringReader;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class StaxReaderTest extends AbstractXMLReaderTest {
    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        final StaxDriver driver = new StaxDriver();
        return driver.createReader(new StringReader(xml));
    }

    @Override
    public void testIsXXEVulnerable() throws Exception {
        try {
            super.testIsXXEVulnerable();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getMessage();
            if (!message.contains("external entity")) {
                if (e.getCause().getClass().getName().equals("com.wutka.dtd.DTDParseException")) {
                    System.err.println("BEAStaxReader was selected as default StAX driver for StaxReaderTest!");
                } else {
                    throw e;
                }
            }
        }
    }

    // inherits tests from superclass
}
