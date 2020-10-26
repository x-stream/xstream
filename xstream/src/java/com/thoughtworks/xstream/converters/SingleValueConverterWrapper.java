/*
 * Copyright (C) 2006, 2007, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 20. February 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.converters;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Wrapper to convert a {@link com.thoughtworks.xstream.converters.SingleValueConverter} into a
 * {@link com.thoughtworks.xstream.converters.Converter}.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.converters.Converter
 * @see com.thoughtworks.xstream.converters.SingleValueConverter
 */
public class SingleValueConverterWrapper implements Converter, SingleValueConverter, ErrorReporter {

    private final SingleValueConverter wrapped;

    public SingleValueConverterWrapper(final SingleValueConverter wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return wrapped.canConvert(type);
    }

    @Override
    public String toString(final Object obj) {
        return wrapped.toString(obj);
    }

    @Override
    public Object fromString(final String str) {
        return wrapped.fromString(str);
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        writer.setValue(toString(source));
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        return fromString(reader.getValue());
    }

    @Override
    public void appendErrors(final ErrorWriter errorWriter) {
        errorWriter.add("wrapped-converter", wrapped == null ? "(null)" : wrapped.getClass().getName());
        if (wrapped instanceof ErrorReporter) {
            ((ErrorReporter)wrapped).appendErrors(errorWriter);
        }
    }
}
