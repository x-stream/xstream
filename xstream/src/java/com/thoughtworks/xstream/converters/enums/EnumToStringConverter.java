/*
 * Copyright (C) 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 14. March 2013 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;


/**
 * A single value converter for a special enum type using its string representation.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4.5
 */
public class EnumToStringConverter<T extends Enum<T>> extends AbstractSingleValueConverter {

    private final Class<T> enumType;
    private final Map<String, T> strings;
    private final EnumMap<T, String> values;

    public EnumToStringConverter(Class<T> type) {
        this(type, extractStringMap(type), null);
    }

    public EnumToStringConverter(Class<T> type, Map<String, T> strings) {
        this(type, strings, buildValueMap(type, strings));
    }

    private EnumToStringConverter(
        Class<T> type, Map<String, T> strings, EnumMap<T, String> values) {
        enumType = type;
        this.strings = strings;
        this.values = values;
    }

    private static <T extends Enum<T>> Map<String, T> extractStringMap(Class<T> type) {
        checkType(type);
        EnumSet<T> values = EnumSet.allOf(type);
        Map<String, T> strings = new HashMap<String, T>(values.size());
        for (T value : values) {
            if (strings.put(value.toString(), value) != null) {
                throw new IllegalArgumentException("Enum type "
                    + type.getName()
                    + " does not have unique string representations for its values");
            }
        }
        return strings;
    }

    private static <T> void checkType(Class<T> type) {
        if (!Enum.class.isAssignableFrom(type) && type != Enum.class) {
            throw new IllegalArgumentException("Converter can only handle enum types");
        }
    }

    private static <T extends Enum<T>> EnumMap<T, String> buildValueMap(Class<T> type,
        Map<String, T> strings) {
        EnumMap<T, String> values = new EnumMap<T, String>(type);
        for (Map.Entry<String, T> entry : strings.entrySet()) {
            values.put(entry.getValue(), entry.getKey());
        }
        return values;
    }

    @Override
    public boolean canConvert(Class type) {
        return enumType.isAssignableFrom(type);
    }

    @Override
    public String toString(Object obj) {
        Enum value = Enum.class.cast(obj);
        return values == null ? value.toString() : values.get(value);
    }

    @Override
    public Object fromString(String str) {
        if (str == null) {
            return null;
        }
        T result = strings.get(str);
        if (result == null) {
            throw new ConversionException("Invalid string representation for enum type "
                + enumType.getName()
                + ": <"
                + str
                + ">");
        }
        return result;
    }
}
