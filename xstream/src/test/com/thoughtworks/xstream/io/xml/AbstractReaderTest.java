/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2011, 2012, 2013, 2015, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 21. March 2019 by Joerg Schaible, extracted from AbstractreaderTest
 */
package com.thoughtworks.xstream.io.xml;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;

import junit.framework.TestCase;


public abstract class AbstractReaderTest extends TestCase {

    protected abstract HierarchicalStreamReader createReader(String xml) throws Exception;

    public void testStartsAtRootTag() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<hello/>")) {
			assertEquals("hello", reader.getNodeName());
		}
    }

    public void testCanNavigateDownChildTagsByIndex() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<a><b><ooh/></b><b><aah/></b></a>")) {
			assertEquals(1, reader.getLevel());
			assertEquals("a", reader.getNodeName());
			
			assertTrue(reader.hasMoreChildren());
			
			reader.moveDown(); // /a/b
			
			assertEquals(2, reader.getLevel());
			assertEquals("b", reader.getNodeName());
			
			assertTrue(reader.hasMoreChildren());
			
			reader.moveDown(); // a/b/ooh
			assertEquals(3, reader.getLevel());
			assertEquals("ooh", reader.getNodeName());
			assertFalse(reader.hasMoreChildren());
			reader.moveUp(); // a/b
			
			assertEquals(2, reader.getLevel());
			assertFalse(reader.hasMoreChildren());
			
			reader.moveUp(); // /a
			
			assertEquals(1, reader.getLevel());
			assertTrue(reader.hasMoreChildren());
			
			reader.moveDown(); // /a/b[2]
			
			assertEquals(2, reader.getLevel());
			assertEquals("b", reader.getNodeName());
			
			assertTrue(reader.hasMoreChildren());
			
			reader.moveDown(); // a/b[2]/aah
			
			assertEquals(3, reader.getLevel());
			assertEquals("aah", reader.getNodeName());
			assertFalse(reader.hasMoreChildren());
			
			reader.moveUp(); // a/b[2]
			
			assertEquals(2, reader.getLevel());
			assertFalse(reader.hasMoreChildren());
			
			reader.moveUp(); // a
			assertEquals(1, reader.getLevel());
			
			assertFalse(reader.hasMoreChildren());
		}
    }

    public void testAttributesCanBeFetchedFromTags() throws Exception {
		try (HierarchicalStreamReader reader = createReader(""
			+ "<hello one=\"1\" two=\"2\">"
			+ "  <child three=\"3\"/>"
			+ "</hello>") // /hello
		) {
			assertEquals("1", reader.getAttribute("one"));
			assertEquals("2", reader.getAttribute("two"));
			assertNull(reader.getAttribute("three"));
			
			reader.moveDown(); // /hello/child
			assertNull(reader.getAttribute("one"));
			assertNull(reader.getAttribute("two"));
			assertEquals("3", reader.getAttribute("three"));
		}
    }

    public void testKeepsWhitespaceAroundText() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<root> hello world </root>")) {
			assertEquals(" hello world ", reader.getValue());
		}
    }

    public void testReturnsLastResultForHasMoreChildrenIfCalledRepeatedlyWithoutMovingNode() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<row><cells></cells></row>")) {
			assertEquals("row", reader.getNodeName());
			assertTrue(reader.hasMoreChildren()); // this is OK
			assertTrue(reader.hasMoreChildren()); // this fails
		}
    }

    public void testExposesAttributesKeysAndValuesByIndex() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<node hello='world' a='b' c='d'><empty/></node>")) {
			assertEquals(3, reader.getAttributeCount());
			
			assertEquals("hello", reader.getAttributeName(0));
			assertEquals("a", reader.getAttributeName(1));
			assertEquals("c", reader.getAttributeName(2));
			
			assertEquals("world", reader.getAttribute(0));
			assertEquals("b", reader.getAttribute(1));
			assertEquals("d", reader.getAttribute(2));
			
			reader.moveDown();
			assertEquals("empty", reader.getNodeName());
			assertEquals(0, reader.getAttributeCount());
		}
    }

    public void testExposesAttributesKeysAsIterator() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<node hello='world' a='b' c='d'><empty/></node>")) {
			final Set<String> expected = new HashSet<>();
			expected.add("hello");
			expected.add("a");
			expected.add("c");
			
			final Set<String> actual = new HashSet<>();
			Iterator<String> iterator;
			
			iterator = reader.getAttributeNames();
			while (iterator.hasNext()) {
				actual.add(iterator.next());
			}
			assertEquals(expected, actual);
			
			// again, to check iteration is repeatable
			iterator = reader.getAttributeNames();
			while (iterator.hasNext()) {
				actual.add(iterator.next());
			}
			assertEquals(expected, actual);
		}
    }

    public void testAllowsValueToBeReadWithoutDisturbingChildren() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<root><child></child><sibling>text2</sibling></root>") // at:
		// /root
		) {
			assertEquals("root", reader.getNodeName());
			assertEquals("", reader.getValue());
			assertTrue(reader.hasMoreChildren());
			
			reader.moveDown(); // at: /root/child
			assertEquals("child", reader.getNodeName());
			assertEquals(null, reader.getAttribute("something"));
			assertEquals("", reader.getValue());
			
			assertFalse(reader.hasMoreChildren()); // <--- This is an awkward one for pull parsers
			
			reader.moveUp(); // at: /root
			
			assertTrue(reader.hasMoreChildren());
			
			reader.moveDown(); // at: /root/sibling
			assertEquals("sibling", reader.getNodeName());
			assertEquals("text2", reader.getValue());
			assertFalse(reader.hasMoreChildren());
			reader.moveUp(); // at: /root
			
			assertFalse(reader.hasMoreChildren());
		}
    }

    public void testExposesTextValueOfCurrentElementButNotChildren() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<root>hello<child>FNARR</child></root>")) {
			assertEquals("hello", reader.getValue());
			reader.moveDown();
			assertEquals("FNARR", reader.getValue());
			reader.moveUp();
		}
    }

    public void testCanReadLineFeedInString() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<string>a\nb</string>")) {
			assertEquals("a\nb", reader.getValue());
		}
    }

    public void testCanReadEncodedAttribute() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<string __attr='value'/>")) {
			assertEquals("value", reader.getAttribute("_attr"));
		}
    }

    public void testCanReadAttributeWithEncodedWhitespace() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<string attr='  A\r\t\nB  C&#x9;&#xa;&#xd;  '/>")) {
			assertEquals("  A   B  C\t\n\r  ", reader.getAttribute("attr"));
		}
    }

    public void testCanSkipStructures() throws Exception {
		try (HierarchicalStreamReader reader = createReader(
			"<a><b1><c><string><![CDATA[skip]]></string></c></b1><b2><aah/></b2><b3>OK</b3></a>")) {
			reader.moveDown();
			reader.moveDown();
			assertEquals("c", reader.getNodeName());
			assertEquals(3, reader.getLevel());
			
			reader.moveUp();
			assertEquals(2, reader.getLevel());
			reader.moveUp();
			assertEquals(1, reader.getLevel());
			
			reader.moveDown();
			assertEquals("b2", reader.getNodeName());
			assertEquals(2, reader.getLevel());
			
			reader.moveUp();
			assertEquals(1, reader.getLevel());
			
			reader.moveDown();
			assertEquals("b3", reader.getNodeName());
			assertEquals(2, reader.getLevel());
			assertEquals("OK", reader.getValue());
			
			reader.moveUp();
			assertEquals(1, reader.getLevel());
		}
    }

    public void testNullCharacterInValue() throws Exception {
		try (HierarchicalStreamReader reader = createReader("<string>X&#x0;Y</string>")) {
			assertEquals("X\u0000Y", reader.getValue());
		}
    }
}
