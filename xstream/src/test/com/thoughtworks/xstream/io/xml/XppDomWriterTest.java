package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;

/**
 * @author J&ouml;rg Schaible
 */
public class XppDomWriterTest extends AbstractDocumentWriterTest {

    protected void setUp() throws Exception {
        super.setUp();
        writer = new XppDomWriter();
    }

    protected DocumentReader createDocumentReaderFor(Object node) {
        return new XppDomReader((Xpp3Dom)node);
    }

    // inherits tests from superclass
}
