/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 05. May 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * A converter for {@link GregorianCalendar} conforming to the ISO8601 standard based on java.time.
 * <p>
 * The converter will always serialize the calendar value in UTC and deserialize it to a value in the current default
 * time zone.
 * </p>
 *
 * @author J&ouml;rg Schaible
 * @see <a href="http://www.iso.org/iso/home/store/catalogue_ics/catalogue_detail_ics.htm?csnumber=40874">ISO 8601</a>
 * @since 1.4.10
 */
public class ISO8601JavaTimeConverter extends AbstractSingleValueConverter {
    private static final DateTimeFormatter STD_DATE_TIME = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
        .appendFraction(ChronoField.NANO_OF_SECOND, 3, 9, true)
        .appendOffsetId()
        .toFormatter();
    private static final DateTimeFormatter STD_ORDINAL_DATE_TIME = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-DDD'T'HH:mm:ss")
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
        .appendOffsetId()
        .toFormatter();
    private static final DateTimeFormatter BASIC_DATE_TIME = new DateTimeFormatterBuilder()
        .appendPattern("yyyyMMdd'T'HHmmss")
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
        .appendOffsetId()
        .toFormatter();
    private static final DateTimeFormatter BASIC_ORDINAL_DATE_TIME = new DateTimeFormatterBuilder()
        .appendPattern("yyyyDDD'T'HHmmss")
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
        .appendOffsetId()
        .toFormatter();
    private static final DateTimeFormatter BASIC_TIME = new DateTimeFormatterBuilder()
        .appendPattern("HHmmss")
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
        .appendOffsetId()
        .toFormatter();
    private static final DateTimeFormatter ISO_TTIME = new DateTimeFormatterBuilder()
        .appendPattern("'T'HH:mm:ss")
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
        .appendOffsetId()
        .toFormatter();
    private static final DateTimeFormatter BASIC_TTIME = new DateTimeFormatterBuilder()
        .appendPattern("'T'HHmmss")
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
        .appendOffsetId()
        .toFormatter();
    private static final DateTimeFormatter ISO_WEEK_DATE_TIME = new DateTimeFormatterBuilder()
        .appendPattern("YYYY-'W'ww-e'T'HH:mm:ss")
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
        .appendOffsetId()
        .toFormatter();
    private static final DateTimeFormatter BASIC_WEEK_DATE_TIME = new DateTimeFormatterBuilder()
        .appendPattern("YYYY'W'wwe'T'HHmmss")
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
        .appendOffsetId()
        .toFormatter();
    private static final DateTimeFormatter BASIC_ORDINAL_DATE = new DateTimeFormatterBuilder()
        .appendPattern("yyyyDDD")
        .toFormatter();
    private static final DateTimeFormatter BASIC_WEEK_DATE = new DateTimeFormatterBuilder()
        .appendPattern("YYYY'W'wwe")
        .toFormatter();
    private static final DateTimeFormatter STD_DATE_HOUR = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH")
        .toFormatter();
    private static final DateTimeFormatter STD_HOUR = new DateTimeFormatterBuilder().appendPattern("HH").toFormatter();
    private static final DateTimeFormatter STD_YEAR_WEEK = new DateTimeFormatterBuilder()
        .appendPattern("YYYY-'W'ww")
        .parseDefaulting(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR, 1)
        .toFormatter();

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return false;
    }

    @Override
    public Object fromString(final String str) {
        try {
            final OffsetDateTime odt = OffsetDateTime.parse(str);
            return GregorianCalendar.from(odt.atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final LocalDateTime ldt = LocalDateTime.parse(str);
            return GregorianCalendar.from(ldt.atZone(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final Instant instant = Instant.parse(str);
            return GregorianCalendar.from(instant.atZone(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final OffsetDateTime odt = BASIC_DATE_TIME.parse(str, OffsetDateTime::from);
            return GregorianCalendar.from(odt.atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final OffsetDateTime odt = STD_ORDINAL_DATE_TIME.parse(str, OffsetDateTime::from);
            return GregorianCalendar.from(odt.atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final OffsetDateTime odt = BASIC_ORDINAL_DATE_TIME.parse(str, OffsetDateTime::from);
            return GregorianCalendar.from(odt.atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final OffsetTime ot = OffsetTime.parse(str);
            return GregorianCalendar.from(ot.atDate(LocalDate.ofEpochDay(0)).atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final OffsetTime ot = BASIC_TIME.parse(str, OffsetTime::from);
            return GregorianCalendar.from(ot.atDate(LocalDate.ofEpochDay(0)).atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final OffsetTime ot = ISO_TTIME.parse(str, OffsetTime::from);
            return GregorianCalendar.from(ot.atDate(LocalDate.ofEpochDay(0)).atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final OffsetTime ot = BASIC_TTIME.parse(str, OffsetTime::from);
            return GregorianCalendar.from(ot.atDate(LocalDate.ofEpochDay(0)).atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final TemporalAccessor ta = ISO_WEEK_DATE_TIME.withLocale(Locale.getDefault()).parse(str);
            final Year y = Year.from(ta);
            final MonthDay md = MonthDay.from(ta);
            final OffsetTime ot = OffsetTime.from(ta);
            return GregorianCalendar.from(ot.atDate(y.atMonthDay(md)).atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final TemporalAccessor ta = BASIC_WEEK_DATE_TIME.withLocale(Locale.getDefault()).parse(str);
            final Year y = Year.from(ta);
            final MonthDay md = MonthDay.from(ta);
            final OffsetTime ot = OffsetTime.from(ta);
            return GregorianCalendar.from(ot.atDate(y.atMonthDay(md)).atZoneSameInstant(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final LocalDate ld = LocalDate.parse(str);
            return GregorianCalendar.from(ld.atStartOfDay(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final LocalDate ld = LocalDate.parse(str, DateTimeFormatter.BASIC_ISO_DATE);
            return GregorianCalendar.from(ld.atStartOfDay(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final LocalDate ld = LocalDate.parse(str, DateTimeFormatter.ISO_ORDINAL_DATE);
            return GregorianCalendar.from(ld.atStartOfDay(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final LocalDate ld = BASIC_ORDINAL_DATE.parse(str, LocalDate::from);
            return GregorianCalendar.from(ld.atStartOfDay(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final LocalDate ld = LocalDate.parse(str, DateTimeFormatter.ISO_WEEK_DATE.withLocale(Locale.getDefault()));
            return GregorianCalendar.from(ld.atStartOfDay(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final TemporalAccessor ta = BASIC_WEEK_DATE.withLocale(Locale.getDefault()).parse(str);
            final Year y = Year.from(ta);
            final MonthDay md = MonthDay.from(ta);
            return GregorianCalendar.from(y.atMonthDay(md).atStartOfDay(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final LocalDateTime ldt = STD_DATE_HOUR.parse(str, LocalDateTime::from);
            return GregorianCalendar.from(ldt.atZone(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final LocalTime lt = STD_HOUR.parse(str, LocalTime::from);
            return GregorianCalendar.from(lt.atDate(LocalDate.ofEpochDay(0)).atZone(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final LocalTime lt = LocalTime.parse(str);
            return GregorianCalendar.from(lt.atDate(LocalDate.ofEpochDay(0)).atZone(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final YearMonth ym = YearMonth.parse(str);
            return GregorianCalendar.from(ym.atDay(1).atStartOfDay(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final Year y = Year.parse(str);
            return GregorianCalendar.from(y.atDay(1).atStartOfDay(ZoneId.systemDefault()));
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        try {
            final TemporalAccessor ta = STD_YEAR_WEEK.withLocale(Locale.getDefault()).parse(str);
            final int y = ta.get(WeekFields.ISO.weekBasedYear());
            final int w = ta.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            return GregorianCalendar.from(LocalDateTime
                .from(ta)
                .with(WeekFields.ISO.weekOfYear(), y)
                .with(WeekFields.ISO.weekOfWeekBasedYear(), w)
                .atZone(ZoneId.systemDefault()));
// } catch (final IllegalArgumentException e) { // TODO: DateTimeParseException
        } catch (final DateTimeParseException e) {
            // try with next formatter
        }
        final ConversionException exception = new ConversionException("Cannot parse date");
        exception.add("date", str);
        throw exception;
    }

    @Override
    public String toString(final Object obj) {
        final Calendar calendar = (Calendar)obj;
        final Instant instant = Instant.ofEpochMilli(calendar.getTimeInMillis());
        final int offsetInMillis = calendar.getTimeZone().getOffset(calendar.getTimeInMillis());
        final OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(instant, ZoneOffset.ofTotalSeconds(offsetInMillis
            / 1000));
        return STD_DATE_TIME.format(offsetDateTime);
    }
}
