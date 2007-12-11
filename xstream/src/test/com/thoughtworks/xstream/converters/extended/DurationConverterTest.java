/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 21. September 2007 by Joerg Schaible
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
