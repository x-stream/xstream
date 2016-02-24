/*
 * Copyright (C) 2004 Joe Walnes.
 * Copyright (C) 2006, 2007, 2008, 2009, 2011, 2013, 2016 XStream Committers.
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
 * Slightly nicer way to find, get and set fields in classes. Wraps standard java.lang.reflect.Field calls and turns
 * exceptions into XStreamExceptions.
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 */
public class Fields {
    public static Field locate(final Class definedIn, final Class fieldType, final boolean isStatic) {
        Field field = null;
        try {
            final Field[] fields = definedIn.getDeclaredFields();
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
        } catch (final SecurityException e) {
            // active SecurityManager
        } catch (final NoClassDefFoundError e) {
            // restricted type in GAE
        }
        return field;
    }

    public static Field find(final Class type, final String name) {
        try {
            final Field result = type.getDeclaredField(name);
            if (!result.isAccessible()) {
                result.setAccessible(true);
            }
            return result;
        } catch (final SecurityException e) {
            throw wrap("Cannot access field", type, name, e);
        } catch (final NoSuchFieldException e) {
            throw wrap("Cannot access field", type, name, e);
        } catch (final NoClassDefFoundError e) {
            throw wrap("Cannot access field", type, name, e);
        }
    }

    public static void write(final Field field, final Object instance, final Object value) {
        try {
            field.set(instance, value);
        } catch (final SecurityException e) {
            throw wrap("Cannot write field", field.getType(), field.getName(), e);
        } catch (final IllegalArgumentException e) {
            throw wrap("Cannot write field", field.getType(), field.getName(), e);
        } catch (final IllegalAccessException e) {
            throw wrap("Cannot write field", field.getType(), field.getName(), e);
        } catch (final NoClassDefFoundError e) {
            throw wrap("Cannot write field", field.getType(), field.getName(), e);
        }
    }

    public static Object read(final Field field, final Object instance) {
        try {
            return field.get(instance);
        } catch (final SecurityException e) {
            throw wrap("Cannot read field", field.getType(), field.getName(), e);
        } catch (final IllegalArgumentException e) {
            throw wrap("Cannot read field", field.getType(), field.getName(), e);
        } catch (final IllegalAccessException e) {
            throw wrap("Cannot read field", field.getType(), field.getName(), e);
        } catch (final NoClassDefFoundError e) {
            throw wrap("Cannot read field", field.getType(), field.getName(), e);
        }
    }

    private static ObjectAccessException wrap(final String message, final Class type, final String name,
            final Throwable ex) {
        final ObjectAccessException exception = new ObjectAccessException(message, ex);
        exception.add("field", type.getName() + "." + name);
        return exception;
    }
}
