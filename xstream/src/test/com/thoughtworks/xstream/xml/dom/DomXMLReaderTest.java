package com.thoughtworks.xstream.xml.dom;

import com.thoughtworks.xstream.xml.AbstractXMLReaderTest;
import com.thoughtworks.xstream.xml.XMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

public class DomXMLReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected XMLReader createReader(String xml) throws Exception {
        return new DomXMLReader(buildDocument(xml));
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

        XMLReader xmlReader = new DomXMLReader(small);
        assertEquals("small", xmlReader.name());
        xmlReader.nextChild();
        assertEquals("tiny", xmlReader.name());
    }

}
