/*
 * Copyright (C) 2007, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 26. June 2007 by Joerg Schaible
 */
package com.thoughtworks.xstream.tools.model.targets;

import com.thoughtworks.xstream.tools.benchmark.Target;

import org.apache.commons.lang.builder.EqualsBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;


/**
 * An abstract Target that fills the fields by reflection.
 * 
 * @author J&ouml;rg Schaible
 * @see com.thoughtworks.xstream.tools.benchmark.Harness
 * @see Target
 */
public abstract class AbstractReflectionTarget implements Target {

    private final Object target;

    public AbstractReflectionTarget(final Object target) {
        this.target = target;
    }

    public abstract String toString();

    protected void fill(final Object o) {
        final char[] zero = new char[8];
        Arrays.fill(zero, '0');
        for (Class cls = o.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
            final Field[] fields = cls.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                final Field field = fields[i];
                field.setAccessible(true);
                if (field.getType() == String.class) {
                    final String hex = Integer.toHexString(i);
                    try {
                        field.set(o, new String(zero, 0, zero.length - hex.length()) + hex);
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public Object target() {
        return target;
    }

    public boolean isEqual(final Object other) {
        return EqualsBuilder.reflectionEquals(target, other);
    }
}
