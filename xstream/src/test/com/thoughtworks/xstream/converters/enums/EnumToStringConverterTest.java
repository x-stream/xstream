/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 14. March 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.enums;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;


// ***** READ THIS *****
// This class will only compile with JDK 1.5.0 or above as it test Java enums.
// If you are using an earlier version of Java, just don't try to build this class. XStream should work fine without it.

/**
 * @author J&ouml;rg Schaible
 */
public class EnumToStringConverterTest extends TestCase {

    private XStream xstream;

    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("big", BigEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);

        Map<String, SimpleEnum> map = new HashMap<String, SimpleEnum>();
        map.put("0xff0000", SimpleEnum.RED);
        map.put("0x00ff00", SimpleEnum.GREEN);
        map.put("0x0000ff", SimpleEnum.BLUE);
        xstream.registerConverter(new EnumToStringConverter<SimpleEnum>(SimpleEnum.class, map));
        xstream.registerConverter(new EnumToStringConverter<BigEnum>(BigEnum.class));
        xstream.registerConverter(new EnumToStringConverter<PolymorphicEnum>(
            PolymorphicEnum.class));
    }

    public void testMapsEnumToProvidedStringValue() {
        String expectedXml = "<simple>0x00ff00</simple>";
        SimpleEnum in = SimpleEnum.GREEN;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testMapsEnumToStringDefaultValue() {
        String expectedXml = "<big>C3</big>";
        BigEnum in = BigEnum.C3;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testMapsToPolymorphicStringValue() {
        String expectedXml = "<polymorphic>banana</polymorphic>";
        PolymorphicEnum in = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

}
