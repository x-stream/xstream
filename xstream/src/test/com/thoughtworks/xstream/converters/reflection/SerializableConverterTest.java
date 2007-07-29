/*
 * Copyright (C) 2007 XStream Committers
 * Created on 29.07.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.acceptance.StandardObject;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * @author J&ouml;rg Schaible
 */
public class SerializableConverterTest extends TestCase {

    static class SimpleType extends StandardObject {
        private String one;
        private String two;

        public String getOne() {
            return this.one;
        }

        public void setOne(String one) {
            this.one = one;
        }

        public String getTwo() {
            return this.two;
        }

        public void setTwo(String two) {
            this.two = two;
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }

        private void readObject(final ObjectInputStream in)
            throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }
    
    public void testCanOmitFieldAtSerialization() {
        XStream xstream = new XStream();
        xstream.alias("simple", SimpleType.class);
        xstream.omitField(SimpleType.class, "two");
        
        String expected = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <one>one</one>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";
        
        SimpleType simple = new SimpleType();
        simple.setOne("one");
        simple.setTwo("two");
        
        String xml = xstream.toXML(simple);
        assertEquals(expected, xml);
    }
    
    public void testCanOmitFieldAtDeserialization() {
        XStream xstream = new XStream();
        xstream.alias("simple", SimpleType.class);
        xstream.omitField(SimpleType.class, "two");
        xstream.omitField(SimpleType.class, "x");
        
        String xml = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <one>one</one>\n"
            + "      <x>x</x>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";
        
        SimpleType simple = new SimpleType();
        simple.setOne("one");
        
        SimpleType serialized = (SimpleType)xstream.fromXML(xml);
        assertEquals(simple, serialized);
    }
    
    static class ExtendedType extends SimpleType {
        private String three;

        public String getThree() {
            return this.three;
        }

        public void setThree(String three) {
            this.three = three;
        }
        
        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }

        private void readObject(final ObjectInputStream in)
            throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }
    
    public void testCanOmitInheritedFieldAtSerialization() {
        XStream xstream = new XStream();
        xstream.alias("extended", ExtendedType.class);
        xstream.alias("simple", SimpleType.class);
        xstream.omitField(SimpleType.class, "two");
        
        String expected = ""
            + "<extended serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <one>one</one>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "  <extended>\n"
            + "    <default>\n"
            + "      <three>three</three>\n"
            + "    </default>\n"
            + "  </extended>\n"
            + "</extended>";
        
        ExtendedType extended = new ExtendedType();
        extended.setOne("one");
        extended.setTwo("two");
        extended.setThree("three");
        
        String xml = xstream.toXML(extended);
        assertEquals(expected, xml);
    }
    
    public void testCanOmitInheritedFieldAtDeserialization() {
        XStream xstream = new XStream();
        xstream.alias("extended", ExtendedType.class);
        xstream.alias("simple", SimpleType.class);
        xstream.omitField(SimpleType.class, "two");
        xstream.omitField(SimpleType.class, "x");
        
        String xml = ""
            + "<extended serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <one>one</one>\n"
            + "      <x>x</x>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "  <extended>\n"
            + "    <default>\n"
            + "      <three>three</three>\n"
            + "    </default>\n"
            + "  </extended>\n"
            + "</extended>";
        
        ExtendedType extended = new ExtendedType();
        extended.setOne("one");
        extended.setThree("three");
        
        SimpleType serialized = (SimpleType)xstream.fromXML(xml);
        assertEquals(extended, serialized);
    }
}
