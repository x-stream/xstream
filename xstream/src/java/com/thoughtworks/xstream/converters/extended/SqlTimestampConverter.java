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

package com.thoughtworks.xstream.converters.extended;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.TimeZone;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat;


/**
 * Converts a {@link Timestamp} to a string.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class SqlTimestampConverter extends AbstractSingleValueConverter {

    private final ThreadSafeSimpleDateFormat format;

    /**
     * Constructs a SqlTimestampConverter using UTC format.
     */
    public SqlTimestampConverter() {
        this(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Constructs a SqlTimestampConverter.
     * <p>
     * XStream uses by default UTC as time zone. However, if the resulting XML is used as feed for a data base (like MS
     * SQL) the server might expect the timestamp to be in local time and does the conversion to UTC on its own. In such
     * a case you can register an own instance of the SqlTimestamp converter using e.g. {@link TimeZone#getDefault()}.
     * </p>
     *
     * @param timeZone the time zone used for the format
     * @since 1.4.10
     */
    public SqlTimestampConverter(final TimeZone timeZone) {
        format = new ThreadSafeSimpleDateFormat("yyyy-MM-dd HH:mm:ss", timeZone, 0, 5, false);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Timestamp.class;
    }

    @Override
    public String toString(final Object obj) {
        final Timestamp timestamp = (Timestamp)obj;
        final StringBuilder buffer = new StringBuilder(format.format(timestamp));
        if (timestamp.getNanos() != 0) {
            buffer.append('.');
            final String nanos = String.valueOf(timestamp.getNanos() + 1000000000);
            int last = 10;
            while (last > 2 && nanos.charAt(last - 1) == '0') {
                --last;
            }
            buffer.append(nanos.subSequence(1, last));
        }
        return buffer.toString();
    }

    @Override
    public Object fromString(final String str) {
        final int idx = str.lastIndexOf('.');
        if (idx > 0 && (str.length() - idx < 2 || str.length() - idx > 10)) {
            throw new ConversionException("Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]");
        }
        try {
            final Timestamp timestamp = new Timestamp(format.parse(idx < 0 ? str : str.substring(0, idx)).getTime());
            if (idx > 0) {
                final StringBuilder buffer = new StringBuilder(str.substring(idx + 1));
                while (buffer.length() != 9) {
                    buffer.append('0');
                }
                timestamp.setNanos(Integer.parseInt(buffer.toString()));
            }
            return timestamp;
        } catch (final NumberFormatException e) {
            throw new ConversionException("Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]", e);
        } catch (final ParseException e) {
            throw new ConversionException("Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]");
        }
    }

}
