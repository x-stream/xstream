package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.objects.SampleDynamicProxy;
import com.thoughtworks.xstream.alias.ClassMapper;
import junit.framework.TestCase;

public class DefaultClassMapperTest extends TestCase {
    private ClassMapper mapper;

    protected void setUp() throws Exception {
        super.setUp();
        mapper = new DefaultClassMapper();
    }

    public void testAppendsArraySuffixOnArrays() {
        Class arrayCls = new String[0].getClass();
        assertEquals("java.lang.String-array", mapper.lookupName(arrayCls));

        mapper.alias("str", String.class, String.class);
        assertEquals("str-array", mapper.lookupName(arrayCls));

        mapper.alias("int", int.class, int.class);
        assertEquals("int-array", mapper.lookupName(new int[0].getClass()));
    }

    public void testAppendsMultipleArraySuffixesOnMultidimensionalArrays() {
        Class arrayCls = new String[0][0][0].getClass();
        assertEquals("java.lang.String-array-array-array", mapper.lookupName(arrayCls));

        mapper.alias("str", String.class, String.class);
        assertEquals("str-array-array-array", mapper.lookupName(arrayCls));

        mapper.alias("int", int.class, int.class);
        assertEquals("int-array-array-array", mapper.lookupName(new int[0][0][0].getClass()));
    }

    public void testPrefixesIllegalXmlElementNamesWithValue() {
        mapper = new DefaultClassMapper.OldClassMapper();
        Class proxyCls = SampleDynamicProxy.newInstance().getClass();
        String aliasedName = mapper.lookupName(proxyCls);
        assertTrue("Does not start with 'default-Proxy' : <" + aliasedName + ">",
                aliasedName.startsWith("default-Proxy"));
        assertEquals(proxyCls, mapper.lookupType(aliasedName));
    }

    public void testCreatesInstancesOfArrays() {
        Class arrayType = mapper.lookupType("java.lang.String-array");
        assertTrue(arrayType.isArray());
        assertEquals(String.class, arrayType.getComponentType());

        mapper.alias("str", String.class, String.class);
        arrayType = mapper.lookupType("str-array");
        assertTrue(arrayType.isArray());
        assertEquals(String.class, arrayType.getComponentType());

        mapper.alias("int", int.class, int.class);
        arrayType = mapper.lookupType("int-array");
        assertTrue(arrayType.isArray());
        assertEquals(int.class, arrayType.getComponentType());
    }

    public void testSupportsAllPrimitiveArrayTypes() {
        assertEquals(int.class, mapper.lookupType("int-array").getComponentType());
        assertEquals(short.class, mapper.lookupType("short-array").getComponentType());
        assertEquals(long.class, mapper.lookupType("long-array").getComponentType());
        assertEquals(char.class, mapper.lookupType("char-array").getComponentType());
        assertEquals(boolean.class, mapper.lookupType("boolean-array").getComponentType());
        assertEquals(float.class, mapper.lookupType("float-array").getComponentType());
        assertEquals(double.class, mapper.lookupType("double-array").getComponentType());
        assertEquals(byte.class, mapper.lookupType("byte-array").getComponentType());
    }

    public void testCreatesInstancesOfMultidimensionalArrays() {
        Class arrayType = mapper.lookupType("java.lang.String-array-array-array");
        assertTrue(arrayType.isArray());
        assertTrue(arrayType.getComponentType().isArray());
        assertTrue(arrayType.getComponentType().getComponentType().isArray());

        assertFalse(arrayType.getComponentType().getComponentType().getComponentType().isArray());
        assertEquals(String.class, arrayType.getComponentType().getComponentType().getComponentType());

        Class primitiveArrayType = mapper.lookupType("int-array-array-array");
        assertTrue(primitiveArrayType.isArray());
        assertTrue(primitiveArrayType.getComponentType().isArray());
        assertTrue(primitiveArrayType.getComponentType().getComponentType().isArray());

        assertFalse(primitiveArrayType.getComponentType().getComponentType().getComponentType().isArray());
        assertEquals(int.class, primitiveArrayType.getComponentType().getComponentType().getComponentType());
    }
}
