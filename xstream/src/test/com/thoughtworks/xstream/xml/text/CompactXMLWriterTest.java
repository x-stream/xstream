package com.thoughtworks.xstream.xml.text;

import com.thoughtworks.xstream.xml.XMLWriter;
import junit.framework.TestCase;

import java.io.StringWriter;

public class CompactXMLWriterTest extends TestCase {
    private StringWriter stringWriter;
    private XMLWriter xmlWriter;

    protected void setUp() throws Exception {
        super.setUp();
        stringWriter = new StringWriter();
        xmlWriter = new CompactXMLWriter(stringWriter);
    }

    public void testXmlIsIndented() {
        xmlWriter.startElement("hello");
        xmlWriter.startElement("world");

        xmlWriter.startElement("one");
        xmlWriter.writeText("potato");
        xmlWriter.endElement();

        xmlWriter.startElement("two");
        xmlWriter.writeText("potatae");
        xmlWriter.endElement();

        xmlWriter.endElement();
        xmlWriter.endElement();

        String expected = "<hello><world><one>potato</one><two>potatae</two></world></hello>";

        assertEquals(expected, stringWriter.toString());
    }

    public void testEncodesFunnyXmlChars() {
        xmlWriter.startElement("tag");
        xmlWriter.writeText("hello & this isn't \"really\" <good>");
        xmlWriter.endElement();

        String expected = "<tag>hello &amp; this isn't \"really\" &lt;good&gt;</tag>";

        assertEquals(expected, stringWriter.toString());
    }

    public void testAttributesCanBeWritten() {
        xmlWriter.startElement("tag");
        xmlWriter.addAttribute("hello", "world");
        xmlWriter.startElement("inner");
        xmlWriter.addAttribute("foo", "bar");
        xmlWriter.addAttribute("poo", "par");
        xmlWriter.writeText("hi");
        xmlWriter.endElement();
        xmlWriter.endElement();

        String expected = "" +
                "<tag hello=\"world\">" +
                "<inner foo=\"bar\" poo=\"par\">hi</inner>" +
                "</tag>";

        assertEquals(expected, stringWriter.toString());
    }

}
