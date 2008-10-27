/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. July 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;


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

    public static class SimpleNamedFieldsType extends StandardObject implements Serializable {

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

        private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("s1", String.class),
            new ObjectStreamField("s2", String.class),
        };

        private void writeObject(ObjectOutputStream out) throws IOException {
            // don't call defaultWriteObject()
            ObjectOutputStream.PutField fields = out.putFields();
            fields.put("s1", one);
            fields.put("s2", two);
            out.writeFields();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            // don't call defaultReadObject()
            ObjectInputStream.GetField fields = in.readFields();
            one = (String) fields.get("s1", "1");
            two = (String) fields.get("s2", "2");
        }
    }
    
    public void testCanOmitNamedFieldAtSerialization() {
        XStream xstream = new XStream();
        xstream.alias("simple", SimpleNamedFieldsType.class);
        xstream.omitField(SimpleNamedFieldsType.class, "s2");
        
        String expected = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <s1>one</s1>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";
        
        SimpleNamedFieldsType simple = new SimpleNamedFieldsType();
        simple.setOne("one");
        simple.setTwo("two");
        
        String xml = xstream.toXML(simple);
        assertEquals(expected, xml);
    }
    
    public void testCanOmitNamedFieldAtDeserialization() {
        XStream xstream = new XStream();
        xstream.alias("simple", SimpleNamedFieldsType.class);
        xstream.omitField(SimpleNamedFieldsType.class, "s2");
        xstream.omitField(SimpleNamedFieldsType.class, "x");
        
        String xml = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <s1>one</s1>\n"
            + "      <x>x</x>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";
        
        SimpleNamedFieldsType simple = new SimpleNamedFieldsType();
        simple.setOne("one");
        simple.setTwo("2");
        
        SimpleNamedFieldsType serialized = (SimpleNamedFieldsType)xstream.fromXML(xml);
        assertEquals(simple, serialized);
    }
    
    public void testCanAliasField() {
        XStream xstream = new XStream();
        xstream.alias("simple", SimpleType.class);
        xstream.aliasField("s2", SimpleType.class, "two");
        
        String expected = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <one>one</one>\n"
            + "      <s2>two</s2>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";
        
        SimpleType simple = new SimpleType();
        simple.setOne("one");
        simple.setTwo("two");
        
        String xml = xstream.toXML(simple);
        assertEquals(expected, xml);
        SimpleType serialized = (SimpleType)xstream.fromXML(xml);
        assertEquals(simple, serialized);
    }

    public void testCanAliasNamedField() {
        XStream xstream = new XStream();
        xstream.alias("simple", SimpleNamedFieldsType.class);
        xstream.aliasField("two", SimpleNamedFieldsType.class, "s2");
        
        String expected = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <s1>one</s1>\n"
            + "      <two>two</two>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";
        
        SimpleNamedFieldsType simple = new SimpleNamedFieldsType();
        simple.setOne("one");
        simple.setTwo("two");
        
        String xml = xstream.toXML(simple);
        assertEquals(expected, xml);
        SimpleNamedFieldsType serialized = (SimpleNamedFieldsType)xstream.fromXML(xml);
        assertEquals(simple, serialized);
    }
}
