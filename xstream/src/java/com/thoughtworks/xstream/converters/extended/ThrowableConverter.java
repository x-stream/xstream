/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013, 2014, 2018, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. May 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.extended;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.ConverterLookup;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converter for {@link Throwable} (and {@link Exception}) that retains stack trace.
 *
 * @author <a href="mailto:boxley@thoughtworks.com">B. K. Oxley (binkley)</a>
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class ThrowableConverter implements Converter {

    private static final String ATTRIBUTE_SERIALIZATION = "serialization";
    private final Converter defaultConverter;
    private final ConverterLookup lookup;
    private final Mapper mapper;

    /**
     * @deprecated As of 1.4.5 use {@link #ThrowableConverter(ConverterLookup)}
     */
    @Deprecated
    public ThrowableConverter(final Converter defaultConverter) {
        this.defaultConverter = defaultConverter;
        lookup = null;
        mapper = null;
    }

    /**
     * @since 1.4.5
     * @deprecated As of upcoming use {@link #ThrowableConverter(Mapper, ConverterLookup)}
     */
    @Deprecated
    public ThrowableConverter(final ConverterLookup lookup) {
        this.lookup = lookup;
        mapper = null;
        defaultConverter = null;
    }

    /**
     * @since upcoming
     */
    public ThrowableConverter(final Mapper mapper, final ConverterLookup lookup) {
        this.mapper = mapper;
        this.lookup = lookup;
        defaultConverter = null;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && Throwable.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Throwable throwable = (Throwable)source;
        if (throwable.getCause() == null) {
            try {
                throwable.initCause(null);
            } catch (final IllegalStateException e) {
                // ignore, initCause failed, cause was already set
            }
        }
        // force stackTrace field to be lazy loaded by special JVM native witchcraft (outside our control).
        throwable.getStackTrace();
        getConverter().marshal(throwable, writer, context);
    }

    private Converter getConverter() {
        return defaultConverter != null ? defaultConverter : lookup.lookupConverterForType(Ser.class);
    }

    private Converter getConverter14() {
        return defaultConverter != null ? defaultConverter : lookup.lookupConverterForType(Object.class);
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final String attributeName = mapper != null
            ? mapper.aliasForSystemAttribute(ATTRIBUTE_SERIALIZATION)
            : ATTRIBUTE_SERIALIZATION;
        final Converter converter = attributeName != null && reader.getAttribute(attributeName) == null
            ? getConverter14()
            : getConverter();
        return converter.unmarshal(reader, context);
    }

    private static class Ser implements Serializable {
        private static final long serialVersionUID = 20190506L;

        @SuppressWarnings("unused")
        public Ser() {
        }

        private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException {
            in.defaultReadObject();
        }
    }
}
