package com.thoughtworks.xstream.core;

import junit.framework.TestCase;

public class DefaultClassMapperTest extends TestCase {
    private DefaultClassMapper mapper;

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

    // @TODO
    public void TODO_testArrayClassesCanBeCreated() {
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
        assertEquals(Integer.class, arrayType.getComponentType());
    }
}
