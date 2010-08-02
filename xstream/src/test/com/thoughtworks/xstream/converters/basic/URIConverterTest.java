/*
 * Copyright (C) 2010 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 3. August 2010 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.basic;

import java.net.URI;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * @author Carlos Roman
 */
public class URIConverterTest  extends AbstractAcceptanceTest {

    private static final String TEST_URI_STRING = "rtmp://cp12345.edgefcs.net/od/public/file.flv";
    private static URI TEST_URI;
    
    public URIConverterTest() {
    }

    public void setUp() throws Exception{
        xstream = new XStream(new DomDriver("UTF-8"));
        xstream.registerConverter(new URIConverter());
        TEST_URI = new URI(TEST_URI_STRING);
        assertNotNull(xstream);
        assertNotNull(TEST_URI);
    }

    /**
     * Test of canConvert method, of class URIConverter.
     */
    public void testCanConvert() {
        final Class type = URI.class;
        final URIConverter instance = new URIConverter();
        final boolean expResult = true;
        final boolean result = instance.canConvert(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of fromString method, of class URIConverter.
     */
    public void testFromString() throws Exception{
        final URIConverter instance = new URIConverter();
        final Object expResult = new URI(TEST_URI_STRING);
        final Object result = instance.fromString(TEST_URI_STRING);
        assertEquals(expResult, result);
    }
}