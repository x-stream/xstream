package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.io.StringReader;

public class StaxReaderTest extends AbstractXMLReaderTest {
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        StaxDriver driver = new StaxDriver();
        return driver.createReader(new StringReader(xml));
    }

    // inherits tests from superclass
}
