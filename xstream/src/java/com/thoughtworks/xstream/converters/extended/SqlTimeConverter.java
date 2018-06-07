/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 24. July 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.sql.Time;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link Time} to a string.
 * <p>
 * Warning: Any granularity smaller than seconds is lost.
 * </p>
 * 
 * @author Jose A. Illescas
 */
public class SqlTimeConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Time.class;
    }

    @Override
    public Object fromString(final String str) {
        return Time.valueOf(str);
    }

}
