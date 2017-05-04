/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013, 2014, 2015, 2016, 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
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
    private transient Map<Class<?>, Converter> typeToConverterMap;

    public DefaultConverterLookup() {
        readResolve();
    }

    @Override
    public Converter lookupConverterForType(final Class<?> type) {
        final Converter cachedConverter = typeToConverterMap.get(type);
        if (cachedConverter != null) {
            return cachedConverter;
        }

        final Map<String, String> errors = new LinkedHashMap<>();
        for (final Converter converter : converters) {
            try {
                if (converter.canConvert(type)) {
                    return converter;
                }
            } catch (final RuntimeException | LinkageError e) {
                errors.put(converter.getClass().getName(), e.getMessage());
            }
        }

        final ConversionException exception = new ConversionException(errors.isEmpty()
            ? "No converter specified"
            : "No converter available");
        exception.add("type", type.getName());
        for (final Map.Entry<String, String> entry : errors.entrySet()) {
            exception.add("converter", entry.getKey());
            exception.add("message", entry.getValue());
        }
        throw exception;
    }

    @Override
    public void registerConverter(final Converter converter, final int priority) {
        converters.add(converter, priority);
        for (final Iterator<Class<?>> iter = typeToConverterMap.keySet().iterator(); iter.hasNext();) {
            final Class<?> type = iter.next();
            try {
                if (converter.canConvert(type)) {
                    iter.remove();
                }
            } catch (final RuntimeException | LinkageError e) {
                // ignore
            }
        }
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

    private Object readResolve() {
        // TODO: Use ConcurrentMap
        typeToConverterMap = Collections.synchronizedMap(new WeakHashMap<Class<?>, Converter>());
        return this;
    }
}
