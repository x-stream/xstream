/*
 * Copyright (C) 2005, 2006 Joe Walnes.
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

import java.util.Iterator;

import junit.framework.TestCase;


public class PrioritizedListTest extends TestCase {

    private void assertNextEquals(final Object expected, final Iterator<String> iterator) {
        assertTrue("Expected to pull of another item from iterator : " + expected, iterator.hasNext());
        assertEquals(expected, iterator.next());
    }

    private void assertNoMore(final Iterator<?> iterator) {
        assertFalse("Should be no more items in iterator", iterator.hasNext());
    }

    public void testIteratesOverElementsInReverseOrderTheyWereAdded() {
        final PrioritizedList<String> list = new PrioritizedList<>();
        list.add("one", 0);
        list.add("two", 0);
        list.add("three", 0);

        final Iterator<String> iterator = list.iterator();
        assertNextEquals("three", iterator);
        assertNextEquals("two", iterator);
        assertNextEquals("one", iterator);
        assertNoMore(iterator);
    }

    public void testHandlesMultipleIsolatedIterators() {
        final PrioritizedList<String> list = new PrioritizedList<>();
        list.add("one", 0);
        list.add("two", 0);

        final Iterator<String> iteratorOne = list.iterator();
        assertNextEquals("two", iteratorOne);

        final Iterator<String> iteratorTwo = list.iterator();
        assertNextEquals("one", iteratorOne);

        assertNextEquals("two", iteratorTwo);
        assertNextEquals("one", iteratorTwo);

        assertNoMore(iteratorTwo);
        assertNoMore(iteratorOne);
    }

    public void testIteratesOverHighestPriorityItemsFirst() {
        final PrioritizedList<String> list = new PrioritizedList<>();
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

        final Iterator<String> iterator = list.iterator();
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
