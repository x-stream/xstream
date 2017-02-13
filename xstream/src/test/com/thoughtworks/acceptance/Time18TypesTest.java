/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 13. January 2017 by Matej Cimbora
 */
package com.thoughtworks.acceptance;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import com.thoughtworks.xstream.XStream;


/**
 * @author Matej Cimbora
 * @author J&ouml;rg Schaible
 */
public class Time18TypesTest extends AbstractAcceptanceTest {

    @Override
    protected void setupSecurity(XStream xstream) {
        super.setupSecurity(xstream);
        xstream.allowTypeHierarchy(Temporal.class);
    }

    public void testZoneOffest() {
        assertBothWays(ZoneOffset.of("Z"), "<zone-id>Z</zone-id>");
        assertBothWays(ZoneOffset.ofTotalSeconds(7777), "<zone-id>+02:09:37</zone-id>");
        assertBothWays(ZoneId.ofOffset("GMT", ZoneOffset.ofTotalSeconds(7777)), "<zone-id>GMT+02:09:37</zone-id>");
        assertBothWays(ZoneId.of("ECT", ZoneId.SHORT_IDS), "<zone-id>Europe/Paris</zone-id>");
        assertBothWays(ZoneId.of("CET"), "<zone-id>CET</zone-id>");
    }

    public void testZoneOffestWithOldFormat() {
        assertEquals(ZoneOffset.ofTotalSeconds(7777), xstream.fromXML("" //
            + "<java.time.ZoneOffset resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>8</byte>\n" //
            + "  <byte>127</byte>\n" //
            + "  <int>7777</int>\n" //
            + "</java.time.ZoneOffset>"));
    }

    public void testZoneRegion() {
        assertBothWays(ZoneId.of("America/Caracas"), "<zone-id>America/Caracas</zone-id>");
        assertBothWays(ZoneId.of("Europe/Berlin"), "<zone-id>Europe/Berlin</zone-id>");
    }

