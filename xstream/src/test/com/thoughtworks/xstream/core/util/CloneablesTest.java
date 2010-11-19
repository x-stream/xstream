/*
 * Copyright (C) 2010 XStream Committers.
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
        final TypedNull stringNull = new CloneableTypedNull(String.class);
        final TypedNull stringNullClone = (TypedNull)Cloneables.clone(stringNull);
        assertSame(String.class, stringNullClone.getType());
    }

    public void testCloneOfNotCloneable() {
        final TypedNull stringNull = new TypedNull(String.class);
        assertNull(Cloneables.clone(stringNull));
    }

    public void testCloneOfUncloneable() {
        final TypedNull stringNull = new UncloneableTypedNull(String.class);
        try {
            Cloneables.clone(stringNull);
            fail("Thrown " + ObjectAccessException.class.getName() + " expected");
        } catch (final ObjectAccessException e) {
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
    }

    public void testPossibleCloneOfCloneable() {
        final TypedNull stringNull = new CloneableTypedNull(String.class);
        final TypedNull stringNullClone = (TypedNull)Cloneables.cloneIfPossible(stringNull);
        assertSame(String.class, stringNullClone.getType());
    }

    public void testCloneOfStringArray() {
        assertEquals(
                Arrays.asList(new String[]{"string"}),
                Arrays.asList((String[])Cloneables.clone(new String[]{"string"})));
    }

    public void testCloneOfPrimitiveArray() {
        int[] clone = (int[])Cloneables.clone(new int[]{1});
        assertEquals(1, clone.length);
        assertEquals(1, clone[0]);
    }

    public void testPossibleCloneOfNotCloneable() {
        final TypedNull stringNull = new TypedNull(String.class);
        assertSame(stringNull, Cloneables.cloneIfPossible(stringNull));
    }

    static final class CloneableTypedNull extends TypedNull implements Cloneable {
        CloneableTypedNull(final Class type) {
            super(type);
        }

        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    static final class UncloneableTypedNull extends TypedNull implements Cloneable {
        UncloneableTypedNull(final Class type) {
            super(type);
        }
    }
}
