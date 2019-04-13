/*
 * Copyright (C) 2006, 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17. March 2006 by Joerg Schaible
 */
package com.thoughtworks.acceptance;

import java.text.DecimalFormat;
import java.text.ParseException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;


public class CustomConverterTest extends AbstractAcceptanceTest {

    private final class DoubleConverter implements SingleValueConverter {
        private final DecimalFormat formatter;

        private DoubleConverter() {
            formatter = new DecimalFormat("#,###,##0");
        }

        @Override
        public boolean canConvert(final Class<?> type) {
            return type == double.class || type == Double.class;
        }

        @Override
        public String toString(final Object obj) {
            return formatter.format(obj);
        }

        @Override
        public Object fromString(final String str) {
            try {
                // the formatter will chose the most appropriate format ... Long
                return formatter.parseObject(str);
            } catch (final ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    public static class DoubleWrapper {
        Double d;

        public DoubleWrapper(final double d) {
            this.d = d;
        }
    }

    public void testWrongObjectTypeReturned() {
        xstream.alias("dw", DoubleWrapper.class);
        xstream.registerConverter(new DoubleConverter());

        final String xml = "" //
            + "<dw>\n"
            + "  <d>-92.000.000</d>\n"
            + "</dw>";

        try {
            xstream.fromXML(xml);
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertTrue(e.getMessage().indexOf(Long.class.getName()) > 0);
        }
    }
}
