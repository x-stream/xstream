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
        final PrioritizedList<String> list = new PrioritizedList<String>();
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
        final PrioritizedList<String> list = new PrioritizedList<String>();
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
        final PrioritizedList<String> list = new PrioritizedList<String>();
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
