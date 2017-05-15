/*
 * Copyright (C) 2017 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. March 2017 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.time;

import java.time.Clock;
import java.time.ZoneId;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a system {@link Clock}, using zone as nested element.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class SystemClockConverter implements Converter {

    private final Mapper mapper;
    private final Class<?> type;

    /**
     * Constructs a SystemClockConverter instance.
     * 
     * @param mapper the Mapper instance
     */
    public SystemClockConverter(final Mapper mapper) {
        this.mapper = mapper;
        type = Clock.systemUTC().getClass();
    }

    @Override
    public boolean canConvert(final Class type) {
        return type == this.type;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Clock clock = (Clock)source;
        ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper.serializedMember(Clock.class, "zone"), null);
        context.convertAnother(clock.getZone());
        writer.endNode();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        reader.moveDown();
        final ZoneId zone = (ZoneId)context.convertAnother(null, ZoneId.class);
        reader.moveUp();
        return Clock.system(zone);
    }
}
