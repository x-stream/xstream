package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import java.io.StringReader;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;

public class JDomReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        Document document = new SAXBuilder().build(new StringReader(xml));
        return new JDomReader(document);
    }

    // inherits tests from superclass

}
