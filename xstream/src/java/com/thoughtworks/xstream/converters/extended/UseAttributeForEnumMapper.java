/*
 * Copyright (C) 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 25. September 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.extended;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.mapper.AttributeMapper;
import com.thoughtworks.xstream.mapper.DefaultMapper;
import com.thoughtworks.xstream.mapper.EnumMapper;
import com.thoughtworks.xstream.mapper.Mapper;


class UseAttributeForEnumMapper extends AttributeMapper {

    public UseAttributeForEnumMapper(final Mapper wrapped) {
        super(wrapped, null, null);
    }

    @Override
    public boolean shouldLookForSingleValueConverter(final String fieldName, final Class<?> type,
            final Class<?> definedIn) {
        return Enum.class.isAssignableFrom(type);
    }

    @Override
    public SingleValueConverter getConverterFromItemType(final String fieldName, final Class<?> type,
            final Class<?> definedIn) {
        return null;
    }

    @Override
    public SingleValueConverter getConverterFromAttribute(final Class<?> definedIn, final String attribute,
            final Class<?> type) {
        return null;
    }

    static Mapper createEnumMapper(final Mapper mapper) {
        return new EnumMapper(new UseAttributeForEnumMapper(mapper.lookupMapperOfType(DefaultMapper.class)));
    }
}
