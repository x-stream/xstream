/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 01. October 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.reflection;

/**
 * Indicates a missing field or property creating an object.
 *
 * @author Nikita Levyankov
 * @author Joerg Schaible
 * @since 1.4.2
 */
public class MissingFieldException extends ObjectAccessException {

    private final String fieldName;
    private final String className;
    
    /**
     * Construct a MissingFieldException.
     * @param className the name of the class missing the field
     * @param fieldName the name of the missed field
     * @since 1.4.2
     */
    public MissingFieldException(final String className, final String fieldName) {
        super("No field '" + fieldName + "' found in class '" + className + "'");
        this.className = className;
        this.fieldName = fieldName;
    }

    /**
     * Retrieve the name of the missing field.
     * @return the field name
     * @since 1.4.2
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Retrieve the name of the class with the missing field.
     * @return the class name
     * @since 1.4.2
     */
    protected String getClassName() {
        return className;
    }
}
