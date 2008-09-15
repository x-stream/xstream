/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
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

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


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

    public void testEnforceSameSystemHashCodeForGCedObjects() throws SecurityException, NoSuchFieldException, IllegalAccessException {
        final Field invalidCounter = ObjectIdDictionary.class.getDeclaredField("invalidCounter");
        invalidCounter.setAccessible(true);
        final StringBuffer memInfo = new StringBuffer("MemoryInfo:\n");
        memInfo.append(memoryInfo());
        memInfo.append('\n');

        // create 100000 Strings and call GC after creation of 10000
        final int loop = 10;
        final int elements = 10000;
        final int[] dictSizes = new int[loop * elements];
        ObjectIdDictionary dict = new ObjectIdDictionary();
        for (int i = 0; i < loop; ++i) {
            for (int j = 0; j < elements; ++j) {
                final String s = new String("JUnit ") + j; // enforce new object
                dictSizes[i * elements + j] = dict.size();
                assertFalse("Failed in (" + i + "/" + j + ")", dict.containsId(s));
                dict.associateId(s, "X");
            }
            memInfo.append(memoryInfo());
            memInfo.append('\n');
        }

        assertFalse("Algorithm did not reach last element", 0 == dictSizes[loop * elements - 1]);
        assertFalse("Dictionary did not shrink " + memInfo + "InvalidCounter: " + invalidCounter.getInt(dict),
            loop * elements - 1 == dictSizes[loop * elements - 1]);
    }

    private void forceGC() {
        SoftReference ref = new SoftReference(new byte[1024*16]);
        for (int count = 0; ref.get() != null && count++ < 10; ) {
            List memory = new ArrayList();
            try {
                while(ref.get() != null) {
                    memory.add(new byte[1024*16]);
                }
            } catch (OutOfMemoryError error) {
                // expected
            }
            memory.clear();
            memory = null;
            System.gc();
            System.runFinalization();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        assertNull("This JVM is not releasing memory!", ref.get() != null);
    }
    
    private String memoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        StringBuffer buffer = new StringBuffer("Memory: ");
        buffer.append(runtime.freeMemory());
        buffer.append(" free / ");
        buffer.append(runtime.totalMemory());
        buffer.append(" total");
        buffer.append(runtime.maxMemory());
        buffer.append(" max / ");
        return buffer.toString();
    }
}
