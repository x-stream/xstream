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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
        final Map<String, String> map = new ConcurrentHashMap<>();
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
        final Map<String, String> map = new ConcurrentHashMap<>();
        final StringConverter converter = new StringConverter(map, 4);
        assertSame(converter.fromString("Test"), converter.fromString(new String("Test"))); // cached value
        assertNotSame(converter.fromString("JUnit"), converter.fromString(new String("JUnit"))); // non-cached value
        assertEquals(1, map.size());
    }
}
