/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2010, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. July 2011 by Joerg Schaible by merging AbstractCircularReferenceTest,
 * AbstractDuplicateReferenceTest, AbstractNestedCircularReferenceTest and
 * AbstractReplacedReferenceTest.
 */
package com.thoughtworks.acceptance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.acceptance.someobjects.WithNamedList;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.AbstractReferenceMarshaller;


public abstract class AbstractReferenceTest extends AbstractAcceptanceTest {
    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("person", Person.class);
        xstream.alias("thing", Thing.class);
    }

    public void testReferencesAreWorking() {

        Thing sameThing = new Thing("hello");
        Thing anotherThing = new Thing("hello");

        List list = new ArrayList();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        String xml = xstream.toXML(list);
        List result = (List)xstream.fromXML(xml);

        assertEquals(list, result);
    }

    public void testReferencesAreTheSameObjectWhenDeserialized() {

        Thing sameThing = new Thing("hello");
        Thing anotherThing = new Thing("hello");

        List list = new ArrayList();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        String xml = xstream.toXML(list);
        List result = (List)xstream.fromXML(xml);

        Thing t0 = (Thing)result.get(0);
        Thing t1 = (Thing)result.get(1);
        Thing t2 = (Thing)result.get(2);

        t0.field = "bye";

        assertEquals("bye", t0.field);
        assertEquals("bye", t1.field);
        assertEquals("hello", t2.field);

    }

    public static class Thing extends StandardObject {
        public String field;

        public Thing() {
        }

        public Thing(String field) {
            this.field = field;
        }
    }

    public static class MultRef {
        public Object s1 = new Object();
        public Object s2 = s1;
    }

    public void testMultipleReferencesToObjectsWithNoChildren() {
        MultRef in = new MultRef();
        assertSame(in.s1, in.s2);

        String xml = xstream.toXML(in);
        MultRef out = (MultRef)xstream.fromXML(xml);

        assertSame(out.s1, out.s2);
    }

    public void testReferencesNotUsedForImmutableValueTypes() {
        MultRef in = new MultRef();
        in.s1 = new Integer(4);
        in.s2 = in.s1;

        String xml = xstream.toXML(in);
        MultRef out = (MultRef)xstream.fromXML(xml);

        assertEquals(out.s1, out.s2);
        assertNotSame(out.s1, out.s2);
    }

    public void testReferencesUsedForMutableValueTypes() {
        MultRef in = new MultRef();
        in.s1 = new StringBuffer("hi");
        in.s2 = in.s1;

        String xml = xstream.toXML(in);
        MultRef out = (MultRef)xstream.fromXML(xml);

        StringBuffer buffer = (StringBuffer)out.s2;
        buffer.append("bye");

        assertEquals("hibye", out.s1.toString());
        assertSame(out.s1, out.s2);
    }

    public void testReferencesToImplicitCollectionIsNotPossible() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        WithNamedList[] wls = new WithNamedList[]{
            new WithNamedList("foo"), new WithNamedList("bar")};
        wls[0].things.add("Hello");
        wls[0].things.add("Daniel");
        wls[1].things = wls[0].things;

        try {
            xstream.toXML(wls);
            fail("Thrown "
                + AbstractReferenceMarshaller.ReferencedImplicitElementException.class
                    .getName() + " expected");
        } catch (final AbstractReferenceMarshaller.ReferencedImplicitElementException e) {
            // OK
        }
    }

    public void testReferencesToElementsOfImplicitCollectionIsPossible() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        WithNamedList[] wls = new WithNamedList[]{
            new WithNamedList("foo"), new WithNamedList("bar")};
        wls[0].things.add("Hello");
        wls[0].things.add("Daniel");
        wls[1].things.add(wls[0]);

        String xml = xstream.toXML(wls);
        WithNamedList[] out = (WithNamedList[])xstream.fromXML(xml);

        assertSame(out[0], out[1].things.get(0));
    }

    public void testReferencesToElementsOfNthImplicitCollectionIsPossible() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        WithNamedList[] wls = new WithNamedList[]{
            new WithNamedList("foo"), new WithNamedList("bar"), new WithNamedList("foobar")};
        wls[1].things.add("Hello");
        wls[1].things.add("Daniel");
        wls[2].things.add(wls[1]);

        String xml = xstream.toXML(wls);
        WithNamedList[] out = (WithNamedList[])xstream.fromXML(xml);

        assertSame(out[1], out[2].things.get(0));
    }

    public void testThrowsForInvalidReference() {
        String xml = "" //
            + "<list>\n"
            + "  <thing>\n"
            + "    <field>Hello</field>\n"
            + "  </thing>\n"
            + "  <thing reference=\"foo\">\n"
            + "</list>";

        try {
            xstream.fromXML(xml);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals("foo", e.get("reference"));
        }
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

    static class LinkedElement {
        String name;
        LinkedElement next;

        LinkedElement(String name) {
            this.name = name;
        }
    }

    static class TreeElement {
        StringBuffer name;
        TreeElement left;
        TreeElement right;

        TreeElement(StringBuffer name) {
            this.name = name;
        }

        TreeElement(String name) {
            this.name = new StringBuffer(name);
        }
    }

    public void testCircularReference() {
        Person bob = new Person("bob");
        Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        String xml = xstream.toXML(bob);

        Person bobOut = (Person)xstream.fromXML(xml);
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

        Person bobOut = (Person)xstream.fromXML(xml);
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
        Person bobOut = (Person)xstream.fromXML(xml);
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

        private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            anotherObject = in.readObject();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeObject(anotherObject);
        }

        private class NestedThing implements Serializable {
            private void readObject(ObjectInputStream in)
                throws IOException, ClassNotFoundException {
                in.defaultReadObject();
            }

            private void writeObject(ObjectOutputStream out) throws IOException {
                out.defaultWriteObject();
            }

        }
    }

    public void testWeirdCircularReference() {
        // I cannot fully explain what's special about WeirdThing, however without ensuring that
        // a reference is only
        // put in the references map once, this fails.

        // This case was first noticed when serializing JComboBox, deserializing it and then
        // serializing it again.
        // Upon the second serialization, it would cause the Sun 1.4.1 JVM to crash:
        // Object in = new javax.swing.JComboBox();
        // Object out = xstream.fromXML(xstream.toXML(in));
        // xstream.toXML(out); ....causes JVM crash on 1.4.1

        // WeirdThing is the least possible code I can create to reproduce the problem.

        // This also fails for JRockit 1.4.2 deeply nested, when it tries to set the final field
        // AbstractNestedCircularReferenceTest$WeirdThing$NestedThing$this$1.

        // setup
        WeirdThing in = new WeirdThing();
        in.anotherObject = in;

        String xml = xstream.toXML(in);
        // System.out.println(xml + "\n");

        // execute
        WeirdThing out = (WeirdThing)xstream.fromXML(xml);

        // verify
        assertSame(out, out.anotherObject);
    }

    public static class TreeData implements Serializable {
        String data;
        TreeData parent;
        List children;

        public TreeData(String data) {
            this.data = data;
            children = new ArrayList();
        }

        private TreeData(TreeData clone) {
            data = clone.data;
            parent = clone.parent;
            children = clone.children;
        }

        public void add(TreeData child) {
            child.parent = this;
            children.add(child);
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((this.children == null) ? 0 : this.children.hashCode());
            result = prime * result + ((this.data == null) ? 0 : this.data.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!(obj instanceof TreeData)) return false;
            TreeData other = (TreeData)obj;
            if (this.children == null) {
                if (other.children != null) return false;
            } else if (!this.children.equals(other.children)) return false;
            if (this.data == null) {
                if (other.data != null) return false;
            } else if (!this.data.equals(other.data)) return false;
            return true;
        }

        private Object writeReplace() {
            if (getClass() == TreeData.class) {
                return this;
            }
            return new TreeData(this);
        }
    }

    public abstract void testReplacedReference();

    public void replacedReference(String expectedXml) {
        TreeData parent = new TreeData("parent");
        parent.add(new TreeData("child") {
            // anonymous type
        });

        xstream.alias("element", TreeData.class);
        xstream.alias("anonymous-element", parent.children.get(0).getClass());

        assertEquals(expectedXml, xstream.toXML(parent));
        TreeData clone = (TreeData)xstream.fromXML(expectedXml);
        assertEquals(parent, clone);
    }

    static class Email extends StandardObject {
        String email;
        private final Email alias;

        Email(String email) {
            this(email, null);
        }
        Email(String email, Email alias) {
            this.email = email;
            this.alias = alias;
        }
    }

    static class EmailList extends StandardObject {
        List addresses = new ArrayList();
        Email main;
    }

    public void testReferenceElementInImplicitCollection() {
        EmailList emails = new EmailList();
        emails.addresses.add(new Email("private@joewalnes.com"));
        emails.addresses.add(new Email("joe@joewalnes.com"));
        emails.addresses.add(new Email("joe.walnes@thoughtworks.com"));
        emails.addresses.add(new Email("joe@thoughtworks.com", (Email)emails.addresses.get(2)));
        emails.main = (Email)emails.addresses.get(1);

        xstream.addImplicitCollection(EmailList.class, "addresses", "address", Email.class);
        String xml = xstream.toXML(emails);
        assertEquals(emails, xstream.fromXML(xml));
    }

    static class EmailArray extends StandardObject {
        Email[] addresses;
        Email main;
    }

    public void testReferenceElementInImplicitArrays() {
        EmailArray emails = new EmailArray();
        Email alias = new Email("joe.walnes@thoughtworks.com");
        emails.addresses = new Email[]{
            new Email("private@joewalnes.com"),
            new Email("joe@joewalnes.com"),
            alias,
            new Email("joe@thoughtworks.com", alias)
        };
        emails.main = emails.addresses[1];

        xstream.addImplicitArray(EmailArray.class, "addresses", "address");
        String xml = xstream.toXML(emails);
        assertEquals(emails, xstream.fromXML(xml));
    }
}
