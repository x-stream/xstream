/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2012, 2014, 2016, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 01. October 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.TimeZone;


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

    public boolean canConvert(Class type) {
        return type == Timestamp.class;
    }

    public String toString(final Object obj) {
        final Timestamp timestamp = (Timestamp)obj;
        final StringBuffer buffer = new StringBuffer(format.format(timestamp));
        if (timestamp.getNanos() != 0) {
            buffer.append('.');
            final String nanos = String.valueOf(timestamp.getNanos() + 1000000000);
            int last = 10;
            while (last > 2 && nanos.charAt(last-1) == '0')
                --last;
            buffer.append(nanos.subSequence(1, last));
        }
        return buffer.toString();
    }

    public Object fromString(final String str) {
        final int idx = str.lastIndexOf('.');
        if (idx > 0 && (str.length() - idx < 2 || str.length() - idx > 10)) {
            throw new ConversionException("Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]");
        }
        try {
            final Timestamp timestamp = new Timestamp(format.parse(idx < 0 ? str : str.substring(0, idx)).getTime());
            if (idx > 0) {
                final StringBuffer buffer = new StringBuffer(str.substring(idx + 1));
                while (buffer.length() != 9) {
                    buffer.append('0');
                }
                timestamp.setNanos(Integer.parseInt(buffer.toString()));
            }
            return timestamp;
        } catch (NumberFormatException e) {
            throw new ConversionException(
                "Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]", e);
        } catch (ParseException e) {
            throw new ConversionException(
                "Timestamp format must be yyyy-mm-dd hh:mm:ss[.fffffffff]", e);
        }
    }

}
