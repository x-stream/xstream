/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.Mapper;


public class RelativeXPathReferenceTest extends AbstractReferenceTest {

    // tests inherited from superclass

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.XPATH_RELATIVE_REFERENCES);
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
            + "  <thing reference=\"../thing\"/>\n"
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
            + "    <likes reference=\"../..\"/>\n"
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
            + "  <likes reference=\"..\"/>\n"
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
            + "      <next reference=\"../../..\"/>\n"
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
            + "      <name reference=\"../../../name\"/>\n"
            + "    </left>\n"
            + "  </left>\n"
            + "  <right>\n"
            + "    <name>Z</name>\n"
            + "    <left reference=\"../../left/left\"/>\n"
            + "    <right>\n"
            + "      <name reference=\"../../../left/name\"/>\n"
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
            + "      <parent reference=\"../../..\"/>\n"
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
            + "  <test reference=\"../test\"/>\n"
            + "</list>";
        final List<?> list = xstream.fromXML(xml);
        assertEquals(2, list.size());
        assertNull(list.get(0));
        assertNull(list.get(1));
    }

    static class RecursiveThing<V> {
        final Map<RecursiveThing<V>, V> map = new HashMap<>();
        final String name; // wrong definition order for HashMap!

        public RecursiveThing(final String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            return obj.getClass().equals(RecursiveThing.class) && name.equals(((RecursiveThing<?>)obj).name);
        }
    }

    public void todoTestRecursiveMap() {
        final RecursiveThing<String> thing = new RecursiveThing<>("joe");
        thing.map.put(thing, "walnes");

        xstream.alias("rec-thing", RecursiveThing.class);

        final String expected = ""
            + "<rec-thing>\n"
            + "  <map>\n"
            + "    <entry>\n"
            + "      <rec-thing reference=\"../../..\"/>\n"
            + "      <string>walnes</string>\n"
            + "    </entry>\n"
            + "  </map>\n"
            + "  <name>joe</name>\n"
            + "</rec-thing>";
        assertBothWays(thing, expected);
    }
}
