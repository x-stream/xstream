/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2010, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 30. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;

public class ObjectIdDictionaryTest extends TestCase {

    public void testMapsIdsToObjectReferences() {
        final ObjectIdDictionary dict = new ObjectIdDictionary();
        final Object a = new Object();
        final Object b = new Object();
        final Object c = new Object();
        dict.associateId(a, "id a");
        dict.associateId(b, "id b");
        dict.associateId(c, "id c");
        assertEquals("id a", dict.lookupId(a));
        assertEquals("id b", dict.lookupId(b));
        assertEquals("id c", dict.lookupId(c));
    }

    public void testTreatsObjectsThatAreEqualButNotSameInstanceAsDifferentReference() {
        final ObjectIdDictionary dict = new ObjectIdDictionary();
        final Integer a = new Integer(3);
        final Integer b = new Integer(3);
        dict.associateId(a, "id a");
        dict.associateId(b, "id b");
        assertEquals("id a", dict.lookupId(a));
        assertEquals("id b", dict.lookupId(b));
    }

    public void testEntriesAreGarbageCollected() throws InterruptedException {
        final ObjectIdDictionary dict = new ObjectIdDictionary();

        int counter = 0;
        for (; counter < 1000; ++counter) {
            final String s = new String("JUnit " + counter); // enforce new object
            assertFalse("Failed in (" + counter + ")", dict.containsId(s));
            dict.associateId(s, "X");
            if (counter % 50 == 49) {
                System.gc();
                Thread.sleep(10);
            }
        }
        int size = dict.size();
        assertTrue("Dictionary did not shrink; "
            + counter
            + " distinct objects; "
            + size
            + " size", dict.size() < 250);
    }
}
