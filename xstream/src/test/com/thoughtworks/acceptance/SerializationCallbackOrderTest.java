package com.thoughtworks.acceptance;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.ByteArrayInputStream;

import com.thoughtworks.xstream.testutil.CallLog;
import com.thoughtworks.xstream.XStream;

public class SerializationCallbackOrderTest extends AbstractAcceptanceTest {

    // static so it can be accessed by objects under test, without them needing a reference back to the testcase
    private static CallLog log = new CallLog();
    private XStream xstream = new XStream();

    // --- Sample class hiearchy

    public static class Base implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("Base.writeObject()");
            out.defaultWriteObject();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("Base.readObject()");
            in.defaultReadObject();
        }

        private Object writeReplace() {
            log.actual("Base.writeReplace()");
            return this;
        }

        private Object readResolve() {
            log.actual("Base.readResolve()");
            return this;
        }
    }

    public static class Child extends Base implements Serializable{

        private void writeObject(ObjectOutputStream out) throws IOException {
            log.actual("Child.writeObject()");
            out.defaultWriteObject();
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            log.actual("Child.readObject()");
            in.defaultReadObject();
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

    public void testJavaSerialization() throws IOException {
        // expectations
        log.expect("Child.writeReplace()");
        log.expect("Base.writeObject()");
        log.expect("Child.writeObject()");

        // execute
        javaSerialize(new Child());

        // verify
        log.verify();
    }

    public void testXStreamSerialization() throws IOException {
        // expectations
        log.expect("Child.writeReplace()");
        log.expect("Base.writeObject()");
        log.expect("Child.writeObject()");

        // execute
        xstream.toXML(new Child());

        // verify
        log.verify();
    }

    public void testJavaDeserialization() throws IOException, ClassNotFoundException {
        // setup
        byte[] data = javaSerialize(new Child());
        log.reset();

        // expectations
        log.expect("Base.readObject()");
        log.expect("Child.readObject()");
        log.expect("Child.readResolve()");

        // execute
        javaDeserialize(data);

        // verify
        log.verify();
    }

    public void testXStreamDeserialization() throws IOException, ClassNotFoundException {
        // setup
        String data = xstream.toXML(new Child());
        log.reset();

        // expectations
        log.expect("Base.readObject()");
        log.expect("Child.readObject()");
        log.expect("Child.readResolve()");

        // execute
        xstream.fromXML(data);

        // verify
        log.verify();
    }

}
