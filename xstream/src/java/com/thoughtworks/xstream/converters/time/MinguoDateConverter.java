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
    public boolean canConvert(final Class<?> type) {
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
