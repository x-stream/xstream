/*
 * Copyright (C) 2008, 2010 XStream Committers.
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

public class BeanIDCircularReferenceTest extends AbstractCircularReferenceTest {

    private ReferenceByFirstnameMarshallingStrategy marshallingStrategy;

	private static final class ReferenceByFirstnameMarshallingStrategy extends ReferenceByIdMarshallingStrategy
	{
		protected TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer,
		    ConverterLookup converterLookup, Mapper mapper) {
		    return new ReferenceByIdMarshaller(writer, converterLookup, mapper, new ReferenceByIdMarshaller.IDGenerator(){

		        public String next(Object item) {
		            // we have only persons here
		            return ((Person)item).firstname;
		        }});
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

        String expected = "" +
                "<person id=\"bob\">\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes id=\"jane\">\n" +
                "    <firstname>jane</firstname>\n" +
                "    <likes reference=\"bob\"/>\n" +
                "  </likes>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCircularReferenceToSelfXml() {
        Person bob = new Person("bob");
        bob.likes = bob;

        String expected = "" +
                "<person id=\"bob\">\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes reference=\"bob\"/>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
    }

    public void testCanAvoidMemberIfUsedAsId() throws Exception
	{
		xstream.omitField(Person.class, "firstname");

        Person bob = new Person("bob");
        Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        String expected = "" +
                "<person id=\"bob\">\n" +
                "  <likes id=\"jane\">\n" +
                "    <likes reference=\"bob\"/>\n" +
                "  </likes>\n" +
                "</person>";

        assertEquals(expected, xstream.toXML(bob));
        
        setUp(); // new XStream instance, since marshal and unmarshal is asymmetric
        xstream.useAttributeFor("firstname", String.class);
        xstream.aliasField("id", Person.class, "firstname");
        
        Person bobAgain = (Person)xstream.fromXML(expected);
        assertEquals("bob", bobAgain.firstname);
        assertEquals("jane", bobAgain.likes.firstname);
	}
}
