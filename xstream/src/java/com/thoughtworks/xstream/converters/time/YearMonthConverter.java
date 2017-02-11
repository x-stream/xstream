/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 11. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.YearMonth;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link YearMonth} instance to string.
 *
 * @author J&ouml;rg Schaible
 */
public class YearMonthConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class type) {
        return YearMonth.class.isAssignableFrom(type);
    }

    @Override
    public YearMonth fromString(final String str) {
        return YearMonth.parse(str);
    }

    @Override
    public String toString(final Object obj) {
        final YearMonth yearMonth = (YearMonth)obj;
        return yearMonth.toString();
    }
}
