package com.thoughtworks.xstream.xml;

import junit.framework.TestCase;

public abstract class AbstractXMLReaderTest extends TestCase {

    // factory method
    protected abstract XMLReader createReader(String xml) throws Exception;

    public void testStartsAtRootTag() throws Exception {
        XMLReader xmlReader = createReader("<hello/>");
        assertEquals("hello", xmlReader.name());
    }

    public void testCanNavigateDownChildTagsByIndex() throws Exception {
        XMLReader xmlReader = createReader("<a><b><ooh/></b><b><aah/></b></a>");

        assertEquals("a", xmlReader.name());

        assertTrue(xmlReader.nextChild());
        {
            assertEquals("b", xmlReader.name());

            assertTrue(xmlReader.nextChild());
            {
                assertEquals("ooh", xmlReader.name());
                assertFalse(xmlReader.nextChild());
            }
            xmlReader.pop();

            assertFalse(xmlReader.nextChild());


        }
        xmlReader.pop();

        assertTrue(xmlReader.nextChild());
        {
            assertEquals("b", xmlReader.name());

            assertTrue(xmlReader.nextChild());
            {
                assertEquals("aah", xmlReader.name());
                assertFalse(xmlReader.nextChild());
            }
            xmlReader.pop();

            assertFalse(xmlReader.nextChild());

        }
        xmlReader.pop();

        assertFalse(xmlReader.nextChild());
    }

    public void testChildTagsCanBeMixedWithOtherNodes() throws Exception {
        XMLReader xmlReader = createReader("<!-- xx --><a> <hello/> <!-- x --> text <world/></a>");

        assertTrue(xmlReader.nextChild());
        assertEquals("hello", xmlReader.name());
        xmlReader.pop();

        assertTrue(xmlReader.nextChild());
        assertEquals("world", xmlReader.name());
        xmlReader.pop();

        assertFalse(xmlReader.nextChild());
    }

    public void testAttributesCanBeFetchedFromTags() throws Exception {
        XMLReader xmlReader = createReader("" +
                "<hello one=\"1\" two=\"2\">" +
                "  <child three=\"3\"/>" +
                "</hello>");

        assertEquals("1", xmlReader.attribute("one"));
        assertEquals("2", xmlReader.attribute("two"));
        assertNull(xmlReader.attribute("three"));

        xmlReader.nextChild();
        assertNull(xmlReader.attribute("one"));
        assertNull(xmlReader.attribute("two"));
        assertEquals("3", xmlReader.attribute("three"));

    }

    public void testTextCanBeExtractedFromTag() throws Exception {
        XMLReader xmlReader = createReader("<root><a>some<!-- ignore me --> text!</a><b>more</b></root>");

        xmlReader.nextChild();
        assertEquals("some text!", xmlReader.text());
        xmlReader.pop();

        xmlReader.nextChild();
        assertEquals("more", xmlReader.text());
        xmlReader.pop();
    }

    public void testDoesNotIgnoreWhitespaceAroundText() throws Exception {
        XMLReader xmlReader = createReader("<root> hello world </root>");

        assertEquals(" hello world ", xmlReader.text());
    }

}
