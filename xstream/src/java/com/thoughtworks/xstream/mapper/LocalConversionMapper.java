/*
 * Copyright (C) 2007, 2008, 2014, 2015, 2024 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 06. November 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.mapper;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.core.util.MemberStore;


/**
 * A Mapper for locally defined converters for a member field.
 *
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class LocalConversionMapper extends MapperWrapper {

    private final MemberStore<Converter> localConverters = MemberStore.newInstance();
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
        localConverters.put(definedIn, fieldName, converter);
    }

    @Override
    public Converter getLocalConverter(final Class<?> definedIn, final String fieldName) {
        return localConverters.get(definedIn, fieldName);
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
