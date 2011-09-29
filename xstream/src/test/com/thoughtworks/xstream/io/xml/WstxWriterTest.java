/*
 * Copyright (C) 2007, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. September 2011 by Joerg Schaible, renamed from WoodstoxStaxWriterTest
 */
package com.thoughtworks.xstream.io.xml;

import com.ctc.wstx.stax.WstxOutputFactory;

import javax.xml.stream.XMLOutputFactory;

public final class WstxWriterTest extends AbstractStaxWriterTest {
    protected void assertXmlProducedIs(String expected) {
        if (!staxDriver.isRepairingNamespace() || perlUtil.match("#<\\w+:\\w+(>| xmlns:\\w+=)#", expected)) {
            expected = perlUtil.substitute("s# xmlns=\"\"##g", expected);
        }
        expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2 />#g", expected);
        expected = replaceAll(expected, "&#x0D;", "&#xd;");
        expected = replaceAll(expected, "&gt;", ">"); // Woodstox bug !!
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    protected String getXMLHeader() {
        return "<?xml version='1.0' encoding='UTF-8'?>";
    }

    protected XMLOutputFactory getOutputFactory() {
        return new WstxOutputFactory();
    }

    protected StaxDriver getStaxDriver() {
        return new WstxDriver();
    }
}