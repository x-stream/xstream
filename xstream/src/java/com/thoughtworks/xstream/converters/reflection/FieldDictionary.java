package com.thoughtworks.xstream.converters.reflection;

import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.core.util.OrderRetainingMap;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A field dictionary instance caches information about classes fields. 
 *
 */
public class FieldDictionary {

    private final Map keyedByFieldNameCache = Collections.synchronizedMap(new HashMap());
    private final Map keyedByFieldKeyCache = Collections.synchronizedMap(new HashMap());

    /**
     * Returns an iterator for all serializable fields for some class
     * @param cls	the class you are interested on
     * @return an iterator for its serializable fields
     */
    public Iterator serializableFieldsFor(Class cls) {
        return buildMap(cls, true).values().iterator();
    }

    /**
     * Returns an specific field of some class. If definedIn is null, it searchs for the field named 'name' inside the class cls.
     * If definedIn is different than null, tries to find the specified field name in the specified class cls which should be defined in
     * class definedIn (either equals cls or a one of it's superclasses)  
     * @param cls	the class where the field is to be searched
     * @param name	the field name
     * @param definedIn	the superclass (or the class itself) of cls where the field was defined
     * @return the field itself
     */
    public Field field(Class cls, String name, Class definedIn) {
        Map fields = buildMap(cls, definedIn != null);
        Field field = (Field) fields.get(definedIn != null ? (Object) new FieldKey(name, definedIn, 0) : (Object) name);
        if (field == null) {
            throw new ObjectAccessException("No such field " + cls.getName() + "." + name);
        } else {
            return field;
        }
    }

    private Map buildMap(Class cls, boolean tupleKeyed) {
        final String clsName = cls.getName();
        if (!keyedByFieldNameCache.containsKey(clsName)) {
            synchronized (keyedByFieldKeyCache) {
                if (!keyedByFieldNameCache.containsKey(clsName)) { // double check
                    final Map keyedByFieldName = new HashMap();
                    final Map keyedByFieldKey = new OrderRetainingMap();
                    while (!Object.class.equals(cls)) {
                        Field[] fields = cls.getDeclaredFields();
                        if (JVM.reverseFieldDefinition()) {
                            for (int i = fields.length >> 1; i-- > 0;) {
                                final int idx = fields.length-i-1;
                                final Field field = fields[i];
                                fields[i] = fields[idx];
                                fields[idx] = field;
                            }
                        }
                        for (int i = 0; i < fields.length; i++) {
                            Field field = fields[i];
                            field.setAccessible(true);
                            if (!keyedByFieldName.containsKey(field.getName())) {
                                keyedByFieldName.put(field.getName(), field);
                            }
                            keyedByFieldKey.put(new FieldKey(field.getName(), field.getDeclaringClass(), i), field);
                        }
                        cls = cls.getSuperclass();
                    }
                    keyedByFieldNameCache.put(clsName, keyedByFieldName);
                    keyedByFieldKeyCache.put(clsName, keyedByFieldKey);
                }
            }
        }
        return (Map) (tupleKeyed ? keyedByFieldKeyCache.get(clsName) : keyedByFieldNameCache.get(clsName));
    }

    private static class FieldKey {
        private String fieldName;
        private Class declaringClass;
        private Integer depth;
        private int order;

        public FieldKey(String fieldName, Class declaringClass, int order) {
            this.fieldName = fieldName;
            this.declaringClass = declaringClass;
            this.order = order;
            Class c = declaringClass;
            int i = 0;
            while (c.getSuperclass() != null) {
                i++;
                c = c.getSuperclass();
            }
            depth = new Integer(i);
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof FieldKey)) return false;

            final FieldKey fieldKey = (FieldKey) o;

            if (declaringClass != null ? !declaringClass.equals(fieldKey.declaringClass) : fieldKey.declaringClass != null) return false;
            if (fieldName != null ? !fieldName.equals(fieldKey.fieldName) : fieldKey.fieldName != null) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = (fieldName != null ? fieldName.hashCode() : 0);
            result = 29 * result + (declaringClass != null ? declaringClass.hashCode() : 0);
            return result;
        }

        public String toString() {
            return "FieldKey{" +
                    "order=" + order +
                    ", writer=" + depth +
                    ", declaringClass=" + declaringClass +
                    ", fieldName='" + fieldName + "'" +
                    "}";
        }


    }

}
