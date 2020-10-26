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
import java.util.Map;

import junit.framework.TestCase;


public class OrderRetainingMapTest extends TestCase {
    private Map<String, String> map;

    private void assertNextEquals(final Object expected, final Iterator<String> iterator) {
        assertTrue("Expected to pull of another item from iterator : " + expected, iterator.hasNext());
        assertEquals(expected, iterator.next());
    }

    private void assertNextEntryEquals(final Object expectedKey, final Object expectedValue,
            final Iterator<Map.Entry<String, String>> iterator) {
        assertTrue("Expected to pull of another item from iterator : " + expectedKey + "=" + expectedValue, iterator
            .hasNext());
        final Map.Entry<String, String> actual = iterator.next();
        assertEquals(expectedKey, actual.getKey());
        assertEquals(expectedValue, actual.getValue());
    }

    private void assertNoMore(final Iterator<?> iterator) {
        assertFalse("Should be no more items in iterator", iterator.hasNext());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        map = new OrderRetainingMap<String, String>();
        map.put("one", "ONE");
        map.put("two", "TWO");
        map.put("three", "THREE");
        map.put("four", "FOUR");
    }

    public void testMaintainsOrderOfKeySet() {
        final Iterator<String> keySetIterator = map.keySet().iterator();
        assertNextEquals("one", keySetIterator);
        assertNextEquals("two", keySetIterator);
        assertNextEquals("three", keySetIterator);
        assertNextEquals("four", keySetIterator);
        assertNoMore(keySetIterator);
    }

    public void testMaintainsOrderOfValues() {
        final Iterator<String> valuesIterator = map.values().iterator();
        assertNextEquals("ONE", valuesIterator);
        assertNextEquals("TWO", valuesIterator);
        assertNextEquals("THREE", valuesIterator);
        assertNextEquals("FOUR", valuesIterator);
        assertNoMore(valuesIterator);
    }

    public void testMaintainsOrderOfEntries() {
        final Iterator<Map.Entry<String, String>> entrySetIterator = map.entrySet().iterator();
        assertNextEntryEquals("one", "ONE", entrySetIterator);
        assertNextEntryEquals("two", "TWO", entrySetIterator);
        assertNextEntryEquals("three", "THREE", entrySetIterator);
        assertNextEntryEquals("four", "FOUR", entrySetIterator);
        assertNoMore(entrySetIterator);
    }

    public void testMaintainsOrderOfEntriesAfterCopyCtor() {
        final Iterator<Map.Entry<String, String>> entrySetIterator = new OrderRetainingMap<String, String>(map)
            .entrySet()
            .iterator();
        assertNextEntryEquals("one", "ONE", entrySetIterator);
        assertNextEntryEquals("two", "TWO", entrySetIterator);
        assertNextEntryEquals("three", "THREE", entrySetIterator);
        assertNextEntryEquals("four", "FOUR", entrySetIterator);
        assertNoMore(entrySetIterator);
    }

}
