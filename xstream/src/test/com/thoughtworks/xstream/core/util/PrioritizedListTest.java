package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;

import java.util.Iterator;

public class PrioritizedListTest extends TestCase {

    private void assertNextEquals(Object expected, Iterator iterator) {
        assertTrue("Expected to pull of another item from iterator : " + expected, iterator.hasNext());
        assertEquals(expected, iterator.next());
    }

    private void assertNoMore(Iterator iterator) {
        assertFalse("Should be no more items in iterator", iterator.hasNext());
    }

    public void testIteratesOverElementsInReverseOrderTheyWereAdded() {
        PrioritizedList list = new PrioritizedList();
        list.add("one", 0);
        list.add("two", 0);
        list.add("three", 0);

        Iterator iterator = list.iterator();
        assertNextEquals("three", iterator);
        assertNextEquals("two", iterator);
        assertNextEquals("one", iterator);
        assertNoMore(iterator);
    }

    public void testHandlesMultipleIsolatedIterators() {
        PrioritizedList list = new PrioritizedList();
        list.add("one", 0);
        list.add("two", 0);

        Iterator iteratorOne = list.iterator();
        assertNextEquals("two", iteratorOne);

        Iterator iteratorTwo = list.iterator();
        assertNextEquals("one", iteratorOne);

        assertNextEquals("two", iteratorTwo);
        assertNextEquals("one", iteratorTwo);

        assertNoMore(iteratorTwo);
        assertNoMore(iteratorOne);
    }

    public void testIteratesOverHighestPriorityItemsFirst() {
        PrioritizedList list = new PrioritizedList();
        list.add("medium one", 0);
        list.add("high one", 1);
        list.add("low one", -1);
        list.add("very high", 4);
        list.add("low two", -1);
        list.add("medium two", 0);
        list.add("VERY VERY high", 100);
        list.add("high two", 1);
        list.add("very low", -4);
        list.add("VERY VERY low", -100);

        Iterator iterator = list.iterator();
        assertNextEquals("VERY VERY high", iterator);
        assertNextEquals("very high", iterator);
        assertNextEquals("high two", iterator);
        assertNextEquals("high one", iterator);
        assertNextEquals("medium two", iterator);
        assertNextEquals("medium one", iterator);
        assertNextEquals("low two", iterator);
        assertNextEquals("low one", iterator);
        assertNextEquals("very low", iterator);
        assertNextEquals("VERY VERY low", iterator);
        assertNoMore(iterator);
    }
}
