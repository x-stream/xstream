package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Pure Java ObjectFactory that instantiates objects using standard Java reflection, however the types of objects
 * that can be constructed are limited.
 *
 * Can newInstance: classes with public visibility, outer classes, static inner classes, classes with default constructors.
 * Cannot newInstance: classes without public visibility, non-static inner classes, classes without default constructors.
 * Note that any code in the constructor of a class will be executed when the ObjectFactory instantiates the object.
 */
public class PureJavaReflectionProvider implements ReflectionProvider {

    private Map cache = new HashMap();

    public Object newInstance(Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
    }

    public void eachSerializableField(Class type, ReflectionProvider.Block visitor) {
        for (Iterator iterator = findAllSerializableFieldsForClass(type).entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            visitor.visit((String) entry.getKey(), ((Field)entry.getValue()).getType());
        }
    }

    public Object readField(Object object, String fieldName) {
        Field field = findField(object.getClass(), fieldName);
        try {
            return field.get(object);
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
        }
    }

    public void writeField(Object object, String fieldName, Object value) {
        Field field = findField(object.getClass(), fieldName);
        try {
            field.set(object, value);
        } catch (IllegalArgumentException e) {
            throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not set field " + object.getClass() + "." + field.getName(), e);
        }
    }

    public Class getFieldType(Object object, String fieldName) {
        return findField(object.getClass(), fieldName).getType();
    }

    private Field findField(Class cls, String fieldName) {
        Map fields = findAllSerializableFieldsForClass(cls);
        Field field = (Field) fields.get(fieldName);
        if (field == null) {
            throw new ObjectAccessException("No such field " + cls + "." + fieldName);
        } else {
            return field;
        }
    }

    private Map findAllSerializableFieldsForClass(Class cls) {
        if (!cache.containsKey(cls)) {
            final Map result = new TreeMap();
            while (!Object.class.equals(cls)) {
                Field[] fields = cls.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    int modifiers = field.getModifiers();
                    if (field.getName().startsWith("this$")) {
                        continue;
                    }
                    if (Modifier.isFinal(modifiers) ||
                            Modifier.isStatic(modifiers) ||
                            Modifier.isTransient(modifiers)) {
                        continue;
                    }
                    field.setAccessible(true);
                    result.put(field.getName(), field);
                }
                cls = cls.getSuperclass();
            }
            cache.put(cls, result);
        }
        return (Map) cache.get(cls);
    }
}
