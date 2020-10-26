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
import java.util.EnumSet;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;


/**
 * Converts an {@link EnumSet}.
 * <p>
 * If a SecurityManager is set, the converter will only work with permissions for SecurityManager.checkPackageAccess,
 * SecurityManager.checkMemberAccess(this, EnumSet.MEMBER) and ReflectPermission("suppressAccessChecks").
 * </p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class EnumSetConverter implements Converter {

    private final Mapper mapper;

    public EnumSetConverter(final Mapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && EnumSet.class.isAssignableFrom(type) && Reflections.typeField != null;
    }

    @Override
    public void marshal(final Object source, final HierarchicalStreamWriter writer, final MarshallingContext context) {
        final EnumSet<?> set = (EnumSet<?>)source;
        final Class<?> enumTypeForSet = (Class<?>)Fields.read(Reflections.typeField, set);
        final String attributeName = mapper.aliasForSystemAttribute("enum-type");
        if (attributeName != null) {
            writer.addAttribute(attributeName, mapper.serializedClass(enumTypeForSet));
        }
        writer.setValue(joinEnumValues(set));
    }

    private String joinEnumValues(final EnumSet<?> set) {
        boolean seenFirst = false;
        final StringBuilder result = new StringBuilder();
        for (final Enum<?> value : set) {
            if (seenFirst) {
                result.append(',');
            } else {
                seenFirst = true;
            }
            result.append(value.name());
        }
        return result.toString();
    }

    @Override
    public Object unmarshal(final HierarchicalStreamReader reader, final UnmarshallingContext context) {
        final String attributeName = mapper.aliasForSystemAttribute("enum-type");
        if (attributeName == null) {
            throw new ConversionException("No EnumType specified for EnumSet");
        }
        @SuppressWarnings("rawtypes")
        final Class enumTypeForSet = mapper.realClass(reader.getAttribute(attributeName));
        @SuppressWarnings("unchecked")
        final EnumSet<?> set = create(enumTypeForSet, reader.getValue());
        return set;
    }

    private <T extends Enum<T>> EnumSet<T> create(final Class<T> type, final String s) {
        final String[] enumValues = s.split(",");
        final EnumSet<T> set = EnumSet.noneOf(type);
        for (final String enumValue : enumValues) {
            if (enumValue.length() > 0) {
                final T value = Enum.valueOf(type, enumValue);
                set.add(value);
            }
        }
        return set;
    }

    private static class Reflections {
        private final static Field typeField = Fields.locate(EnumSet.class, Class.class, false);
    }
}
