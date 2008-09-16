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
import java.util.Arrays;
import java.util.List;


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
        System.setProperty("xstream.debug", "true");

        final Field invalidCounter = ObjectIdDictionary.class
            .getDeclaredField("invalidCounter");
        invalidCounter.setAccessible(true);
        final ObjectIdDictionary dict = new ObjectIdDictionary();
        invalidCounter.setInt(dict, 1);

        final StringBuffer memInfo = new StringBuffer("JVM: ");
        memInfo.append(System.getProperty("java.version"));
        memInfo.append("\nMemoryInfo:\n");
        memInfo.append(memoryInfo());
        memInfo.append('\n');

        byte[] memReserved = new byte[1024 * 512];
        Arrays.fill(memReserved, (byte)255); // prevent JVM optimization
        int oome = 0;
        int counter = 0;
        try {
            for (; invalidCounter.getInt(dict) != 0; ++counter) {
                try {
                    final String s = new String("JUnit ") + counter; // enforce new object
                    assertFalse("Failed in (" + counter + ")", dict.containsId(s));
                    dict.associateId(s, "X");
                    if (counter % 20000 == 19999) {
                        forceGC(memInfo);
                    }
                } catch (final OutOfMemoryError e) {
                    memReserved = null;
                    forceGC(memInfo);
                    if ( ++oome == 5) {
                        memInfo.append("Counter: ");
                        memInfo.append(counter);
                        memInfo.append("\nInvalid Counter: ");
                        memInfo.append(invalidCounter.getInt(dict));
                        memInfo.append('\n');
                        System.out.println(memInfo);
                        throw e;
                    }
                    memReserved = new byte[1024 * 512];
                }
            }
            assertTrue("Dictionary did not shrink; "
                + counter
                + " distinct objects; "
                + dict.size()
                + " size; "
                + memInfo, dict.size() < 10);
        } catch (final ForceGCError error) {
            memInfo.append("Counter: ");
            memInfo.append(counter);
            memInfo.append("\nInvalid Counter: ");
            memInfo.append(invalidCounter.getInt(dict));
            memInfo.append('\n');
            System.out.println(memInfo);
            throw error;
        } finally {
            System.setProperty("xstream.debug", "false");
        }
    }

    private void forceGC(final StringBuffer memInfo) {
        memInfo.append(memoryInfo());
        memInfo.append('\n');

        int i = 0;
        final SoftReference ref = new SoftReference(new Object());
        for (int count = 0; ref.get() != null && count++ < 4;) {
            List memory = new ArrayList();
            try {
                // fill up memory
                while (ref.get() != null) {
                    memory.add(new byte[1024 * 16]);
                }
            } catch (final OutOfMemoryError error) {
                // expected
            }
            i = memory.size();
            memory.clear();
            memory = null;
            System.gc();
            try {
                Thread.sleep(200);
            } catch (final InterruptedException e) {
                // ignore
            }
        }

        memInfo.append("Force GC, allocated blocks of 16KB: " + i);
        memInfo.append('\n');

        if (ref.get() != null) {
            throw new ForceGCError("This JVM is not releasing memory!");
        }
    }

    private String memoryInfo() {
        final Runtime runtime = Runtime.getRuntime();
        final StringBuffer buffer = new StringBuffer("Memory: ");
        buffer.append(runtime.freeMemory());
        buffer.append(" free / ");
        buffer.append(runtime.totalMemory());
        buffer.append(" total / ");
        buffer.append(runtime.maxMemory());
        buffer.append(" max");
        return buffer.toString();
    }

    private static class ForceGCError extends Error {
        public ForceGCError(final String msg) {
            super(msg);
        }
    }
}
