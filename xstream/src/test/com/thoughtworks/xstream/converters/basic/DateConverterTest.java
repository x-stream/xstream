package com.thoughtworks.xstream.converters.basic;

import junit.framework.TestCase;

import java.util.Date;

public class DateConverterTest extends TestCase {

    private DateConverter converter;

    protected void setUp() throws Exception {
        super.setUp();
        converter = new DateConverter();
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

    public void testUnmarshallsOldXStreamDatesThatLackMillisecond() {
        // setup
        String oldStyleText = "2004-02-22 15:16:04PM";

        // execute
        Date out = (Date) converter.fromString(oldStyleText);

        // verify
        assertEquals("2004-02-22 15:16:04.0 PM", converter.toString(out));
    }

}
