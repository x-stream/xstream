/*
 * Copyright (C) 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 8. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;


/**
 * Converts a {@link ZoneId} instance to string.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class ZoneIdConverter implements SingleValueConverter {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return type != null && ZoneId.class.isAssignableFrom(type);
    }

    @Override
    public ZoneId fromString(final String str) {
        ConversionException exception;
        try {
            return ZoneId.of(str);
        } catch (final ZoneRulesException e) {
            exception = new ConversionException("Not a valid zone id", e);
        } catch (final DateTimeException e) {
            exception = new ConversionException("Cannot parse value as zone id", e);
        }
        exception.add("value", str);
        throw exception;
    }

    @Override
    public String toString(final Object obj) {
        if (obj == null) {
            return null;
        }
        final ZoneId zoneId = (ZoneId)obj;
        return zoneId.getId();
    }
}
