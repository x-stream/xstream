/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2011, 2012, 2014, 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Wrapper around java.text.SimpleDateFormat that can be called by multiple threads concurrently.
 * <p>
 * SimpleDateFormat has a high overhead in creating and is not thread safe. To make best use of resources, the
 * ThreadSafeSimpleDateFormat provides a dynamically sizing pool of instances, each of which will only be called by a
 * single thread at a time.
 * </p>
 * <p>
 * The pool has a maximum capacity, to limit overhead. If all instances in the pool are in use and another is required,
 * it shall block until one becomes available.
 * </p>
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class ThreadSafeSimpleDateFormat {

    private final String formatString;
    private final Pool<DateFormat> pool;
    private final TimeZone timeZone;

    public ThreadSafeSimpleDateFormat(
            final String format, final TimeZone timeZone, final int initialPoolSize, final int maxPoolSize,
            final boolean lenient) {
        this(format, timeZone, Locale.ENGLISH, initialPoolSize, maxPoolSize, lenient);
    }

    public ThreadSafeSimpleDateFormat(
            final String format, final TimeZone timeZone, final Locale locale, final int initialPoolSize,
            final int maxPoolSize, final boolean lenient) {
        formatString = format;
        this.timeZone = timeZone;
        pool = new Pool<>(initialPoolSize, maxPoolSize, new Pool.Factory<DateFormat>() {
            @Override
            public SimpleDateFormat newInstance() {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(formatString, locale);
                dateFormat.setLenient(lenient);
                return dateFormat;
            }

        });
    }

    public String format(final Date date) {
        final DateFormat format = fetchFromPool();
        try {
            return format.format(date);
        } finally {
            pool.putInPool(format);
        }
    }

    public Date parse(final String date) throws ParseException {
        final DateFormat format = fetchFromPool();
        try {
            return format.parse(date);
        } finally {
            pool.putInPool(format);
        }
    }

    private DateFormat fetchFromPool() {
        final DateFormat format = pool.fetchFromPool();
        final TimeZone tz = timeZone != null ? timeZone : TimeZone.getDefault();
        if (!tz.equals(format.getTimeZone())) {
            format.setTimeZone(tz);
        }
        return format;
    }

    @Override
    public String toString() {
        return formatString;
    }
}
