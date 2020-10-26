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
    final private int depth;
    final private int order;

    public FieldKey(final String fieldName, final Class<?> declaringClass, final int order) {
        if (fieldName == null || declaringClass == null) {
            throw new IllegalArgumentException("fieldName or declaringClass is null");
        }
        this.fieldName = fieldName;
        this.declaringClass = declaringClass;
        this.order = order;
        Class<?> c = declaringClass;
        int i = 0;
        while (c.getSuperclass() != null) {
            i++;
            c = c.getSuperclass();
        }
        depth = i;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public int getDepth() {
        return depth;
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
            + depth
            + ", declaringClass="
            + declaringClass
            + ", fieldName='"
            + fieldName
            + "'"
            + "}";
    }

}
