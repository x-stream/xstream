/*
 * Copyright (C) 2007, 2009, 2011, 2016 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 10. April 2007 by Guilherme Silveira
 */
package com.thoughtworks.xstream.converters.reflection;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.core.Caching;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;


/**
 * The default implementation for sorting fields. Invoke registerFieldOrder in order to set the field order for an
 * specific type.
 *
 * @author Guilherme Silveira
 * @since 1.2.2
 */
public class SortableFieldKeySorter implements FieldKeySorter, Caching {

    private final static FieldKey[] EMPTY_FIELD_KEY_ARRAY = {};
    private final Map map = new HashMap();

    public Map sort(final Class type, final Map keyedByFieldKey) {
        if (map.containsKey(type)) {
            final Map result = new OrderRetainingMap();
            final FieldKey[] fieldKeys = (FieldKey[])keyedByFieldKey.keySet().toArray(EMPTY_FIELD_KEY_ARRAY);
            Arrays.sort(fieldKeys, (Comparator)map.get(type));
            for (int i = 0; i < fieldKeys.length; i++ ) {
                result.put(fieldKeys[i], keyedByFieldKey.get(fieldKeys[i]));
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
    public void registerFieldOrder(final Class type, final String[] fields) {
        map.put(type, new FieldComparator(type, fields));
    }

    private class FieldComparator implements Comparator {

        private final String[] fieldOrder;
        private final Class type;

        public FieldComparator(final Class type, final String[] fields) {
            this.type = type;
            fieldOrder = fields;
        }

        public int compare(final String first, final String second) {
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

        public int compare(final Object firstObject, final Object secondObject) {
            final FieldKey first = (FieldKey)firstObject, second = (FieldKey)secondObject;
            return compare(first.getFieldName(), second.getFieldName());
        }

    }

    public void flushCache() {
        map.clear();
    }
}
