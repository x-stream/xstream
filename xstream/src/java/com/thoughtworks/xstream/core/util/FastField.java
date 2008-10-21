/*
 * Copyright (C) 2008 XStream Committers.
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
    private final Class declaringClass;

    public FastField(Class definedIn, String name) {
        this.name = name;
        this.declaringClass = definedIn;
    }

    public String getName() {
        return this.name;
    }

    public Class getDeclaringClass() {
        return this.declaringClass;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this == null) {
            return false;
        }
        if (obj.getClass() == FastField.class) {
            final FastField field = (FastField)obj;
            return name.equals(field.getName())
                && declaringClass.equals(field.getDeclaringClass());
        }
        return false;
    }

    public int hashCode() {
        return name.hashCode() ^ declaringClass.hashCode();
    }

    public String toString() {
        return declaringClass.getName() + "[" + name + "]";
    }
}