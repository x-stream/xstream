package com.thoughtworks.xstream.io.xml;

import nu.xom.Element;

public class XomWriterTest extends AbstractDocumentWriterTest {

    protected void setUp() throws Exception {
        super.setUp();
        writer = new XomWriter();
    }

    protected DocumentReader createDocumentReaderFor(final Object node) {
        return new XomReader((Element)node);
    }

    // inherits tests from superclass
}
