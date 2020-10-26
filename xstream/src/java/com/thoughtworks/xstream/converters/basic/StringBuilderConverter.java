/*
 * Copyright (C) 2008, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. January 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.basic;

/**
 * Converts the contents of a {@link StringBuilder} to a string.
 * 
 * @author J&ouml;rg Schaible
 */
public class StringBuilderConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == StringBuilder.class;
    }

    @Override
    public Object fromString(final String str) {
        return new StringBuilder(str);
    }
}
