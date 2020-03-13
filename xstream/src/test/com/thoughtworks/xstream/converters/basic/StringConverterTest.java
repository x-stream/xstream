/*
 * Copyright (C) 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. Mar 2020 by Zezeng Wang
 */

package com.thoughtworks.xstream.converters.basic;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


/**
 * Tests {@link StringConverter}.
 */
public class StringConverterTest extends TestCase {

    /**
     * Tests use of own map implementation for cache.
     */
    public void testOwnMapImplementationForCache() {
        // Using the parameter map constructor
        final Map map = new HashMap();
        final StringConverter converter = new StringConverter(map);
        assertSame(converter.fromString("JUnit"), converter.fromString(new String("JUnit"))); // cached value
        assertEquals(1, map.size());
    }

    /**
     * Tests cache limitation for string length.
     */
    public void testCacheLimitationBasedOnStringLength() {
        // Using the int constructor
        final StringConverter converter = new StringConverter(4);
        assertSame(converter.fromString("Test"), converter.fromString(new String("Test"))); // cached value
        assertNotSame(converter.fromString("JUnit"), converter.fromString(new String("JUnit"))); // non-cached value
    }

    /**
     * Tests no cache.
     */
    public void testNoCache() {
        final StringConverter converter = new StringConverter(null);
        assertNotSame(converter.fromString("JUnit"), converter.fromString(new String("JUnit"))); // non-cached value
    }

    /**
     * Tests own map implementation and string length limit for cache.
     */
    public void testOwnMapImplementationAndStringLegnthLimitForCache() {
        // Using the map and int constructor
        final Map map = new HashMap();
        final StringConverter converter = new StringConverter(map, 4);
        assertSame(converter.fromString("Test"), converter.fromString(new String("Test"))); // cached value
        assertNotSame(converter.fromString("JUnit"), converter.fromString(new String("JUnit"))); // non-cached value
        assertEquals(1, map.size());
    }
}
