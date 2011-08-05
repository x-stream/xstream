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

import java.util.Map;


/**
 * A sorter that keeps the natural order of the bean properties as they are returned by the
 * JavaBean introspection.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class NativePropertySorter implements PropertySorter {

    public Map sort(final Class type, final Map nameMap) {
        return nameMap;
    }

}
