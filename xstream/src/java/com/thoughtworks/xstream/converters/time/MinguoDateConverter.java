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
import java.time.chrono.MinguoChronology;
import java.time.chrono.MinguoDate;
import java.time.chrono.MinguoEra;
import java.util.Collections;


/**
 * Converts a {@link java.time.chrono.MinguoDate} to a string.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class MinguoDateConverter extends AbstractChronoLocalDateConverter<MinguoEra> {

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return MinguoDate.class == type;
    }

    @Override
    public Object fromString(final String str) {
        return parseChronoLocalDate(str, "Minguo", Collections.singleton(MinguoChronology.INSTANCE));
    }

    @Override
    protected ChronoLocalDate chronoLocalDateOf(final MinguoEra era, final int prolepticYear, final int month,
            final int dayOfMonth) {
        return MinguoDate.of(prolepticYear, month, dayOfMonth);
    }

    @Override
    protected MinguoEra eraOf(final String id) {
        return MinguoEra.valueOf(id);
    }

}
