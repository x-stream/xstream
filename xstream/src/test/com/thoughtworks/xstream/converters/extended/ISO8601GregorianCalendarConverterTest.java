/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 03. October 2005 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import junit.framework.TestCase;


public class ISO8601GregorianCalendarConverterTest extends TestCase {

    private Locale locale;
    private ISO8601GregorianCalendarConverter converter;

    protected void setUp() throws Exception {
        super.setUp();
        converter = new ISO8601GregorianCalendarConverter();
        locale = Locale.getDefault();
        Locale.setDefault(Locale.GERMANY);

        // Ensure that this test always run as if it were in the timezone of Panama.
        // This prevents failures when running the tests in different zones.
        // Note: 'America/Panama' has no relevance - it was just a randomly chosen zone.
        TimeZoneChanger.change("America/Panama");
    }

    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        Locale.setDefault(locale);
        super.tearDown();
    }

    public void testRetainsDetailDownToMillisecondLevel() {
        // setup
        final Calendar in = Calendar.getInstance();

        // execute
        final String text = converter.toString(in);
        final Calendar out = (Calendar)converter.fromString(text);

        // verify
        assertEquals(in.getTime(), out.getTime());
    }

    public void testSavedTimeIsInUTC() {
        final Calendar in = Calendar.getInstance();
        final String iso8601;
        iso8601 = new DateTime(in).toString();
        final String converterXML = converter.toString(in);
        assertEquals(iso8601, converterXML);

        final Calendar out = (Calendar)converter.fromString(converterXML);
        assertEquals(in.getTime(), out.getTime());
    }

    public void testCanLoadTimeInDifferentTimeZone() {
        final Calendar in = Calendar.getInstance();
        final String converterXML = converter.toString(in);

        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Moscow"));
        final Calendar timeInMoscow = Calendar.getInstance();
        timeInMoscow.setTime(in.getTime());
        final Calendar out = (Calendar)converter.fromString(converterXML);
        assertEquals(timeInMoscow.getTime(), out.getTime());
    }

    public void testParsesBasicDateTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 4, 7, 19, 11, 22);
        Calendar out = (Calendar)converter.fromString("20170508T001122Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("20170508T001122.300Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(2017, 3, 20, 19, 11, 22);
    }

    public void testParsesBasicOrdinalDateTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 3, 20, 19, 11, 22);
        Calendar out = (Calendar)converter.fromString("2017111T001122Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("2017111T001122.300Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(1970, 0, 1, 13, 11, 22);
    }

    public void testParsesBasicTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(1970, 0, 1, 13, 11, 22);
        Calendar out = (Calendar)converter.fromString("181122Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("181122.300Z");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesBasicTTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(1970, 0, 1, 13, 11, 22);
        Calendar out = (Calendar)converter.fromString("T181122Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("T181122.300Z");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesBasicWeekDateTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 4, 8, 13, 11, 22);
        Calendar out = (Calendar)converter.fromString("2017W191T181122Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("2017W191T181122.300Z");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesBasicDate() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 3, 21);
        final Calendar out = (Calendar)converter.fromString("20170421");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesBasicOrdinalDate() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 3, 21);
        final Calendar out = (Calendar)converter.fromString("2017111");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesBasicWeekDate() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 3, 21);
        final Calendar out = (Calendar)converter.fromString("2017W165");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardDateTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 4, 7, 19, 11, 22);
        Calendar out = (Calendar)converter.fromString("2017-05-08T00:11:22Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("2017-05-08T00:11:22.300Z");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardOrdinalDateTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 3, 20, 19, 11, 22);
        Calendar out = (Calendar)converter.fromString("2017-111T00:11:22Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("2017-111T00:11:22.300Z");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(1970, 0, 1, 13, 11, 22);
        Calendar out = (Calendar)converter.fromString("18:11:22Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("18:11:22.300Z");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardTTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(1970, 0, 1, 13, 11, 22);
        Calendar out = (Calendar)converter.fromString("T18:11:22Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("T18:11:22.300Z");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardWeekDateTime() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 4, 8, 13, 11, 22);
        Calendar out = (Calendar)converter.fromString("2017-W19-1T18:11:22Z");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 300);
        out = (Calendar)converter.fromString("2017-W19-1T18:11:22.300Z");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardDate() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 3, 21);
        final Calendar out = (Calendar)converter.fromString("2017-04-21");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardOrdinalDate() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 3, 21);
        final Calendar out = (Calendar)converter.fromString("2017-111");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardWeekDate() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 3, 21);
        final Calendar out = (Calendar)converter.fromString("2017-W16-5");
        assertEquals(expected.getTime(), out.getTime());

    }

    public void testParsesStandardDateTimeFragment() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(2017, 3, 21);
        expected.set(Calendar.HOUR, 11);
        Calendar out = (Calendar)converter.fromString("2017-04-21T11");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MINUTE, 22);
        out = (Calendar)converter.fromString("2017-04-21T11:22");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.SECOND, 33);
        out = (Calendar)converter.fromString("2017-04-21T11:22:33");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 44);
        out = (Calendar)converter.fromString("2017-04-21T11:22:33.044");
        assertEquals(expected.getTime(), out.getTime());
        out = (Calendar)converter.fromString("2017-04-21T11:22:33.044777888");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardTimeFragment() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(Calendar.HOUR, 11);
        Calendar out = (Calendar)converter.fromString("11");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MINUTE, 22);
        out = (Calendar)converter.fromString("11:22");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.SECOND, 33);
        out = (Calendar)converter.fromString("11:22:33");
        assertEquals(expected.getTime(), out.getTime());
        expected.set(Calendar.MILLISECOND, 44);
        out = (Calendar)converter.fromString("11:22:33.044");
        assertEquals(expected.getTime(), out.getTime());
        out = (Calendar)converter.fromString("11:22:33.044777888");
        assertEquals(expected.getTime(), out.getTime());
    }

    public void testParsesStandardDateFragment() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        expected.set(Calendar.YEAR, 2017);
        Calendar out = (Calendar)converter.fromString("2017");
        assertEquals(expected.getTime(), out.getTime());
        if (JVM.isVersion(8)) { // Java 8 passes, Joda-Time fails
            expected.set(Calendar.MONTH, 3);
            out = (Calendar)converter.fromString("2017-04");
            assertEquals(expected.getTime(), out.getTime());
        }
    }

    public void testParsesStandardWeekDateFragment() {
        final Calendar expected = Calendar.getInstance();
        expected.clear();
        if (!JVM.isVersion(8)) { // TODO: Java 8 fails here, Joda-Time passes
            expected.set(2017, 3, 17);
            final Calendar out = (Calendar)converter.fromString("2017-W16");
            assertEquals(expected.getTime(), out.getTime());
        }
    }

    public void testCalendarWithExplicitTimeZone() {
        final Calendar timeInMoscow = Calendar.getInstance();
        timeInMoscow.set(2010, 6, 3, 10, 20, 36);
        timeInMoscow.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));

        final String converterXML = converter.toString(timeInMoscow);
        final Calendar out = (Calendar)converter.fromString(converterXML);
        assertEquals(timeInMoscow.getTimeInMillis(), out.getTimeInMillis());

        out.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        assertEquals(timeInMoscow.getTime(), out.getTime());
    }

    public void testIsThreadSafe() throws InterruptedException {
        final List results = Collections.synchronizedList(new ArrayList());
        final ISO8601GregorianCalendarConverter converter = new ISO8601GregorianCalendarConverter();
        final Object monitor = new Object();
        final int numberOfCallsPerThread = 20;
        final int numberOfThreads = 20;

        // spawn some concurrent threads, that hammer the converter
        final Runnable runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < numberOfCallsPerThread; i++) {
                    try {
                        converter.fromString("1993-02-14T13:10:30");
                        results.add("PASS");
                    } catch (final ConversionException e) {
                        results.add("FAIL");
                    } finally {
                        synchronized (monitor) {
                            monitor.notifyAll();
                        }
                    }
                }
            }
        };
        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(runnable).start();
        }

        // wait for all results
        while (results.size() < numberOfThreads * numberOfCallsPerThread) {
            synchronized (monitor) {
                monitor.wait(100);
            }
        }

        assertTrue("Nothing succeded", results.contains("PASS"));
        assertFalse("At least one attempt failed", results.contains("FAIL"));
    }
}
