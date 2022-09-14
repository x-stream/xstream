/*
 * Copyright (C) 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 9 September 2022 by Basil Crow.
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.basic.BooleanConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.concurrent.atomic.AtomicBoolean;

/** Converts an {@link AtomicBoolean}. */
public class AtomicBooleanConverter implements Converter {

    @Override
    public boolean canConvert(Class<?> type) {
        return type != null && AtomicBoolean.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(
            Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        final AtomicBoolean atomicBoolean = (AtomicBoolean) source;
        writer.startNode("value");
        context.convertAnother(
                atomicBoolean.get(), new SingleValueConverterWrapper(BooleanConverter.BINARY));
        writer.endNode();
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        reader.moveDown();
        Object item =
                context.convertAnother(
                        null,
                        Boolean.class,
                        new SingleValueConverterWrapper(BooleanConverter.BINARY));
        boolean value = item instanceof Boolean ? ((Boolean) item).booleanValue() : false;
        reader.moveUp();
        return new AtomicBoolean(value);
    }
}
