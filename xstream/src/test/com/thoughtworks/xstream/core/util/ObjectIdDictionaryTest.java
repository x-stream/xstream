package com.thoughtworks.xstream.core.util;

import junit.framework.TestCase;


public class ObjectIdDictionaryTest extends TestCase {

    public void testMapsIdsToObjectReferences() {
        ObjectIdDictionary dict = new ObjectIdDictionary();
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        dict.associateId(a, "id a");
        dict.associateId(b, "id b");
        dict.associateId(c, "id c");
        assertEquals("id a", dict.lookupId(a));
        assertEquals("id b", dict.lookupId(b));
        assertEquals("id c", dict.lookupId(c));
    }

    public void testTreatsObjectsThatAreEqualButNotSameInstanceAsDifferentReference() {
        ObjectIdDictionary dict = new ObjectIdDictionary();
        Integer a = new Integer(3);
        Integer b = new Integer(3);
        dict.associateId(a, "id a");
        dict.associateId(b, "id b");
        assertEquals("id a", dict.lookupId(a));
        assertEquals("id b", dict.lookupId(b));
    }

    public void testEnforceSameSystemHashCodeForGCedObjects() {
        // create 100000 Strings and call GC after creation of 10000
        final int loop = 10;
        final int elements = 10000;
        final int[] dictSizes = new int[loop * elements];
        ObjectIdDictionary dict = new ObjectIdDictionary();
        for (int i = 0; i < loop; ++i) {
            System.gc();
            for (int j = 0; j < elements; ++j) {
                final String s = new String("JUnit ") + j; // enforce new object
                dictSizes[i * elements + j] = dict.size();
                assertFalse("Failed in (" + i + "/" + j + ")", dict.containsId(s));
                dict.associateId(s, "X");
            }
        }
        assertFalse("Algorithm did not reach last element", 0 == dictSizes[loop * elements - 1]);
        assertFalse("Dictionary did not shrink", loop * elements - 1 == dictSizes[loop * elements - 1]);
    }
}
