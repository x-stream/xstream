/*
 * Copyright (C) 2009, 2011, 2014, 2015, 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 15. August 2009 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.naming;

import java.util.HashMap;
import java.util.Map;


/**
 * A NameCoder that encodes and decodes names based on a map.
 * <p>
 * The provided map should contain a mapping between the name of the Java type or field to the proper element in the
 * target format. If a name cannot be found in the map, it is assumed not to be mapped at all. Note that the values of
 * the map should be unique also, otherwise the decoding is undefined and will produce wrong results.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class StaticNameCoder implements NameCoder {

    private final Map<String, String> java2Node;
    private final Map<String, String> java2Attribute;

    private transient Map<String, String> node2Java;
    private transient Map<String, String> attribute2Java;

    /**
     * Construct a StaticNameCoder.
     * 
     * @param java2Node mapping of Java names to nodes
     * @param java2Attribute mapping of Java names to attributes
     * @since 1.4
     */
    public StaticNameCoder(final Map<String, String> java2Node, final Map<String, String> java2Attribute) {
        this.java2Node = new HashMap<>(java2Node);
        if (java2Node == java2Attribute || java2Attribute == null) {
            this.java2Attribute = this.java2Node;
        } else {
            this.java2Attribute = new HashMap<>(java2Attribute);
        }
        readResolve();
    }

    @Override
    public String decodeAttribute(final String attributeName) {
        final String name = attribute2Java.get(attributeName);
        return name == null ? attributeName : name;
    }

    @Override
    public String decodeNode(final String nodeName) {
        final String name = node2Java.get(nodeName);
        return name == null ? nodeName : name;
    }

    @Override
    public String encodeAttribute(final String name) {
        final String friendlyName = java2Attribute.get(name);
        return friendlyName == null ? name : friendlyName;
    }

    @Override
    public String encodeNode(final String name) {
        final String friendlyName = java2Node.get(name);
        return friendlyName == null ? name : friendlyName;
    }

    private Object readResolve() {
        node2Java = invertMap(java2Node);
        if (java2Node == java2Attribute) {
            attribute2Java = node2Java;
        } else {
            attribute2Java = invertMap(java2Attribute);
        }
        return this;
    }

    private Map<String, String> invertMap(final Map<String, String> map) {
        final Map<String, String> inverseMap = new HashMap<>(map.size());
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            inverseMap.put(entry.getValue(), entry.getKey());
        }
        return inverseMap;
    }
}
