/*
 * Copyright (C) 2003, 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. September 2003 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.basic;

import java.util.Collections;
import java.util.Map;

import com.thoughtworks.xstream.core.util.WeakCache;


/**
 * Converts a String to a String ;).
 * <p>
 * Well ok, it doesn't <i>actually</i> do any conversion. The converter uses by default a map
 * with weak references to reuse instances of strings that do not exceed a length limit. This
 * limit is by default 38 characters to cache typical strings containing UUIDs. Only shorter
 * strings are typically repeated more often in XML values.
 * </p>
 * 
 * @author Joe Walnes
 * @author Rene Schwietzke
 * @author J&ouml;rg Schaible
 */
public class StringConverter extends AbstractSingleValueConverter {

    private static final int LENGTH_LIMIT = 38;
    
    /**
     * A Map to store strings as long as needed to map similar strings onto the same instance
     * and conserve memory. The map can be set from the outside during construction, so it can
     * be a LRU map or a weak map, synchronised or not.
     */
    private final Map cache;
    private final int lengthLimit;

    /**
     * Construct a StringConverter using a map-based cache for strings not exceeding the length limit.
     * 
     * @param map the map to use for the instances to reuse (may be null to not cache at all)
     * @param lengthLimit maximum string length of a cached string, -1 to cache all, 0 to turn off the cache
     * @since 1.4.2
     */
    public StringConverter(final Map map, int lengthLimit) {
        cache = map;
        this.lengthLimit = lengthLimit;
    }

    /**
     * Construct a StringConverter using a map-based cache for strings not exceeding 38 characters.
     * 
     * @param map the map to use for the instances to reuse (may be null to not cache at all)
     */
    public StringConverter(final Map map) {
        this(map, LENGTH_LIMIT);
    }

    /**
     * Construct a StringConverter using a cache with weak references for strings not exceeding the length limit.
     * 
     * @param lengthLimit maximum string length of a cached string, -1 to cache all, 0 to turn off the cache
     * @since 1.4.2
     */
    public StringConverter(int lengthLimit) {
        this(Collections.synchronizedMap(new WeakCache()), lengthLimit);
    }

    /**
     * Construct a StringConverter using a cache with weak references for strings not exceeding 38 characters.
     */
    public StringConverter() {
        this(LENGTH_LIMIT);
    }

    public boolean canConvert(final Class type) {
        return type.equals(String.class);
    }

    public Object fromString(final String str) {
        if (cache != null && str != null && (lengthLimit < 0 || str.length() <= lengthLimit)) {
            String s = (String)cache.get(str);

            if (s == null) {
                // fill cache
                cache.put(str, str);

                s = str;
            }

            return s;
        } else {
            return str;
        }
    }
}
