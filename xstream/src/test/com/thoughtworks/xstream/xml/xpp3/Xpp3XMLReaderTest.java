package com.thoughtworks.xstream.xml.xpp3;

import com.thoughtworks.xstream.xml.AbstractXMLReaderTest;
import com.thoughtworks.xstream.xml.XMLReader;

import java.io.StringReader;

public class Xpp3XMLReaderTest extends AbstractXMLReaderTest {
    protected XMLReader createReader(String xml) throws Exception {
        return new Xpp3DomXMLReaderDriver().createReader(xml);
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

        XMLReader xmlReader = new Xpp3DomXMLReader(small);

        assertEquals("small", xmlReader.name());

        xmlReader.nextChild();

        assertEquals("tiny", xmlReader.name());
    }
}
