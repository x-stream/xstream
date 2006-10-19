package com.thoughtworks.xstream.io.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DomWriterTest extends AbstractDocumentWriterTest {

    private Document document;

    protected void setUp() throws Exception {
        super.setUp();
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.newDocument();
        writer = new DomWriter(document);
    }

    protected DocumentReader createDocumentReaderFor(final Object node) {
        return new DomReader((Element)node);
    }

    // inherits tests from superclass
}
