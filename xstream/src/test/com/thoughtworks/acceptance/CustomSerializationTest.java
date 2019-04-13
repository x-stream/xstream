/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 23. August 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.objects.StandardObject;


public class CustomSerializationTest extends AbstractAcceptanceTest {

    public static class ObjectWithCustomSerialization extends StandardObject implements Serializable {
        private static final long serialVersionUID = 200408L;
        @SuppressWarnings("unused")
        private int a;
        private transient int b;
        private transient String c;
        private transient Object d;
        private transient Software e;

        public ObjectWithCustomSerialization() {
        }

        public ObjectWithCustomSerialization(final int a, final int b, final String c, final Software e) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.e = e;
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            b = in.readInt();
            in.defaultReadObject();
            c = (String)in.readObject();
            d = in.readObject();
            e = (Software)in.readObject();
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.writeInt(b);
            out.defaultWriteObject();
            out.writeObject(c);
            out.writeObject(d);
            out.writeObject(e);
        }

    }

    public void testWritesCustomFieldsToStream() {
        final ObjectWithCustomSerialization obj = new ObjectWithCustomSerialization(1, 2, "hello", new Software("tw",
            "xs"));
        xstream.alias("custom", ObjectWithCustomSerialization.class);
        xstream.alias("software", Software.class);

        final String expectedXml = ""
            + "<custom serialization=\"custom\">\n"
            + "  <custom>\n"
            + "    <int>2</int>\n"
            + "    <default>\n"
            + "      <a>1</a>\n"
            + "    </default>\n"
            + "    <string>hello</string>\n"
            + "    <null/>\n"
            + "    <software>\n"
            + "      <vendor>tw</vendor>\n"
            + "      <name>xs</name>\n"
            + "    </software>\n"
            + "  </custom>\n"
            + "</custom>";

        assertBothWays(obj, expectedXml);
    }

    public static class Parent extends StandardObject implements Serializable {
        private static final long serialVersionUID = 200408L;

        private transient int parentA;
        @SuppressWarnings("unused")
        private int parentB;
        private transient int parentC;

        public Parent() {
        }

        public Parent(final int parentA, final int parentB, final int parentC) {
            this.parentA = parentA;
            this.parentB = parentB;
            this.parentC = parentC;
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            parentA = in.readInt();
            in.defaultReadObject();
            parentC = in.readInt();
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.writeInt(parentA);
            out.defaultWriteObject();
            out.writeInt(parentC);
        }
    }

    public static class Child extends Parent {
        private static final long serialVersionUID = 200408L;
        private transient int childA;
        @SuppressWarnings("unused")
        private int childB;
        private transient int childC;

        public Child() {
        }

        public Child(
                final int parentA, final int parentB, final int parentC, final int childA, final int childB,
                final int childC) {
            super(parentA, parentB, parentC);
            this.childA = childA;
            this.childB = childB;
            this.childC = childC;
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            childA = in.readInt();
            in.defaultReadObject();
            childC = in.readInt();
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.writeInt(childA);
            out.defaultWriteObject();
            out.writeInt(childC);
        }
    }

    public void testIncludesCompleteClassHierarchyWhenParentAndChildHaveSerializationMethods() {
        final Child child = new Child(1, 2, 3, 10, 20, 30);
        xstream.alias("child", Child.class);
        xstream.alias("parent", Parent.class);

        final String expectedXml = ""
            + "<child serialization=\"custom\">\n"
            + "  <parent>\n"
            + "    <int>1</int>\n"
            + "    <default>\n"
            + "      <parentB>2</parentB>\n"
            + "    </default>\n"
            + "    <int>3</int>\n"
            + "  </parent>\n"
            + "  <child>\n"
            + "    <int>10</int>\n"
            + "    <default>\n"
            + "      <childB>20</childB>\n"
            + "    </default>\n"
            + "    <int>30</int>\n"
            + "  </child>\n"
            + "</child>";

        assertBothWays(child, expectedXml);
    }

    public static class Child2 extends Parent {
        private static final long serialVersionUID = 200412L;

        @SuppressWarnings("unused")
        private final int childA;

        public Child2(final int parentA, final int parentB, final int parentC, final int childA) {
            super(parentA, parentB, parentC);
            this.childA = childA;
        }

    }

    public void testIncludesCompleteClassHierarchyWhenOnlyParentHasSerializationMethods() {
        final Child2 child = new Child2(1, 2, 3, 20);
        xstream.alias("child2", Child2.class);
        xstream.alias("parent", Parent.class);

        final String expectedXml = ""
            + "<child2 serialization=\"custom\">\n"
            + "  <parent>\n"
            + "    <int>1</int>\n"
            + "    <default>\n"
            + "      <parentB>2</parentB>\n"
            + "    </default>\n"
            + "    <int>3</int>\n"
            + "  </parent>\n"
            + "  <child2>\n"
            + "    <default>\n"
            + "      <childA>20</childA>\n"
            + "    </default>\n"
            + "  </child2>\n"
            + "</child2>";

        assertBothWays(child, expectedXml);
    }

    static class MyDate extends java.util.Date {
        private static final long serialVersionUID = 200410L;

        public MyDate(final int time) {
            super(time);
        }
    }

    static class MyHashtable<K, V> extends java.util.Hashtable<K, V> {
        private static final long serialVersionUID = 200412L;
        private final String name;

        public MyHashtable(final String name) {
            this.name = name;
        }

        @Override
        public synchronized boolean equals(final Object o) {
            return super.equals(o) && ((MyHashtable<?, ?>)o).name.equals(name);
        }
    }

    public void testSupportsSubclassesOfClassesThatAlreadyHaveConverters() {
        final MyDate in = new MyDate(1234567890);
        final String xml = xstream.toXML(in);
        assertObjectsEqual(in, xstream.fromXML(xml));

        final MyHashtable<String, Object> in2 = new MyHashtable<>("hi");
        in2.put("cheese", "curry");
        in2.put("apple", 3);
        final String xml2 = xstream.toXML(in2);
        assertObjectsEqual(in2, xstream.fromXML(xml2));
    }

    public static class ObjectWithNamedFields extends StandardObject implements Serializable {
        private static final long serialVersionUID = 200501L;

        private String name;
        private int number;
        private Software someSoftware;
        private Object polymorphic;
        private Object nothing;

        private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("theName", String.class), new ObjectStreamField("theNumber", int.class),
            new ObjectStreamField("theSoftware", Software.class), new ObjectStreamField("thePolymorphic", Object.class),
            new ObjectStreamField("theNothing", Object.class)};

        private void writeObject(final ObjectOutputStream out) throws IOException {
            // don't call defaultWriteObject()
            final ObjectOutputStream.PutField fields = out.putFields();
            fields.put("theName", name);
            fields.put("theNumber", number);
            fields.put("theSoftware", someSoftware);
            fields.put("thePolymorphic", polymorphic);
            fields.put("theNothing", nothing);
            out.writeFields();
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            // don't call defaultReadObject()
            final ObjectInputStream.GetField fields = in.readFields();
            name = (String)fields.get("theName", "unknown");
            number = fields.get("theNumber", -1);
            someSoftware = (Software)fields.get("theSoftware", null);
            polymorphic = fields.get("thePolymorphic", null);
            nothing = fields.get("theNothing", null);
        }

    }

    public void testAllowsNamedFields() {
        final ObjectWithNamedFields obj = new ObjectWithNamedFields();
        obj.name = "Joe";
        obj.number = 99;
        obj.someSoftware = new Software("tw", "xs");
        obj.polymorphic = new Hardware("small", "ipod");
        obj.nothing = null;

        xstream.alias("with-named-fields", ObjectWithNamedFields.class);
        xstream.alias("software", Software.class);

        final String expectedXml = ""
            + "<with-named-fields serialization=\"custom\">\n"
            + "  <with-named-fields>\n"
            + "    <default>\n"
            + "      <theName>Joe</theName>\n"
            + "      <theNumber>99</theNumber>\n"
            + "      <theSoftware>\n"
            + "        <vendor>tw</vendor>\n"
            + "        <name>xs</name>\n"
            + "      </theSoftware>\n"
            + "      <thePolymorphic class=\"com.thoughtworks.acceptance.objects.Hardware\">\n"
            + "        <arch>small</arch>\n"
            + "        <name>ipod</name>\n"
            + "      </thePolymorphic>\n"
            + "    </default>\n"
            + "  </with-named-fields>\n"
            + "</with-named-fields>";

        assertBothWays(obj, expectedXml);
    }

    public void testUsesDefaultIfNamedFieldNotFound() {
        xstream.alias("with-named-fields", ObjectWithNamedFields.class);
        xstream.alias("software", Software.class);

        final String inputXml = ""
            + "<with-named-fields serialization=\"custom\">\n"
            + "  <with-named-fields>\n"
            + "    <default>\n"
            + "      <theSoftware>\n"
            + "        <vendor>tw</vendor>\n"
            + "        <name>xs</name>\n"
            + "      </theSoftware>\n"
            + "      <thePolymorphic class=\"com.thoughtworks.acceptance.objects.Hardware\">\n"
            + "        <arch>small</arch>\n"
            + "        <name>ipod</name>\n"
            + "      </thePolymorphic>\n"
            + "    </default>\n"
            + "  </with-named-fields>\n"
            + "</with-named-fields>";

        final ObjectWithNamedFields result = (ObjectWithNamedFields)xstream.fromXML(inputXml);
        assertEquals(-1, result.number);
        assertEquals("unknown", result.name);
        assertEquals(new Software("tw", "xs"), result.someSoftware);
    }

    public void testCustomStreamWithNestedCustomStream() {
        final ObjectWithNamedFields outer = new ObjectWithNamedFields();
        outer.name = "Joe";
        outer.someSoftware = new Software("tw", "xs");
        outer.nothing = null;

        final ObjectWithNamedFields inner = new ObjectWithNamedFields();
        inner.name = "Thing";

        outer.polymorphic = inner;

        xstream.alias("with-named-fields", ObjectWithNamedFields.class);
        xstream.alias("software", Software.class);

        final String expectedXml = ""
            + "<with-named-fields serialization=\"custom\">\n"
            + "  <with-named-fields>\n"
            + "    <default>\n"
            + "      <theName>Joe</theName>\n"
            + "      <theNumber>0</theNumber>\n"
            + "      <theSoftware>\n"
            + "        <vendor>tw</vendor>\n"
            + "        <name>xs</name>\n"
            + "      </theSoftware>\n"
            + "      <thePolymorphic class=\"with-named-fields\" serialization=\"custom\">\n"
            + "        <with-named-fields>\n"
            + "          <default>\n"
            + "            <theName>Thing</theName>\n"
            + "            <theNumber>0</theNumber>\n"
            + "          </default>\n"
            + "        </with-named-fields>\n"
            + "      </thePolymorphic>\n"
            + "    </default>\n"
            + "  </with-named-fields>\n"
            + "</with-named-fields>";

        assertBothWays(outer, expectedXml);
    }

    public static class NoDefaultFields extends StandardObject implements Serializable {
        private static final long serialVersionUID = 200501L;
        private transient int something;

        public NoDefaultFields() {
        }

        public NoDefaultFields(final int something) {
            this.something = something;
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            something = in.readInt();
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeInt(something);
        }

    }

    public void testObjectWithCallToDefaultWriteButNoDefaultFields() {
        xstream.alias("x", NoDefaultFields.class);

        final String expectedXml = ""
            + "<x serialization=\"custom\">\n"
            + "  <x>\n"
            + "    <default/>\n"
            + "    <int>77</int>\n"
            + "  </x>\n"
            + "</x>";
        assertBothWays(new NoDefaultFields(77), expectedXml);
    }

    public void testMaintainsBackwardsCompatabilityWithXStream1_1_0FieldFormat() {
        final ObjectWithNamedFields outer = new ObjectWithNamedFields();
        outer.name = "Joe";
        outer.someSoftware = new Software("tw", "xs");
        outer.nothing = null;

        final ObjectWithNamedFields inner = new ObjectWithNamedFields();
        inner.name = "Thing";

        outer.polymorphic = inner;

        xstream.alias("with-named-fields", ObjectWithNamedFields.class);
        xstream.alias("software", Software.class);

        final String oldFormatOfXml = ""
            + "<with-named-fields serialization=\"custom\">\n"
            + "  <with-named-fields>\n"
            + "    <fields>\n"
            + "      <field name=\"theName\" class=\"string\">Joe</field>\n"
            + "      <field name=\"theNumber\" class=\"int\">0</field>\n"
            + "      <field name=\"theSoftware\" class=\"software\">\n"
            + "        <vendor>tw</vendor>\n"
            + "        <name>xs</name>\n"
            + "      </field>\n"
            + "      <field name=\"thePolymorphic\" class=\"with-named-fields\" serialization=\"custom\">\n"
            + "        <with-named-fields>\n"
            + "          <fields>\n"
            + "            <field name=\"theName\" class=\"string\">Thing</field>\n"
            + "            <field name=\"theNumber\" class=\"int\">0</field>\n"
            + "          </fields>\n"
            + "        </with-named-fields>\n"
            + "      </field>\n"
            + "    </fields>\n"
            + "  </with-named-fields>\n"
            + "</with-named-fields>";

        assertEquals(outer, xstream.fromXML(oldFormatOfXml));
    }

    public static class ObjectWithNamedThatMatchRealFields extends StandardObject implements Serializable {
        private static final long serialVersionUID = 200502L;
        private String name;
        private int number;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            final ObjectOutputStream.PutField fields = out.putFields();
            fields.put("name", name.toUpperCase());
            fields.put("number", number * 100);
            out.writeFields();
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            final ObjectInputStream.GetField fields = in.readFields();
            name = ((String)fields.get("name", "unknown")).toLowerCase();
            number = fields.get("number", 10000) / 100;
        }

    }

    public void testSupportsWritingFieldsForObjectsThatDoNotExplicitlyDefineThem() {
        xstream.alias("an-object", ObjectWithNamedThatMatchRealFields.class);

        final ObjectWithNamedThatMatchRealFields input = new ObjectWithNamedThatMatchRealFields();
        input.name = "a name";
        input.number = 5;

        final String expectedXml = ""
            + "<an-object serialization=\"custom\">\n"
            + "  <an-object>\n"
            + "    <default>\n"
            + "      <name>A NAME</name>\n"
            + "      <number>500</number>\n"
            + "    </default>\n"
            + "  </an-object>\n"
            + "</an-object>";

        assertBothWays(input, expectedXml);
    }

    public static class ObjectThatReadsCustomFieldsButDoesNotWriteThem extends StandardObject implements Serializable {
        private static final long serialVersionUID = 200502L;
        @SuppressWarnings("unused")
        private String name;
        @SuppressWarnings("unused")
        private int number;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            final ObjectInputStream.GetField fields = in.readFields();
            name = (String)fields.get("name", "unknown");
            number = fields.get("number", 10000);
        }

    }

    public void testSupportsGetFieldsWithoutPutFields() {
        xstream.alias("an-object", ObjectThatReadsCustomFieldsButDoesNotWriteThem.class);

        final ObjectThatReadsCustomFieldsButDoesNotWriteThem input =
                new ObjectThatReadsCustomFieldsButDoesNotWriteThem();
        input.name = "a name";
        input.number = 5;

        final String expectedXml = ""
            + "<an-object serialization=\"custom\">\n"
            + "  <an-object>\n"
            + "    <default>\n"
            + "      <number>5</number>\n"
            + "      <name>a name</name>\n"
            + "    </default>\n"
            + "  </an-object>\n"
            + "</an-object>";

        assertBothWays(input, expectedXml);
    }

    public static class ObjectThatWritesCustomFieldsButDoesNotReadThem extends StandardObject implements Serializable {
        private static final long serialVersionUID = 201502L;

        private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("number", int.class), new ObjectStreamField("name", String.class),};

        private void writeObject(final ObjectOutputStream out) throws IOException {
            final ObjectOutputStream.PutField fields = out.putFields();
            fields.put("name", "test");
            fields.put("number", 42);
            out.writeFields();
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    public void testSupportsPutFieldsWithoutGetFields() {
        xstream.alias("an-object", ObjectThatWritesCustomFieldsButDoesNotReadThem.class);

        final ObjectThatWritesCustomFieldsButDoesNotReadThem input =
                new ObjectThatWritesCustomFieldsButDoesNotReadThem();

        final String expectedXml = ""
            + "<an-object serialization=\"custom\">\n"
            + "  <an-object>\n"
            + "    <default>\n"
            + "      <name>test</name>\n"
            + "      <number>42</number>\n"
            + "    </default>\n"
            + "  </an-object>\n"
            + "</an-object>";

        assertBothWays(input, expectedXml);
    }
}
