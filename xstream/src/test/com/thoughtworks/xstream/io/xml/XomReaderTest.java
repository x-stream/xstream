package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import nu.xom.Builder;
import nu.xom.Document;

import java.io.StringReader;

public class XomReaderTest extends AbstractXMLReaderTest {

    // factory method
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        Document document = new Builder().build(new StringReader(xml));
        return new XomReader(document);
    }

//    public void testCanReadFromElementOfLargerDocument() throws DocumentException {
//        Document document = DocumentHelper.parseText("" +
//                "<big>" +
//                "  <small>" +
//                "    <tiny/>" +
//                "  </small>" +
//                "  <small-two>" +
//                "  </small-two>" +
//                "</big>");
//        Element small = document.getRootElement().element("small");
//
//        HierarchicalStreamReader xmlReader = new Dom4JReader(small);
//        assertEquals("small", xmlReader.getNodeName());
//        xmlReader.moveDown();
//        assertEquals("tiny", xmlReader.getNodeName());
//    }

}
