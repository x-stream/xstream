/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2009, 2013, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 18. March 2005 by Joe Walnes
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
