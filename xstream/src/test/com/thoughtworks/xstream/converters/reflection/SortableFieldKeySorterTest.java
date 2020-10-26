/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import com.thoughtworks.xstream.converters.ConversionException;

import junit.framework.TestCase;


public class SortableFieldKeySorterTest extends TestCase {

    public void testDoesNotAffectUnregisteredTypes() {
        final SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        sorter.registerFieldOrder(Mother.class, new String[]{"field2", "field1"});
        sorter.registerFieldOrder(Child.class, new String[]{"field2", "field1"});
        final Map<FieldKey, Field> originalMap = buildMap(Base.class);
        final Map<FieldKey, Field> map = sorter.sort(Base.class, originalMap);
        assertEquals(originalMap, map);
    }

    public void testIgnoresUnknownFields() {
        final SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        final String[] fieldOrder = new String[]{"whatever", "field2", "field1", "field3"};
        sorter.registerFieldOrder(Child.class, fieldOrder);
        final Map<FieldKey, Field> originalMap = buildMap(Child.class);
        final Map<FieldKey, Field> map = sorter.sort(Child.class, originalMap);
        final Field[] fields = map.values().toArray(new Field[map.size()]);
        assertEquals(fieldOrder.length - 1, fields.length);
        for (int i = 1; i < fieldOrder.length; i++) {
            assertEquals(fieldOrder[i], fields[i - 1].getName());
        }
    }

    public void testComplainsIfSomeFieldIsNotSpecified() {
        final SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        sorter.registerFieldOrder(Base.class, new String[]{"field3"});
        try {
            sorter.sort(Base.class, buildMap(Base.class));
            fail();
        } catch (final ConversionException ex) {
            assertEquals(Base.class.getName(), ex.get("sort-type"));
        }
    }

    public void testSortsMapAccordingToDefinedFieldOrder() {
        final SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        final String[] fieldOrder = new String[]{"field2", "field1", "field3"};
        sorter.registerFieldOrder(Child.class, fieldOrder);
        final Map<FieldKey, Field> originalMap = buildMap(Child.class);
        final Map<FieldKey, Field> map = sorter.sort(Child.class, originalMap);
        final Field[] fields = map.values().toArray(new Field[map.size()]);
        assertEquals(fieldOrder.length, fields.length);
        for (int i = 0; i < fieldOrder.length; i++) {
            assertEquals(fieldOrder[i], fields[i].getName());
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

    static class Base extends Mother {
        String field3;
    }

    static class Child extends Base {}

    static class Mother {
        String field1, field2;
    }

}
