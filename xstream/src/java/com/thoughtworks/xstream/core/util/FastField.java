/*
 * Copyright (C) 2008, 2010 XStream Committers.
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

    public FastField(String definedIn, String name) {
        this.name = name;
        this.declaringClass = definedIn;
    }

    public FastField(Class definedIn, String name) {
        this(definedIn == null ? null : definedIn.getName(), name);
    }

    public String getName() {
        return this.name;
    }

    public String getDeclaringClass() {
        return this.declaringClass;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof FastField) {
            final FastField field = (FastField)obj;
            if ((declaringClass == null && field.declaringClass != null)
                || (declaringClass != null && field.declaringClass == null)) {
                return false;
            }
            return name.equals(field.getName())
                && (declaringClass == null || declaringClass.equals(field.getDeclaringClass()));
        }
        return false;
    }

    public int hashCode() {
        return name.hashCode() ^ (declaringClass == null ? 0 : declaringClass.hashCode());
    }

    public String toString() {
        return (declaringClass == null ? "" : declaringClass + ".") + name;
    }
}