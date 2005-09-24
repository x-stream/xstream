package com.thoughtworks.acceptance;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class AbstractNestedCircularReferenceTest extends AbstractAcceptanceTest {

    public static class WeirdThing implements Serializable {
        public transient Object anotherObject;
        private NestedThing nestedThing = new NestedThing();

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            anotherObject = in.readObject();
        }
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeObject(anotherObject);
        }
        private class NestedThing implements Serializable {
            private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
                in.defaultReadObject();
            }
            private void writeObject(ObjectOutputStream out) throws IOException {
                out.defaultWriteObject();
            }

        }
    }

    public void testWeirdCircularReference() {
        // I cannot fully explain what's special about WeirdThing, however without ensuring that a reference is only
        // put in the references map once, this fails.

        // This case was first noticed when serializing JComboBox, deserializing it and then serializing it again.
        // Upon the second serialization, it would cause the Sun 1.4.1 JVM to crash:
        // Object in = new javax.swing.JComboBox();
        // Object out = xstream.fromXML(xstream.toXML(in));
        // xstream.toXML(out); ....causes JVM crash on 1.4.1

        // WeirdThing is the least possible code I can create to reproduce the problem.

        // setup
        WeirdThing in = new WeirdThing();
        in.anotherObject = in;

        // execute
        WeirdThing out = (WeirdThing) xstream.fromXML(xstream.toXML(in));

        // verify
        assertSame(out, out.anotherObject);
    }

}
