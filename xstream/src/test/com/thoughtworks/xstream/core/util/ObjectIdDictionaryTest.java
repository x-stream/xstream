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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
        final StringBuffer memInfo = new StringBuffer("JVM: ");
        memInfo.append(System.getProperty("java.version"));
        memInfo.append("\nMemoryInfo:\n");
        System.setProperty("xstream.debug", "true");

        try {
        int blocks = forceGCAndGetNumberOfBlocks(memInfo)/5;
        List softMemory = new ArrayList();
        while (blocks-- > 0) {
            softMemory.add(blocks < 60 ? (Object)new SoftReference(new byte[1024*80]) : (Object)new byte[1024*80]);
        }
        forceGCAndGetNumberOfBlocks(memInfo);
        
        // create 200000 Strings and call GC after creation of 50000
        final int loop = 4;
        final int elements = 50000;
        final int[] dictSizes = new int[loop * elements];
        ObjectIdDictionary dict = new ObjectIdDictionary();
        final Set systemHashes = new HashSet();
        for (int i = 0; i < loop; ++i) {
            for (int j = 0; j < elements; ++j) {
                final String s = new String("JUnit ") + j; // enforce new object
                systemHashes.add(new Integer(System.identityHashCode(s)));
                dictSizes[i * elements + j] = dict.size();
                assertFalse("Failed in (" + i + "/" + j + ")", dict.containsId(s));
                dict.associateId(s, "X");
            }
            forceGCAndGetNumberOfBlocks(memInfo);
        }

        System.setProperty("xstream.debug", "false");
        assertFalse("Algorithm did not reach last element", 0 == dictSizes[loop * elements - 1]);
        assertFalse("Dictionary did not shrink; " + systemHashes.size() + " distinct objects; " + memInfo,
            loop * elements - 1 == dictSizes[loop * elements - 1]);
        } catch(OutOfMemoryError e) {
            System.out.println(memInfo);
            throw e;
        }
    }

    private int forceGCAndGetNumberOfBlocks(StringBuffer memInfo) {
        memInfo.append(memoryInfo());
        memInfo.append('\n');

        int i = 0;
        SoftReference ref = new SoftReference(new Object());
        for (int count = 0; ref.get() != null && count++ < 4; ) {
            List memory = new ArrayList();
            try {
                // fill up memory
                while(ref.get() != null) {
                    memory.add(new byte[1024*16]);
                }
            } catch (OutOfMemoryError error) {
                // expected
            }
            i = memory.size();
            memory.clear();
            memory = null;
            System.gc();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        memInfo.append("Force GC, blocks: " + i);
        memInfo.append('\n');
        
        assertNull("This JVM is not releasing memory!", ref.get());
        return i;
    }
    
    private String memoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        StringBuffer buffer = new StringBuffer("Memory: ");
        buffer.append(runtime.freeMemory());
        buffer.append(" free / ");
        buffer.append(runtime.totalMemory());
        buffer.append(" total / ");
        buffer.append(runtime.maxMemory());
        buffer.append(" max");
        return buffer.toString();
    }
}
