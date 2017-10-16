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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.security.AnyAnnotationTypePermission;
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
    private Map<String, Class<?>> classMap;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        classMap = new HashMap<String, Class<?>>();
        mapper = new SecurityMapper(new MapperWrapper(null) {
            @Override
            public Class realClass(final String elementName) {
                return classMap.get(elementName);
            }
        });
    }

    private void register(final Class<?>... types) {
        for (final Class<?> type : types) {
            classMap.put(type.getName(), type);
        }
    }

    public void testAnyType() {
        register(String.class, URL.class, List.class);
        mapper.addPermission(NoTypePermission.NONE);
        mapper.addPermission(AnyTypePermission.ANY);
        assertSame(String.class, mapper.realClass(String.class.getName()));
        assertSame(List.class, mapper.realClass(List.class.getName()));
        assertNull(mapper.realClass(null));
    }

    public void testNoType() {
        register(String.class, URL.class, List.class);
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
        register(String.class, Mapper.Null.class);
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
        register(String.class, int.class, Integer.class, char[].class, Character[].class);
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
        register(String.class, int.class, Integer.class, char[].class, Character[].class);
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
        register(String.class, List.class);
        mapper.addPermission(new ExplicitTypePermission(String.class.getName(), List.class.getName()));
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
        final Class<?> anonymous = new Object() {}.getClass();
        register(String.class, JVM.class, QuickWriter.class, Foo$_0.class, anonymous, DefaultClassMapperTest.class);
        mapper.addPermission(new RegExpTypePermission(".*Test", ".*\\.core\\..*", ".*SecurityMapperTest\\$.+"));
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
        final Class<?> anonymous = new Object() {}.getClass();
        register(String.class, JVM.class, QuickWriter.class, Foo$_0.class, Foo$_1.class, anonymous);
        mapper.addPermission(new WildcardTypePermission("**.*_0", "**.core.*", "**.SecurityMapperTest$?"));
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

    public void testNamesWithAnyAnnotation() {
        @XStreamAlias("AliasedFoo") class AliasedFoo {}
        class NonAnnotatedFoo {}
        @XStreamAlias("OtherAliasedFoo") class OtherAliasedFoo {
            class NestedNonAnnotatedFoo {}
        }
        final Class<?> anonymous = new Object() {}.getClass();
        register(String.class, JVM.class, QuickWriter.class,
                AliasedFoo.class, NonAnnotatedFoo.class,
                OtherAliasedFoo.class, OtherAliasedFoo.NestedNonAnnotatedFoo.class,
                anonymous);
        mapper.addPermission(new AnyAnnotationTypePermission());
        assertForbiddenClass(String.class);
        assertForbiddenClass(JVM.class);
        assertForbiddenClass(QuickWriter.class);
        assertAcceptedClass(AliasedFoo.class);
        assertForbiddenClass(NonAnnotatedFoo.class);
        assertAcceptedClass(OtherAliasedFoo.class);
        assertForbiddenClass(OtherAliasedFoo.NestedNonAnnotatedFoo.class);
        assertForbiddenClass(anonymous);
    }

    private void assertAcceptedClass(Class<?> type) {
        assertSame(type, mapper.realClass(type.getName()));
    }

    private void assertForbiddenClass(Class<?> type) {
        try {
            mapper.realClass(type.getName());
            fail("Thrown " + ForbiddenClassException.class.getName() + " expected");
        } catch (final ForbiddenClassException e) {
            assertEquals(type.getName(), e.getMessage());
        }
    }

}
