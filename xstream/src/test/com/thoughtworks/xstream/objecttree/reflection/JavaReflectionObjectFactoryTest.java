package com.thoughtworks.xstream.objecttree.reflection;

import com.thoughtworks.xstream.objecttree.ObjectAccessException;
import junit.framework.TestCase;

public class JavaReflectionObjectFactoryTest extends TestCase {
    private ObjectFactory objectFactory;

    protected void setUp() throws Exception {
        super.setUp();
        objectFactory = new JavaReflectionObjectFactory();
    }

    public void testConstructsStandardClass() {
        assertCanCreate(OuterClass.class);
    }

    public void testConstructsStaticInnerClass() {
        assertCanCreate(PublicStaticInnerClass.class);
    }

    public void testNotCapableOfConstructingNonPublicAndNonStaticInnerClasses() {
        assertCannotCreate(PrivateStaticInnerClass.class);
        assertCannotCreate(PublicNonStaticInnerClass.class);
        assertCannotCreate(PrivateNonStaticInnerClass.class);
    }

    public void testUnfortunatelyExecutesCodeInsideConstructor() {
        try {
            objectFactory.create(WithConstructorThatDoesStuff.class);
            fail("Expected code in constructor to be executed and throw an exception");
        } catch (UnsupportedOperationException expectedException) {
            // good
        }
    }

    public void testIsNotCapableOfConstructingClassesWithoutDefault() {
        assertCannotCreate(WithoutDefaultConstructor.class);
    }

    private void assertCanCreate(Class type) {
        Object result = objectFactory.create(type);
        assertEquals(type, result.getClass());
    }

    private void assertCannotCreate(Class type) {
        try {
            objectFactory.create(type);
            fail("Should not have been able to create " + type);
        } catch (ObjectAccessException goodException) {
        }
    }

    public static class PublicStaticInnerClass {
    }

    private static class PrivateStaticInnerClass {
    }

    public class PublicNonStaticInnerClass {
    }

    private class PrivateNonStaticInnerClass {
    }

    public static class WithConstructorThatDoesStuff {
        public WithConstructorThatDoesStuff() {
            throw new UnsupportedOperationException("constructor called");
        }
    }

    public static class WithoutDefaultConstructor {
        public WithoutDefaultConstructor(String arg) {
        }
    }

}

class OuterClass {
}

