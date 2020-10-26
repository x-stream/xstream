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
            this.d = new Double(d);
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
