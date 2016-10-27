/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2014, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 24. August 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.thoughtworks.acceptance.objects.Original;
import com.thoughtworks.acceptance.objects.Replaced;
import com.thoughtworks.acceptance.objects.StandardObject;


public class WriteReplaceTest extends AbstractAcceptanceTest {

    public static class Thing extends StandardObject implements Serializable {

        int a;
        int b;

        public Thing() {
        }

        public Thing(final int a, final int b) {
            this.a = a;
            this.b = b;
        }

        private Object writeReplace() {
            return new Thing(a * 1000, b * 1000);
        }

        private Object readResolve() {
            return new Thing(a / 1000, b / 1000);
        }

    }

    public void testReplacesAndResolves() throws IOException, ClassNotFoundException {
        final Thing thing = new Thing(3, 6);

        // ensure that Java serialization does not cause endless loop for a Thing
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(thing);
        oos.close();

        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ObjectInputStream ios = new ObjectInputStream(bais);
        assertEquals(thing, ios.readObject());
        ios.close();

        // ensure that XStream does not cause endless loop for a Thing
        xstream.alias("thing", Thing.class);

        final String expectedXml = "" + "<thing>\n" + "  <a>3000</a>\n" + "  <b>6000</b>\n" + "</thing>";

        assertBothWays(thing, expectedXml);
    }

    public void testAllowsDifferentTypeToBeSubstituted() {
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        final Original in = new Original("hello world");

        final String expectedXml = ""
            + "<original-class resolves-to=\"replaced-class\">\n"
            + "  <replacedValue>HELLO WORLD</replacedValue>\n"
            + "</original-class>";

        assertBothWays(in, expectedXml);
    }

    public void testAllowsDifferentTypeToBeSubstitutedInList() {
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        final List in = new ArrayList();
        in.add(new Original("hello world"));

        final String expectedXml = ""
            + "<list>\n"
            + "  <original-class resolves-to=\"replaced-class\">\n"
            + "    <replacedValue>HELLO WORLD</replacedValue>\n"
            + "  </original-class>\n"
            + "</list>";

        assertBothWays(in, expectedXml);
    }

    public static class Container extends StandardObject {
        Original original;
    }

    public void testAllowsDifferentTypeToBeSubstitutedAsMember() {
        xstream.alias("container", Container.class);
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        final Container in = new Container();
        in.original = new Original("hello world");

        final String expectedXml = ""
            + "<container>\n"
            + "  <original resolves-to=\"replaced-class\">\n"
            + "    <replacedValue>HELLO WORLD</replacedValue>\n"
            + "  </original>\n"
            + "</container>";

        assertBothWays(in, expectedXml);
    }

    public static class ExternalizableContainer extends StandardObject implements Externalizable {
        Original original;

        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            original = (Original)in.readObject();
        }

