/*
 * Copyright (C) 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. December 2022 by Jorg Schaible, renamed from Time18TypesTest
 */
package com.thoughtworks.acceptance;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.HijrahEra;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.chrono.JapaneseEra;
import java.time.chrono.MinguoChronology;
import java.time.chrono.MinguoDate;
import java.time.chrono.MinguoEra;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.chrono.ThaiBuddhistDate;
import java.time.chrono.ThaiBuddhistEra;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.time.temporal.JulianFields;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;

import com.thoughtworks.xstream.converters.ConversionException;


/**
 * Test types of java.time.* package.
 * 
 * @author Matej Cimbora
 * @author J&ouml;rg Schaible
 */
public class TimeTypesTest extends AbstractAcceptanceTest {
    public void testFixedClock() {
        assertBothWays(Clock.fixed(Instant.parse("2017-02-15T18:49:25Z"), ZoneOffset.of("Z")), "" //
            + "<fixed-clock>\n"
            + "  <instant>2017-02-15T18:49:25Z</instant>\n"
            + "  <zone>Z</zone>\n"
            + "</fixed-clock>");
    }

    public void testOffsetClock() {
        assertBothWays(Clock.offset(Clock.systemUTC(), Duration.ofHours(1)), "" //
            + "<offset-clock>\n"
            + "  <baseClock class=\"system-clock\">\n"
            + "    <zone>Z</zone>\n"
            + "  </baseClock>\n"
            + "  <offset>PT1H</offset>\n"
            + "</offset-clock>");
    }

    public void testSystemClock() {
        assertBothWays(Clock.systemUTC(), "" //
            + "<system-clock>\n"
            + "  <zone>Z</zone>\n"
            + "</system-clock>");
        assertBothWays(Clock.system(ZoneId.of("Europe/Berlin")), "" //
            + "<system-clock>\n"
            + "  <zone>Europe/Berlin</zone>\n"
            + "</system-clock>");
    }

    public void testTickClock() {
        assertBothWays(Clock.tick(Clock.systemUTC(), Duration.ofMillis(42)), "" //
            + "<tick-clock>\n"
            + "  <baseClock class=\"system-clock\">\n"
            + "    <zone>Z</zone>\n"
            + "  </baseClock>\n"
            + "  <tickNanos>42000000</tickNanos>\n"
            + "</tick-clock>");
    }

    public void testDuration() {
        assertBothWays(Duration.ofDays(1000), "<duration>PT24000H</duration>");
        assertBothWays(Duration.ofHours(50), "<duration>PT50H</duration>");
        assertBothWays(Duration.ofMinutes(77), "<duration>PT1H17M</duration>");
        assertBothWays(Duration.ofSeconds(55), "<duration>PT55S</duration>");
        assertBothWays(Duration.ofMillis(4444), "<duration>PT4.444S</duration>");
        assertBothWays(Duration.ofNanos(123456789), "<duration>PT0.123456789S</duration>");
        assertBothWays(Duration.ofNanos(100000000), "<duration>PT0.1S</duration>");
        assertBothWays(Duration.ofNanos(9), "<duration>PT0.000000009S</duration>");
        assertBothWays(Duration.ofNanos(6333123456789L), "<duration>PT1H45M33.123456789S</duration>");
        assertBothWays(Duration.ofSeconds(-3), "<duration>PT-3S</duration>");
        assertBothWays(Duration.ofSeconds(-30001), "<duration>PT-8H-20M-1S</duration>");
    }

