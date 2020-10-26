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
 * Provides core reflection services.
 * 
 * @author Joe Walnes
 */
public interface ReflectionProvider {

    /**
     * Creates a new instance of the specified type. It is in the responsibility of the implementation how such an
     * instance is created.
     * 
     * @param type the type to instantiate
     * @return a new instance of this type
     */
    Object newInstance(Class<?> type);

    void visitSerializableFields(Object object, Visitor visitor);

    void writeField(Object object, String fieldName, Object value, Class<?> definedIn);

    Class<?> getFieldType(Object object, String fieldName, Class<?> definedIn);

    /**
     * @deprecated As of 1.4.5, use {@link #getFieldOrNull(Class, String)} instead
     */
    @Deprecated
    boolean fieldDefinedInClass(String fieldName, Class<?> type);

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
        void visit(String name, Class<?> type, Class<?> definedIn, Object value);
    }

    /**
     * Returns a field defined in some class.
     * 
     * @param definedIn class where the field was defined
     * @param fieldName field name
     * @return the field itself
     * @throws ObjectAccessException if field does not exist
     */
    Field getField(Class<?> definedIn, String fieldName);

    /**
     * Returns a field defined in some class.
     * 
     * @param definedIn class where the field was defined
     * @param fieldName field name
     * @return the field itself or null
     * @since 1.4.5
     */
    Field getFieldOrNull(Class<?> definedIn, String fieldName);
}
