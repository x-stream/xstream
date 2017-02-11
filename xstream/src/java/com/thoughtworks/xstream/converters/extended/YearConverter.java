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
package com.thoughtworks.xstream.converters.extended;

import java.time.Year;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link Year} instance to string.
 *
 * @author J&ouml;rg Schaible
 */
public class YearConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class type) {
        return Year.class.isAssignableFrom(type);
    }

    @Override
    public Year fromString(final String str) {
        return Year.of(Integer.parseInt(str));
    }

    @Override
    public String toString(final Object obj) {
        final Year year = (Year)obj;
        return year.toString();
    }
}
