/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 13. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.MonthDay;
import java.time.format.DateTimeParseException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link MonthDay} instance to string.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class MonthDayConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return MonthDay.class == type;
    }

    @Override
    public MonthDay fromString(final String str) {
        try {
            return MonthDay.parse(str);
        } catch (final DateTimeParseException ex) {
            final ConversionException exception = new ConversionException("Cannot parse value as month day", ex);
            exception.add("value", str);
            throw exception;
        }
    }
}
