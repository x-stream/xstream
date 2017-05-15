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
import java.time.format.DateTimeParseException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link YearMonth} instance to string.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class YearMonthConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return YearMonth.class == type;
    }

    @Override
    public YearMonth fromString(final String str) {
        try {
            return YearMonth.parse(str);
        } catch (final DateTimeParseException ex) {
            final ConversionException exception = new ConversionException("Cannot parse value as year month", ex);
            exception.add("value", str);
            throw exception;
        }
    }
}
