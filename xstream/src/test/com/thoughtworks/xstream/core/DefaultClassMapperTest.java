package com.thoughtworks.xstream.core;

import com.thoughtworks.acceptance.objects.SampleDynamicProxy;
import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.mapper.XmlFriendlyMapper;
import com.thoughtworks.xstream.core.util.CompositeClassLoader;
import junit.framework.TestCase;

public class DefaultClassMapperTest extends TestCase {
    private ClassMapper mapper;

    protected void setUp() throws Exception {
        super.setUp();
        mapper = new DefaultClassMapper();
    }

    public void testAppendsArraySuffixOnArrays() {
        Class arrayCls = new String[0].getClass();
        assertEquals("java.lang.String-array", mapper.serializedClass(arrayCls));

        mapper.alias("str", String.class, String.class);
        assertEquals("str-array", mapper.serializedClass(arrayCls));

        mapper.alias("int", int.class, int.class);
        assertEquals("int-array", mapper.serializedClass(new int[0].getClass()));
    }

    public void testAppendsMultipleArraySuffixesOnMultidimensionalArrays() {
        Class arrayCls = new String[0][0][0].getClass();
        assertEquals("java.lang.String-array-array-array", mapper.serializedClass(arrayCls));

        mapper.alias("str", String.class, String.class);
        assertEquals("str-array-array-array", mapper.serializedClass(arrayCls));

        mapper.alias("int", int.class, int.class);
        assertEquals("int-array-array-array", mapper.serializedClass(new int[0][0][0].getClass()));
    }

    public void testPrefixesIllegalXmlElementNamesWithValue() {
        mapper = new XmlFriendlyMapper(new com.thoughtworks.xstream.mapper.DefaultMapper(new CompositeClassLoader()));
        Class proxyCls = SampleDynamicProxy.newInstance().getClass();
        String aliasedName = mapper.serializedClass(proxyCls);
        assertTrue("Does not start with 'default-Proxy' : <" + aliasedName + ">",
                aliasedName.startsWith("default-Proxy"));
        assertEquals(proxyCls, mapper.realClass(aliasedName));
    }

    public void testCreatesInstancesOfArrays() {
        Class arrayType = mapper.realClass("java.lang.String-array");
        assertTrue(arrayType.isArray());
        assertEquals(String.class, arrayType.getComponentType());

        mapper.alias("str", String.class, String.class);
        arrayType = mapper.realClass("str-array");
        assertTrue(arrayType.isArray());
        assertEquals(String.class, arrayType.getComponentType());

        mapper.alias("int", int.class, int.class);
        arrayType = mapper.realClass("int-array");
        assertTrue(arrayType.isArray());
        assertEquals(int.class, arrayType.getComponentType());
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
