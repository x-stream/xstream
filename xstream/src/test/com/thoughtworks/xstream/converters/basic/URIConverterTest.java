/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.converters.basic;

import java.net.URI;

import com.thoughtworks.acceptance.AbstractAcceptanceTest;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/**
 * @author Carlos Roman
 */
public class URIConverterTest extends AbstractAcceptanceTest {

    private static final String TEST_URI_STRING = "rtmp://cp12345.edgefcs.net/od/public/file.flv";
    private static URI TEST_URI;

    public URIConverterTest() {
    }

    @Override
    public void setUp() throws Exception {
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
        final Class<?> type = URI.class;
        final URIConverter instance = new URIConverter();
        final boolean expResult = true;
        final boolean result = instance.canConvert(type);
        assertEquals(expResult, result);
    }

    /**
     * Test of fromString method, of class URIConverter.
     */
    public void testFromString() throws Exception {
        final URIConverter instance = new URIConverter();
        final Object expResult = new URI(TEST_URI_STRING);
        final Object result = instance.fromString(TEST_URI_STRING);
        assertEquals(expResult, result);
    }
}
