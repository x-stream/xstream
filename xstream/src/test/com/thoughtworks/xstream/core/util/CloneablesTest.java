/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.core.util;

import java.util.Arrays;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

import junit.framework.TestCase;


public class CloneablesTest extends TestCase {
    public void testCloneOfCloneable() {
        final TypedNull<String> stringNull = new CloneableTypedNull<String>(String.class);
        final TypedNull<String> stringNullClone = Cloneables.clone(stringNull);
        assertSame(String.class, stringNullClone.getType());
    }

    public void testCloneOfNotCloneable() {
        final TypedNull<String> stringNull = new TypedNull<String>(String.class);
        assertNull(Cloneables.clone(stringNull));
    }

    public void testCloneOfUncloneable() {
        final TypedNull<String> stringNull = new UncloneableTypedNull<String>(String.class);
        try {
            Cloneables.clone(stringNull);
            fail("Thrown " + ObjectAccessException.class.getName() + " expected");
        } catch (final ObjectAccessException e) {
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
    }

    public void testPossibleCloneOfCloneable() {
        final TypedNull<String> stringNull = new CloneableTypedNull<String>(String.class);
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
        final TypedNull<String> stringNull = new TypedNull<String>(String.class);
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
