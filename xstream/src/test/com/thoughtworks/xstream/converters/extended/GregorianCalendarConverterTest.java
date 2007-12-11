/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 14. May 2005 by Mauro Talevi
 */
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
