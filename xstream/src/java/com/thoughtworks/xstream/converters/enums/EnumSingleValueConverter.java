/*
 * Copyright (C) 2008, 2009, 2010, 2013, 2014, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 12. February 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * A single value converter for a special enum type. Converter is internally automatically instantiated for enum types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class EnumSingleValueConverter<T extends Enum<T>> extends AbstractSingleValueConverter {

    private final Class<T> enumType;

    public EnumSingleValueConverter(final Class<T> type) {
        if (!Enum.class.isAssignableFrom(type) && !Enum.class.equals(type)) {
            throw new IllegalArgumentException("Converter can only handle defined enums");
        }
        enumType = type;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && enumType.isAssignableFrom(type);
    }

    @Override
    public String toString(final Object obj) {
        return Enum.class.cast(obj).name();
    }

    @Override
    public Object fromString(final String str) {
        return Enum.valueOf(enumType, str);
    }
}
