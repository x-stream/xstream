/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 22. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.DateTimeException;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.chrono.JapaneseEra;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link java.time.chrono.JapaneseDate} to a string.
 *
 * @author J&ouml;rg Schaible
 */
public class JapaneseDateConverter extends AbstractSingleValueConverter {

    private final static Pattern CHRONO_DATE_PATTERN = Pattern.compile("^ (\\w+) (\\d+)-(\\d+)-(\\d+)$");

    @Override
    public boolean canConvert(final Class<?> type) {
        return JapaneseDate.class == type;
    }

    @Override
    public Object fromString(final String str) {
        if (str == null) {
            return null;
        }

        ConversionException exception = null;
        final String id = JapaneseChronology.INSTANCE.getId();
        if (str.startsWith(id + ' ')) {
            final Matcher matcher = CHRONO_DATE_PATTERN.matcher(str.subSequence(id.length(), str.length()));
            if (matcher.matches()) {
                JapaneseEra era = null;
                try {
                    era = JapaneseEra.valueOf(matcher.group(1));
                } catch (final IllegalArgumentException e) {
                    exception = new ConversionException("Cannot parse value as Japanese date", e);
                }
                if (exception == null) {
                    try {
                        return JapaneseDate.of(era, Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(
                            3)), Integer.parseInt(matcher.group(4)));
                    } catch (final DateTimeException e) {
                        exception = new ConversionException("Cannot parse value as Japanese date", e);
                    }
                }
            }
        }
        if (exception == null) {
            exception = new ConversionException("Cannot parse value as Japanese date");
        }
        exception.add("value", str);
        throw exception;
    }

}
