/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013, 2014, 2015 XStream Committers.
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
        for (final Converter converter : converters) {
            if (converter.canConvert(type)) {
                return converter;
            }
        }
        throw new ConversionException("No converter specified for " + type);
    }

    @Override
    public void registerConverter(final Converter converter, final int priority) {
        converters.add(converter, priority);
        for (final Iterator<Class<?>> iter = typeToConverterMap.keySet().iterator(); iter.hasNext();) {
            final Class<?> type = iter.next();
            if (converter.canConvert(type)) {
                iter.remove();
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
        typeToConverterMap = Collections.synchronizedMap(new WeakHashMap<>());
        return this;
    }
}
