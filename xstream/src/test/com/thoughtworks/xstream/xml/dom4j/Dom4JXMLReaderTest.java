package com.thoughtworks.xstream.xml.dom4j;

import com.thoughtworks.xstream.xml.AbstractXMLReaderTest;
import com.thoughtworks.xstream.xml.XMLReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Dom4JXMLReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected XMLReader createReader(String xml) throws Exception {
        return new Dom4JXMLReader(DocumentHelper.parseText(xml));
    }

    public void testCanReadFromElementOfLargerDocument() throws DocumentException {
        Document document = DocumentHelper.parseText("" +
                "<big>" +
                "  <small>" +
                "    <tiny/>" +
                "  </small>" +
                "  <small-two>" +
                "  </small-two>" +
                "</big>");
        Element small = document.getRootElement().element("small");

        XMLReader xmlReader = new Dom4JXMLReader(small);
        assertEquals("small", xmlReader.name());
        xmlReader.child(0);
        assertEquals("tiny", xmlReader.name());
    }

}
