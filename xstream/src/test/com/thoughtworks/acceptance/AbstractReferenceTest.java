/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2010, 2011, 2014, 2015, 2018 XStream Committers.
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
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("person", Person.class);
        xstream.alias("thing", Thing.class);
        xstream.allowTypesByWildcard(AbstractReferenceTest.class.getName() + "$*");
    }

    public void testReferencesAreWorking() {

        final Thing sameThing = new Thing("hello");
        final Thing anotherThing = new Thing("hello");

        final List<Thing> list = new ArrayList<>();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        final String xml = xstream.toXML(list);
        final List<Thing> result = xstream.fromXML(xml);

        assertEquals(list, result);
    }

    public void testReferencesAreTheSameObjectWhenDeserialized() {

        final Thing sameThing = new Thing("hello");
        final Thing anotherThing = new Thing("hello");

        final List<Thing> list = new ArrayList<>();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        final String xml = xstream.toXML(list);
        final List<Thing> result = xstream.fromXML(xml);

        final Thing t0 = result.get(0);
        final Thing t1 = result.get(1);
        final Thing t2 = result.get(2);

        assertSame(t0, t1);

        t0.field = "bye";

        assertEquals("bye", t0.field);
        assertEquals("bye", t1.field);
        assertEquals("hello", t2.field);

    }

    public static class Thing extends StandardObject {
        private static final long serialVersionUID = 201107L;
        public String field;

        public Thing(final String field) {
            this.field = field;
        }
    }

    public static class MultRef<T> {
        @SuppressWarnings("unchecked")
        public T s1 = (T)new Object();
        public T s2 = s1;
    }

    public void testMultipleReferencesToObjectsWithNoChildren() {
        final MultRef<?> in = new MultRef<>();
        assertSame(in.s1, in.s2);

        final String xml = xstream.toXML(in);
        final MultRef<?> out = xstream.fromXML(xml);

        assertSame(out.s1, out.s2);
    }

    public void testReferencesNotUsedForImmutableValueTypes() {
        final MultRef<Integer> in = new MultRef<>();
        in.s1 = new Integer(4);
        in.s2 = in.s1;

        final String xml = xstream.toXML(in);
        final MultRef<Integer> out = xstream.fromXML(xml);

        assertEquals(out.s1, out.s2);
        assertNotSame(out.s1, out.s2);
    }

    public void testReferencesUsedForMutableValueTypes() {
        final MultRef<StringBuffer> in = new MultRef<>();
        in.s1 = new StringBuffer("hi");
        in.s2 = in.s1;

        final String xml = xstream.toXML(in);
        final MultRef<StringBuffer> out = xstream.fromXML(xml);

        final StringBuffer buffer = out.s2;
        buffer.append("bye");

        assertEquals("hibye", out.s1.toString());
        assertSame(out.s1, out.s2);
    }

    public void testReferencesToImplicitCollectionIsNotPossible() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        @SuppressWarnings("unchecked")
        final WithNamedList<String>[] wls = new WithNamedList[]{
            new WithNamedList<String>("foo"), new WithNamedList<String>("bar")};
        wls[0].things.add("Hello");
        wls[0].things.add("Daniel");
        wls[1].things = wls[0].things;

        try {
            xstream.toXML(wls);
            fail("Thrown "
                + AbstractReferenceMarshaller.ReferencedImplicitElementException.class.getName()
                + " expected");
        } catch (final AbstractReferenceMarshaller.ReferencedImplicitElementException e) {
            // OK
        }
    }

    public void testReferencesToElementsOfImplicitCollectionIsPossible() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        @SuppressWarnings("unchecked")
        final WithNamedList<Object>[] wls = new WithNamedList[]{
            new WithNamedList<String>("foo"), new WithNamedList<String>("bar")};
        wls[0].things.add("Hello");
        wls[0].things.add("Daniel");
        wls[1].things.add(wls[0]);

        final String xml = xstream.toXML(wls);
        final WithNamedList<Object>[] out = xstream.fromXML(xml);

        assertSame(out[0], out[1].things.get(0));
    }

    public void testReferencesToElementsOfNthImplicitCollectionIsPossible() {
        xstream.alias("strings", WithNamedList.class);
        xstream.addImplicitCollection(WithNamedList.class, "things");
        @SuppressWarnings("unchecked")
        final WithNamedList<Object>[] wls = new WithNamedList[]{
            new WithNamedList<String>("foo"), new WithNamedList<String>("bar"), new WithNamedList<String>("foobar")};
        wls[1].things.add("Hello");
        wls[1].things.add("Daniel");
        wls[2].things.add(wls[1]);

        final String xml = xstream.toXML(wls);
        final WithNamedList<Object>[] out = xstream.fromXML(xml);

        assertSame(out[1], out[2].things.get(0));
    }

    public void testThrowsForInvalidReference() {
        final String xml = "" //
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

        public Person(final String name) {
            firstname = name;
        }
    }

    static class LinkedElement {
        String name;
        LinkedElement next;

        LinkedElement(final String name) {
            this.name = name;
        }
    }

    static class TreeElement {
        StringBuffer name;
        TreeElement left;
        TreeElement right;

        TreeElement(final StringBuffer name) {
            this.name = name;
        }

        TreeElement(final String name) {
            this.name = new StringBuffer(name);
        }
    }

    public void testCircularReference() {
        final Person bob = new Person("bob");
        final Person jane = new Person("jane");
        bob.likes = jane;
        jane.likes = bob;

        final String xml = xstream.toXML(bob);

        final Person bobOut = xstream.fromXML(xml);
        assertEquals("bob", bobOut.firstname);
        final Person janeOut = bobOut.likes;

        assertEquals("jane", janeOut.firstname);

        assertSame(bobOut.likes, janeOut);
        assertSame(bobOut, janeOut.likes);
    }

    public void testCircularReferenceToSelf() {
        final Person bob = new Person("bob");
        bob.likes = bob;

        final String xml = xstream.toXML(bob);

        final Person bobOut = xstream.fromXML(xml);
        assertEquals("bob", bobOut.firstname);
        assertSame(bobOut, bobOut.likes);
    }

    public void testDeepCircularReferences() {
        final Person bob = new Person("bob");
        final Person jane = new Person("jane");
        final Person ann = new Person("ann");
        final Person poo = new Person("poo");

        bob.likes = jane;
        bob.loathes = ann;
        ann.likes = jane;
        ann.loathes = poo;
        poo.likes = jane;
        poo.loathes = ann;
        jane.likes = jane;
        jane.loathes = bob;

        final String xml = xstream.toXML(bob);
        final Person bobOut = xstream.fromXML(xml);
        final Person janeOut = bobOut.likes;
        final Person annOut = bobOut.loathes;
        final Person pooOut = annOut.loathes;

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
        private static final long serialVersionUID = 201107L;
        public transient Object anotherObject;
        final NestedThing nestedThing = new NestedThing();

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            anotherObject = in.readObject();
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeObject(anotherObject);
        }

        private class NestedThing implements Serializable {
            private static final long serialVersionUID = 201107L;

            private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
                in.defaultReadObject();
            }

            private void writeObject(final ObjectOutputStream out) throws IOException {
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
        final WeirdThing in = new WeirdThing();
        in.anotherObject = in;

        final String xml = xstream.toXML(in);
        // System.out.println(xml + "\n");

        // execute
        final WeirdThing out = xstream.fromXML(xml);

        // verify
        assertSame(out, out.anotherObject);
    }

    public static class TreeData implements Serializable {
        private static final long serialVersionUID = 201107L;
        String data;
        TreeData parent;
        List<TreeData> children;

        public TreeData(final String data) {
            this.data = data;
            children = new ArrayList<>();
        }

        private TreeData(final TreeData clone) {
            data = clone.data;
            parent = clone.parent;
            children = clone.children;
        }

        public void add(final TreeData child) {
            child.parent = this;
            children.add(child);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (children == null ? 0 : children.hashCode());
            result = prime * result + (data == null ? 0 : data.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof TreeData)) {
                return false;
            }
            final TreeData other = (TreeData)obj;
            if (children == null) {
                if (other.children != null) {
                    return false;
                }
            } else if (!children.equals(other.children)) {
                return false;
            }
            if (data == null) {
                if (other.data != null) {
                    return false;
                }
            } else if (!data.equals(other.data)) {
                return false;
            }
            return true;
        }

        protected Object writeReplace() {
            if (getClass() == TreeData.class) {
                return this;
            }
            return new TreeData(this);
        }
    }

    public abstract void testReplacedReference();

    public void replacedReference(final String expectedXml) {
        final TreeData parent = new TreeData("parent");
        parent.add(new TreeData("child") {
            private static final long serialVersionUID = 201107L;
            // anonymous type
        });

        xstream.alias("element", TreeData.class);
        xstream.alias("anonymous-element", parent.children.get(0).getClass());

        assertEquals(expectedXml, xstream.toXML(parent));
        final TreeData clone = xstream.fromXML(expectedXml);
        assertEquals(parent, clone);
    }

    static class Email extends StandardObject {
        private static final long serialVersionUID = 201107L;
        String email;
        final Email alias;

        Email(final String email) {
            this(email, null);
        }

        Email(final String email, final Email alias) {
            this.email = email;
            this.alias = alias;
        }
    }

    static class EmailList extends StandardObject {
        private static final long serialVersionUID = 201107L;
        List<Email> addresses = new ArrayList<>();
        Email main;
    }

    public void testReferenceElementInImplicitCollection() {
        final EmailList emails = new EmailList();
        emails.addresses.add(new Email("private@joewalnes.com"));
        emails.addresses.add(new Email("joe@joewalnes.com"));
        emails.addresses.add(new Email("joe.walnes@thoughtworks.com"));
        emails.addresses.add(new Email("joe@thoughtworks.com", emails.addresses.get(2)));
        emails.main = emails.addresses.get(1);

        xstream.addImplicitCollection(EmailList.class, "addresses", "address", Email.class);
        final String xml = xstream.toXML(emails);
        assertEquals(emails, xstream.fromXML(xml));
    }

    static class EmailArray extends StandardObject {
        private static final long serialVersionUID = 201107L;
        Email[] addresses;
        Email main;
    }

    public void testReferenceElementInImplicitArrays() {
        final EmailArray emails = new EmailArray();
        final Email alias = new Email("joe.walnes@thoughtworks.com");
        emails.addresses = new Email[]{
            new Email("private@joewalnes.com"), new Email("joe@joewalnes.com"), alias, new Email("joe@thoughtworks.com",
                alias)};
        emails.main = emails.addresses[1];

        xstream.addImplicitArray(EmailArray.class, "addresses", "address");
        final String xml = xstream.toXML(emails);
        assertEquals(emails, xstream.fromXML(xml));
    }

    public void testImmutableInstancesAreNotReferenced() {
        xstream.addImmutableType(Thing.class, false);

        final Thing sameThing = new Thing("hello");
        final Thing anotherThing = new Thing("hello");

        final List<Thing> list = new ArrayList<>();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        final String xml = xstream.toXML(list);
        final List<Thing> result = xstream.fromXML(xml);

        final Thing t0 = result.get(0);
        final Thing t1 = result.get(1);
        result.get(2);

        assertEquals(t0, t1);
        assertNotSame(t0, t1);
    }

    public void testImmutableInstancesCanBeDereferenced() {

        final Thing sameThing = new Thing("hello");
        final Thing anotherThing = new Thing("hello");

        final List<Thing> list = new ArrayList<>();
        list.add(sameThing);
        list.add(sameThing);
        list.add(anotherThing);

        final String xml = xstream.toXML(list);

        xstream.addImmutableType(Thing.class, false);

        try {
            xstream.fromXML(xml);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(Thing.class.getName(), e.get("referenced-type"));
        }

        xstream.addImmutableType(Thing.class, true);

        final List<Thing> result = xstream.fromXML(xml);

        final Thing t0 = result.get(0);
        final Thing t1 = result.get(1);
        result.get(2);

        assertEquals(t0, t1);
        assertSame(t0, t1);
    }
}
