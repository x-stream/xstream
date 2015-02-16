/*
 * Copyright (C) 2003, 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2012, 2013, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.ErrorReporter;
import com.thoughtworks.xstream.converters.ErrorWriter;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.ThreadSafeSimpleDateFormat;


/**
 * Converts a {@link Date} to a string as a date format, retaining precision down to milliseconds.
 * <p>
 * The formatted string is by default in UTC and English locale. You can provide a different {@link Locale} and
 * {@link TimeZone} that are used for serialization or <code>null</code> to use always the current TimeZone. Note, that
 * the default format uses 3-letter time zones that can be ambiguous and may cause wrong results at deserialization and
 * is localized since Java 6.
 * </p>
 * <p>
 * Using a Java 7 runtime or higher, the converter supports the <a href="http://www.w3.org/TR/NOTE-datetime">datetime
 * format defined by W3C</a> (a subset of ISO 8601) at deserialization. Only the formats that also contain the time
 * information.
 * </p>
 * <p>
 * Dates in a different era are using a special default pattern that contains the era itself.
 * </p>
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class DateConverter extends AbstractSingleValueConverter implements ErrorReporter {

    private static final String[] DEFAULT_ACCEPTABLE_FORMATS;
    private static final String DEFAULT_PATTERN;
    private static final String DEFAULT_ERA_PATTERN;
    private static final TimeZone UTC;
    private static final long ERA_START;
    static {
        UTC = TimeZone.getTimeZone("UTC");
        
        final String defaultPattern = "yyyy-MM-dd HH:mm:ss.S z";
        final String defaultEraPattern = "yyyy-MM-dd G HH:mm:ss.S z";
        final List acceptablePatterns = new ArrayList();
        final boolean utcSupported = JVM.canParseUTCDateFormat();
        DEFAULT_PATTERN = utcSupported ? defaultPattern : "yyyy-MM-dd HH:mm:ss.S 'UTC'";
        DEFAULT_ERA_PATTERN = utcSupported ? defaultEraPattern : "yyyy-MM-dd G HH:mm:ss.S 'UTC'";
        acceptablePatterns.add("yyyy-MM-dd HH:mm:ss.S z");
        if (!utcSupported) {
            acceptablePatterns.add(defaultPattern);
        }
        acceptablePatterns.add("yyyy-MM-dd HH:mm:ss.S a");
        // JDK 1.3 needs both versions
        acceptablePatterns.add("yyyy-MM-dd HH:mm:ssz");
        acceptablePatterns.add("yyyy-MM-dd HH:mm:ss z");
        if (!utcSupported) {
            acceptablePatterns.add("yyyy-MM-dd HH:mm:ss 'UTC'");
        }
        if (JVM.canParseISO8601TimeZoneInDateFormat()) {
            acceptablePatterns.add("yyyy-MM-dd'T'HH:mm:ss.SX");
            acceptablePatterns.add("yyyy-MM-dd'T'HH:mm:ssX");
            acceptablePatterns.add("yyyy-MM-dd'T'HH:mmX");
        }
        // backwards compatibility
        acceptablePatterns.add("yyyy-MM-dd HH:mm:ssa");
        DEFAULT_ACCEPTABLE_FORMATS = (String[]) acceptablePatterns.toArray(new String[acceptablePatterns.size()]);
        
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(UTC);
        cal.clear();
        cal.set(1, Calendar.JANUARY, 1);
        ERA_START = cal.getTime().getTime(); // calendar.getTimeInMillis() not available under JDK 1.3
    }
    private final ThreadSafeSimpleDateFormat defaultFormat;
    private final ThreadSafeSimpleDateFormat defaultEraFormat;
    private final ThreadSafeSimpleDateFormat[] acceptableFormats;

    /**
     * Construct a DateConverter with standard formats and lenient set off.
     */
    public DateConverter() {
        this(false);
    }

    /**
     * Construct a DateConverter with standard formats, lenient set off and uses a given
     * TimeZone for serialization.
     * 
     * @param timeZone the TimeZone used to serialize the Date
     * @since 1.4
     */
    public DateConverter(final TimeZone timeZone) {
        this(DEFAULT_PATTERN, DEFAULT_ACCEPTABLE_FORMATS, timeZone);
    }

    /**
     * Construct a DateConverter with standard formats and using UTC.
     * 
     * @param lenient the lenient setting of {@link SimpleDateFormat#setLenient(boolean)}
     * @since 1.3
     */
    public DateConverter(final boolean lenient) {
        this(DEFAULT_PATTERN, DEFAULT_ACCEPTABLE_FORMATS, lenient);
    }

    /**
     * Construct a DateConverter with lenient set off using UTC.
     * 
     * @param defaultFormat the default format
     * @param acceptableFormats fallback formats
     */
    public DateConverter(final String defaultFormat, final String[] acceptableFormats) {
        this(defaultFormat, acceptableFormats, false);
    }

    /**
     * Construct a DateConverter with a given TimeZone and lenient set off.
     * 
     * @param defaultFormat the default format
     * @param acceptableFormats fallback formats
     * @since 1.4
     */
    public DateConverter(final String defaultFormat, final String[] acceptableFormats, final TimeZone timeZone) {
        this(defaultFormat, acceptableFormats, timeZone, false);
    }

    /**
     * Construct a DateConverter.
     * 
     * @param defaultFormat the default format
     * @param acceptableFormats fallback formats
     * @param lenient the lenient setting of {@link SimpleDateFormat#setLenient(boolean)}
     * @since 1.3
     */
    public DateConverter(final String defaultFormat, final String[] acceptableFormats, final boolean lenient) {
        this(defaultFormat, acceptableFormats, UTC, lenient);
    }

    /**
     * Construct a DateConverter.
     * 
     * @param defaultFormat the default format
     * @param acceptableFormats fallback formats
     * @param timeZone the TimeZone used to serialize the Date
     * @param lenient the lenient setting of {@link SimpleDateFormat#setLenient(boolean)}
     * @since 1.4
     */
    public DateConverter(
            final String defaultFormat, final String[] acceptableFormats, final TimeZone timeZone, final boolean lenient) {
        this(DEFAULT_ERA_PATTERN, defaultFormat, acceptableFormats, Locale.ENGLISH, timeZone, lenient);
    }

    /**
     * Construct a DateConverter.
     * 
     * @param defaultEraFormat the default format for dates in a different era (may be
     *            <code>null</code> to drop era support)
     * @param defaultFormat the default format
     * @param acceptableFormats fallback formats
     * @param locale locale to use for the format
     * @param timeZone the TimeZone used to serialize the Date
     * @param lenient the lenient setting of {@link SimpleDateFormat#setLenient(boolean)}
     * @since 1.4.4
     */
    public DateConverter(
            final String defaultEraFormat, final String defaultFormat, final String[] acceptableFormats,
            final Locale locale, final TimeZone timeZone, final boolean lenient) {
        if (defaultEraFormat != null) {
            this.defaultEraFormat = new ThreadSafeSimpleDateFormat(
                defaultEraFormat, timeZone, locale, 4, 20, lenient);
        } else {
            this.defaultEraFormat = null;
        }
        this.defaultFormat = new ThreadSafeSimpleDateFormat(
            defaultFormat, timeZone, locale, 4, 20, lenient);
        this.acceptableFormats = acceptableFormats != null
            ? new ThreadSafeSimpleDateFormat[acceptableFormats.length]
            : new ThreadSafeSimpleDateFormat[0];
        for (int i = 0; i < this.acceptableFormats.length; i++ ) {
            this.acceptableFormats[i] = new ThreadSafeSimpleDateFormat(
                acceptableFormats[i], timeZone, locale, 1, 20, lenient);
        }
    }

    public boolean canConvert(Class type) {
        return type.equals(Date.class);
    }

    public Object fromString(String str) {
        if (defaultEraFormat != null) {
            try {
                return defaultEraFormat.parse(str);
            } catch (ParseException e) {
                // try next ...
            }
        }
        if (defaultEraFormat != defaultFormat) {
            try {
                return defaultFormat.parse(str);
            } catch (ParseException e) {
                // try next ...
            }
        }
        for (int i = 0; i < acceptableFormats.length; i++ ) {
            try {
                return acceptableFormats[i].parse(str);
            } catch (ParseException e3) {
                // no worries, let's try the next format.
            }
        }
        // no dateFormats left to try
        throw new ConversionException("Cannot parse date " + str);
    }

    public String toString(Object obj) {
        final Date date = (Date)obj;
        if (date.getTime() < ERA_START && defaultEraFormat != null) {
            return defaultEraFormat.format(date);
        } else {
            return defaultFormat.format(date);
        }
    }

    public void appendErrors(ErrorWriter errorWriter) {
        errorWriter.add("Default date pattern", defaultFormat.toString());
        if (defaultEraFormat != null) {
            errorWriter.add("Default era date pattern", defaultEraFormat.toString());
        }
        for (int i = 0; i < acceptableFormats.length; i++ ) {
            errorWriter.add("Alternative date pattern", acceptableFormats[i].toString());
        }
    }
}
