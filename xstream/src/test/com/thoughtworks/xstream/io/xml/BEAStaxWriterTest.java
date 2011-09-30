/*
 * Copyright (C) 2007, 2008, 2009, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. November 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

public final class BEAStaxWriterTest extends AbstractStaxWriterTest {
    protected void assertXmlProducedIs(String expected) {
        expected = perlUtil.substitute("s# xmlns=\"\"##g", expected);
        expected = perlUtil.substitute("s#<(\\w+)([^>]*)/>#<$1$2></$1>#g", expected);
        expected = replaceAll(expected, "&#xd;", "&#13;");
        expected = replaceAll(expected, "&#xa;", "&#10;");
        expected = replaceAll(expected, "&#x9;", "&#9;");
        expected = getXMLHeader() + expected;
        assertEquals(expected, buffer.toString());
    }

    protected String getXMLHeader() {
        return "<?xml version='1.0' encoding='utf-8'?>";
    }

    protected StaxDriver getStaxDriver() {
        return new BEAStaxDriver();
    }

    protected void marshalRepairing(QNameMap qnameMap, String expected) {
        // repairing mode fails for BEA's reference implementation in this case
        if (!getName().equals("testNamespacedXmlWithPrefixTwice"))
            super.marshalRepairing(qnameMap, expected);
    }
}