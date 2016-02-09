/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 08. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.StringReader;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class XppReaderTest extends AbstractXMLReaderTest {
    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        return new XppReader(new StringReader(xml));
    }

    @Override
    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalGeneralEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (!message.contains("resolve entity")) {
                throw e;
            }
        }
    }

    // inherits tests from superclass
}
