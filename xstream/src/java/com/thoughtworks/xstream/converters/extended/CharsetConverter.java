/*
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. April 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.nio.charset.Charset;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link Charset} to a string.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class CharsetConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && Charset.class.isAssignableFrom(type);
    }

    @Override
    public String toString(final Object obj) {
        return ((Charset)obj).name();
    }

    @Override
    public Object fromString(final String str) {
        return Charset.forName(str);
    }
}
