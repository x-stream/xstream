/*
 * Copyright (C) 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 19. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.DateTimeException;
import java.time.chrono.Chronology;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;


/**
 * Converts a {@link Chronology} instance to a string using its id.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class ChronologyConverter implements SingleValueConverter {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return type != null && Chronology.class.isAssignableFrom(type);
    }

    @Override
    public Chronology fromString(final String str) {
        if (str == null) {
            return null;
        }
        try {
            return Chronology.of(str);
        } catch (final DateTimeException e) {
            final ConversionException exception = new ConversionException("Cannot parse value as chronology", e);
            exception.add("value", str);
            throw exception;
        }
    }

    @Override
    public String toString(final Object obj) {
        return obj == null ? null : ((Chronology)obj).getId();
    }
}
