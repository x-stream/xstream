package com.thoughtworks.acceptance;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class AbstractCircularReferenceTest extends AbstractAcceptanceTest {

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("person", Person.class);
    }

    public static class Person {
        public String firstname;
        public Person likes;
        public Person loathes;

        public Person() {
        }

        public Person(String name) {
            this.firstname = name;
        }
    }

    public void testCircularReference() {
        Person bob = new Person("bob");
        Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        String xml = xstream.toXML(bob);

        Person bobOut = (Person) xstream.fromXML(xml);
        assertEquals("bob", bobOut.firstname);
        Person janeOut = bobOut.likes;

        assertEquals("jane", janeOut.firstname);

        assertSame(bobOut.likes, janeOut);
        assertSame(bobOut, janeOut.likes);
    }

    public void testCircularReferenceToSelf() {
        Person bob = new Person("bob");
        bob.likes = bob;

        String xml = xstream.toXML(bob);

        Person bobOut = (Person) xstream.fromXML(xml);
        assertEquals("bob", bobOut.firstname);
        assertSame(bobOut, bobOut.likes);
    }

    public void testDeepCircularReferences() {
        Person bob = new Person("bob");
        Person jane = new Person("jane");
        Person ann = new Person("ann");
        Person poo = new Person("poo");

        bob.likes = jane;
        bob.loathes = ann;
        ann.likes = jane;
        ann.loathes = poo;
        poo.likes = jane;
        poo.loathes = ann;
        jane.likes = jane;
        jane.loathes = bob;

        String xml = xstream.toXML(bob);
        Person bobOut = (Person) xstream.fromXML(xml);
        Person janeOut = bobOut.likes;
        Person annOut = bobOut.loathes;
        Person pooOut = annOut.loathes;

        assertEquals("bob", bobOut.firstname);
        assertEquals("jane", janeOut.firstname);
        assertEquals("ann", annOut.firstname);
        assertEquals("poo", pooOut.firstname);

        assertSame(janeOut, bobOut.likes);
        assertSame(annOut, bobOut.loathes);
        assertSame(janeOut, annOut.likes);
        assertSame(pooOut, annOut.loathes);
        assertSame(janeOut, pooOut.likes);
        assertSame(annOut, pooOut.loathes);
        assertSame(janeOut, janeOut.likes);
        assertSame(bobOut, janeOut.loathes);
    }

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
