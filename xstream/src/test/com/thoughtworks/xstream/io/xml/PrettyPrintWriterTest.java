package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import junit.framework.TestCase;

import java.io.StringWriter;

public class PrettyPrintWriterTest extends TestCase {

    public void testXmlIsIndented() {
        StringWriter stringWriter = new StringWriter();
        HierarchicalStreamWriter xmlWriter = new PrettyPrintWriter(stringWriter, "  ");

        xmlWriter.startNode("hello");
        xmlWriter.startNode("world");
        xmlWriter.addAttribute("id", "one");

        xmlWriter.startNode("one");
        xmlWriter.setValue("potato");
        xmlWriter.startNode();

        xmlWriter.startNode("two");
        xmlWriter.addAttribute("id", "two");
        xmlWriter.setValue("potatae");
        xmlWriter.startNode();

        xmlWriter.startNode();

        xmlWriter.startNode("empty");
        xmlWriter.startNode();

        xmlWriter.startNode();

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
