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

import java.lang.reflect.Field;


/**
 * A wrapper implementation for the ReflectionProvider.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.2
 */
public class ReflectionProviderWrapper implements ReflectionProvider {

    final protected ReflectionProvider wrapped;

    public ReflectionProviderWrapper(final ReflectionProvider wrapper) {
        wrapped = wrapper;
    }

    /**
     * @deprecated As of 1.4.5, use {@link #getFieldOrNull(Class, String)} instead
     */
    @Deprecated
    @Override
    public boolean fieldDefinedInClass(final String fieldName, final Class<?> type) {
        return wrapped.fieldDefinedInClass(fieldName, type);
    }

    @Override
    public Field getField(final Class<?> definedIn, final String fieldName) {
        return wrapped.getField(definedIn, fieldName);
    }

    @Override
    public Field getFieldOrNull(final Class<?> definedIn, final String fieldName) {
        return wrapped.getFieldOrNull(definedIn, fieldName);
    }

    @Override
    public Class<?> getFieldType(final Object object, final String fieldName, final Class<?> definedIn) {
        return wrapped.getFieldType(object, fieldName, definedIn);
    }

    @Override
    public Object newInstance(final Class<?> type) {
        return wrapped.newInstance(type);
    }

    @Override
    public void visitSerializableFields(final Object object, final Visitor visitor) {
        wrapped.visitSerializableFields(object, visitor);
    }

    @Override
    public void writeField(final Object object, final String fieldName, final Object value, final Class<?> definedIn) {
        wrapped.writeField(object, fieldName, value, definedIn);
    }

}
