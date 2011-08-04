/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2010, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 08. May 2005 by Joe Walnes
 */
package com.thoughtworks.acceptance;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeMapAndTreeSetTest extends AbstractAcceptanceTest {
    
    public static class MyComparator implements Comparator {
        private String something = "stuff";

        public int compare(Object o1, Object o2) {
            return ((String) o1).compareTo((String) o2);
        }
    }
    
    public static class UnusedComparator implements Comparator {

        private final static Comparator THROWING_COMPARATOR = new Comparator() {

            public int compare(Object o1, Object o2) {
                throw new UnsupportedOperationException();
            }
            
        };

        public int compare(Object o1, Object o2) {
            return ((String) o1).compareTo((String) o2);
        }
        
        private Object readResolve() {
            return THROWING_COMPARATOR;
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("my-comparator", MyComparator.class);
        xstream.alias("unused-comparator", UnusedComparator.class);
    }

    public void testTreeMapWithComparator() {
        TreeMap map = new TreeMap(new MyComparator());
        map.put("benny", "hill");
        map.put("joe", "walnes");

        String expected = "" +
                "<tree-map>\n" +
                "  <comparator class=\"my-comparator\">\n" +
                "    <something>stuff</something>\n" +
                "  </comparator>\n" +
                "  <entry>\n" +
                "    <string>benny</string>\n" +
                "    <string>hill</string>\n" +
                "  </entry>\n" +
                "  <entry>\n" +
                "    <string>joe</string>\n" +
                "    <string>walnes</string>\n" +
                "  </entry>\n" +
                "</tree-map>";

        TreeMap result = (TreeMap) assertBothWays(map, expected);
        assertEquals(MyComparator.class, result.comparator().getClass());
    }

    public void testTreeMapWithoutComparator() {
        TreeMap map = new TreeMap();
        map.put("benny", "hill");
        map.put("joe", "walnes");

        String expected = "" +
                "<tree-map>\n" +
                "  <entry>\n" +
                "    <string>benny</string>\n" +
                "    <string>hill</string>\n" +
                "  </entry>\n" +
                "  <entry>\n" +
                "    <string>joe</string>\n" +
                "    <string>walnes</string>\n" +
                "  </entry>\n" +
                "</tree-map>";

        TreeMap result = (TreeMap) assertBothWays(map, expected);
        assertNull(result.comparator());
    }

    public void testEmptyTreeMap() {
        TreeMap map = new TreeMap();

        String expected = "<tree-map/>";
        TreeMap result = (TreeMap) assertBothWays(map, expected);
        assertNull(result.comparator());
    }

    public void testTreeMapDoesNotUseComparatorAtDeserialization() {
        TreeMap map = new TreeMap(new UnusedComparator());
        map.put("john", "doe");
        map.put("benny", "hill");
        map.put("joe", "walnes");

        String expected = "" +
                "<tree-map>\n" +
                "  <comparator class=\"unused-comparator\"/>\n" +
                "  <entry>\n" +
                "    <string>benny</string>\n" +
                "    <string>hill</string>\n" +
                "  </entry>\n" +
                "  <entry>\n" +
                "    <string>joe</string>\n" +
                "    <string>walnes</string>\n" +
                "  </entry>\n" +
                "  <entry>\n" +
                "    <string>john</string>\n" +
                "    <string>doe</string>\n" +
                "  </entry>\n" +
                "</tree-map>";

        assertEquals(expected, xstream.toXML(map));
        TreeMap result = (TreeMap) xstream.fromXML(expected);
        assertSame(UnusedComparator.THROWING_COMPARATOR, result.comparator());
        assertEquals(new ArrayList(map.entrySet()), new ArrayList(result.entrySet()));
    }

    public void testTreeSetWithComparator() {
        TreeSet set = new TreeSet(new MyComparator());
        set.add("hi");
        set.add("bye");

        String expected = "" +
                "<sorted-set>\n" +
                "  <comparator class=\"my-comparator\">\n" +
                "    <something>stuff</something>\n" +
                "  </comparator>\n" +
                "  <string>bye</string>\n" +
                "  <string>hi</string>\n" +
                "</sorted-set>";

        TreeSet result = (TreeSet) assertBothWays(set, expected);
        assertEquals(MyComparator.class, result.comparator().getClass());
    }

    public void testTreeSetWithoutComparator() {
        TreeSet set = new TreeSet();
        set.add("hi");
        set.add("bye");

        String expected = "" +
                "<sorted-set>\n" +
                "  <string>bye</string>\n" +
                "  <string>hi</string>\n" +
                "</sorted-set>";

        TreeSet result = (TreeSet)assertBothWays(set, expected);
        assertNull(result.comparator());
    }

    public void testEmptyTreeSet() {
        TreeSet set = new TreeSet();

        String expected = "<sorted-set/>";
        TreeSet result = (TreeSet)assertBothWays(set, expected);
        assertNull(result.comparator());
    }

    public void testTreeSetDoesNotUseComparatorAtDeserialization() {
        TreeSet set = new TreeSet(new UnusedComparator());
        set.add("guy");
        set.add("hi");
        set.add("bye");

        String expected = "" +
                "<sorted-set>\n" +
                "  <comparator class=\"unused-comparator\"/>\n" +
                "  <string>bye</string>\n" +
                "  <string>guy</string>\n" +
                "  <string>hi</string>\n" +
                "</sorted-set>";

        assertEquals(expected, xstream.toXML(set));
        TreeSet result = (TreeSet) xstream.fromXML(expected);
        assertSame(UnusedComparator.THROWING_COMPARATOR, result.comparator());
        assertEquals(new ArrayList(set), new ArrayList(result));
    }
}
