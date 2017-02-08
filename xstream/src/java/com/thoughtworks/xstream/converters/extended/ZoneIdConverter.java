/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 8. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.time.ZoneId;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link ZoneId} instance to string.
 *
 * @author J&ouml;rg Schaible
 */
public class ZoneIdConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return ZoneId.class.isAssignableFrom(type);
    }

    @Override
    public ZoneId fromString(final String str) {
        return ZoneId.of(str);
    }

    @Override
    public String toString(final Object obj) {
        final ZoneId zoneId = (ZoneId)obj;
        return zoneId.getId();
    }
}
