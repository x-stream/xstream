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

package com.thoughtworks.xstream.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;


public class XmlSetTest extends TestCase {
    private MockedStrategy strategy;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        strategy = new MockedStrategy();
    }

    public void testWritesASingleObject() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        assertTrue(strategy.map.containsValue("guilherme"));
    }

    public void testWritesTwoObjects() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        set.add("silveira");
        assertTrue(strategy.map.containsValue("guilherme"));
        assertTrue(strategy.map.containsValue("silveira"));
    }

    public void testRemovesAWrittenObject() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        assertTrue(strategy.map.containsValue("guilherme"));
        final boolean changed = set.remove("guilherme");
        assertTrue(changed);
        assertFalse(strategy.map.containsValue("guilherme"));
    }

    public void testRemovesAnInvalidObject() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        final boolean removed = set.remove("guilherme");
        assertFalse(removed);
    }

    public void testHasZeroLength() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        assertEquals(0, set.size());
    }

    public void testHasOneItem() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        assertEquals(1, set.size());
    }

    public void testHasTwoItems() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        set.add("silveira");
        assertEquals(2, set.size());
    }

    public void testIsNotEmpty() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        assertFalse("set should not be empty", set.isEmpty());
    }

    public void testDoesNotContainKey() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        assertFalse(set.contains("guilherme"));
    }

    public void testContainsKey() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        assertTrue(set.contains("guilherme"));
    }

    public void testGetsAnObject() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        final Object onlyValue = set.iterator().next();
        assertEquals("guilherme", onlyValue);
    }

    public void testIsEmpty() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        assertTrue("set should be empty", set.isEmpty());
    }

    public void testClearsItsObjects() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        set.add("silveira");
        set.clear();
        assertEquals(0, set.size());
    }

    public void testPutsAllAddsTwoItems() {
        final Set<String> original = new HashSet<String>();
        original.add("guilherme");
        original.add("silveira");
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.addAll(original);
        assertEquals(2, set.size());
    }

    public void testContainsASpecificValue() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        assertTrue(set.contains("guilherme"));
    }

    public void testDoesNotContainASpecificValue() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        assertFalse(set.contains("zzzz"));
    }

    public void testEntrySetContainsAllItems() {
        final Set<String> original = new HashSet<String>();
        original.add("guilherme");
        original.add("silveira");
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        set.add("silveira");
        assertTrue(set.containsAll(original));
    }

    // actually an acceptance test?
    public void testIteratesOverEntryAndChecksWithAnotherInstance() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        set.add("silveira");
        final XmlSet<String> built = new XmlSet<String>(strategy);
        for (final String entry : set) {
            assertTrue(built.contains(entry));
        }
    }

    public void testIteratesOverEntrySetContainingTwoItems() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        set.add("silveira");
        final Set<String> built = new HashSet<String>();
        for (final String entry : set) {
            built.add(entry);
        }
        assertEquals(set, built);
    }

    public void testRemovesAnItemThroughIteration() {
        final XmlSet<String> set = new XmlSet<String>(strategy);
        set.add("guilherme");
        set.add("silveira");
        for (final Iterator<String> iter = set.iterator(); iter.hasNext();) {
            final String entry = iter.next();
            if (entry.equals("guilherme")) {
                iter.remove();
            }
        }
        assertFalse(set.contains("guilherme"));
    }

    private static class MockedStrategy implements PersistenceStrategy<Long, String> {

        private final Map<Long, String> map = new HashMap<Long, String>();

        @Override
        public Iterator<Map.Entry<Long, String>> iterator() {
            return map.entrySet().iterator();
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public String get(final Object key) {
            return map.get(key);
        }

        @Override
        public String put(final Long key, final String value) {
            return map.put(key, value);
        }

        @Override
        public String remove(final Object key) {
            return map.remove(key);
        }

    }

}
