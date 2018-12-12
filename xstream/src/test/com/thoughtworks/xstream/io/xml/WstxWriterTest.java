/*
 * Copyright (C) 2007, 2009, 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. September 2011 by Joerg Schaible, renamed from WoodstoxStaxWriterTest
 */
package com.thoughtworks.xstream.io.xml;

import javax.xml.stream.XMLOutputFactory;

import com.ctc.wstx.stax.WstxOutputFactory;


public final class WstxWriterTest extends AbstractStaxWriterTest {
    @Override
    protected void assertXmlProducedIs(String expected) {
        if (!staxDriver.isRepairingNamespace() || expected.matches("<\\w+:\\w+ xmlns:\\w+=.+")) {
            expected = expected.replaceAll(" xmlns=\"\"", "");
        }
        expected = expected.replace("&#x0D;", "&#xd;");
        expected = expected.replace("&gt;", ">"); // unusual behavior in Woodstox, but allowed in spec
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    @Override
    protected String getXMLHeader() {
        return "<?xml version='1.0' encoding='UTF-8'?>";
    }

    protected XMLOutputFactory getOutputFactory() {
        return new WstxOutputFactory();
    }

    @Override
    protected StaxDriver getStaxDriver() {
        return new WstxDriver();
    }
}
