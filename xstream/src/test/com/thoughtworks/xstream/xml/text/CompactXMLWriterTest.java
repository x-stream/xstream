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
        xmlWriter.pushElement("hello");
        xmlWriter.pushElement("world");

        xmlWriter.pushElement("one");
        xmlWriter.text("potato");
        xmlWriter.pop();

        xmlWriter.pushElement("two");
        xmlWriter.text("potatae");
        xmlWriter.pop();

        xmlWriter.pop();
        xmlWriter.pop();

        String expected = "<hello><world><one>potato</one><two>potatae</two></world></hello>";

        assertEquals(expected, stringWriter.toString());
    }

    public void testEncodesFunnyXmlChars() {
        xmlWriter.pushElement("tag");
        xmlWriter.text("hello & this isn't \"really\" <good>");
        xmlWriter.pop();

        String expected = "<tag>hello &amp; this isn't \"really\" &lt;good&gt;</tag>";

        assertEquals(expected, stringWriter.toString());
    }

    public void testAttributesCanBeWritten() {
        xmlWriter.pushElement("tag");
        xmlWriter.attribute("hello", "world");
        xmlWriter.pushElement("inner");
        xmlWriter.attribute("foo", "bar");
        xmlWriter.attribute("poo", "par");
        xmlWriter.text("hi");
        xmlWriter.pop();
        xmlWriter.pop();

        String expected = "" +
                "<tag hello=\"world\">" +
                "<inner foo=\"bar\" poo=\"par\">hi</inner>" +
                "</tag>";

        assertEquals(expected, stringWriter.toString());
    }

}
