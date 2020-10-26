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
