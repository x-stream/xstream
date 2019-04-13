/*
 * Copyright (C) 2008, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 08. October 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.enums.EnumMapperTest.TypeWithEnums;

import junit.framework.TestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class EnumCustomConverterTest extends TestCase {

    private XStream xstream;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream = new XStream();
        xstream.allowTypes(TypeWithEnums.class);
        xstream.allowTypesByWildcard(this.getClass().getName() + "$*");
        xstream.alias("simple", SimpleEnum.class);
        xstream.alias("polymorphic", PolymorphicEnum.class);
    }

    public void testCanBeUsedDirectly() {
        xstream.registerConverter(new PolymorphicEnumConverter<>(PolymorphicEnum.class));
        final String expectedXml = "<polymorphic>b</polymorphic>";
        final PolymorphicEnum in = PolymorphicEnum.B;
        assertEquals(expectedXml, xstream.toXML(in));
        assertEquals(in, xstream.fromXML(expectedXml));
    }

    public void testCanBeUsedForMember() {
        xstream.registerConverter(new PolymorphicEnumConverter<>(PolymorphicEnum.class));
        xstream.alias("type", TypeWithEnums.class);
        xstream.autodetectAnnotations(true);
        final String expectedXml = "" // force format
            + "<type simple=\"GREEN\">\n"
            + "  <poly>b</poly>\n"
            + "  <big>C3</big>\n"
            + "</type>";
        final TypeWithEnums in = new TypeWithEnums();
        in.poly = PolymorphicEnum.B;
        in.simple = SimpleEnum.GREEN;
        in.big = BigEnum.C3;
        assertEquals(expectedXml, xstream.toXML(in));
        final TypeWithEnums out = xstream.<TypeWithEnums>fromXML(expectedXml);
        assertSame(out.poly, PolymorphicEnum.B);
        assertSame(out.simple, SimpleEnum.GREEN);
    }

    public void testCanBeUsedForAttribute() {
        xstream.registerConverter(new PolymorphicEnumConverter<>(PolymorphicEnum.class));
        xstream.alias("type", TypeWithEnums.class);
        xstream.useAttributeFor(PolymorphicEnum.class);
        xstream.autodetectAnnotations(true);
        final String expectedXml = "" // force format
            + "<type poly=\"b\" simple=\"GREEN\">\n"
            + "  <big>C3</big>\n"
            + "</type>";
        final TypeWithEnums in = new TypeWithEnums();
        in.poly = PolymorphicEnum.B;
        in.simple = SimpleEnum.GREEN;
        in.big = BigEnum.C3;
        assertEquals(expectedXml, xstream.toXML(in));
        final TypeWithEnums out = xstream.<TypeWithEnums>fromXML(expectedXml);
        assertSame(out.poly, PolymorphicEnum.B);
        assertSame(out.simple, SimpleEnum.GREEN);
    }

    public void testCanBeUsedLocallyForAttribute() {
        xstream.registerLocalConverter(TypeWithEnums.class, "poly", new PolymorphicEnumConverter<>(
            PolymorphicEnum.class));
        xstream.alias("type", TypeWithEnums.class);
        xstream.useAttributeFor(PolymorphicEnum.class);
        xstream.autodetectAnnotations(true);
        final String expectedXml = "" // force format
            + "<type poly=\"b\" simple=\"GREEN\">\n"
            + "  <big>C3</big>\n"
            + "</type>";
        final TypeWithEnums in = new TypeWithEnums();
        in.poly = PolymorphicEnum.B;
        in.simple = SimpleEnum.GREEN;
        in.big = BigEnum.C3;
        assertEquals(expectedXml, xstream.toXML(in));
        final TypeWithEnums out = xstream.<TypeWithEnums>fromXML(expectedXml);
        assertSame(out.poly, PolymorphicEnum.B);
        assertSame(out.simple, SimpleEnum.GREEN);
    }

    private final static class PolymorphicEnumConverter<T extends Enum<T>> extends EnumSingleValueConverter<T> {
        private PolymorphicEnumConverter(final Class<T> type) {
            super(type);
        }

        @Override
        public Object fromString(final String str) {
            return super.fromString(str.toUpperCase());
        }

        @Override
        public String toString(final Object obj) {
            return super.toString(obj).toLowerCase();
        }
    }
}
