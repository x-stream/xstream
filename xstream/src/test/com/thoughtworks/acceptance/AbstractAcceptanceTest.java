/*
 * Copyright (C) 2003, 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2014, 2015, 2021 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.acceptance;

import java.lang.reflect.Array;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.binary.BinaryStreamWriter;
import com.thoughtworks.xstream.io.binary.BinaryStreamReader;
import com.thoughtworks.xstream.io.xml.XppDriver;

public abstract class AbstractAcceptanceTest extends TestCase {

    protected transient XStream xstream = createXStream();
    
    protected XStream createXStream() {
        XStream xstream = new XStream(createDriver());
        setupSecurity(xstream);
        return xstream;
    }

    protected HierarchicalStreamDriver createDriver() {
        // if the system property is set, use it to load the driver
        String driver = null;
        try {
            driver = System.getProperty("xstream.driver");
            if (driver != null) {
                System.out.println("Using driver: " + driver);
                Class type = Class.forName(driver);
                return (HierarchicalStreamDriver) type.newInstance();
            }
        } catch (final Exception e) {
            throw new RuntimeException("Could not load driver: " + driver, e);
        }
        return new XppDriver();
    }
    
    protected void setupSecurity(XStream xstream) {
        xstream.allowTypesByWildcard(new String[]{
            AbstractAcceptanceTest.class.getPackage().getName()+".*objects.**",
            this.getClass().getName()+"$*"
        });
    }
    
    protected Object assertBothWaysNormalized(Object root, String xml, final String match,
        final String templateSelect, final String sortSelect) {
        try {
            // First, serialize the object to XML and check it matches the expected XML.
            String resultXml = normalizedXML(
                toXML(root), new String[]{match}, templateSelect, sortSelect);
            assertEquals(
                normalizedXML(xml, new String[]{match}, templateSelect, sortSelect), resultXml);

            // Now deserialize the XML back into the object and check it equals the original object.
            Object resultRoot = xstream.fromXML(resultXml);
            assertObjectsEqual(root, resultRoot);

            // While we're at it, let's check the binary serialization works...
            assertBinarySerialization(root);

            return resultRoot;

        } catch (final TransformerException e) {
            final AssertionFailedError error = new AssertionFailedError("Cannot normalize XML: " + e.getMessage());
            error.initCause(e);
            throw error;
        }
    }

    protected Object assertBothWays(Object root, String xml) {

        // First, serialize the object to XML and check it matches the expected XML.
        String resultXml = toXML(root);
        assertEquals(xml, resultXml);

        // Now deserialize the XML back into the object and check it equals the original object.
        Object resultRoot = xstream.fromXML(resultXml);
        assertObjectsEqual(root, resultRoot);

        // While we're at it, let's check the binary serialization works...
        assertBinarySerialization(root);

        return resultRoot;
    }

    private void assertBinarySerialization(Object root) {
        // Serialize as binary
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xstream.marshal(root, new BinaryStreamWriter(outputStream));

        // Deserialize the binary and check it equals the original object.
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Object binaryResult = xstream.unmarshal(new BinaryStreamReader(inputStream));
        assertObjectsEqual(root, binaryResult);
    }

    protected Object assertWithAsymmetricalXml(Object root, String inXml, String outXml) {
        String resultXml = toXML(root);
        assertEquals(outXml, resultXml);
        Object resultRoot = xstream.fromXML(inXml);
        assertObjectsEqual(root, resultRoot);
        return resultRoot;
    }
    
    protected static void serialize(final Object object, final OutputStream outputStream) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(outputStream);
            out.writeObject(object);
            out.close();
        } catch (final NotSerializableException e) {
            fail("Serialization of object of type "
                + object.getClass().getName()
                + " failed because of reference to type "
                + e.getMessage(), e);
        } catch (final IOException e) {
            fail("Serialization of object of type " + object.getClass().getName() + " failed", e);
        }
    }

    protected static Object deserialize(final InputStream inputStream) {
        try {
            ObjectInputStream out = new ObjectInputStream(inputStream);
            final Object t = out.readObject();
            out.close();
            return t;
        } catch (final ClassNotFoundException e) {
            fail("Cannot find class " + e.getMessage() + " during deserialization", e);
            throw new AssertionFailedError(); // never reached
        } catch (final IOException e) {
            fail("Deserialization failed reading the InputStream", e);
            throw new AssertionFailedError(); // never reached
        }
    }

    protected Object assertJavaSerialization(final Object in) {
        byte[] data;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            serialize(in, os);
            data = os.toByteArray();
            os.close();
        } catch (final IOException e) {
            fail("Serialization failed closing the OutputStream", e);
            throw new AssertionFailedError(); // never reached
        }
        try {
            InputStream is = new ByteArrayInputStream(data);
            Object t = deserialize(is);
            is.close();
            return t;
        } catch (final IOException e) {
            fail("Deserialization failed closing the InputStream", e);
            throw new AssertionFailedError(); // never reached
        }
    }

    /**
     * Allow derived classes to decide how to turn the object into XML text
     */
    protected String toXML(Object root) {
        return xstream.toXML(root);
    }

    /**
     * More descriptive version of assertEquals
     */ 
    protected void assertObjectsEqual(Object expected, Object actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertNotNull("Should not be null", actual);
            if (actual.getClass().isArray()) {
                assertArrayEquals(expected, actual);
            } else {
//                assertEquals(expected.getClass(), actual.getClass());
                if (!expected.equals(actual)) {
                    assertEquals("Object deserialization failed",
                            "DESERIALIZED OBJECT\n" + xstream.toXML(expected),
                            "DESERIALIZED OBJECT\n" + xstream.toXML(actual));
                }
            }
        }
    }

    protected void assertArrayEquals(Object expected, Object actual) {
        assertEquals(Array.getLength(expected), Array.getLength(actual));
        for (int i = 0; i < Array.getLength(expected); i++) {
            assertObjectsEqual(Array.get(expected, i), Array.get(actual, i));
        }
    }

    protected void assertByteArrayEquals(byte expected[], byte actual[]) {
        assertEquals(dumpBytes(expected), dumpBytes(actual));
    }

    private String dumpBytes(byte bytes[]) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            result.append(bytes[i]).append(' ');
            if (bytes[i] < 100) result.append(' ');
            if (bytes[i] < 10) result.append(' ');
            if (bytes[i] >= 0) result.append(' ');
            if (i % 16 == 15) result.append('\n');
        }
        return result.toString();
    }
    
    protected String normalizedXML(final String xml, final String[] matches,
        final String templateSelect, final String sortSelect) throws TransformerException {
        final StringBuffer match = new StringBuffer();
        for (int i = 0; i < matches.length; i++) {
            if (i > 0) {
                match.append('|');
            }
            match.append(matches[i]);
        }
        final StringBuffer sort = new StringBuffer();
        if (sortSelect != null) {
            sort.append(" select=\"");
            sort.append(sortSelect);
            sort.append('"');
        }

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory
            .newTransformer(new StreamSource(
                new StringReader(
                    ""
                        + "<?xml version=\"1.0\"?>\n"
                        + "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">\n"
                        + "<xsl:template match=\"" + match.toString() + "\">\n"
                        + "   <xsl:copy>\n"
                        + "           <xsl:apply-templates select=\"" + templateSelect + "\">\n"
                        + "                   <xsl:sort" + sort.toString() + "/>\n"
                        + "           </xsl:apply-templates>\n"
                        + "   </xsl:copy>\n"
                        + "</xsl:template>\n"
                        + "<xsl:template match=\"@*|node()\">\n"
                        + "   <xsl:copy>\n"
                        + "           <xsl:apply-templates select=\"@*|node()\"/>\n"
                        + "   </xsl:copy>\n"
                        + "</xsl:template>\n"
                        + "</xsl:stylesheet>")));
        final StringWriter writer = new StringWriter();
        transformer
            .transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
        return writer.toString();
    }

    protected static void fail(final String message, final Throwable cause) {
        final AssertionFailedError err = new AssertionFailedError(message);
        err.initCause(cause);
        throw err;
    }
}
