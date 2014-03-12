/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. April 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;


/**
 * Slightly nicer way to find, get and set fields in classes. Wraps standard java.lang.reflect.Field calls but wraps
 * wraps exception in XStreamExceptions.
 * 
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class Fields {
    public static Field locate(final Class<?> definedIn, final Class<?> fieldType, final boolean isStatic) {
        Field field = null;
        try {
            final Field[] fields = definedIn.getDeclaredFields();
            for (final Field field2 : fields) {
                if (Modifier.isStatic(field2.getModifiers()) == isStatic) {
                    if (fieldType.isAssignableFrom(field2.getType())) {
                        field = field2;
                    }
                }
            }
            if (field != null && !field.isAccessible()) {
                field.setAccessible(true);
            }
        } catch (final SecurityException e) {
            // active SecurityManager
        } catch (final NoClassDefFoundError e) {
            // restricted type in GAE
        }
        return field;
    }

    public static Field find(final Class<?> type, final String name) {
        try {
            final Field result = type.getDeclaredField(name);
            if (!result.isAccessible()) {
                result.setAccessible(true);
            }
            return result;
        } catch (final NoSuchFieldException e) {
            final String message = "Could not access " + type.getName() + "." + name + " field: " + e.getMessage();
            throw new IllegalArgumentException(message);
        } catch (final NoClassDefFoundError e) {
            final String message = "Could not access " + type.getName() + "." + name + " field: " + e.getMessage();
            throw new ObjectAccessException(message);
        }
    }

    public static void write(final Field field, final Object instance, final Object value) {
        try {
            field.set(instance, value);
        } catch (final IllegalAccessException e) {
            final String message = "Could not write " + field.getType().getName() + "." + field.getName() + " field";
            throw new ObjectAccessException(message, e);
        } catch (final NoClassDefFoundError e) {
            final String message = "Could not write " + field.getType().getName() + "." + field.getName() + " field";
            throw new ObjectAccessException(message, e);
        }
    }

    public static Object read(final Field field, final Object instance) {
        try {
            return field.get(instance);
        } catch (final IllegalAccessException e) {
            final String message = "Could not read " + field.getType().getName() + "." + field.getName() + " field";
            throw new ObjectAccessException(message, e);
        } catch (final NoClassDefFoundError e) {
            final String message = "Could not read " + field.getType().getName() + "." + field.getName() + " field";
            throw new ObjectAccessException(message, e);
        }
    }
}
