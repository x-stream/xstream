/*
 * Copyright (C) 2006 Joe Walnes.
 * Copyright (C) 2007, 2008, 2009, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. July 2006 by Guilherme Silveira
 */
package com.thoughtworks.xstream.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;


public class XmlArrayListTest extends TestCase {
    private MockedStrategy<String> strategy;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        strategy = new MockedStrategy<String>();
    }

    public void testWritesASingleObject() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        assertTrue(strategy.map.containsValue("guilherme"));
    }

    public void testWritesASingleObjectInANegativePosition() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        try {
            xmlList.add(-1, "guilherme");
            fail();
        } catch (final IndexOutOfBoundsException ex) {
            // ok
        }
    }

    public void testWritesASingleObjectInFirstPosition() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        assertTrue(strategy.map.containsKey(new Integer(0)));
    }

    public void testWritesTwoObjects() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        assertTrue(strategy.map.containsValue("guilherme"));
        assertTrue(strategy.map.containsValue("silveira"));
        assertTrue(strategy.map.containsKey(new Integer(0)));
        assertTrue(strategy.map.containsKey(new Integer(1)));
    }

    public void testWritesTwoObjectsGuaranteesItsEnumerationOrder() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        assertEquals("guilherme", strategy.map.get(new Integer(0)));
        assertEquals("silveira", strategy.map.get(new Integer(1)));
    }

    public void testWritesASecondObjectInAPositionHigherThanTheListsSize() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        try {
            xmlList.add("silveira");
            xmlList.add(3, "guilherme");
            fail();
        } catch (final IndexOutOfBoundsException ex) {
            // ok
        }
    }

    public void testRemovesAWrittenObject() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        assertTrue(xmlList.remove("guilherme"));
        assertFalse(strategy.map.containsValue("guilherme"));
    }

    public void testRemovesAWrittenObjectImplyingInAChangeInTheList() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        final boolean changed = xmlList.remove("guilherme");
        assertTrue(changed);
    }

    public void testRemovesAnInvalidObjectWithoutAffectingTheList() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        final boolean removed = xmlList.remove("guilherme");
        assertFalse(removed);
    }

    public void testHasZeroLengthWhenInstantiated() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        assertEquals(0, xmlList.size());
    }

    public void testHasOneItem() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        assertEquals(1, xmlList.size());
    }

    public void testHasTwoItems() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        assertEquals(2, xmlList.size());
    }

    public void testIsNotEmpty() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        assertFalse(xmlList.isEmpty());
    }

    public void testDoesNotContainKey() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        assertFalse(xmlList.contains("guilherme"));
    }

    public void testContainsKey() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        assertTrue(xmlList.contains("guilherme"));
    }

    public void testGetsAnObject() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        final Object onlyValue = xmlList.iterator().next();
        assertEquals("guilherme", onlyValue);
    }

    public void testGetsTheFirstObject() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        assertEquals("guilherme", xmlList.get(0));
    }

    public void testGetsTheSecondObject() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        assertEquals("silveira", xmlList.get(1));
    }

    public void testInsertsAnObjectInTheMiddleOfTheList() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        xmlList.add(1, "de azevedo");
        assertEquals("guilherme", xmlList.get(0));
        assertEquals("de azevedo", xmlList.get(1));
        assertEquals("silveira", xmlList.get(2));
    }

    public void testIteratingGuaranteesItsEnumeration() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        final Iterator<String> it = xmlList.iterator();
        assertEquals("guilherme", it.next());
        assertEquals("silveira", it.next());
    }

    public void testIsEmpty() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        assertTrue(xmlList.isEmpty());
    }

    public void testClearsItsObjects() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        xmlList.clear();
        assertEquals(0, xmlList.size());
    }

    public void testPutsAllAddsTwoItems() {
        final Set<String> original = new HashSet<String>();
        original.add("guilherme");
        original.add("silveira");
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.addAll(original);
        assertEquals(2, xmlList.size());
    }

    public void testContainsASpecificValue() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        assertTrue(xmlList.contains("guilherme"));
    }

    public void testDoesNotContainASpecificValue() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        assertFalse(xmlList.contains("zzzz"));
    }

    public void testEntrySetContainsAllItems() {
        final Set<String> original = new HashSet<String>();
        original.add("guilherme");
        original.add("silveira");
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        assertTrue(xmlList.containsAll(original));
    }

    // actually an acceptance test?
    public void testIteratesOverEntryAndChecksWithAnotherInstance() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        final XmlArrayList<String> built = new XmlArrayList<String>(strategy);
        for (Object entry : xmlList) {
            assertTrue(built.contains(entry));
        }
    }

    public void testIteratesOverEntrySetContainingTwoItems() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        final List<String> built = new ArrayList<String>();
        for (String entry : xmlList) {
            built.add(entry);
        }
        assertEquals(xmlList, built);
    }

    public void testRemovesAnItemThroughIteration() {
        final XmlArrayList<String> xmlList = new XmlArrayList<String>(strategy);
        xmlList.add("guilherme");
        xmlList.add("silveira");
        for (final Iterator<String> iter = xmlList.iterator(); iter.hasNext();) {
            final Object entry = iter.next();
            if (entry.equals("guilherme")) {
                iter.remove();
            }
        }
        assertFalse(xmlList.contains("guilherme"));
    }

    private static class MockedStrategy<V> implements PersistenceStrategy<Integer, V> {

        private final Map<Integer, V> map = new HashMap<Integer, V>();

        @Override
        public Iterator<Map.Entry<Integer, V>> iterator() {
            return map.entrySet().iterator();
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public V get(final Object key) {
            return map.get(key);
        }

        @Override
        public V put(final Integer key, final V value) {
            return map.put(key, value);
        }

        @Override
        public V remove(final Object key) {
            return map.remove(key);
        }

    }

}
