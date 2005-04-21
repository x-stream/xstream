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
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * @author James Strachan
 */
public class StaxWriterTest extends TestCase {

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
    private XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

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
    }

    public void testMarshalsObjectToSAX() throws XMLStreamException {
        String expected="<?xml version='1.0' encoding='utf-8'?><x><aStr>zzz</aStr><anInt>9</anInt><innerObj><yField>ooo</yField></innerObj></x>";

        Writer buffer = new StringWriter();
        StaxWriter writer = createStaxWriter(buffer);

        xstream.marshal(testInput, writer);

        assertEquals(expected, buffer.toString());
    }

    protected StaxWriter createStaxWriter(Writer buffer) throws XMLStreamException {
        return new StaxWriter(new QNameMap(), outputFactory.createXMLStreamWriter(buffer));
    }

}

