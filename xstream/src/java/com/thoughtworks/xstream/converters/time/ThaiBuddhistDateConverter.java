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
import java.time.chrono.ThaiBuddhistChronology;
import java.time.chrono.ThaiBuddhistDate;
import java.time.chrono.ThaiBuddhistEra;
import java.util.Collections;


/**
 * Converts a {@link java.time.chrono.ThaiBuddhistDate} to a string.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class ThaiBuddhistDateConverter extends AbstractChronoLocalDateConverter<ThaiBuddhistEra> {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return ThaiBuddhistDate.class == type;
    }

    @Override
    public Object fromString(final String str) {
        return parseChronoLocalDate(str, "Thai Buddhist", Collections.singleton(ThaiBuddhistChronology.INSTANCE));
    }

    @Override
    protected ChronoLocalDate chronoLocalDateOf(final ThaiBuddhistEra era, final int prolepticYear, final int month,
            final int dayOfMonth) {
        return ThaiBuddhistDate.of(prolepticYear, month, dayOfMonth);
    }

    @Override
    protected ThaiBuddhistEra eraOf(final String id) {
        return ThaiBuddhistEra.valueOf(id);
    }

}
