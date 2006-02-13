package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Hardware;
import com.thoughtworks.acceptance.objects.Software;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

public class CustomSerializationTest extends AbstractAcceptanceTest {

    public static class ObjectWithCustomSerialization extends StandardObject implements Serializable {

        private int a;
        private transient int b;
        private transient String c;
        private transient Object d;
        private transient Software e;

        public ObjectWithCustomSerialization() {
        }

        public ObjectWithCustomSerialization(int a, int b, String c, Software e) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.e = e;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            b = in.readInt();
            in.defaultReadObject();
            c = (String) in.readObject();
            d = in.readObject();
            e = (Software) in.readObject();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(b);
            out.defaultWriteObject();
            out.writeObject(c);
            out.writeObject(d);
            out.writeObject(e);
        }

    }

    public void testWritesCustomFieldsToStream() {
        ObjectWithCustomSerialization obj = new ObjectWithCustomSerialization(1, 2, "hello", new Software("tw", "xs"));
        xstream.alias("custom", ObjectWithCustomSerialization.class);
        xstream.alias("software", Software.class);

        String expectedXml = ""
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

        private transient int parentA;
        private int parentB;
        private transient int parentC;

        public Parent() {
        }

        public Parent(int parentA, int parentB, int parentC) {
            this.parentA = parentA;
            this.parentB = parentB;
            this.parentC = parentC;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            parentA = in.readInt();
            in.defaultReadObject();
            parentC = in.readInt();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(parentA);
            out.defaultWriteObject();
            out.writeInt(parentC);
        }
    }

    public static class Child extends Parent {

        private transient int childA;
        private int childB;
        private transient int childC;

        public Child() {
        }

        public Child(int parentA, int parentB, int parentC, int childA, int childB, int childC) {
            super(parentA, parentB, parentC);
            this.childA = childA;
            this.childB = childB;
            this.childC = childC;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            childA = in.readInt();
            in.defaultReadObject();
            childC = in.readInt();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeInt(childA);
            out.defaultWriteObject();
            out.writeInt(childC);
        }
    }

    public void testIncludesCompleteClassHeirarchy() {
        Child child = new Child(1, 2, 3, 10, 20, 30);
        xstream.alias("child", Child.class);
        xstream.alias("parent", Parent.class);

        String expectedXml = ""
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

        private int childA;

        public Child2(int parentA, int parentB, int parentC, int childA) {
            super(parentA, parentB, parentC);
            this.childA = childA;
        }

    }

    public void testIncludesCompleteClassHeirarchy2() {
        Child2 child = new Child2(1, 2, 3, 20);
        xstream.alias("child2", Child2.class);
        xstream.alias("parent", Parent.class);

        String expectedXml = ""
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
        public MyDate(int time) {
            super(time);
        }
    }

    static class MyHashtable extends java.util.Hashtable {
        private String name;

        public MyHashtable(String name) {
            this.name = name;
        }

        public synchronized boolean equals(Object o) {
            return super.equals(o) && ((MyHashtable)o).name.equals(name);
        }
    }

    public void testSupportsSubclassesOfClassesThatAlreadyHaveConverters() {
        MyDate in = new MyDate(1234567890);
        String xml = xstream.toXML(in);
        assertObjectsEqual(in, xstream.fromXML(xml));

        MyHashtable in2 = new MyHashtable("hi");
        in2.put("cheese", "curry");
        in2.put("apple", new Integer(3));
        String xml2 = xstream.toXML(in2);
        assertObjectsEqual(in2, xstream.fromXML(xml2));
    }

    public static class ObjectWithNamedFields extends StandardObject implements Serializable {

        private String name;
        private int number;
        private Software someSoftware;
        private Object polymorphic;
        private Object nothing;

        private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("theName", String.class),
            new ObjectStreamField("theNumber", int.class),
            new ObjectStreamField("theSoftware", Software.class),
            new ObjectStreamField("thePolymorphic", Object.class),
            new ObjectStreamField("theNothing", Object.class)
        };

        private void writeObject(ObjectOutputStream out) throws IOException {
            // don't call defaultWriteObject()
            ObjectOutputStream.PutField fields = out.putFields();
            fields.put("theName", name);
            fields.put("theNumber", number);
            fields.put("theSoftware", someSoftware);
            fields.put("thePolymorphic", polymorphic);
            fields.put("theNothing", nothing);
            out.writeFields();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            // don't call defaultReadObject()
            ObjectInputStream.GetField fields = in.readFields();
            name = (String) fields.get("theName", "unknown");
            number = fields.get("theNumber", -1);
            someSoftware = (Software) fields.get("theSoftware", null);
            polymorphic = fields.get("thePolymorphic", null);
            nothing = fields.get("theNothing", null);
        }

    }

    public void testAllowsNamedFields() {
        ObjectWithNamedFields obj = new ObjectWithNamedFields();
        obj.name = "Joe";
        obj.number = 99;
        obj.someSoftware = new Software("tw", "xs");
        obj.polymorphic = new Hardware("small", "ipod");
        obj.nothing = null;

        xstream.alias("with-named-fields", ObjectWithNamedFields.class);
        xstream.alias("software", Software.class);

        String expectedXml = ""
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

        String inputXml = ""
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

        ObjectWithNamedFields result = (ObjectWithNamedFields) xstream.fromXML(inputXml);
        assertEquals(-1, result.number);
        assertEquals("unknown", result.name);
        assertEquals(new Software("tw", "xs"), result.someSoftware);
    }

    public void testCustomStreamWithNestedCustomStream() {
        ObjectWithNamedFields outer = new ObjectWithNamedFields();
        outer.name = "Joe";
        outer.someSoftware = new Software("tw", "xs");
        outer.nothing = null;

        ObjectWithNamedFields inner = new ObjectWithNamedFields();
        inner.name = "Thing";

        outer.polymorphic = inner;

        xstream.alias("with-named-fields", ObjectWithNamedFields.class);
        xstream.alias("software", Software.class);

        String expectedXml = ""
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

        private transient int something;

        public NoDefaultFields() {
        }

        public NoDefaultFields(int something) {
            this.something = something;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            something = in.readInt();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeInt(something);
        }

    }

    public void testObjectWithCallToDefaultWriteButNoDefaultFields() {
        xstream.alias("x", NoDefaultFields.class);

        String expectedXml = ""
                + "<x serialization=\"custom\">\n"
                + "  <x>\n"
                + "    <default/>\n"
                + "    <int>77</int>\n"
                + "  </x>\n"
                + "</x>";
        assertBothWays(new NoDefaultFields(77), expectedXml);
    }

    public void testMaintainsBackwardsCompatabilityWithXStream1_1_0FieldFormat() {
        ObjectWithNamedFields outer = new ObjectWithNamedFields();
        outer.name = "Joe";
        outer.someSoftware = new Software("tw", "xs");
        outer.nothing = null;

        ObjectWithNamedFields inner = new ObjectWithNamedFields();
        inner.name = "Thing";

        outer.polymorphic = inner;

        xstream.alias("with-named-fields", ObjectWithNamedFields.class);
        xstream.alias("software", Software.class);

        String oldFormatOfXml = ""
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

        private String name;
        private int number;

        private void writeObject(ObjectOutputStream out) throws IOException {
            ObjectOutputStream.PutField fields = out.putFields();
            fields.put("name", name.toUpperCase());
            fields.put("number", number * 100);
            out.writeFields();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            ObjectInputStream.GetField fields = in.readFields();
            name = ((String) fields.get("name", "unknown")).toLowerCase();
            number = fields.get("number", 10000) / 100;
        }

    }

    public void testSupportsWritingFieldsForObjectsThatDoNotExplicitlyDefineThem() {
        xstream.alias("an-object", ObjectWithNamedThatMatchRealFields.class);

        ObjectWithNamedThatMatchRealFields input = new ObjectWithNamedThatMatchRealFields();
        input.name = "a name";
        input.number = 5;

        String expectedXml = ""
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

        private String name;
        private int number;

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            ObjectInputStream.GetField fields = in.readFields();
            name = ((String) fields.get("name", "unknown"));
            number = fields.get("number", 10000);
        }

    }

    public void testSupportsGetFieldsWithoutPutFields() {
        xstream.alias("an-object", ObjectThatReadsCustomFieldsButDoesNotWriteThem.class);

        ObjectThatReadsCustomFieldsButDoesNotWriteThem input = new ObjectThatReadsCustomFieldsButDoesNotWriteThem();
        input.name = "a name";
        input.number = 5;

        String expectedXml = ""
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
}
