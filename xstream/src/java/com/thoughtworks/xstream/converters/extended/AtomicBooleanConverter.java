/*
 * Copyright (C) 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 26. November 2022 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.concurrent.atomic.AtomicBoolean;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts an AtomicBoolean type.
 *
 * @author Basil Crow
 * @author J&ouml;rg Schaible
 * @since 1.4.20
 */
public class AtomicBooleanConverter extends BooleanConverter implements Converter {

    /**
     * Constructs an AtomicBooleanConverter. Initializes the converter with <em>true</em> and <em>false</em> as
     * string representation.
     */
    public AtomicBooleanConverter() {
        super();
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && type == AtomicBoolean.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        writer.setValue(toString(source));
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final String data = reader.getValue(); // needs to be called before hasMoreChildren.
        if (!reader.hasMoreChildren()) {
            return fromString(data);
        } else {
            // backwards compatibility ... unmarshal nested element
            reader.moveDown();
            final AtomicBoolean atomicBoolean = new AtomicBoolean("1".equals(reader.getValue()));
            reader.moveUp();
            return atomicBoolean;
        }
    }

    @Override
    public String toString(final Object obj) {
        return super.toString(((AtomicBoolean)obj).get() ? Boolean.TRUE : Boolean.FALSE);
    }

    @Override
    public Object fromString(final String str) {
        return new AtomicBoolean(((Boolean)super.fromString(str)).booleanValue());
    }
}
