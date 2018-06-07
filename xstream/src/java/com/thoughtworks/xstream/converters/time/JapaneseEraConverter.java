/*
 * Copyright (C) 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 22. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.chrono.JapaneseEra;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link JapaneseEra} instance to a string using its id.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class JapaneseEraConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return type != null && JapaneseEra.class.isAssignableFrom(type);
    }

    @Override
    public JapaneseEra fromString(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return JapaneseEra.valueOf(str);
        } catch (final IllegalArgumentException e) {
            final ConversionException exception = new ConversionException("Cannot parse value as Japanese era", e);
            exception.add("value", str);
            throw exception;
        }
    }
}
