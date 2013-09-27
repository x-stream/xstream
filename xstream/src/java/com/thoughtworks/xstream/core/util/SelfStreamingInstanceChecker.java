/*
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. March 2013 by Joerg Schaible, moved from package
 * com.thoughtworks.xstream.converters.reflection.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * A special converter that prevents self-serialization. The serializing XStream instance
 * adds a converter of this type to prevent self-serialization and will throw an
 * exception instead.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class SelfStreamingInstanceChecker implements Converter {

    private final Object self;
    private Converter defaultConverter;
    private final ConverterLookup lookup;

    /**
     * @since 1.4.5
     */
    public SelfStreamingInstanceChecker(ConverterLookup lookup, Object xstream) {
        this.lookup = lookup;
        this.self = xstream;
    }

    /**
     * @deprecated As of 1.4.5 use {@link #SelfStreamingInstanceChecker(ConverterLookup, Object)}
     */
    public SelfStreamingInstanceChecker(Converter defaultConverter, Object xstream) {
        this.defaultConverter = defaultConverter;
        this.self = xstream;
        lookup = null;
    }

    public boolean canConvert(Class type) {
        return type == self.getClass();
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        if (source == self) {
            throw new ConversionException("Cannot marshal the XStream instance in action");
        }
        getConverter().marshal(source, writer, context);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return getConverter().unmarshal(reader, context);
    }

    private Converter getConverter() {
        return defaultConverter != null ? defaultConverter : lookup.lookupConverterForType(Object.class);
    }
}
