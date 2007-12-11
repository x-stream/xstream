/*
 * Copyright (C) 2006, 2007 XStream Committers.
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

    public ReflectionProviderWrapper(ReflectionProvider wrapper) {
        this.wrapped = wrapper;
    }

    public boolean fieldDefinedInClass(String fieldName, Class type) {
        return this.wrapped.fieldDefinedInClass(fieldName, type);
    }

    public Field getField(Class definedIn, String fieldName) {
        return this.wrapped.getField(definedIn, fieldName);
    }

    public Class getFieldType(Object object, String fieldName, Class definedIn) {
        return this.wrapped.getFieldType(object, fieldName, definedIn);
    }

    public Object newInstance(Class type) {
        return this.wrapped.newInstance(type);
    }

    public void visitSerializableFields(Object object, Visitor visitor) {
        this.wrapped.visitSerializableFields(object, visitor);
    }

    public void writeField(Object object, String fieldName, Object value, Class definedIn) {
        this.wrapped.writeField(object, fieldName, value, definedIn);
    }

}
