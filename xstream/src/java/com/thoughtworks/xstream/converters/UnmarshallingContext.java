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

package com.thoughtworks.xstream.converters;

public interface UnmarshallingContext extends DataHolder {

    /**
     * Convert a nested object of given type.
     * 
     * @param current the current instance (can be {@code null})
     * @param type the expected type of the nested object
     * @return the unmarshalled object
     */
    Object convertAnother(Object current, Class<?> type);

    /**
     * Convert a nested object of given type with a specified converter.
     * 
     * @param current the current instance (can be {@code null})
     * @param type the expected type of the nested object
     * @param converter the converter to use (special cases only)
     * @return the unmarshalled object
     * @since 1.2
     */
    Object  convertAnother(Object current, Class<?> type, Converter converter);

    /**
     * Retrieve the given root object.
     * 
     * <p>This method will return only an object, if the parent object is root and the root was provided.</p>
     * 
     * @return the root object or {@code null}
     * @see com.thoughtworks.xstream.XStream#unmarshal(com.thoughtworks.xstream.io.HierarchicalStreamReader, Object, DataHolder)
     */
    Object currentObject();

    /**
     * Retrieve the required type for the current conversion.
     * 
     * @return the class type
     */
    Class<?> getRequiredType();

    void addCompletionCallback(Runnable work, int priority);

}
