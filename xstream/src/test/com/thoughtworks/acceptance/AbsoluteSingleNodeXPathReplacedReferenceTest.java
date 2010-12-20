/*
 * Copyright (C) 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20. December 2010 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

public class AbsoluteSingleNodeXPathReplacedReferenceTest extends AbstractReplacedReferenceTest {

    // inherits test from superclass
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.SINGLE_NODE_XPATH_ABSOLUTE_REFERENCES);
    }

    public void testReplacedReference() {
        String expectedXml = ""
            + "<element>\n"
            + "  <data>parent</data>\n"
            + "  <children>\n"
            + "    <anonymous-element resolves-to=\"element\">\n"
            + "      <data>child</data>\n"
            + "      <parent reference=\"/element[1]\"/>\n"
            + "      <children/>\n"
            + "    </anonymous-element>\n"
            + "  </children>\n"
            + "</element>";
        
        replacedReference(expectedXml);
    }
}
