package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.testutil.TimeZoneChanger;

import junit.framework.TestCase;

import java.sql.Timestamp;


/**
 * @author Chung-Onn Cheong
 * @author J&ouml;rg Schaible
 */
public class ISO8601SqlTimestampConverterTest extends TestCase {
    private ISO8601SqlTimestampConverter converter;

    protected void setUp() throws Exception {
        super.setUp();
        converter = new ISO8601SqlTimestampConverter();

        // Ensure that this test always run as if it were in the EST timezone.
        // This prevents failures when running the tests in different zones.
        // Note: 'EST' has no relevance - it was just a randomly chosen zone.
        TimeZoneChanger.change("EST");
    }

    protected void tearDown() throws Exception {
        TimeZoneChanger.reset();
        super.tearDown();
    }

    public void testISO8601SqlTimestamp() {
        XStream xs = new XStream();
        xs.registerConverter(converter);

        long currentTime = System.currentTimeMillis();

        Timestamp ts1 = new Timestamp(currentTime);
        String xmlString = xs.toXML(ts1);

        Timestamp ts2 = (Timestamp)xs.fromXML(xmlString);

        assertEquals("ISO Timestamp Converted is not the same ", ts1, ts2);
        assertEquals(
                "Current time not equal to converted timestamp", currentTime,
                (ts2.getTime() / 1000) * 1000 + ts2.getNanos() / 1000000);
    }

    public void testISO8601SqlTimestampWith1Milli() {
        XStream xs = new XStream();
        xs.registerConverter(converter);

        long currentTime = (System.currentTimeMillis() / 1000 * 1000) + 1;

        Timestamp ts1 = new Timestamp(currentTime);
        String xmlString = xs.toXML(ts1);

        Timestamp ts2 = (Timestamp)xs.fromXML(xmlString);

        assertEquals("ISO Timestamp Converted is not the same ", ts1, ts2);
        assertEquals(
                "Current time not equal to converted timestamp", currentTime,
                (ts2.getTime() / 1000) * 1000 + ts2.getNanos() / 1000000);
    }

    public void testISO8601SqlTimestampWithNanos() {
        XStream xs = new XStream();
        xs.registerConverter(converter);

        Timestamp ts1 = new Timestamp(System.currentTimeMillis());
        ts1.setNanos(987654321);
        String xmlString = xs.toXML(ts1);

        Timestamp ts2 = (Timestamp)xs.fromXML(xmlString);

        assertEquals("ISO Timestamp Converted is not the same ", ts1, ts2);
        assertEquals("Nanos are not equal", ts1.getNanos(), ts2.getNanos());
    }

    public void testTimestampWithoutFraction() {
        String isoFormat = "1993-02-14T13:10:30-05:00";
        Timestamp out = (Timestamp)converter.fromString(isoFormat);
        assertEquals("1993-02-14T13:10:30.000000000-05:00", converter.toString(out));
    }
}
