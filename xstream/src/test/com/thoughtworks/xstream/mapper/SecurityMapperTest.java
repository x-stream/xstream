/*
 * Copyright (C) 2014 XStream Committers.
 * All rights reserved.
 *
 * Created on 09. January 2014 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.ArrayTypePermission;
import com.thoughtworks.xstream.security.ExplicitTypePermission;
import com.thoughtworks.xstream.security.ForbiddenClassException;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import com.thoughtworks.xstream.security.RegExpTypePermission;
import com.thoughtworks.xstream.security.TypePermission;
import com.thoughtworks.xstream.security.WildcardTypePermission;

import junit.framework.TestCase;


/**
 * Tests the {@link SecurityMapper} and the {@link TypePermission} implementations.
 * 
 * @author J&ouml;rg Schaible
 */
public class SecurityMapperTest extends TestCase {

    private SecurityMapper mapper;
    private Map classMap;

    protected void setUp() throws Exception {
        super.setUp();

        classMap = new HashMap();
        mapper = new SecurityMapper(new MapperWrapper(null) {
            public Class realClass(final String elementName) {
                return (Class)classMap.get(elementName);
            }
        });
    }

    private void register(final Class[] types) {
        for (int i = 0; i < types.length; ++i) {
            classMap.put(types[i].getName(), types[i]);
        }
    }

    public void testAnyType() {
        register(new Class[]{String.class, URL.class, List.class});
        mapper.addPermission(NoTypePermission.NONE);
        mapper.addPermission(AnyTypePermission.ANY);
        assertSame(String.class, mapper.realClass(String.class.getName()));
        assertSame(List.class, mapper.realClass(List.class.getName()));
        assertNull(mapper.realClass(null));
    }

    public void testNoType() {
        register(new Class[]{String.class, URL.class, List.class});
        mapper.addPermission(NoTypePermission.NONE);
        try {
            mapper.realClass(String.class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(String.class.getName(), e.getMessage());
        }
        try {
            mapper.realClass(null);
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals("null", e.getMessage());
        }
    }

    public void testNullType() {
        register(new Class[]{String.class, Mapper.Null.class});
        mapper.addPermission(NullPermission.NULL);
        assertSame(Mapper.Null.class, mapper.realClass(Mapper.Null.class.getName()));
        assertNull(mapper.realClass(null));
        try {
            mapper.realClass(String.class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(String.class.getName(), e.getMessage());
        }
    }

    public void testPrimitiveTypes() {
        register(new Class[]{String.class, int.class, Integer.class, char[].class, Character[].class});
        mapper.addPermission(PrimitiveTypePermission.PRIMITIVES);
        assertSame(int.class, mapper.realClass(int.class.getName()));
        assertSame(Integer.class, mapper.realClass(Integer.class.getName()));
        try {
            mapper.realClass(String.class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(String.class.getName(), e.getMessage());
        }
        try {
            mapper.realClass(null);
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals("null", e.getMessage());
        }
        try {
            mapper.realClass(char[].class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(char[].class.getName(), e.getMessage());
        }
    }

    public void testArrayTypes() {
        register(new Class[]{String.class, int.class, Integer.class, char[].class, Character[].class});
        mapper.addPermission(ArrayTypePermission.ARRAYS);
        assertSame(char[].class, mapper.realClass(char[].class.getName()));
        assertSame(Character[].class, mapper.realClass(Character[].class.getName()));
        try {
            mapper.realClass(String.class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(String.class.getName(), e.getMessage());
        }
        try {
            mapper.realClass(null);
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals("null", e.getMessage());
        }
        try {
            mapper.realClass(int.class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(int.class.getName(), e.getMessage());
        }
    }

    public void testExplicitTypes() {
        register(new Class[]{String.class, List.class});
        mapper.addPermission(new ExplicitTypePermission(new String[]{String.class.getName(), List.class.getName()}));
        assertSame(String.class, mapper.realClass(String.class.getName()));
        assertSame(List.class, mapper.realClass(List.class.getName()));
        try {
            mapper.realClass(null);
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals("null", e.getMessage());
        }
    }

    public void testNamesWithRegExps() {
        class Foo$_0 {}
        final Class anonymous = new Object() {}.getClass();
        register(new Class[]{
            String.class, JVM.class, QuickWriter.class, Foo$_0.class, anonymous, DefaultClassMapperTest.class});
        mapper.addPermission(new RegExpTypePermission(new String[]{
            ".*Test", ".*\\.core\\..*", ".*SecurityMapperTest\\$.+"}));
        assertSame(DefaultClassMapperTest.class, mapper.realClass(DefaultClassMapperTest.class.getName()));
        assertSame(JVM.class, mapper.realClass(JVM.class.getName()));
        assertSame(QuickWriter.class, mapper.realClass(QuickWriter.class.getName()));
        assertSame(Foo$_0.class, mapper.realClass(Foo$_0.class.getName()));
        assertSame(anonymous, mapper.realClass(anonymous.getName()));
        try {
            mapper.realClass(String.class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(String.class.getName(), e.getMessage());
        }
    }

    public void testNamesWithWildcardPatterns() {
        class Foo$_0 {}
        class Foo$_1 {}
        final Class anonymous = new Object() {}.getClass();
        register(new Class[]{String.class, JVM.class, QuickWriter.class, Foo$_0.class, Foo$_1.class, anonymous});
        mapper
            .addPermission(new WildcardTypePermission(new String[]{"**.*_0", "**.core.*", "**.SecurityMapperTest$?"}));
        assertSame(JVM.class, mapper.realClass(JVM.class.getName()));
        assertSame(Foo$_0.class, mapper.realClass(Foo$_0.class.getName()));
        assertSame(anonymous, mapper.realClass(anonymous.getName()));
        try {
            mapper.realClass(String.class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(String.class.getName(), e.getMessage());
        }
        try {
            mapper.realClass(QuickWriter.class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(QuickWriter.class.getName(), e.getMessage());
        }
        try {
            mapper.realClass(Foo$_1.class.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(Foo$_1.class.getName(), e.getMessage());
        }
    }
}
