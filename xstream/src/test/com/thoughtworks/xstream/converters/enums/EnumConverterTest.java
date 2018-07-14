/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2018 XStream Committers.
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


/**
 * @author Joe Walnes
 * @author Bryan Coleman
 */
public class EnumConverterTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);
    }

    public void testRepresentsEnumAsSingleStringValue() {
        final String expectedXml = "<simple>GREEN</simple>";
        final SimpleEnum in = SimpleEnum.GREEN;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testRepresentsPolymorphicEnumAsSingleStringValue() {
        final String expectedXml = "<polymorphic>B</polymorphic>";
        final PolymorphicEnum in = PolymorphicEnum.B;
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
        out = xstream.<PolymorphicEnum>fromXML(xstream.toXML(in));
        assertEquals("apple", ((Fruit)out).fruit()); // see Bug ID: 6522780

        in = PolymorphicEnum.B;
        out = xstream.<PolymorphicEnum>fromXML(xstream.toXML(in));
        assertEquals("banana", ((Fruit)out).fruit()); // see Bug ID: 6522780
    }

}
