package com.thoughtworks.xstream.io.xml;

import nu.xom.Element;

public class XomWriterTest extends AbstractXMLWriterTest {

    private Element root;

    protected void setUp() throws Exception {
        super.setUp();
        root = new Element("my-root");
        writer = new XomWriter(root);
    }

    protected void assertXmlProducedIs(String expected) {
        assertEquals(1, root.getChildCount());
        String actual = root.getChild(0).toXML();
        actual = replaceAll(actual , " />", "/>");
        assertEquals(expected, actual);
    }

    // inherits tests from superclass
}
