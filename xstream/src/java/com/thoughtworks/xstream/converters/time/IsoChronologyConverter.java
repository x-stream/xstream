/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 18. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.chrono.IsoChronology;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;


/**
 * Converts {@link IsoChronology#INSTANCE} to an empty string.
 *
 * @author J&ouml;rg Schaible
 */
public class IsoChronologyConverter implements SingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return IsoChronology.class == type;
    }

    @Override
    public IsoChronology fromString(final String str) {
        if (str == null) {
            return null;
        }
        if (!str.isEmpty()) {
            final ConversionException exception = new ConversionException(
                "ISO chronology does not have a string representation");
            exception.add("ISO chronology", str);
            throw exception;
        }
        return IsoChronology.INSTANCE;
    }

    @Override
    public String toString(final Object obj) {
        return obj == null ? null : "";
    }
}
