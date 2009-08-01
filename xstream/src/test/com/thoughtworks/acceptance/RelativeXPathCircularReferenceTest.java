/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. May 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;

public class RelativeXPathCircularReferenceTest extends AbstractCircularReferenceTest {

    // inherits test from superclass
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
    }

    public void testCircularReferenceXml() {
        Person bob = new Person("bob");
        Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        String expected = "" +
                "<person>\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes>\n" +
                "    <firstname>jane</firstname>\n" +
                "    <likes reference=\"../..\"/>\n" +
                "  </likes>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCircularReferenceToSelfXml() {
        Person bob = new Person("bob");
        bob.likes = bob;

        String expected = "" +
                "<person>\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes reference=\"..\"/>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testRing() {
        LinkedElement tom = new LinkedElement("Tom");
        LinkedElement dick = new LinkedElement("Dick");
        LinkedElement harry = new LinkedElement("Harry");
        tom.next = dick;
        dick.next = harry;
        harry.next = tom;
        
        xstream.alias("elem", LinkedElement.class);
        String expected = "" +
            "<elem>\n" +
            "  <name>Tom</name>\n" +
            "  <next>\n" +
            "    <name>Dick</name>\n" +
            "    <next>\n" +
            "      <name>Harry</name>\n" +
            "      <next reference=\"../../..\"/>\n" +
            "    </next>\n" +
            "  </next>\n" +
            "</elem>";

        assertEquals(expected, xstream.toXML(tom));
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
            "      <name reference=\"../../../name\"/>\n" +
            "    </left>\n" +
            "  </left>\n" +
            "  <right>\n" +
            "    <name>Z</name>\n" +
            "    <left reference=\"../../left/left\"/>\n" +
            "    <right>\n" +
            "      <name reference=\"../../../left/name\"/>\n" +
            "    </right>\n" +
            "  </right>\n" +
            "</elem>";
    
        assertEquals(expected, xstream.toXML(root));
    }
}
