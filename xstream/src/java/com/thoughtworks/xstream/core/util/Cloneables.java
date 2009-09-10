/*
 * Copyright (C) 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 29. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;

/**
 * Utility functions for {@link Cloneable} objects.
 * 
 * @author J&ouml;rg Schaible
 * @since upcoming
 */
public class Cloneables {
    
    public static Object clone(Object o) {
        if (o instanceof Cloneable) {
            try {
                Method clone = o.getClass().getMethod("clone", (Class[])null);
                return clone.invoke(o, (Object[])null);
            } catch (NoSuchMethodException e) {
                throw new ObjectAccessException("Cloneable type has no clone method", e);
            } catch (IllegalAccessException e) {
                throw new ObjectAccessException("Cannot clone Cloneable type", e);
            } catch (InvocationTargetException e) {
                throw new ObjectAccessException("Exception cloning Cloneable type", e.getCause());
            } 
        }
        return null;
    }
    
    public static Object cloneIfPossible(Object o) {
        Object clone = clone(o);
        return clone == null ? o : clone;
    }
}
