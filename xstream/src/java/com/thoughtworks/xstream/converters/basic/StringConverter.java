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

import java.util.Collections;
import java.util.Map;

import com.thoughtworks.xstream.core.util.WeakCache;


/**
 * Converts a {@link String} to a string ;).
 * <p>
 * Well ok, it doesn't <i>actually</i> do any conversion. The converter uses by default a map with weak references to
 * reuse instances of strings that do not exceed a length limit. This limit is by default 38 characters to cache typical
 * strings containing UUIDs. Only shorter strings are typically repeated more often in XML values.
 * </p>
 * 
 * @author Joe Walnes
 * @author Rene Schwietzke
 * @author J&ouml;rg Schaible
 */
public class StringConverter extends AbstractSingleValueConverter {

    private static final int LENGTH_LIMIT = 38;

    /**
     * A Map to store strings as long as needed to map similar strings onto the same instance and conserve memory. The
     * map can be set from the outside during construction, so it can be a LRU map or a weak map, synchronized or not.
     */
    private final Map<String, String> cache;
    private final int lengthLimit;

    /**
     * Construct a StringConverter using a map-based cache for strings not exceeding the length limit.
     * 
     * @param map the map to use for the instances to reuse (may be null to not cache at all)
     * @param lengthLimit maximum string length of a cached string, -1 to cache all, 0 to turn off the cache
     * @since 1.4.2
     */
    public StringConverter(final Map<String, String> map, final int lengthLimit) {
        cache = map;
        this.lengthLimit = lengthLimit;
    }

    /**
     * Construct a StringConverter using a map-based cache for strings not exceeding 38 characters.
     * 
     * @param map the map to use for the instances to reuse (may be null to not cache at all)
     */
    public StringConverter(final Map<String, String> map) {
        this(map, LENGTH_LIMIT);
    }

    /**
     * Construct a StringConverter using a cache with weak references for strings not exceeding the length limit.
     * 
     * @param lengthLimit maximum string length of a cached string, -1 to cache all, 0 to turn off the cache
     * @since 1.4.2
     */
    public StringConverter(final int lengthLimit) {
        this(Collections.synchronizedMap(new WeakCache<String, String>()), lengthLimit);
    }

    /**
     * Construct a StringConverter using a cache with weak references for strings not exceeding 38 characters.
     */
    public StringConverter() {
        this(LENGTH_LIMIT);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == String.class;
    }

    @Override
    public Object fromString(final String str) {
        if (cache != null && str != null && (lengthLimit < 0 || str.length() <= lengthLimit)) {
            final String s = cache.putIfAbsent(str, str); // fill cache
            return s == null ? str : s;
        } else {
            return str;
        }
    }
}
