/*
 * Copyright (C) 2011, 2014, 2015 XStream Committers.
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
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


/**
 * A sorter that uses a comparator to determine the order of the bean properties.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class ComparingPropertySorter implements PropertySorter {

    private final Comparator<String> comparator;

    public ComparingPropertySorter(final Comparator<String> propertyNameComparator) {
        comparator = propertyNameComparator;
    }

    @Override
    public Map<String, PropertyDescriptor> sort(final Class<?> type, final Map<String, PropertyDescriptor> nameMap) {
        final TreeMap<String, PropertyDescriptor> map = new TreeMap<>(comparator);
        map.putAll(nameMap);
        return map;
    }

}
