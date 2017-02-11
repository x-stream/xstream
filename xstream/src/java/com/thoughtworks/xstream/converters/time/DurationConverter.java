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

import java.time.Duration;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link Duration} instance to string.
 *
 * @author J&ouml;rg Schaible
 */
public class DurationConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class type) {
        return Duration.class.isAssignableFrom(type);
    }

    @Override
    public Duration fromString(final String str) {
        return Duration.parse(str);
    }

    @Override
    public String toString(final Object obj) {
        final Duration duration = (Duration)obj;
        return duration.toString();
    }
}
