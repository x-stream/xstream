/*
 * Copyright (C) 2007, 2009, 2011, 2014, 2015, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 10. April 2007 by Guilherme Silveira
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.Caching;


/**
 * The default implementation for sorting fields. Invoke registerFieldOrder in order to set the field order for an
 * specific type.
 *
 * @author Guilherme Silveira
 * @since 1.2.2
 */
public class SortableFieldKeySorter implements FieldKeySorter, Caching {

    private final static FieldKey[] EMPTY_FIELD_KEY_ARRAY = {};
    private final Map<Class<?>, Comparator<FieldKey>> map = new HashMap<>();

    @Override
    public Map<FieldKey, Field> sort(final Class<?> type, final Map<FieldKey, Field> keyedByFieldKey) {
        if (map.containsKey(type)) {
            final Map<FieldKey, Field> result = new LinkedHashMap<>();
            final FieldKey[] fieldKeys = keyedByFieldKey.keySet().toArray(EMPTY_FIELD_KEY_ARRAY);
            Arrays.sort(fieldKeys, map.get(type));
            for (final FieldKey fieldKey : fieldKeys) {
                result.put(fieldKey, keyedByFieldKey.get(fieldKey));
            }
            return result;
        } else {
            return keyedByFieldKey;
        }
    }

    /**
     * Registers the field order to use for a specific type. This will not affect any of the type's super or sub
     * classes. If you skip a field which will be serialized, XStream will thrown a {@link ConversionException} during
     * the serialization process.
     *
     * @param type the type
     * @param fields the field order
     */
    public void registerFieldOrder(final Class<?> type, final String[] fields) {
        map.put(type, new FieldComparator(type, fields));
    }

    private class FieldComparator implements Comparator<FieldKey> {

        private final String[] fieldOrder;
        private final Class<?> type;

        public FieldComparator(final Class<?> type, final String[] fields) {
            this.type = type;
            fieldOrder = fields;
        }

        private int compare(final String first, final String second) {
            int firstPosition = -1, secondPosition = -1;
            for (int i = 0; i < fieldOrder.length; i++) {
                if (fieldOrder[i].equals(first)) {
                    firstPosition = i;
                }
                if (fieldOrder[i].equals(second)) {
                    secondPosition = i;
                }
            }
            if (firstPosition == -1 || secondPosition == -1) {
                // field not defined!!!
                final ConversionException exception = new ConversionException(
                    "Incomplete list of serialized fields for type");
                exception.add("sort-type", type.getName());
                throw exception;
            }
            return firstPosition - secondPosition;
        }

        @Override
        public int compare(final FieldKey first, final FieldKey second) {
            return compare(first.getFieldName(), second.getFieldName());
        }

    }

    @Override
    public void flushCache() {
        map.clear();
    }
}
