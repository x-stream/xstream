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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import junit.framework.TestCase;


public class ISO8601DateConverterTest extends TestCase {

    private ISO8601DateConverter converter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        converter = new ISO8601DateConverter();

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

    public void testUnmashallsInCorrectTimeZone() {
        // setup
        final Date in = new Date();

        // execute
        final String text = converter.toString(in);
        final Date out = (Date)converter.fromString(text);

        // verify
        assertEquals(in, out);
        assertEquals(in.toString(), out.toString());
        assertEquals(in.getTime(), out.getTime());
    }

    public void testUnmarshallsISOFormatInUTC() throws ParseException {
        // setup
        final String isoFormat = "1993-02-14T13:10:30-05:00";
        final String simpleFormat = "1993-02-14 13:10:30EST";
        // execute
        final Date out = (Date)converter.fromString(isoFormat);
        final Date control = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(simpleFormat);
        // verify for EST
        assertEquals("Sun Feb 14 13:10:30 EST 1993", out.toString());
        assertEquals(control, out);
    }

    public void testUnmarshallsISOFormatInLocalTime() {
        // setup
        final String isoFormat = "1993-02-14T13:10:30";
        // execute
        final Date out = (Date)converter.fromString(isoFormat);
        // verify for EST
        final Calendar calendar = Calendar.getInstance();
        calendar.set(1993, 1, 14, 13, 10, 30);
        assertEquals(calendar.getTime().toString(), out.toString());
    }
}
