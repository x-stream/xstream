/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. July 2011 by Joerg Schaible by merging IDCircularReferenceTest,
 * IDDuplicateReferenceTest, IDNestedCircularReferenceTest and
 * IDReplacedReferenceTest.
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.ArrayList;
import java.util.List;

public class IDReferenceTest extends AbstractReferenceTest {

    // tests inherited from superclass

    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
    }

    public void testXmlContainsReferenceIds() {

        Thing sameThing = new Thing("hello");
        Thing anotherThing = new Thing("hello");

        List list = new ArrayList();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        String expected = "" +
                "<list id=\"1\">\n" +
                "  <thing id=\"2\">\n" +
                "    <field>hello</field>\n" +
                "  </thing>\n" +
                "  <thing reference=\"2\"/>\n" +
                "  <thing id=\"3\">\n" +
                "    <field>hello</field>\n" +
                "  </thing>\n" +
                "</list>";

        assertEquals(expected, xstream.toXML(list));
    }

    public void testCircularReferenceXml() {
        Person bob = new Person("bob");
        Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        String expected = "" +
                "<person id=\"1\">\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes id=\"2\">\n" +
                "    <firstname>jane</firstname>\n" +
                "    <likes reference=\"1\"/>\n" +
                "  </likes>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCircularReferenceToSelfXml() {
        Person bob = new Person("bob");
        bob.likes = bob;

        String expected = "" +
                "<person id=\"1\">\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes reference=\"1\"/>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testReplacedReference() {
        String expectedXml = ""
            + "<element id=\"1\">\n"
            + "  <data>parent</data>\n"
            + "  <children id=\"2\">\n"
            + "    <anonymous-element id=\"3\" resolves-to=\"element\">\n"
            + "      <data>child</data>\n"
            + "      <parent reference=\"1\"/>\n"
            + "      <children id=\"4\"/>\n"
            + "    </anonymous-element>\n"
            + "  </children>\n"
            + "</element>";
        
        replacedReference(expectedXml);
    }
    
    public void testCanReferenceDeserializedNullValues() {
        xstream.alias("test", Mapper.Null.class);
        String xml = ""
                + "<list id=\"1\">\n"
                + "  <test id=\"2\"/>\n"
                + "  <test reference=\"2\"/>\n"
                + "</list>";
        List list = (List)xstream.fromXML(xml);
        assertEquals(2, list.size());
        assertNull(list.get(0));
        assertNull(list.get(1));
    }
}
