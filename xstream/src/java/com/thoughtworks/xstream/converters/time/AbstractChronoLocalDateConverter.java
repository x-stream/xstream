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

import java.time.DateTimeException;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * Abstract base class for converters handling derived classes of {@link java.time.chrono.ChronoLocalDate} instances.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
abstract class AbstractChronoLocalDateConverter<E extends Era> extends AbstractSingleValueConverter {
    protected abstract ChronoLocalDate chronoLocalDateOf(final E era, final int prolepticYear, final int month,
            final int dayOfMonth);

    protected abstract E eraOf(final String id);

    private final static Pattern CHRONO_DATE_PATTERN = Pattern.compile("^ (\\w+) (\\d+)-(\\d+)-(\\d+)$");

    protected ChronoLocalDate parseChronoLocalDate(final String str, final String dateTypeName,
            final Set<Chronology> chronologies) {
        if (str == null) {
            return null;
        }

        ConversionException exception = null;
        for (final Chronology chronology : chronologies) {
            final String id = chronology.getId();
            if (str.startsWith(id + ' ')) {
                final Matcher matcher = CHRONO_DATE_PATTERN.matcher(str.subSequence(id.length(), str.length()));
                if (matcher.matches()) {
                    E era = null;
                    try {
                        era = eraOf(matcher.group(1));
                    } catch (final IllegalArgumentException e) {
                        exception = new ConversionException("Cannot parse value as " + dateTypeName + " date", e);
                        break;
                    }
                    if (era != null) {
                        try {
                            return chronoLocalDateOf(era, Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher
                                .group(3)), Integer.parseInt(matcher.group(4)));
                        } catch (final DateTimeException e) {
                            exception = new ConversionException("Cannot parse value as " + dateTypeName + " date", e);
                            break;
                        }
                    }
                }
            }
        }
        if (exception == null) {
            exception = new ConversionException("Cannot parse value as " + dateTypeName + " date");
        }
        exception.add("value", str);
        throw exception;
    }

}
