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
 * Indicates a missing field or property creating an object.
 *
 * @author Nikita Levyankov
 * @author Joerg Schaible
 * @since 1.4.2
 */
public class MissingFieldException extends ObjectAccessException {

    private static final long serialVersionUID = 20160226L;
    private final String fieldName;
    private final String className;

    /**
     * Construct a MissingFieldException.
     *
     * @param className the name of the class missing the field
     * @param fieldName the name of the missed field
     * @since 1.4.2
     */
    public MissingFieldException(final String className, final String fieldName) {
        super("Field not found in class.");
        this.className = className;
        this.fieldName = fieldName;
        add("field", className + "." + fieldName);
    }

    /**
     * Retrieve the name of the missing field.
     *
     * @return the field name
     * @since 1.4.2
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Retrieve the name of the class with the missing field.
     *
     * @return the class name
     * @since 1.4.2
     */
    protected String getClassName() {
        return className;
    }
}
