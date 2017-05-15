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

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.chrono.JapaneseEra;
import java.util.Collections;


/**
 * Converts a {@link java.time.chrono.JapaneseDate} to a string.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class JapaneseDateConverter extends AbstractChronoLocalDateConverter<JapaneseEra> {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return JapaneseDate.class == type;
    }

    @Override
    public Object fromString(final String str) {
        return parseChronoLocalDate(str, "Japanese", Collections.singleton(JapaneseChronology.INSTANCE));
    }

    @Override
    protected ChronoLocalDate chronoLocalDateOf(final JapaneseEra era, final int prolepticYear, final int month,
            final int dayOfMonth) {
        return JapaneseDate.of(era, prolepticYear, month, dayOfMonth);
    }

    @Override
    protected JapaneseEra eraOf(final String id) {
        return JapaneseEra.valueOf(id);
    }

}
