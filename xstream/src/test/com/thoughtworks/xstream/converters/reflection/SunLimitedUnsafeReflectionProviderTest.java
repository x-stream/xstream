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

public class SunLimitedUnsafeReflectionProviderTest extends AbstractReflectionProviderTest {

    // inherits tests from superclass

    @Override
    public ReflectionProvider createReflectionProvider() {
        return new SunLimitedUnsafeReflectionProvider();
    }

    protected static class WithFinalFields {
        final String finalField;
        final int finalInt;
        final long finalLong;
        final short finalShort;
        final char finalChar;
        final byte finalByte;
        final float finalFloat;
        final double finalDouble;
        final boolean finalBoolean;

        private WithFinalFields() {
            finalField = null;
            finalChar = '\0';
            finalInt = 0;
            finalLong = 0;
            finalShort = 0;
            finalByte = 0;
            finalFloat = 0.0f;
            finalDouble = 0.0;
            finalBoolean = false;
        }

    }

    public void testCanWriteFinalFields() {
        final WithFinalFields thingy = new WithFinalFields();
        reflectionProvider.writeField(thingy, "finalField", "zero", WithFinalFields.class);
        assertEquals("zero", thingy.finalField);

        reflectionProvider.writeField(thingy, "finalInt", new Integer(1), WithFinalFields.class);
        assertEquals(1, thingy.finalInt);

        reflectionProvider.writeField(thingy, "finalLong", new Long(2), WithFinalFields.class);
        assertEquals(2, thingy.finalLong);

        reflectionProvider.writeField(thingy, "finalShort", new Short((short)3), WithFinalFields.class);
        assertEquals(3, thingy.finalShort);

        reflectionProvider.writeField(thingy, "finalChar", new Character('4'), WithFinalFields.class);
        assertEquals('4', thingy.finalChar);

        reflectionProvider.writeField(thingy, "finalByte", new Byte((byte)5), WithFinalFields.class);
        assertEquals(5, thingy.finalByte);

        reflectionProvider.writeField(thingy, "finalFloat", new Float(0.6), WithFinalFields.class);
        assertEquals(0.6f, thingy.finalFloat, 0.0);

        reflectionProvider.writeField(thingy, "finalDouble", new Double(0.7), WithFinalFields.class);
        assertEquals(0.7, thingy.finalDouble, 0.0);

        reflectionProvider.writeField(thingy, "finalBoolean", new Boolean(true), WithFinalFields.class);
        assertEquals(true, thingy.finalBoolean);

        reflectionProvider.writeField(thingy, "finalBoolean", new Boolean(false), null);
        assertEquals(false, thingy.finalBoolean);
    }

    protected static class Unistantiatable {
        {
            if (true) {
                throw new IllegalStateException("<init>");
            }
        }

        public Unistantiatable() {
            throw new IllegalStateException("ctor");
        }

        public Unistantiatable(final String s) {
            throw new IllegalStateException("ctor(String)");
        }
    }

    public void testCanInstantiateWithoutInitializer() {
        assertCanCreate(Unistantiatable.class);
    }
}
