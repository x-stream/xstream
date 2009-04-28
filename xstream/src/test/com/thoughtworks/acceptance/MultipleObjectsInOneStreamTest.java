/*
 * Copyright (C) 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. December 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.acceptance.objects.Software;
import com.thoughtworks.acceptance.objects.StandardObject;
import com.thoughtworks.xstream.MarshallingStrategy;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.DataHolder;
import com.thoughtworks.xstream.core.ReferenceByIdMarshaller;
import com.thoughtworks.xstream.core.ReferenceByIdUnmarshaller;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppReader;
import com.thoughtworks.xstream.mapper.Mapper;
import com.thoughtworks.xstream.testutil.CallLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;


public class MultipleObjectsInOneStreamTest extends AbstractAcceptanceTest {

    public static class Person extends StandardObject {

        private String firstName;
        private String lastName;
        private Person secretary;

        public Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

    }

    public void testReadAndWriteMultipleObjectsInOneStream() {
        xstream.alias("person", Person.class);
        StringWriter buffer = new StringWriter();

        // serialize
        HierarchicalStreamWriter writer = new PrettyPrintWriter(buffer);
        writer.startNode("people");
        xstream.marshal(new Person("Postman", "Pat"), writer);
        xstream.marshal(new Person("Bob", "Builder"), writer);
        xstream.marshal(new Person("Tinky", "Winky"), writer);
        writer.endNode();

        assertEquals(""
            + "<people>\n"
            + "  <person>\n"
            + "    <firstName>Postman</firstName>\n"
            + "    <lastName>Pat</lastName>\n"
            + "  </person>\n"
            + "  <person>\n"
            + "    <firstName>Bob</firstName>\n"
            + "    <lastName>Builder</lastName>\n"
            + "  </person>\n"
            + "  <person>\n"
            + "    <firstName>Tinky</firstName>\n"
            + "    <lastName>Winky</lastName>\n"
            + "  </person>\n"
            + "</people>", buffer.toString());

        // deserialize
        HierarchicalStreamReader reader = new XppReader(new StringReader(buffer.toString()));

        assertTrue("should be another object to read (1)", reader.hasMoreChildren());
        reader.moveDown();
        assertEquals(new Person("Postman", "Pat"), xstream.unmarshal(reader));
        reader.moveUp();

        assertTrue("should be another object to read (2)", reader.hasMoreChildren());
        reader.moveDown();
        assertEquals(new Person("Bob", "Builder"), xstream.unmarshal(reader));
        reader.moveUp();

        assertTrue("should be another object to read (3)", reader.hasMoreChildren());
        reader.moveDown();
        assertEquals(new Person("Tinky", "Winky"), xstream.unmarshal(reader));
        reader.moveUp();

        assertFalse("should be no more objects", reader.hasMoreChildren());
    }

    public void testDrivenThroughObjectStream() throws IOException, ClassNotFoundException {
        Writer writer = new StringWriter();
        xstream.alias("software", Software.class);

        ObjectOutputStream oos = xstream.createObjectOutputStream(writer);
        oos.writeInt(123);
        oos.writeObject("hello");
        oos.writeObject(new Software("tw", "xs"));
        oos.close();

        String expectedXml = ""
            + "<object-stream>\n"
            + "  <int>123</int>\n"
            + "  <string>hello</string>\n"
            + "  <software>\n"
            + "    <vendor>tw</vendor>\n"
            + "    <name>xs</name>\n"
            + "  </software>\n"
            + "</object-stream>";

        assertEquals(expectedXml, writer.toString());

        ObjectInputStream ois = xstream.createObjectInputStream(new StringReader(writer
            .toString()));
        assertEquals(123, ois.readInt());
        assertEquals("hello", ois.readObject());
        assertEquals(new Software("tw", "xs"), ois.readObject());

        try {
            ois.readObject(); // As far as I can see this is the only clue the
                                // ObjectInputStream gives that it's done.
            fail("Expected EOFException");
        } catch (EOFException expectedException) {
            // good
        }
    }

    public void testDrivenThroughCompressedObjectStream()
        throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(new DeflaterOutputStream(baos, new Deflater(
            Deflater.BEST_COMPRESSION)), "UTF-8");
        xstream.alias("software", Software.class);

        ObjectOutputStream oos = xstream.createObjectOutputStream(writer);
        oos.writeInt(123);
        oos.writeObject("hello");
        oos.writeObject(new Software("tw", "xs"));
        oos.flush();
        oos.close();

        byte[] data = baos.toByteArray();
        assertTrue("Too less data: " + data.length, data.length > 2);

        ObjectInputStream ois = xstream.createObjectInputStream(new InputStreamReader(
            new InflaterInputStream(new ByteArrayInputStream(data), new Inflater()), "UTF-8"));
        assertEquals(123, ois.readInt());
        assertEquals("hello", ois.readObject());
        assertEquals(new Software("tw", "xs"), ois.readObject());

        try {
            ois.readObject(); // As far as I can see this is the only clue the
                                // ObjectInputStream gives that it's done.
            fail("Expected EOFException");
        } catch (EOFException expectedException) {
            // good
        }
    }

    public void testObjectOutputStreamPropagatesCloseAndFlushEvents() throws IOException {
        // setup
        final CallLog log = new CallLog();
        Writer loggingWriter = new Writer() {
            public void close() {
                log.actual("close");
            }

            public void flush() {
                log.actual("flush");
            }

            public void write(char cbuf[], int off, int len) {
                // don't care about this
            }
        };

        // expectations
        log.expect("flush"); // TWO flushes are currently caused. Only one is needed, but
                                // this is no big deal.
        log.expect("flush");
        log.expect("close");

        // execute
        ObjectOutputStream objectOutputStream = xstream.createObjectOutputStream(loggingWriter);
        objectOutputStream.flush();
        objectOutputStream.close();

        // verify
        log.verify();
    }

    public void testObjectInputStreamPropegatesCloseEvent() throws IOException {
        // setup
        final CallLog log = new CallLog();
        Reader loggingReader = new StringReader("<int>1</int>") {
            public void close() {
                log.actual("close");
            }
        };

        // expectations
        log.expect("close");

        // execute
        ObjectInputStream objectInputStream = xstream.createObjectInputStream(loggingReader);
        objectInputStream.close();

        // verify
        log.verify();
    }

    public void testByDefaultDoesNotPreserveReferencesAcrossDifferentObjectsInStream()
        throws Exception {
        xstream.alias("person", Person.class);

        // Setup initial data: two object, one referencing another...
        Person alice = new Person("Alice", "Thing");
        Person jane = new Person("Jane", "Blah");
        jane.secretary = alice;

        // Serialize the two individual objects.
        StringWriter writer = new StringWriter();
        ObjectOutputStream out = xstream.createObjectOutputStream(writer);
        out.writeObject(alice);
        out.writeObject(jane);
        out.close();

        // Deserialize the two objects.
        ObjectInputStream in = xstream.createObjectInputStream(new StringReader(writer
            .toString()));
        alice = (Person)in.readObject();
        jane = (Person)in.readObject();
        in.close();

        assertNotSame(alice, jane.secretary); // NOT SAME
    }

    static class ReusingReferenceByIdMarshallingStrategy implements MarshallingStrategy {

        private ReferenceByIdMarshaller marshaller;
        private ReferenceByIdUnmarshaller unmarshaller;

        public void marshal(HierarchicalStreamWriter writer, Object obj,
            ConverterLookup converterLookup, Mapper mapper, DataHolder dataHolder) {
            if (marshaller == null) {
                marshaller = new ReferenceByIdMarshaller(writer, converterLookup, mapper);
            }
            marshaller.start(obj, dataHolder);
        }

        public Object unmarshal(Object root, HierarchicalStreamReader reader,
            DataHolder dataHolder, ConverterLookup converterLookup, Mapper mapper) {
            if (unmarshaller == null) {
                unmarshaller = new ReferenceByIdUnmarshaller(
                    root, reader, converterLookup, mapper);
            }
            return unmarshaller.start(dataHolder);
        }
    }

    public void testSupportsOptionToPreserveReferencesAcrossDifferentObjectsInStream()
        throws Exception {
        xstream.alias("person", Person.class);
        xstream.setMarshallingStrategy(new ReusingReferenceByIdMarshallingStrategy());

        // Setup initial data: two object, one referencing another...
        Person alice = new Person("Alice", "Thing");
        Person jane = new Person("Jane", "Blah");
        jane.secretary = alice;

        // Serialize the two individual objects.
        StringWriter writer = new StringWriter();
        ObjectOutputStream out = xstream.createObjectOutputStream(writer);
        out.writeObject(alice);
        out.writeObject(jane);
        out.close();

        // Deserialize the two objects.
        ObjectInputStream in = xstream.createObjectInputStream(new StringReader(writer
            .toString()));
        alice = (Person)in.readObject();
        jane = (Person)in.readObject();
        in.close();

        assertSame(alice, jane.secretary);
    }
}
