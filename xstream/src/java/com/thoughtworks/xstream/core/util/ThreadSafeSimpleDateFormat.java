/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
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
 * Wrapper around java.text.SimpleDateFormat that can
 * be called by multiple threads concurrently.
 * <p>SimpleDateFormat has a high overhead in creating
 * and is not thread safe. To make best use of resources,
 * the ThreadSafeSimpleDateFormat provides a dynamically
 * sizing pool of instances, each of which will only
 * be called by a single thread at a time.</p>
 * <p>The pool has a maximum capacity, to limit overhead.
 * If all instances in the pool are in use and another is
 * required, it shall block until one becomes available.</p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class ThreadSafeSimpleDateFormat {

    private final String formatString;
    private final Pool pool;

    public ThreadSafeSimpleDateFormat(String format, int initialPoolSize, int maxPoolSize, final boolean lenient) {
        formatString = format;
        pool = new Pool(initialPoolSize, maxPoolSize, new Pool.Factory() {
            public Object newInstance() {
                SimpleDateFormat dateFormat = new SimpleDateFormat(formatString, Locale.ENGLISH);
                dateFormat.setLenient(lenient);
                return dateFormat;
            }
            
        });
    }

    public String format(Date date) {
        DateFormat format = fetchFromPool();
        try {
            return format.format(date);
        } finally {
            pool.putInPool(format);
        }
    }

    public Date parse(String date) throws ParseException {
        DateFormat format = fetchFromPool();
        try {
            return format.parse(date);
        } finally {
            pool.putInPool(format);
        }
    }

    private DateFormat fetchFromPool() {
        TimeZone tz = TimeZone.getDefault();
        DateFormat format = (DateFormat)pool.fetchFromPool();
        if (!tz.equals(format.getTimeZone())) {
            format.setTimeZone(tz);
        }
        return format;
    }
}
