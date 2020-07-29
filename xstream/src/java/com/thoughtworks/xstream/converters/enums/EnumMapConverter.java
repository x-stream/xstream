/*
 * Copyright (C) 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2013, 2014, 2020 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. April 2005 by Joe Walnes
 */

package com.thoughtworks.xstream.converters.enums;

import java.lang.reflect.Field;
import java.util.EnumMap;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts an {@link EnumMap}, including the type of Enum it's for.
 * <p>
 * If a {@link SecurityManager} is set, the converter will only work with permissions for
 * SecurityManager.checkPackageAccess, SecurityManager.checkMemberAccess(this, EnumSet.MEMBER) and
 * ReflectPermission("suppressAccessChecks").
 * </p>
 *
 * @author Joe Walnes
 */
public class EnumMapConverter extends MapConverter {

    public EnumMapConverter(final Mapper mapper) {
        super(mapper);
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type == EnumMap.class && Reflections.typeField != null;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final Class<?> type = (Class<?>)Fields.read(Reflections.typeField, source);
        final String attributeName = mapper().aliasForSystemAttribute("enum-type");
        if (attributeName != null) {
            writer.addAttribute(attributeName, mapper().serializedClass(type));
        }
        super.marshal(source, writer, context);
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final String attributeName = mapper().aliasForSystemAttribute("enum-type");
        if (attributeName == null) {
            throw new ConversionException("No EnumType specified for EnumMap");
        }
        final Class<?> type = mapper().realClass(reader.getAttribute(attributeName));
        @SuppressWarnings({"rawtypes", "unchecked"})
        final EnumMap<?, ?> map = new EnumMap(type);
        populateMap(reader, context, map);
        return map;
    }

    private static class Reflections {
        private final static Field typeField = Fields.locate(EnumMap.class, Class.class, false);
    }
}
