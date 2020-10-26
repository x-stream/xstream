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

package com.thoughtworks.xstream.converters.javabean;

import java.beans.PropertyDescriptor;
import java.util.Map;


/**
 * An interface capable of sorting Java bean properties.
 * <p>
 * Implement this interface if you want to customize the order in which XStream serializes the properties of a bean.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public interface PropertySorter {

    /**
     * Sort the properties of a bean type.
     * <p>
     * The method will be called with the class type that contains all the properties and a Map that retains the order
     * in which the elements have been added. The sequence in which elements are returned by an iterator defines the
     * processing order of the properties. An implementation may create a different Map with similar semantic, add all
     * elements of the original map and return the new one.
     * </p>
     * 
     * @param type the bean class that contains all the properties
     * @param nameMap the map to sort, key is the property name, value the {@link PropertyDescriptor}
     * @return the sorted nameMap
     * @since 1.4
     */
    Map<String, PropertyDescriptor> sort(Class<?> type, Map<String, PropertyDescriptor> nameMap);

}
