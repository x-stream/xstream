package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import junit.framework.TestCase;

public abstract class AbstractXMLReaderTest extends TestCase {

    // factory method
    protected abstract HierarchicalStreamReader createReader(String xml) throws Exception;

    public void testStartsAtRootTag() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<hello/>");
        assertEquals("hello", xmlReader.getNodeName());
    }

    public void testCanNavigateDownChildTagsByIndex() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<a><b><ooh/></b><b><aah/></b></a>");

        assertEquals("a", xmlReader.getNodeName());

        assertTrue(xmlReader.getNextChildNode());
        {
            assertEquals("b", xmlReader.getNodeName());

            assertTrue(xmlReader.getNextChildNode());
            {
                assertEquals("ooh", xmlReader.getNodeName());
                assertFalse(xmlReader.getNextChildNode());
            }
            xmlReader.getParentNode();

            assertFalse(xmlReader.getNextChildNode());


        }
        xmlReader.getParentNode();

        assertTrue(xmlReader.getNextChildNode());
        {
            assertEquals("b", xmlReader.getNodeName());

            assertTrue(xmlReader.getNextChildNode());
            {
                assertEquals("aah", xmlReader.getNodeName());
                assertFalse(xmlReader.getNextChildNode());
            }
            xmlReader.getParentNode();

            assertFalse(xmlReader.getNextChildNode());

        }
        xmlReader.getParentNode();

        assertFalse(xmlReader.getNextChildNode());
    }

    public void testChildTagsCanBeMixedWithOtherNodes() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<!-- xx --><a> <hello/> <!-- x --> getValue <world/></a>");

        assertTrue(xmlReader.getNextChildNode());
        assertEquals("hello", xmlReader.getNodeName());
        xmlReader.getParentNode();

        assertTrue(xmlReader.getNextChildNode());
        assertEquals("world", xmlReader.getNodeName());
        xmlReader.getParentNode();

        assertFalse(xmlReader.getNextChildNode());
    }

    public void testAttributesCanBeFetchedFromTags() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("" +
                "<hello one=\"1\" two=\"2\">" +
                "  <child three=\"3\"/>" +
                "</hello>");

        assertEquals("1", xmlReader.getAttribute("one"));
        assertEquals("2", xmlReader.getAttribute("two"));
        assertNull(xmlReader.getAttribute("three"));

        xmlReader.getNextChildNode();
        assertNull(xmlReader.getAttribute("one"));
        assertNull(xmlReader.getAttribute("two"));
        assertEquals("3", xmlReader.getAttribute("three"));

    }

    public void testTextCanBeExtractedFromTag() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<root><a>some<!-- ignore me --> getValue!</a><b>more</b></root>");

        xmlReader.getNextChildNode();
        assertEquals("some getValue!", xmlReader.getValue());
        xmlReader.getParentNode();

        xmlReader.getNextChildNode();
        assertEquals("more", xmlReader.getValue());
        xmlReader.getParentNode();
    }

    public void testDoesNotIgnoreWhitespaceAroundText() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<root> hello world </root>");

        assertEquals(" hello world ", xmlReader.getValue());
    }

    public void testReturnsEmptyStringForEmptyTags() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<root></root>");

        String text = xmlReader.getValue();
        assertNotNull(text);
        assertEquals("", text);
    }

}
