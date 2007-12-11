/*
 * Copyright (C) 2006, 2007 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 15. February 2006 by Mauro Talevi
 */
package com.thoughtworks.xstream.converters;

/**
 * SingleValueConverter implementations are marshallable to/from a single value String representation.
 * <p/>
 * <p>{@link com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter}
 * provides a starting point for objects that can store all information in a single value String.</p>
 *
 * @author Joe Walnes
 * @author J&ouml;rg Schaible
 * @author Mauro Talevi
 * @see com.thoughtworks.xstream.converters.Converter
 * @see com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter
 * @since 1.2
 */
public interface SingleValueConverter extends ConverterMatcher {

    /**
     * Marshals an Object into a single value representation.
     * @param obj the Object to be converted
     * @return a String with the single value of the Object or <code>null</code>
     */
    public String toString(Object obj);

    /**
     * Unmarshals an Object from its single value representation.
     * @param str the String with the single value of the Object
     * @return the Object
     */
    public Object fromString(String str);

}