/*
 * Copyright (C) 2004, 2005, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core;

import com.thoughtworks.xstream.alias.ClassMapper;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.ConverterRegistry;
import com.thoughtworks.xstream.core.util.PrioritizedList;
import com.thoughtworks.xstream.mapper.Mapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The default implementation of converters lookup.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Guilherme Silveira
 */
public class DefaultConverterLookup implements ConverterLookup, ConverterRegistry {

    private final PrioritizedList converters = new PrioritizedList();
    private transient Map typeToConverterMap = Collections.synchronizedMap(new HashMap());

    public DefaultConverterLookup() {
    }

    /**
     * @deprecated since 1.3, use {@link #DefaultConverterLookup()}
     */
    public DefaultConverterLookup(Mapper mapper) {
    }

    /**
     * @deprecated since 1.2, use {@link #DefaultConverterLookup(Mapper)}
     */
    public DefaultConverterLookup(ClassMapper classMapper) {
    }

    public Converter lookupConverterForType(Class type) {
        Converter cachedConverter = (Converter) typeToConverterMap.get(type);
        if (cachedConverter != null) return cachedConverter;
        Iterator iterator = converters.iterator();
        while (iterator.hasNext()) {
            Converter converter = (Converter) iterator.next();
            if (converter.canConvert(type)) {
                typeToConverterMap.put(type, converter);
                return converter;
            }
        }
        throw new ConversionException("No converter specified for " + type);
    }
    
    public void registerConverter(Converter converter, int priority) {
        converters.add(converter, priority);
        for (Iterator iter = this.typeToConverterMap.keySet().iterator(); iter.hasNext();) {
            Class type = (Class) iter.next();
            if (converter.canConvert(type)) {
                iter.remove();
            }
        }
    }
    
    private Object readResolve() {
        typeToConverterMap = Collections.synchronizedMap(new HashMap());
        return this;
    }

}