    public void testDurationConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<duration>PT77XS</duration>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(Duration.class.getName(), e.get("class"));
            assertEquals("PT77XS", e.get("value"));
        }
    }

    public void testDurationWithOldFormat() {
        assertEquals(Duration.ofSeconds(7777), xstream.fromXML("" //
            + "<java.time.Duration resolves-to=\"java.time.Ser\">\n"
            + "  <byte>1</byte>\n"
            + "  <long>7777</long>\n"
            + "  <int>0</int>\n"
            + "</java.time.Duration>"));
    }

    public void testDurationIsImmutable() {
        final Duration[] array = new Duration[2];
        array[0] = array[1] = Duration.ofHours(50);
        assertBothWays(array, "" //
            + "<duration-array>\n"
            + "  <duration>PT50H</duration>\n"
            + "  <duration>PT50H</duration>\n"
            + "</duration-array>");
    }

    public void testInstant() {
        assertBothWays(Instant.from(ZonedDateTime.of(2017, 7, 30, 20, 40, 0, 0, ZoneOffset.of("Z"))),
            "<instant>2017-07-30T20:40:00Z</instant>");
        assertBothWays(Instant.from(ZonedDateTime.of(2017, 7, 30, 20, 40, 0, 0, ZoneId.of("Europe/London"))),
            "<instant>2017-07-30T19:40:00Z</instant>");
        assertBothWays(Instant.from(ZonedDateTime.of(2017, 7, 30, 20, 40, 0, 0, ZoneId.of("Europe/Paris"))),
            "<instant>2017-07-30T18:40:00Z</instant>");
        assertBothWays(Instant.from(ZonedDateTime.of(2017, 7, 30, 20, 40, 0, 123456789, ZoneOffset.of("Z"))),
            "<instant>2017-07-30T20:40:00.123456789Z</instant>");
        assertBothWays(Instant.from(ZonedDateTime.of(2017, 7, 30, 20, 40, 0, 100000000, ZoneOffset.of("Z"))),
            "<instant>2017-07-30T20:40:00.100Z</instant>");
        assertBothWays(Instant.from(ZonedDateTime.of(2017, 7, 30, 20, 40, 0, 100000, ZoneOffset.of("Z"))),
            "<instant>2017-07-30T20:40:00.000100Z</instant>");
        assertBothWays(Instant.from(ZonedDateTime.of(2017, 7, 30, 20, 40, 0, 1000, ZoneOffset.of("Z"))),
            "<instant>2017-07-30T20:40:00.000001Z</instant>");
        assertBothWays(Instant.from(ZonedDateTime.of(2017, 7, 30, 20, 40, 0, 100, ZoneOffset.of("Z"))),
            "<instant>2017-07-30T20:40:00.000000100Z</instant>");
    }

    public void testInstantConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<instant>2017-07-30X20:40:00Z</instant>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(Instant.class.getName(), e.get("class"));
            assertEquals("2017-07-30X20:40:00Z", e.get("value"));
        }
    }

    public void testInstantWithOldFormat() {
        assertEquals(Instant.parse("2017-02-15T18:49:25Z"), xstream.fromXML("" //
            + "<java.time.Instant resolves-to=\"java.time.Ser\">\n"
            + "  <byte>2</byte>\n"
            + "  <long>1487184565</long>\n"
            + "  <int>0</int>\n"
            + "</java.time.Instant>"));
    }

    public void testInstantIsImmutable() {
        final Instant[] array = new Instant[2];
        array[0] = array[1] = Instant.from(ZonedDateTime.of(2017, 7, 30, 20, 40, 0, 0, ZoneOffset.of("Z")));
        assertBothWays(array, "" //
            + "<instant-array>\n"
            + "  <instant>2017-07-30T20:40:00Z</instant>\n"
            + "  <instant>2017-07-30T20:40:00Z</instant>\n"
            + "</instant-array>");
    }

    public void testPeriod() {
        assertBothWays(Period.ofDays(1000), "<period>P1000D</period>");
        assertBothWays(Period.ofWeeks(70), "<period>P490D</period>");
        assertBothWays(Period.ofMonths(70), "<period>P70M</period>");
        assertBothWays(Period.ofYears(2017), "<period>P2017Y</period>");
        assertBothWays(Period.of(-5, 70, -45), "<period>P-5Y70M-45D</period>");
        assertBothWays(Period.ofYears(0), "<period>P0D</period>");
        assertBothWays(Period.of(1, 0, 2), "<period>P1Y2D</period>");
        assertEquals(Period.ofDays(21), xstream.fromXML("<period>P3W</period>"));
    }

    public void testPeriodConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<period>P1YXD</period>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(Period.class.getName(), e.get("class"));
            assertEquals("P1YXD", e.get("period"));
        }
    }

    public void testPeriodWithOldFormat() {
        assertEquals(Period.ofDays(7777), xstream.fromXML("" //
            + "<java.time.Period resolves-to=\"java.time.Ser\">\n"
            + "  <byte>14</byte>\n"
            + "  <int>0</int>\n"
            + "  <int>0</int>\n"
            + "  <int>7777</int>\n"
            + "</java.time.Period>"));
    }

    public void testPeriodIsImmutable() {
        final Period[] array = new Period[2];
        array[0] = array[1] = Period.ofDays(1);
        assertBothWays(array, "" //
            + "<period-array>\n"
            + "  <period>P1D</period>\n"
            + "  <period>P1D</period>\n"
            + "</period-array>");
    }

    public void testLocalDate() {
        assertBothWays(LocalDate.of(2017, 10, 30), "<local-date>2017-10-30</local-date>");
    }

    public void testLocalDateConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<local-date>2017-13-30</local-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(LocalDate.class.getName(), e.get("class"));
            assertEquals("2017-13-30", e.get("value"));
        }
    }

    public void testLocalDateWithOldFormat() {
        assertEquals(LocalDate.of(2017, 10, 30), xstream.fromXML("" //
            + "<java.time.LocalDate resolves-to=\"java.time.Ser\">\n"
            + "  <byte>3</byte>\n"
            + "  <int>2017</int>\n"
            + "  <byte>10</byte>\n"
            + "  <byte>30</byte>\n"
            + "</java.time.LocalDate>"));
    }

    public void testLocalDateIsImmutable() {
        final LocalDate[] array = new LocalDate[2];
        array[0] = array[1] = LocalDate.of(2017, 10, 30);
        assertBothWays(array, "" //
            + "<local-date-array>\n"
            + "  <local-date>2017-10-30</local-date>\n"
            + "  <local-date>2017-10-30</local-date>\n"
            + "</local-date-array>");
    }

    public void testLocalDateTime() {
        assertBothWays(LocalDateTime.of(2017, 7, 30, 20, 40), "<local-date-time>2017-07-30T20:40:00</local-date-time>");
        assertBothWays(LocalDateTime.of(2017, 10, 30, 20, 40, 15),
            "<local-date-time>2017-10-30T20:40:15</local-date-time>");
        assertBothWays(LocalDateTime.of(2017, 10, 30, 20, 40, 15, 123456789),
            "<local-date-time>2017-10-30T20:40:15.123456789</local-date-time>");
        assertBothWays(LocalDateTime.of(2017, 10, 30, 20, 40, 15, 9),
            "<local-date-time>2017-10-30T20:40:15.000000009</local-date-time>");
        assertEquals(LocalDateTime.of(2017, 7, 30, 20, 40), xstream.fromXML(
            "<local-date-time>2017-07-30T20:40</local-date-time>"));
    }

    public void testLocalDateTimeConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<local-date-time>2017-13-30T20:40:00</local-date-time>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(LocalDateTime.class.getName(), e.get("class"));
            assertEquals("2017-13-30T20:40:00", e.get("value"));
        }
    }

    public void testLocalDateTimeWithOldFormat() {
        assertEquals(LocalDateTime.of(2017, 7, 30, 20, 40), xstream.fromXML("" //
            + "<java.time.LocalDateTime resolves-to=\"java.time.Ser\">\n"
            + "  <byte>5</byte>\n"
            + "  <int>2017</int>\n"
            + "  <byte>7</byte>\n"
            + "  <byte>30</byte>\n"
            + "  <byte>20</byte>\n"
            + "  <byte>-41</byte>\n"
            + "</java.time.LocalDateTime>"));
    }

    public void testLocalDateTimeIsImmutable() {
        final LocalDateTime[] array = new LocalDateTime[2];
        array[0] = array[1] = LocalDateTime.of(2017, 7, 30, 20, 40);
        assertBothWays(array, "" //
            + "<local-date-time-array>\n"
            + "  <local-date-time>2017-07-30T20:40:00</local-date-time>\n"
            + "  <local-date-time>2017-07-30T20:40:00</local-date-time>\n"
            + "</local-date-time-array>");
    }

    public void testLocalTime() {
        assertBothWays(LocalTime.of(10, 30), "<local-time>10:30:00</local-time>");
        assertBothWays(LocalTime.of(10, 30, 20), "<local-time>10:30:20</local-time>");
        assertBothWays(LocalTime.of(10, 30, 20, 123456789), "<local-time>10:30:20.123456789</local-time>");
        assertBothWays(LocalTime.of(10, 30, 20, 9), "<local-time>10:30:20.000000009</local-time>");
        assertBothWays(LocalTime.of(10, 30, 20, 1000000), "<local-time>10:30:20.001</local-time>");
        assertBothWays(LocalTime.of(10, 30, 20, 100000000), "<local-time>10:30:20.1</local-time>");
        assertEquals(LocalTime.of(10, 30), xstream.fromXML("<local-time>10:30</local-time>"));
    }

    public void testLocalTimeConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<local-time>10:30:77</local-time>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(LocalTime.class.getName(), e.get("class"));
            assertEquals("10:30:77", e.get("value"));
        }
    }

    public void testLocalTimeWithOldFormat() {
        assertEquals(LocalTime.of(10, 30), xstream.fromXML("" //
            + "<java.time.LocalTime resolves-to=\"java.time.Ser\">\n"
            + "  <byte>4</byte>\n"
            + "  <byte>10</byte>\n"
            + "  <byte>-31</byte>\n"
            + "</java.time.LocalTime>"));
    }

    public void testLocalTimeIsImmutable() {
        final LocalTime array[] = new LocalTime[2];
        array[0] = array[1] = LocalTime.of(10, 30);
        assertBothWays(array, "" //
            + "<local-time-array>\n"
            + "  <local-time>10:30:00</local-time>\n"
            + "  <local-time>10:30:00</local-time>\n"
            + "</local-time-array>");
    }

    public void testDayOfWeek() {
        assertBothWays(DayOfWeek.MONDAY, "<day-of-week>MONDAY</day-of-week>");
    }

    public void testMonth() {
        assertBothWays(Month.FEBRUARY, "<month>FEBRUARY</month>");
    }

    public void testMonthDay() {
        assertBothWays(MonthDay.of(1, 13), "<month-day>--01-13</month-day>");
        assertBothWays(MonthDay.of(2, 29), "<month-day>--02-29</month-day>");
    }

    public void testMonthDayConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<month-day>--00-13</month-day>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(MonthDay.class.getName(), e.get("class"));
            assertEquals("--00-13", e.get("value"));
        }
    }

    public void testMonthDayWithOldFormat() {
        assertEquals(MonthDay.of(Month.JANUARY, 13), xstream.fromXML("" //
            + "<java.time.MonthDay resolves-to=\"java.time.Ser\">\n"
            + "  <byte>13</byte>\n"
            + "  <byte>1</byte>\n"
            + "  <byte>13</byte>\n"
            + "</java.time.MonthDay>"));
    }

    public void testMonthDayIsImmutable() {
        final MonthDay array[] = new MonthDay[2];
        array[0] = array[1] = MonthDay.of(Month.APRIL, 10);
        assertBothWays(array, "" //
            + "<month-day-array>\n"
            + "  <month-day>--04-10</month-day>\n"
            + "  <month-day>--04-10</month-day>\n"
            + "</month-day-array>");
    }

    public void testOffsetDateTime() {
        assertBothWays(OffsetDateTime.of(2017, 7, 30, 20, 40, 0, 0, ZoneOffset.ofHours(0)),
            "<offset-date-time>2017-07-30T20:40:00Z</offset-date-time>");
        assertBothWays(OffsetDateTime.of(2017, 7, 30, 20, 40, 15, 0, ZoneOffset.ofHours(0)),
            "<offset-date-time>2017-07-30T20:40:15Z</offset-date-time>");
        assertBothWays(OffsetDateTime.of(2017, 7, 30, 20, 40, 15, 0, ZoneOffset.ofHours(1)),
            "<offset-date-time>2017-07-30T20:40:15+01:00</offset-date-time>");
        assertBothWays(OffsetDateTime.of(2017, 10, 30, 20, 40, 15, 123456789, ZoneOffset.ofHours(1)),
            "<offset-date-time>2017-10-30T20:40:15.123456789+01:00</offset-date-time>");
        assertBothWays(OffsetDateTime.of(2017, 10, 30, 20, 40, 15, 9, ZoneOffset.ofHours(1)),
            "<offset-date-time>2017-10-30T20:40:15.000000009+01:00</offset-date-time>");
        assertBothWays(OffsetDateTime.of(2017, 10, 30, 20, 40, 15, 1000000, ZoneOffset.ofHours(1)),
            "<offset-date-time>2017-10-30T20:40:15.001+01:00</offset-date-time>");
        assertBothWays(OffsetDateTime.of(2017, 10, 30, 20, 40, 15, 100000000, ZoneOffset.ofHours(1)),
            "<offset-date-time>2017-10-30T20:40:15.1+01:00</offset-date-time>");
        assertBothWays(OffsetDateTime.of(2017, 10, 30, 20, 40, 15, 123456789, ZoneOffset.ofHoursMinutesSeconds(1, 30,
            15)), "<offset-date-time>2017-10-30T20:40:15.123456789+01:30:15</offset-date-time>");
        assertEquals(OffsetDateTime.of(2017, 7, 30, 20, 40, 0, 0, ZoneOffset.ofHours(0)), xstream.fromXML(
            "<offset-date-time>2017-07-30T20:40Z</offset-date-time>"));
        assertEquals(OffsetDateTime.of(2017, 10, 30, 20, 40, 15, 100000000, ZoneOffset.ofHours(1)), xstream.fromXML(
            "<offset-date-time>2017-10-30T20:40:15.100+01:00</offset-date-time>"));
    }

    public void testOffsetDateTimeConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<offset-date-time>2017-07-30T27:40:00Z</offset-date-time>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(OffsetDateTime.class.getName(), e.get("class"));
            assertEquals("2017-07-30T27:40:00Z", e.get("value"));
        }
    }

    public void testOffsetDateTimeWithOldFormat() {
        assertEquals(OffsetDateTime.of(2017, 7, 30, 20, 40, 0, 0, ZoneOffset.ofHours(0)), xstream.fromXML("" //
            + "<java.time.OffsetDateTime resolves-to=\"java.time.Ser\">\n"
            + "  <byte>10</byte>\n"
            + "  <int>2017</int>\n"
            + "  <byte>7</byte>\n"
            + "  <byte>30</byte>\n"
            + "  <byte>20</byte>\n"
            + "  <byte>-41</byte>\n"
            + "  <byte>0</byte>\n"
            + "</java.time.OffsetDateTime>"));
    }

    public void testOffsetDateTimeIsImmutable() {
        final OffsetDateTime array[] = new OffsetDateTime[2];
        array[0] = array[1] = OffsetDateTime.of(2017, 7, 30, 20, 40, 15, 0, ZoneOffset.ofHours(1));
        assertBothWays(array, "" //
            + "<offset-date-time-array>\n"
            + "  <offset-date-time>2017-07-30T20:40:15+01:00</offset-date-time>\n"
            + "  <offset-date-time>2017-07-30T20:40:15+01:00</offset-date-time>\n"
            + "</offset-date-time-array>");
    }

    public void testOffsetTime() {
        assertBothWays(OffsetTime.of(20, 40, 0, 0, ZoneOffset.ofHours(0)), "<offset-time>20:40:00Z</offset-time>");
        assertBothWays(OffsetTime.of(20, 40, 15, 0, ZoneOffset.ofHours(0)), "<offset-time>20:40:15Z</offset-time>");
        assertBothWays(OffsetTime.of(20, 40, 15, 0, ZoneOffset.ofHours(1)),
            "<offset-time>20:40:15+01:00</offset-time>");
        assertBothWays(OffsetTime.of(20, 40, 15, 123456789, ZoneOffset.ofHours(1)),
            "<offset-time>20:40:15.123456789+01:00</offset-time>");
        assertBothWays(OffsetTime.of(20, 40, 15, 9, ZoneOffset.ofHours(1)),
            "<offset-time>20:40:15.000000009+01:00</offset-time>");
        assertBothWays(OffsetTime.of(20, 40, 15, 1000000, ZoneOffset.ofHours(1)),
            "<offset-time>20:40:15.001+01:00</offset-time>");
        assertBothWays(OffsetTime.of(20, 40, 15, 100000000, ZoneOffset.ofHours(1)),
            "<offset-time>20:40:15.1+01:00</offset-time>");
        assertBothWays(OffsetTime.of(20, 40, 15, 123456789, ZoneOffset.ofHoursMinutesSeconds(1, 30, 15)),
            "<offset-time>20:40:15.123456789+01:30:15</offset-time>");
        assertEquals(OffsetTime.of(20, 40, 0, 0, ZoneOffset.ofHours(0)), xstream.fromXML(
            "<offset-time>20:40Z</offset-time>"));
        assertEquals(OffsetTime.of(20, 40, 15, 100000000, ZoneOffset.ofHours(1)), xstream.fromXML(
            "<offset-time>20:40:15.100+01:00</offset-time>"));
    }

    public void testOffsetTimeConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<offset-time>20:77:00Z</offset-time>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(OffsetTime.class.getName(), e.get("class"));
            assertEquals("20:77:00Z", e.get("value"));
        }
    }

    public void testOffsetTimeWithOldFormat() {
        assertEquals(OffsetTime.of(20, 40, 0, 0, ZoneOffset.ofHours(0)), xstream.fromXML("" //
            + "<java.time.OffsetTime resolves-to=\"java.time.Ser\">\n"
            + "  <byte>9</byte>\n"
            + "  <byte>20</byte>\n"
            + "  <byte>-41</byte>\n"
            + "  <byte>0</byte>\n"
            + "</java.time.OffsetTime>"));
    }

    public void testOffsetTimeIsImmutable() {
        final OffsetTime array[] = new OffsetTime[2];
        array[0] = array[1] = OffsetTime.of(20, 40, 15, 0, ZoneOffset.ofHours(1));
        assertBothWays(array, "" //
            + "<offset-time-array>\n"
            + "  <offset-time>20:40:15+01:00</offset-time>\n"
            + "  <offset-time>20:40:15+01:00</offset-time>\n"
            + "</offset-time-array>");
    }

    public void testYear() {
        assertBothWays(Year.of(2017), "<year>2017</year>");
        assertBothWays(Year.of(0), "<year>0</year>");
        assertBothWays(Year.of(-1), "<year>-1</year>");
    }

    public void testYearConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<year>Z</year>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(Year.class.getName(), e.get("class"));
            assertEquals("Z", e.get("value"));
        }
    }

    public void testYearWithOldFormat() {
        assertEquals(Year.of(2017), xstream.fromXML("" //
            + "<java.time.Year resolves-to=\"java.time.Ser\">\n"
            + "  <byte>11</byte>\n"
            + "  <int>2017</int>\n"
            + "</java.time.Year>"));
    }

    public void testYearIsImmutable() {
        final Year array[] = new Year[2];
        array[0] = array[1] = Year.of(2017);
        assertBothWays(array, "" //
            + "<year-array>\n"
            + "  <year>2017</year>\n"
            + "  <year>2017</year>\n"
            + "</year-array>");
    }

    public void testYearMonth() {
        assertBothWays(YearMonth.of(2017, 2), "<year-month>2017-02</year-month>");
        assertBothWays(YearMonth.of(0, 2), "<year-month>0000-02</year-month>");
        assertBothWays(YearMonth.of(-1, 2), "<year-month>-0001-02</year-month>");
    }

    public void testYearMonthConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<year-month>Z-02</year-month>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(YearMonth.class.getName(), e.get("class"));
            assertEquals("Z-02", e.get("value"));
        }
    }

    public void testYearMonthWithOldFormat() {
        assertEquals(YearMonth.of(2017, 2), xstream.fromXML("" //
            + "<java.time.YearMonth resolves-to=\"java.time.Ser\">\n"
            + "  <byte>12</byte>\n"
            + "  <int>2017</int>\n"
            + "  <byte>2</byte>\n"
            + "</java.time.YearMonth>"));
    }

    public void testYearMonthIsImmutable() {
        final YearMonth array[] = new YearMonth[2];
        array[0] = array[1] = YearMonth.of(2017, 2);
        assertBothWays(array, "" //
            + "<year-month-array>\n"
            + "  <year-month>2017-02</year-month>\n"
            + "  <year-month>2017-02</year-month>\n"
            + "</year-month-array>");
    }

    public void testZonedDateTime() {
        assertBothWays(ZonedDateTime.of(2017, 10, 30, 20, 40, 0, 0, ZoneId.of("Europe/London")),
            "<zoned-date-time>2017-10-30T20:40:00Z[Europe/London]</zoned-date-time>");
        assertBothWays(ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 0, ZoneId.of("Europe/London")),
            "<zoned-date-time>2017-10-30T20:40:15Z[Europe/London]</zoned-date-time>");
        assertBothWays(ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 0, ZoneId.of("Europe/Paris")),
            "<zoned-date-time>2017-10-30T20:40:15+01:00[Europe/Paris]</zoned-date-time>");
        assertBothWays(ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 123456789, ZoneId.of("Europe/Paris")),
            "<zoned-date-time>2017-10-30T20:40:15.123456789+01:00[Europe/Paris]</zoned-date-time>");
        assertBothWays(ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 9, ZoneId.of("Europe/Paris")),
            "<zoned-date-time>2017-10-30T20:40:15.000000009+01:00[Europe/Paris]</zoned-date-time>");
        assertBothWays(ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 1000000, ZoneId.of("Europe/Paris")),
            "<zoned-date-time>2017-10-30T20:40:15.001+01:00[Europe/Paris]</zoned-date-time>");
        assertBothWays(ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 100000000, ZoneId.of("Europe/Paris")),
            "<zoned-date-time>2017-10-30T20:40:15.1+01:00[Europe/Paris]</zoned-date-time>");
        assertEquals(ZonedDateTime.of(2017, 10, 30, 20, 40, 0, 0, ZoneId.of("Europe/London")), xstream.fromXML(
            "<zoned-date-time>2017-10-30T20:40Z[Europe/London]</zoned-date-time>"));
        assertEquals(ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 100000000, ZoneId.of("Europe/Paris")), xstream.fromXML(
            "<zoned-date-time>2017-10-30T20:40:15.100+01:00[Europe/Paris]</zoned-date-time>"));
    }

    public void testZonedDateTimeConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<zoned-date-time>2017-10-30T20:40:00Z[Europe/Bonn]</zoned-date-time>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(ZonedDateTime.class.getName(), e.get("class"));
            assertEquals("2017-10-30T20:40:00Z[Europe/Bonn]", e.get("value"));
        }
    }

    public void testZonedDateTimeWithOldFormat() {
        assertEquals(ZonedDateTime.of(2017, 10, 30, 20, 40, 0, 0, ZoneId.of("Europe/London")), xstream.fromXML("" //
            + "<java.time.ZonedDateTime resolves-to=\"java.time.Ser\">\n"
            + "  <byte>6</byte>\n"
            + "  <int>2017</int>\n"
            + "  <byte>10</byte>\n"
            + "  <byte>30</byte>\n"
            + "  <byte>20</byte>\n"
            + "  <byte>-41</byte>\n"
            + "  <byte>0</byte>\n"
            + "  <byte>7</byte>\n"
            + "  <string>Europe/London</string>\n"
            + "</java.time.ZonedDateTime>"));
    }

    public void testZonedDateTimeIsImmutable() {
        final ZonedDateTime array[] = new ZonedDateTime[2];
        array[0] = array[1] = ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 0, ZoneId.of("Europe/Paris"));
        assertBothWays(array, ""
            + "<zoned-date-time-array>\n"
            + "  <zoned-date-time>2017-10-30T20:40:15+01:00[Europe/Paris]</zoned-date-time>\n"
            + "  <zoned-date-time>2017-10-30T20:40:15+01:00[Europe/Paris]</zoned-date-time>\n"
            + "</zoned-date-time-array>");
    }

    public void testZoneOffest() {
        assertBothWays(ZoneOffset.of("Z"), "<zone-id>Z</zone-id>");
        assertBothWays(ZoneOffset.ofTotalSeconds(7777), "<zone-id>+02:09:37</zone-id>");
        assertBothWays(ZoneId.ofOffset("GMT", ZoneOffset.ofTotalSeconds(7777)), "<zone-id>GMT+02:09:37</zone-id>");
        assertBothWays(ZoneId.of("ECT", ZoneId.SHORT_IDS), "<zone-id>Europe/Paris</zone-id>");
        assertBothWays(ZoneId.of("CET"), "<zone-id>CET</zone-id>");
    }

    public void testZoneIdConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<zone-id>X</zone-id>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(ZoneId.class.getName(), e.get("class"));
            assertEquals("X", e.get("value"));
        }
        try {
            xstream.fromXML("<zone-id>Europe/X</zone-id>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(ZoneId.class.getName(), e.get("class"));
            assertEquals("Europe/X", e.get("value"));
        }
    }

    public void testZoneOffestWithOldFormat() {
        assertEquals(ZoneOffset.ofTotalSeconds(7777), xstream.fromXML("" //
            + "<java.time.ZoneOffset resolves-to=\"java.time.Ser\">\n"
            + "  <byte>8</byte>\n"
            + "  <byte>127</byte>\n"
            + "  <int>7777</int>\n"
            + "</java.time.ZoneOffset>"));
    }

    public void testZoneOffestIsImmutable() {
        final ZoneOffset[] array = new ZoneOffset[2];
        array[0] = array[1] = ZoneOffset.of("Z");
        assertBothWays(array, "" //
            + "<zone-id-array>\n"
            + "  <zone-id>Z</zone-id>\n"
            + "  <zone-id>Z</zone-id>\n"
            + "</zone-id-array>");
    }

    public void testZoneRegion() {
        assertBothWays(ZoneId.of("America/Caracas"), "<zone-id>America/Caracas</zone-id>");
        assertBothWays(ZoneId.of("Europe/Berlin"), "<zone-id>Europe/Berlin</zone-id>");
    }

    public void testZoneRegionWithOldFormat() {
        assertEquals(ZoneId.of("America/Caracas"), xstream.fromXML("" //
            + "<java.time.ZoneRegion resolves-to=\"java.time.Ser\">\n"
            + "  <byte>7</byte>\n"
            + "  <string>America/Caracas</string>\n"
            + "</java.time.ZoneRegion>"));
    }

    public void testZoneRegionIsImmutable() {
        final ZoneId[] array = new ZoneId[2];
        array[0] = array[1] = ZoneId.of("Europe/Rome");
        assertBothWays(array, "" //
            + "<zone-id-array>\n"
            + "  <zone-id>Europe/Rome</zone-id>\n"
            + "  <zone-id>Europe/Rome</zone-id>\n"
            + "</zone-id-array>");
    }

    public void testChronology() {
        assertBothWays(IsoChronology.INSTANCE, "<chronology>ISO</chronology>");
        assertBothWays(JapaneseChronology.INSTANCE, "<chronology>Japanese</chronology>");
    }

    public void testChronologyConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<chronology>Z</chronology>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(Chronology.class.getName(), e.get("class"));
            assertEquals("Z", e.get("value"));
        }
    }

    public void testChronologyWithOldFormat() {
        assertSame(IsoChronology.INSTANCE, xstream.fromXML("" //
            + "<java.time.chrono.IsoChronology resolves-to=\"java.time.chrono.Ser\">\n"
            + "  <byte>1</byte>\n"
            + "  <string>ISO</string>\n"
            + "</java.time.chrono.IsoChronology>"));
    }

    public void testChronologyIsImmutable() {
        final Chronology[] array = new Chronology[2];
        array[0] = array[1] = IsoChronology.INSTANCE;
        assertBothWays(array, "" //
            + "<chronology-array>\n"
            + "  <chronology>ISO</chronology>\n"
            + "  <chronology>ISO</chronology>\n"
            + "</chronology-array>");
    }

    public void testHijrahDate() {
        assertBothWays(HijrahChronology.INSTANCE.date(LocalDate.of(2017, 7, 30)),
            "<hijrah-date>Hijrah-umalqura AH 1438-11-07</hijrah-date>");
    }

    public void testHijrahDateWithOldFormat() {
        assertEquals(HijrahChronology.INSTANCE.date(LocalDate.of(2017, 7, 30)), xstream.fromXML("" //
            + "<java.time.chrono.HijrahDate resolves-to=\"java.time.chrono.Ser\">\n"
            + "  <byte>6</byte>\n"
            + "  <java.time.chrono.HijrahChronology resolves-to=\"java.time.chrono.Ser\">\n"
            + "    <byte>1</byte>\n"
            + "    <string>Hijrah-umalqura</string>\n"
            + "  </java.time.chrono.HijrahChronology>\n"
            + "  <int>1438</int>\n"
            + "  <byte>11</byte>\n"
            + "  <byte>7</byte>\n"
            + "</java.time.chrono.HijrahDate>"));
    }

    public void testHijrahDateConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<hijrah-date>Hijrah-X AH 1438-11-07</hijrah-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(HijrahDate.class.getName(), e.get("class"));
            assertEquals("Hijrah-X AH 1438-11-07", e.get("value"));
        }
        try {
            xstream.fromXML("<hijrah-date>Hijrah-umalqura X 1438-11-07</hijrah-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(HijrahDate.class.getName(), e.get("class"));
            assertEquals("Hijrah-umalqura X 1438-11-07", e.get("value"));
        }
        try {
            xstream.fromXML("<hijrah-date>Hijrah-umalqura AH 1438-42-07</hijrah-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(HijrahDate.class.getName(), e.get("class"));
            assertEquals("Hijrah-umalqura AH 1438-42-07", e.get("value"));
        }
    }

    public void testHijrahDateIsImmutable() {
        final HijrahDate[] array = new HijrahDate[2];
        array[0] = array[1] = HijrahChronology.INSTANCE.date(LocalDate.of(2017, 7, 30));
        assertBothWays(array, "" //
            + "<hijrah-date-array>\n"
            + "  <hijrah-date>Hijrah-umalqura AH 1438-11-07</hijrah-date>\n"
            + "  <hijrah-date>Hijrah-umalqura AH 1438-11-07</hijrah-date>\n"
            + "</hijrah-date-array>");
    }

    public void testHijrahEra() {
        assertBothWays(HijrahEra.AH, "<hijrah-era>AH</hijrah-era>");
    }

    public void testJapaneseDate() {
        assertBothWays(JapaneseChronology.INSTANCE.date(LocalDate.of(2017, 7, 30)),
            "<japanese-date>Japanese Heisei 29-07-30</japanese-date>");
    }

    public void testJapaneseDateWithOldFormat() {
        assertEquals(JapaneseChronology.INSTANCE.date(LocalDate.of(2017, 7, 30)), xstream.fromXML("" //
            + "<java.time.chrono.JapaneseDate resolves-to=\"java.time.chrono.Ser\">\n"
            + "  <byte>4</byte>\n"
            + "  <int>2017</int>\n"
            + "  <byte>7</byte>\n"
            + "  <byte>30</byte>\n"
            + "</java.time.chrono.JapaneseDate>"));
    }

    public void testJapaneseDateConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<japanese-date>Chinese Heisei 29-07-30</japanese-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(JapaneseDate.class.getName(), e.get("class"));
            assertEquals("Chinese Heisei 29-07-30", e.get("value"));
        }
        try {
            xstream.fromXML("<japanese-date>Japanese Mitsubishi 29-07-30</japanese-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(JapaneseDate.class.getName(), e.get("class"));
            assertEquals("Japanese Mitsubishi 29-07-30", e.get("value"));
        }
        try {
            xstream.fromXML("<japanese-date>Japanese Heisei 29-13-30</japanese-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(JapaneseDate.class.getName(), e.get("class"));
            assertEquals("Japanese Heisei 29-13-30", e.get("value"));
        }
    }

    public void testJapaneseDateIsImmutable() {
        final JapaneseDate[] array = new JapaneseDate[2];
        array[0] = array[1] = JapaneseChronology.INSTANCE.date(LocalDate.of(2017, 7, 30));
        assertBothWays(array, "" //
            + "<japanese-date-array>\n"
            + "  <japanese-date>Japanese Heisei 29-07-30</japanese-date>\n"
            + "  <japanese-date>Japanese Heisei 29-07-30</japanese-date>\n"
            + "</japanese-date-array>");
    }

    public void testJapaneseEra() {
        assertBothWays(JapaneseEra.TAISHO, "<japanese-era>Taisho</japanese-era>");
    }

    public void testJapaneseEraWithOldFormat() {
        assertEquals(JapaneseEra.TAISHO, xstream.fromXML("" //
            + "<java.time.chrono.JapaneseEra resolves-to=\"java.time.chrono.Ser\">\n"
            + "  <byte>5</byte>\n"
            + "  <byte>0</byte>\n"
            + "</java.time.chrono.JapaneseEra>"));
    }

    public void testJapaneseEraConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<japanese-era>NEMO</japanese-era>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(JapaneseEra.class.getName(), e.get("class"));
            assertEquals("NEMO", e.get("value"));
        }
    }

    public void testJapaneseEraIsImmutable() {
        final JapaneseEra[] array = new JapaneseEra[2];
        array[0] = array[1] = JapaneseEra.SHOWA;
        assertBothWays(array, "" //
            + "<japanese-era-array>\n"
            + "  <japanese-era>Showa</japanese-era>\n"
            + "  <japanese-era>Showa</japanese-era>\n"
            + "</japanese-era-array>");
    }

    public void testMinguoDate() {
        assertBothWays(MinguoChronology.INSTANCE.date(LocalDate.of(2017, 7, 30)),
            "<minguo-date>Minguo ROC 106-07-30</minguo-date>");
    }

    public void testMinguoDateWithOldFormat() {
        assertEquals(MinguoChronology.INSTANCE.date(LocalDate.of(2017, 7, 30)), xstream.fromXML("" //
            + "<java.time.chrono.MinguoDate resolves-to=\"java.time.chrono.Ser\">\n"
            + "  <byte>7</byte>\n"
            + "  <int>106</int>\n"
            + "  <byte>7</byte>\n"
            + "  <byte>30</byte>\n"
            + "</java.time.chrono.MinguoDate>"));
    }

    public void testMinguoDateConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<minguo-date>Chinese ROC 106-07-30</minguo-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(MinguoDate.class.getName(), e.get("class"));
            assertEquals("Chinese ROC 106-07-30", e.get("value"));
        }
        try {
            xstream.fromXML("<minguo-date>Minguo X 106-07-30</minguo-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(MinguoDate.class.getName(), e.get("class"));
            assertEquals("Minguo X 106-07-30", e.get("value"));
        }
        try {
            xstream.fromXML("<minguo-date>Minguo ROC 106-13-30</minguo-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(MinguoDate.class.getName(), e.get("class"));
            assertEquals("Minguo ROC 106-13-30", e.get("value"));
        }
    }

    public void testMinguoDateIsImmutable() {
        final MinguoDate[] array = new MinguoDate[2];
        array[0] = array[1] = MinguoChronology.INSTANCE.date(LocalDate.of(2017, 7, 30));
        assertBothWays(array, "" //
            + "<minguo-date-array>\n"
            + "  <minguo-date>Minguo ROC 106-07-30</minguo-date>\n"
            + "  <minguo-date>Minguo ROC 106-07-30</minguo-date>\n"
            + "</minguo-date-array>");
    }

    public void testMinguoEra() {
        assertBothWays(MinguoEra.ROC, "<minguo-era>ROC</minguo-era>");
    }

    public void testThaiBuddhistDate() {
        assertBothWays(ThaiBuddhistChronology.INSTANCE.date(LocalDate.of(2017, 7, 30)),
            "<thai-buddhist-date>ThaiBuddhist BE 2560-07-30</thai-buddhist-date>");
    }

    public void testThaiBuddhistDateWithOldFormat() {
        assertEquals(ThaiBuddhistChronology.INSTANCE.date(LocalDate.of(2017, 7, 30)), xstream.fromXML("" //
            + "<java.time.chrono.ThaiBuddhistDate resolves-to=\"java.time.chrono.Ser\">\n"
            + "  <byte>8</byte>\n"
            + "  <int>2560</int>\n"
            + "  <byte>7</byte>\n"
            + "  <byte>30</byte>\n"
            + "</java.time.chrono.ThaiBuddhistDate>"));
    }

    public void testThaiBuddhistDateConversionExceptionContainsInvalidValue() {
        try {
            xstream.fromXML("<thai-buddhist-date>Chinese BE 2560-07-30</thai-buddhist-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(ThaiBuddhistDate.class.getName(), e.get("class"));
            assertEquals("Chinese BE 2560-07-30", e.get("value"));
        }
        try {
            xstream.fromXML("<thai-buddhist-date>ThaiBuddhist X 2560-07-30</thai-buddhist-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(ThaiBuddhistDate.class.getName(), e.get("class"));
            assertEquals("ThaiBuddhist X 2560-07-30", e.get("value"));
        }
        try {
            xstream.fromXML("<thai-buddhist-date>ThaiBuddhist BE 2560-13-30</thai-buddhist-date>");
            fail("Thrown " + ConversionException.class.getName() + " expected");
        } catch (final ConversionException e) {
            assertEquals(ThaiBuddhistDate.class.getName(), e.get("class"));
            assertEquals("ThaiBuddhist BE 2560-13-30", e.get("value"));
        }
    }

    public void testThaiBuddhistDateIsImmutable() {
        final ThaiBuddhistDate[] array = new ThaiBuddhistDate[2];
        array[0] = array[1] = ThaiBuddhistChronology.INSTANCE.date(LocalDate.of(2017, 7, 30));
        assertBothWays(array, "" //
            + "<thai-buddhist-date-array>\n"
            + "  <thai-buddhist-date>ThaiBuddhist BE 2560-07-30</thai-buddhist-date>\n"
            + "  <thai-buddhist-date>ThaiBuddhist BE 2560-07-30</thai-buddhist-date>\n"
            + "</thai-buddhist-date-array>");
    }

    public void testThaiBuddhistEra() {
        assertBothWays(ThaiBuddhistEra.BE, "<thai-buddhist-era>BE</thai-buddhist-era>");
    }

    public void testChronoField() {
        assertBothWays(ChronoField.ERA, "<chrono-field>ERA</chrono-field>");
    }

    public void testChronoUnit() {
        assertBothWays(ChronoUnit.DECADES, "<chrono-unit>DECADES</chrono-unit>");
    }

    public void testIsoFields() {
        assertBothWays(IsoFields.DAY_OF_QUARTER, "<iso-field>DAY_OF_QUARTER</iso-field>");
        assertBothWays(IsoFields.QUARTER_OF_YEAR, "<iso-field>QUARTER_OF_YEAR</iso-field>");
        assertBothWays(IsoFields.QUARTER_YEARS, "<iso-unit>QUARTER_YEARS</iso-unit>");
        assertBothWays(IsoFields.WEEK_BASED_YEAR, "<iso-field>WEEK_BASED_YEAR</iso-field>");
        assertBothWays(IsoFields.WEEK_BASED_YEARS, "<iso-unit>WEEK_BASED_YEARS</iso-unit>");
        assertBothWays(IsoFields.WEEK_OF_WEEK_BASED_YEAR, "<iso-field>WEEK_OF_WEEK_BASED_YEAR</iso-field>");
    }

    public void testIsoFieldsAreImmutable() {
        final Object[] array = new Object[4];
        array[0] = array[1] = IsoFields.DAY_OF_QUARTER;
        array[2] = array[3] = IsoFields.QUARTER_YEARS;
        assertBothWays(array, "" //
            + "<object-array>\n"
            + "  <iso-field>DAY_OF_QUARTER</iso-field>\n"
            + "  <iso-field>DAY_OF_QUARTER</iso-field>\n"
            + "  <iso-unit>QUARTER_YEARS</iso-unit>\n"
            + "  <iso-unit>QUARTER_YEARS</iso-unit>\n"
            + "</object-array>");
    }

    public void testJulianFields() {
        assertBothWays(JulianFields.JULIAN_DAY, "<julian-field>JULIAN_DAY</julian-field>");
        assertBothWays(JulianFields.MODIFIED_JULIAN_DAY, "<julian-field>MODIFIED_JULIAN_DAY</julian-field>");
        assertBothWays(JulianFields.RATA_DIE, "<julian-field>RATA_DIE</julian-field>");
    }

    public void testJulianFieldsAreImmutable() {
        final TemporalField[] array = new TemporalField[2];
        array[0] = array[1] = JulianFields.JULIAN_DAY;
        assertBothWays(array, "" //
            + "<java.time.temporal.TemporalField-array>\n"
            + "  <julian-field>JULIAN_DAY</julian-field>\n"
            + "  <julian-field>JULIAN_DAY</julian-field>\n"
            + "</java.time.temporal.TemporalField-array>");
    }

    public void testValueRange() {
        assertBothWays(ValueRange.of(0, 1, 30, 45), "" //
            + "<temporal-value-range>\n"
            + "  <maxLargest>45</maxLargest>\n"
            + "  <maxSmallest>30</maxSmallest>\n"
            + "  <minLargest>1</minLargest>\n"
            + "  <minSmallest>0</minSmallest>\n"
            + "</temporal-value-range>");
    }

    public void testValueRangeWithAlias() {
        xstream.aliasField("max", ValueRange.class, "maxLargest");
        xstream.aliasField("max-s", ValueRange.class, "maxSmallest");
        xstream.aliasField("min-l", ValueRange.class, "minLargest");
        xstream.aliasField("min", ValueRange.class, "minSmallest");
        assertBothWays(ValueRange.of(0, 1, 30, 45), "" //
            + "<temporal-value-range>\n"
            + "  <max>45</max>\n"
            + "  <max-s>30</max-s>\n"
            + "  <min-l>1</min-l>\n"
            + "  <min>0</min>\n"
            + "</temporal-value-range>");
    }

    public void testValueRangeWithOldFormat() {
        assertEquals(ValueRange.of(0, 1, 30, 45), xstream.fromXML("" //
            + "<java.time.temporal.ValueRange serialization=\"custom\">\n"
            + "  <java.time.temporal.ValueRange>\n"
            + "    <default>\n"
            + "      <maxLargest>45</maxLargest>\n"
            + "      <maxSmallest>30</maxSmallest>\n"
            + "      <minLargest>1</minLargest>\n"
            + "      <minSmallest>0</minSmallest>\n"
            + "    </default>\n"
            + "  </java.time.temporal.ValueRange>\n"
            + "</java.time.temporal.ValueRange>"));
    }

    public void testWeekFields() {
        assertBothWays(WeekFields.of(DayOfWeek.TUESDAY, 2), "" //
            + "<week-fields>\n"
            + "  <minimalDays>2</minimalDays>\n"
            + "  <firstDayOfWeek>TUESDAY</firstDayOfWeek>\n"
            + "</week-fields>");
        assertBothWays(WeekFields.ISO, "" //
            + "<week-fields>\n"
            + "  <minimalDays>4</minimalDays>\n"
            + "  <firstDayOfWeek>MONDAY</firstDayOfWeek>\n"
            + "</week-fields>");
    }

    public void testWeekFieldsWithFieldAlias() {
        xstream.aliasField("days", WeekFields.class, "minimalDays");
        xstream.aliasField("week-day", WeekFields.class, "firstDayOfWeek");
        assertBothWays(WeekFields.of(DayOfWeek.TUESDAY, 2), "" //
            + "<week-fields>\n"
            + "  <days>2</days>\n"
            + "  <week-day>TUESDAY</week-day>\n"
            + "</week-fields>");
    }

    public void testWeekFieldsWithOldFormat() {
        assertEquals(WeekFields.of(DayOfWeek.TUESDAY, 2), xstream.fromXML("" //
            + "<java.time.temporal.WeekFields serialization=\"custom\">\n"
            + "  <java.time.temporal.WeekFields>\n"
            + "    <default>\n"
            + "      <minimalDays>2</minimalDays>\n"
            + "      <firstDayOfWeek>TUESDAY</firstDayOfWeek>\n"
            + "    </default>\n"
            + "  </java.time.temporal.WeekFields>\n"
            + "</java.time.temporal.WeekFields>"));
    }
}
