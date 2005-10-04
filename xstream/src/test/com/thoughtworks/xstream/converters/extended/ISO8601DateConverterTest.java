package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import junit.framework.TestCase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public void testUnmarshallsISOFormatInUTC() throws ParseException {
        // setup
        String isoFormat = "1993-02-14T13:10:30-05:00";
        String simpleFormat = "1993-02-14 13:10:30EST";
        // execute
        Date out = (Date) converter.fromString(isoFormat);
        Date control = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(simpleFormat);
        // verify for EST
        assertEquals("Sun Feb 14 13:10:30 EST 1993", out.toString());
        assertEquals(control, out);
    }

    public void testUnmarshallsISOFormatInLocalTime() {
        // setup
        String isoFormat = "1993-02-14T13:10:30";
        // execute
        Date out = (Date) converter.fromString(isoFormat);
        // verify for EST
        Calendar calendar = Calendar.getInstance();
        calendar.set(1993, 1, 14, 13, 10, 30);
        assertEquals(calendar.getTime().toString(), out.toString());
    }
}
