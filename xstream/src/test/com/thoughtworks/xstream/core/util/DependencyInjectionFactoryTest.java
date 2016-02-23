/*
 * Copyright (C) 2007, 2009, 2010, 2011, 2012, 2014, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 30. March 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.util.BitSet;

import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

import junit.framework.TestCase;


public class DependencyInjectionFactoryTest extends TestCase {
    public void testDependencyInjectionWithMatchingParameterSequence() {
        final BitSet used = new BitSet();
        final Exception exception = DependencyInjectionFactory.newInstance(used, ObjectAccessException.class,
            "The message", this, new RuntimeException("JUnit"));
        assertTrue(exception instanceof ObjectAccessException);
        assertEquals("The message", ((ErrorWriter)exception).get("message"));
        assertEquals("JUnit", ((ErrorWriter)exception).get("cause-message"));
        assertTrue(used.get(0));
        assertFalse(used.get(1));
        assertTrue(used.get(2));
    }

    public void testWillUseDefaultConstructor() {
        final BitSet used = new BitSet();
        final String string = DependencyInjectionFactory.newInstance(used, String.class, this);
        assertEquals("", string);
        assertFalse(used.get(0));
    }

    public void testWillMatchNullValue() {
        final BitSet used = new BitSet();
        final Exception exception = DependencyInjectionFactory.newInstance(used, ObjectAccessException.class,
            new TypedNull<String>(String.class), this, new RuntimeException("JUnit"));
        assertTrue(exception instanceof ObjectAccessException);
        assertNull(((ErrorWriter)exception).get("message"));
        assertEquals("JUnit", ((ErrorWriter)exception).get("cause-message"));
        assertTrue(used.get(0));
        assertFalse(used.get(1));
        assertTrue(used.get(2));
    }

    public void testWillMatchPrimitives() {
        final BitSet used = new BitSet();
        final String string = DependencyInjectionFactory.newInstance(used, String.class, "JUnit".getBytes(), 1, this,
            4);
        assertEquals("Unit", string);
        assertTrue(used.get(0));
        assertTrue(used.get(1));
        assertFalse(used.get(2));
        assertTrue(used.get(3));
    }

    public void testWillUseArbitraryOrder() {
        final BitSet used = new BitSet();
        final Exception exception = DependencyInjectionFactory.newInstance(used, ObjectAccessException.class,
            new RuntimeException("JUnit"), this, "The message");
        assertTrue(exception instanceof ObjectAccessException);
        assertEquals("The message", ((ErrorWriter)exception).get("message"));
        assertEquals("JUnit", ((ErrorWriter)exception).get("cause-message"));
        assertTrue(used.get(0));
        assertFalse(used.get(1));
        assertTrue(used.get(2));
    }

    public void testWillMatchMostSpecificDependency() {
        final BitSet used = new BitSet();
        final Exception exception = DependencyInjectionFactory.newInstance(used, ObjectAccessException.class,
            new RuntimeException("JUnit"), new IllegalArgumentException("foo"), this, "The message");
        assertTrue(exception instanceof ObjectAccessException);
        assertEquals("The message", ((ErrorWriter)exception).get("message"));
        assertEquals("foo", ((ErrorWriter)exception).get("cause-message"));
        assertFalse(used.get(0));
        assertTrue(used.get(1));
        assertFalse(used.get(2));
        assertTrue(used.get(3));
    }

    public void testWillMatchFirstMatchingDependency() {
        final BitSet used = new BitSet();
        final Exception exception = DependencyInjectionFactory.newInstance(used, ObjectAccessException.class,
            new RuntimeException("JUnit"), "The message", "bar", new IllegalArgumentException("foo"), this);
        assertTrue(exception instanceof ObjectAccessException);
        assertEquals("The message", ((ErrorWriter)exception).get("message"));
        assertEquals("foo", ((ErrorWriter)exception).get("cause-message"));
        assertFalse(used.get(0));
        assertTrue(used.get(1));
        assertFalse(used.get(2));
        assertTrue(used.get(3));
        assertFalse(used.get(4));
    }

    static class Thing {
        final TestCase testCase;
        final int first;
        final int second;

        public Thing() {
            this(1, 2, null);
        }

        public Thing(final Number num) {
            this(num.intValue(), 8 * num.intValue(), null);
        }

        public Thing(final String str, final TestCase testCase) {
            this(str.length(), 4 * str.length(), testCase);
        }

        public Thing(final Number num, final TestCase testCase) {
            this(num.intValue(), 4 * num.intValue(), testCase);
        }

        public Thing(final int first, final int second, final TestCase testCase) {
            this.first = first;
            this.second = second;
            this.testCase = testCase;
        }

        TestCase getTestCase() {
            return testCase;
        }

        int getFirst() {
            return first;
        }

        int getSecond() {
            return second;
        }
    }

    public void testWillMatchArbitraryOrderForOneAvailableConstructorOnly() {
        final BitSet used = new BitSet();
        final Thing thing = DependencyInjectionFactory.newInstance(used, Thing.class, this, 1, 2);
        assertSame(this, thing.getTestCase());
        assertEquals(1, thing.getFirst());
        assertEquals(2, thing.getSecond());
        assertTrue(used.get(0));
        assertTrue(used.get(1));
        assertTrue(used.get(2));
    }

    public void testWillSelectMatchingConstructor() {
        BitSet used = new BitSet();
        Thing thing = DependencyInjectionFactory.newInstance(used, Thing.class, this, 1);
        assertSame(this, thing.getTestCase());
        assertEquals(1, thing.getFirst());
        assertEquals(4, thing.getSecond());
        assertTrue(used.get(0));
        assertTrue(used.get(1));

        used = new BitSet();
        thing = DependencyInjectionFactory.newInstance(used, Thing.class, this, "a");
        assertSame(this, thing.getTestCase());
        assertEquals(1, thing.getFirst());
        assertEquals(4, thing.getSecond());
        assertTrue(used.get(0));
        assertTrue(used.get(1));
    }

    public void testWillSelectMatchingConstructorForFirstMatchingArguments() {
        BitSet used = new BitSet();
        Thing thing = DependencyInjectionFactory.newInstance(used, Thing.class, this, 1, "foo");
        assertSame(this, thing.getTestCase());
        assertEquals(1, thing.getFirst());
        assertEquals(4, thing.getSecond());
        assertTrue(used.get(0));
        assertTrue(used.get(1));
        assertFalse(used.get(2));

        used = new BitSet();
        thing = DependencyInjectionFactory.newInstance(used, Thing.class, this, "foo", 1);
        assertSame(this, thing.getTestCase());
        assertEquals(3, thing.getFirst());
        assertEquals(12, thing.getSecond());
        assertTrue(used.get(0));
        assertTrue(used.get(1));
        assertFalse(used.get(2));
    }
}
