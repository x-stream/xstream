package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Pure Java ObjectFactory that instantiates objects using standard Java reflection, however the types of objects
 * that can be constructed are limited.
 *
 * Can newInstance: classes with public visibility, outer classes, static inner classes, classes with default constructors.
 * Cannot newInstance: classes without public visibility, non-static inner classes, classes without default constructors.
 * Note that any code in the constructor of a class will be executed when the ObjectFactory instantiates the object.
 */
public class PureJavaReflectionProvider implements ReflectionProvider {
    public Object newInstance(Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Cannot construct " + type.getName(), e);
        }
    }

    public Iterator listSerializableFields(Class type) {
        List result = new LinkedList();

        while (!Object.class.equals(type)) {
            Field[] fields = type.getDeclaredFields();
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
                result.add(field);
            }
            type = type.getSuperclass();
        }

        return result.iterator();
    }

    public void eachSerializableFields(Class type, ReflectionProvider.Block visitor) {
        while (!Object.class.equals(type)) {
            Field[] fields = type.getDeclaredFields();
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
                visitor.visit(field.getName(), field.getType());
            }
            type = type.getSuperclass();
        }
    }

    public Object readField(Object object, String fieldName) {
        Field field = findField(object.getClass(), fieldName);
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not get field " + field.getClass() + "." + field.getName(), e);
        }
    }

    private Field findField(Class type, String fieldName) {
        // @todo: cache this!
        while (!Object.class.equals(type)) {
            Field[] fields = type.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            type = type.getSuperclass();
        }
        throw new ObjectAccessException("Could not get field " + type + "." + fieldName);
    }

}
