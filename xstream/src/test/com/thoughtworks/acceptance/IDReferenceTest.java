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
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.Mapper;


public class IDReferenceTest extends AbstractReferenceTest {

    // tests inherited from superclass

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.setMode(XStream.ID_REFERENCES);
    }

    public void testXmlContainsReferenceIds() {

        final Thing sameThing = new Thing("hello");
        final Thing anotherThing = new Thing("hello");

        final List<Thing> list = new ArrayList<>();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        final String expected = ""
            + "<list id=\"1\">\n"
            + "  <thing id=\"2\">\n"
            + "    <field>hello</field>\n"
            + "  </thing>\n"
            + "  <thing reference=\"2\"/>\n"
            + "  <thing id=\"3\">\n"
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
            + "<person id=\"1\">\n"
            + "  <firstname>bob</firstname>\n"
            + "  <likes id=\"2\">\n"
            + "    <firstname>jane</firstname>\n"
            + "    <likes reference=\"1\"/>\n"
            + "  </likes>\n"
            + "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCircularReferenceToSelfXml() {
        final Person bob = new Person("bob");
        bob.likes = bob;

        final String expected = ""
            + "<person id=\"1\">\n"
            + "  <firstname>bob</firstname>\n"
            + "  <likes reference=\"1\"/>\n"
            + "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    @Override
    public void testReplacedReference() {
        final String expectedXml = ""
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
        final String xml = ""
            + "<list id=\"1\">\n"
            + "  <test id=\"2\"/>\n"
            + "  <test reference=\"2\"/>\n"
            + "</list>";
        final List<?> list = xstream.fromXML(xml);
        assertEquals(2, list.size());
        assertNull(list.get(0));
        assertNull(list.get(1));
    }
}
