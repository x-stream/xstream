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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;


/**
 * Converts a {@link OffsetDateTime} to a string.
 *
 * @author Matej Cimbora
 * @since 1.4.10
 */
public class OffsetDateTimeConverter implements SingleValueConverter {

    private static final DateTimeFormatter FORMATTER;

    static {
        FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern("uuuu-MM-dd'T'HH:mm:ss")
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .appendOffsetId()
            .toFormatter();
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return OffsetDateTime.class == type;
    }

    @Override
    public Object fromString(final String str) {
        try {
            return OffsetDateTime.parse(str);
        } catch (final DateTimeParseException e) {
            final ConversionException exception = new ConversionException("Cannot parse value as offset date time", e);
            exception.add("value", str);
            throw exception;
        }
    }

    @Override
    public String toString(final Object obj) {
        if (obj == null) {
            return null;
        }

        final OffsetDateTime offsetDateTime = (OffsetDateTime)obj;
        return FORMATTER.format(offsetDateTime);
    }
}
