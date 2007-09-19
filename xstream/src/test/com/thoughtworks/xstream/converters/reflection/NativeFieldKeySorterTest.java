package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.core.util.OrderRetainingMap;

import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.Map;


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
        String[] fieldOrder = new String[]{"yyy", "ccc", "bbb", "aaa", "xxx", "zzz"};
        FieldKeySorter sorter = new NativeFieldKeySorter();
        Map originalMap = buildMap(Second.class);
        Map map = sorter.sort(Second.class, originalMap);
        Field[] fields = (Field[])map.values().toArray(new Field[map.size()]);
        assertEquals(fieldOrder.length, fields.length);
        for (int i = 0; i < fieldOrder.length; i++) {
            assertEquals("Field[" + i + ']', fieldOrder[i], fields[i].getName());
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
}
