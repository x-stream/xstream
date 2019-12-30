/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013, 2016, 2017, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.core.util.Cloneables;
import com.thoughtworks.xstream.core.util.PrioritizedList;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * The default implementation of converters lookup.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 */
public class DefaultConverterLookup implements ConverterLookup, ConverterRegistry, Caching {

    private final PrioritizedList converters = new PrioritizedList();
    private transient Map typeToConverterMap;
    private Map serializationMap = null;

    public DefaultConverterLookup() {
        this(new HashMap());
    }

    /**
     * Constructs a DefaultConverterLookup with a provided map.
     *
     * @param map the map to use
     * @throws NullPointerException if map is null
     * @since 1.4.11
     */
    public DefaultConverterLookup(Map map) {
        typeToConverterMap = map;
        typeToConverterMap.clear();
    }

    /**
     * @deprecated As of 1.3, use {@link #DefaultConverterLookup()}
     */
    public DefaultConverterLookup(Mapper mapper) {
        this();
    }

    public Converter lookupConverterForType(Class type) {
        Converter cachedConverter = type != null ? (Converter)typeToConverterMap.get(type.getName()) : null;
        if (cachedConverter != null) {
            return cachedConverter;
        }

        final Map errors = new LinkedHashMap();
        Iterator iterator = converters.iterator();
        while (iterator.hasNext()) {
            Converter converter = (Converter)iterator.next();
            try {
                if (converter.canConvert(type)) {
                    if (type != null) {
                        typeToConverterMap.put(type.getName(), converter);
                    }
                    return converter;
                }
            } catch (final RuntimeException e) {
                errors.put(converter.getClass().getName(), e.getMessage());
            } catch (final LinkageError e) {
                errors.put(converter.getClass().getName(), e.getMessage());
            }
        }

        final ConversionException exception = new ConversionException(errors.isEmpty()
            ? "No converter specified"
            : "No converter available");
        exception.add("type", type != null ? type.getName() : "null");
        iterator = errors.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = (Map.Entry)iterator.next();
            exception.add("converter", entry.getKey().toString());
            exception.add("message", entry.getValue().toString());
        }
        throw exception;
    }

    public void registerConverter(Converter converter, int priority) {
        typeToConverterMap.clear();
        converters.add(converter, priority);
    }

    public void flushCache() {
        typeToConverterMap.clear();
        Iterator iterator = converters.iterator();
        while (iterator.hasNext()) {
            Converter converter = (Converter)iterator.next();
            if (converter instanceof Caching) {
                ((Caching)converter).flushCache();
            }
        }
    }

    private Object writeReplace() {
        serializationMap = (Map)Cloneables.cloneIfPossible(typeToConverterMap);
        serializationMap.clear();
        return this;
    }

    private Object readResolve() {
        typeToConverterMap = serializationMap == null ? new HashMap() : serializationMap;
        serializationMap = null;
        return this;
    }
}
