package com.thoughtworks.xstream.core.util;

import java.lang.reflect.Field;

/**
 * Slightly nicer way to find, get and set fields in classes. Wraps standard java.lang.reflect.Field calls but wraps
 * wraps exception in RuntimeExceptions.
 *
 * @author Joe Walnes
 */
public class Fields {
    public static Field find(Class type, String name) {
        try {
            Field result = type.getDeclaredField(name);
            result.setAccessible(true);
            return result;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Could not access " + type.getName() + "." + name + " field");
        }
    }

    public static void write(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not write " + field.getType().getName() + "." + field.getName() + " field");
        }
    }

    public static Object read(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not read " + field.getType().getName() + "." + field.getName() + " field");
        }
    }
}
