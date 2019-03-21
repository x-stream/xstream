/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015, 2016, 2018, 2019 XStream Committers.
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

import org.xmlpull.mxp1.MXParser;

import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class XppReaderTest extends AbstractXMLReaderTest {
    @Override
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        return new XppReader(new StringReader(xml), XppDriver.createDefaultParser());
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
    
    @Override
    public void testSupportsFieldsWithSpecialCharsInXml11() throws Exception {
        // no support for XML 1.1 if XPP implementation is Xpp3
        if (!(XppDriver.createDefaultParser() instanceof MXParser)) {
            super.testSupportsFieldsWithSpecialCharsInXml11();
        }
    }

    // inherits tests from superclass
}
