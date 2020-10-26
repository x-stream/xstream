/*
 * Copyright (C) 2009, 2010, 2011, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

/**
 * Utility functions for {@link Cloneable} objects.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class Cloneables {

    @SuppressWarnings("unchecked")
    public static <T> T clone(final T o) {
        if (o instanceof Cloneable) {
            if (o.getClass().isArray()) {
                final Class<?> componentType = o.getClass().getComponentType();
                if (!componentType.isPrimitive()) {
                    return (T)((Object[])o).clone();
                } else {
                    int length = Array.getLength(o);
                    final Object clone = Array.newInstance(componentType, length);
                    while (length-- > 0) {
                        Array.set(clone, length, Array.get(o, length));
                    }
                    return (T)clone;
                }
            } else {
                try {
                    final Method clone = o.getClass().getMethod("clone");
                    return (T)clone.invoke(o);
                } catch (final NoSuchMethodException e) {
                    throw new ObjectAccessException("Cloneable type has no clone method", e);
                } catch (final IllegalAccessException e) {
                    throw new ObjectAccessException("Cannot clone Cloneable type", e);
                } catch (final InvocationTargetException e) {
                    throw new ObjectAccessException("Exception cloning Cloneable type", e.getCause());
                }
            }
        }
        return null;
    }

    public static <T> T cloneIfPossible(final T o) {
        final T clone = clone(o);
        return clone == null ? o : clone;
    }
}
