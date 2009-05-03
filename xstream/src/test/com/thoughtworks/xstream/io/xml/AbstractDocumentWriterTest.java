/*
 * Copyright (C) 2006, 2007, 2008, 2009 XStream Committers.
 * All rights reserved.
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 * 
 * Created on 19. October 2006 by Joerg Schaible
 */
package com.thoughtworks.xstream.io.xml;

import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.xml.xppdom.XppDom;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public abstract class AbstractDocumentWriterTest extends TestCase {

    private final HierarchicalStreamCopier copier = new HierarchicalStreamCopier();
    protected DocumentWriter writer;

    protected abstract DocumentReader createDocumentReaderFor(Object node);

    protected void assertDocumentProducedIs(final XppDom expected) {
        assertDocumentProducedIs(new XppDom[]{expected});
    }

    protected boolean equals(final XppDom node1, final XppDom node2) {
        if (node1.getName().equals(node2.getName())) {
            final String value1 = node1.getValue();
            final String value2 = node2.getValue();
            if ((value1 == null && value2 == null) || value1.equals(value2)) {
                final Set set1 = new HashSet(Arrays.asList(node1.getAttributeNames()));
                final Set set2 = new HashSet(Arrays.asList(node2.getAttributeNames()));
                if (set1.equals(set2)) {
                    final XppDom[] children1 = node1.getChildren();
                    final XppDom[] children2 = node2.getChildren();
                    if (children1.length == children2.length) {
                        for (int i = 0; i < children1.length; i++) {
                            if (!equals(children1[i], children2[i])) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void assertDocumentProducedIs(final XppDom[] expected) {
        for (int i = 0; i < expected.length; i++) {
            copier.copy(new XppDomReader(expected[i]), writer);
        }
        final Object[] nodes = writer.getTopLevelNodes().toArray(new Object[0]);
        assertEquals(expected.length, nodes.length);
        for (int i = 0; i < nodes.length; i++) {
            final XppDomWriter xpp3 = new XppDomWriter();
            copier.copy(createDocumentReaderFor(nodes[i]), xpp3);
            assertTrue(equals(expected[i], xpp3.getConfiguration()));
        }
    }

    public void testProducesDomElements() {
        final XppDom root = new XppDom("hello");
        root.setValue("world");
        assertDocumentProducedIs(root);
    }

    public void testSupportsNestedElements() {
        final XppDom a = new XppDom("a");

        XppDom b = new XppDom("b");
        b.setValue("one");
        a.addChild(b);

        b = new XppDom("b");
        b.setValue("two");
        a.addChild(b);

        final XppDom c = new XppDom("c");
        a.addChild(c);
        final XppDom d = new XppDom("d");
        d.setValue("three");
        c.addChild(d);

        assertDocumentProducedIs(a);
    }

    public void testSupportsAttributes() {
        final XppDom person = new XppDom("person");
        person.setAttribute("firstname", "Joe");
        person.setAttribute("lastname", "Walnes");
        assertDocumentProducedIs(person);
    }

    public void testAttributesAreResettedForNewNode() {
        final XppDom[] roots = new XppDom[2];
        final XppDom person = roots[0] = new XppDom("person");
        person.setAttribute("firstname", "Joe");
        person.setAttribute("lastname", "Walnes");
        final XppDom project = roots[1] = new XppDom("project");
        project.setAttribute("XStream", "Codehaus");

        assertDocumentProducedIs(roots);
    }

    public void testSupportsEmptyNestedTags() {
        final XppDom parent = new XppDom("parent");
        parent.addChild(new XppDom("child"));

        assertDocumentProducedIs(parent);
    }

    protected void assertDocumentProducedIs(final XppDom expected, final XppDom tree)
    {
            copier.copy(new XppDomReader(tree), writer);

            final Object[] nodes = writer.getTopLevelNodes().toArray(new Object[0]);
            assertEquals(1, nodes.length);
            for (int i = 0; i < nodes.length; i++) {
                    final XppDomWriter xpp3 = new XppDomWriter();
                    copier.copy(createDocumentReaderFor(nodes[i]), xpp3);
                    assertTrue(equals(expected, xpp3.getConfiguration()));
            }
    }
}
