/*
 * Copyright (C) 2006, 2007, 2010, 2012, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. June 2006 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;


/**
 * <p>
 * A class {@link Serializable} {@link Parent} class implements <code>writeObject()</code> and holds a {@link Child}
 * class that also implements <code>writeObject()</code>
 * </p>
 *
 * @author <a href="mailto:cleclerc@pobox.com">Cyrille Le Clerc</a>
 */
public class SerializationNestedWriteObjectsTest extends AbstractAcceptanceTest {

    public static class Child implements Serializable {

        private static final long serialVersionUID = 200606L;

        private int i = 3;

        public Child(final int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }

        private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }
    }

    public static class Parent implements Serializable {

        private static final long serialVersionUID = 200606L;

        private final String name;

        private transient Child child;

        public Parent(final String name, final Child child) {
            this.name = name;
            this.child = child;
        }

        public Child getChild() {
            return child;
        }

        public String getName() {
            return name;
        }

        private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            child = (Child)in.readObject();
            in.defaultReadObject();
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.writeObject(child);
            out.defaultWriteObject();
        }
    }

    public void testObjectInputStream() throws Exception {
        xstream.alias("parent", Parent.class);
        xstream.alias("child", Child.class);

        final String sourceXml = ""
            + "<object-stream>\n"
            + "  <parent serialization=\"custom\">\n"
            + "    <parent>\n"
            + "      <child serialization=\"custom\">\n"
            + "        <child>\n"
            + "          <default>\n"
            + "            <i>1</i>\n"
            + "          </default>\n"
            + "        </child>\n"
            + "      </child>\n"
            + "      <default>\n"
            + "        <name>ze-name</name>\n"
            + "      </default>\n"
            + "    </parent>\n"
            + "  </parent>\n"
            + "</object-stream>";

        @SuppressWarnings("resource")
        final ObjectInputStream objectInputStream = xstream.createObjectInputStream(new StringReader(sourceXml));

        final Parent parent = (Parent)objectInputStream.readObject();

        assertEquals("ze-name", parent.getName());
        assertEquals(1, parent.getChild().getI());
    }

    public void testObjectOutputStream() throws Exception {
        xstream.alias("parent", Parent.class);
        xstream.alias("child", Child.class);

        final String expectedXml = ""
            + "<object-stream>\n"
            + "  <parent serialization=\"custom\">\n"
            + "    <parent>\n"
            + "      <child serialization=\"custom\">\n"
            + "        <child>\n"
            + "          <default>\n"
            + "            <i>1</i>\n"
            + "          </default>\n"
            + "        </child>\n"
            + "      </child>\n"
            + "      <default>\n"
            + "        <name>ze-name</name>\n"
            + "      </default>\n"
            + "    </parent>\n"
            + "  </parent>\n"
            + "</object-stream>";

        final Parent parent = new Parent("ze-name", new Child(1));
        final StringWriter stringWriter = new StringWriter();
		try (ObjectOutputStream os = xstream.createObjectOutputStream(stringWriter)) {
			os.writeObject(parent);
		}
        final String actualXml = stringWriter.getBuffer().toString();
        assertEquals(expectedXml, actualXml);
    }

    public void testToXML() {

        xstream.alias("parent", Parent.class);
        xstream.alias("child", Child.class);

        final String expected = ""
            + "<parent serialization=\"custom\">\n"
            + "  <parent>\n"
            + "    <child serialization=\"custom\">\n"
            + "      <child>\n"
            + "        <default>\n"
            + "          <i>1</i>\n"
            + "        </default>\n"
            + "      </child>\n"
            + "    </child>\n"
            + "    <default>\n"
            + "      <name>ze-name</name>\n"
            + "    </default>\n"
            + "  </parent>\n"
            + "</parent>";

        final Parent parent = new Parent("ze-name", new Child(1));

        assertBothWays(parent, expected);
    }

    public static class RawString implements Serializable {

        private String s;

        public RawString(final String s) {
            this.s = s;
        }

        public String getS() {
            return s;
        }

        private void readObject(final java.io.ObjectInputStream in) throws IOException {
            final int i = in.read();
            final byte[] b = new byte[i];
            in.read(b);
            s = new String(b);
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            final byte[] b = s.getBytes();
            out.write(b.length);
            out.write(b);
        }
    }

    public void testCanHandleRawBytes() throws IOException, ClassNotFoundException {
        xstream.alias("raw", RawString.class);

        final String expectedXml = ""
            + "<root>\n"
            + "  <raw serialization=\"custom\">\n"
            + "    <raw>\n"
            + "      <byte>7</byte>\n"
            + "      <byte-array>WFN0cmVhbQ==</byte-array>\n"
            + "    </raw>\n"
            + "  </raw>\n"
            + "</root>";

        final StringWriter stringWriter = new StringWriter();
		try (ObjectOutputStream os = xstream.createObjectOutputStream(stringWriter, "root")) {
			os.writeObject(new RawString("XStream"));
		}
        final String actualXml = stringWriter.getBuffer().toString();
        assertEquals(expectedXml, actualXml);

        @SuppressWarnings("resource")
        final ObjectInputStream objectInputStream = xstream.createObjectInputStream(new StringReader(actualXml));

        final RawString rawString = (RawString)objectInputStream.readObject();
        assertEquals("XStream", rawString.getS());
    }

    static class Store<T> implements Serializable {
        private static final long serialVersionUID = 201011L;
        List<T> store;

        public Store() {
            store = new ArrayList<>();
        }

        private void writeObject(final ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
        }
    }

    static class OtherStore<T> extends Store<T> {
        private static final long serialVersionUID = 201011L;

        private Object readResolve() {
            if (store instanceof LinkedList) {
                final Store<T> replacement = new MyStore<>();
                replacement.store = store;
                return replacement;
            }
            return this;
        }
    }

    static class MyStore<T> extends OtherStore<T> {
        private static final long serialVersionUID = 201011L;

        public MyStore() {
            store = new LinkedList<>();
        }

        private Object writeReplace() {
            final Store<T> replacement = new OtherStore<>();
            replacement.store = store;
            return replacement;
        }
    }

    public void testCachingInheritedWriteObject() throws Exception {
        xstream.alias("store", Store.class);
        xstream.alias("my", MyStore.class);
        xstream.alias("other", OtherStore.class);

        final String expectedXml = ""
            + "<store-array>\n"
            + "  <my resolves-to=\"other\" serialization=\"custom\">\n"
            + "    <store>\n"
            + "      <default>\n"
            + "        <store class=\"linked-list\">\n"
            + "          <string>one</string>\n"
            + "        </store>\n"
            + "      </default>\n"
            + "    </store>\n"
            + "  </my>\n"
            + "  <other serialization=\"custom\">\n"
            + "    <store>\n"
            + "      <default>\n"
            + "        <store>\n"
            + "          <string>two</string>\n"
            + "        </store>\n"
            + "      </default>\n"
            + "    </store>\n"
            + "  </other>\n"
            + "</store-array>";

        @SuppressWarnings("unchecked")
        final Store<String>[] stores = new Store[]{new MyStore<String>(), new OtherStore<String>()};
        stores[0].store.add("one");
        stores[1].store.add("two");

        assertBothWays(stores, expectedXml);
    }

    static class MoscowCalendar extends GregorianCalendar {
        private static final long serialVersionUID = 201202L;

        public MoscowCalendar() {
            super(TimeZone.getTimeZone("Europe/Moscow"));
        }
    }

    public void testNestedSerializationOfDefaultType() {
        final Calendar in = new MoscowCalendar();
        in.setTimeInMillis(44444);
        final String xml = xstream.toXML(in);
        final Calendar out = (Calendar)xstream.fromXML(xml);
        assertEquals(in.getTime(), out.getTime());
    }
}
