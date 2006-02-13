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

    // TODO
    public void TODOtestMaintainsOrderOfEntries() {
        Iterator entrySetIterator = map.entrySet().iterator();
        assertNextEntryEquals("one", "ONE", entrySetIterator);
        assertNextEntryEquals("two", "TWO", entrySetIterator);
        assertNextEntryEquals("three", "THREE", entrySetIterator);
        assertNextEntryEquals("four", "FOUR", entrySetIterator);
        assertNoMore(entrySetIterator);
    }

}
