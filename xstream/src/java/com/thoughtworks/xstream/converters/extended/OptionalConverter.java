/*
 * Copyright (C) 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 8. December 2022 by Joerg Schaible
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
 * Converts an Optional type.
 *
 * @author Emanuel Alves
 * @author J&ouml;rg Schaible
 * @since 1.4.20
 */
public class OptionalConverter implements Converter {

    private final Mapper mapper;

    public OptionalConverter(final Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && type == Optional.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Optional<?> optional = (Optional<?>)source;
        if (optional.isPresent()) {
            writer.startNode(mapper.serializedMember(Optional.class, "value"));

            final Object object = optional.get();
            final String name = mapper.serializedClass(object != null ? object.getClass() : null);
            writer.addAttribute(mapper.aliasForSystemAttribute("class"), name);
            context.convertAnother(optional.get());
            writer.endNode();
        }
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final Object value;
        if (reader.hasMoreChildren()) {
            reader.moveDown();

            final Class<?> type = HierarchicalStreams.readClassType(reader, mapper);
            value = context.convertAnother(context, type);
            reader.moveUp();
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }

}
