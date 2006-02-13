package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3DomBuilder;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class XppDomReaderTest extends AbstractXMLReaderTest {
    protected HierarchicalStreamReader createReader(String xml) throws Exception {
        return new XppDomDriver().createReader(new StringReader(xml));
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

        HierarchicalStreamReader xmlReader = new XppDomReader(small);

        assertEquals("small", xmlReader.getNodeName());

        xmlReader.moveDown();

        assertEquals("tiny", xmlReader.getNodeName());
    }

    public void testExposesAttributesKeysAndValuesByIndex() throws Exception {

        // overrides test in superclass, because XppDom does not retain order of actualAttributes.

        HierarchicalStreamReader xmlReader = createReader("<node hello='world' a='b' c='d'><empty/></node>");

        assertEquals(3, xmlReader.getAttributeCount());

        Map expectedAttributes = new HashMap();
        expectedAttributes.put("hello", "world");
        expectedAttributes.put("a", "b");
        expectedAttributes.put("c", "d");

        Map actualAttributes = new HashMap();
        for (int i = 0; i < xmlReader.getAttributeCount(); i++) {
            String name = xmlReader.getAttributeName(i);
            String value = xmlReader.getAttribute(i);
            actualAttributes.put(name, value);
        }

        assertEquals(expectedAttributes, actualAttributes);

        xmlReader.moveDown();
        assertEquals("empty", xmlReader.getNodeName());
        assertEquals(0, xmlReader.getAttributeCount());
    }

}
