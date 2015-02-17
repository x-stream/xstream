/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2013, 2014, 2015 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 03. October 2005 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * A GregorianCalendarConverter conforming to the ISO8601 standard. The converter will always
 * serialize the calendar value in UTC and deserialize it to a value in the current default time
 * zone.
 * 
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 * @see <a href="http://www.iso.org/iso/home/store/catalogue_ics/catalogue_detail_ics.htm?csnumber=40874">ISO 8601</a>
 * @since 1.1.3
 */
public class ISO8601GregorianCalendarConverter extends AbstractSingleValueConverter {
    private static final DateTimeFormatter[] formattersUTC = new DateTimeFormatter[]{
        ISODateTimeFormat.dateTime(),
        ISODateTimeFormat.dateTimeNoMillis(),
        ISODateTimeFormat.basicDateTime(),
        ISODateTimeFormat.basicOrdinalDateTime(),
        ISODateTimeFormat.basicOrdinalDateTimeNoMillis(),
        ISODateTimeFormat.basicTime(),
        ISODateTimeFormat.basicTimeNoMillis(),
        ISODateTimeFormat.basicTTime(),
        ISODateTimeFormat.basicTTimeNoMillis(),
        ISODateTimeFormat.basicWeekDateTime(),
        ISODateTimeFormat.basicWeekDateTimeNoMillis(),
        ISODateTimeFormat.ordinalDateTime(),
        ISODateTimeFormat.ordinalDateTimeNoMillis(),
        ISODateTimeFormat.time(),
        ISODateTimeFormat.timeNoMillis(),
        ISODateTimeFormat.tTime(),
        ISODateTimeFormat.tTimeNoMillis(),
        ISODateTimeFormat.weekDateTime(),
        ISODateTimeFormat.weekDateTimeNoMillis()
    };
    private static final DateTimeFormatter[] formattersNoUTC = new DateTimeFormatter[]{
        ISODateTimeFormat.basicDate(),
        ISODateTimeFormat.basicOrdinalDate(),
        ISODateTimeFormat.basicWeekDate(),
        ISODateTimeFormat.date(),
        ISODateTimeFormat.dateHour(),
        ISODateTimeFormat.dateHourMinute(),
        ISODateTimeFormat.dateHourMinuteSecond(),
        ISODateTimeFormat.dateHourMinuteSecondFraction(),
        ISODateTimeFormat.dateHourMinuteSecondMillis(),
        ISODateTimeFormat.hour(),
        ISODateTimeFormat.hourMinute(),
        ISODateTimeFormat.hourMinuteSecond(),
        ISODateTimeFormat.hourMinuteSecondFraction(),
        ISODateTimeFormat.hourMinuteSecondMillis(),
        ISODateTimeFormat.ordinalDate(),
        ISODateTimeFormat.weekDate(),
        ISODateTimeFormat.year(),
        ISODateTimeFormat.yearMonth(),
        ISODateTimeFormat.yearMonthDay(),
        ISODateTimeFormat.weekyear(),
        ISODateTimeFormat.weekyearWeek(),
        ISODateTimeFormat.weekyearWeekDay()
    };
    
    public boolean canConvert(Class type) {
        return type.equals(GregorianCalendar.class);
    }

    public Object fromString(String str) {
        for (int i = 0; i < formattersUTC.length; i++ ) {
            DateTimeFormatter formatter = formattersUTC[i];
            try {
                DateTime dt = formatter.parseDateTime(str);
                Calendar calendar = dt.toGregorianCalendar();
                calendar.setTimeZone(TimeZone.getDefault());
                return calendar;
            } catch (IllegalArgumentException e) {
                // try with next formatter
            }
        }
        final DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(TimeZone.getDefault());
        for (int i = 0; i < formattersNoUTC.length; i++ ) {
            try {
                final DateTimeFormatter formatter = formattersNoUTC[i].withZone(dateTimeZone);
                final DateTime dt = formatter.parseDateTime(str);
                final Calendar calendar = dt.toGregorianCalendar();
                calendar.setTimeZone(TimeZone.getDefault());
                return calendar;
            } catch (IllegalArgumentException e) {
                // try with next formatter
            }
        }
        throw new ConversionException("Cannot parse date " + str);
    }

    public String toString(Object obj) {
        DateTime dt = new DateTime(obj);
        return dt.toString(formattersUTC[0]);
    }
}
