/*
 * Copyright (C) 2013, 2018 XStream Committers.
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


/**
 * @author J&ouml;rg Schaible
 */
public class EnumToStringConverterTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("big", BigEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);

        final Map<String, SimpleEnum> map = new HashMap<String, SimpleEnum>();
        map.put("0xff0000", SimpleEnum.RED);
        map.put("0x00ff00", SimpleEnum.GREEN);
        map.put("0x0000ff", SimpleEnum.BLUE);
        xstream.registerConverter(new EnumToStringConverter<SimpleEnum>(SimpleEnum.class, map));
        xstream.registerConverter(new EnumToStringConverter<BigEnum>(BigEnum.class));
        xstream.registerConverter(new EnumToStringConverter<PolymorphicEnum>(PolymorphicEnum.class));
    }

    public void testMapsEnumToProvidedStringValue() {
        final String expectedXml = "<simple>0x00ff00</simple>";
        final SimpleEnum in = SimpleEnum.GREEN;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testMapsEnumToStringDefaultValue() {
        final String expectedXml = "<big>C3</big>";
        final BigEnum in = BigEnum.C3;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testMapsToPolymorphicStringValue() {
        final String expectedXml = "<polymorphic>banana</polymorphic>";
        final PolymorphicEnum in = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

}
