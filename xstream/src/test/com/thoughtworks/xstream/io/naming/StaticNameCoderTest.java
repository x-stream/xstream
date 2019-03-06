/*
 * Copyright (C) 2019 John Bergqvist.
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

import com.thoughtworks.xstream.io.naming.StaticNameCoder;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;

public class StaticNameCoderTest extends TestCase {

  public void testParametersNotModified() {
    HashMap<String, String> nodes = new HashMap<>();
    HashMap<String, String> attributes = new HashMap<>(); 
    nodes.put("node1", "A");
    attributes.put("attribute1", "B");

    HashMap<String, String> nodesClone = new HashMap<String, String>(nodes);
    HashMap<String, String> attributesClone = new HashMap<String, String>(attributes);
    StaticNameCoder staticNameCoder = new StaticNameCoder(nodes, attributes);

    assertEquals(nodes, nodesClone);
    assertEquals(attributes, attributesClone);
  }

  public void testNodesNotMatchingAttributes() {
    HashMap<String, String> nodes = new HashMap<>();
    HashMap<String, String> attributes = new HashMap<>(); 
    nodes.put("node1", "A");
    attributes.put("attribute1", "B");
    StaticNameCoder staticNameCoder = new StaticNameCoder(nodes, attributes);
    asserts(staticNameCoder, nodes, attributes);
  }

  public void testNodesMatchAttributes() {
    HashMap<String, String> nodes = new HashMap<>();
    HashMap<String, String> attributes = nodes; 
    nodes.put("node1", "A");
    StaticNameCoder staticNameCoder = new StaticNameCoder(nodes, attributes);
    asserts(staticNameCoder, nodes, attributes);
  }

  public void testNullAttributes() {
    HashMap<String, String> nodes = new HashMap<>();
    nodes.put("node1", "A");
    StaticNameCoder staticNameCoder = new StaticNameCoder(nodes, null);
    asserts(staticNameCoder, nodes, null);
  }

  public void asserts(StaticNameCoder staticNameCoder, HashMap<String, String> nodes, HashMap<String, String> attributes) {
    for (final Map.Entry<String, String> entry : nodes.entrySet()) {
      assertEquals(entry.getKey(), staticNameCoder.decodeNode(entry.getValue()));
      assertEquals(entry.getValue(), staticNameCoder.encodeNode(entry.getKey()));
    }

    if (attributes != null) {
      for (final Map.Entry<String, String> entry : attributes.entrySet()) {
        assertEquals(entry.getKey(), staticNameCoder.decodeAttribute(entry.getValue()));
        assertEquals(entry.getValue(), staticNameCoder.encodeAttribute(entry.getKey()));
      }
    } else {
      for (final Map.Entry<String, String> entry : nodes.entrySet()) {
        assertEquals(entry.getKey(), staticNameCoder.decodeAttribute(entry.getValue()));
        assertEquals(entry.getValue(), staticNameCoder.encodeAttribute(entry.getKey()));
      }
    }

    assertEquals("non-existing node", staticNameCoder.decodeNode("non-existing node")); 
    assertEquals("non-existing node", staticNameCoder.encodeNode("non-existing node"));
    assertEquals("non-existing attribute", staticNameCoder.decodeAttribute("non-existing attribute"));
    assertEquals("non-existing attribute", staticNameCoder.encodeAttribute("non-existing attribute"));
  }
}
