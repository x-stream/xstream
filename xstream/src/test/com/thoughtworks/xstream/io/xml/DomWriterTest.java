package com.thoughtworks.xstream.io.xml;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DomWriterTest extends AbstractXMLWriterTest {

    private Document document;

    protected void setUp() throws Exception {
        super.setUp();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.newDocument();
        writer = new DomWriter(document);
    }

    protected void assertXmlProducedIs(String expected) {
        // conver w3c doc to dom4j doc
        org.dom4j.io.DOMReader domReader = new org.dom4j.io.DOMReader();
        org.dom4j.Document dom4Jdoc = domReader.read(document);
        String actual = dom4Jdoc.getRootElement().asXML();
        assertEquals(expected, actual);
    }

    // inherits tests from superclass

    public void testEscapesWhitespaceCharacters() {
        // TODO: Support whitespaces.
        // This method overrides a test in the superclass to prevent it from being run.
    }
}
