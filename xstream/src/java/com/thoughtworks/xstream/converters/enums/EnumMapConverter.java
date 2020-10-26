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
