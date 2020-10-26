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
    public boolean canConvert(final Class<?> type) {
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
