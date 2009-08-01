/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. July 2009 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

public class RelativeSingleNodeXPathCircularReferenceTest extends RelativeXPathCircularReferenceTest {

    // inherits test from superclass
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.SINGLE_NODE_XPATH_RELATIVE_REFERENCES);
    }
    
    public void testTree() {
        TreeElement root = new TreeElement("X");
        TreeElement left = new TreeElement("Y");
        TreeElement right = new TreeElement("Z");
        root.left = left;
        root.right = right;
        left.left = new TreeElement(root.name);
        right.right = new TreeElement(left.name);
        right.left = left.left;
    
        xstream.alias("elem", TreeElement.class);
        String expected = "" +
            "<elem>\n" +
            "  <name>X</name>\n" +
            "  <left>\n" +
            "    <name>Y</name>\n" +
            "    <left>\n" +
            "      <name reference=\"../../../name[1]\"/>\n" +
            "    </left>\n" +
            "  </left>\n" +
            "  <right>\n" +
            "    <name>Z</name>\n" +
            "    <left reference=\"../../left[1]/left[1]\"/>\n" +
            "    <right>\n" +
            "      <name reference=\"../../../left[1]/name[1]\"/>\n" +
            "    </right>\n" +
            "  </right>\n" +
            "</elem>";
    
        assertEquals(expected, xstream.toXML(root));
    }
}
