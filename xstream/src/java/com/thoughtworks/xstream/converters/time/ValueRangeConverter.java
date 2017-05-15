/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 22. February 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.temporal.ValueRange;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a temporal {@link ValueRange}, using four nested elements: maxLargest, maxSmallest, minLargest, and
 * minSmallest.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class ValueRangeConverter implements Converter {

    private final Mapper mapper;

    /**
     * Constructs a ValueRangeConverter instance.
     * 
     * @param mapper the Mapper instance
     */
    public ValueRangeConverter(final Mapper mapper) {
        this.mapper = mapper;

    }

    @Override
    public boolean canConvert(@SuppressWarnings("rawtypes") final Class type) {
        return type == ValueRange.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final ValueRange valueRange = (ValueRange)source;
        write("maxLargest", valueRange.getMaximum(), writer);
        write("maxSmallest", valueRange.getSmallestMaximum(), writer);
        write("minLargest", valueRange.getLargestMinimum(), writer);
        write("minSmallest", valueRange.getMinimum(), writer);
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final boolean oldFormat = "custom".equals(reader.getAttribute(mapper.aliasForSystemAttribute("serialization")));
        if (oldFormat) {
            reader.moveDown();
            reader.moveDown();
        }
        final Map<String, Long> elements = new HashMap<>();
        while (reader.hasMoreChildren()) {
            reader.moveDown();

            final String name = reader.getNodeName();
            elements.put(oldFormat ? name : mapper.realMember(ValueRange.class, name), Long.valueOf(reader.getValue()));
            reader.moveUp();
        }
        if (oldFormat) {
            reader.moveUp();
            reader.moveUp();
        }
        return ValueRange.of(elements.get("minSmallest").longValue(), elements.get("minLargest").longValue(), elements
            .get("maxSmallest")
            .longValue(), elements.get("maxLargest").longValue());
    }

    private void write(final String fieldName, final long value, final HierarchicalStreamWriter writer) {
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper.serializedMember(ValueRange.class, fieldName),
            long.class);
        writer.setValue(String.valueOf(value));
        writer.endNode();
    }
}
