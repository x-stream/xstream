/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014, 2017, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 22. November 2004 by Mauro Talevi
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * A converter for {@link Date} conforming to the ISO8601 standard.
 * 
 * @see <a href="http://www.iso.org/iso/home/store/catalogue_ics/catalogue_detail_ics.htm?csnumber=40874">ISO 8601</a>
 * @author Mauro Talevi
 * @author J&ouml;rg Schaible
 */
public class ISO8601DateConverter extends AbstractSingleValueConverter {

    private final ISO8601GregorianCalendarConverter converter = new ISO8601GregorianCalendarConverter();

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == Date.class && converter.canConvert(GregorianCalendar.class);
    }

    @Override
    public Object fromString(final String str) {
        return ((Calendar)converter.fromString(str)).getTime();
    }

    @Override
    public String toString(final Object obj) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime((Date)obj);
        return converter.toString(calendar);
    }
}
