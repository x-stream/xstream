/*
 * Copyright (C) 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. July 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class SerializableConverterTest extends TestCase {

    static class SimpleType extends StandardObject {
        private static final long serialVersionUID = 200707L;
        private String one;
        private String two;

        public String getOne() {
            return one;
        }

        public void setOne(final String one) {
            this.one = one;
        }

        public String getTwo() {
            return two;
        }

        public void setTwo(final String two) {
            this.two = two;
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    public void testCanOmitFieldAtSerialization() {
        final XStream xstream = new XStream();
        xstream.alias("simple", SimpleType.class);
        xstream.omitField(SimpleType.class, "two");

        final String expected = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <one>one</one>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";

        final SimpleType simple = new SimpleType();
        simple.setOne("one");
        simple.setTwo("two");

        final String xml = xstream.toXML(simple);
        assertEquals(expected, xml);
    }

    public void testCanOmitFieldAtDeserialization() {
        final XStream xstream = new XStream();
        xstream.allowTypes(SimpleType.class);
        xstream.alias("simple", SimpleType.class);
        xstream.omitField(SimpleType.class, "two");
        xstream.omitField(SimpleType.class, "x");

        final String xml = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <one>one</one>\n"
            + "      <x>x</x>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";

        final SimpleType simple = new SimpleType();
        simple.setOne("one");

        final SimpleType serialized = xstream.<SimpleType>fromXML(xml);
        assertEquals(simple, serialized);
    }

    static class ExtendedType extends SimpleType {
        private static final long serialVersionUID = 200707L;
        private String three;

        public String getThree() {
            return three;
        }

        public void setThree(final String three) {
            this.three = three;
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    public void testCanOmitInheritedFieldAtSerialization() {
        final XStream xstream = new XStream();
        xstream.alias("extended", ExtendedType.class);
        xstream.alias("simple", SimpleType.class);
        xstream.omitField(SimpleType.class, "two");

        final String expected = ""
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

        final ExtendedType extended = new ExtendedType();
        extended.setOne("one");
        extended.setTwo("two");
        extended.setThree("three");

        final String xml = xstream.toXML(extended);
        assertEquals(expected, xml);
    }

    public void testCanOmitInheritedFieldAtDeserialization() {
        final XStream xstream = new XStream();
        xstream.allowTypes(SimpleType.class, ExtendedType.class);
        xstream.alias("extended", ExtendedType.class);
        xstream.alias("simple", SimpleType.class);
        xstream.omitField(SimpleType.class, "two");
        xstream.omitField(SimpleType.class, "x");

        final String xml = ""
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

        final ExtendedType extended = new ExtendedType();
        extended.setOne("one");
        extended.setThree("three");

        final SimpleType serialized = xstream.<SimpleType>fromXML(xml);
        assertEquals(extended, serialized);
    }

    public static class SimpleNamedFieldsType extends StandardObject implements Serializable {
        private static final long serialVersionUID = 200708L;

        private String one;
        private String two;

        public String getOne() {
            return one;
        }

        public void setOne(final String one) {
            this.one = one;
        }

        public String getTwo() {
            return two;
        }

        public void setTwo(final String two) {
            this.two = two;
        }

        private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("s1", String.class), new ObjectStreamField("s2", String.class),};

        private void writeObject(final ObjectOutputStream out) throws IOException {
            // don't call defaultWriteObject()
            final ObjectOutputStream.PutField fields = out.putFields();
            fields.put("s1", one);
            fields.put("s2", two);
            out.writeFields();
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            // don't call defaultReadObject()
            final ObjectInputStream.GetField fields = in.readFields();
            one = (String)fields.get("s1", "1");
            two = (String)fields.get("s2", "2");
        }
    }

    public void testCanOmitNamedFieldAtSerialization() {
        final XStream xstream = new XStream();
        xstream.allowTypes(SimpleNamedFieldsType.class);
        xstream.alias("simple", SimpleNamedFieldsType.class);
        xstream.omitField(SimpleNamedFieldsType.class, "s2");

        final String expected = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <s1>one</s1>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";

        final SimpleNamedFieldsType simple = new SimpleNamedFieldsType();
        simple.setOne("one");
        simple.setTwo("two");

        final String xml = xstream.toXML(simple);
        assertEquals(expected, xml);
    }

    public void testCanOmitNamedFieldAtDeserialization() {
        final XStream xstream = new XStream();
        xstream.allowTypes(SimpleNamedFieldsType.class);
        xstream.alias("simple", SimpleNamedFieldsType.class);
        xstream.omitField(SimpleNamedFieldsType.class, "s2");
        xstream.omitField(SimpleNamedFieldsType.class, "x");

        final String xml = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <s1>one</s1>\n"
            + "      <x>x</x>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";

        final SimpleNamedFieldsType simple = new SimpleNamedFieldsType();
        simple.setOne("one");
        simple.setTwo("2");

        final SimpleNamedFieldsType serialized = (SimpleNamedFieldsType)xstream.fromXML(xml);
        assertEquals(simple, serialized);
    }

    public void testCanAliasField() {
        final XStream xstream = new XStream();
        xstream.allowTypes(SimpleType.class);
        xstream.alias("simple", SimpleType.class);
        xstream.aliasField("s2", SimpleType.class, "two");

        final String expected = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <one>one</one>\n"
            + "      <s2>two</s2>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";

        final SimpleType simple = new SimpleType();
        simple.setOne("one");
        simple.setTwo("two");

        final String xml = xstream.toXML(simple);
        assertEquals(expected, xml);
        final SimpleType serialized = xstream.<SimpleType>fromXML(xml);
        assertEquals(simple, serialized);
    }

    public void testCanAliasNamedField() {
        final XStream xstream = new XStream();
        xstream.allowTypes(SimpleNamedFieldsType.class);
        xstream.alias("simple", SimpleNamedFieldsType.class);
        xstream.aliasField("two", SimpleNamedFieldsType.class, "s2");

        final String expected = ""
            + "<simple serialization=\"custom\">\n"
            + "  <simple>\n"
            + "    <default>\n"
            + "      <s1>one</s1>\n"
            + "      <two>two</two>\n"
            + "    </default>\n"
            + "  </simple>\n"
            + "</simple>";

        final SimpleNamedFieldsType simple = new SimpleNamedFieldsType();
        simple.setOne("one");
        simple.setTwo("two");

        final String xml = xstream.toXML(simple);
        assertEquals(expected, xml);
        final SimpleNamedFieldsType serialized = xstream.<SimpleNamedFieldsType>fromXML(xml);
        assertEquals(simple, serialized);
    }

    public static class SerializableType implements Serializable {
        private static final long serialVersionUID = 201401L;
        public Serializable serializable;
    }

    public void testCanHandleFieldsDeclaredWithSerializableInterface() {
        final XStream xstream = new XStream();
        xstream.allowTypes(SerializableType.class);
        xstream.alias("sertype", SerializableType.class);
        xstream.useAttributeFor(SerializableType.class, "serializable");

        final String expected = ""
            + "<sertype>\n"
            + "  <serializable class=\"string\">String</serializable>\n"
            + "</sertype>";

        final SerializableType s = new SerializableType();
        s.serializable = "String";

        final String xml = xstream.toXML(s);
        assertEquals(expected, xml);
        final SerializableType serialized = xstream.<SerializableType>fromXML(xml);
        assertEquals(s.serializable, serialized.serializable);
    }
}
