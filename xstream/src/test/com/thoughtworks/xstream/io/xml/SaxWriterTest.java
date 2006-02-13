package com.thoughtworks.xstream.io.xml;

import com.megginson.sax.DataWriter;
import com.thoughtworks.acceptance.someobjects.X;
import com.thoughtworks.acceptance.someobjects.Y;
import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * @author Laurent Bihanic
 */
public class SaxWriterTest extends TestCase {

    private final static String IDENTITY_STYLESHEET =
            "<xsl:stylesheet version=\"1.0\"" +
            "                xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" +
            "\n" +
            "  <xsl:output method=\"xml\"" +
            "              omit-xml-declaration=\"yes\" indent=\"no\"/>\n" +
            "\n" +
            "  <xsl:template" +
            "      match=\"*|@*|comment()|processing-instruction()|text()\">\n" +
            "    <xsl:copy>\n" +
            "      <xsl:apply-templates" +
            "          select=\"*|@*|comment()|processing-instruction()|text()\"/>\n" +
            "    </xsl:copy>\n" +
            "  </xsl:template>\n" +
            "</xsl:stylesheet>";

    private XStream xstream;
    private X testInput;
    private Templates identityStylesheet;

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

        identityStylesheet = TransformerFactory.newInstance().newTemplates(new StreamSource(new StringReader(IDENTITY_STYLESHEET)));
    }

    public void testMarshalsObjectToSAX() {
        String expected =
                "<?xml version=\"1.0\" standalone=\"yes\"?>\n\n" +
                "<x>\n" +
                "  <aStr>zzz</aStr>\n" +
                "  <anInt>9</anInt>\n" +
                "  <innerObj>\n" +
                "    <yField>ooo</yField>\n" +
                "  </innerObj>\n" +
                "</x>\n\n";

        Writer buffer = new StringWriter();
        SaxWriter writer = new SaxWriter();
        DataWriter outputter = new DataWriter(writer, buffer);
        outputter.setIndentStep(2);

        writer.setContentHandler(outputter);

        xstream.marshal(testInput, writer);

        assertEquals(expected, buffer.toString());
    }

    public void testAllowsStartAndEndDocCallbacksToBeSkipped() {
        String expected =
                "<int>1</int>\n" +
                "<int>2</int>\n" +
                "<int>3</int>\n";

        Writer buffer = new StringWriter();
        SaxWriter writer = new SaxWriter(false);
        DataWriter outputter = new DataWriter(writer, buffer);
        outputter.setIndentStep(2);

        writer.setContentHandler(outputter);

        xstream.marshal(new Integer(1), writer);
        xstream.marshal(new Integer(2), writer);
        xstream.marshal(new Integer(3), writer);

        assertEquals(expected, buffer.toString());
    }

    public void testMarshalsObjectToTrAX() throws Exception {
        String expected =
                "<x><aStr>zzz</aStr><anInt>9</anInt>" +
                "<innerObj><yField>ooo</yField></innerObj>" +
                "</x>" +
                "<y><yField>ooo</yField></y>";

        TraxSource traxSource = new TraxSource();
        traxSource.setXStream(xstream);
        traxSource.setSourceAsList(Arrays.asList(new Object[]{testInput, testInput.innerObj}));

        Writer buffer = new StringWriter();
        Transformer transformer = identityStylesheet.newTransformer();

        transformer.transform(traxSource, new StreamResult(buffer));

        assertEquals(expected, buffer.toString());
    }

    public void testNullSourceObject() {
        TraxSource traxSource = new TraxSource();

        try {
            traxSource.setSource(null);
            fail("Null source object not rejected");
        } catch (IllegalArgumentException e) { /* good! */
        }
    }

    public void testNullSourceList() {
        TraxSource traxSource = new TraxSource();

        try {
            traxSource.setSourceAsList(null);
            fail("Null source list not rejected");
        } catch (IllegalArgumentException e) { /* good! */
        }
    }

    public void testEmptySourceList() {
        TraxSource traxSource = new TraxSource();

        try {
            traxSource.setSourceAsList(Collections.EMPTY_LIST);
            fail("Empty source list not rejected");
        } catch (IllegalArgumentException e) { /* good! */
        }
    }

    /**
     * This method tests a quite insidious side-effect of
     * XStreamSource delaying the allocation and configuration of
     * the SAXWriter until the XSLT processor requests it.
     * <p/>
     * SAXWriter performs a copy of the source list contents upon
     * property setting to avoid objects being added or removed from
     * the list during the parse.</p>
     * <p/>
     * To avoid just another list copy, XStreamSource does not
     * protect itself against list changes. Hence, it is possible
     * for an application to configure the XStreamSource and then
     * empty the list prior triggering the XSL transformation.</p>.
     * <p/>
     * This method ensures SAXWriter indeed checks the list content
     * prior starting the parse.</p>
     */
    public void testEmptySourceListAtParse() throws Exception {
        TraxSource traxSource = new TraxSource();
        Writer buffer = new StringWriter();

        List list = new ArrayList();
        list.add(testInput);

        traxSource.setSourceAsList(list);
        list.clear();

        Transformer transformer = identityStylesheet.newTransformer();
        transformer.setErrorListener(new TrAXErrorListener());

        try {
            transformer.transform(traxSource, new StreamResult(buffer));

            fail("Empty source list not rejected");
        } catch (Exception expectedException) {
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

        public void warning(TransformerException e) {
            /* Ignore... */
        }

        public void error(TransformerException e) {
            /* Ignore... */
        }

        public void fatalError(TransformerException e)
                throws TransformerException {
            throw e;
        }
    }
}

