/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
        } catch (final SecurityException | NoClassDefFoundError e) {
            // active SecurityManager
        }
		// restricted type in GAE
        return field;
    }

    public static Field find(final Class<?> type, final String name) {
        try {
            final Field result = type.getDeclaredField(name);
            if (!result.isAccessible()) {
                result.setAccessible(true);
            }
            return result;
        } catch (final SecurityException | NoSuchFieldException | NoClassDefFoundError e) {
            throw wrap("Cannot access field", type, name, e);
        }
    }

    public static void write(final Field field, final Object instance, final Object value) {
        try {
            field.set(instance, value);
        } catch (final SecurityException | IllegalArgumentException | IllegalAccessException | NoClassDefFoundError e) {
            throw wrap("Cannot write field", field.getType(), field.getName(), e);
        }
    }

    public static Object read(final Field field, final Object instance) {
        try {
            return field.get(instance);
        } catch (final SecurityException | IllegalArgumentException | IllegalAccessException | NoClassDefFoundError e) {
            throw wrap("Cannot read field", field.getType(), field.getName(), e);
        }
    }

    private static ObjectAccessException wrap(final String message, final Class<?> type, final String name,
            final Throwable ex) {
        final ObjectAccessException exception = new ObjectAccessException(message, ex);
        exception.add("field", type.getName() + "." + name);
        return exception;
    }
}
