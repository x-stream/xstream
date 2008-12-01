/*
 * Copyright (C) 2008 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 09. October 2008 by Joerg Schaible
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

/**
 * Helper methods for {@link HierarchicalStreamReader} and {@link HierarchicalStreamWriter}.
 * 
 * @author J&ouml;rg Schaible
 * @since 1.3.1
 */
public class HierarchicalStreams {

    public static Class readClassType(HierarchicalStreamReader reader, Mapper mapper) {
        String classAttribute = readClassAttribute(reader, mapper);
        Class type;
        if (classAttribute == null) {
            type = mapper.realClass(reader.getNodeName());
        } else {
            type = mapper.realClass(classAttribute);
        }
        return type;
    }

    public static String readClassAttribute(HierarchicalStreamReader reader, Mapper mapper) {
        String attributeName = mapper.aliasForSystemAttribute("resolves-to");
        String classAttribute = attributeName == null ? null : reader.getAttribute(attributeName);
        if (classAttribute == null) {
            attributeName = mapper.aliasForSystemAttribute("class");
            if (attributeName != null) {
                classAttribute = reader.getAttribute(attributeName);
            }
        }
        return classAttribute;
    }

}
