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
                + "<custom>\n"
                + "  <field.int>2</field.int>\n"
                + "  <a>1</a>\n"
                + "  <field.string>hello</field.string>\n"
                + "  <field.null/>\n"
                + "  <field.software>\n"
                + "    <vendor>tw</vendor>\n"
                + "    <name>xs</name>\n"
                + "  </field.software>\n"
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

    public static class Child extends Parent implements Serializable {

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

    public void xtestIncludesCompleteClassHeirarchy() {
        Child child = new Child(1, 2, 3, 10, 20, 30);
        xstream.alias("child", Child.class);
        xstream.alias("parent", Parent.class);

        String expectedXml = ""
                + "<child>\n"
                + "  <field.int>10</field.int>\n"
                + "  <childB>20</childB>\n"
                + "  <field.int>30</field.int>\n"
                + "  <field.int defined-in=\"parent\">1</field.int>\n"
                + "  <parentB>2</parentB>\n"
                + "  <field.int defined-in=\"parent\">3</field.int>\n"
                + "</child>";

        assertBothWays(child, expectedXml);
    }

    static class MyDate extends java.util.Date {
        public MyDate(int time) {
            super(time);
        }
    }

    public void testSupportsSubclassesOfClassesThatAlreadyHaveConverters() {
        MyDate in = new MyDate(1234567890);
        String xml = xstream.toXML(in);
        MyDate out = (MyDate) xstream.fromXML(xml);
        assertEquals(in, out);
    }
}
