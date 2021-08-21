/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 02. February 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.SerializableConverter;
import com.thoughtworks.xstream.testutil.CallLog;


public class SerializationCallbackOrderTest extends AbstractAcceptanceTest {

    // static so it can be accessed by objects under test, without them needing a reference back to the test case
    private static CallLog log = new CallLog();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        log.reset();
    }

    // --- Sample class hierarchy

    public static class PrivateBase implements Serializable {

        private static final long serialVersionUID = 200502L;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("PrivateBase.writeObject() start");
            out.defaultWriteObject();
            log.actual("PrivateBase.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
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

    public static class PrivateChildOwnRR extends PrivateBase implements Serializable {

        private static final long serialVersionUID = 200502L;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("PrivateChildOwnRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("PrivateChildOwnRR.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
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

    public static class PrivateChildNoRR extends PrivateBase implements Serializable {

        private static final long serialVersionUID = 201410L;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("PrivateChildNoRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("PrivateChildNoRR.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("PrivateChildNoRR.readObject() start");
            in.defaultReadObject();
            log.actual("PrivateChildNoRR.readObject() end");
        }
    }

    public static class ProtectedBase implements Serializable {

        private static final long serialVersionUID = 201410L;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("ProtectedBase.writeObject() start");
            out.defaultWriteObject();
            log.actual("ProtectedBase.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
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

    public static class ProtectedChildOwnRR extends ProtectedBase implements Serializable {

        private static final long serialVersionUID = 201410L;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("ProtectedChildOwnRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("ProtectedChildOwnRR.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("ProtectedChildOwnRR.readObject() start");
            in.defaultReadObject();
            log.actual("ProtectedChildOwnRR.readObject() end");
        }

        @Override
        protected Object writeReplace() {
            log.actual("ProtectedChildOwnRR.writeReplace()");
            return this;
        }

        @Override
        protected Object readResolve() {
            log.actual("ProtectedChildOwnRR.readResolve()");
            return this;
        }
    }

    public static class ProtectedChildInheritedRR extends ProtectedBase implements Serializable {

        private static final long serialVersionUID = 201410L;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("ProtectedChildInheritedRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("ProtectedChildInheritedRR.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("ProtectedChildInheritedRR.readObject() start");
            in.defaultReadObject();
            log.actual("ProtectedChildInheritedRR.readObject() end");
        }
    }

    public static class PackageBase implements Serializable {

        private static final long serialVersionUID = 201410L;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("PackageBase.writeObject() start");
            out.defaultWriteObject();
            log.actual("PackageBase.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
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

    public static class PackageChildOwnRR extends PackageBase implements Serializable {

        private static final long serialVersionUID = 201410L;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("PackageChildOwnRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("PackageChildOwnRR.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("PackageChildOwnRR.readObject() start");
            in.defaultReadObject();
            log.actual("PackageChildOwnRR.readObject() end");
        }

        @Override
        Object writeReplace() {
            log.actual("PackageChildOwnRR.writeReplace()");
            return this;
        }

        @Override
        Object readResolve() {
            log.actual("PackageChildOwnRR.readResolve()");
            return this;
        }
    }

    public static class PackageChildInheritedRR extends PackageBase implements Serializable {

        private static final long serialVersionUID = 201410L;

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("PackageChildInheritedRR.writeObject() start");
            out.defaultWriteObject();
            log.actual("PackageChildInheritedRR.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("PackageChildInheritedRR.readObject() start");
            in.defaultReadObject();
            log.actual("PackageChildInheritedRR.readObject() end");
        }
    }

    public static class UnserializableBase {
        protected UnserializableBase() {
            log.actual("UnserializableBase.UnserializableBase()");
        }
    }

    public static class ChildUnserializableBase extends UnserializableBase implements Serializable {

        private static final long serialVersionUID = 202107L;

        public ChildUnserializableBase(final String s) {
            log.actual("ChildUnserializableBase.ChildUnserializableBase(String)");
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("ChildUnserializableBase.writeObject() start");
            out.defaultWriteObject();
            log.actual("ChildUnserializableBase.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("ChildUnserializableBase.readObject() start");
            in.defaultReadObject();
            log.actual("ChildUnserializableBase.readObject() end");
        }
    }

    public static class ChildUnserializableBaseRR extends ChildUnserializableBase {
        private static final long serialVersionUID = 202107L;

        private ChildUnserializableBaseRR() {
            super("");
            log.actual("ChildUnserializableBaseRR.ChildUnserializableBaseRR()");
        }

        public ChildUnserializableBaseRR(final String s) {
            super(s);
            log.actual("ChildUnserializableBaseRR.ChildUnserializableBaseRR(String)");
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            log.actual("ChildUnserializableBaseRR.writeObject() start");
            out.defaultWriteObject();
            out.writeInt(42);
            log.actual("ChildUnserializableBaseRR.writeObject() end");
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("ChildUnserializableBaseRR.readObject() start");
            in.defaultReadObject();
            in.readInt();
            log.actual("ChildUnserializableBaseRR.readObject() end");
        }

        Object writeReplace() {
            log.actual("ChildUnserializableBaseRR.writeReplace()");
            return this;
        }

        Object readResolve() {
            log.actual("ChildUnserializableBaseRR.readResolve()");
            return this;
        }
    }

    // --- Tests

    public void testJavaSerializationOwnPrivateRR() {
        // expectations
        log.expect("PrivateChildOwnRR.writeReplace()");
        log.expect("PrivateBase.writeObject() start");
        log.expect("PrivateBase.writeObject() end");
        log.expect("PrivateChildOwnRR.writeObject() start");
        log.expect("PrivateChildOwnRR.writeObject() end");

        // execute
        serialize(new PrivateChildOwnRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationNoRR() {
        // expectations
        log.expect("PrivateBase.writeObject() start");
        log.expect("PrivateBase.writeObject() end");
        log.expect("PrivateChildNoRR.writeObject() start");
        log.expect("PrivateChildNoRR.writeObject() end");

        // execute
        serialize(new PrivateChildNoRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationOwnProtectedRR() {
        // expectations
        log.expect("ProtectedChildOwnRR.writeReplace()");
        log.expect("ProtectedBase.writeObject() start");
        log.expect("ProtectedBase.writeObject() end");
        log.expect("ProtectedChildOwnRR.writeObject() start");
        log.expect("ProtectedChildOwnRR.writeObject() end");

        // execute
        serialize(new ProtectedChildOwnRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationInheritedRR() {
        // expectations
        log.expect("ProtectedBase.writeReplace()");
        log.expect("ProtectedBase.writeObject() start");
        log.expect("ProtectedBase.writeObject() end");
        log.expect("ProtectedChildInheritedRR.writeObject() start");
        log.expect("ProtectedChildInheritedRR.writeObject() end");

        // execute
        serialize(new ProtectedChildInheritedRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationOwnPackageRR() {
        // expectations
        log.expect("PackageChildOwnRR.writeReplace()");
        log.expect("PackageBase.writeObject() start");
        log.expect("PackageBase.writeObject() end");
        log.expect("PackageChildOwnRR.writeObject() start");
        log.expect("PackageChildOwnRR.writeObject() end");

        // execute
        serialize(new PackageChildOwnRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationInheritedPackageRR() {
        // expectations
        log.expect("PackageBase.writeReplace()");
        log.expect("PackageBase.writeObject() start");
        log.expect("PackageBase.writeObject() end");
        log.expect("PackageChildInheritedRR.writeObject() start");
        log.expect("PackageChildInheritedRR.writeObject() end");

        // execute
        serialize(new PackageChildInheritedRR());

        // verify
        log.verify();
    }

    public void testJavaSerializationUnserializableBase() {
        final Serializable object = new ChildUnserializableBase("");
        log.reset();

        // expectations
        log.expect("ChildUnserializableBase.writeObject() start");
        log.expect("ChildUnserializableBase.writeObject() end");

        // execute
        serialize(object);

        // verify
        log.verify();
    }

    public void testJavaSerializationUnserializableBaseRR() {
        final Serializable object = new ChildUnserializableBaseRR("");
        log.reset();

        // expectations
        log.expect("ChildUnserializableBaseRR.writeReplace()");
        log.expect("ChildUnserializableBase.writeObject() start");
        log.expect("ChildUnserializableBase.writeObject() end");
        log.expect("ChildUnserializableBaseRR.writeObject() start");
        log.expect("ChildUnserializableBaseRR.writeObject() end");

        // execute
        serialize(object);

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

    public void testXStreamSerializationUnserializableBase() {
        final Serializable object = new ChildUnserializableBase("");
        log.reset();

        // expectations
        log.expect("ChildUnserializableBase.writeObject() start");
        log.expect("ChildUnserializableBase.writeObject() end");

        // execute
        xstream.toXML(object);

        // verify
        log.verify();
    }

    public void testXStreamSerializationUnserializableBaseRR() {
        final Serializable object = new ChildUnserializableBaseRR("");
        log.reset();

        // expectations
        log.expect("ChildUnserializableBaseRR.writeReplace()");
        log.expect("ChildUnserializableBase.writeObject() start");
        log.expect("ChildUnserializableBase.writeObject() end");
        log.expect("ChildUnserializableBaseRR.writeObject() start");
        log.expect("ChildUnserializableBaseRR.writeObject() end");

        // execute
        xstream.toXML(object);

        // verify
        log.verify();
    }

    public void testJavaDeserializationOwnPrivateRR() {
        // setup
        final byte[] data = serialize(new PrivateChildOwnRR());
        log.reset();

        // expectations
        log.expect("PrivateBase.readObject() start");
        log.expect("PrivateBase.readObject() end");
        log.expect("PrivateChildOwnRR.readObject() start");
        log.expect("PrivateChildOwnRR.readObject() end");
        log.expect("PrivateChildOwnRR.readResolve()");

        // execute
        deserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationNoRR() {
        // setup
        final byte[] data = serialize(new PrivateChildNoRR());
        log.reset();

        // expectations
        log.expect("PrivateBase.readObject() start");
        log.expect("PrivateBase.readObject() end");
        log.expect("PrivateChildNoRR.readObject() start");
        log.expect("PrivateChildNoRR.readObject() end");

        // execute
        deserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationOwnProtectedRR() {
        // setup
        final byte[] data = serialize(new ProtectedChildOwnRR());
        log.reset();

        // expectations
        log.expect("ProtectedBase.readObject() start");
        log.expect("ProtectedBase.readObject() end");
        log.expect("ProtectedChildOwnRR.readObject() start");
        log.expect("ProtectedChildOwnRR.readObject() end");
        log.expect("ProtectedChildOwnRR.readResolve()");

        // execute
        deserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationInheritedRR() {
        // setup
        final byte[] data = serialize(new ProtectedChildInheritedRR());
        log.reset();

        // expectations
        log.expect("ProtectedBase.readObject() start");
        log.expect("ProtectedBase.readObject() end");
        log.expect("ProtectedChildInheritedRR.readObject() start");
        log.expect("ProtectedChildInheritedRR.readObject() end");
        log.expect("ProtectedBase.readResolve()");

        // execute
        deserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationOwnPackageRR() {
        // setup
        final byte[] data = serialize(new PackageChildOwnRR());
        log.reset();

        // expectations
        log.expect("PackageBase.readObject() start");
        log.expect("PackageBase.readObject() end");
        log.expect("PackageChildOwnRR.readObject() start");
        log.expect("PackageChildOwnRR.readObject() end");
        log.expect("PackageChildOwnRR.readResolve()");

        // execute
        deserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationInheritedPackageRR() {
        // setup
        final byte[] data = serialize(new PackageChildInheritedRR());
        log.reset();

        // expectations
        log.expect("PackageBase.readObject() start");
        log.expect("PackageBase.readObject() end");
        log.expect("PackageChildInheritedRR.readObject() start");
        log.expect("PackageChildInheritedRR.readObject() end");
        log.expect("PackageBase.readResolve()");

        // execute
        deserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationUnserializableBase() {
        // setup
        final byte[] data = serialize(new ChildUnserializableBase(""));
        log.reset();

        // expectations
        log.expect("UnserializableBase.UnserializableBase()");
        log.expect("ChildUnserializableBase.readObject() start");
        log.expect("ChildUnserializableBase.readObject() end");

        // execute
        deserialize(data);

        // verify
        log.verify();
    }

    public void testJavaDeserializationUnserializableBaseRR() {
        // setup
        final byte[] data = serialize(new ChildUnserializableBaseRR(""));
        log.reset();

        // expectations
        log.expect("UnserializableBase.UnserializableBase()");
        log.expect("ChildUnserializableBase.readObject() start");
        log.expect("ChildUnserializableBase.readObject() end");
        log.expect("ChildUnserializableBaseRR.readObject() start");
        log.expect("ChildUnserializableBaseRR.readObject() end");
        log.expect("ChildUnserializableBaseRR.readResolve()");

        // execute
        deserialize(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationOwnPrivateRR() {
        // setup
        final String data = xstream.toXML(new PrivateChildOwnRR());
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
        final String data = xstream.toXML(new PrivateChildNoRR());
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
        final String data = xstream.toXML(new ProtectedChildOwnRR());
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
        final String data = xstream.toXML(new ProtectedChildInheritedRR());
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
        final String data = xstream.toXML(new PackageChildOwnRR());
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
        final String data = xstream.toXML(new PackageChildInheritedRR());
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

    public void testXStreamDeserializationUnserializableBaseUnsafe() {
        // Use Java deserialization for Serializables with unserializable parent, but no readResolve

        // setup
        final String data = xstream.toXML(new ChildUnserializableBase(""));
        log.reset();

        // expectations
        // log.expect("UnserializableBase.UnserializableBase()"); // XStream cannot call ctor of parent only
        log.expect("ChildUnserializableBase.readObject() start");
        log.expect("ChildUnserializableBase.readObject() end");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationUnserializableBasePure() {
        // Use Java deserialization for Serializables with unserializable parent, but no readResolve

        // setup
        xstream.registerConverter(new SerializableConverter(xstream.getMapper(), new PureJavaReflectionProvider(),
            xstream.getClassLoaderReference()) {
            @Override
            public boolean canConvert(final Class<?> type) {
                return type == ChildUnserializableBase.class;
            }
        });
        final String data = xstream.toXML(new ChildUnserializableBase(""));
        log.reset();

        // expectations
        log.expect("UnserializableBase.UnserializableBase()");
        log.expect("ChildUnserializableBase.readObject() start"); // XStream runs readObject twice
        log.expect("ChildUnserializableBase.readObject() end"); // XStream runs readObject twice
        log.expect("ChildUnserializableBase.readObject() start");
        log.expect("ChildUnserializableBase.readObject() end");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationUnserializableBaseRRUnsafe() {
        // setup
        final String data = xstream.toXML(new ChildUnserializableBaseRR(""));
        log.reset();

        // expectations
        // log.expect("UnserializableBase.UnserializableBase()"); // XStream cannot call ctor of parent only
        log.expect("ChildUnserializableBase.readObject() start");
        log.expect("ChildUnserializableBase.readObject() end");
        log.expect("ChildUnserializableBaseRR.readObject() start");
        log.expect("ChildUnserializableBaseRR.readObject() end");
        log.expect("ChildUnserializableBaseRR.readResolve()");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserializationUnserializableBaseRRPure() {
        // setup
        xstream.registerConverter(new SerializableConverter(xstream.getMapper(), new PureJavaReflectionProvider(),
            xstream.getClassLoaderReference()) {
            @Override
            public boolean canConvert(final Class<?> type) {
                return type == ChildUnserializableBaseRR.class;
            }
        });
        final String data = xstream.toXML(new ChildUnserializableBaseRR(""));
        log.reset();

        // expectations
        log.expect("UnserializableBase.UnserializableBase()");
        log.expect("ChildUnserializableBase.ChildUnserializableBase(String)"); // XStream cannot call ctor of parent
                                                                               // only
        log.expect("ChildUnserializableBaseRR.ChildUnserializableBaseRR()"); // XStream cannot call ctor of parent only
        log.expect("ChildUnserializableBase.readObject() start");
        log.expect("ChildUnserializableBase.readObject() end");
        log.expect("ChildUnserializableBaseRR.readObject() start");
        log.expect("ChildUnserializableBaseRR.readObject() end");
        log.expect("ChildUnserializableBaseRR.readResolve()");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

    public static class ParentNotTransient implements Serializable {

        private static final long serialVersionUID = 200502L;

        public int somethingNotTransient;

        public ParentNotTransient(final int somethingNotTransient) {
            this.somethingNotTransient = somethingNotTransient;
        }

    }

    public static class ChildWithTransient extends ParentNotTransient implements Serializable {

        private static final long serialVersionUID = 200502L;

        public transient int somethingTransient;

        public ChildWithTransient(final int somethingNotTransient, final int somethingTransient) {
            super(somethingNotTransient);
            this.somethingTransient = somethingTransient;
        }

        private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            somethingTransient = 99999;
        }
    }

    public void testCallsReadObjectEvenWithoutNonTransientFields() {
        xstream.alias("parent", ParentNotTransient.class);
        xstream.alias("child", ChildWithTransient.class);

        final Object in = new ChildWithTransient(10, 22222);
        final String expectedXml = ""
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

        final String xml = xstream.toXML(in);
        assertEquals(expectedXml, xml);

        final ChildWithTransient childWithTransient = (ChildWithTransient)xstream.fromXML(xml);

        assertEquals(10, childWithTransient.somethingNotTransient);
        assertEquals(99999, childWithTransient.somethingTransient);
    }

    public static class SomethingThatValidates implements Serializable {

        private static final long serialVersionUID = 200502L;

        private void readObject(final ObjectInputStream s) throws IOException {

            final int LOW_PRIORITY = -5;
            final int MEDIUM_PRIORITY = 0;
            final int HIGH_PRIORITY = 5;

            s.registerValidation(new ObjectInputValidation() {
                @Override
                public void validateObject() {
                    log.actual("validateObject() medium priority 1");
                }
            }, MEDIUM_PRIORITY);

            s.registerValidation(new ObjectInputValidation() {
                @Override
                public void validateObject() {
                    log.actual("validateObject() high priority");
                }
            }, HIGH_PRIORITY);

            s.registerValidation(new ObjectInputValidation() {
                @Override
                public void validateObject() {
                    log.actual("validateObject() low priority");
                }
            }, LOW_PRIORITY);

            s.registerValidation(new ObjectInputValidation() {
                @Override
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

    public void testJavaSerializationValidatesObjectIsCalledInPriorityOrder() {
        // expect
        log.expect("readResolve()");
        log.expect("validateObject() high priority");
        log.expect("validateObject() medium priority 2");
        log.expect("validateObject() medium priority 1");
        log.expect("validateObject() low priority");

        // execute
        deserialize(serialize(new SomethingThatValidates()));

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

        private static final long serialVersionUID = 200602L;

        public int y;

        public CustomSerializableChild() {
            y = 10;
        }

        private void writeObject(final ObjectOutputStream stream) throws IOException {
            log.actual("Child.writeObject() start");
            stream.defaultWriteObject();
            log.actual("Child.writeObject() end");
        }

        private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
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

        final CustomSerializableChild child = new CustomSerializableChild();
        final String expected = ""
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

        final CustomSerializableChild serialized = (CustomSerializableChild)assertBothWays(child, expected);
        assertEquals(5, serialized.x);
        assertEquals(10, serialized.y);
    }

    public static class SerializableGrandChild extends CustomSerializableChild implements Serializable {
        private static final long serialVersionUID = 200604L;
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

        final SerializableGrandChild grandChild = new SerializableGrandChild();
        final String expected = ""
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

        final SerializableGrandChild serialized = (SerializableGrandChild)assertBothWays(grandChild, expected);
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
        final String data = xstream.toXML(new CustomSerializableChild());
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
