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
    public boolean canConvert(final Class<?> type) {
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
