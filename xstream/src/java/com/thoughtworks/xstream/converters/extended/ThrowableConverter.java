/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 29. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for Throwable (and Exception) that retains stack trace.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class ThrowableConverter implements Converter {
    
    private Converter defaultConverter;
    private final ConverterLookup lookup;

    /**
     * @deprecated As of 1.4.5 use {@link #ThrowableConverter(ConverterLookup)}
     */
    public ThrowableConverter(Converter defaultConverter) {
        this.defaultConverter = defaultConverter;
        lookup = null;
    }

    /**
     * @since 1.4.5
     */
    public ThrowableConverter(ConverterLookup lookup) {
        this.lookup = lookup;
    }

    public boolean canConvert(final Class type) {
        return Throwable.class.isAssignableFrom(type);
    }

    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        Throwable throwable = (Throwable) source;
        if (throwable.getCause() == null) {
            try {
                throwable.initCause(null);
            } catch (IllegalStateException e) {
                // ignore, initCause failed, cause was already set
            }
        }
        throwable.getStackTrace(); // Force stackTrace field to be lazy loaded by special JVM native witchcraft (outside our control).
        getConverter().marshal(throwable, writer, context);
    }

    private Converter getConverter() {
        return defaultConverter != null ? defaultConverter : lookup.lookupConverterForType(Object.class);
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return getConverter().unmarshal(reader, context);
    }
}
