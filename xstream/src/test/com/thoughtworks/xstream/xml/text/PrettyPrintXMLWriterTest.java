package com.thoughtworks.xstream.xml.text;

import com.thoughtworks.xstream.xml.XMLWriter;
import junit.framework.TestCase;

import java.io.StringWriter;

public class PrettyPrintXMLWriterTest extends TestCase {

    public void testXmlIsIndented() {
        StringWriter stringWriter = new StringWriter();
        XMLWriter xmlWriter = new PrettyPrintXMLWriter(stringWriter, "  ");

        xmlWriter.pushElement("hello");
        xmlWriter.pushElement("world");
        xmlWriter.attribute("id", "one");

        xmlWriter.pushElement("one");
        xmlWriter.text("potato");
        xmlWriter.pop();

        xmlWriter.pushElement("two");
        xmlWriter.attribute("id", "two");
        xmlWriter.text("potatae");
        xmlWriter.pop();

        xmlWriter.pop();
        xmlWriter.pop();

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
