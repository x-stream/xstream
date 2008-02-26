/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat;

/**
 * Converts a java.util.Date to a String as a date format,
 * retaining precision down to milliseconds.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class DateConverter extends AbstractSingleValueConverter {

    private final ThreadSafeSimpleDateFormat defaultFormat;
    private final ThreadSafeSimpleDateFormat[] acceptableFormats;

    /**
     * Construct a DateConverter with standard formats and lenient set off.
     */
    public DateConverter() {
        this(false);
    }

    /**
     * Construct a DateConverter with standard formats.
     * 
     * @param lenient the lenient setting of {@link SimpleDateFormat#setLenient(boolean)}
     * @since 1.3
     */
    public DateConverter(boolean lenient) {
        this("yyyy-MM-dd HH:mm:ss.S z",
            new String[] { 
                "yyyy-MM-dd HH:mm:ss.S a", 
                "yyyy-MM-dd HH:mm:ssz", "yyyy-MM-dd HH:mm:ss z", // JDK 1.3 needs both versions
                "yyyy-MM-dd HH:mm:ssa" },  // backwards compatibility
                lenient);
    }

    /**
     * Construct a DateConverter with lenient set off.
     * 
     * @param defaultFormat the default format
     * @param acceptableFormats fallback formats
     */
    public DateConverter(String defaultFormat, String[] acceptableFormats) {
        this(defaultFormat, acceptableFormats, false);
    }

    /**
     * Construct a DateConverter.
     * 
     * @param defaultFormat the default format
     * @param acceptableFormats fallback formats
     * @param lenient the lenient setting of {@link SimpleDateFormat#setLenient(boolean)}
     * @since 1.3
     */
    public DateConverter(String defaultFormat, String[] acceptableFormats, boolean lenient) {
        this.defaultFormat = new ThreadSafeSimpleDateFormat(defaultFormat, 4, 20, lenient);
        this.acceptableFormats = new ThreadSafeSimpleDateFormat[acceptableFormats.length];
        for (int i = 0; i < acceptableFormats.length; i++) {
            this.acceptableFormats[i] = new ThreadSafeSimpleDateFormat(acceptableFormats[i], 1, 20, lenient);
        }
    }

    public boolean canConvert(Class type) {
        return type.equals(Date.class);
    }

    public Object fromString(String str) {
        try {
            return defaultFormat.parse(str);
        } catch (ParseException e) {
            for (int i = 0; i < acceptableFormats.length; i++) {
                try {
                    return acceptableFormats[i].parse(str);
                } catch (ParseException e2) {
                    // no worries, let's try the next format.
                }
            }
            // no dateFormats left to try
            throw new ConversionException("Cannot parse date " + str);
        }
    }

    public String toString(Object obj) {
        return defaultFormat.format((Date) obj);
    }

}
