package com.thoughtworks.xstream.converters.reflection;

public class PureJavaReflectionProviderTest extends AbstractReflectionProviderTest {

    // inherits tests from superclass

    public ReflectionProvider createReflectionProvider() {
        return new PureJavaReflectionProvider();
    }

    public void testNotCapableOfConstructingNonPublicAndNonStaticInnerClasses() {
        assertCannotCreate(PrivateStaticInnerClass.class);
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

    public void testIsNotCapableOfConstructingClassesWithoutDefault() {
        assertCannotCreate(WithoutDefaultConstructor.class);
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

