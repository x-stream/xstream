package com.thoughtworks.xstream.converters.reflection;

public class PureJavaReflectionProviderTest extends AbstractReflectionProviderTest {

    // inherits tests from superclass

    public ReflectionProvider createReflectionProvider() {
        return new PureJavaReflectionProvider();
    }

    public void testCanCreatePrivateStaticInnerClasses() {
        assertCanCreate(PrivateStaticInnerClass.class);
    }

    public void testIsNotCapableOfConstructingNonStaticInnerClasses() {
        assertCannotCreate(PublicNonStaticInnerClass.class);
        assertCannotCreate(PrivateNonStaticInnerClass.class);
    }

    public void testUnfortunatelyExecutesCodeInsideConstructor() {
        try {
            reflectionProvider.newInstance(WithConstructorThatDoesStuff.class);
            fail("Expected code in constructor to be executed and throw an exception");
        } catch (UnsupportedOperationException expectedException) {
            // good
        }
    }

    public void testIsNotCapableOfConstructingClassesWithoutDefaultConstructor() {
        assertCannotCreate(WithoutDefaultConstructor.class);
    }

    public void testUsesPrivateConstructorIfNecessary() {
        assertCanCreate(WithPrivateDefaultConstructor.class);
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

    public static class WithPrivateDefaultConstructor {
        private WithPrivateDefaultConstructor(String thing) {
            throw new UnsupportedOperationException("wrong constructor called");
        }

        private WithPrivateDefaultConstructor() {
        }
    }
}

