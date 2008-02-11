/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 18. March 2005 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

import java.util.Arrays;


// ***** READ THIS *****
// This class will only compile with JDK 1.5.0 or above as it test Java enums.
// If you are using an earlier version of Java, just don't try to build this class. XStream
// should work fine without it.

/**
 * @author Joe Walnes
 * @author Bryan Coleman
 */
public class EnumConverterTest extends TestCase {

    private XStream xstream;

    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);
    }

    public void testRepresentsEnumAsSingleStringValue() {
        String expectedXml = "<simple>GREEN</simple>";
        SimpleEnum in = SimpleEnum.GREEN;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testRepresentsPolymorphicEnumAsSingleStringValue() {
        String expectedXml = "<polymorphic>B</polymorphic>";
        PolymorphicEnum in = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testDeserializedEnumIsTheSameNotJustEqual() {
        assertSame(SimpleEnum.GREEN, xstream.fromXML(xstream.toXML(SimpleEnum.GREEN)));
        assertSame(PolymorphicEnum.B, xstream.fromXML(xstream.toXML(PolymorphicEnum.B)));
    }

    public void testResolvesSpecializedPolymorphicEnum() {
        PolymorphicEnum in;
        PolymorphicEnum out;

        in = PolymorphicEnum.A;
        out = (PolymorphicEnum)xstream.fromXML(xstream.toXML(in));
        assertEquals("apple", out.fruit());

        in = PolymorphicEnum.B;
        out = (PolymorphicEnum)xstream.fromXML(xstream.toXML(in));
        assertEquals("banana", out.fruit());
    }

    static class Bowl {
        Fruit fruit;
    }

    public void testSupportsDefaultImplementationToBeAnEnum() {
        xstream.alias("bowl", Bowl.class);
        xstream.addDefaultImplementation(PolymorphicEnum.class, Fruit.class);
        String expectedXml = "" // force format
            + "<bowl>\n"
            + "  <fruit>B</fruit>\n"
            + "</bowl>";
        Bowl in = new Bowl();
        in.fruit = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        Bowl out = (Bowl)xstream.fromXML(expectedXml);
        assertSame(in.fruit, PolymorphicEnum.B);
    }

    public void testEnumsAreImmutable() {
        SimpleEnum[] in = new SimpleEnum[]{SimpleEnum.GREEN, SimpleEnum.GREEN};
        String expectedXml = ""
            + "<simple-array>\n"
            + "  <simple>GREEN</simple>\n"
            + "  <simple>GREEN</simple>\n"
            + "</simple-array>";
        assertEquals(expectedXml, xstream.toXML(in));
        assertTrue(Arrays.equals(in, (SimpleEnum[])xstream.fromXML(expectedXml)));
    }
}
