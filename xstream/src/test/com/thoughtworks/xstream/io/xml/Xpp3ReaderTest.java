package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.xpp3.Xpp3Dom;
import com.thoughtworks.xstream.io.xml.xpp3.Xpp3DomBuilder;

import java.io.StringReader;

public class Xpp3ReaderTest extends AbstractXMLReaderTest {
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return new Xpp3Driver().createReader(xml);
    }

    public void testCanReadFromElementOfLargerDocument()
            throws Exception {
        String xml =
                "<big>" +
                "  <small>" +
                "    <tiny/>" +
                "  </small>" +
                "  <small-two>" +
                "  </small-two>" +
                "</big>";

        Xpp3Dom document = Xpp3DomBuilder.build(new StringReader(xml));

        Xpp3Dom small = document.getChild("small");

        HierarchicalStreamReader xmlReader = new Xpp3Reader(small);

        assertEquals("small", xmlReader.getNodeName());

        xmlReader.getNextChildNode();

        assertEquals("tiny", xmlReader.getNodeName());
    }
}
