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
        assertEquals(2, xmlReader.childCount());

        xmlReader.child(0);
        {
            assertEquals("b", xmlReader.name());
            assertEquals(1, xmlReader.childCount());

            xmlReader.child(0);
            {
                assertEquals("ooh", xmlReader.name());
                assertEquals(0, xmlReader.childCount());
            }
            xmlReader.pop();

        }
        xmlReader.pop();

        xmlReader.child(1);
        {
            assertEquals("b", xmlReader.name());
            assertEquals(1, xmlReader.childCount());

            xmlReader.child(0);
            {
                assertEquals("aah", xmlReader.name());
                assertEquals(0, xmlReader.childCount());
            }
            xmlReader.pop();

        }
        xmlReader.pop();
    }

    public void testChildTagsCanBeMixedWithOtherNodes() throws Exception {
        XMLReader xmlReader = createReader("<!-- xx --><a> <hello/> <!-- x --> text <world/></a>");

        assertEquals(2, xmlReader.childCount());

        xmlReader.child(0);
        assertEquals("hello", xmlReader.name());
        xmlReader.pop();

        xmlReader.child(1);
        assertEquals("world", xmlReader.name());
        xmlReader.pop();
    }

    public void testAttributesCanBeFetchedFromTags() throws Exception {
        XMLReader xmlReader = createReader("" +
                "<hello one=\"1\" two=\"2\">" +
                "  <child three=\"3\"/>" +
                "</hello>");

        assertEquals("1", xmlReader.attribute("one"));
        assertEquals("2", xmlReader.attribute("two"));
        assertNull(xmlReader.attribute("three"));

        xmlReader.child(0);
        assertNull(xmlReader.attribute("one"));
        assertNull(xmlReader.attribute("two"));
        assertEquals("3", xmlReader.attribute("three"));

    }

    public void testTextCanBeExtractedFromTag() throws Exception {
        XMLReader xmlReader = createReader("<root><a>some<!-- ignore me --> text!</a><b>more</b></root>");

        xmlReader.child(0);
        assertEquals("some text!", xmlReader.text());
        xmlReader.pop();

        xmlReader.child(1);
        assertEquals("more", xmlReader.text());
        xmlReader.pop();
    }

}
