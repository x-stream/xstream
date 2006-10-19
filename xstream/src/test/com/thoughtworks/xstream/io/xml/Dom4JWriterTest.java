package com.thoughtworks.xstream.io.xml;

import org.dom4j.Element;

public class Dom4JWriterTest extends AbstractDocumentWriterTest {

    protected void setUp() throws Exception {
        super.setUp();
        writer = new Dom4JWriter();
    }

    protected DocumentReader createDocumentReaderFor(final Object node) {
        return new Dom4JReader((Element)node);
    }

    // inherits tests from superclass
}
