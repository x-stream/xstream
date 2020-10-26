/*
 * Copyright (C) 2019 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 *
 * Created on 7. March 2019 by John Bergqvist
 */
package com.thoughtworks.xstream.io.naming;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


public class StaticNameCoderTest extends TestCase {

    public void testMappingIsNotSharedBetweenNodesAndAttributes() {
        final Map<String, String> nodes = new HashMap<>();
        final Map<String, String> attributes = new HashMap<>();
        nodes.put("node1", "A");
        attributes.put("attribute1", "B");

        final NameCoder nameCoder = new StaticNameCoder(nodes, attributes);

        assertEquals("A", nameCoder.encodeNode("node1"));
        assertEquals("attribute1", nameCoder.encodeNode("attribute1"));
        assertEquals("B", nameCoder.encodeAttribute("attribute1"));
        assertEquals("node1", nameCoder.encodeAttribute("node1"));
    }

    public void testMappingIsSharedWhenAttributesAreNull() {
        final Map<String, String> nodes = new HashMap<>();
        nodes.put("node1", "A");
        nodes.put("attribute1", "B");

        final NameCoder nameCoder = new StaticNameCoder(nodes, null);

        assertEquals("A", nameCoder.encodeNode("node1"));
        assertEquals("B", nameCoder.encodeNode("attribute1"));
        assertEquals("B", nameCoder.encodeAttribute("attribute1"));
        assertEquals("A", nameCoder.encodeAttribute("node1"));
    }

    public void testMappingIsSymmetrical() {
        final Map<String, String> nodes = new HashMap<>();
        final Map<String, String> attributes = new HashMap<>();
        nodes.put("node1", "A");
        attributes.put("attribute1", "B");

        final NameCoder nameCoder = new StaticNameCoder(nodes, attributes);

        assertEquals("node1", nameCoder.decodeNode(nameCoder.encodeNode("node1")));
        assertEquals("attribute1", nameCoder.decodeAttribute(nameCoder.encodeAttribute("attribute1")));
    }

    public void testUnmappedNodesAndAttributes() {
        final Map<String, String> nodes = new HashMap<>();
        final Map<String, String> attributes = new HashMap<>();
        nodes.put("node1", "A");
        attributes.put("attribute1", "B");

        final NameCoder nameCoder = new StaticNameCoder(nodes, attributes);

        assertEquals("A", nameCoder.encodeNode("node1"));
        assertEquals("node2", nameCoder.encodeNode("node2"));
        assertEquals("B", nameCoder.encodeAttribute("attribute1"));
        assertEquals("attribute2", nameCoder.encodeAttribute("attribute2"));
    }

    public void testParametersByReference() {
        final Map<String, String> nodes = new HashMap<>();
        final Map<String, String> attributes = new HashMap<>();
        nodes.put("node1", "A");
        attributes.put("attribute1", "B");

        final NameCoder nameCoder = new StaticNameCoder(nodes, attributes);

        nodes.put("node2", "C");
        attributes.put("attribute2", "D");

        assertEquals("A", nameCoder.encodeNode("node1"));
        assertEquals("B", nameCoder.encodeAttribute("attribute1"));
        assertEquals("node2", nameCoder.encodeNode("node2"));
        assertEquals("attribute2", nameCoder.encodeAttribute("attribute2"));
    }
}
