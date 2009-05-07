/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. August 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Original;
import com.thoughtworks.acceptance.objects.Replaced;
import com.thoughtworks.acceptance.objects.StandardObject;

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

public class WriteReplaceTest extends AbstractAcceptanceTest {

    public static class Thing extends StandardObject implements Serializable {

        int a;
        int b;

        public Thing() {
        }

        public Thing(int a, int b) {
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
        Thing thing = new Thing(3, 6);

        // ensure that Java serialization does not cause endless loop for a Thing
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(thing);
        oos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ios = new ObjectInputStream(bais);
        assertEquals(thing, ios.readObject());
        ios.close();
        
        // ensure that XStream does not cause endless loop for a Thing
        xstream.alias("thing", Thing.class);

        String expectedXml = ""
                + "<thing>\n"
                + "  <a>3000</a>\n"
                + "  <b>6000</b>\n"
                + "</thing>";

        assertBothWays(thing, expectedXml);
    }

    public void testAllowsDifferentTypeToBeSubstituted() {
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        Original in = new Original("hello world");

        String expectedXml = ""
                + "<original-class resolves-to=\"replaced-class\">\n"
                + "  <replacedValue>HELLO WORLD</replacedValue>\n"
                + "</original-class>";

        assertBothWays(in, expectedXml);
    }

    public void testAllowsDifferentTypeToBeSubstitutedInList() {
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        List in = new ArrayList(); 
        in.add(new Original("hello world"));

        String expectedXml = ""
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

        Container in = new Container(); 
        in.original = new Original("hello world");

        String expectedXml = ""
                + "<container>\n"
                + "  <original resolves-to=\"replaced-class\">\n"
                + "    <replacedValue>HELLO WORLD</replacedValue>\n"
                + "  </original>\n" 
                + "</container>";

        assertBothWays(in, expectedXml);
    }

    public static class ExtenalizableContainer extends StandardObject implements Externalizable {
        Original original;
        
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            original = (Original)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(original);
        }
    }
    
    public void testAllowsDifferentTypeToBeSubstitutedInExternalizable() {
        xstream.alias("container", ExtenalizableContainer.class);
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        ExtenalizableContainer in = new ExtenalizableContainer(); 
        in.original = new Original("hello world");

        String expectedXml = ""
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

        Original in = new Original("hello world");

        String xml = ""
                + "<original-class resolves-to=\"replaced-class\" class=\"not.Existing\">\n"
                + "  <replacedValue>HELLO WORLD</replacedValue>\n"
                + "</original-class>";

        assertEquals(in, xstream.fromXML(xml));
    }

    public void testAllowsDifferentTypeToBeSubstitutedWithNonExistingClassInList() {
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        List in = new ArrayList(); 
        in.add(new Original("hello world"));

        String xml = ""
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

        Container in = new Container(); 
        in.original = new Original("hello world");

        String xml = ""
                + "<container>\n"
                + "  <original resolves-to=\"replaced-class\" class=\"not.Existing\">\n"
                + "    <replacedValue>HELLO WORLD</replacedValue>\n"
                + "  </original>\n" 
                + "</container>";

        assertEquals(in, xstream.fromXML(xml));
    }
    
    public void testAllowsDifferentTypeToBeSubstitutedWithNonExistingClassInExternalizable() {
        xstream.alias("container", ExtenalizableContainer.class);
        xstream.alias("original-class", Original.class);
        xstream.alias("replaced-class", Replaced.class);

        ExtenalizableContainer in = new ExtenalizableContainer(); 
        in.original = new Original("hello world");

        String xml = ""
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

        public OriginalSerializable(String originalValue) {
            this.originalValue = originalValue;
        }

        private Object writeReplace() {
            return new ReplacedSerializable(originalValue.toUpperCase());
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    public static class ReplacedSerializable extends StandardObject {
        String replacedValue;

        public ReplacedSerializable() {
        }

        public ReplacedSerializable(String replacedValue) {
            this.replacedValue = replacedValue;
        }

        private Object readResolve() {
            return new OriginalSerializable(replacedValue.toLowerCase());
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }
    }

    public void testAllowsDifferentTypeToBeSubstitutedForCustomSerializableObjects() {
        xstream.alias("original-serializable-class", OriginalSerializable.class);
        xstream.alias("replaced-serializable-class", ReplacedSerializable.class);

        OriginalSerializable in = new OriginalSerializable("hello world");

        String expectedXml = ""
                + "<original-serializable-class resolves-to=\"replaced-serializable-class\" serialization=\"custom\">\n"
                + "  <replaced-serializable-class>\n"
                + "    <default>\n"
                + "      <replacedValue>HELLO WORLD</replacedValue>\n"
                + "    </default>\n"
                + "  </replaced-serializable-class>\n"
                + "</original-serializable-class>";

        assertBothWays(in, expectedXml);
    }
}
