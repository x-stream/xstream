/*
 * Copyright (C) 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17. December 2022 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.OptionalInt;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.IntConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * Converts an OptionalInt type.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.20
 */
public class OptionalIntConverter extends IntConverter implements Converter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && type == OptionalInt.class;
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
            final int value = (Integer)context.convertAnother(context, Integer.class);
            reader.moveUp();
            return isPresent ? OptionalInt.of(value) : OptionalInt.empty();
        }
    }

    @Override
    public String toString(final Object obj) {
        final OptionalInt optional = (OptionalInt)obj;
        return optional.isPresent() ? super.toString(optional.getAsInt()) : "";
    }

    @Override
    public Object fromString(final String str) {
        return str == null || str.length() == 0 ? OptionalInt.empty() : OptionalInt.of((Integer)super.fromString(str));
    }
}
