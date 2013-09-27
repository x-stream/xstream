/*
 * Copyright (C) 2004, 2006 Joe Walnes.
 * Copyright (C) 2006, 2007, 2013 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 07. March 2004 by Joe Walnes
 */
package com.thoughtworks.xstream.converters.reflection;

import java.lang.reflect.Field;


/**
 * Provides core reflection services.
 * 
 * @author Joe Walnes
 */
public interface ReflectionProvider {

    /**
     * Creates a new instance of the specified type. It is in the responsibility of the
     * implementation how such an instance is created.
     * 
     * @param type the type to instantiate
     * @return a new instance of this type
     */
    Object newInstance(Class type);

    void visitSerializableFields(Object object, Visitor visitor);

    void writeField(Object object, String fieldName, Object value, Class definedIn);

    Class getFieldType(Object object, String fieldName, Class definedIn);

    /**
     * @deprecated As of 1.4.5, use {@link #getFieldOrNull(Class, String)} instead
     */
    boolean fieldDefinedInClass(String fieldName, Class type);

    /**
     * A visitor interface for serializable fields defined in a class.
     */
    interface Visitor {

        /**
         * Callback for each visit
         * 
         * @param name field name
         * @param type field type
         * @param definedIn where the field was defined
         * @param value field value
         */
        void visit(String name, Class type, Class definedIn, Object value);
    }

    /**
     * Returns a field defined in some class.
     * 
     * @param definedIn class where the field was defined
     * @param fieldName field name
     * @return the field itself
     * @throws ObjectAccessException if field does not exist
     */
    Field getField(Class definedIn, String fieldName);

    /**
     * Returns a field defined in some class.
     * 
     * @param definedIn class where the field was defined
     * @param fieldName field name
     * @return the field itself or null
     * @since 1.4.5
     */
    Field getFieldOrNull(Class definedIn, String fieldName);
}
