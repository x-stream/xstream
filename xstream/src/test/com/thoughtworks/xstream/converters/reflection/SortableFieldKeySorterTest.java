/*
 * Copyright (C) 2007 XStream Committers.
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
import java.util.Map;

import com.thoughtworks.xstream.core.util.OrderRetainingMap;
import com.thoughtworks.xstream.io.StreamException;

import junit.framework.TestCase;


public class SortableFieldKeySorterTest extends TestCase {

    public void testDoesNotAffectUnregisteredTypes() {
        SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        sorter.registerFieldOrder(Mother.class, new String[]{"field2", "field1"});
        sorter.registerFieldOrder(Child.class, new String[]{"field2", "field1"});
        Map originalMap = buildMap(Base.class);
        Map map = sorter.sort(Base.class, originalMap);
        assertEquals(originalMap, map);
    }

    public void testIgnoresUnknownFields() {
        SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        String[] fieldOrder = new String[]{"whatever", "field2", "field1", "field3"};
        sorter.registerFieldOrder(Child.class, fieldOrder);
        Map originalMap = buildMap(Child.class);
        Map map = sorter.sort(Child.class, originalMap);
        Field[] fields = (Field[])map.values().toArray(new Field[map.size()]);
        assertEquals(fieldOrder.length - 1, fields.length);
        for (int i = 1; i < fieldOrder.length; i++) {
            assertEquals(fieldOrder[i], fields[i - 1].getName());
        }
    }

    public void testComplainsIfSomeFieldIsNotSpecified() {
        SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        sorter.registerFieldOrder(Base.class, new String[]{"field3"});
        try {
            sorter.sort(Base.class, buildMap(Base.class));
            fail();
        } catch (StreamException ex) {
            // ok
        }
    }

    public void testSortsMapAccordingToDefinedFieldOrder() {
        SortableFieldKeySorter sorter = new SortableFieldKeySorter();
        String[] fieldOrder = new String[]{"field2", "field1", "field3"};
        sorter.registerFieldOrder(Child.class, fieldOrder);
        Map originalMap = buildMap(Child.class);
        Map map = sorter.sort(Child.class, originalMap);
        Field[] fields = (Field[])map.values().toArray(new Field[map.size()]);
        assertEquals(fieldOrder.length, fields.length);
        for (int i = 0; i < fieldOrder.length; i++) {
            assertEquals(fieldOrder[i], fields[i].getName());
        }
    }

    private Map buildMap(Class type) {
        Map map = new OrderRetainingMap();
        Class cls = type;
        while (!cls.equals(Object.class)) {
            Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                map.put(new FieldKey(field.getName(), cls, i), field);
            }
            cls = cls.getSuperclass();
        }
        return map;
    }

    static class Base extends Mother {
        String field3;
    }

    static class Child extends Base {
    }

    static class Mother {
        String field1, field2;
    }

}
