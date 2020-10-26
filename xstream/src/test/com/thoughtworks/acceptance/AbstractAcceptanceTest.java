/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.acceptance;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.DefaultDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.binary.BinaryStreamReader;
import com.thoughtworks.xstream.io.binary.BinaryStreamWriter;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;


public abstract class AbstractAcceptanceTest extends TestCase {

    protected transient XStream xstream = createXStream();

    protected XStream createXStream() {
        final XStream xstream = new XStream(createDriver());
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
                final Class<?> type = Class.forName(driver);
                return (HierarchicalStreamDriver)type.newInstance();
            }
        } catch (final Exception e) {
            throw new RuntimeException("Could not load driver: " + driver, e);
        }
        return DefaultDriver.create();
    }

    protected void setupSecurity(final XStream xstream) {
        xstream.allowTypesByWildcard(AbstractAcceptanceTest.class.getPackage().getName() + ".*objects.**");
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
    }

    protected <T> T assertBothWaysNormalized(final Object root, final String xml, final String match,
            final String templateSelect, final String sortSelect) {
        try {
            // first, serialize the object to XML and check it matches the expected XML.
            final String resultXml = normalizedXML(toXML(root), new String[]{match}, templateSelect, sortSelect);
            assertEquals(normalizedXML(xml, new String[]{match}, templateSelect, sortSelect), resultXml);

            // now deserialize the XML back into the object and check it equals the original object.
            final T resultRoot = xstream.fromXML(resultXml);
            assertObjectsEqual(root, resultRoot);

            // while we're at it, let's check the binary serialization works...
            assertBinarySerialization(root);

            return resultRoot;

        } catch (final TransformerException e) {
            final AssertionFailedError error = new AssertionFailedError("Cannot normalize XML: " + e.getMessage());
            error.initCause(e);
            throw error;
        }
    }

    protected <T> T assertBothWays(final Object root, final String xml) {

        // first, serialize the object to XML and check it matches the expected XML.
        final String resultXml = toXML(root);
        assertEquals(xml, resultXml);

        // now deserialize the XML back into the object and check it equals the original object.
        final T resultRoot = xstream.fromXML(resultXml);
        assertObjectsEqual(root, resultRoot);

        // while we're at it, let's check the binary serialization works...
        assertBinarySerialization(root);

        return resultRoot;
    }

    @SuppressWarnings("resource")
    private void assertBinarySerialization(final Object root) {
        // serialize as binary
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        xstream.marshal(root, new BinaryStreamWriter(outputStream));

        // deserialize the binary and check it equals the original object.
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        final Object binaryResult = xstream.unmarshal(new BinaryStreamReader(inputStream));
        assertObjectsEqual(root, binaryResult);
    }

    protected <T> T assertWithAsymmetricalXml(final Object root, final String inXml, final String outXml) {
        final String resultXml = toXML(root);
        assertEquals(outXml, resultXml);
        final T resultRoot = xstream.fromXML(inXml);
        assertObjectsEqual(root, resultRoot);
        return resultRoot;
    }

    /**
     * Allow derived classes to decide how to turn the object into XML text
     */
    protected String toXML(final Object root) {
        return xstream.toXML(root);
    }

    /**
     * Alternative version of assertEquals comparing the XML representation of the two objects.
     */
    protected void assertObjectsEqual(final Object expected, final Object actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertNotNull("Should not be null", actual);
            if (actual.getClass().isArray()) {
                assertArrayEquals(expected, actual);
            } else {
                // assertEquals(expected.getClass(), actual.getClass());
                if (!expected.equals(actual)) {
                    assertEquals("Object deserialization failed", "DESERIALIZED OBJECT\n" + xstream.toXML(expected),
                        "DESERIALIZED OBJECT\n" + xstream.toXML(actual));
                }
            }
        }
    }

    protected void assertArrayEquals(final Object expected, final Object actual) {
        assertEquals(Array.getLength(expected), Array.getLength(actual));
        for (int i = 0; i < Array.getLength(expected); i++) {
            assertObjectsEqual(Array.get(expected, i), Array.get(actual, i));
        }
    }

    protected void assertByteArrayEquals(final byte expected[], final byte actual[]) {
        assertEquals(dumpBytes(expected), dumpBytes(actual));
    }

    private String dumpBytes(final byte bytes[]) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            result.append(bytes[i]).append(' ');
            if (bytes[i] < 100) {
                result.append(' ');
            }
            if (bytes[i] < 10) {
                result.append(' ');
            }
            if (bytes[i] >= 0) {
                result.append(' ');
            }
            if (i % 16 == 15) {
                result.append('\n');
            }
        }
        return result.toString();
    }

    protected String normalizedXML(final String xml, final String[] matches, final String templateSelect,
            final String sortSelect)
            throws TransformerException {
        final StringBuilder match = new StringBuilder();
        for (int i = 0; i < matches.length; i++) {
            if (i > 0) {
                match.append('|');
            }
            match.append(matches[i]);
        }
        final StringBuilder sort = new StringBuilder();
        if (sortSelect != null) {
            sort.append(" select=\"");
            sort.append(sortSelect);
            sort.append('"');
        }

        final String stylesheet = String
            .format(""
                + "<?xml version=\"1.0\"?>\n"
                + "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">\n"
                + "<xsl:template match=\"%1$s\">\n"
                + "   <xsl:copy>\n"
                + "           <xsl:apply-templates select=\"%2$s\">\n"
                + "                   <xsl:sort%3$s/>\n"
                + "           </xsl:apply-templates>\n"
                + "   </xsl:copy>\n"
                + "</xsl:template>\n"
                + "<xsl:template match=\"@*|node()\">\n"
                + "   <xsl:copy>\n"
                + "           <xsl:apply-templates select=\"@*|node()\"/>\n"
                + "   </xsl:copy>\n"
                + "</xsl:template>\n"
                + "</xsl:stylesheet>", match, templateSelect, sort);

        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory
            .newTransformer(new StreamSource(new StringReader(stylesheet)));
        final StringWriter writer = new StringWriter();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(writer));
        return writer.toString();
    }
}
