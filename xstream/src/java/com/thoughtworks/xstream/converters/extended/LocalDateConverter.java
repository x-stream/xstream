/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 13. January 2017 by Matej Cimbora
 */
package com.thoughtworks.xstream.converters.extended;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/**
 * Converts a {@link java.time.LocalDate} to a string.
 *
 * @author Matej Cimbora
 */
public class LocalDateConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(Class<?> type) {
        return type.equals(LocalDate.class);
    }

    @Override
    public Object fromString(String str) {
        try {
            return LocalDate.parse(str);
        } catch (DateTimeParseException e) {
            ConversionException exception = new ConversionException("Cannot parse string");
            exception.add("string", str);
            exception.add("targetType", LocalDate.class.getSimpleName());
            throw exception;
        }
    }

}
