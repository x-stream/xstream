package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import junit.framework.TestCase;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    public void testUnmashallsInCorrectTimeZone() {
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

    public void testUnmarshallsISOFormat() {
        // setup
        String isoFormat = "1993-02-14T13:10:30Z";
        // execute
        Date out = (Date) converter.fromString(isoFormat);
        // verify for EST
        assertEquals("1993-02-14T08:10:30.000-05:00", converter.toString(out));
    }
}
