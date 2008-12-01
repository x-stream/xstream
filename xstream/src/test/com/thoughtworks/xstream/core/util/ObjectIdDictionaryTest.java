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

import java.lang.reflect.Field;


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

    public void testEnforceSameSystemHashCodeForGCedObjects()
        throws NoSuchFieldException, IllegalAccessException {
        final Field actionCounter = ObjectIdDictionary.class.getDeclaredField("counter");
        actionCounter.setAccessible(true);
        final ObjectIdDictionary dict = new ObjectIdDictionary();
        actionCounter.setInt(dict, 1);

        final StringBuffer memInfo = new StringBuffer("JVM: ");
        memInfo.append(System.getProperty("java.version"));
        memInfo.append("\nOS: ");
        memInfo.append(System.getProperty("os.name"));
        memInfo.append(" / ");
        memInfo.append(System.getProperty("os.arch"));
        memInfo.append(" / ");
        memInfo.append(System.getProperty("os.version"));
        memInfo.append("\nMemoryInfo:\n");
        memInfo.append(memoryInfo());
        memInfo.append('\n');

        int counter = 0;
        for (; actionCounter.getInt(dict) != 0; ++counter) {
            final String s = new String("JUnit ") + counter; // enforce new object
            assertFalse("Failed in (" + counter + ")", dict.containsId(s));
            dict.associateId(s, "X");
            if (counter % 4000 == 3999) {
                System.gc();
                memInfo.append("\nMemoryInfo:\n");
                memInfo.append(memoryInfo());
            }
        }
        memInfo.append("\nMemoryInfo:\n");
        memInfo.append(memoryInfo());
        assertTrue("Dictionary did not shrink; "
            + counter
            + " distinct objects; "
            + dict.size()
            + " size; "
            + memInfo, dict.size() < 1100);
    }

    private String memoryInfo() {
        final Runtime runtime = Runtime.getRuntime();
        final StringBuffer buffer = new StringBuffer("Memory: ");
        // not available in JDK 1.3
        // buffer.append(runtime.maxMemory());
        // buffer.append(" max / ");
        buffer.append(runtime.freeMemory());
        buffer.append(" free / ");
        buffer.append(runtime.totalMemory());
        buffer.append(" total");
        return buffer.toString();
    }
}
