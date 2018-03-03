/*
 * Copyright (C) 2006, 2007, 2009, 2010, 2011, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. July 2011 by Joerg Schaible by merging AbsolutXPathCircularReferenceTest,
 * AbsolutXPathDuplicateReferenceTest, AbsolutXPathNestedCircularReferenceTest and
 * AbsolutXPathReplacedReferenceTest.
 */
package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.Mapper;


public class AbsoluteXPathReferenceTest extends AbstractReferenceTest {

    // tests inherited from superclass

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
    }

    public void testXmlContainsReferencePaths() {

        final Thing sameThing = new Thing("hello");
        final Thing anotherThing = new Thing("hello");

        final List<Thing> list = new ArrayList<>();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        final String expected = ""
            + "<list>\n"
            + "  <thing>\n"
            + "    <field>hello</field>\n"
            + "  </thing>\n"
            + "  <thing reference=\"/list/thing\"/>\n"
            + "  <thing>\n"
            + "    <field>hello</field>\n"
            + "  </thing>\n"
            + "</list>";

        assertEquals(expected, xstream.toXML(list));
    }

    public void testCircularReferenceXml() {
        final Person bob = new Person("bob");
        final Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        final String expected = ""
            + "<person>\n"
            + "  <firstname>bob</firstname>\n"
            + "  <likes>\n"
            + "    <firstname>jane</firstname>\n"
            + "    <likes reference=\"/person\"/>\n"
            + "  </likes>\n"
            + "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCircularReferenceToSelfXml() {
        final Person bob = new Person("bob");
        bob.likes = bob;

        final String expected = ""
            + "<person>\n"
            + "  <firstname>bob</firstname>\n"
            + "  <likes reference=\"/person\"/>\n"
            + "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testRing() {
        final LinkedElement tom = new LinkedElement("Tom");
        final LinkedElement dick = new LinkedElement("Dick");
        final LinkedElement harry = new LinkedElement("Harry");
        tom.next = dick;
        dick.next = harry;
        harry.next = tom;

        xstream.alias("elem", LinkedElement.class);
        final String expected = ""
            + "<elem>\n"
            + "  <name>Tom</name>\n"
            + "  <next>\n"
            + "    <name>Dick</name>\n"
            + "    <next>\n"
            + "      <name>Harry</name>\n"
            + "      <next reference=\"/elem\"/>\n"
            + "    </next>\n"
            + "  </next>\n"
            + "</elem>";

        assertEquals(expected, xstream.toXML(tom));
    }

    public void testTree() {
        final TreeElement root = new TreeElement("X");
        final TreeElement left = new TreeElement("Y");
        final TreeElement right = new TreeElement("Z");
        root.left = left;
        root.right = right;
        left.left = new TreeElement(root.name);
        right.right = new TreeElement(left.name);
        right.left = left.left;

        xstream.alias("elem", TreeElement.class);
        final String expected = ""
            + "<elem>\n"
            + "  <name>X</name>\n"
            + "  <left>\n"
            + "    <name>Y</name>\n"
            + "    <left>\n"
            + "      <name reference=\"/elem/name\"/>\n"
            + "    </left>\n"
            + "  </left>\n"
            + "  <right>\n"
            + "    <name>Z</name>\n"
            + "    <left reference=\"/elem/left/left\"/>\n"
            + "    <right>\n"
            + "      <name reference=\"/elem/left/name\"/>\n"
            + "    </right>\n"
            + "  </right>\n"
            + "</elem>";

        assertEquals(expected, xstream.toXML(root));
    }

    @Override
    public void testReplacedReference() {
        final String expectedXml = ""
            + "<element>\n"
            + "  <data>parent</data>\n"
            + "  <children>\n"
            + "    <anonymous-element resolves-to=\"element\">\n"
            + "      <data>child</data>\n"
            + "      <parent reference=\"/element\"/>\n"
            + "      <children/>\n"
            + "    </anonymous-element>\n"
            + "  </children>\n"
            + "</element>";

        replacedReference(expectedXml);
    }

    public void testCanReferenceDeserializedNullValues() {
        xstream.alias("test", Mapper.Null.class);
        final String xml = "" //
            + "<list>\n"
            + "  <test/>\n"
            + "  <test reference=\"/list/test\"/>\n"
            + "</list>";
        final List<?> list = xstream.fromXML(xml);
        assertEquals(2, list.size());
        assertNull(list.get(0));
        assertNull(list.get(1));
    }

}
