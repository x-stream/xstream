package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ISO8601DateConverterTest extends TestCase {

    private ISO8601DateConverter converter;

    protected void setUp() throws Exception {
        super.setUp();
        converter = new ISO8601DateConverter();
        
        // Ensure that this test always run as if it were in the EST timezone.
        // This prevents failures when running the tests in different zones.
        // Note: 'EST' has no relevance - it was just a randomly chosen zone.
        TimeZoneChanger.change("EST");
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
        Date out = (Date) converter.fromString(text);

        // verify
        assertEquals(in, out);
        assertEquals(in.toString(), out.toString());
        assertEquals(in.getTime(), out.getTime());
    }

    // Note: this test assumes that your are in the GMT timezone
    // if not - simply set your computers' timezone to be in GMT
    public void testUnmarshallsISOFormat() {
        // setup
        String isoFormat = "1993-02-14T13:10:30";
        // execute
        Date out = (Date) converter.fromString(isoFormat);
        // verify
        assertEquals("1993-02-14T13:10:30.000Z", converter.toString(out));
    }

    public void testIsThreadSafe() throws InterruptedException {
        final List results = Collections.synchronizedList(new ArrayList());
        final ISO8601DateConverter converter = new ISO8601DateConverter();
        final Object monitor = new Object();
        final int numberOfCallsPerThread = 20;
        final int numberOfThreads = 20;

        // spawn some concurrent threads, that hammer the converter
        Runnable runnable = new Runnable() {
            public void run() {
                for (int i = 0; i < numberOfCallsPerThread; i++) {
                    try {
                        converter.fromString("1993-02-14T13:10:30");
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

        assertTrue("Nothing succeded", results.contains("PASS"));
        assertFalse("At least one attempt failed", results.contains("FAIL"));
    }
}
