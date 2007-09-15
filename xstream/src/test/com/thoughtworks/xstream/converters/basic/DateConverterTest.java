package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class DateConverterTest extends TestCase {

    private DateConverter converter = new DateConverter();

    protected void setUp() throws Exception {
        super.setUp();

        // Ensure that this test always run as if it were in the IST timezone.
        // This prevents failures when running the tests in different zones.
        // Note: 'IST' has no relevance - it was just a randomly chosen zone 
        // without daylight saving.
        TimeZoneChanger.change("IST");
    }

    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testRetainsDetailDownToMillisecondLevel() {
        // setup
        Date in = new Date();

        // execute
        String text = converter.toString(in);
        Date out = (Date)converter.fromString(text);

        // verify
        assertEquals(in, out);
        assertEquals(in.toString(), out.toString());
        assertEquals(in.getTime(), out.getTime());
    }

    public void testUnmarshalsOldXStreamDatesThatLackMillisecond() {
        Date expected = (Date)converter.fromString("2004-02-22 15:16:04.0 EST");

        assertEquals(expected, converter.fromString("2004-02-22 15:16:04.0 EST"));
        assertEquals(expected, converter.fromString("2004-02-22 15:16:04 EST"));
        assertEquals(expected, converter.fromString("2004-02-22 15:16:04EST"));
        
        TimeZone.setDefault(TimeZone.getTimeZone("EST")); // Need correct local time, no TZ info in string
        assertEquals(expected, converter.fromString("2004-02-22 15:16:04.0 PM"));
        assertEquals(expected, converter.fromString("2004-02-22 15:16:04PM"));
    }

    public void testUnmarshalsDatesWithDifferentTimeZones() {
        Date expected = (Date)converter.fromString("2004-02-22 15:16:04.0 EST");

        assertEquals(expected, converter.fromString("2004-02-22 15:16:04.0 EST"));
        assertEquals(expected, converter.fromString("2004-02-22 15:16:04.0 GMT-05:00"));
        assertEquals(expected, converter.fromString("2004-02-22 20:16:04.0 UTC"));
        assertEquals(expected, converter.fromString("2004-02-23 01:46:04.0 IST"));
        assertEquals(expected, converter.fromString("2004-02-23 01:46:04.0 GMT+05:30"));
    }

    public void testUnmashalsDateWithDifferentDefaultTimeZones() throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2004, Calendar.FEBRUARY, 23, 1, 46, 4);
        Date date = cal.getTime();
        String strIST = converter.toString(date);
        assertEquals("2004-02-23 01:46:04.0 IST", strIST);
        // select arbitrary TZ
        TimeZone.setDefault(TimeZone.getTimeZone("EST"));
        // compare parsed date with JDK implementation
        Date dateRetrieved = (Date)converter.fromString(strIST);
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");
        Date simpleDate = f.parse(strIST);
        assertEquals(simpleDate, dateRetrieved);
        // DateConverter does not get influenced by change of current TZ ...
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        dateRetrieved = (Date)converter.fromString(strIST);
        assertEquals(simpleDate, dateRetrieved);
        // ... as well as the SimpleDateFormat
        f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S z");
        simpleDate = f.parse(strIST);
        assertEquals(simpleDate, dateRetrieved);
        assertEquals(date, f.parse("2004-02-22 20:16:04.0 UTC"));
        // assertEquals(date, simpleDate); off by +03:30 ... but why ??
    }

    public void testIsThreadSafe() throws InterruptedException {
        final List results = Collections.synchronizedList(new ArrayList());
        final DateConverter converter = new DateConverter();
        final Object monitor = new Object();
        final int numberOfCallsPerThread = 20;
        final int numberOfThreads = 20;

        // spawn some concurrent threads, that hammer the converter
        Runnable runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < numberOfCallsPerThread; i++) {
                    try {
                        converter.fromString("2004-02-22 15:16:04.0 EST");
                        results.add("PASS");
                    } catch (ConversionException e) {
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

        assertTrue("Nothing suceeded", results.contains("PASS"));
        assertFalse("At least one attempt failed", results.contains("FAIL"));
    }
    
    public void testDatesInNonLenientMode() {
        String[] dateFormats = new String[] { "yyyyMMdd", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd" };
        converter = new DateConverter("yyyy-MM-dd'T'HH:mm:ss.S'Z'", dateFormats);
        Date expected = (Date)converter.fromString("2004-02-22T15:16:04.0Z");
        assertEquals(expected, converter.fromString("2004-02-22T15:16:04Z"));
    }
    
    public void testDatesInLenientMode() {
        converter = new DateConverter("yyyy-MM-dd HH:mm:ss.S z", new String[0], true);
        Date expected = (Date)converter.fromString("2004-02-22 15:16:04.0 IST");
        assertEquals(expected, converter.fromString("2004-02-21 39:16:04.0 IST"));
    }
}
