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

/**
 * @author Matej Cimbora
 */
public class Extended18TypesTest extends AbstractAcceptanceTest {

    public void testLocalDate() {
        LocalDate localDate = LocalDate.of(2017, 10, 30);
        assertBothWays(localDate, "<local-date>2017-10-30</local-date>");
    }

    public void testLocalDateTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 10, 30, 20, 40, 15, 123456789);
        assertBothWays(localDateTime, "<local-date-time>2017-10-30T20:40:15.123456789</local-date-time>");
    }

    public void testLocalDateTimeNanosOmitted() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 10, 30, 20, 40, 15);
        assertBothWays(localDateTime, "<local-date-time>2017-10-30T20:40:15</local-date-time>");
    }

    public void testLocalTime() {
        LocalTime localTime = LocalTime.of(10, 30, 20, 123456789);
        assertBothWays(localTime, "<local-time>10:30:20.123456789</local-time>");
    }

    public void testLocalTimeNanosOmitted() {
        LocalTime localTime = LocalTime.of(10, 30, 20);
        assertBothWays(localTime, "<local-time>10:30:20</local-time>");
    }

    public void testOffsetDateTime() {
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2017, 10, 30, 20, 40, 15, 123456789, ZoneOffset.ofHours(1));
        assertBothWays(offsetDateTime, "<offset-date-time>2017-10-30T20:40:15.123456789+01:00</offset-date-time>");
    }

    public void testOffsetDateTimeNanosOmitted() {
        OffsetDateTime offsetDateTime = OffsetDateTime.of(2017, 10, 30, 20, 40, 15, 0, ZoneOffset.ofHours(1));
        assertBothWays(offsetDateTime, "<offset-date-time>2017-10-30T20:40:15+01:00</offset-date-time>");
    }

    public void testZonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 123456789, ZoneId.of("Europe/Paris"));
        assertBothWays(zonedDateTime, "<zoned-date-time>2017-10-30T20:40:15.123456789+01:00[Europe/Paris]</zoned-date-time>");
    }

    public void testZonedDateTimeNanosOmitted() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2017, 10, 30, 20, 40, 15, 0, ZoneId.of("Europe/Paris"));
        assertBothWays(zonedDateTime, "<zoned-date-time>2017-10-30T20:40:15+01:00[Europe/Paris]</zoned-date-time>");
    }

}
