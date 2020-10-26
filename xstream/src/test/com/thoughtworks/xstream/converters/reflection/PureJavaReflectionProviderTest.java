/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.converters.reflection;

import java.io.Serializable;


public class PureJavaReflectionProviderTest extends AbstractReflectionProviderTest {

    // inherits tests from superclass

    @Override
    public ReflectionProvider createReflectionProvider() {
        return new PureJavaReflectionProvider();
    }

    // ---------------------------------------------------------

    private static class PrivateStaticInnerClass {}

    public void testCanCreatePrivateStaticInnerClasses() {
        assertCanCreate(PrivateStaticInnerClass.class);
    }

    // ---------------------------------------------------------

    public class PublicNonStaticInnerClass {}

    private class PrivateNonStaticInnerClass {}

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
        } catch (final UnsupportedOperationException expectedException) {
            // good
        }
    }

    // ---------------------------------------------------------

    public static class WithoutDefaultConstructor {
        public WithoutDefaultConstructor(final String arg) {
        }
    }

    public void testIsNotCapableOfConstructingClassesWithoutDefaultConstructor() {
        assertCannotCreate(WithoutDefaultConstructor.class);
    }

    // ---------------------------------------------------------

    public static class WithPrivateDefaultConstructor {
        private WithPrivateDefaultConstructor(final String thing) {
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
        private static final long serialVersionUID = 200410L;
        @SuppressWarnings("unused")
        private int field1, field2;

        @SuppressWarnings("unused")
        public SerializableWithoutDefaultConstructor(final String thing) {
            throw new UnsupportedOperationException("constructor called");
        }
    }

    private class NonStaticSerializableWithoutDefaultConstructor implements Serializable {
        private static final long serialVersionUID = 200410L;

        @SuppressWarnings("unused")
        public NonStaticSerializableWithoutDefaultConstructor(final String thing) {
            throw new UnsupportedOperationException("constructor called");
        }
    }

    public void testBypassesConstructorForSerializableObjectsWithNoDefaultConstructor() {
        assertCanCreate(SerializableWithoutDefaultConstructor.class);
        assertCanCreate(NonStaticSerializableWithoutDefaultConstructor.class);
    }

    // ---------------------------------------------------------

    public static class WithFinalField {
        private final String s;

        private WithFinalField() {
            s = "";
        }

        String getFinal() {
            return s;
        }
    }

    public void testCanCreateWithFinalField() {
        assertCanCreate(WithFinalField.class);
    }

    public void testWriteToFinalField() {
        final Object result = reflectionProvider.newInstance(WithFinalField.class);
        reflectionProvider.writeField(result, "s", "foo", WithFinalField.class);
        final WithFinalField withFinalField = (WithFinalField)result;
        assertEquals("foo", withFinalField.getFinal());
    }
}
