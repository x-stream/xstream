/*
 * Copyright (C) 2007, 2014, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 10. April 2007 by Guilherme Silveira
 */
package com.thoughtworks.xstream.converters.reflection;

/**
 * A field key.
 * 
 * @author Guilherme Silveira
 * @author J&ouml;rg Schaible
 */
public class FieldKey {
    final private String fieldName;
    final private Class<?> declaringClass;
    final private int order;
    private int depth = -1;

    public FieldKey(final String fieldName, final Class<?> declaringClass, final int order) {
        if (fieldName == null || declaringClass == null) {
            throw new IllegalArgumentException("fieldName or declaringClass is null");
        }
        this.fieldName = fieldName;
        this.declaringClass = declaringClass;
        this.order = order;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public int getDepth() {
        if (this.depth == -1) {
            Class<?> c = declaringClass;
            int i = 0;
            while (c.getSuperclass() != null) {
                i++;
                c = c.getSuperclass();
            }
            depth = i;
        }
        return this.depth;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FieldKey)) {
            return false;
        }

        final FieldKey fieldKey = (FieldKey)o;

        if (!declaringClass.equals(fieldKey.declaringClass)) {
            return false;
        }
        if (!fieldName.equals(fieldKey.fieldName)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = fieldName.hashCode();
        result = 29 * result + declaringClass.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FieldKey{"
            + "order="
            + order
            + ", writer="
            + getDepth()
            + ", declaringClass="
            + declaringClass
            + ", fieldName='"
            + fieldName
            + "'"
            + "}";
    }

}
