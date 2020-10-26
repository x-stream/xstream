/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 14. July 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

import junit.framework.TestCase;


public class FieldDictionaryTest extends TestCase {

    private FieldDictionary fieldDictionary;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        fieldDictionary = new FieldDictionary();
    }

    @SuppressWarnings("unused")
    static class SomeClass {
        private String a;
        private String c;
        private transient String b;
        private static String d;
        private String e;
    }

    public void testListsFieldsInClassInDefinitionOrder() {
        final Iterator<Field> fields = fieldDictionary.fieldsFor(SomeClass.class);
        assertEquals("a", getNonStaticFieldName(fields));
        assertEquals("c", getNonStaticFieldName(fields));
        assertEquals("b", getNonStaticFieldName(fields));
        assertEquals("e", getNonStaticFieldName(fields));
        assertFalse("No more fields should be present", fields.hasNext());
    }

    @SuppressWarnings("unused")
    static class SpecialClass extends SomeClass {
        private String brilliant;
    }

    public void testIncludesFieldsInSuperClasses() {
        final Iterator<Field> fields = fieldDictionary.fieldsFor(SpecialClass.class);
        assertEquals("a", getNonStaticFieldName(fields));
        assertEquals("c", getNonStaticFieldName(fields));
        assertEquals("b", getNonStaticFieldName(fields));
        assertEquals("e", getNonStaticFieldName(fields));
        assertEquals("brilliant", getNonStaticFieldName(fields));
        assertFalse("No more fields should be present", fields.hasNext());
    }

    private static String getNonStaticFieldName(final Iterator<Field> fields) {
        final Field field = fields.next();
        // JRockit declares static fields first, XStream will ignore them anyway
        if ((field.getModifiers() & Modifier.STATIC) > 0) {
            return getNonStaticFieldName(fields);
        }
        return field.getName();
    }

    @SuppressWarnings("unused")
    class InnerClass { // note: no static makes this an inner class, not nested class.
        private String someThing;
    }

    public void testIncludesOuterClassReferenceForInnerClass() {
        final Iterator<Field> fields = fieldDictionary.fieldsFor(InnerClass.class);
        assertEquals("someThing", getNonStaticFieldName(fields));
        final Field innerField = fields.next();
        assertEquals("this$0", innerField.getName());
        assertEquals(FieldDictionaryTest.class, innerField.getType());
        assertFalse("No more fields should be present", fields.hasNext());
    }

    @SuppressWarnings("serial")
    private static class AssertNoDuplicateHashMap<K, V> extends ConcurrentHashMap<K, V> {
        @Override
        public V put(final K key, final V value) {
            assertFalse("Attempt to insert duplicate key: " + key, containsKey(key));
            return super.put(key, value);
        }
    }

    static class A { String a; }
    static class B extends A { String b; }
    static class C extends B { String c; }
    static class D extends C { String d; }
    static class E extends D { String e; }
    static class F extends E { String f; }
    static class G extends F { String g; }
    static class H extends G { String h; }
    static class I extends H { String i; }
    static class J extends I { String j; }
    static class BB extends B { @SuppressWarnings("hiding") String b; }
    static class CC extends C { @SuppressWarnings("hiding") String c; }
    static class DD extends D { @SuppressWarnings("hiding") String d; }
    static class EE extends E { @SuppressWarnings("hiding") String e; }
    static class FF extends F { @SuppressWarnings("hiding") String f; }
    static class GG extends G { @SuppressWarnings("hiding") String g; }
    static class HH extends H { @SuppressWarnings("hiding") String h; }
    static class II extends I { @SuppressWarnings("hiding") String i; }
    static class JJ extends J { @SuppressWarnings("hiding") String j; }
    static class JJJ extends JJ { @SuppressWarnings("hiding") String j; }

    public void testSynchronizedAccessShouldEnsureEachClassAddedOnceToCache() throws Exception {
        AssertNoDuplicateHashMap<Class<?>, Map<String, Field>> assertNoDuplicateHashMap =
                new AssertNoDuplicateHashMap<Class<?>, Map<String, Field>>();
        Field field = FieldDictionary.class.getDeclaredField("dictionaryEntries");
        field.setAccessible(true);
        field.set(fieldDictionary, assertNoDuplicateHashMap);

        final List<String> exceptions = Collections.synchronizedList(new ArrayList<String>());

        final Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(final Thread th, final Throwable ex) {
                exceptions.add("Exception " + ex.getClass() + " message " + ex.getMessage() + "\n");
            }
        };

        final List<Class<?>> types =
                Arrays.asList(A.class, B.class, C.class, E.class, F.class, G.class, H.class, I.class, J.class,
                    BB.class, CC.class, DD.class, EE.class, FF.class, GG.class, HH.class, II.class, JJ.class,
                    JJJ.class, FieldDictionaryTest.class);
        final CyclicBarrier gate = new CyclicBarrier(types.size() + 1);
        final List<Thread> threads = createThreads(gate, types);

        for (final Thread thread : threads) {
            thread.setUncaughtExceptionHandler(exceptionHandler);
            thread.start();
        }
        gate.await();

        for (final Thread thread : threads) {
            thread.join();
        }

        assertEquals("Assertions failed or exceptions thrown", Collections.emptyList(), exceptions);
    }

    private List<Thread> createThreads(final CyclicBarrier gate, final List<Class<?>> types) {
        Collections.shuffle(types);
        final List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < types.size(); i++) {
            final Class<?> type = types.get(i);
            threads.add(new Thread() {
                @Override
                public void run() {
                    try {
                        gate.await();
                        final Iterator<Field> fieldIterator = fieldDictionary.fieldsFor(type);
                        int fieldCount = 0;
                        while (fieldIterator.hasNext()) {
                            fieldCount++;
                            fieldIterator.next();
                        }
                        
                        if (type == FieldDictionaryTest.class) {
                            assertEquals("fieldCount not equal for type " + type.getName(), 2, fieldCount);
                        } else {
                            int count = 0;
                            for(Class<?> cls = type; cls != null; count++, cls = cls.getSuperclass());
                            assertEquals("fieldCount not equal for type " + type.getName(), count-1, fieldCount);
                        }
                    } catch (final InterruptedException | BrokenBarrierException e) {
                        fail("Exception " + e.getClass());
                    }
                }
            });
        }
        return threads;
    }
}
