/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. May 2004 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.thoughtworks.acceptance.objects.StatusEnum;
import com.thoughtworks.xstream.converters.ConversionException;


/**
 * @author Chris Kelly
 * @author Joe Walnes
 */
public class ReadResolveTest extends AbstractAcceptanceTest {

    public void testReadResolveWithDefaultSerialization() throws IOException, ClassNotFoundException {
        final StatusEnum status = StatusEnum.STARTED;

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ObjectOutputStream os = new ObjectOutputStream(bout);
        os.writeObject(status);

        final byte[] bArray = bout.toByteArray();
        StatusEnum rStatus = null;
        ObjectInputStream in = null;

        final ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
        in = new ObjectInputStream(bin);
        rStatus = (StatusEnum)in.readObject();
        assertNotNull(rStatus);

        assertSame(status, rStatus);
    }

    public void testReadResolveWithXStream() {
        final StatusEnum status = StatusEnum.STARTED;

        final String xml = xstream.toXML(status);
        final StatusEnum rStatus = (StatusEnum)xstream.fromXML(xml);

        assertSame(status, rStatus);
    }

    public static class ResolveToNull implements Serializable {
        private static final long serialVersionUID = 201412L;
        final String name;

        public ResolveToNull(final String name) {
            this.name = name;
        }

        private Object readResolve() {
            return null;
        }
    }

    public void testResolveToNull() throws IOException, ClassNotFoundException {
        final ResolveToNull obj = new ResolveToNull("test");

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final ObjectOutputStream os = new ObjectOutputStream(bout);
        os.writeObject(obj);

        final byte[] bArray = bout.toByteArray();
        ObjectInputStream in = null;
        final ByteArrayInputStream bin = new ByteArrayInputStream(bArray);
        in = new ObjectInputStream(bin);
        assertNull(in.readObject());

        xstream.alias("toNull", ResolveToNull.class);
        assertNull(xstream.fromXML("<toNull><name>test</name></toNull>"));
    }

    public void testOutOfMemoryInReadObject() {
        final String xml = ""
                + "<java.util.PriorityQueue serialization='custom'>\n"
                + "  <unserializable-parents/>\n"
                + "  <java.util.PriorityQueue>\n"
                + "    <default>\n"
                + "      <size>2147483647</size>\n"
                + "    </default>\n"
                + "    <int>2</int>\n"
                + "  </java.util.PriorityQueue>\n"
                + "</java.util.PriorityQueue>";

        try {
            xstream.fromXML(xml);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            // OK
        }
    }
}
