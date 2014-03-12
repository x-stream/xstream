/*
 * Copyright (C) 2006, 2007, 2013, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 13. April 2006 by Joerg Schaible
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
