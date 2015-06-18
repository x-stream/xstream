/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. July 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class FieldDictionaryTest extends TestCase {

    static class AssertNoDuplicateHashMap<K, V> extends HashMap<K, V> {
        public V put(K key, V value) {
            assertFalse("Attempt to insert duplicate key: " + key, this.containsKey(key));
            return super.put(key, value);
        }
    }

    AssertNoDuplicateHashMap<Class<?>, Map<String, Field>> assertNoDuplicateHashMap;

    private FieldDictionary fieldDictionary;

    protected void setUp() throws Exception {
        super.setUp();
        fieldDictionary = new FieldDictionary();
        assertNoDuplicateHashMap = new AssertNoDuplicateHashMap<Class<?>, Map<String, Field>>();
        fieldDictionary.keyedByFieldNameCache = assertNoDuplicateHashMap;
    }

    static class SomeClass {
        private String a;
        private String c;
        private transient String b;
        private static String d;
        private String e;
    }

    public void testListsFieldsInClassInDefinitionOrder() {
        Iterator fields = fieldDictionary.fieldsFor(SomeClass.class);
        assertEquals("a", getNonStaticFieldName(fields));
        assertEquals("c", getNonStaticFieldName(fields));
        assertEquals("b", getNonStaticFieldName(fields));
        assertEquals("e", getNonStaticFieldName(fields));
        assertFalse("No more fields should be present", fields.hasNext());
    }

    static class SpecialClass extends SomeClass {
        private String brilliant;
    }

    public void testIncludesFieldsInSuperClasses() {
        Iterator fields = fieldDictionary.fieldsFor(SpecialClass.class);
        assertEquals("a", getNonStaticFieldName(fields));
        assertEquals("c", getNonStaticFieldName(fields));
        assertEquals("b", getNonStaticFieldName(fields));
        assertEquals("e", getNonStaticFieldName(fields));
        assertEquals("brilliant", getNonStaticFieldName(fields));
        assertFalse("No more fields should be present", fields.hasNext());
    }

    class InnerClass { // note: no static makes this an inner class, not nested class.
        private String someThing;
    }

    public void testIncludesOuterClassReferenceForInnerClass() {
        Iterator fields = fieldDictionary.fieldsFor(InnerClass.class);
        assertEquals("someThing", getNonStaticFieldName(fields));
        Field innerField = ((Field)fields.next());
        assertEquals("this$0", innerField.getName());
        assertEquals(FieldDictionaryTest.class, innerField.getType());
        assertFalse("No more fields should be present", fields.hasNext());
    }

    static class ManyFields {
        String a1, b1, c1, d1, e1, f1, g1, h1, i1, j1, k1, l1, m1, n1, o1, p1, q1, r1, s1, t1, u1;
        String a2, b2, c2, d2, e2, f2, g2, h2, i2, j2, k2, l2, m2, n2, o2, p2, q2, r2, s2, t2, u2;
    }

    public void testSynchronizedAccessShouldEnsureEachClassAddedOnceToCache() throws Exception {
        final List<String> exceptions = Collections.synchronizedList(new ArrayList<String>());

        Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                exceptions.add("Exception " + ex.getClass() + " message " + ex.getMessage() + "\n");
            }
        };

        CyclicBarrier gate = new CyclicBarrier(21);
        List<Thread> threads = createThreads(gate, 20);

        for (Thread thread : threads) {
            thread.setUncaughtExceptionHandler(exceptionHandler);
            thread.start();
        }
        gate.await();

        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals("Assertions failed or exceptions thrown",
                     Collections.emptyList(), exceptions);
    }

    private static String getNonStaticFieldName(Iterator fields) {
        final Field field = (Field)fields.next();
        // JRockit declares static fields first, XStream will ignore them anyway
        if ((field.getModifiers() & Modifier.STATIC) > 0) {
            return getNonStaticFieldName(fields);
        }
        return field.getName();
    }

    private List<Thread> createThreads(final CyclicBarrier gate, int count) {
        List<Thread> threads = new ArrayList<Thread>();
        for (int i=0; i < count; i++) {
            threads.add(new Thread() {
                public void run() {
                    try {
                        gate.await();
                        Iterator<Field> fieldIterator = fieldDictionary.fieldsFor(ManyFields.class);
                        int fieldCount = 0;
                        while (fieldIterator.hasNext()) {
                            fieldCount++;
                            fieldIterator.next();
                        }
                        assertTrue("fieldCount expected > 10 actual " + fieldCount, fieldCount > 10);
                    } catch (InterruptedException e) {
                        fail("Exception " + e.getClass());
                    } catch (BrokenBarrierException e) {
                        fail("Exception " + e.getClass());
                    }
                }
            });
        }
        return threads;
    }
}
