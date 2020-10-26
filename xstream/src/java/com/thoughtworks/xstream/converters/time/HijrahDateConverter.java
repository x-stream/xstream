/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
    public boolean canConvert(final Class<?> type) {
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
