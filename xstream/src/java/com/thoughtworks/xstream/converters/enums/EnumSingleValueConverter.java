/*
 * Copyright (C) 2008, 2009, 2010 XStream Committers.
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
 * A single value converter for arbitrary enums. Converter is internally automatically
 * instantiated for enum types.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3
 */
public class EnumSingleValueConverter extends AbstractSingleValueConverter {

    private final Class<? extends Enum> enumType;

    public EnumSingleValueConverter(Class<? extends Enum> type) {
        if (!Enum.class.isAssignableFrom(type) && type != Enum.class) {
            throw new IllegalArgumentException("Converter can only handle defined enums");
        }
        enumType = type;
    }

    @Override
    public boolean canConvert(Class type) {
        return enumType.isAssignableFrom(type);
    }

    @Override
    public String toString(Object obj) {
        return Enum.class.cast(obj).name();
    }

    @Override
    public Object fromString(String str) {
        @SuppressWarnings("unchecked")
        Enum result = Enum.valueOf(enumType, str);
        return result;
    }
}
