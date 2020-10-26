/*
 * Copyright (C) 2004, 2005 Joe Walnes.
 * Copyright (C) 2006, 2007, 2014 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 06. March 2004 by Joe Walnes
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
