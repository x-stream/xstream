package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.reflection.Sun14ReflectionProvider;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import junit.framework.TestCase;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class CircularReferenceTest extends TestCase {

    private ClassMapper classMapper;
    private DefaultConverterLookup converterLookup;

    protected void setUp() throws Exception {
        super.setUp();
        classMapper = new DefaultClassMapper();
        converterLookup = new DefaultConverterLookup(
                        new Sun14ReflectionProvider(),
                        classMapper, "class");
        classMapper.alias("person", Person.class, Person.class);
        converterLookup.setupDefaults();
    }

    public void testCircularReference() {
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

        String xml = toXML(bob);
        assertEquals(expected, xml);

        Person bobOut = (Person) fromXML(xml);
        assertEquals("bob", bobOut.firstname);
        Person janeOut = bobOut.likes;

        assertEquals("jane", janeOut.firstname);

        assertSame(bobOut.likes, janeOut);
        assertSame(bobOut, janeOut.likes);
    }

    public void testCircularReferenceToSelf() {
        Person bob = new Person("bob");
        bob.likes = bob;

        String expected = "" +
                "<person id=\"1\">\n" +
                "  <firstname>bob</firstname>\n" +
                "  <likes reference=\"1\"/>\n" +
                "</person>";

        String xml = toXML(bob);
        assertEquals(expected, xml);

        Person bobOut = (Person) fromXML(xml);
        assertEquals("bob", bobOut.firstname);
        assertSame(bobOut, bobOut.likes);
    }

    private String toXML(Object obj) {
        StringWriter buffer = new StringWriter();
        HierarchicalStreamWriter writer = new PrettyPrintWriter(buffer);
        Marshaller marshaller = new ReferenceByIdMarshaller(
                        writer, converterLookup, classMapper);

        marshaller.start(obj);
        return buffer.toString();
    }

    private Object fromXML(String xml) {
        XppReader reader = new XppReader(new StringReader(xml));

        Unmarshaller unmarshaller = new ReferenceByIdUnmarshaller(
                null, reader, converterLookup, classMapper, "class");

        return unmarshaller.start();
    }

    class Person {
        public String firstname;
        public Person likes;

        public Person(String name) {
            this.firstname = name;
        }
    }

}
