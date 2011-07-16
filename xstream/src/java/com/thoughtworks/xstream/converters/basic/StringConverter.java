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
 * with weak references to reuse instances.
 * </p>
 * 
 * @author Joe Walnes
 * @author Rene Schwietzke
 * @author J&ouml;rg Schaible
 * @see String#intern()
 */
public class StringConverter extends AbstractSingleValueConverter {

    /**
     * A Map to store strings as long as needed to map similar strings onto the same instance
     * and conserve memory. The map can be set from the outside during construction, so it can
     * be a LRU map or a weak map, synchronised or not.
     */
    private final Map cache;

    /**
     * Construct a StringConverter using a cache with weak references.
     * 
     * @param map the map to use for the instances to reuse (may be null to not cache at all)
     */
    public StringConverter(final Map map) {
        cache = map;
    }

    /**
     * Construct a StringConverter using a cache with weak references.
     */
    public StringConverter() {
        this(Collections.synchronizedMap(new WeakCache()));
    }

    public boolean canConvert(final Class type) {
        return type.equals(String.class);
    }

    public Object fromString(final String str) {
        if (cache != null) {
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
