/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. February 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;

import java.util.Iterator;
import java.util.Map;

public class OrderRetainingMapTest extends TestCase {
    private Map map;

    private void assertNextEquals(Object expected, Iterator iterator) {
        assertTrue("Expected to pull of another item from iterator : " + expected, iterator.hasNext());
        assertEquals(expected, iterator.next());
    }

    private void assertNextEntryEquals(Object expectedKey, Object expectedValue, Iterator iterator) {
        assertTrue("Expected to pull of another item from iterator : " + expectedKey + "=" + expectedValue, iterator.hasNext());
        Map.Entry actual = (Map.Entry) iterator.next();
        assertEquals(expectedKey, actual.getKey());
        assertEquals(expectedValue, actual.getValue());
    }

    private void assertNoMore(Iterator iterator) {
        assertFalse("Should be no more items in iterator", iterator.hasNext());
    }

    protected void setUp() throws Exception {
        super.setUp();
        map = new OrderRetainingMap();
        map.put("one", "ONE");
        map.put("two", "TWO");
        map.put("three", "THREE");
        map.put("four", "FOUR");
    }

    public void testMaintainsOrderOfKeySet() {
        Iterator keySetIterator = map.keySet().iterator();
        assertNextEquals("one", keySetIterator);
        assertNextEquals("two", keySetIterator);
        assertNextEquals("three", keySetIterator);
        assertNextEquals("four", keySetIterator);
        assertNoMore(keySetIterator);
    }

    public void testMaintainsOrderOfValues() {
        Iterator valuesIterator = map.values().iterator();
        assertNextEquals("ONE", valuesIterator);
        assertNextEquals("TWO", valuesIterator);
        assertNextEquals("THREE", valuesIterator);
        assertNextEquals("FOUR", valuesIterator);
        assertNoMore(valuesIterator);
    }

    public void testMaintainsOrderOfEntries() {
        Iterator entrySetIterator = map.entrySet().iterator();
        assertNextEntryEquals("one", "ONE", entrySetIterator);
        assertNextEntryEquals("two", "TWO", entrySetIterator);
        assertNextEntryEquals("three", "THREE", entrySetIterator);
        assertNextEntryEquals("four", "FOUR", entrySetIterator);
        assertNoMore(entrySetIterator);
    }

    public void testMaintainsOrderOfEntriesAfterCopyCtor() {
        Iterator entrySetIterator = new OrderRetainingMap(map).entrySet().iterator();
        assertNextEntryEquals("one", "ONE", entrySetIterator);
        assertNextEntryEquals("two", "TWO", entrySetIterator);
        assertNextEntryEquals("three", "THREE", entrySetIterator);
        assertNextEntryEquals("four", "FOUR", entrySetIterator);
        assertNoMore(entrySetIterator);
    }

}
