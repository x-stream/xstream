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

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.HijrahEra;
import java.util.HashSet;
import java.util.Set;


/**
 * Converts a {@link java.time.chrono.HijrahDate} to a string.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class HijrahDateConverter extends AbstractChronoLocalDateConverter<HijrahEra> {

    private final Set<Chronology> hijrahChronologies;

    /**
     * Constructs a HijrahDateConverter instance.
     */
    public HijrahDateConverter() {
        hijrahChronologies = new HashSet<>();
        final Set<Chronology> chronologies = Chronology.getAvailableChronologies();
        for (final Chronology chronology : chronologies) {
            if (chronology instanceof HijrahChronology) {
                hijrahChronologies.add(chronology);
            }
        }
    }

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return HijrahDate.class == type;
    }

    @Override
    public Object fromString(final String str) {
        return parseChronoLocalDate(str, "Hijrah", hijrahChronologies);
    }

    @Override
    protected ChronoLocalDate chronoLocalDateOf(final HijrahEra era, final int prolepticYear, final int month,
            final int dayOfMonth) {
        return era != null ? HijrahDate.of(prolepticYear, month, dayOfMonth) : null;
    }

    @Override
    protected HijrahEra eraOf(final String id) {
        return HijrahEra.valueOf(id);
    }

}
