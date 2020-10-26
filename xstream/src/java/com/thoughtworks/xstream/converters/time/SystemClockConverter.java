/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.converters.time;

import java.time.Clock;
import java.time.ZoneId;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
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
    public boolean canConvert(final Class<?> type) {
        return type == this.type;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Clock clock = (Clock)source;
        writer.startNode(mapper.serializedMember(Clock.class, "zone"), null);
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
