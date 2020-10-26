/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.acceptance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.TreeSet;

import com.thoughtworks.xstream.core.JVM;


public class TreeMapAndTreeSetTest extends AbstractAcceptanceTest {

    public static class MyComparator implements Comparator<String> {
        @SuppressWarnings("unused")
        private final String something = "stuff";

        @Override
        public int compare(final String o1, final String o2) {
            return o1.compareTo(o2);
        }
    }

    public static class UnusedComparator implements Comparator<String> {

        private final static Comparator<String> THROWING_COMPARATOR = new Comparator<String>() {

            @Override
            public int compare(final String o1, final String o2) {
                throw new UnsupportedOperationException();
            }

        };

        @Override
        public int compare(final String o1, final String o2) {
            return o1.compareTo(o2);
        }

        private Object readResolve() {
            return THROWING_COMPARATOR;
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("my-comparator", MyComparator.class);
        xstream.alias("unused-comparator", UnusedComparator.class);
    }

    public void testTreeMapWithComparator() {
        final TreeMap<String, String> map = new TreeMap<>(new MyComparator());
        map.put("benny", "hill");
        map.put("joe", "walnes");

        final String expected = ""
            + "<tree-map>\n"
            + "  <comparator class=\"my-comparator\">\n"
            + "    <something>stuff</something>\n"
            + "  </comparator>\n"
            + "  <entry>\n"
            + "    <string>benny</string>\n"
            + "    <string>hill</string>\n"
            + "  </entry>\n"
            + "  <entry>\n"
            + "    <string>joe</string>\n"
            + "    <string>walnes</string>\n"
            + "  </entry>\n"
            + "</tree-map>";

        final TreeMap<String, String> result = assertBothWays(map, expected);
        assertEquals(MyComparator.class, result.comparator().getClass());
    }

    public void testTreeMapWithoutComparator() {
        final TreeMap<String, String> map = new TreeMap<>();
        map.put("benny", "hill");
        map.put("joe", "walnes");

        final String expected = ""
            + "<tree-map>\n"
            + "  <entry>\n"
            + "    <string>benny</string>\n"
            + "    <string>hill</string>\n"
            + "  </entry>\n"
            + "  <entry>\n"
            + "    <string>joe</string>\n"
            + "    <string>walnes</string>\n"
            + "  </entry>\n"
            + "</tree-map>";

        final TreeMap<String, String> result = assertBothWays(map, expected);
        assertNull(result.comparator());
    }

    public void testEmptyTreeMap() {
        final TreeMap<String, String> map = new TreeMap<>();

        final String expected = "<tree-map/>";
        final TreeMap<String, String> result = assertBothWays(map, expected);
        assertNull(result.comparator());
    }

    public void testTreeMapDoesNotUseComparatorAtDeserialization() {
        if (JVM.hasOptimizedTreeMapPutAll()) {
            final TreeMap<String, String> map = new TreeMap<>(new UnusedComparator());
            map.put("john", "doe");
            map.put("benny", "hill");
            map.put("joe", "walnes");

            final String expected = ""
                + "<tree-map>\n"
                + "  <comparator class=\"unused-comparator\"/>\n"
                + "  <entry>\n"
                + "    <string>benny</string>\n"
                + "    <string>hill</string>\n"
                + "  </entry>\n"
                + "  <entry>\n"
                + "    <string>joe</string>\n"
                + "    <string>walnes</string>\n"
                + "  </entry>\n"
                + "  <entry>\n"
                + "    <string>john</string>\n"
                + "    <string>doe</string>\n"
                + "  </entry>\n"
                + "</tree-map>";

            assertEquals(expected, xstream.toXML(map));
            final TreeMap<String, String> result = xstream.fromXML(expected);
            assertSame(UnusedComparator.THROWING_COMPARATOR, result.comparator());
            assertEquals(new ArrayList<>(map.entrySet()), new ArrayList<>(result.entrySet()));
        }
    }

    public void testTreeSetWithComparator() {
        final TreeSet<String> set = new TreeSet<>(new MyComparator());
        set.add("hi");
        set.add("bye");

        final String expected = ""
            + "<sorted-set>\n"
            + "  <comparator class=\"my-comparator\">\n"
            + "    <something>stuff</something>\n"
            + "  </comparator>\n"
            + "  <string>bye</string>\n"
            + "  <string>hi</string>\n"
            + "</sorted-set>";

        final TreeSet<String> result = assertBothWays(set, expected);
        assertEquals(MyComparator.class, result.comparator().getClass());
    }

    public void testTreeSetWithoutComparator() {
        final TreeSet<String> set = new TreeSet<>();
        set.add("hi");
        set.add("bye");

        final String expected = ""
            + "<sorted-set>\n"
            + "  <string>bye</string>\n"
            + "  <string>hi</string>\n"
            + "</sorted-set>";

        final TreeSet<String> result = assertBothWays(set, expected);
        assertNull(result.comparator());
    }

    public void testEmptyTreeSet() {
        final TreeSet<String> set = new TreeSet<>();

        final String expected = "<sorted-set/>";
        final TreeSet<String> result = assertBothWays(set, expected);
        assertNull(result.comparator());
    }

    public void testTreeSetDoesNotUseComparatorAtDeserialization() {
        if (JVM.hasOptimizedTreeSetAddAll()) {
            final TreeSet<String> set = new TreeSet<>(new UnusedComparator());
            set.add("guy");
            set.add("hi");
            set.add("bye");

            final String expected = ""
                + "<sorted-set>\n"
                + "  <comparator class=\"unused-comparator\"/>\n"
                + "  <string>bye</string>\n"
                + "  <string>guy</string>\n"
                + "  <string>hi</string>\n"
                + "</sorted-set>";

            assertEquals(expected, xstream.toXML(set));
            final TreeSet<String> result = xstream.fromXML(expected);
            assertSame(UnusedComparator.THROWING_COMPARATOR, result.comparator());
            assertEquals(new ArrayList<>(set), new ArrayList<>(result));
        }
    }

    public void testTreeSetRemoveWorksProperlyAfterDeserialization() {
        final TreeSet<String> set = new TreeSet<>();
        set.add("guy");
        set.add("hi");
        set.add("bye");

        final TreeSet<String> result = xstream.fromXML(xstream.toXML(set));
        assertTrue(result.remove("hi"));
    }
}
