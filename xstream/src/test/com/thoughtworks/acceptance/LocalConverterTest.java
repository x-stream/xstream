/*
 * Copyright (C) 2007, 2008, 2009, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. November 2007 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;


/**
 * @author J&ouml;rg Schaible
 */
public class LocalConverterTest extends AbstractAcceptanceTest {

    public static class MultiBoolean {
        final boolean bool;
        final boolean speech;
        final boolean bit;

        @SuppressWarnings("unused")
        private MultiBoolean() {
            this(false, false, false);
        }

        public MultiBoolean(final boolean bool, final boolean speech, final boolean bit) {
            this.bool = bool;
            this.speech = speech;
            this.bit = bit;
        }

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xstream.alias("mbool", MultiBoolean.class);
        xstream.registerConverter(new ReflectionConverter(xstream.getMapper(), new PureJavaReflectionProvider()),
            XStream.PRIORITY_VERY_LOW);
    }

    public void testCanBeAppliedToIndividualFields() {
        final MultiBoolean multiBool = new MultiBoolean(true, true, true);
        final String xml = ""
            + "<mbool>\n"
            + "  <bool>true</bool>\n"
            + "  <speech>yes</speech>\n"
            + "  <bit>1</bit>\n"
            + "</mbool>";

        xstream.registerLocalConverter(MultiBoolean.class, "speech", BooleanConverter.YES_NO);
        xstream.registerLocalConverter(MultiBoolean.class, "bit", BooleanConverter.BINARY);
        assertBothWays(multiBool, xml);
    }

    public static class SymbolParameter {
        int type;
        int color;
        int width;

        public SymbolParameter() {
        }

        public SymbolParameter(final int type, final int color, final int width) {
            this.type = type;
            this.color = color;
            this.width = width;
        }

    }

    public static class HexNumberConverter implements SingleValueConverter {
        @Override
        public boolean canConvert(final Class<?> type) {
            return type.equals(int.class) || type.equals(Integer.class);
        }

        @Override
        public Object fromString(final String value) {
            return Integer.parseInt(value, 16);
        }

        @Override
        public String toString(final Object obj) {
            return Integer.toHexString(((Number)obj).intValue());
        }
    }

    public void testCanBeUsedForAttributeValue() {
        final SymbolParameter multiBool = new SymbolParameter(1, 0xff00ff, 100);
        final String xml = ""
            + "<param color=\"ff00ff\">\n"
            + "  <type>1</type>\n"
            + "  <width>100</width>\n"
            + "</param>";

        xstream.alias("param", SymbolParameter.class);
        xstream.useAttributeFor("color", int.class);
        xstream.registerLocalConverter(SymbolParameter.class, "color", new HexNumberConverter());
        assertBothWays(multiBool, xml);
    }
}
