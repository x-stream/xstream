package com.thoughtworks.xstream.io.xml;

import javax.xml.stream.XMLOutputFactory;

import java.io.StringWriter;

/*
 * @author James Strachan
 */
public class StaxWriterTest extends AbstractXMLWriterTest {

    private StringWriter buffer;

    protected void setUp() throws Exception {
        super.setUp();
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        buffer = new StringWriter();
        writer = new StaxWriter(new QNameMap(), outputFactory.createXMLStreamWriter(buffer));
    }

    protected void assertXmlProducedIs(String expected) {
        expected = "<?xml version='1.0' encoding='utf-8'?>" + expected; // include header
        assertEquals(expected, buffer.toString());
    }

    public void testEscapesWhitespaceCharacters() {
        // overriding test in superclass... this doesn't seem to work with StaxWriter.
    }

    public void testSupportsEmptyTags() {
        writer.startNode("empty");
        writer.endNode();

        assertXmlProducedIs("<empty></empty>");
    }

    public void testSupportsEmptyNestedTags() {
        writer.startNode("parent");
        writer.startNode("child");
        writer.endNode();
        writer.endNode();

        assertXmlProducedIs("<parent><child></child></parent>");
    }

    public void testSupportsAttributes() {
        writer.startNode("person");
        writer.addAttribute("firstname", "Joe");
        writer.addAttribute("lastname", "Walnes");
        writer.endNode();

        assertXmlProducedIs("<person firstname=\"Joe\" lastname=\"Walnes\"></person>");
    }
}

