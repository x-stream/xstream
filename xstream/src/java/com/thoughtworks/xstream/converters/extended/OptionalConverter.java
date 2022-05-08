/*
 * Copyright (C) 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 8. May 2022.
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.Optional;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts an {@link Optional}.
 */
public class OptionalConverter implements Converter {

    private final Mapper mapper;

    public OptionalConverter(final Mapper mapper) {
        this.mapper = mapper;
    }

    public boolean canConvert(Class<?> type){
        return type != null && type == Optional.class;
    }

    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Optional<?> optional = (Optional<?>) source;

        if (optional.isPresent()) {
            Object item = optional.get();
            String name = mapper.serializedClass(item.getClass());

            writer.startNode(name);
            context.convertAnother(item);
            writer.endNode();
        } else {
            final String name = mapper.serializedClass(null);
            writer.startNode(name);
            writer.endNode();
        }
    }

    public Optional<?> unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        reader.moveDown();
        final Class<?> type = HierarchicalStreams.readClassType(reader, mapper);
        Object item = context.convertAnother(null, type);
        reader.moveUp();

        return Optional.ofNullable(item);
    }

}
