/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2015 XStream Committers.
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.core.JVM;

public class FieldDictionaryTest extends TestCase {

    private FieldDictionary fieldDictionary;

    protected void setUp() throws Exception {
        super.setUp();
        fieldDictionary = new FieldDictionary();
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

    private static String getNonStaticFieldName(Iterator fields) {
        final Field field = (Field)fields.next();
        // JRockit declares static fields first, XStream will ignore them anyway
        if ((field.getModifiers() & Modifier.STATIC) > 0) {
            return getNonStaticFieldName(fields);
        }
        return field.getName();
    }

    private static class AssertNoDuplicateHashMap extends HashMap {
        public Object put(final Object key, final Object value) {
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
    static class BB extends B { String b; }
    static class CC extends C { String c; }
    static class DD extends D { String d; }
    static class EE extends E { String e; }
    static class FF extends F { String f; }
    static class GG extends G { String g; }
    static class HH extends H { String h; }
    static class II extends I { String i; }
    static class JJ extends J { String j; }
    static class JJJ extends JJ { String j; }

    public void testSynchronizedAccessShouldEnsureEachClassAddedOnceToCache() throws Exception {
        AssertNoDuplicateHashMap assertNoDuplicateHashMap = new AssertNoDuplicateHashMap();

        Field field = FieldDictionary.class.getDeclaredField("dictionaryEntries");
        field.setAccessible(true);
        field.set(fieldDictionary, assertNoDuplicateHashMap);

        final List exceptions = Collections.synchronizedList(new ArrayList());

        final List types =
                Arrays.asList(new Class[] {
                    A.class, B.class, C.class, E.class, F.class, G.class, H.class, I.class, J.class,
                    BB.class, CC.class, DD.class, EE.class, FF.class, GG.class, HH.class, II.class, JJ.class,
                    JJJ.class, FieldDictionaryTest.class
                 });
        final Thread[] threads = createThreads(types, exceptions);

        for (int i = 0; i < threads.length; ++i) {
            synchronized (threads[i]) {
                threads[i].start();
                threads[i].wait();
            }
        }

        for (int i = 0; i < threads.length; ++i) {
            synchronized (threads[i]) {
                threads[i].notifyAll();
            }
        }

        Thread.sleep(1500);

        for (int i = 0; i < threads.length; ++i) {
            threads[i].interrupt();
        }
        for (int i = 0; i < threads.length; ++i) {
            synchronized (threads[i]) {
                threads[i].join();
            }
        }

        assertEquals("Assertions failed or exceptions thrown", Collections.EMPTY_LIST, exceptions);
    }

    private Thread[] createThreads(final List types, final List exceptions) {
        Collections.shuffle(types);
        final Thread[] threads = new Thread[types.size()];
        for (int i = 0; i < types.size(); i++) {
            final Class type = (Class)types.get(i);
            threads[i] = new Thread() {
                public void run() {
                    try {
                        synchronized (this) {
                            notifyAll();
                            wait();
                        }
                        final Iterator fieldIterator = fieldDictionary.fieldsFor(type);
                        int fieldCount = 0;
                        while (fieldIterator.hasNext()) {
                            Field field = (Field)fieldIterator.next();
                            if (JVM.is15() || !Modifier.isStatic(field.getModifiers())) {
                                fieldCount++;
                            }
                        }
                        
                        if (type == FieldDictionaryTest.class) {
                            assertEquals("fieldCount not equal for type " + type.getName(), 2, fieldCount);
                        } else {
                            int count = 0;
                            for(Class cls = type; cls != null; count++, cls = cls.getSuperclass());
                            assertEquals("fieldCount not equal for type " + type.getName(), count-1, fieldCount);
                        }
                    } catch (final Exception e) {
                        exceptions.add(e);
                    } catch (final Error e) {
                        exceptions.add(e);
                    }
                }
            };
        }
        return threads;
    }
}
