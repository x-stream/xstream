/*
 * Copyright (C) 2007 XStream Committers.
 * Created on 21.09.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.SingleValueConverter;

import junit.framework.TestCase;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;


/**
 * @author John Kristian
 */
public class DurationConverterTest extends TestCase {
    private static final String[] STRINGS = {"-P1Y2M3DT4H5M6.7S", "P1Y", "PT1H2M"};

    public void testConversion() throws Exception {
        final SingleValueConverter converter = new DurationConverter();
        DatatypeFactory factory = DatatypeFactory.newInstance();
        for (int i = 0; i < STRINGS.length; i++) {
            final String s = STRINGS[i];
            Duration o = factory.newDuration(s);
            assertEquals(s, converter.toString(o));
            assertEquals(o, converter.fromString(s));
        }
    }

}
