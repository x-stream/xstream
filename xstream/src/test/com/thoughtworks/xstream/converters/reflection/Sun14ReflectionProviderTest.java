package com.thoughtworks.xstream.converters.reflection;

import junit.framework.TestCase;

public class Sun14ReflectionProviderTest extends TestCase {

    private ReflectionProvider objectFactory;

    protected void setUp() throws Exception {
        super.setUp();
        objectFactory = new Sun14ReflectionProvider();
    }

    public void testFinalField() {
        WithFinalFields thingy = new WithFinalFields();
        objectFactory.writeField(thingy, "finalField", "zero");
        assertEquals("zero", thingy.finalField);

        objectFactory.writeField(thingy, "finalInt", new Integer(1));
        assertEquals(1, thingy.finalInt);

        objectFactory.writeField(thingy, "finalLong", new Long(2));
        assertEquals(2, thingy.finalLong);

        objectFactory.writeField(thingy, "finalShort", new Short((short) 3));
        assertEquals(3, thingy.finalShort);

        objectFactory.writeField(thingy, "finalChar", new Character('4'));
        assertEquals('4', thingy.finalChar);

        objectFactory.writeField(thingy, "finalByte", new Byte((byte) 5));
        assertEquals(5, thingy.finalByte);

        objectFactory.writeField(thingy, "finalFloat", new Float(0.6));
        assertEquals(0.6f, thingy.finalFloat, 0.0);

        objectFactory.writeField(thingy, "finalDouble", new Double(0.7));
        assertEquals(0.7, thingy.finalDouble, 0.0);

        objectFactory.writeField(thingy, "finalBoolean", new Boolean(true));
        assertEquals(true, thingy.finalBoolean);
    }

    private static class WithFinalFields {
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

}
