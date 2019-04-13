/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

/**
 * Converts a short primitive or {@link Short} wrapper to a string.
 * 
 * @author Joe Walnes
 */
public class ShortConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == short.class || type == Short.class;
    }

    @Override
    public Object fromString(final String str) {
        final int value = Integer.decode(str);
        if (value < Short.MIN_VALUE || value > 0xFFFF) {
            throw new NumberFormatException("For input string: \"" + str + '"');
        }
        return (short)value;
    }

}
