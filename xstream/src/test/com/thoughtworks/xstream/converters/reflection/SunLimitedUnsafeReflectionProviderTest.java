/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * Created on 08. January 2014 by Joerg Schaible, factored out from SunUnsafeReflectionProviderTest
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

        reflectionProvider.writeField(thingy, "finalInt", 1, WithFinalFields.class);
        assertEquals(1, thingy.finalInt);

        reflectionProvider.writeField(thingy, "finalLong", new Long(2), WithFinalFields.class);
        assertEquals(2, thingy.finalLong);

        reflectionProvider.writeField(thingy, "finalShort", (short)3, WithFinalFields.class);
        assertEquals(3, thingy.finalShort);

        reflectionProvider.writeField(thingy, "finalChar", '4', WithFinalFields.class);
        assertEquals('4', thingy.finalChar);

        reflectionProvider.writeField(thingy, "finalByte", (byte)5, WithFinalFields.class);
        assertEquals(5, thingy.finalByte);

        reflectionProvider.writeField(thingy, "finalFloat", new Float(0.6), WithFinalFields.class);
        assertEquals(0.6f, thingy.finalFloat, 0.0);

        reflectionProvider.writeField(thingy, "finalDouble", 0.7, WithFinalFields.class);
        assertEquals(0.7, thingy.finalDouble, 0.0);

        reflectionProvider.writeField(thingy, "finalBoolean", true, WithFinalFields.class);
        assertEquals(true, thingy.finalBoolean);

        reflectionProvider.writeField(thingy, "finalBoolean", false, null);
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
