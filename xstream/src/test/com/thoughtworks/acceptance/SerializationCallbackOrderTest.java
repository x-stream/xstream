/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 02. February 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.testutil.CallLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationCallbackOrderTest extends AbstractAcceptanceTest {

    // static so it can be accessed by objects under test, without them needing a reference back to the testcase
    private static CallLog log = new CallLog();

    protected void setUp() throws Exception {
        super.setUp();
        log.reset();
    }
   

    // --- Sample class hierarchy

    public static class PrivateBase implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("PrivateBase.writeObject() start");
            out.defaultWriteObject();
            log.actual("PrivateBase.writeObject() end");
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("PrivateBase.readObject() start");
            in.defaultReadObject();
            log.actual("PrivateBase.readObject() end");
        }

        private Object writeReplace() {
            log.actual("PrivateBase.writeReplace()");
            return this;
        }

        private Object readResolve() {
            log.actual("PrivateBase.readResolve()");
            return this;
        }
    }

    public static class PrivateChildOwnRR extends PrivateBase implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("PrivateChildOwnRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("PrivateChildOwnRR.writeObject() end");
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("PrivateChildOwnRR.readObject() start");
            in.defaultReadObject();
            log.actual("PrivateChildOwnRR.readObject() end");
        }

        private Object writeReplace() {
            log.actual("PrivateChildOwnRR.writeReplace()");
            return this;
        }

        private Object readResolve() {
            log.actual("PrivateChildOwnRR.readResolve()");
            return this;
        }
    }

    public static class PrivateChildNoRR extends PrivateBase implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("PrivateChildNoRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("PrivateChildNoRR.writeObject() end");
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("PrivateChildNoRR.readObject() start");
            in.defaultReadObject();
            log.actual("PrivateChildNoRR.readObject() end");
        }
    }
    
    public static class ProtectedBase implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("ProtectedBase.writeObject() start");
            out.defaultWriteObject();
            log.actual("ProtectedBase.writeObject() end");
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("ProtectedBase.readObject() start");
            in.defaultReadObject();
            log.actual("ProtectedBase.readObject() end");
        }

        protected Object writeReplace() {
            log.actual("ProtectedBase.writeReplace()");
            return this;
        }

        protected Object readResolve() {
            log.actual("ProtectedBase.readResolve()");
            return this;
        }
    }

    public static class ProtectedChildOwnRR extends ProtectedBase implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("ProtectedChildOwnRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("ProtectedChildOwnRR.writeObject() end");
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("ProtectedChildOwnRR.readObject() start");
            in.defaultReadObject();
            log.actual("ProtectedChildOwnRR.readObject() end");
        }

        protected Object writeReplace() {
            log.actual("ProtectedChildOwnRR.writeReplace()");
            return this;
        }

        protected Object readResolve() {
            log.actual("ProtectedChildOwnRR.readResolve()");
            return this;
        }
    }

    public static class ProtectedChildInheritedRR extends ProtectedBase implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("ProtectedChildInheritedRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("ProtectedChildInheritedRR.writeObject() end");
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("ProtectedChildInheritedRR.readObject() start");
            in.defaultReadObject();
            log.actual("ProtectedChildInheritedRR.readObject() end");
        }
    }
    
    public static class PackageBase implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("PackageBase.writeObject() start");
            out.defaultWriteObject();
            log.actual("PackageBase.writeObject() end");
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("PackageBase.readObject() start");
            in.defaultReadObject();
            log.actual("PackageBase.readObject() end");
        }

        Object writeReplace() {
            log.actual("PackageBase.writeReplace()");
            return this;
        }

        Object readResolve() {
            log.actual("PackageBase.readResolve()");
            return this;
        }
    }

    public static class PackageChildOwnRR extends PackageBase implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("PackageChildOwnRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("PackageChildOwnRR.writeObject() end");
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("PackageChildOwnRR.readObject() start");
            in.defaultReadObject();
            log.actual("PackageChildOwnRR.readObject() end");
        }

        Object writeReplace() {
            log.actual("PackageChildOwnRR.writeReplace()");
            return this;
        }

        Object readResolve() {
            log.actual("PackageChildOwnRR.readResolve()");
            return this;
        }
    }

    public static class PackageChildInheritedRR extends PackageBase implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("PackageChildInheritedRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("PackageChildInheritedRR.writeObject() end");
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("PackageChildInheritedRR.readObject() start");
            in.defaultReadObject();
            log.actual("PackageChildInheritedRR.readObject() end");
        }
    }

    // --- Convenience wrappers around Java Object Serialization

    private byte[] javaSerialize(Object object) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(bytes);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return bytes.toByteArray();
    }

    private Object javaDeserialize(byte[] data) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        return objectInputStream.readObject();
    }

    // --- Tests

    public void testJavaSerializationOwnPrivateRR() throws IOException {
        // expectations
        log.expect("PrivateChildOwnRR.writeReplace()");
        log.expect("PrivateBase.writeObject() start");
        log.expect("PrivateBase.writeObject() end");
        log.expect("PrivateChildOwnRR.writeObject() start");
        log.expect("PrivateChildOwnRR.writeObject() end");

        // execute
        javaSerialize(new PrivateChildOwnRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationNoRR() throws IOException {
        // expectations
        log.expect("PrivateBase.writeObject() start");
        log.expect("PrivateBase.writeObject() end");
        log.expect("PrivateChildNoRR.writeObject() start");
        log.expect("PrivateChildNoRR.writeObject() end");

        // execute
        javaSerialize(new PrivateChildNoRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationOwnProtectedRR() throws IOException {
        // expectations
        log.expect("ProtectedChildOwnRR.writeReplace()");
        log.expect("ProtectedBase.writeObject() start");
        log.expect("ProtectedBase.writeObject() end");
        log.expect("ProtectedChildOwnRR.writeObject() start");
        log.expect("ProtectedChildOwnRR.writeObject() end");

        // execute
        javaSerialize(new ProtectedChildOwnRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationInheritedRR() throws IOException {
        // expectations
        log.expect("ProtectedBase.writeReplace()");
        log.expect("ProtectedBase.writeObject() start");
        log.expect("ProtectedBase.writeObject() end");
        log.expect("ProtectedChildInheritedRR.writeObject() start");
        log.expect("ProtectedChildInheritedRR.writeObject() end");

        // execute
        javaSerialize(new ProtectedChildInheritedRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationOwnPackageRR() throws IOException {
        // expectations
        log.expect("PackageChildOwnRR.writeReplace()");
        log.expect("PackageBase.writeObject() start");
        log.expect("PackageBase.writeObject() end");
        log.expect("PackageChildOwnRR.writeObject() start");
        log.expect("PackageChildOwnRR.writeObject() end");

        // execute
        javaSerialize(new PackageChildOwnRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationInheritedPackageRR() throws IOException {
        // expectations
        log.expect("PackageBase.writeReplace()");
        log.expect("PackageBase.writeObject() start");
        log.expect("PackageBase.writeObject() end");
        log.expect("PackageChildInheritedRR.writeObject() start");
        log.expect("PackageChildInheritedRR.writeObject() end");

        // execute
        javaSerialize(new PackageChildInheritedRR());

        // verify
        log.verify();
    }

    public void testXStreamSerializationOwnPrivateRR() {
        // expectations
        log.expect("PrivateChildOwnRR.writeReplace()");
        log.expect("PrivateBase.writeObject() start");
        log.expect("PrivateBase.writeObject() end");
        log.expect("PrivateChildOwnRR.writeObject() start");
        log.expect("PrivateChildOwnRR.writeObject() end");

        // execute
        xstream.toXML(new PrivateChildOwnRR());

        // verify
        log.verify();
    }

    public void testXStreamSerializationNoRR() {
        // expectations
        log.expect("PrivateBase.writeObject() start");
        log.expect("PrivateBase.writeObject() end");
        log.expect("PrivateChildNoRR.writeObject() start");
        log.expect("PrivateChildNoRR.writeObject() end");

        // execute
        xstream.toXML(new PrivateChildNoRR());

        // verify
        log.verify();
    }

    public void testXStreamSerializationOwnProtectedRR() {
        // expectations
        log.expect("ProtectedChildOwnRR.writeReplace()");
        log.expect("ProtectedBase.writeObject() start");
        log.expect("ProtectedBase.writeObject() end");
        log.expect("ProtectedChildOwnRR.writeObject() start");
        log.expect("ProtectedChildOwnRR.writeObject() end");

        // execute
        xstream.toXML(new ProtectedChildOwnRR());

        // verify
        log.verify();
    }

    public void testXStreamSerializationOwnInheritedRR() {
        // expectations
        log.expect("ProtectedBase.writeReplace()");
        log.expect("ProtectedBase.writeObject() start");
        log.expect("ProtectedBase.writeObject() end");
        log.expect("ProtectedChildInheritedRR.writeObject() start");
        log.expect("ProtectedChildInheritedRR.writeObject() end");

        // execute
        xstream.toXML(new ProtectedChildInheritedRR());

        // verify
        log.verify();
    }

    public void testXStreamSerializationOwnPackageRR() {
        // expectations
        log.expect("PackageChildOwnRR.writeReplace()");
        log.expect("PackageBase.writeObject() start");
        log.expect("PackageBase.writeObject() end");
        log.expect("PackageChildOwnRR.writeObject() start");
        log.expect("PackageChildOwnRR.writeObject() end");

        // execute
        xstream.toXML(new PackageChildOwnRR());

        // verify
        log.verify();
    }

    public void testXStreamSerializationOwnInheritedPackageRR() {
        // expectations
        log.expect("PackageBase.writeReplace()");
        log.expect("PackageBase.writeObject() start");
        log.expect("PackageBase.writeObject() end");
        log.expect("PackageChildInheritedRR.writeObject() start");
        log.expect("PackageChildInheritedRR.writeObject() end");

        // execute
        xstream.toXML(new PackageChildInheritedRR());

        // verify
        log.verify();
    }

    public void testJavaDeserializationOwnPrivateRR() throws IOException, ClassNotFoundException {
        // setup
        byte[] data = javaSerialize(new PrivateChildOwnRR());
        log.reset();

        // expectations
        log.expect("PrivateBase.readObject() start");
        log.expect("PrivateBase.readObject() end");
        log.expect("PrivateChildOwnRR.readObject() start");
        log.expect("PrivateChildOwnRR.readObject() end");
        log.expect("PrivateChildOwnRR.readResolve()");

        // execute
        javaDeserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationNoRR() throws IOException, ClassNotFoundException {
        // setup
        byte[] data = javaSerialize(new PrivateChildNoRR());
        log.reset();

        // expectations
        log.expect("PrivateBase.readObject() start");
        log.expect("PrivateBase.readObject() end");
        log.expect("PrivateChildNoRR.readObject() start");
        log.expect("PrivateChildNoRR.readObject() end");

        // execute
        javaDeserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationOwnProtectedRR() throws IOException, ClassNotFoundException {
        // setup
        byte[] data = javaSerialize(new ProtectedChildOwnRR());
        log.reset();

        // expectations
        log.expect("ProtectedBase.readObject() start");
        log.expect("ProtectedBase.readObject() end");
        log.expect("ProtectedChildOwnRR.readObject() start");
        log.expect("ProtectedChildOwnRR.readObject() end");
        log.expect("ProtectedChildOwnRR.readResolve()");

        // execute
        javaDeserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationInheritedRR() throws IOException, ClassNotFoundException {
        // setup
        byte[] data = javaSerialize(new ProtectedChildInheritedRR());
        log.reset();

        // expectations
        log.expect("ProtectedBase.readObject() start");
        log.expect("ProtectedBase.readObject() end");
        log.expect("ProtectedChildInheritedRR.readObject() start");
        log.expect("ProtectedChildInheritedRR.readObject() end");
        log.expect("ProtectedBase.readResolve()");

        // execute
        javaDeserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationOwnPackageRR() throws IOException, ClassNotFoundException {
        // setup
        byte[] data = javaSerialize(new PackageChildOwnRR());
        log.reset();

        // expectations
        log.expect("PackageBase.readObject() start");
        log.expect("PackageBase.readObject() end");
        log.expect("PackageChildOwnRR.readObject() start");
        log.expect("PackageChildOwnRR.readObject() end");
        log.expect("PackageChildOwnRR.readResolve()");

        // execute
        javaDeserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationInheritedPackageRR() throws IOException, ClassNotFoundException {
        // setup
        byte[] data = javaSerialize(new PackageChildInheritedRR());
        log.reset();

        // expectations
        log.expect("PackageBase.readObject() start");
        log.expect("PackageBase.readObject() end");
        log.expect("PackageChildInheritedRR.readObject() start");
        log.expect("PackageChildInheritedRR.readObject() end");
        log.expect("PackageBase.readResolve()");

        // execute
        javaDeserialize(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationOwnPrivateRR() {
        // setup
        String data = xstream.toXML(new PrivateChildOwnRR());
        log.reset();

        // expectations
        log.expect("PrivateBase.readObject() start");
        log.expect("PrivateBase.readObject() end");
        log.expect("PrivateChildOwnRR.readObject() start");
        log.expect("PrivateChildOwnRR.readObject() end");
        log.expect("PrivateChildOwnRR.readResolve()");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationNoRR() {
        // setup
        String data = xstream.toXML(new PrivateChildNoRR());
        log.reset();

        // expectations
        log.expect("PrivateBase.readObject() start");
        log.expect("PrivateBase.readObject() end");
        log.expect("PrivateChildNoRR.readObject() start");
        log.expect("PrivateChildNoRR.readObject() end");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationOwnProtectedRR() {
        // setup
        String data = xstream.toXML(new ProtectedChildOwnRR());
        log.reset();

        // expectations
        log.expect("ProtectedBase.readObject() start");
        log.expect("ProtectedBase.readObject() end");
        log.expect("ProtectedChildOwnRR.readObject() start");
        log.expect("ProtectedChildOwnRR.readObject() end");
        log.expect("ProtectedChildOwnRR.readResolve()");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationInheritedRR() {
        // setup
        String data = xstream.toXML(new ProtectedChildInheritedRR());
        log.reset();

        // expectations
        log.expect("ProtectedBase.readObject() start");
        log.expect("ProtectedBase.readObject() end");
        log.expect("ProtectedChildInheritedRR.readObject() start");
        log.expect("ProtectedChildInheritedRR.readObject() end");
        log.expect("ProtectedBase.readResolve()");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationOwnPackageRR() {
        // setup
        String data = xstream.toXML(new PackageChildOwnRR());
        log.reset();

        // expectations
        log.expect("PackageBase.readObject() start");
        log.expect("PackageBase.readObject() end");
        log.expect("PackageChildOwnRR.readObject() start");
        log.expect("PackageChildOwnRR.readObject() end");
        log.expect("PackageChildOwnRR.readResolve()");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationInheritedPackageRR() {
        // setup
        String data = xstream.toXML(new PackageChildInheritedRR());
        log.reset();

        // expectations
        log.expect("PackageBase.readObject() start");
        log.expect("PackageBase.readObject() end");
        log.expect("PackageChildInheritedRR.readObject() start");
        log.expect("PackageChildInheritedRR.readObject() end");
        log.expect("PackageBase.readResolve()");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public static class ParentNotTransient implements Serializable {

        public int somethingNotTransient;

        public ParentNotTransient(int somethingNotTransient) {
            this.somethingNotTransient = somethingNotTransient;
        }

    }

    public static class ChildWithTransient extends ParentNotTransient implements Serializable {

        public transient int somethingTransient;

        public ChildWithTransient(int somethingNotTransient, int somethingTransient) {
            super(somethingNotTransient);
            this.somethingTransient = somethingTransient;
        }

        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            somethingTransient = 99999;
        }
    }

    public void testCallsReadObjectEvenWithoutNonTransientFields() {
        xstream.alias("parent", ParentNotTransient.class);
        xstream.alias("child", ChildWithTransient.class);

        Object in = new ChildWithTransient(10, 22222);
        String expectedXml = ""
                + "<child serialization=\"custom\">\n"
                + "  <parent>\n"
                + "    <default>\n"
                + "      <somethingNotTransient>10</somethingNotTransient>\n"
                + "    </default>\n"
                + "  </parent>\n"
                + "  <child>\n"
                + "    <default/>\n"
                + "  </child>\n"
                + "</child>";

        String xml = xstream.toXML(in);
        assertEquals(expectedXml, xml);

        ChildWithTransient childWithTransient = (ChildWithTransient) xstream.fromXML(xml);

        assertEquals(10, childWithTransient.somethingNotTransient);
        assertEquals(99999, childWithTransient.somethingTransient);
    }


    public static class SomethingThatValidates implements Serializable {

        private void readObject(ObjectInputStream s) throws IOException {

            final int LOW_PRIORITY = -5;
            final int MEDIUM_PRIORITY = 0;
            final int HIGH_PRIORITY = 5;

            s.registerValidation(new ObjectInputValidation() {
                public void validateObject() {
                    log.actual("validateObject() medium priority 1");
                }
            }, MEDIUM_PRIORITY);

            s.registerValidation(new ObjectInputValidation() {
                public void validateObject() {
                    log.actual("validateObject() high priority");
                }
            }, HIGH_PRIORITY);

            s.registerValidation(new ObjectInputValidation() {
                public void validateObject() {
                    log.actual("validateObject() low priority");
                }
            }, LOW_PRIORITY);

            s.registerValidation(new ObjectInputValidation() {
                public void validateObject() {
                    log.actual("validateObject() medium priority 2");
                }
            }, MEDIUM_PRIORITY);
        }
        
        private Object readResolve() {
            log.actual("readResolve()");
            return this;
        }
    }

    public void testJavaSerializationValidatesObjectIsCalledInPriorityOrder() throws IOException, ClassNotFoundException {
        // expect
        log.expect("readResolve()");
        log.expect("validateObject() high priority");
        log.expect("validateObject() medium priority 2");
        log.expect("validateObject() medium priority 1");
        log.expect("validateObject() low priority");

        // execute
        javaDeserialize(javaSerialize(new SomethingThatValidates()));

        // verify
        log.verify();
    }

    public void testXStreamSerializationValidatesObjectIsCalledInPriorityOrder() {
        // expect
        log.expect("readResolve()");
        log.expect("validateObject() high priority");
        log.expect("validateObject() medium priority 2");
        log.expect("validateObject() medium priority 1");
        log.expect("validateObject() low priority");

        // execute
        xstream.fromXML(xstream.toXML(new SomethingThatValidates()));

        // verify
        log.verify();
    }

    public static class UnserializableParent {
        public int x;

        public UnserializableParent() {
            x = 5;
        }
    }

    public static class CustomSerializableChild extends UnserializableParent implements Serializable {
        public int y;

        public CustomSerializableChild() {
            y = 10;
        }

        private void writeObject(ObjectOutputStream stream) throws IOException {
            log.actual("Child.writeObject() start");
            stream.defaultWriteObject();
            log.actual("Child.writeObject() end");
        }

        private void readObject(ObjectInputStream stream)
                throws IOException, ClassNotFoundException {
            log.actual("Child.readObject() start");
            stream.defaultReadObject();
            log.actual("Child.readObject() end");
        }

        private Object writeReplace() {
            log.actual("Child.writeReplace()");
            return this;
        }

        private Object readResolve() {
            log.actual("Child.readResolve()");
            return this;
        }
    }

    public void testFieldsOfUnserializableParentsArePreserved() {
        xstream.alias("parent", UnserializableParent.class);
        xstream.alias("child", CustomSerializableChild.class);

        CustomSerializableChild child = new CustomSerializableChild();
        String expected = ""
                + "<child serialization=\"custom\">\n"
                + "  <unserializable-parents>\n"
                + "    <x>5</x>\n"
                + "  </unserializable-parents>\n"
                + "  <child>\n"
                + "    <default>\n"
                + "      <y>10</y>\n"
                + "    </default>\n"
                + "  </child>\n"
                + "</child>";

        CustomSerializableChild serialized =(CustomSerializableChild)assertBothWays(child, expected);
        assertEquals(5, serialized.x);
        assertEquals(10, serialized.y);
    }
    
    public static class SerializableGrandChild extends CustomSerializableChild implements Serializable {
        public int z;

        public SerializableGrandChild() {
            super();
            z = 42;
        }
    }
    
    public void testUnserializableParentsAreWrittenOnlyOnce() {
        xstream.alias("parent", UnserializableParent.class);
        xstream.alias("child", CustomSerializableChild.class);
        xstream.alias("grandchild", SerializableGrandChild.class);
        
        SerializableGrandChild grandChild = new SerializableGrandChild();
        String expected = ""
                + "<grandchild serialization=\"custom\">\n"
                + "  <unserializable-parents>\n"
                + "    <x>5</x>\n"
                + "  </unserializable-parents>\n"
                + "  <child>\n"
                + "    <default>\n"
                + "      <y>10</y>\n"
                + "    </default>\n"
                + "  </child>\n"
                + "  <grandchild>\n"
                + "    <default>\n"
                + "      <z>42</z>\n"
                + "    </default>\n"
                + "  </grandchild>\n"
                + "</grandchild>";

        SerializableGrandChild serialized =(SerializableGrandChild)assertBothWays(grandChild, expected);
        assertEquals(5, serialized.x);
        assertEquals(10, serialized.y);
        assertEquals(42, serialized.z);
    }

    public void testXStreamSerializationForObjectsWithUnserializableParents() {
        // expectations
        log.expect("Child.writeReplace()");
        log.expect("Child.writeObject() start");
        log.expect("Child.writeObject() end");

        // execute
        xstream.toXML(new CustomSerializableChild());

        // verify
        log.verify();
    }

    public void testXStreamDeserializationForObjectsWithUnserializableParents() {
        // setup
        String data = xstream.toXML(new CustomSerializableChild());
        log.reset();

        // expectations
        log.expect("Child.readObject() start");
        log.expect("Child.readObject() end");
        log.expect("Child.readResolve()");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

}
