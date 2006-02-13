package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.StringReader;

public class JDomReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        Document document = new SAXBuilder().build(new StringReader(xml));
        return new JDomReader(document);
    }

    // inherits tests from superclass

}
