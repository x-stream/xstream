/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 14. August 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.megginson.sax.DataWriter;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;


/*
 * @author Laurent Bihanic
 */
public class SaxWriterTest extends TestCase {

    private final static String IDENTITY_STYLESHEET = ""
        + "<xsl:stylesheet version=\"1.0\""
        + "                xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
        + "\n"
        + "  <xsl:output method=\"xml\""
        + "              omit-xml-declaration=\"yes\" indent=\"no\"/>\n"
        + "\n"
        + "  <xsl:template"
        + "      match=\"*|@*|comment()|processing-instruction()|text()\">\n"
        + "    <xsl:copy>\n"
        + "      <xsl:apply-templates"
        + "          select=\"*|@*|comment()|processing-instruction()|text()\"/>\n"
        + "    </xsl:copy>\n"
        + "  </xsl:template>\n"
        + "</xsl:stylesheet>";

    private XStream xstream;
    private X testInput;
    private Templates identityStylesheet;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("x", X.class);
        xstream.alias("y", Y.class);

        testInput = new X();
        testInput.anInt = 9;
        testInput.aStr = "zzz";
        testInput.innerObj = new Y();
        testInput.innerObj.yField = "ooo";

        identityStylesheet = TransformerFactory.newInstance().newTemplates(new StreamSource(new StringReader(
            IDENTITY_STYLESHEET)));
    }

    public void testMarshalsObjectToSAX() {
        final String expected = ""
            + "<?xml version=\"1.0\" standalone=\"yes\"?>\n\n"
            + "<x>\n"
            + "  <aStr>zzz</aStr>\n"
            + "  <anInt>9</anInt>\n"
            + "  <innerObj>\n"
            + "    <yField>ooo</yField>\n"
            + "  </innerObj>\n"
            + "</x>\n\n";

        final Writer buffer = new StringWriter();
        try (final SaxWriter writer = new SaxWriter()) {
            final DataWriter outputter = new DataWriter(writer, buffer);
            outputter.setIndentStep(2);

            writer.setContentHandler(outputter);

            xstream.marshal(testInput, writer);
        }

        assertEquals(expected, buffer.toString());
    }

    public void testAllowsStartAndEndDocCallbacksToBeSkipped() {
        final String expected = "" //
            + "<int>1</int>\n"
            + "<int>2</int>\n"
            + "<int>3</int>\n";

        final Writer buffer = new StringWriter();
        try (final SaxWriter writer = new SaxWriter(false)) {
            final DataWriter outputter = new DataWriter(writer, buffer);
            outputter.setIndentStep(2);

            writer.setContentHandler(outputter);

            xstream.marshal(new Integer(1), writer);
            xstream.marshal(new Integer(2), writer);
            xstream.marshal(new Integer(3), writer);
        }

        assertEquals(expected, buffer.toString());
    }

    public void testMarshalsObjectToTrAX() throws Exception {
        final String expected = ""
            + "<x><aStr>zzz</aStr><anInt>9</anInt>"
            + "<innerObj><yField>ooo</yField></innerObj>"
            + "</x>"
            + "<y><yField>ooo</yField></y>";

        final TraxSource traxSource = new TraxSource();
        traxSource.setXStream(xstream);
        traxSource.setSourceAsList(Arrays.asList(new Object[]{testInput, testInput.innerObj}));

        final Writer buffer = new StringWriter();
        final Transformer transformer = identityStylesheet.newTransformer();

        transformer.transform(traxSource, new StreamResult(buffer));

        assertEquals(expected, buffer.toString());
    }

    public void testNullSourceObject() {
        final TraxSource traxSource = new TraxSource();

        try {
            traxSource.setSource(null);
            fail("Null source object not rejected");
        } catch (final IllegalArgumentException e) { /* good! */
        }
    }

    public void testNullSourceList() {
        final TraxSource traxSource = new TraxSource();

        try {
            traxSource.setSourceAsList(null);
            fail("Null source list not rejected");
        } catch (final IllegalArgumentException e) { /* good! */
        }
    }

    public void testEmptySourceList() {
        final TraxSource traxSource = new TraxSource();

        try {
            traxSource.setSourceAsList(Collections.EMPTY_LIST);
            fail("Empty source list not rejected");
        } catch (final IllegalArgumentException e) { /* good! */
        }
    }

    /**
     * This method tests a quite insidious side-effect of XStreamSource delaying the allocation and configuration of the
     * SAXWriter until the XSLT processor requests it.
     * <p/>
     * SAXWriter performs a copy of the source list contents upon property setting to avoid objects being added or
     * removed from the list during the parse.
     * </p>
     * <p/>
     * To avoid just another list copy, XStreamSource does not protect itself against list changes. Hence, it is
     * possible for an application to configure the XStreamSource and then empty the list prior triggering the XSL
     * transformation.
     * </p>
     * .
     * <p/>
     * This method ensures SAXWriter indeed checks the list content prior starting the parse.
     * </p>
     */
    public void testEmptySourceListAtParse() throws Exception {
        final TraxSource traxSource = new TraxSource();
        final Writer buffer = new StringWriter();

        final List<X> list = new ArrayList<>();
        list.add(testInput);

        traxSource.setSourceAsList(list);
        list.clear();

        final Transformer transformer = identityStylesheet.newTransformer();
        transformer.setErrorListener(new TrAXErrorListener());

        try {
            transformer.transform(traxSource, new StreamResult(buffer));

            fail("Empty source list not rejected");
        } catch (final Exception expectedException) {
            if (expectedException.getMessage().endsWith("shall not be an empty list")) {
                // Good!
            } else {
                throw expectedException;
            }
        }
    }

    private static class TrAXErrorListener implements ErrorListener {
        public TrAXErrorListener() {
            super();
        }

        @Override
        public void warning(final TransformerException e) {
            /* Ignore... */
        }

        @Override
        public void error(final TransformerException e) {
            /* Ignore... */
        }

        @Override
        public void fatalError(final TransformerException e) throws TransformerException {
            throw e;
        }
    }
}
