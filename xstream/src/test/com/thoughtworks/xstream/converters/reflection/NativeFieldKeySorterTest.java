/*
 * Copyright (C) 2007, 2018 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 17. May 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;


public class NativeFieldKeySorterTest extends TestCase {

    static class Base {
        String yyy;
        String ccc;
        String bbb;
    }

    static class First extends Base {
        String aaa;
    }

    static class Second extends First {
        String xxx;
        String zzz;
    }

    public void testDoesSortInDeclarationOrderWithFieldsOfBaseClassFirst() {
        final String[] fieldOrder = new String[]{"yyy", "ccc", "bbb", "aaa", "xxx", "zzz"};
        final FieldKeySorter sorter = new NativeFieldKeySorter();
        final Map<FieldKey, Field> originalMap = buildMap(Second.class);
        final Map<FieldKey, Field> map = sorter.sort(Second.class, originalMap);
        final Field[] fields = map.values().toArray(new Field[map.size()]);
        assertEquals(fieldOrder.length, fields.length);
        for (int i = 0; i < fieldOrder.length; i++) {
            assertEquals("Field[" + i + ']', fieldOrder[i], fields[i].getName());
        }
    }

    private Map<FieldKey, Field> buildMap(final Class<?> type) {
        final Map<FieldKey, Field> map = new LinkedHashMap<>();
        Class<?> cls = type;
        while (!cls.equals(Object.class)) {
            final Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                final Field field = fields[i];
                if (field.isSynthetic() && field.getName().startsWith("$jacoco")) {
                    continue;
                }
                map.put(new FieldKey(field.getName(), cls, i), field);
            }
            cls = cls.getSuperclass();
        }
        return map;
    }
}
