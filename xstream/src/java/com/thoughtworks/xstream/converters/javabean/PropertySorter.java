/*
 * Copyright (C) 2011 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 16. July 2011 by Joerg Schaible
 */
package com.thoughtworks.xstream.converters.javabean;

import java.beans.PropertyDescriptor;
import java.util.Map;

/**
 * An interface capable of sorting Java bean properties. Implement this interface if you
 * want to customize the order in which XStream serializes the properties of a bean.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public interface PropertySorter {

    /**
     * Sort the properties of a bean type. The method will be called with the class type
     * that contains all the properties and a Map that retains the order in which the
     * elements have been added. The sequence in which elements are returned by an iterator
     * defines the processing order of the properties. An implementation may create a
     * different Map with similar semantic, add all elements of the original map and return
     * the new one.
     * 
     * @param type the bean class that contains all the properties
     * @param nameMap the map to sort, key is the property name, value the
     *            {@link PropertyDescriptor}
     * @return the sorted nameMap
     * @since 1.4
     */
    Map sort(Class type, Map nameMap);

}