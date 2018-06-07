/*
 * Copyright (C) 2013, 2014, 2015, 2016, 2018 XStream Committers.
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
import java.util.Map;

import com.thoughtworks.xstream.InitializationException;
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

    public EnumToStringConverter(final Class<T> type) {
        this(type, extractStringMap(type), null);
    }

    public EnumToStringConverter(final Class<T> type, final Map<String, T> strings) {
        this(type, strings, buildValueMap(type, strings));
    }

    private EnumToStringConverter(final Class<T> type, final Map<String, T> strings, final EnumMap<T, String> values) {
        enumType = type;
        this.strings = strings;
        this.values = values;
    }

    private static <T extends Enum<T>> Map<String, T> extractStringMap(final Class<T> type) {
        checkType(type);
        final EnumSet<T> values = EnumSet.allOf(type);
        final Map<String, T> strings = new HashMap<>(values.size());
        for (final T value : values) {
            if (strings.put(value.toString(), value) != null) {
                throw new InitializationException("Enum type "
                    + type.getName()
                    + " does not have unique string representations for its values");
            }
        }
        return strings;
    }

    private static <T> void checkType(final Class<T> type) {
        if (!Enum.class.isAssignableFrom(type) && type != Enum.class) {
            throw new InitializationException("Converter can only handle enum types");
        }
    }

    private static <T extends Enum<T>> EnumMap<T, String> buildValueMap(final Class<T> type,
            final Map<String, T> strings) {
        final EnumMap<T, String> values = new EnumMap<>(type);
        for (final Map.Entry<String, T> entry : strings.entrySet()) {
            values.put(entry.getValue(), entry.getKey());
        }
        return values;
    }

    @Override
    public boolean canConvert(final Class<?> type) {
        return type != null && enumType.isAssignableFrom(type);
    }

    @Override
    public String toString(final Object obj) {
        return values == null ? obj.toString() : values.get(obj);
    }

    @Override
    public Object fromString(final String str) {
        if (str == null) {
            return null;
        }
        final T result = strings.get(str);
        if (result == null) {
            final ConversionException exception = new ConversionException(
                "Invalid string representation for enum type");
            exception.add("enum-type", enumType.getName());
            exception.add("enum-string", str);
            throw exception;
        }
        return result;
    }
}
