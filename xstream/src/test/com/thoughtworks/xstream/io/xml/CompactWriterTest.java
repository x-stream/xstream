package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import junit.framework.TestCase;

import java.io.StringWriter;

public class CompactWriterTest extends TestCase {
    private StringWriter stringWriter;
    private HierarchicalStreamWriter xmlWriter;

    protected void setUp() throws Exception {
        super.setUp();
        stringWriter = new StringWriter();
        xmlWriter = new CompactWriter(stringWriter);
    }

    public void testXmlIsIndented() {
        xmlWriter.startNode("hello");
        xmlWriter.startNode("world");

        xmlWriter.startNode("one");
        xmlWriter.setValue("potato");
        xmlWriter.startNode();

        xmlWriter.startNode("two");
        xmlWriter.setValue("potatae");
        xmlWriter.startNode();

        xmlWriter.startNode();
        xmlWriter.startNode();

        String expected = "<hello><world><one>potato</one><two>potatae</two></world></hello>";

        assertEquals(expected, stringWriter.toString());
    }

    public void testEncodesFunnyXmlChars() {
        xmlWriter.startNode("tag");
        xmlWriter.setValue("hello & this isn't \"really\" <good>");
        xmlWriter.startNode();

        String expected = "<tag>hello &amp; this isn't \"really\" &lt;good&gt;</tag>";

        assertEquals(expected, stringWriter.toString());
    }

    public void testAttributesCanBeWritten() {
        xmlWriter.startNode("tag");
        xmlWriter.addAttribute("hello", "world");
        xmlWriter.startNode("inner");
        xmlWriter.addAttribute("foo", "bar");
        xmlWriter.addAttribute("poo", "par");
        xmlWriter.setValue("hi");
        xmlWriter.startNode();
        xmlWriter.startNode();

        String expected = "" +
                "<tag hello=\"world\">" +
                "<inner foo=\"bar\" poo=\"par\">hi</inner>" +
                "</tag>";

        assertEquals(expected, stringWriter.toString());
    }

}
