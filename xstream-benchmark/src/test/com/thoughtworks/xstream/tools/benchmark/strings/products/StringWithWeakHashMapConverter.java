/*
 * Copyright (C) 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 04. May 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.benchmark.strings.products;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import com.thoughtworks.xstream.tools.benchmark.Product;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;


/**
 * Uses WeakHashMap for StringConverter.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Product
 */
public class StringWithWeakHashMapConverter implements Product {

    private final XStream xstream;

    public StringWithWeakHashMapConverter() {
        xstream = new XStream(new XppDriver());
        xstream.registerConverter(new StringConverter());
    }

    public void serialize(Object object, OutputStream output) throws Exception {
        xstream.toXML(object, output);
    }

    public Object deserialize(InputStream input) throws Exception {
        return xstream.fromXML(input);
    }

    public String toString() {
        return "StringConverter using WeakHashMap";
    }

    /**
     * Converts a String to a String.
     * <p>
     * Well ok, it doesn't <i>actually</i> do any conversion. The converter uses a map to reuse
     * instances. This map is by default a {@link WeakHashMap}.
     * </p>
     * 
     * @author Rene Schwietzke
     * @author J&ouml;rg Schaible
     * @see WeakHashMap
     */
    public static class StringConverter extends AbstractSingleValueConverter {
        /**
         * A Map to store strings as long as needed to map similar strings onto the same
         * instance and conserve memory. The map can be set from the outside during
         * construction, so it can be a LRU map or a weak map, sychronized or not.
         */
        private final Map cache;

        public StringConverter(Map map) {
            this.cache = map;
        }

        public StringConverter() {
            this(new WeakHashMap());
        }

        public boolean canConvert(Class type) {
            return type.equals(String.class);
        }

        public Object fromString(String str) {
            WeakReference ref = (WeakReference)cache.get(str);
            String s = (String)(ref == null ? null : ref.get());

            if (s == null) {
                // fill cache
                cache.put(str, new WeakReference(str));

                s = str;
            }

            return s;
        }
    }
}
