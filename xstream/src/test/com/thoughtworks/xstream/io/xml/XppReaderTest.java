package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.io.StringReader;

public class XppReaderTest extends AbstractXMLReaderTest {
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return new XppReader(new StringReader(xml));
    }

    // inherits tests from superclass
}
