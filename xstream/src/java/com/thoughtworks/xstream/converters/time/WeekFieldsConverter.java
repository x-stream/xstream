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

import java.time.DayOfWeek;
import java.time.temporal.WeekFields;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter.UnknownFieldException;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts a {@link WeekFields} instance, using two nested elements: minimalDays and minSmallest.
 *
 * @author J&ouml;rg Schaible
 * @since 1.4.10
 */
public class WeekFieldsConverter implements Converter {

    private final Mapper mapper;

    /**
     * Constructs a WeekFieldsConverter instance.
     *
     * @param mapper the Mapper instance
     */
    public WeekFieldsConverter(final Mapper mapper) {
        this.mapper = mapper;

    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == WeekFields.class;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final WeekFields weekFields = (WeekFields)source;
        writer.startNode(mapper.serializedMember(WeekFields.class, "minimalDays"), int.class);
        writer.setValue(String.valueOf(weekFields.getMinimalDaysInFirstWeek()));
        writer.endNode();
        writer.startNode(mapper.serializedMember(WeekFields.class, "firstDayOfWeek"), DayOfWeek.class);
        context.convertAnother(weekFields.getFirstDayOfWeek());
        writer.endNode();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final boolean oldFormat = "custom".equals(reader.getAttribute(mapper.aliasForSystemAttribute("serialization")));
        if (oldFormat) {
            reader.moveDown();
            reader.moveDown();
        }

        int minimalDays = 0;
        DayOfWeek firstDayOfWeek = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            final String name = oldFormat
                ? reader.getNodeName()
                : mapper.realMember(WeekFields.class, reader.getNodeName());
            if ("minimalDays".equals(name)) {
                minimalDays = Integer.parseInt(reader.getValue());
            } else if ("firstDayOfWeek".equals(name)) {
                firstDayOfWeek = (DayOfWeek)context.convertAnother(null, DayOfWeek.class);
            } else {
                throw new UnknownFieldException(WeekFields.class.getName(), name);
            }
            reader.moveUp();
        }
        if (oldFormat) {
            reader.moveUp();
            reader.moveUp();
        }
        return WeekFields.of(firstDayOfWeek, minimalDays);
    }
}
