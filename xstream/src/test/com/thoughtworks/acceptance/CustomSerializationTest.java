package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.objects.Hardware;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CustomSerializationTest extends AbstractAcceptanceTest {

    public static class ObjectWithCustomSerialization extends StandardObject implements Serializable {

        private int a;
        private transient int b;
        private transient String c;
        private transient Object d;
        private transient Software e;

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

        private void writeObject(ObjectOutputStream out) throws IOException {
            // don't call defaultWriteObject()
            ObjectOutputStream.PutField fields = out.putFields();
            fields.put("the-name", name);
            fields.put("the-number", number);
            fields.put("the-software", someSoftware);
            fields.put("the-polymorphic", polymorphic);
            fields.put("the-nothing", nothing);
            out.writeFields();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            // don't call defaultReadObject()
            ObjectInputStream.GetField fields = in.readFields();
            name = (String) fields.get("the-name", "unknown");
            number = fields.get("the-number", -1);
            someSoftware = (Software) fields.get("the-software", null);
            polymorphic = fields.get("the-polymorphic", null);
            nothing = fields.get("the-nothing", null);
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
                + "    <fields>\n"
                + "      <field name=\"the-polymorphic\" class=\"com.thoughtworks.acceptance.objects.Hardware\">\n"
                + "        <arch>small</arch>\n"
                + "        <name>ipod</name>\n"
                + "      </field>\n"
                + "      <field name=\"the-software\" class=\"software\">\n"
                + "        <vendor>tw</vendor>\n"
                + "        <name>xs</name>\n"
                + "      </field>\n"
                + "      <field name=\"the-name\" class=\"string\">Joe</field>\n"
                + "      <field name=\"the-number\" class=\"int\">99</field>\n"
                + "    </fields>\n"
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
                + "    <fields>\n"
                + "      <field name=\"the-polymorphic\" class=\"com.thoughtworks.acceptance.objects.Hardware\">\n"
                + "        <arch>small</arch>\n"
                + "        <name>ipod</name>\n"
                + "      </field>\n"
                + "      <field name=\"the-software\" class=\"software\">\n"
                + "        <vendor>tw</vendor>\n"
                + "        <name>xs</name>\n"
                + "      </field>\n"
                + "    </fields>\n"
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
                + "    <fields>\n"
                + "      <field name=\"the-polymorphic\" class=\"with-named-fields\" serialization=\"custom\">\n"
                + "        <with-named-fields>\n"
                + "          <fields>\n"
                + "            <field name=\"the-name\" class=\"string\">Thing</field>\n"
                + "            <field name=\"the-number\" class=\"int\">0</field>\n"
                + "          </fields>\n"
                + "        </with-named-fields>\n"
                + "      </field>\n"
                + "      <field name=\"the-software\" class=\"software\">\n"
                + "        <vendor>tw</vendor>\n"
                + "        <name>xs</name>\n"
                + "      </field>\n"
                + "      <field name=\"the-name\" class=\"string\">Joe</field>\n"
                + "      <field name=\"the-number\" class=\"int\">0</field>\n"
                + "    </fields>\n"
                + "  </with-named-fields>\n"
                + "</with-named-fields>";

        assertBothWays(outer, expectedXml);
    }

    public static class NoDefaultFields extends StandardObject implements Serializable {

        private transient int something;

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

}
