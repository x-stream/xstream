/*
 * Copyright (C) 2008, 2010, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 13. October 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

public final class FastField {
    private final String name;
    private final String declaringClass;

    public FastField(final String definedIn, final String name) {
        this.name = name;
        declaringClass = definedIn;
    }

    public FastField(final Class<?> definedIn, final String name) {
        this(definedIn == null ? null : definedIn.getName(), name);
    }

    public String getName() {
        return name;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof FastField) {
            final FastField field = (FastField)obj;
            if (declaringClass == null && field.declaringClass != null
                    || declaringClass != null && field.declaringClass == null) {
                return false;
            }
            return name.equals(field.getName())
                    && (declaringClass == null || declaringClass.equals(field.getDeclaringClass()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ (declaringClass == null ? 0 : declaringClass.hashCode());
    }

    @Override
    public String toString() {
        return (declaringClass == null ? "" : declaringClass + ".") + name;
    }
}