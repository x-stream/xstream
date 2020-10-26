/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.converters.extended;

import java.sql.Timestamp;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import junit.framework.TestCase;


/**
 * @author Chung-Onn Cheong
 * @author J&ouml;rg Schaible
 */
public class ISO8601SqlTimestampConverterTest extends TestCase {
    private ISO8601SqlTimestampConverter converter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        converter = new ISO8601SqlTimestampConverter();

        // Ensure that this test always run as if it were in the EST timezone.
        // This prevents failures when running the tests in different zones.
        // Note: 'EST' has no relevance - it was just a randomly chosen zone.
        TimeZoneChanger.change("EST");
    }

    @Override
    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testISO8601SqlTimestamp() {
        final XStream xs = new XStream();
        xs.registerConverter(converter);

        final long currentTime = System.currentTimeMillis();

        final Timestamp ts1 = new Timestamp(currentTime);
        final String xmlString = xs.toXML(ts1);

        final Timestamp ts2 = xs.<Timestamp>fromXML(xmlString);

        assertEquals("ISO Timestamp Converted is not the same ", ts1, ts2);
        assertEquals("Current time not equal to converted timestamp", currentTime, ts2.getTime() / 1000 * 1000
            + ts2.getNanos() / 1000000);
    }

    public void testISO8601SqlTimestampWith1Milli() {
        final XStream xs = new XStream();
        xs.registerConverter(converter);

        final long currentTime = System.currentTimeMillis() / 1000 * 1000 + 1;

        final Timestamp ts1 = new Timestamp(currentTime);
        final String xmlString = xs.toXML(ts1);

        final Timestamp ts2 = xs.<Timestamp>fromXML(xmlString);

        assertEquals("ISO Timestamp Converted is not the same ", ts1, ts2);
        assertEquals("Current time not equal to converted timestamp", currentTime, ts2.getTime() / 1000 * 1000
            + ts2.getNanos() / 1000000);
    }

    public void testISO8601SqlTimestampWithNanos() {
        final XStream xs = new XStream();
        xs.registerConverter(converter);

        final Timestamp ts1 = new Timestamp(System.currentTimeMillis());
        ts1.setNanos(987654321);
        final String xmlString = xs.toXML(ts1);

        final Timestamp ts2 = xs.<Timestamp>fromXML(xmlString);

        assertEquals("ISO Timestamp Converted is not the same ", ts1, ts2);
        assertEquals("Nanos are not equal", ts1.getNanos(), ts2.getNanos());
    }

    public void testTimestampWithoutFraction() {
        final String isoFormat = "1993-02-14T13:10:30-05:00";
        final Timestamp out = (Timestamp)converter.fromString(isoFormat);
        assertEquals("1993-02-14T13:10:30.000000000-05:00", converter.toString(out));
    }
}
