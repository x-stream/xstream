package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.XStream;

import junit.framework.TestCase;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author J&ouml;rg Schaible
 */
public class GregorianCalendarConverterTest extends TestCase {

    public void testCalendar() {
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        final XStream xstream = new XStream();
        final String xml = xstream.toXML(cal);
        final Calendar serialized = (Calendar)xstream.fromXML(xml);
        assertEquals(cal, serialized);
    }

}
