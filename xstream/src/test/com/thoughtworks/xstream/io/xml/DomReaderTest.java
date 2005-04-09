package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public class DomReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return new DomReader(buildDocument(xml));
    }

    private Document buildDocument(String xml) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        Document document = documentBuilder.parse(inputStream);
        return document;
    }

    public void testCanReadFromElementOfLargerDocument() throws Exception {
        Document document = buildDocument("" +
                "<big>" +
                "  <small>" +
                "    <tiny/>" +
                "  </small>" +
                "  <small-two>" +           
                "  </small-two>" +
                "</big>");
        Element small = (Element) document.getDocumentElement().getElementsByTagName("small").item(0);

        HierarchicalStreamReader xmlReader = new DomReader(small);
        assertEquals("small", xmlReader.getNodeName());
        xmlReader.moveDown();
        assertEquals("tiny", xmlReader.getNodeName());
    }

    public void testExposesAttributesKeysAndValuesByIndex() throws Exception {

        // overrides test in superclass, because DOM does not retain order of attributes.

        HierarchicalStreamReader xmlReader = createReader("<node hello='world' a='b' c='d'><empty/></node>");

        assertEquals(3, xmlReader.getAttributeCount());

        assertEquals("a", xmlReader.getAttributeName(0));
        assertEquals("c", xmlReader.getAttributeName(1));
        assertEquals("hello", xmlReader.getAttributeName(2));

        assertEquals("b", xmlReader.getAttribute(0));
        assertEquals("d", xmlReader.getAttribute(1));
        assertEquals("world", xmlReader.getAttribute(2));

        xmlReader.moveDown();
        assertEquals("empty", xmlReader.getNodeName());
        assertEquals(0, xmlReader.getAttributeCount());
    }

}
