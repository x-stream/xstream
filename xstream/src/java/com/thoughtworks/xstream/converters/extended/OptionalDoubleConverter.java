/*
 * Copyright (C) 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 14. December 2022 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.OptionalDouble;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.DoubleConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts an OptionalDouble type.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.20
 */
public class OptionalDoubleConverter extends DoubleConverter implements Converter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && type == OptionalDouble.class;
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
            reader.moveDown();
            final boolean isPresent = (Boolean)context.convertAnother(context, Boolean.class);
            reader.moveUp();
            reader.moveDown();
            final double value = (Double)context.convertAnother(context, Double.class);
            reader.moveUp();
            return isPresent ? OptionalDouble.of(value) : OptionalDouble.empty();
        }
    }

    @Override
    public String toString(final Object obj) {
        final OptionalDouble optional = (OptionalDouble)obj;
        return optional.isPresent() ? super.toString(optional.getAsDouble()) : "";
    }

    @Override
    public Object fromString(final String str) {
        return str == null || str.length() == 0
            ? OptionalDouble.empty()
            : OptionalDouble.of((Double)super.fromString(str));
    }
}
