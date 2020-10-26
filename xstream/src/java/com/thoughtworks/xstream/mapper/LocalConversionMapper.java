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

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.core.util.FastField;


/**
 * A Mapper for locally defined converters for a member field.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class LocalConversionMapper extends MapperWrapper {

    private final Map<FastField, Converter> localConverters = new HashMap<>();
    private transient AttributeMapper attributeMapper;

    /**
     * Constructs a LocalConversionMapper.
     * 
     * @param wrapped
     * @since 1.3
     */
    public LocalConversionMapper(final Mapper wrapped) {
        super(wrapped);
        readResolve();
    }

    public void registerLocalConverter(final Class<?> definedIn, final String fieldName, final Converter converter) {
        localConverters.put(new FastField(definedIn, fieldName), converter);
    }

    @Override
    public Converter getLocalConverter(final Class<?> definedIn, final String fieldName) {
        return localConverters.get(new FastField(definedIn, fieldName));
    }

    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> definedIn, final String attribute,
            final Class<?> type) {
        final SingleValueConverter converter = getLocalSingleValueConverter(definedIn, attribute, type);
        return converter == null ? super.getConverterFromAttribute(definedIn, attribute, type) : converter;
    }

    @Override
    public SingleValueConverter getConverterFromItemType(final String fieldName, final Class<?> type,
            final Class<?> definedIn) {
        final SingleValueConverter converter = getLocalSingleValueConverter(definedIn, fieldName, type);
        return converter == null ? super.getConverterFromItemType(fieldName, type, definedIn) : converter;
    }

    private SingleValueConverter getLocalSingleValueConverter(final Class<?> definedIn, final String fieldName,
            final Class<?> type) {
        if (attributeMapper != null && attributeMapper.shouldLookForSingleValueConverter(fieldName, type, definedIn)) {
            final Converter converter = getLocalConverter(definedIn, fieldName);
            if (converter != null && converter instanceof SingleValueConverter) {
                return (SingleValueConverter)converter;
            }
        }
        return null;
    }

    private Object readResolve() {
        attributeMapper = lookupMapperOfType(AttributeMapper.class);
        return this;
    }
}
