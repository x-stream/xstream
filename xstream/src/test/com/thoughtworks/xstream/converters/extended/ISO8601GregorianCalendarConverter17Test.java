/*
 * Copyright (C) 2013, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 21. June 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import junit.framework.TestCase;


public class ISO8601GregorianCalendarConverter17Test extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Ensure that this test always run as if it were in the timezone of Panama.
        // This prevents failures when running the tests in different zones.
        // Note: 'America/Panama' has no relevance - it was just a randomly chosen zone.
        TimeZoneChanger.change("America/Panama");
    }

    @Override
    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testCanLoadTimeWithDefaultDifferentLocaleForFormat() {
        final ISO8601GregorianCalendarConverter converter = new ISO8601GregorianCalendarConverter();

        final Locale defaultLocale = Locale.getDefault();
        final Locale defaultLocaleForFormat = Locale.getDefault(Locale.Category.FORMAT);
        try {
            Locale.setDefault(Locale.US);
            Locale.setDefault(Locale.Category.FORMAT, Locale.GERMANY);
            final Calendar in = new GregorianCalendar(2013, Calendar.JUNE, 17, 16, 0, 0);

            final String converterXML = converter.toString(in);
            final Calendar out = (Calendar)converter.fromString(converterXML);
            assertEquals(in.getTime(), out.getTime());
        } finally {
            Locale.setDefault(defaultLocale);
            Locale.setDefault(defaultLocaleForFormat);
        }
    }
}
