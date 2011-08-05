/*
 * Copyright (C) 2009, 2011 XStream Committers.
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
import java.util.Iterator;
import java.util.Map;


/**
 * A NameCoder that encodes and decodes names based on a map.
 * <p>
 * The provided map should contain a mapping between the name of the Java type or field to the
 * proper element in the target format. If a name cannot be found in the map, it is assumed not
 * to be mapped at all. Note that the values of the map should be unique also, otherwise the
 * decoding will produce wrong results.
 * </p>
 * 
 * @author J&ouml;rg Schaible
 * @since 1.4
 */
public class StaticNameCoder implements NameCoder {

    private final Map java2Node;
    private final Map java2Attribute;

    private transient Map node2Java;
    private transient Map attribute2Java;

    /**
     * Construct a StaticNameCoder.
     * 
     * @param java2Node mapping of Java names to nodes
     * @param java2Attribute mapping of Java names to attributes
     * @since 1.4
     */
    public StaticNameCoder(Map java2Node, Map java2Attribute) {
        this.java2Node = new HashMap(java2Node);
        if (java2Node == java2Attribute || java2Attribute == null) {
            this.java2Attribute = this.java2Node;
        } else {
            this.java2Attribute = new HashMap(java2Attribute);
        }
        readResolve();
    }

    /**
     * {@inheritDoc}
     */
    public String decodeAttribute(String attributeName) {
        String name = (String)attribute2Java.get(attributeName);
        return name == null ? attributeName : name;
    }

    /**
     * {@inheritDoc}
     */
    public String decodeNode(String nodeName) {
        String name = (String)node2Java.get(nodeName);
        return name == null ? nodeName : name;
    }

    /**
     * {@inheritDoc}
     */
    public String encodeAttribute(String name) {
        String friendlyName = (String)java2Attribute.get(name);
        return friendlyName == null ? name : friendlyName;
    }

    /**
     * {@inheritDoc}
     */
    public String encodeNode(String name) {
        String friendlyName = (String)java2Node.get(name);
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

    private Map invertMap(Map map) {
        Map inverseMap = new HashMap(map.size());
        for (final Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            final Map.Entry entry = (Map.Entry)iter.next();
            inverseMap.put((String)entry.getValue(), (String)entry.getKey());
        }
        return inverseMap;
    }
}
