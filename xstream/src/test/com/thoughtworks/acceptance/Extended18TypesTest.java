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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;

import com.thoughtworks.xstream.XStream;


/**
 * @author Matej Cimbora
 * @author J&ouml;rg Schaible
 */
public class Extended18TypesTest extends AbstractAcceptanceTest {

    @Override
    protected void setupSecurity(XStream xstream) {
        super.setupSecurity(xstream);
        xstream.allowTypeHierarchy(Temporal.class);
    }

    public void testLocalDate() {
        assertBothWays(LocalDate.of(2017, 10, 30), "<local-date>2017-10-30</local-date>");
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

    public void testZonedDateTime() {
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
