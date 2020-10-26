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

package com.thoughtworks.xstream.converters.extended;

import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;

import junit.framework.TestCase;


/**
 * @author Paul Hammant
 */
public class ToStringConverterTest extends TestCase {

    public void testClaimsCanConvertRightType() throws NoSuchMethodException {
        final SingleValueConverter converter = new ToStringConverter(Foo.class);
        assertTrue(converter.canConvert(Foo.class));
    }

    public void testClaimsCantConvertWrongType() throws NoSuchMethodException {
        final SingleValueConverter converter = new ToStringConverter(Foo.class);
        assertFalse(converter.canConvert(Map.class));
    }

    public void testClaimsCantConvertWrongType2() {
        try {
            new ToStringConverter(Map.class);
            fail("shoulda barfed");
        } catch (final NoSuchMethodException e) {
            // expected.
        }
    }

    public void testCanConvertRightType() throws NoSuchMethodException {
        final SingleValueConverter converter = new ToStringConverter(Foo.class);
        assertTrue(converter.fromString("hello") instanceof Foo);
        assertEquals("hello", ((Foo)converter.fromString("hello")).foo);
    }

    public void testCanInnocentlyConvertWrongTypeToString() throws NoSuchMethodException {
        final SingleValueConverter converter = new ToStringConverter(Foo.class);
        assertEquals("whoa", converter.toString("whoa"));
    }

    public void testCantConvertWrongType() throws NoSuchMethodException {
        final SingleValueConverter converter = new ToStringConverter(BadFoo1.class);
        try {
            converter.fromString("whoa");
            fail("shoulda barfed");
        } catch (final ConversionException e) {
            assertTrue(e.getMessage().startsWith("Unable to target single String param constructor"));
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }

    public static class Foo {
        final String foo;

        public Foo(final String foo) {
            this.foo = foo;
        }

        @Override
        public String toString() {
            return foo;
        }
    }

    public static class BadFoo1 {
        public BadFoo1(final String string) {
            throw new NullPointerException("abc");
        }
    }

}
