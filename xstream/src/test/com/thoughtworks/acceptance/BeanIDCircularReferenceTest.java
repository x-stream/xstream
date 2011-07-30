/*
 * Copyright (C) 2008, 2010, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 12. November 2008 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.core.ReferenceByIdMarshaller;
import com.thoughtworks.xstream.core.ReferenceByIdMarshallingStrategy;
import com.thoughtworks.xstream.core.TreeMarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


public class BeanIDCircularReferenceTest extends AbstractReferenceTest {

    private ReferenceByFirstnameMarshallingStrategy marshallingStrategy;

    private static final class ReferenceByFirstnameMarshallingStrategy extends
        ReferenceByIdMarshallingStrategy {
        protected TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer,
            ConverterLookup converterLookup, Mapper mapper) {
            return new ReferenceByIdMarshaller(
                writer, converterLookup, mapper, new ReferenceByIdMarshaller.IDGenerator() {
                    int id = 0;

                    public String next(Object item) {
                        final String id;
                        if (item instanceof Person) {
                            id = ((Person)item).firstname;
                        } else if (item instanceof TreeData) {
                            id = ((TreeData)item).data;
                        } else {
                            id = String.valueOf(this.id++ );
                        }
                        return id;
                    }
                });
        }
    }

    // inherits test from superclass
    protected void setUp() throws Exception {
        super.setUp();
        marshallingStrategy = new ReferenceByFirstnameMarshallingStrategy();
        xstream.setMarshallingStrategy(marshallingStrategy);
    }

    public void testCircularReferenceXml() {
        Person bob = new Person("bob");
        Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        String expected = ""
            + "<person id=\"bob\">\n"
            + "  <firstname>bob</firstname>\n"
            + "  <likes id=\"jane\">\n"
            + "    <firstname>jane</firstname>\n"
            + "    <likes reference=\"bob\"/>\n"
            + "  </likes>\n"
            + "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCircularReferenceToSelfXml() {
        Person bob = new Person("bob");
        bob.likes = bob;

        String expected = ""
            + "<person id=\"bob\">\n"
            + "  <firstname>bob</firstname>\n"
            + "  <likes reference=\"bob\"/>\n"
            + "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCanAvoidMemberIfUsedAsId() throws Exception {
        xstream.omitField(Person.class, "firstname");

        Person bob = new Person("bob");
        Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        String expected = ""
            + "<person id=\"bob\">\n"
            + "  <likes id=\"jane\">\n"
            + "    <likes reference=\"bob\"/>\n"
            + "  </likes>\n"
            + "</person>";

        assertEquals(expected, xstream.toXML(bob));

        // new XStream instance, since marshal and unmarshal is asymmetric
        xstream = createXStream();
        setUp();
        xstream.useAttributeFor("firstname", String.class);
        xstream.aliasField("id", Person.class, "firstname");

        Person bobAgain = (Person)xstream.fromXML(expected);
        assertEquals("bob", bobAgain.firstname);
        assertEquals("jane", bobAgain.likes.firstname);
    }

    public void testReplacedReference() {
        String expectedXml = ""
            + "<element id=\"parent\">\n"
            + "  <data>parent</data>\n"
            + "  <children id=\"0\">\n"
            + "    <anonymous-element id=\"child\" resolves-to=\"element\">\n"
            + "      <data>child</data>\n"
            + "      <parent reference=\"parent\"/>\n"
            + "      <children id=\"1\"/>\n"
            + "    </anonymous-element>\n"
            + "  </children>\n"
            + "</element>";

        replacedReference(expectedXml);
    }
}
