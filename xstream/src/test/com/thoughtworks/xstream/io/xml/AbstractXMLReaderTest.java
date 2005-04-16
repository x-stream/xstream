package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.io.StringReader;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.mxp1.MXParser;

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

        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // /a/b

        assertEquals("b", xmlReader.getNodeName());

        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // a/b/ooh
        assertEquals("ooh", xmlReader.getNodeName());
        assertFalse(xmlReader.hasMoreChildren());
        xmlReader.moveUp(); // a/b

        assertFalse(xmlReader.hasMoreChildren());

        xmlReader.moveUp(); // /a

        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // /a/b[2]

        assertEquals("b", xmlReader.getNodeName());

        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // a/b[2]/aah

        assertEquals("aah", xmlReader.getNodeName());
        assertFalse(xmlReader.hasMoreChildren());

        xmlReader.moveUp(); // a/b[2]

        assertFalse(xmlReader.hasMoreChildren());

        xmlReader.moveUp(); // a

        assertFalse(xmlReader.hasMoreChildren());
    }

    public void testChildTagsCanBeMixedWithOtherNodes() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<!-- xx --><a> <hello/> <!-- x --> getValue <world/></a>");

        assertTrue(xmlReader.hasMoreChildren());
        xmlReader.moveDown();
        assertEquals("hello", xmlReader.getNodeName());
        xmlReader.moveUp();

        assertTrue(xmlReader.hasMoreChildren());
        xmlReader.moveDown();
        assertEquals("world", xmlReader.getNodeName());
        xmlReader.moveUp();

        assertFalse(xmlReader.hasMoreChildren());
    }

    public void testAttributesCanBeFetchedFromTags() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("" +
                "<hello one=\"1\" two=\"2\">" +
                "  <child three=\"3\"/>" +
                "</hello>"); // /hello

        assertEquals("1", xmlReader.getAttribute("one"));
        assertEquals("2", xmlReader.getAttribute("two"));
        assertNull(xmlReader.getAttribute("three"));

        xmlReader.moveDown(); // /hello/child
        assertNull(xmlReader.getAttribute("one"));
        assertNull(xmlReader.getAttribute("two"));
        assertEquals("3", xmlReader.getAttribute("three"));

    }

    public void testTextCanBeExtractedFromTag() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<root><a>some<!-- ignore me --> getValue!</a><b>more</b></root>");

        xmlReader.moveDown();
        assertEquals("some getValue!", xmlReader.getValue());
        xmlReader.moveUp();

        xmlReader.moveDown();
        assertEquals("more", xmlReader.getValue());
        xmlReader.moveUp();
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

    public void testReturnsLastResultForHasMoreChildrenIfCalledRepeatedlyWithoutMovingNode() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<row><cells></cells></row>");

        assertEquals("row", xmlReader.getNodeName());
        assertTrue(xmlReader.hasMoreChildren()); // this is OK
        assertTrue(xmlReader.hasMoreChildren()); // this fails
    }

    public void testExposesAttributesKeysAndValuesByIndex() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<node hello='world' a='b' c='d'><empty/></node>");

        assertEquals(3, xmlReader.getAttributeCount());

        assertEquals("hello", xmlReader.getAttributeName(0));
        assertEquals("a", xmlReader.getAttributeName(1));
        assertEquals("c", xmlReader.getAttributeName(2));

        assertEquals("world", xmlReader.getAttribute(0));
        assertEquals("b", xmlReader.getAttribute(1));
        assertEquals("d", xmlReader.getAttribute(2));

        xmlReader.moveDown();
        assertEquals("empty", xmlReader.getNodeName());
        assertEquals(0, xmlReader.getAttributeCount());
    }

    public void testExposesAttributesKeysAsIterator() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<node hello='world' a='b' c='d'><empty/></node>");

        Set expected = new HashSet();
        expected.add("hello");
        expected.add("a");
        expected.add("c");

        Set actual = new HashSet();
        Iterator iterator;

        iterator = xmlReader.getAttributeNames();
        while(iterator.hasNext()) {
            actual.add(iterator.next());
        }
        assertEquals(expected, actual);

        // again, to check iteration is repeatable 
        iterator = xmlReader.getAttributeNames();
        while(iterator.hasNext()) {
            actual.add(iterator.next());
        }
        assertEquals(expected, actual);
    }

    public void test() throws Exception {
        HierarchicalStreamReader xmlReader
                = createReader("<root><child></child><sibling>text2</sibling></root>"); // at: /root

        assertEquals("root", xmlReader.getNodeName());
        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // at: /root/child
        assertEquals("child", xmlReader.getNodeName());
        assertEquals(null, xmlReader.getAttribute("something"));
        assertEquals("", xmlReader.getValue());

        assertFalse(xmlReader.hasMoreChildren()); // <--- This is an awkward one for pull parsers

        xmlReader.moveUp(); // at: /root

        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // at: /root/sibling
        assertEquals("sibling", xmlReader.getNodeName());
        assertEquals("text2", xmlReader.getValue());
        assertFalse(xmlReader.hasMoreChildren());
        xmlReader.moveUp(); // at: /root

        assertFalse(xmlReader.hasMoreChildren());
    }

    public void test3() throws XmlPullParserException, IOException {
        XmlPullParser parser = new MXParser();
        parser.setInput(new StringReader("<root><child>x</child><sibling>text2</sibling></root>"));
        loop: while(true) {
            int next = parser.next();
            System.out.println(XmlPullParser.TYPES[next] + " : " + parser.getName());
            switch(next) {
                case XmlPullParser.END_DOCUMENT:
                    break loop;
            }
        }
    }
}
