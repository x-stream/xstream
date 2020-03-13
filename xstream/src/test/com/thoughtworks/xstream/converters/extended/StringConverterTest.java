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

package com.thoughtworks.xstream.converters.extended;


import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.basic.StringConverter;

import junit.framework.TestCase;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Tests {@link StringConverter}.
 */
public class StringConverterTest extends TestCase {

    /**
     * Tests use cache.
     */
    public void testUseStringConverterCache() {
        final String str1 = "Use cache";
        final String str2 = new String("Use cache");
        assertFalse(str1 == str2);
        final Map<String,String> map = new ConcurrentHashMap<>();
        map.put(str1, str1);

        // Using the parameter map constructor
        final SingleValueConverter converter = new SingleValueConverterWrapper(new StringConverter(map));
        assertTrue(converter.fromString(str2) == map.get(str1));// Cached value

        // Using the parameter Int 38 constructor
        final SingleValueConverter converter2 = new SingleValueConverterWrapper(new StringConverter(38));
        assertTrue(converter2.fromString(str2) == converter2.fromString(str1));
    }

    /**
     * Tests not cached when the length exceeds 38 or null.
     */
    public void testNotCacheOverLengthOrNull() {
        final String str1 = "Not cacheNot cacheNot cacheNot cacheNot cache";
        final String str2 = new String("Not cacheNot cacheNot cacheNot cacheNot cache");
        assertFalse(str1 == str2);
        final Map<String,String> map = new ConcurrentHashMap<>();
        map.put(str1, str1);
        final SingleValueConverter converter = new SingleValueConverterWrapper(new StringConverter(map));
        assertFalse(converter.fromString(str2) == map.get(str1));// Non-cached value

        final SingleValueConverter converter2 = new SingleValueConverterWrapper(new StringConverter(null));
        assertTrue(converter2.fromString(str2) == str2);// Original value
    }
}
