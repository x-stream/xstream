package com.thoughtworks.xstream.xml.text;

import com.thoughtworks.xstream.xml.XMLWriter;
import junit.framework.TestCase;

import java.io.StringWriter;

public class PrettyPrintXMLWriterTest extends TestCase {

    public void testXmlIsIndented() {
        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter(stringWriter, "  ");

        xmlWriter.startElement("hello");
        xmlWriter.startElement("world");
        xmlWriter.addAttribute("id", "one");

        xmlWriter.startElement("one");
        xmlWriter.writeText("potato");
        xmlWriter.endElement();

        xmlWriter.startElement("two");
        xmlWriter.addAttribute("id", "two");
        xmlWriter.writeText("potatae");
        xmlWriter.endElement();

        xmlWriter.endElement();
        xmlWriter.endElement();

        String expected =
                "<hello>\n" +
                "  <world id=\"one\">\n" +
                "    <one>potato</one>\n" +
                "    <two id=\"two\">potatae</two>\n" +
                "  </world>\n" +
                "</hello>";

        assertEquals(expected, stringWriter.toString());
    }

}
