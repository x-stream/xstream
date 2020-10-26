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

package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.EnumMapper;


/**
 * Converter for {@link Enum} types.
 * <p>
 * Combined with {@link EnumMapper} this also deals with polymorphic enums.
 * </p>
 * 
 * @author Eric Snell
 * @author Bryan Coleman
 */
public class EnumConverter implements Converter {

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && type.isEnum() || Enum.class.isAssignableFrom(type);
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        writer.setValue(((Enum<?>)source).name());
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        Class<?> type = context.getRequiredType();
        if (type.getSuperclass() != Enum.class) {
            type = type.getSuperclass(); // polymorphic enums
        }
        final String name = reader.getValue();
        try {
            @SuppressWarnings("rawtypes")
            final Class rawType = type;
            @SuppressWarnings("unchecked")
            final Enum<?> enumValue = Enum.valueOf(rawType, name);
            return enumValue;
        } catch (final IllegalArgumentException e) {
            // failed to find it, do a case insensitive match
            for (final Enum<?> c : (Enum<?>[])type.getEnumConstants()) {
                if (c.name().equalsIgnoreCase(name)) {
                    return c;
                }
            }
            // all else failed
            throw e;
        }
    }

}
