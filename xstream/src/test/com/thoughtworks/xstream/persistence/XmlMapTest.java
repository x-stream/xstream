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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;


public class XmlMapTest extends TestCase {

    private MockedStrategy strategy;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        strategy = new MockedStrategy();
    }

    public void testWritesASingleObject() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        assertTrue(strategy.map.containsKey("guilherme"));
    }

    public void testWritesTwoObjects() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        assertTrue(strategy.map.containsKey("guilherme"));
        assertTrue(strategy.map.containsKey("silveira"));
    }

    public void testRemovesAWrittenObject() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        assertTrue(strategy.map.containsKey("guilherme"));
        final String aCuteString = map.remove("guilherme");
        assertEquals("aCuteString", aCuteString);
        assertFalse(strategy.map.containsKey("guilherme"));
    }

    public void testRemovesAnInvalidObject() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        final String aCuteString = map.remove("guilherme");
        assertNull(aCuteString);
    }

    public void testHasZeroLength() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        assertEquals(0, map.size());
    }

    public void testHasOneItem() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        assertEquals(1, map.size());
    }

    public void testHasTwoItems() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        assertEquals(2, map.size());
    }

    public void testIsNotEmpty() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        assertFalse("Map should not be empty", map.isEmpty());
    }

    public void testDoesNotContainKey() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        assertFalse(map.containsKey("guilherme"));
    }

    public void testContainsKey() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        assertTrue(map.containsKey("guilherme"));
    }

    public void testGetsAnObject() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        strategy.map.put("guilherme", "aCuteString");
        final String aCuteString = map.get("guilherme");
        assertEquals("aCuteString", aCuteString);
    }

    public void testGetsAnInvalidObject() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        final String aCuteString = map.get("guilherme");
        assertNull(aCuteString);
    }

    public void testRewritesASingleObject() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        assertEquals("aCuteString", map.get("guilherme"));
        map.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", map.get("guilherme"));
    }

    public void testIsEmpty() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        assertTrue("Map should be empty", map.isEmpty());
    }

    public void testClearsItsObjects() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        map.clear();
        assertEquals(0, map.size());
    }

    public void testPutsAllAddsTwoItems() {
        final Map<String, String> original = new HashMap<String, String>();
        original.put("guilherme", "aCuteString");
        original.put("silveira", "anotherCuteString");
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.putAll(original);
        assertEquals(2, map.size());
    }

    public void testContainsASpecificValue() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        final String value = "aCuteString";
        map.put("guilherme", value);
        assertTrue(map.containsValue(value));
    }

    public void testDoesNotContainASpecificValue() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        assertFalse(map.containsValue("zzzz"));
    }

    public void testEntrySetContainsAllItems() {
        final Map<String, String> original = new HashMap<String, String>();
        original.put("guilherme", "aCuteString");
        original.put("silveira", "anotherCuteString");
        final Set<Map.Entry<String, String>> originalSet = original.entrySet();
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        final Set<Map.Entry<String, String>> set = map.entrySet();
        assertTrue(set.containsAll(originalSet));
    }

    // actually an acceptance test?
    public void testIteratesOverEntryAndChecksItsKeyWithAnotherInstance() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        final XmlMap<String, String> built = new XmlMap<String, String>(strategy);
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            assertTrue(built.containsKey(entry.getKey()));
        }
    }

    // actually an acceptance test?
    public void testIteratesOverEntryAndChecksItsValueWithAnotherInstance() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        final XmlMap<String, String> built = new XmlMap<String, String>(strategy);
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            assertTrue(built.containsValue(entry.getValue()));
        }
    }

    public void testIteratesOverEntrySetContainingTwoItems() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        final Map<String, String> built = new HashMap<String, String>();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            built.put(entry.getKey(), entry.getValue());
        }
        assertEquals(map, built);
    }

    public void testRemovesAnItemThroughIteration() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        for (final Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator(); iter.hasNext();) {
            final Map.Entry<String, String> entry = iter.next();
            if (entry.getKey().equals("guilherme")) {
                iter.remove();
            }
        }
        assertFalse(map.containsKey("guilherme"));
    }

    public void testRewritesAObject() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", map.get("guilherme"));
    }

    public void testPutReturnsTheOldValueWhenRewritingAObject() {
        final XmlMap<String, String> map = new XmlMap<String, String>(strategy);
        map.put("guilherme", "aCuteString");
        assertEquals("aCuteString", map.put("guilherme", "anotherCuteString"));
    }

    private static class MockedStrategy implements PersistenceStrategy<String, String> {

        private final Map<String, String> map = new HashMap<String, String>();

        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
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
        public String put(final String key, final String value) {
            return map.put(key, value);
        }

        @Override
        public String remove(final Object key) {
            return map.remove(key);
        }

    }

}
