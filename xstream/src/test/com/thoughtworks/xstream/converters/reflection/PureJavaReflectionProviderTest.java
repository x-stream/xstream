/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import java.io.Serializable;

public class PureJavaReflectionProviderTest extends AbstractReflectionProviderTest {

    // inherits tests from superclass

    public ReflectionProvider createReflectionProvider() {
        return new PureJavaReflectionProvider();
    }


    // ---------------------------------------------------------


    private static class PrivateStaticInnerClass {
    }

    public void testCanCreatePrivateStaticInnerClasses() {
        assertCanCreate(PrivateStaticInnerClass.class);
    }


    // ---------------------------------------------------------


    public class PublicNonStaticInnerClass {
    }

    private class PrivateNonStaticInnerClass {
    }

    public void testIsNotCapableOfConstructingNonStaticInnerClasses() {
        assertCannotCreate(PublicNonStaticInnerClass.class);
        assertCannotCreate(PrivateNonStaticInnerClass.class);
    }


    // ---------------------------------------------------------


    public static class WithConstructorThatDoesStuff {
        public WithConstructorThatDoesStuff() {
            throw new UnsupportedOperationException("constructor called");
        }
    }

    public void testUnfortunatelyExecutesCodeInsideConstructor() {
        try {
            reflectionProvider.newInstance(WithConstructorThatDoesStuff.class);
            fail("Expected code in constructor to be executed and throw an exception");
        } catch (UnsupportedOperationException expectedException) {
            // good
        }
    }


    // ---------------------------------------------------------


    public static class WithoutDefaultConstructor {
        public WithoutDefaultConstructor(String arg) {
        }
    }

    public void testIsNotCapableOfConstructingClassesWithoutDefaultConstructor() {
        assertCannotCreate(WithoutDefaultConstructor.class);
    }


    // ---------------------------------------------------------


    public static class WithPrivateDefaultConstructor {
        private WithPrivateDefaultConstructor(String thing) {
            throw new UnsupportedOperationException("wrong constructor called");
        }

        private WithPrivateDefaultConstructor() {
        }
    }

    public void testUsesPrivateConstructorIfNecessary() {
        assertCanCreate(WithPrivateDefaultConstructor.class);
    }


    // ---------------------------------------------------------


    private static class SerializableWithoutDefaultConstructor implements Serializable {
        private int field1, field2;
        public SerializableWithoutDefaultConstructor(String thing) {
            throw new UnsupportedOperationException("constructor called");
        }
    }

    private class NonStaticSerializableWithoutDefaultConstructor implements Serializable {
        public NonStaticSerializableWithoutDefaultConstructor(String thing) {
            throw new UnsupportedOperationException("constructor called");
        }
    }

    public void testBypassesConstructorForSerializableObjectsWithNoDefaultConstructor() {
        assertCanCreate(SerializableWithoutDefaultConstructor.class);
        assertCanCreate(NonStaticSerializableWithoutDefaultConstructor.class);
    }


}

