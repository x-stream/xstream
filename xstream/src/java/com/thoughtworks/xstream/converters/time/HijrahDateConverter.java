/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 21. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.DateTimeException;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.HijrahEra;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Converts a {@link java.time.chrono.HijrahDate} to a string.
 *
 * @author J&ouml;rg Schaible
 */
public class HijrahDateConverter extends AbstractSingleValueConverter {

    private final static Pattern HIJRAH_PATTERN = Pattern.compile("^ (\\w+) (\\d+)-(\\d+)-(\\d+)$");
    private final Set<HijrahChronology> hijrahChronologies;

    public HijrahDateConverter() {
        hijrahChronologies = new HashSet<>();
        final Set<Chronology> chronologies = Chronology.getAvailableChronologies();
        for (final Chronology chronology : chronologies) {
            if (chronology instanceof HijrahChronology) {
                hijrahChronologies.add((HijrahChronology)chronology);
            }
        }
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return HijrahDate.class == type;
    }

    @Override
    public Object fromString(final String str) {
        if (str == null) {
            return null;
        }

        ConversionException exception = null;
        for (final HijrahChronology hijrahChronology : hijrahChronologies) {
            final String id = hijrahChronology.getId();
            if (str.startsWith(id + ' ')) {
                final Matcher matcher = HIJRAH_PATTERN.matcher(str.subSequence(id.length(), str.length()));
                if (matcher.matches()) {
                    try {
                        HijrahEra.valueOf(matcher.group(1));
                    } catch (final IllegalArgumentException e) {
                        exception = new ConversionException("Cannot parse value as Hijrah date", e);
                        break;
                    }
                    try {
                        return HijrahDate.of(Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)),
                            Integer.parseInt(matcher.group(4)));
                    } catch (final DateTimeException e) {
                        exception = new ConversionException("Cannot parse value as Hijrah date", e);
                        break;
                    }
                }
            }
        }
        if (exception == null) {
            exception = new ConversionException("Cannot parse value as Hijrah date");
        }
        exception.add("value", str);
        throw exception;
    }

}
