package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import junit.framework.TestCase;

import java.io.StringWriter;

public class PrettyPrintWriterTest extends TestCase {

    public void testXmlIsIndented() {
        StringWriter stringWriter = new StringWriter();
        HierarchicalStreamWriter xmlWriter = new PrettyPrintWriter(stringWriter, "  ");

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

        xmlWriter.startElement("empty");
        xmlWriter.endElement();

        xmlWriter.endElement();

        String expected =
                "<hello>\n" +
                "  <world id=\"one\">\n" +
                "    <one>potato</one>\n" +
                "    <two id=\"two\">potatae</two>\n" +
                "  </world>\n" +
                "  <empty/>\n" +
                "</hello>";

        assertEquals(expected, stringWriter.toString());
    }

}
