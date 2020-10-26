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

import java.util.Locale;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link Locale} to a string.
 * 
 * @author Jose A. Illescas
 * @author Joe Walnes
 */
public class LocaleConverter extends AbstractSingleValueConverter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Locale.class;
    }

    @Override
    public Object fromString(final String str) {
        final int[] underscorePositions = underscorePositions(str);
        String language, country, variant;
        if (underscorePositions[0] == -1) { // "language"
            language = str;
            country = "";
            variant = "";
        } else if (underscorePositions[1] == -1) { // "language_country"
            language = str.substring(0, underscorePositions[0]);
            country = str.substring(underscorePositions[0] + 1);
            variant = "";
        } else { // "language_country_variant"
            language = str.substring(0, underscorePositions[0]);
            country = str.substring(underscorePositions[0] + 1, underscorePositions[1]);
            variant = str.substring(underscorePositions[1] + 1);
        }
        return new Locale(language, country, variant);
    }

    private int[] underscorePositions(final String in) {
        final int[] result = new int[2];
        for (int i = 0; i < result.length; i++) {
            final int last = i == 0 ? 0 : result[i - 1];
            result[i] = in.indexOf('_', last + 1);
        }
        return result;
    }

}
