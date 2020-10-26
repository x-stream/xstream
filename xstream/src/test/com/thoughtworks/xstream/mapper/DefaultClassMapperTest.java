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

package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.core.ClassLoaderReference;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;

import junit.framework.TestCase;


public class DefaultClassMapperTest extends TestCase {
    private Mapper mapper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mapper = new ArrayMapper(new DefaultMapper(new ClassLoaderReference(new CompositeClassLoader())));
    }

    public void testAppendsArraySuffixOnArrays() {
        final Class<?> arrayCls = new String[0].getClass();
        assertEquals("java.lang.String-array", mapper.serializedClass(arrayCls));
    }

    public void testAppendsMultipleArraySuffixesOnMultidimensionalArrays() {
        final Class<?> arrayCls = new String[0][0][0].getClass();
        assertEquals("java.lang.String-array-array-array", mapper.serializedClass(arrayCls));
    }

    public void testCreatesInstancesOfArrays() {
        final Class<?> arrayType = mapper.realClass("java.lang.String-array");
        assertTrue(arrayType.isArray());
        assertEquals(String.class, arrayType.getComponentType());
    }

    public void testSupportsAllPrimitiveArrayTypes() {
        assertEquals(int.class, mapper.realClass("int-array").getComponentType());
        assertEquals(short.class, mapper.realClass("short-array").getComponentType());
        assertEquals(long.class, mapper.realClass("long-array").getComponentType());
        assertEquals(char.class, mapper.realClass("char-array").getComponentType());
        assertEquals(boolean.class, mapper.realClass("boolean-array").getComponentType());
        assertEquals(float.class, mapper.realClass("float-array").getComponentType());
        assertEquals(double.class, mapper.realClass("double-array").getComponentType());
        assertEquals(byte.class, mapper.realClass("byte-array").getComponentType());
    }

    public void testCreatesInstancesOfMultidimensionalArrays() {
        final Class<?> arrayType = mapper.realClass("java.lang.String-array-array-array");
        assertTrue(arrayType.isArray());
        assertTrue(arrayType.getComponentType().isArray());
        assertTrue(arrayType.getComponentType().getComponentType().isArray());

        assertFalse(arrayType.getComponentType().getComponentType().getComponentType().isArray());
        assertEquals(String.class, arrayType.getComponentType().getComponentType().getComponentType());

        final Class<?> primitiveArrayType = mapper.realClass("int-array-array-array");
        assertTrue(primitiveArrayType.isArray());
        assertTrue(primitiveArrayType.getComponentType().isArray());
        assertTrue(primitiveArrayType.getComponentType().getComponentType().isArray());

        assertFalse(primitiveArrayType.getComponentType().getComponentType().getComponentType().isArray());
        assertEquals(int.class, primitiveArrayType.getComponentType().getComponentType().getComponentType());
    }
}
