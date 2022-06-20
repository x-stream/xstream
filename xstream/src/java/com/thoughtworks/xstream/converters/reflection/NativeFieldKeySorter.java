/*
 * Copyright (C) 2007, 2014, 2022 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17.05.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * Sort the fields in their natural order. Fields are returned in their declaration order, fields of base classes first.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
public class NativeFieldKeySorter implements FieldKeySorter {

    @Override
    public Map<FieldKey, Field> sort(final Class<?> type, final Map<FieldKey, Field> keyedByFieldKey) {
        final Map<FieldKey, Field> map = new TreeMap<>(new Comparator<FieldKey>() {

            @Override
            public int compare(final FieldKey fieldKey1, final FieldKey fieldKey2) {
                int i = fieldKey1.getDepth() - fieldKey2.getDepth();
                if (i == 0) {
                    i = fieldKey1.getOrder() - fieldKey2.getOrder();
                }
                return i;
            }
        });
        map.putAll(keyedByFieldKey);
        return map;
    }

}
