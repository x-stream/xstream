package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.core.util.CompositeClassLoader;

import junit.framework.TestCase;

public class DefaultClassMapperTest extends TestCase {
    private Mapper mapper;

    protected void setUp() throws Exception {
        super.setUp();
        mapper = new ArrayMapper(new DefaultMapper(new CompositeClassLoader()));
    }

    public void testAppendsArraySuffixOnArrays() {
        Class arrayCls = new String[0].getClass();
        assertEquals("java.lang.String-array", mapper.serializedClass(arrayCls));
    }

    public void testAppendsMultipleArraySuffixesOnMultidimensionalArrays() {
        Class arrayCls = new String[0][0][0].getClass();
        assertEquals("java.lang.String-array-array-array", mapper.serializedClass(arrayCls));
    }

    public void testCreatesInstancesOfArrays() {
        Class arrayType = mapper.realClass("java.lang.String-array");
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
        Class arrayType = mapper.realClass("java.lang.String-array-array-array");
        assertTrue(arrayType.isArray());
        assertTrue(arrayType.getComponentType().isArray());
        assertTrue(arrayType.getComponentType().getComponentType().isArray());

        assertFalse(arrayType.getComponentType().getComponentType().getComponentType().isArray());
        assertEquals(String.class, arrayType.getComponentType().getComponentType().getComponentType());

        Class primitiveArrayType = mapper.realClass("int-array-array-array");
        assertTrue(primitiveArrayType.isArray());
        assertTrue(primitiveArrayType.getComponentType().isArray());
        assertTrue(primitiveArrayType.getComponentType().getComponentType().isArray());

        assertFalse(primitiveArrayType.getComponentType().getComponentType().getComponentType().isArray());
        assertEquals(int.class, primitiveArrayType.getComponentType().getComponentType().getComponentType());
    }
}
