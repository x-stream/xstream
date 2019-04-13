/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2007, 2008, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 13. June 2006 by Guilherme Silveira
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
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        assertTrue(strategy.map.containsKey("guilherme"));
    }

    public void testWritesTwoObjects() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        assertTrue(strategy.map.containsKey("guilherme"));
        assertTrue(strategy.map.containsKey("silveira"));
    }

    public void testRemovesAWrittenObject() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        assertTrue(strategy.map.containsKey("guilherme"));
        final String aCuteString = map.remove("guilherme");
        assertEquals("aCuteString", aCuteString);
        assertFalse(strategy.map.containsKey("guilherme"));
    }

    public void testRemovesAnInvalidObject() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        final String aCuteString = map.remove("guilherme");
        assertNull(aCuteString);
    }

    public void testHasZeroLength() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        assertEquals(0, map.size());
    }

    public void testHasOneItem() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        assertEquals(1, map.size());
    }

    public void testHasTwoItems() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        assertEquals(2, map.size());
    }

    public void testIsNotEmpty() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        assertFalse("Map should not be empty", map.isEmpty());
    }

    public void testDoesNotContainKey() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        assertFalse(map.containsKey("guilherme"));
    }

    public void testContainsKey() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        assertTrue(map.containsKey("guilherme"));
    }

    public void testGetsAnObject() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        strategy.map.put("guilherme", "aCuteString");
        final String aCuteString = map.get("guilherme");
        assertEquals("aCuteString", aCuteString);
    }

    public void testGetsAnInvalidObject() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        final String aCuteString = map.get("guilherme");
        assertNull(aCuteString);
    }

    public void testRewritesASingleObject() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        assertEquals("aCuteString", map.get("guilherme"));
        map.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", map.get("guilherme"));
    }

    public void testIsEmpty() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        assertTrue("Map should be empty", map.isEmpty());
    }

    public void testClearsItsObjects() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        map.clear();
        assertEquals(0, map.size());
    }

    public void testPutsAllAddsTwoItems() {
        final Map<String, String> original = new HashMap<>();
        original.put("guilherme", "aCuteString");
        original.put("silveira", "anotherCuteString");
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.putAll(original);
        assertEquals(2, map.size());
    }

    public void testContainsASpecificValue() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        final String value = "aCuteString";
        map.put("guilherme", value);
        assertTrue(map.containsValue(value));
    }

    public void testDoesNotContainASpecificValue() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        assertFalse(map.containsValue("zzzz"));
    }

    public void testEntrySetContainsAllItems() {
        final Map<String, String> original = new HashMap<>();
        original.put("guilherme", "aCuteString");
        original.put("silveira", "anotherCuteString");
        final Set<Map.Entry<String, String>> originalSet = original.entrySet();
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        final Set<Map.Entry<String, String>> set = map.entrySet();
        assertTrue(set.containsAll(originalSet));
    }

    // actually an acceptance test?
    public void testIteratesOverEntryAndChecksItsKeyWithAnotherInstance() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        final XmlMap<String, String> built = new XmlMap<>(strategy);
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            assertTrue(built.containsKey(entry.getKey()));
        }
    }

    // actually an acceptance test?
    public void testIteratesOverEntryAndChecksItsValueWithAnotherInstance() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        final XmlMap<String, String> built = new XmlMap<>(strategy);
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            assertTrue(built.containsValue(entry.getValue()));
        }
    }

    public void testIteratesOverEntrySetContainingTwoItems() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("silveira", "anotherCuteString");
        final Map<String, String> built = new HashMap<>();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            built.put(entry.getKey(), entry.getValue());
        }
        assertEquals(map, built);
    }

    public void testRemovesAnItemThroughIteration() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
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
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        map.put("guilherme", "anotherCuteString");
        assertEquals("anotherCuteString", map.get("guilherme"));
    }

    public void testPutReturnsTheOldValueWhenRewritingAObject() {
        final XmlMap<String, String> map = new XmlMap<>(strategy);
        map.put("guilherme", "aCuteString");
        assertEquals("aCuteString", map.put("guilherme", "anotherCuteString"));
    }

    private static class MockedStrategy implements PersistenceStrategy<String, String> {

        private final Map<String, String> map = new HashMap<>();

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
