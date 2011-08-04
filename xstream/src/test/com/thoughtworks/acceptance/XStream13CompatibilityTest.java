/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. August 2011 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.util.TreeMap;
import java.util.TreeSet;


/**
 * Test XStream 1.3 compatibility.
 * 
 * @author J&ouml;rg Schaible
 */
public class XStream13CompatibilityTest extends AbstractAcceptanceTest {

    public void testCanReadOldTreeSet() {
        final String in = ""
            + "<tree-set>\n"
            + "  <no-comparator/>\n"
            + "  <string>one</string>\n"
            + "  <string>two</string>\n"
            + "</tree-set>";
        TreeSet expected = new TreeSet();
        expected.add("two");
        expected.add("one");
        assertEquals(expected, xstream.fromXML(in));
    }

    public void testCanReadOldTreeMap() {
        final String in = ""
            + "<tree-map>\n"
            + "  <no-comparator/>\n"
            + "  <entry>\n"
            + "    <string>one</string>\n"
            + "    <int>1</int>\n"
            + "  </entry>\n"
            + "  <entry>\n"
            + "    <string>two</string>\n"
            + "    <int>2</int>\n"
            + "  </entry>\n"
            + "</tree-map>";
        TreeMap expected = new TreeMap();
        expected.put("two", new Integer(2));
        expected.put("one", new Integer(1));
        assertEquals(expected, xstream.fromXML(in));
    }
}
