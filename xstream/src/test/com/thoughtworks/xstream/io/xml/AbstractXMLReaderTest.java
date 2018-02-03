/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2011, 2012, 2013, 2015, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractXMLReaderTest extends TestCase {

    // factory method
    protected abstract HierarchicalStreamReader createReader(String xml) throws Exception;

    public void testStartsAtRootTag() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<hello/>");
        assertEquals("hello", xmlReader.getNodeName());
        xmlReader.close();
    }

    public void testCanNavigateDownChildTagsByIndex() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<a><b><ooh/></b><b><aah/></b></a>");

        assertEquals(1, xmlReader.getLevel());
        assertEquals("a", xmlReader.getNodeName());

        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // /a/b

        assertEquals(2, xmlReader.getLevel());
        assertEquals("b", xmlReader.getNodeName());

        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // a/b/ooh
        assertEquals(3, xmlReader.getLevel());
        assertEquals("ooh", xmlReader.getNodeName());
        assertFalse(xmlReader.hasMoreChildren());
        xmlReader.moveUp(); // a/b

        assertEquals(2, xmlReader.getLevel());
        assertFalse(xmlReader.hasMoreChildren());

        xmlReader.moveUp(); // /a

        assertEquals(1, xmlReader.getLevel());
        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // /a/b[2]

        assertEquals(2, xmlReader.getLevel());
        assertEquals("b", xmlReader.getNodeName());

        assertTrue(xmlReader.hasMoreChildren());

        xmlReader.moveDown(); // a/b[2]/aah

        assertEquals(3, xmlReader.getLevel());
        assertEquals("aah", xmlReader.getNodeName());
        assertFalse(xmlReader.hasMoreChildren());

        xmlReader.moveUp(); // a/b[2]

        assertEquals(2, xmlReader.getLevel());
        assertFalse(xmlReader.hasMoreChildren());

        xmlReader.moveUp(); // a
        assertEquals(1, xmlReader.getLevel());

        assertFalse(xmlReader.hasMoreChildren());

        xmlReader.close();
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
        xmlReader.close();
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

        xmlReader.close();
    }

    public void testTextCanBeExtractedFromTag() throws Exception {
        HierarchicalStreamReader xmlReader = createReader(
        	"<root><a>some<!-- ignore me --> getValue!</a><b><![CDATA[more&&more;]]></b></root>");

        xmlReader.moveDown();
        assertEquals("some getValue!", xmlReader.getValue());
        xmlReader.moveUp();

        xmlReader.moveDown();
        assertEquals("more&&more;", xmlReader.getValue());
        xmlReader.moveUp();
        xmlReader.close();
    }

    public void testDoesNotIgnoreWhitespaceAroundText() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<root> hello world </root>");

        assertEquals(" hello world ", xmlReader.getValue());
        xmlReader.close();
    }

    public void testReturnsEmptyStringForEmptyTags() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<root></root>");

        String text = xmlReader.getValue();
        assertNotNull(text);
        assertEquals("", text);
        xmlReader.close();
    }

    public void testReturnsLastResultForHasMoreChildrenIfCalledRepeatedlyWithoutMovingNode() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<row><cells></cells></row>");

        assertEquals("row", xmlReader.getNodeName());
        assertTrue(xmlReader.hasMoreChildren()); // this is OK
        assertTrue(xmlReader.hasMoreChildren()); // this fails
        xmlReader.close();
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
        xmlReader.close();
    }

    public void testExposesAttributesKeysAsIterator() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<node hello='world' a='b' c='d'><empty/></node>");

        Set<String> expected = new HashSet<>();
        expected.add("hello");
        expected.add("a");
        expected.add("c");

        Set<String> actual = new HashSet<>();
        Iterator<String> iterator;

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
        xmlReader.close();
    }

    public void testAllowsValueToBeReadWithoutDisturbingChildren() throws Exception {
        HierarchicalStreamReader xmlReader
                = createReader("<root><child></child><sibling>text2</sibling></root>"); // at: /root

        assertEquals("root", xmlReader.getNodeName());
        assertEquals("", xmlReader.getValue());
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
        xmlReader.close();
    }

    public void testExposesTextValueOfCurrentElementButNotChildren() throws Exception {
        HierarchicalStreamReader xmlReader
                = createReader("<root>hello<child>FNARR</child></root>");

        assertEquals("hello", xmlReader.getValue());
        xmlReader.moveDown();
        assertEquals("FNARR", xmlReader.getValue());
        xmlReader.moveUp();
        xmlReader.close();
    }

    public void testCanReadLineFeedInString() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<string>a\nb</string>");
        assertEquals("a\nb", xmlReader.getValue());
        xmlReader.close();
    }

    public void testCanReadEncodedAttribute() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<string __attr='value'/>");
        assertEquals("value", xmlReader.getAttribute("_attr"));
        xmlReader.close();
    }

    public void testCanReadAttributeWithEncodedWhitespace() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<string attr='  A\r\t\nB  C&#x9;&#xa;&#xd;  '/>");
        assertEquals("  A   B  C\t\n\r  ", xmlReader.getAttribute("attr"));
        xmlReader.close();
    }

    public void testCanReadCDATAWithEmbeddedTags() throws Exception {
        String content = "<tag>the content</tag>";
        HierarchicalStreamReader xmlReader = createReader("<string><![CDATA[" + content + "]]></string>");
        assertEquals(content, xmlReader.getValue());
        xmlReader.close();
    }
    
    public void testIsXXEVulnerableWithExternalGeneralEntity() throws Exception {
        HierarchicalStreamReader xmlReader = createReader(""
                + "<?xml version=\"1.0\"?>\n"
                +"<!DOCTYPE root [\n"
                +"<!ELEMENT string (#PCDATA)>\n"
                +"<!ENTITY content SYSTEM \"file:src/test/$Package.java\">\n"
//                +"<!ENTITY content SYSTEM \"file:pom.xml\">\n"
//                +"<!ENTITY content SYSTEM \"file:/etc/passwd\">\n"
                +"]><string>&content;</string>");
        assertEquals("", xmlReader.getValue());
        xmlReader.close();
    }
    
    public void testIsXXEVulnerableWithExternalParameterEntity() throws Exception {
        HierarchicalStreamReader xmlReader = createReader(""
            + "<?xml version=\"1.0\"?>\n"
                +"<!DOCTYPE root [\n"
                +"<!ELEMENT string (#PCDATA)>\n"
                +"<!ENTITY % content SYSTEM \"file:src/test/$Package.java\">\n"
//                +"<!ENTITY % content SYSTEM \"file:pom.xml\">\n"
//                +"<!ENTITY % content SYSTEM \"file:/etc/passwd\">\n"
                +"%content;\n"
                +"]><string>test</string>");
        assertEquals("test", xmlReader.getValue());
        xmlReader.close();
    }
    
    public void testCanSkipStructures() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<a><b1><c><string><![CDATA[skip]]></string></c></b1><b2><aah/></b2><b3>OK</b3></a>");
        xmlReader.moveDown();
        xmlReader.moveDown();
        assertEquals("c", xmlReader.getNodeName());
        assertEquals(3, xmlReader.getLevel());

        xmlReader.moveUp();
        assertEquals(2, xmlReader.getLevel());
        xmlReader.moveUp();
        assertEquals(1, xmlReader.getLevel());

        xmlReader.moveDown();
        assertEquals("b2", xmlReader.getNodeName());
        assertEquals(2, xmlReader.getLevel());

        xmlReader.moveUp();
        assertEquals(1, xmlReader.getLevel());

        xmlReader.moveDown();
        assertEquals("b3", xmlReader.getNodeName());
        assertEquals(2, xmlReader.getLevel());
        assertEquals("OK", xmlReader.getValue());

        xmlReader.moveUp();
        assertEquals(1, xmlReader.getLevel());

        xmlReader.close();
    }

    // TODO: See XSTR-473
    public void todoTestCanReadNullValueInString() throws Exception {
        HierarchicalStreamReader xmlReader = createReader("<string>&#x0;</string>");
        assertEquals("\u0000", xmlReader.getValue());
        xmlReader.close();
    }
}
