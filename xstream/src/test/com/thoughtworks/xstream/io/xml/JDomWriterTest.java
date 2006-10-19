package com.thoughtworks.xstream.io.xml;

import org.jdom.Element;

public class JDomWriterTest extends AbstractDocumentWriterTest {

    protected void setUp() throws Exception {
        super.setUp();
        writer = new JDomWriter();
    }

    protected DocumentReader createDocumentReaderFor(final Object node) {
        return new JDomReader((Element)node);
    }

    // inherits tests from superclass
}
