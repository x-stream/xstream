/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015, 2016 XStream Committers.
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
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;


public class StaxReaderTest extends AbstractXMLReaderTest {
    protected HierarchicalStreamReader createReader(final String xml) throws Exception {
        final StaxDriver driver = new StaxDriver();
        return driver.createReader(new StringReader(xml));
    }

    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalGeneralEntity();
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (message.indexOf("external entity") < 0) {
                throw e;
            }
        }
    }

    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        try {
            super.testIsXXEVulnerableWithExternalParameterEntity();
            fail("Thrown " + XStreamException.class.getName() + " expected");
        } catch (final XStreamException e) {
            final String message = e.getCause().getMessage();
            if (JVM.is14() && message == null) {
                if (JVM.is16()) {
                    throw e;
                }
                System.err.println("BEAStaxReader was selected as default StAX driver for StaxReaderTest!");
            } else if (message.indexOf("external entity") < 0) {
                if (JVM.is16() && message.indexOf("com.wutka.dtd.DTDParseException") >= 0) {
                    System.err.println("BEAStaxReader was selected as default StAX driver for StaxReaderTest!");
                } else if (message.replaceAll("[:space:]", "").endsWith("null")) {
                    System.err.println("BEAStaxReader was selected as default StAX driver for StaxReaderTest!");
                } else {
                    throw e;
                }
            }
        }
    }

    // inherits tests from superclass
}
