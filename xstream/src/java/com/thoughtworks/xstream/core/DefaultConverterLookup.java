/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013, 2014, 2015, 2016, 2017, 2019, 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.core.util.Cloneables;
import com.thoughtworks.xstream.core.util.PrioritizedList;


/**
 * The default implementation of converters lookup.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 */
public class DefaultConverterLookup implements ConverterLookup, ConverterRegistry, Caching {

    private final PrioritizedList<Converter> converters = new PrioritizedList<>();
    private transient Map<String, Converter> typeToConverterMap;
    private Map<String, Converter> serializationMap = null;

    public DefaultConverterLookup() {
        this(new HashMap<>());
    }

    /**
     * Constructs a DefaultConverterLookup with a provided map.
     *
     * @param map the map to use
     * @throws NullPointerException if map is null
     * @since 1.4.11
     */
    public DefaultConverterLookup(final Map<String, Converter> map) {
        typeToConverterMap = map;
        typeToConverterMap.clear();
    }

    @Override
    public Converter lookupConverterForType(final Class<?> type) {
        final Converter cachedConverter = type != null ? typeToConverterMap.get(type.getName()) : null;
        if (cachedConverter != null) {
            return cachedConverter;
        }

        final Map<String, String> errors = new LinkedHashMap<>();
        for (final Converter converter : converters) {
            try {
                if (converter.canConvert(type)) {
                    if (type != null) {
                        typeToConverterMap.put(type.getName(), converter);
                    }
                    return converter;
                }
            } catch (final RuntimeException | LinkageError e) {
                errors.put(converter.getClass().getName(), e.getMessage());
            }
        }

        final ConversionException exception = new ConversionException(errors.isEmpty()
            ? "No converter specified"
            : "No converter available");
        exception.add("type", type != null ? type.getName() : "null");
        for (final Map.Entry<String, String> entry : errors.entrySet()) {
            exception.add("converter", entry.getKey());
            exception.add("message", entry.getValue());
        }
        throw exception;
    }

    @Override
    public void registerConverter(final Converter converter, final int priority) {
        typeToConverterMap.clear();
        converters.add(converter, priority);
    }

    @Override
    public void flushCache() {
        typeToConverterMap.clear();
        for (final Converter converter : converters) {
            if (converter instanceof Caching) {
                ((Caching)converter).flushCache();
            }
        }
    }

    private Object writeReplace() {
        serializationMap = Cloneables.cloneIfPossible(typeToConverterMap);
        serializationMap.clear();
        return this;
    }

    private Object readResolve() {
        typeToConverterMap = serializationMap == null ? new HashMap<>() : serializationMap;
        serializationMap = null;
        return this;
    }
}
