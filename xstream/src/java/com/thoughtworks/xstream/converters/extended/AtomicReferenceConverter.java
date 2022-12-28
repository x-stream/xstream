/*
 * Copyright (C) 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. November 2022 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import java.util.concurrent.atomic.AtomicReference;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts an AtomicReference type.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.20
 */
public class AtomicReferenceConverter implements Converter {

    private final Mapper mapper;

    public AtomicReferenceConverter(final Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && type == AtomicReference.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final AtomicReference<?> ref = (AtomicReference<?>)source;
        writer.startNode(mapper.serializedMember(AtomicReference.class, "value"));

        final Object object = ref.get();
        final String name = mapper.serializedClass(object != null ? object.getClass() : null);
        writer.addAttribute(mapper.aliasForSystemAttribute("class"), name);
        context.convertAnother(ref.get());
        writer.endNode();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        reader.moveDown();

        final Class<?> type = HierarchicalStreams.readClassType(reader, mapper);
        final Object value = context.convertAnother(context, type);
        reader.moveUp();
        return new AtomicReference<>(value);
    }

}
