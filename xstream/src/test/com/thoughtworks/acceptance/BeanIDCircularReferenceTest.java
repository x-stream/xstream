/*
 * Copyright (C) 2008, 2010, 2011, 2018 XStream Committers.
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

    private static final class ReferenceByFirstnameMarshallingStrategy extends ReferenceByIdMarshallingStrategy {
        @Override
        protected TreeMarshaller createMarshallingContext(final HierarchicalStreamWriter writer,
                final ConverterLookup converterLookup, final Mapper mapper) {
            return new ReferenceByIdMarshaller(writer, converterLookup, mapper,
                new ReferenceByIdMarshaller.IDGenerator() {
                    int id = 0;

                    @Override
                    public String next(final Object item) {
                        final String id;
                        if (item instanceof Person) {
                            id = ((Person)item).firstname;
                        } else if (item instanceof TreeData) {
                            id = ((TreeData)item).data;
                        } else {
                            id = String.valueOf(this.id++);
                        }
                        return id;
                    }
                });
        }
    }

    // inherits test from superclass
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        marshallingStrategy = new ReferenceByFirstnameMarshallingStrategy();
        xstream.setMarshallingStrategy(marshallingStrategy);
    }

    public void testCircularReferenceXml() {
        final Person bob = new Person("bob");
        final Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        final String expected = ""
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
        final Person bob = new Person("bob");
        bob.likes = bob;

        final String expected = ""
            + "<person id=\"bob\">\n"
            + "  <firstname>bob</firstname>\n"
            + "  <likes reference=\"bob\"/>\n"
            + "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCanAvoidMemberIfUsedAsId() throws Exception {
        xstream.omitField(Person.class, "firstname");

        final Person bob = new Person("bob");
        final Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        final String expected = ""
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

        final Person bobAgain = xstream.fromXML(expected);
        assertEquals("bob", bobAgain.firstname);
        assertEquals("jane", bobAgain.likes.firstname);
    }

    @Override
    public void testReplacedReference() {
        final String expectedXml = ""
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
