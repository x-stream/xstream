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

package com.thoughtworks.xstream.mapper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.enums.EnumSingleValueConverter;
import com.thoughtworks.xstream.core.Caching;


/**
 * Mapper that handles the special case of polymorphic enums in Java 1.5. This renames MyEnum$1 to MyEnum making it less
 * bloaty in the XML and avoiding the need for an alias per enum value to be specified. Additionally every enum is
 * treated automatically as immutable and non-refrenceable type that can be written as attribute.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class EnumMapper extends MapperWrapper implements Caching {

    private transient AttributeMapper attributeMapper;
    private transient Map<Class<?>, SingleValueConverter> enumConverterMap;

    public EnumMapper(final Mapper wrapped) {
        super(wrapped);
        readResolve();
    }

    @Override
    public String serializedClass(final Class<?> type) {
        if (type == null) {
            return super.serializedClass(type);
        }
        if (Enum.class.isAssignableFrom(type) && type.getSuperclass() != Enum.class) {
            return super.serializedClass(type.getSuperclass());
        } else if (EnumSet.class.isAssignableFrom(type)) {
            return super.serializedClass(EnumSet.class);
        } else {
            return super.serializedClass(type);
        }
    }

    @Override
    public boolean isImmutableValueType(final Class<?> type) {
        return Enum.class.isAssignableFrom(type) || super.isImmutableValueType(type);
    }

    @Override
    public boolean isReferenceable(final Class<?> type) {
        if (type != null && Enum.class.isAssignableFrom(type)) {
            return false;
        } else {
            return super.isReferenceable(type);
        }
    }

    @Override
    public SingleValueConverter getConverterFromItemType(final String fieldName, final Class<?> type,
            final Class<?> definedIn) {
        final SingleValueConverter converter = getLocalConverter(fieldName, type, definedIn);
        return converter == null ? super.getConverterFromItemType(fieldName, type, definedIn) : converter;
    }

    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> definedIn, final String attribute,
            final Class<?> type) {
        final SingleValueConverter converter = getLocalConverter(attribute, type, definedIn);
        return converter == null ? super.getConverterFromAttribute(definedIn, attribute, type) : converter;
    }

    private SingleValueConverter getLocalConverter(final String fieldName, final Class<?> type, final Class<?> definedIn) {
        if (attributeMapper != null
                && Enum.class.isAssignableFrom(type)
                && attributeMapper.shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
            synchronized (enumConverterMap) {
                SingleValueConverter singleValueConverter = enumConverterMap.get(type);
                if (singleValueConverter == null) {
                    singleValueConverter = super.getConverterFromItemType(fieldName, type, definedIn);
                    if (singleValueConverter == null) {
                        @SuppressWarnings("unchecked")
                        final Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>)type;
                        @SuppressWarnings({"rawtypes", "unchecked"})
                        final EnumSingleValueConverter<?> enumConverter = new EnumSingleValueConverter(enumType);
                        singleValueConverter = enumConverter;
                    }
                    enumConverterMap.put(type, singleValueConverter);
                }
                return singleValueConverter;
            }
        }
        return null;
    }

    @Override
    public void flushCache() {
        if (enumConverterMap.size() > 0) {
            synchronized (enumConverterMap) {
                enumConverterMap.clear();
            }
        }
    }

    private Object readResolve() {
        enumConverterMap = new HashMap<>();
        attributeMapper = lookupMapperOfType(AttributeMapper.class);
        return this;
    }
}
