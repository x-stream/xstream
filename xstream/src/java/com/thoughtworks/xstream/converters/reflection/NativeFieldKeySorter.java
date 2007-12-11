/*
 * Copyright (C) 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 17.05.2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * Sort the fields in their natural order. Fields are returned in their declaration order,
 * fields of base classes first.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2.2
 */
public class NativeFieldKeySorter implements FieldKeySorter {

    public Map sort(final Class type, final Map keyedByFieldKey) {
        final Map map = new TreeMap(new Comparator() {

            public int compare(final Object o1, final Object o2) {
                final FieldKey fieldKey1 = (FieldKey)o1;
                final FieldKey fieldKey2 = (FieldKey)o2;
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
