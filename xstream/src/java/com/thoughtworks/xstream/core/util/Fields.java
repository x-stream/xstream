/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013 XStream Committers.
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
    public static Field locate(Class definedIn, Class fieldType, boolean isStatic) {
        Field field = null;
        try {
            Field[] fields = definedIn.getDeclaredFields();
            for(int i = 0; i < fields.length; ++i) {
                if (Modifier.isStatic(fields[i].getModifiers()) == isStatic) {
                    if (fieldType.isAssignableFrom(fields[i].getType())) {
                        field = fields[i];
                    }
                }
            }
            if (field != null && !field.isAccessible()) {
                field.setAccessible(true);
            }
        } catch (SecurityException e) {
            // active SecurityManager
        } catch (NoClassDefFoundError e) {
            // restricted type in GAE
        }
        return field;
    }

    public static Field find(Class type, String name) {
        try {
            Field result = type.getDeclaredField(name);
            if (!result.isAccessible()) {
                result.setAccessible(true);
            }
            return result;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException("Could not access " + type.getName() + "." + name + " field: " + e.getMessage());
        } catch (NoClassDefFoundError e) {
            throw new ObjectAccessException("Could not access " + type.getName() + "." + name + " field: " + e.getMessage());
        }
    }

    public static void write(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not write " + field.getType().getName() + "." + field.getName() + " field", e);
        } catch (NoClassDefFoundError e) {
            throw new ObjectAccessException("Could not write " + field.getType().getName() + "." + field.getName() + " field", e);
        }
    }

    public static Object read(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new ObjectAccessException("Could not read " + field.getType().getName() + "." + field.getName() + " field", e);
        } catch (NoClassDefFoundError e) {
            throw new ObjectAccessException("Could not read " + field.getType().getName() + "." + field.getName() + " field", e);
        }
    }
}
