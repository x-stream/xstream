package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class FieldDictionary {

    private static final Map keyedByFieldNameCache = new TreeMap();
    private static final Map keyedByFieldKeyCache = new TreeMap();

    public Iterator serializableFieldsFor(Class cls) {
        return buildMap(cls, true).values().iterator();
    }

    public Field field(Class cls, String name) {
        Map fields = buildMap(cls, false);
        Field field = (Field) fields.get(name);
        if (field == null) {
            throw new ObjectAccessException("No such field " + cls.getName() + "." + name);
        } else {
            return field;
        }
    }

    public Field field(Class cls, String name, Class definedIn) {
        Map fields = buildMap(cls, true);
        Field field = (Field) fields.get(new FieldKey(name, definedIn));
        if (field == null) {
            throw new ObjectAccessException("No such field " + cls.getName() + "." + name);
        } else {
            return field;
        }
    }

    private Map buildMap(Class cls, boolean tupleKeyed) {
        final String clsName = cls.getName();
        if (!keyedByFieldNameCache.containsKey(clsName)) {
            final Map keyedByFieldName = new TreeMap();
            final Map keyedByFieldKey = new TreeMap();
            while (!Object.class.equals(cls)) {
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    if (field.getName().startsWith("this$")) {
                        continue;
                    }
                    field.setAccessible(true);
                    if (!keyedByFieldName.containsKey(field.getName())) {
                        keyedByFieldName.put(field.getName(), field);
                    }
                    keyedByFieldKey.put(new FieldKey(field.getName(), field.getDeclaringClass()), field);
                }
                cls = cls.getSuperclass();
            }
            keyedByFieldNameCache.put(clsName, keyedByFieldName);
            keyedByFieldKeyCache.put(clsName, keyedByFieldKey);
        }
        return (Map) (tupleKeyed ? keyedByFieldKeyCache.get(clsName) : keyedByFieldNameCache.get(clsName));
    }

    private static class FieldKey implements Comparable {
        private String fieldName;
        private Class declaringClass;
        private Integer depth;

        public FieldKey(String fieldName, Class declaringClass) {
            this.fieldName = fieldName;
            this.declaringClass = declaringClass;
            Class c = declaringClass;
            int i = 0;
            while (c.getSuperclass() != null) {
                i++;
                c = c.getSuperclass();
            }
            depth = new Integer(i);
        }

        public int compareTo(Object o) {
            FieldKey t = (FieldKey) o;
            int result = fieldName.compareTo(t.fieldName);
            if (result == 0) {
                result = 0 - depth.compareTo(t.depth);
            }
            if (result == 0) {
                result = declaringClass.getName().compareTo(t.declaringClass.getName());
            }
            return result;
        }
    }

}
