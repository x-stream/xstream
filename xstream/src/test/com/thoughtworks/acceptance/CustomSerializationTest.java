package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Software;

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
                + "  <com.thoughtworks.acceptance.StandardObject>\n"
                + "    <default/>\n"
                + "  </com.thoughtworks.acceptance.StandardObject>\n"
                + "  <object>\n"
                + "    <default/>\n"
                + "  </object>\n"
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
                + "  <child>\n"
                + "    <int>10</int>\n"
                + "    <default>\n"
                + "      <childB>20</childB>\n"
                + "    </default>\n"
                + "    <int>30</int>\n"
                + "  </child>\n"
                + "  <parent>\n"
                + "    <int>1</int>\n"
                + "    <default>\n"
                + "      <parentB>2</parentB>\n"
                + "    </default>\n"
                + "    <int>3</int>\n"
                + "  </parent>\n"
                + "  <com.thoughtworks.acceptance.StandardObject>\n"
                + "    <default/>\n"
                + "  </com.thoughtworks.acceptance.StandardObject>\n"
                + "  <object>\n"
                + "    <default/>\n"
                + "  </object>\n"
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
                + "  <child2>\n"
                + "    <default>\n"
                + "      <childA>20</childA>\n"
                + "    </default>\n"
                + "  </child2>\n"
                + "  <parent>\n"
                + "    <int>1</int>\n"
                + "    <default>\n"
                + "      <parentB>2</parentB>\n"
                + "    </default>\n"
                + "    <int>3</int>\n"
                + "  </parent>\n"
                + "  <com.thoughtworks.acceptance.StandardObject>\n"
                + "    <default/>\n"
                + "  </com.thoughtworks.acceptance.StandardObject>\n"
                + "  <object>\n"
                + "    <default/>\n"
                + "  </object>\n"
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
}