    public void testZoneRegionWithOldFormat() {
        assertEquals(ZoneId.of("America/Caracas"), xstream.fromXML("" //
            + "<java.time.ZoneRegion resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>7</byte>\n" //
            + "  <string>America/Caracas</string>\n" //
            + "</java.time.ZoneRegion>"));
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

    public void testDurationWithOldFormat() {
        assertEquals(Duration.ofSeconds(7777), xstream.fromXML("" //
            + "<java.time.Duration resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>1</byte>\n" //
            + "  <long>7777</long>\n" //
            + "  <int>0</int>\n" //
            + "</java.time.Duration>"));
    }

    public void testDurationIsImmutable() {
        final Duration[] array = new Duration[2];
        array[0] = array[1] = Duration.ofHours(50);
        assertBothWays(array, "" //
            + "<duration-array>\n" //
            + "  <duration>PT50H</duration>\n" //
            + "  <duration>PT50H</duration>\n" //
            + "</duration-array>");
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

    public void testPeriodWithOldFormat() {
        assertEquals(Period.ofDays(7777), xstream.fromXML("" //
            + "<java.time.Period resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>14</byte>\n" //
            + "  <int>0</int>\n" //
            + "  <int>0</int>\n" //
            + "  <int>7777</int>\n" //
            + "</java.time.Period>"));
    }

    public void testPeriodIsImmutable() {
        final Period[] array = new Period[2];
        array[0] = array[1] = Period.ofDays(1);
        assertBothWays(array, "" //
            + "<period-array>\n" //
            + "  <period>P1D</period>\n" //
            + "  <period>P1D</period>\n" //
            + "</period-array>");
    }

    public void testLocalDate() {
        assertBothWays(LocalDate.of(2017, 10, 30), "<local-date>2017-10-30</local-date>");
    }

    public void testLocalDateWithOldFormat() {
        assertEquals(LocalDate.of(2017, 10, 30), xstream.fromXML("" //
            + "<java.time.LocalDate resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>3</byte>\n" //
            + "  <int>2017</int>\n" //
            + "  <byte>10</byte>\n" //
            + "  <byte>30</byte>\n" //
            + "</java.time.LocalDate>"));
    }

    public void testLocalDateIsImmutable() {
        final LocalDate[] array = new LocalDate[2];
        array[0] = array[1] = LocalDate.of(2017, 10, 30);
        assertBothWays(array, "" //
            + "<local-date-array>\n" //
            + "  <local-date>2017-10-30</local-date>\n" //
            + "  <local-date>2017-10-30</local-date>\n" //
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

    public void testLocalDateTimeWithOldFormat() {
        assertEquals(LocalDateTime.of(2017, 7, 30, 20, 40), xstream.fromXML("" //
            + "<java.time.LocalDateTime resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>5</byte>\n" //
            + "  <int>2017</int>\n" //
            + "  <byte>7</byte>\n" //
            + "  <byte>30</byte>\n" //
            + "  <byte>20</byte>\n" //
            + "  <byte>-41</byte>\n" //
            + "</java.time.LocalDateTime>"));
    }

    public void testLocalDateTimeIsImmutable() {
        final LocalDateTime[] array = new LocalDateTime[2];
        array[0] = array[1] = LocalDateTime.of(2017, 7, 30, 20, 40);
        assertBothWays(array, "" //
            + "<local-date-time-array>\n" //
            + "  <local-date-time>2017-07-30T20:40:00</local-date-time>\n" //
            + "  <local-date-time>2017-07-30T20:40:00</local-date-time>\n" //
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

    public void testLocalTimeWithOldFormat() {
        assertEquals(LocalTime.of(10, 30), xstream.fromXML("" //
            + "<java.time.LocalTime resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>4</byte>\n" //
            + "  <byte>10</byte>\n" //
            + "  <byte>-31</byte>\n" //
            + "</java.time.LocalTime>"));
    }

    public void testLocalTimeIsImmutable() {
        final LocalTime array[] = new LocalTime[2];
        array[0] = array[1] = LocalTime.of(10, 30);
        assertBothWays(array, "" //
            + "<local-time-array>\n"
            + "  <local-time>10:30:00</local-time>\n" //
            + "  <local-time>10:30:00</local-time>\n" //
            + "</local-time-array>");
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

    public void testOffsetDateTimeWithOldFormat() {
        assertEquals(OffsetDateTime.of(2017, 7, 30, 20, 40, 0, 0, ZoneOffset.ofHours(0)), xstream.fromXML("" //
            + "<java.time.OffsetDateTime resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>10</byte>\n" //
            + "  <int>2017</int>\n" //
            + "  <byte>7</byte>\n" //
            + "  <byte>30</byte>\n" //
            + "  <byte>20</byte>\n" //
            + "  <byte>-41</byte>\n" //
            + "  <byte>0</byte>\n" //
            + "</java.time.OffsetDateTime>"));
    }

    public void testOffsetDateTimeIsImmutable() {
        final OffsetDateTime array[] = new OffsetDateTime[2];
        array[0] = array[1] = OffsetDateTime.of(2017, 7, 30, 20, 40, 15, 0, ZoneOffset.ofHours(1));
        assertBothWays(array, "" //
            + "<offset-date-time-array>\n"
            + "  <offset-date-time>2017-07-30T20:40:15+01:00</offset-date-time>\n" //
            + "  <offset-date-time>2017-07-30T20:40:15+01:00</offset-date-time>\n" //
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

    public void testOffsetTimeWithOldFormat() {
        assertEquals(OffsetTime.of(20, 40, 0, 0, ZoneOffset.ofHours(0)), xstream.fromXML("" //
            + "<java.time.OffsetTime resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>9</byte>\n" //
            + "  <byte>20</byte>\n" //
            + "  <byte>-41</byte>\n" //
            + "  <byte>0</byte>\n" //
            + "</java.time.OffsetTime>"));
    }

    public void testOffsetTimeIsImmutable() {
        final OffsetTime array[] = new OffsetTime[2];
        array[0] = array[1] = OffsetTime.of(20, 40, 15, 0, ZoneOffset.ofHours(1));
        assertBothWays(array, "" //
            + "<offset-time-array>\n"
            + "  <offset-time>20:40:15+01:00</offset-time>\n" //
            + "  <offset-time>20:40:15+01:00</offset-time>\n" //
            + "</offset-time-array>");
    }

    public void testYear() {
        assertBothWays(Year.of(2017), "<year>2017</year>");
        assertBothWays(Year.of(0), "<year>0</year>");
        assertBothWays(Year.of(-1), "<year>-1</year>");
    }

    public void testYearWithOldFormat() {
        assertEquals(Year.of(2017), xstream.fromXML("" //
            + "<java.time.Year resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>11</byte>\n" //
            + "  <int>2017</int>\n" //
            + "</java.time.Year>"));
    }

    public void testYearIsImmutable() {
        final Year array[] = new Year[2];
        array[0] = array[1] = Year.of(2017);
        assertBothWays(array, "" //
            + "<year-array>\n"
            + "  <year>2017</year>\n" //
            + "  <year>2017</year>\n" //
            + "</year-array>");
    }

    public void testYearMonth() {
        assertBothWays(YearMonth.of(2017, 2), "<year-month>2017-02</year-month>");
    }

    public void testYearMonthWithOldFormat() {
        assertEquals(YearMonth.of(2017, 2), xstream.fromXML("" //
            + "<java.time.YearMonth resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>12</byte>\n" //
            + "  <int>2017</int>\n" //
            + "  <byte>2</byte>\n" //
            + "</java.time.YearMonth>"));
    }

    public void testYearMonthIsImmutable() {
        final YearMonth array[] = new YearMonth[2];
        array[0] = array[1] = YearMonth.of(2017, 2);
        assertBothWays(array, "" //
            + "<year-month-array>\n"
            + "  <year-month>2017-02</year-month>\n" //
            + "  <year-month>2017-02</year-month>\n" //
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

    public void testZonedDateTimeWithOldFormat() {
        assertEquals(ZonedDateTime.of(2017, 10, 30, 20, 40, 0, 0, ZoneId.of("Europe/London")), xstream.fromXML("" //
            + "<java.time.ZonedDateTime resolves-to=\"java.time.Ser\">\n" //
            + "  <byte>6</byte>\n" //
            + "  <int>2017</int>\n" //
            + "  <byte>10</byte>\n" //
            + "  <byte>30</byte>\n" //
            + "  <byte>20</byte>\n" //
            + "  <byte>-41</byte>\n" //
            + "  <byte>0</byte>\n" //
            + "  <byte>7</byte>\n" //
            + "  <string>Europe/London</string>\n" //
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
}