        public void writeExternal(final ObjectOutput out) throws IOException {
            out.writeObject(original);
        }
    }

    public void testAllowsDifferentTypeToBeSubstitutedInExternalizable() {
        xstream.alias("container", ExternalizableContainer.class);
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        final ExternalizableContainer in = new ExternalizableContainer();
        in.original = new Original("hello world");

        final String expectedXml = ""
            + "<container>\n"
            + "  <original-class resolves-to=\"replaced-class\">\n"
            + "    <replacedValue>HELLO WORLD</replacedValue>\n"
            + "  </original-class>\n"
            + "</container>";

        assertBothWays(in, expectedXml);
    }

    public void testAllowsDifferentTypeToBeSubstitutedWithNonExistingClass() {
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        final Original in = new Original("hello world");

        final String xml = ""
            + "<original-class resolves-to=\"replaced-class\" class=\"not.Existing\">\n"
            + "  <replacedValue>HELLO WORLD</replacedValue>\n"
            + "</original-class>";

        assertEquals(in, xstream.fromXML(xml));
    }

    public void testAllowsDifferentTypeToBeSubstitutedWithNonExistingClassInList() {
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        final List in = new ArrayList();
        in.add(new Original("hello world"));

        final String xml = ""
            + "<list>\n"
            + "  <original-class resolves-to=\"replaced-class\" class=\"not.Existing\">\n"
            + "    <replacedValue>HELLO WORLD</replacedValue>\n"
            + "  </original-class>\n"
            + "</list>";

        assertEquals(in, xstream.fromXML(xml));
    }

    public void testAllowsDifferentTypeToBeSubstitutedWithNonExistingClassAsMember() {
        xstream.alias("container", Container.class);
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        final Container in = new Container();
        in.original = new Original("hello world");

        final String xml = ""
            + "<container>\n"
            + "  <original resolves-to=\"replaced-class\" class=\"not.Existing\">\n"
            + "    <replacedValue>HELLO WORLD</replacedValue>\n"
            + "  </original>\n"
            + "</container>";

        assertEquals(in, xstream.fromXML(xml));
    }

    public void testAllowsDifferentTypeToBeSubstitutedWithNonExistingClassInExternalizable() {
        xstream.alias("container", ExternalizableContainer.class);
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        final ExternalizableContainer in = new ExternalizableContainer();
        in.original = new Original("hello world");

        final String xml = ""
            + "<container>\n"
            + "  <original-class resolves-to=\"replaced-class\" class=\"not.Existing\">\n"
            + "    <replacedValue>HELLO WORLD</replacedValue>\n"
            + "  </original-class>\n"
            + "</container>";

        assertEquals(in, xstream.fromXML(xml));
    }

    public static class OriginalSerializable extends StandardObject {
        String originalValue;

        public OriginalSerializable() {
        }

        public OriginalSerializable(final String originalValue) {
            this.originalValue = originalValue;
        }

        private Object writeReplace() {
            return new ReplacedSerializable(originalValue.toUpperCase());
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    public static class ReplacedSerializable extends StandardObject {
        String replacedValue;

        public ReplacedSerializable() {
        }

        public ReplacedSerializable(final String replacedValue) {
            this.replacedValue = replacedValue;
        }

        private Object readResolve() {
            return new OriginalSerializable(replacedValue.toLowerCase());
        }

        private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    public void testAllowsDifferentTypeToBeSubstitutedForCustomSerializableObjects() {
        xstream.alias("original-serializable-class", OriginalSerializable.class);
        xstream.alias("replaced-serializable-class", ReplacedSerializable.class);

        final OriginalSerializable in = new OriginalSerializable("hello world");

        final String expectedXml = ""
            + "<original-serializable-class resolves-to=\"replaced-serializable-class\" serialization=\"custom\">\n"
            + "  <replaced-serializable-class>\n"
            + "    <default>\n"
            + "      <replacedValue>HELLO WORLD</replacedValue>\n"
            + "    </default>\n"
            + "  </replaced-serializable-class>\n"
            + "</original-serializable-class>";

        assertBothWays(in, expectedXml);
    }

    public static class OriginalExternalizable extends StandardObject implements Externalizable {
        String originalValue;

        public OriginalExternalizable() {
        }

        public OriginalExternalizable(final String originalValue) {
            this.originalValue = originalValue;
        }

        private Object writeReplace() {
            return new ReplacedExternalizable(originalValue.toUpperCase());
        }

        public void writeExternal(final ObjectOutput out) throws IOException {
            out.writeObject(originalValue);
        }

        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            originalValue = (String)in.readObject();
        }
    }

    public static class ReplacedExternalizable extends StandardObject implements Externalizable {
        String replacedValue;

        public ReplacedExternalizable() {
        }

        public ReplacedExternalizable(final String replacedValue) {
            this.replacedValue = replacedValue;
        }

        private Object readResolve() {
            return new OriginalExternalizable(replacedValue.toLowerCase());
        }

        public void writeExternal(final ObjectOutput out) throws IOException {
            out.writeObject(StringUtils.reverse(replacedValue));
        }

        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            replacedValue = StringUtils.reverse((String)in.readObject());
        }
    }

    public void testAllowsDifferentTypeToBeSubstitutedForCustomExternalizableObjects() {
        xstream.alias("original-externalizable-class", OriginalExternalizable.class);
        xstream.alias("replaced-externalizable-class", ReplacedExternalizable.class);

        final OriginalExternalizable in = new OriginalExternalizable("hello world");

        final String expectedXml = ""
            + "<original-externalizable-class resolves-to=\"replaced-externalizable-class\">\n"
            + "  <string>DLROW OLLEH</string>\n"
            + "</original-externalizable-class>";

        assertBothWays(in, expectedXml);
    }

    public static class OriginalThing extends StandardObject {
        private final String value;

        public OriginalThing() {
            this("");
        }

        public OriginalThing(final String value) {
            this.value = value;
        }

        private Object writeReplace() {
            return new IntermediateThing(value);
        }
    }

    public static class IntermediateThing {
        private final String value;

        public IntermediateThing(final String value) {
            this.value = value;
        }

        private Object writeReplace() {
            return new ReplacedThing(value);
        }
    }

    public static class ReplacedThing {
        private final String value;

        public ReplacedThing(final String value) {
            this.value = value;
        }

        private Object readResolve() {
            return new OriginalThing(value);
        }
    }
    
    public void testCascadedWriteReplace() {
        xstream.alias("original-thing", OriginalThing.class);
        xstream.alias("intermediate-thing", IntermediateThing.class);
        xstream.alias("replaced-thing", ReplacedThing.class);

        final OriginalThing in = new OriginalThing("hello world");
        
        final String expectedXml = ""
            + "<original-thing resolves-to=\"replaced-thing\">\n"
            + "  <value>hello world</value>\n"
            + "</original-thing>";

        assertBothWays(in, expectedXml);
    }
}
