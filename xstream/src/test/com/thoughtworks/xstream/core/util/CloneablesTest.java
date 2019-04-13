/*
 * Copyright (C) 2010, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created 18.11.2010 by Joerg Schaible.
 */
package com.thoughtworks.xstream.core.util;

import java.util.Arrays;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

import junit.framework.TestCase;


public class CloneablesTest extends TestCase {
    public void testCloneOfCloneable() {
        final TypedNull<String> stringNull = new CloneableTypedNull<>(String.class);
        final TypedNull<String> stringNullClone = Cloneables.clone(stringNull);
        assertSame(String.class, stringNullClone.getType());
    }

    public void testCloneOfNotCloneable() {
        final TypedNull<String> stringNull = new TypedNull<>(String.class);
        assertNull(Cloneables.clone(stringNull));
    }

    public void testCloneOfUncloneable() {
        final TypedNull<String> stringNull = new UncloneableTypedNull<>(String.class);
        try {
            Cloneables.clone(stringNull);
            fail("Thrown " + ObjectAccessException.class.getName() + " expected");
        } catch (final ObjectAccessException e) {
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
    }

    public void testPossibleCloneOfCloneable() {
        final TypedNull<String> stringNull = new CloneableTypedNull<>(String.class);
        final TypedNull<String> stringNullClone = Cloneables.cloneIfPossible(stringNull);
        assertSame(String.class, stringNullClone.getType());
    }

    public void testCloneOfStringArray() {
        assertEquals(Arrays.asList(new String[]{"string"}), Arrays.asList(Cloneables.clone(new String[]{"string"})));
    }

    public void testCloneOfPrimitiveArray() {
        final int[] clone = Cloneables.clone(new int[]{1});
        assertEquals(1, clone.length);
        assertEquals(1, clone[0]);
    }

    public void testPossibleCloneOfNotCloneable() {
        final TypedNull<String> stringNull = new TypedNull<>(String.class);
        assertSame(stringNull, Cloneables.cloneIfPossible(stringNull));
    }

    static final class CloneableTypedNull<T> extends TypedNull<T> implements Cloneable {
        CloneableTypedNull(final Class<T> type) {
            super(type);
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    static final class UncloneableTypedNull<T> extends TypedNull<T> implements Cloneable {
        UncloneableTypedNull(final Class<T> type) {
            super(type);
        }
    }
}
